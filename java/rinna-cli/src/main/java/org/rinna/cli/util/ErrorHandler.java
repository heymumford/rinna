/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 * 
 * Developed with analytical assistance from AI tools.
 * All rights reserved.
 * 
 * This source code is licensed under the MIT License
 * found in the LICENSE file in the root directory of this source tree.
 */
package org.rinna.cli.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.rinna.cli.service.MetadataService;

/**
 * Standardized error handling for CLI commands with integrated operation tracking.
 * This utility provides consistent error reporting and ensures that all errors
 * are properly tracked in the metadata service.
 */
public class ErrorHandler {
    
    /**
     * Error severity levels for categorizing errors.
     */
    public enum Severity {
        /** User input validation errors */
        VALIDATION,
        
        /** Non-fatal issues that might affect operation */
        WARNING,
        
        /** Fatal issues that prevent operation completion */
        ERROR,
        
        /** System-level errors (file system, network, etc.) */
        SYSTEM,
        
        /** Security-related issues */
        SECURITY
    }
    
    private final MetadataService metadataService;
    private boolean verbose = false;
    private String outputFormat = "text";
    private Consumer<String> errorOutputConsumer = System.err::println;
    
    /**
     * Creates a new error handler with the specified metadata service.
     *
     * @param metadataService the metadata service for tracking errors
     */
    public ErrorHandler(MetadataService metadataService) {
        this.metadataService = metadataService;
    }
    
    /**
     * Sets whether to display verbose error information.
     *
     * @param verbose true for verbose output
     * @return this handler for method chaining
     */
    public ErrorHandler verbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }
    
    /**
     * Sets the output format to use for error messages.
     *
     * @param outputFormat the output format ("text" or "json")
     * @return this handler for method chaining
     */
    public ErrorHandler outputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }
    
    /**
     * Sets a custom consumer for error output.
     * By default, errors are sent to System.err.
     *
     * @param errorOutputConsumer the consumer for error output
     * @return this handler for method chaining
     */
    public ErrorHandler errorOutputConsumer(Consumer<String> errorOutputConsumer) {
        this.errorOutputConsumer = errorOutputConsumer;
        return this;
    }
    
    /**
     * Handles an error by tracking it and outputting an appropriate message.
     *
     * @param operationId the operation ID for tracking
     * @param commandName the command name
     * @param errorMessage the error message
     * @param exception the exception that occurred
     * @param severity the severity level of the error
     * @return the error exit code (1)
     */
    public int handleError(String operationId, String commandName, String errorMessage, Exception exception, Severity severity) {
        // Track the error in the metadata service
        metadataService.failOperation(operationId, exception);
        metadataService.trackOperationError(operationId, commandName, errorMessage, exception);
        
        // Track severity level
        metadataService.trackOperationDetail(operationId, "errorSeverity", severity.name());
        
        // Output the error message
        outputError(errorMessage, exception, severity);
        
        // Return a standard error exit code
        return 1;
    }
    
    /**
     * Handles an error by tracking it and outputting an appropriate message.
     * Uses ERROR severity by default.
     *
     * @param operationId the operation ID for tracking
     * @param commandName the command name
     * @param errorMessage the error message
     * @param exception the exception that occurred
     * @return the error exit code (1)
     */
    public int handleError(String operationId, String commandName, String errorMessage, Exception exception) {
        return handleError(operationId, commandName, errorMessage, exception, Severity.ERROR);
    }
    
    /**
     * Handles an error by tracking it and outputting an appropriate message.
     * This version does not take the exception, for cases where there isn't one.
     *
     * @param operationId the operation ID for tracking
     * @param commandName the command name
     * @param errorMessage the error message
     * @param severity the severity level of the error
     * @return the error exit code (1)
     */
    public int handleError(String operationId, String commandName, String errorMessage, Severity severity) {
        // Create a generic exception for tracking
        Exception exception = new RuntimeException(errorMessage);
        return handleError(operationId, commandName, errorMessage, exception, severity);
    }
    
    /**
     * Handles an error by tracking it and outputting an appropriate message.
     * This version does not take the exception, for cases where there isn't one.
     * Uses ERROR severity by default.
     *
     * @param operationId the operation ID for tracking
     * @param commandName the command name
     * @param errorMessage the error message
     * @return the error exit code (1)
     */
    public int handleError(String operationId, String commandName, String errorMessage) {
        // Create a generic exception for tracking
        Exception exception = new RuntimeException(errorMessage);
        return handleError(operationId, commandName, errorMessage, exception, Severity.ERROR);
    }
    
    /**
     * Handles a validation error, which is tracked but may have a different format.
     *
     * @param operationId the operation ID for tracking
     * @param commandName the command name
     * @param validationErrors a map of field names to error messages
     * @return the error exit code (1)
     */
    public int handleValidationError(String operationId, String commandName, Map<String, String> validationErrors) {
        // Build a combined error message
        StringBuilder errorMessage = new StringBuilder("Validation errors:\n");
        for (Map.Entry<String, String> entry : validationErrors.entrySet()) {
            errorMessage.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        // Track the validation errors in the metadata service
        Exception exception = new IllegalArgumentException("Validation failed");
        metadataService.failOperation(operationId, exception);
        
        // Track each validation error separately
        for (Map.Entry<String, String> entry : validationErrors.entrySet()) {
            Map<String, Object> errorDetail = new HashMap<>();
            errorDetail.put("field", entry.getKey());
            errorDetail.put("message", entry.getValue());
            metadataService.trackOperationDetail(operationId, "validationError_" + entry.getKey(), errorDetail);
        }
        
        // Track severity level
        metadataService.trackOperationDetail(operationId, "errorSeverity", Severity.VALIDATION.name());
        
        // Output the error message
        outputError(errorMessage.toString(), exception, Severity.VALIDATION);
        
        // Return a standard error exit code
        return 1;
    }
    
    /**
     * Handles an unexpected error with more detailed tracking.
     *
     * @param operationId the operation ID for tracking
     * @param commandName the command name
     * @param exception the exception that occurred
     * @param severity the severity level of the error
     * @return the error exit code (1)
     */
    public int handleUnexpectedError(String operationId, String commandName, Throwable exception, Severity severity) {
        // Create a more descriptive error message for unexpected errors
        String errorMessage = "Unexpected error in " + commandName + ": " + exception.getMessage();
        
        // Track detailed error information
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("exceptionType", exception.getClass().getName());
        errorDetail.put("message", exception.getMessage());
        errorDetail.put("severity", severity.name());
        
        if (exception.getCause() != null) {
            errorDetail.put("cause", exception.getCause().getMessage());
            errorDetail.put("causeType", exception.getCause().getClass().getName());
        }
        
        metadataService.trackOperationDetail(operationId, "unexpectedError", errorDetail);
        metadataService.trackOperationDetail(operationId, "errorSeverity", severity.name());
        metadataService.failOperation(operationId, exception);
        
        // Output the error message
        outputError(errorMessage, exception, severity);
        
        // Return a standard error exit code
        return 1;
    }
    
    /**
     * Handles an unexpected error with more detailed tracking.
     * Uses SYSTEM severity by default.
     *
     * @param operationId the operation ID for tracking
     * @param commandName the command name
     * @param exception the exception that occurred
     * @return the error exit code (1)
     */
    public int handleUnexpectedError(String operationId, String commandName, Throwable exception) {
        return handleUnexpectedError(operationId, commandName, exception, Severity.SYSTEM);
    }
    
    /**
     * Outputs an error message in the appropriate format with severity information.
     *
     * @param message the error message
     * @param exception the exception that occurred (may be null)
     * @param severity the severity level of the error
     */
    public void outputError(String message, Throwable exception, Severity severity) {
        if ("json".equalsIgnoreCase(outputFormat)) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("result", "error");
            errorData.put("message", message);
            errorData.put("severity", severity.name());
            
            // Add details object for standardized format
            Map<String, Object> details = new HashMap<>();
            details.put("context", "Command execution error");
            
            if (exception != null) {
                details.put("exceptionMessage", exception.getMessage());
            }
            
            errorData.put("details", details);
            
            if (verbose && exception != null) {
                errorData.put("exceptionType", exception.getClass().getName());
                if (exception.getCause() != null) {
                    errorData.put("cause", exception.getCause().getMessage());
                }
                
                // Add stack trace for debugging
                StringBuilder stackTrace = new StringBuilder();
                for (StackTraceElement element : exception.getStackTrace()) {
                    stackTrace.append(element.toString()).append("\n");
                }
                errorData.put("stackTrace", stackTrace.toString());
            }
            
            errorOutputConsumer.accept(OutputFormatter.toJson(errorData, verbose));
        } else {
            // For text format, prepend severity for WARNING and above
            if (severity == Severity.ERROR || severity == Severity.SYSTEM || severity == Severity.SECURITY) {
                errorOutputConsumer.accept(severity.name() + " ERROR: " + message);
            } else if (severity == Severity.WARNING) {
                errorOutputConsumer.accept("WARNING: " + message);
            } else {
                errorOutputConsumer.accept("Error: " + message);
            }
            
            if (verbose && exception != null) {
                // Print stack trace for debugging in verbose mode
                exception.printStackTrace();
            }
        }
    }
    
    /**
     * Outputs an error message in the appropriate format.
     * Uses ERROR severity by default.
     *
     * @param message the error message
     * @param exception the exception that occurred (may be null)
     */
    public void outputError(String message, Throwable exception) {
        outputError(message, exception, Severity.ERROR);
    }
    
    /**
     * Handles command success by tracking it and returning a success code.
     *
     * @param operationId the operation ID for tracking
     * @param result the operation result
     * @return the success exit code (0)
     */
    public int handleSuccess(String operationId, Object result) {
        metadataService.completeOperation(operationId, result);
        return 0;
    }
    
    /**
     * Creates a standard result map for successful operations.
     *
     * @param commandName the command name
     * @param data any additional result data
     * @return a map containing the result data
     */
    public Map<String, Object> createSuccessResult(String commandName, Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("command", commandName);
        
        if (data != null) {
            result.putAll(data);
        }
        
        return result;
    }
}