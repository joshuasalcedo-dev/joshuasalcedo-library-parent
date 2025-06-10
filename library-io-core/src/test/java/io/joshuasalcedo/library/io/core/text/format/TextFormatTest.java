//package io.joshuasalcedo.library.io.core.text.format;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Test class for {@link TextFormat}.
// */
//public class TextFormatTest {
//
//    @Test
//    @DisplayName("Test basic text transformations")
//    public void testBasicTransformations() {
//        // Test uppercase transformation
//        String result = TextFormat.of("hello world").toUpperCase().get();
//        assertEquals("HELLO WORLD", result, "Text should be converted to uppercase");
//
//        // Test lowercase transformation
//        result = TextFormat.of("HELLO WORLD").toLowerCase().get();
//        assertEquals("hello world", result, "Text should be converted to lowercase");
//
//        // Test capitalization
//        result = TextFormat.of("hello world").capitalize().get();
//        assertEquals("Hello world", result, "First letter should be capitalized");
//
//        // Test word capitalization
//        result = TextFormat.of("hello world").capitalizeWords().get();
//        assertEquals("Hello World", result, "First letter of each word should be capitalized");
//    }
//
//    @Test
//    @DisplayName("Test case conversions")
//    public void testCaseConversions() {
//        // Test camelCase
//        String result = TextFormat.of("hello world").toCamelCase().get();
//        assertEquals("helloWorld", result, "Text should be converted to camelCase");
//
//        // Test PascalCase
//        result = TextFormat.of("hello world").toPascalCase().get();
//        assertEquals("HelloWorld", result, "Text should be converted to PascalCase");
//
//        // Test snake_case
//        result = TextFormat.of("helloWorld").toSnakeCase().get();
//        assertEquals("hello_world", result, "Text should be converted to snake_case");
//
//        // Test kebab-case
//        result = TextFormat.of("helloWorld").toKebabCase().get();
//        assertEquals("hello-world", result, "Text should be converted to kebab-case");
//    }
//
//    @Test
//    @DisplayName("Test string manipulations")
//    public void testStringManipulations() {
//        // Test trim
//        String result = TextFormat.of("  hello world  ").trim().get();
//        assertEquals("hello world", result, "Whitespace should be trimmed");
//
//        // Test remove whitespace
//        result = TextFormat.of("hello world").removeWhitespace().get();
//        assertEquals("helloworld", result, "All whitespace should be removed");
//
//        // Test truncate
//        result = TextFormat.of("hello world").truncate(5).get();
//        assertEquals("hello...", result, "Text should be truncated with default ellipsis");
//
//        // Test truncate with custom ellipsis
//        result = TextFormat.of("hello world").truncate(5, "***").get();
//        assertEquals("hello***", result, "Text should be truncated with custom ellipsis");
//    }
//
//    @Test
//    @DisplayName("Test chaining operations")
//    public void testChaining() {
//        // Test multiple operations chained together
//        String result = TextFormat.of("  hello world  ")
//                .trim()
//                .capitalizeWords()
//                .surround("\"", "\"")
//                .get();
//
//        assertEquals("\"Hello World\"", result, "Chained operations should be applied in order");
//    }
//
//    @Test
//    @DisplayName("Test conditional transformations")
//    public void testConditionalTransformations() {
//        // Test when condition is true
//        String result = TextFormat.of("hello")
//                .when(true, format -> format.append(" world"))
//                .get();
//
//        assertEquals("hello world", result, "Transformation should be applied when condition is true");
//
//        // Test when condition is false
//        result = TextFormat.of("hello")
//                .when(false, format -> format.append(" world"))
//                .get();
//
//        assertEquals("hello", result, "Transformation should not be applied when condition is false");
//    }
//}