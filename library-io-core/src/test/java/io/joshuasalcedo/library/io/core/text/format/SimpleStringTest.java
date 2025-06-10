//package io.joshuasalcedo.library.io.core.text.format;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * A simple test class to demonstrate JUnit 5 testing in the project.
// */
//public class SimpleStringTest {
//
//    @Test
//    @DisplayName("Test string concatenation")
//    public void testStringConcatenation() {
//        String str1 = "Hello";
//        String str2 = " World";
//        String result = str1 + str2;
//
//        assertEquals("Hello World", result, "String concatenation should work correctly");
//    }
//
//    @Test
//    @DisplayName("Test string uppercase conversion")
//    public void testUpperCase() {
//        String original = "hello world";
//        String result = original.toUpperCase();
//
//        assertEquals("HELLO WORLD", result, "String should be converted to uppercase");
//    }
//
//    @Test
//    @DisplayName("Test string lowercase conversion")
//    public void testLowerCase() {
//        String original = "HELLO WORLD";
//        String result = original.toLowerCase();
//
//        assertEquals("hello world", result, "String should be converted to lowercase");
//    }
//
//    @Test
//    @DisplayName("Test string contains method")
//    public void testContains() {
//        String text = "The quick brown fox jumps over the lazy dog";
//
//        assertTrue(text.contains("quick"), "String should contain 'quick'");
//        assertTrue(text.contains("fox"), "String should contain 'fox'");
//        assertFalse(text.contains("cat"), "String should not contain 'cat'");
//    }
//
//    @Test
//    @DisplayName("Test string replace method")
//    public void testReplace() {
//        String original = "The quick brown fox";
//        String result = original.replace("fox", "dog");
//
//        assertEquals("The quick brown dog", result, "String replacement should work correctly");
//    }
//}