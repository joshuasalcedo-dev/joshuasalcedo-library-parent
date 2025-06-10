//package io.joshuasalcedo.library.io.core.test;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * A basic test class to demonstrate JUnit 5 testing in the project.
// */
//public class BasicTest {
//
//    @Test
//    @DisplayName("Test basic addition")
//    public void testAddition() {
//        int a = 5;
//        int b = 7;
//        int result = a + b;
//
//        assertEquals(12, result, "Addition should work correctly");
//    }
//
//    @Test
//    @DisplayName("Test string operations")
//    public void testStringOperations() {
//        String text = "Hello, World!";
//
//        assertTrue(text.startsWith("Hello"), "String should start with 'Hello'");
//        assertTrue(text.endsWith("!"), "String should end with '!'");
//        assertEquals(13, text.length(), "String length should be 13");
//    }
//
//    @Test
//    @DisplayName("Test boolean assertions")
//    public void testBooleanAssertions() {
//        assertTrue(true, "True should be true");
//        assertFalse(false, "False should be false");
//        assertNotEquals(true, false, "True should not equal false");
//    }
//}