package org.rinna.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Simple unit test with a Tag to demonstrate our test categorization system.
 */
@Tag("unit")
public class UnitTaggedTest {

    @Test
    @DisplayName("Simple unit test to demonstrate tagging")
    void testSimpleUnitTest() {
        // Just a simple test that always passes
        assertTrue(true, "This test should always pass");
    }
}
