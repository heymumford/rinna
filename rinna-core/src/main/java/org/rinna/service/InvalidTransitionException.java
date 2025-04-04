/*
 * Service component for the Rinna workflow management system
 *
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 * This file is subject to the terms and conditions defined in
 * the LICENSE file, which is part of this source code package.
 */

package org.rinna.service;

/**
 * Exception thrown when an invalid workflow transition is attempted.
 */
public class InvalidTransitionException extends Exception {
    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new InvalidTransitionException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidTransitionException(String message) {
        super(message);
    }
}