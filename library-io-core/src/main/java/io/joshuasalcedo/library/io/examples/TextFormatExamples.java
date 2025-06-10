package io.joshuasalcedo.library.io.examples;

import io.joshuasalcedo.library.io.core.text.format.TextFormat;
import io.joshuasalcedo.library.io.core.text.format.TextFormatter;

/**
 * Comprehensive examples demonstrating all features of the TextFormat API.
 */
public class TextFormatExamples {
    
    public static void main(String[] args) {
        demonstrateFactoryMethods();
        demonstrateCaseConversions();
        demonstrateTextManipulation();
        demonstratePaddingAndTruncation();
        demonstrateFluentAdditions();
        demonstrateConditionalOperations();
        demonstrateCustomTransformations();
        demonstrateQueryMethods();
        demonstrateChainingOperations();
    }
    
    /**
     * Demonstrates factory methods for creating TextFormat instances.
     */
    private static void demonstrateFactoryMethods() {
        System.out.println("=== Factory Methods ===");
        
        // Creating with initial text
        String result1 = TextFormat.of("Hello World").get();
        // Result: "Hello World"
        System.out.println("TextFormat.of(\"Hello World\"): " + result1);
        
        // Creating empty instance
        String result2 = TextFormat.empty().append("Starting fresh").get();
        // Result: "Starting fresh"
        System.out.println("TextFormat.empty().append(): " + result2);
        
        System.out.println();
    }
    
    /**
     * Demonstrates all case conversion methods.
     */
    private static void demonstrateCaseConversions() {
        System.out.println("=== Case Conversions ===");
        String original = "hello world example";
        
        // UPPERCASE
        String upper = TextFormat.of(original).toUpperCase().get();
        // Result: "HELLO WORLD EXAMPLE"
        System.out.println("toUpperCase(): " + upper);
        
        // lowercase
        String lower = TextFormat.of("HELLO WORLD").toLowerCase().get();
        // Result: "hello world"
        System.out.println("toLowerCase(): " + lower);
        
        // camelCase
        String camel = TextFormat.of(original).toCamelCase().get();
        // Result: "helloWorldExample"
        System.out.println("toCamelCase(): " + camel);
        
        // PascalCase
        String pascal = TextFormat.of(original).toPascalCase().get();
        // Result: "HelloWorldExample"
        System.out.println("toPascalCase(): " + pascal);
        
        // snake_case
        String snake = TextFormat.of("hello world example").toSnakeCase().get();
        // Result: "hello_world_example"
        System.out.println("toSnakeCase(): " + snake);
        
        // kebab-case
        String kebab = TextFormat.of("hello world example").toKebabCase().get();
        // Result: "hello-world-example"
        System.out.println("toKebabCase(): " + kebab);
        
        // Title Case
        String title = TextFormat.of(original).toTitleCase().get();
        // Result: "Hello World Example"
        System.out.println("toTitleCase(): " + title);
        
        // Sentence case
        String sentence = TextFormat.of(original).toSentenceCase().get();
        // Result: "Hello world example"
        System.out.println("toSentenceCase(): " + sentence);
        
        // Generic case conversion
        String genericCase = TextFormat.of(original)
            .toCase(TextFormatter.CaseType.PASCAL_CASE).get();
        // Result: "HelloWorldExample"
        System.out.println("toCase(PASCAL_CASE): " + genericCase);
        
        System.out.println();
    }
    
    /**
     * Demonstrates text manipulation methods.
     */
    private static void demonstrateTextManipulation() {
        System.out.println("=== Text Manipulation ===");
        
        // Capitalize first letter
        String capitalized = TextFormat.of("hello").capitalize().get();
        // Result: "Hello"
        System.out.println("capitalize(): " + capitalized);
        
        // Capitalize each word
        String capitalizedWords = TextFormat.of("hello world example")
            .capitalizeWords().get();
        // Result: "Hello World Example"
        System.out.println("capitalizeWords(): " + capitalizedWords);
        
        // Reverse text
        String reversed = TextFormat.of("Hello").reverse().get();
        // Result: "olleH"
        System.out.println("reverse(): " + reversed);
        
        // Trim whitespace
        String trimmed = TextFormat.of("  Hello World  ").trim().get();
        // Result: "Hello World"
        System.out.println("trim(): \"" + trimmed + "\"");
        
        // Remove all whitespace
        String noWhitespace = TextFormat.of("Hello World Example")
            .removeWhitespace().get();
        // Result: "HelloWorldExample"
        System.out.println("removeWhitespace(): " + noWhitespace);
        
        // Replace text
        String replaced = TextFormat.of("Hello World")
            .replace("World", "Universe").get();
        // Result: "Hello Universe"
        System.out.println("replace(): " + replaced);
        
        // Wrap text at line length
        String wrapped = TextFormat.of("This is a long text that needs wrapping")
            .wrap(15).get();
        // Result: "This is a long\ntext that needs\nwrapping"
        System.out.println("wrap(15):\n" + wrapped);
        
        System.out.println();
    }
    
    /**
     * Demonstrates padding and truncation methods.
     */
    private static void demonstratePaddingAndTruncation() {
        System.out.println("=== Padding and Truncation ===");
        
        // Truncate with default ellipsis
        String truncated1 = TextFormat.of("This is a long text")
            .truncate(10).get();
        // Result: "This is..."
        System.out.println("truncate(10): " + truncated1);
        
        // Truncate with custom ellipsis
        String truncated2 = TextFormat.of("This is a long text")
            .truncate(10, "---").get();
        // Result: "This is---"
        System.out.println("truncate(10, \"---\"): " + truncated2);
        
        // Pad left with custom character
        String padLeft1 = TextFormat.of("123").padLeft(6, '0').get();
        // Result: "000123"
        System.out.println("padLeft(6, '0'): " + padLeft1);
        
        // Pad left with spaces
        String padLeft2 = TextFormat.of("Hello").padLeft(10).get();
        // Result: "     Hello"
        System.out.println("padLeft(10): \"" + padLeft2 + "\"");
        
        // Pad right with custom character
        String padRight1 = TextFormat.of("Hello").padRight(10, '.').get();
        // Result: "Hello....."
        System.out.println("padRight(10, '.'): " + padRight1);
        
        // Pad right with spaces
        String padRight2 = TextFormat.of("Hello").padRight(10).get();
        // Result: "Hello     "
        System.out.println("padRight(10): \"" + padRight2 + "\"");
        
        // Repeat text
        String repeated = TextFormat.of("Ha").repeat(3).get();
        // Result: "HaHaHa"
        System.out.println("repeat(3): " + repeated);
        
        System.out.println();
    }
    
    /**
     * Demonstrates fluent addition methods.
     */
    private static void demonstrateFluentAdditions() {
        System.out.println("=== Fluent Additions ===");
        
        // Append text
        String appended = TextFormat.of("Hello").append(" World").get();
        // Result: "Hello World"
        System.out.println("append(): " + appended);
        
        // Prepend text
        String prepended = TextFormat.of("World").prepend("Hello ").get();
        // Result: "Hello World"
        System.out.println("prepend(): " + prepended);
        
        // Surround with different prefix and suffix
        String surrounded1 = TextFormat.of("content")
            .surround("[", "]").get();
        // Result: "[content]"
        System.out.println("surround(\"[\", \"]\"): " + surrounded1);
        
        // Surround with same delimiter
        String surrounded2 = TextFormat.of("quoted")
            .surround("\"").get();
        // Result: "\"quoted\""
        System.out.println("surround(\"\\\"\"): " + surrounded2);
        
        System.out.println();
    }
    
    /**
     * Demonstrates conditional operations.
     */
    private static void demonstrateConditionalOperations() {
        System.out.println("=== Conditional Operations ===");
        
        // Conditional with true condition
        int priority = 7;
        String result1 = TextFormat.of("important")
            .when(priority > 5, tf -> tf.toUpperCase())
            .prepend("Alert: ")
            .get();
        // Result: "Alert: IMPORTANT"
        System.out.println("when(true) with priority " + priority + ": " + result1);
        
        // Conditional with false condition
        priority = 3;
        String result2 = TextFormat.of("normal")
            .when(priority > 5, tf -> tf.toUpperCase())
            .prepend("Info: ")
            .get();
        // Result: "Info: normal"
        System.out.println("when(false) with priority " + priority + ": " + result2);
        
        // Multiple conditional operations
        boolean isUrgent = true;
        boolean isPublic = false;
        String result3 = TextFormat.of("message")
            .when(isUrgent, tf -> tf.toUpperCase())
            .when(isPublic, tf -> tf.prepend("PUBLIC: "))
            .when(!isPublic, tf -> tf.prepend("PRIVATE: "))
            .get();
        // Result: "PRIVATE: MESSAGE"
        System.out.println("Multiple conditions: " + result3);
        
        System.out.println();
    }
    
    /**
     * Demonstrates custom transformations.
     */
    private static void demonstrateCustomTransformations() {
        System.out.println("=== Custom Transformations ===");
        
        // Replace vowels with asterisks
        String custom1 = TextFormat.of("data")
            .transform(s -> s.replaceAll("[aeiou]", "*"))
            .toUpperCase()
            .get();
        // Result: "D*T*"
        System.out.println("Replace vowels: " + custom1);
        
        // Custom length limiter
        String custom2 = TextFormat.of("This is a test")
            .transform(s -> s.length() > 10 ? s.substring(0, 10) : s)
            .append("...")
            .get();
        // Result: "This is a ..."
        System.out.println("Custom length limit: " + custom2);
        
        // ROT13 cipher
        String custom3 = TextFormat.of("Hello")
            .transform(s -> {
                StringBuilder result = new StringBuilder();
                for (char c : s.toCharArray()) {
                    if (c >= 'a' && c <= 'z') {
                        result.append((char) ('a' + (c - 'a' + 13) % 26));
                    } else if (c >= 'A' && c <= 'Z') {
                        result.append((char) ('A' + (c - 'A' + 13) % 26));
                    } else {
                        result.append(c);
                    }
                }
                return result.toString();
            })
            .get();
        // Result: "Uryyb"
        System.out.println("ROT13 cipher: " + custom3);
        
        // Remove numbers
        String custom4 = TextFormat.of("Test123Example456")
            .transform(s -> s.replaceAll("\\d", ""))
            .get();
        // Result: "TestExample"
        System.out.println("Remove numbers: " + custom4);
        
        System.out.println();
    }
    
    /**
     * Demonstrates query methods.
     */
    private static void demonstrateQueryMethods() {
        System.out.println("=== Query Methods ===");
        
        TextFormat text1 = TextFormat.of("Hello World Example");
        
        // Word count
        int words = text1.wordCount();
        // Result: 3
        System.out.println("wordCount(): " + words);
        
        // Text length
        int length = text1.length();
        // Result: 19
        System.out.println("length(): " + length);
        
        // Check if blank
        boolean blank1 = text1.isBlank();
        // Result: false
        System.out.println("isBlank() for \"Hello World Example\": " + blank1);
        
        boolean blank2 = TextFormat.of("   ").isBlank();
        // Result: true
        System.out.println("isBlank() for \"   \": " + blank2);
        
        // Check if not blank
        boolean notBlank = text1.isNotBlank();
        // Result: true
        System.out.println("isNotBlank(): " + notBlank);
        
        // Check case type
        boolean isTitle = text1.isCase(TextFormatter.CaseType.TITLE_CASE);
        // Result: true
        System.out.println("isCase(TITLE_CASE): " + isTitle);
        
        boolean isCamel = TextFormat.of("helloWorld")
            .isCase(TextFormatter.CaseType.CAMEL_CASE);
        // Result: true
        System.out.println("isCase(CAMEL_CASE) for \"helloWorld\": " + isCamel);
        
        // Copy for branching
        TextFormat original = TextFormat.of("base text");
        TextFormat copy1 = original.copy().toUpperCase();
        TextFormat copy2 = original.copy().toLowerCase();
        // original: "base text", copy1: "BASE TEXT", copy2: "base text"
        System.out.println("Original: " + original.get() + 
                         ", Copy1: " + copy1.get() + 
                         ", Copy2: " + copy2.get());
        
        System.out.println();
    }
    
    /**
     * Demonstrates complex chaining operations.
     */
    private static void demonstrateChainingOperations() {
        System.out.println("=== Complex Chaining Examples ===");
        
        // Email formatter
        String email = TextFormat.of("  John DOE  ")
            .trim()
            .toLowerCase()
            .replace(" ", ".")
            .append("@company.com")
            .get();
        // Result: "john.doe@company.com"
        System.out.println("Email formatter: " + email);
        
        // Code variable name formatter
        String varName = TextFormat.of("Get User Name By ID")
            .toCamelCase()
            .prepend("private String ")
            .append(";")
            .get();
        // Result: "private String getUserNameById;"
        System.out.println("Variable declaration: " + varName);
        
        // Log message formatter
        String logMessage = TextFormat.of("error occurred")
            .when(true, tf -> tf.toUpperCase())
            .prepend("[ERROR] ")
            .append(" at " + System.currentTimeMillis())
            .truncate(50)
            .get();
        // Result: "[ERROR] ERROR OCCURRED at 1234567890123..." (truncated)
        System.out.println("Log message: " + logMessage);
        
        // SQL-like formatter
        String sqlColumn = TextFormat.of("First Name")
            .toSnakeCase()
            .toUpperCase()
            .prepend("USER_TABLE.")
            .get();
        // Result: "USER_TABLE.FIRST_NAME"
        System.out.println("SQL column: " + sqlColumn);
        
        // File name sanitizer
        String fileName = TextFormat.of("My File: Version 2.0 (Final)")
            .replace(":", "-")
            .replace("(", "")
            .replace(")", "")
            .trim()
            .toKebabCase()
            .append(".txt")
            .get();
        // Result: "my-file--version-2.0-final.txt"
        System.out.println("Sanitized filename: " + fileName);
        
        System.out.println();
    }
}