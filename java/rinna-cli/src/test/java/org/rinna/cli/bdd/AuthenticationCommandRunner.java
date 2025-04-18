/*
 * Runner for Authentication command BDD tests
 *
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 * This file is subject to the terms and conditions defined in
 * the LICENSE file, which is part of this source code package.
 */

package org.rinna.cli.bdd;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 runner for authentication command tests.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/authentication-commands.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.rinna.cli.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class AuthenticationCommandRunner {
    // Test runner implementation handled by JUnit and Cucumber
}