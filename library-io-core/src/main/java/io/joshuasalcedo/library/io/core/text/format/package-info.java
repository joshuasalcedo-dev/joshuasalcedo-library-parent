/**
 * Provides a comprehensive text formatting library with a fluent API for string manipulation and transformation.
 *
 * <p>This package contains utilities for performing various text formatting operations including case conversions,
 * string manipulations, and text transformations through an intuitive, chainable interface.</p>
 *
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link io.joshuasalcedo.library.io.core.text.format.TextFormat} - The main fluent API entry point for text formatting</li>
 *   <li>{@link io.joshuasalcedo.library.io.core.text.format.TextFormatter} - The interface defining text formatting operations</li>
 *   <li>{@link io.joshuasalcedo.library.io.core.text.format.TextFormatter.CaseType} - Enumeration of supported case formats</li>
 *   <li>{@link io.joshuasalcedo.library.io.core.text.format.TextFormatter.CaseConverter} - Interface for case conversion implementations</li>
 * </ul>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Case Conversions:</strong> Transform text between various case formats (camelCase, snake_case, PascalCase, etc.)</li>
 *   <li><strong>Text Manipulations:</strong> Capitalize, reverse, trim, truncate, pad, and wrap text</li>
 *   <li><strong>Fluent Interface:</strong> Chain multiple operations for readable and concise code</li>
 *   <li><strong>Null Safety:</strong> Graceful handling of null and empty strings</li>
 *   <li><strong>Extensibility:</strong> Custom transformations through functional interfaces</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Factory Methods</h3>
 * <pre>{@code
 * // Create with initial text
 * String result1 = TextFormat.of("Hello World").get();
 * // Result: "Hello World"
 *
 * // Create empty and build
 * String result2 = TextFormat.empty().append("Built from scratch").get();
 * // Result: "Built from scratch"
 * }</pre>
 *
 * <h3>Case Conversions</h3>
 * <pre>{@code
 * String original = "hello world example";
 *
 * TextFormat.of(original).toUpperCase().get();      // "HELLO WORLD EXAMPLE"
 * TextFormat.of(original).toLowerCase().get();      // "hello world example"
 * TextFormat.of(original).toCamelCase().get();      // "helloWorldExample"
 * TextFormat.of(original).toPascalCase().get();     // "HelloWorldExample"
 * TextFormat.of(original).toSnakeCase().get();      // "hello_world_example"
 * TextFormat.of(original).toKebabCase().get();      // "hello-world-example"
 * TextFormat.of(original).toTitleCase().get();      // "Hello World Example"
 * TextFormat.of(original).toSentenceCase().get();   // "Hello world example"
 *
 * // Generic case conversion
 * TextFormat.of(original).toCase(TextFormatter.CaseType.PASCAL_CASE).get();
 * // Result: "HelloWorldExample"
 * }</pre>
 *
 * <h3>Text Manipulation</h3>
 * <pre>{@code
 * // Basic operations
 * TextFormat.of("hello").capitalize().get();                    // "Hello"
 * TextFormat.of("hello world").capitalizeWords().get();         // "Hello World"
 * TextFormat.of("Hello").reverse().get();                       // "olleH"
 * TextFormat.of("  spaces  ").trim().get();                     // "spaces"
 * TextFormat.of("Hello World").removeWhitespace().get();        // "HelloWorld"
 *
 * // Text replacement
 * TextFormat.of("Hello World").replace("World", "Java").get();  // "Hello Java"
 *
 * // Text wrapping
 * TextFormat.of("This is a long text that needs wrapping").wrap(15).get();
 * // Result: "This is a long\ntext that needs\nwrapping"
 * }</pre>
 *
 * <h3>Padding and Truncation</h3>
 * <pre>{@code
 * // Truncation
 * TextFormat.of("This is a long text").truncate(10).get();          // "This is..."
 * TextFormat.of("This is a long text").truncate(10, "---").get();   // "This is---"
 *
 * // Padding
 * TextFormat.of("123").padLeft(6, '0').get();       // "000123"
 * TextFormat.of("Hello").padLeft(10).get();         // "     Hello"
 * TextFormat.of("Hello").padRight(10, '.').get();   // "Hello....."
 * TextFormat.of("Hello").padRight(10).get();        // "Hello     "
 *
 * // Repetition
 * TextFormat.of("Ha").repeat(3).get();              // "HaHaHa"
 * }</pre>
 *
 * <h3>Fluent Additions</h3>
 * <pre>{@code
 * // Building text
 * TextFormat.of("Hello").append(" World").get();              // "Hello World"
 * TextFormat.of("World").prepend("Hello ").get();             // "Hello World"
 * TextFormat.of("content").surround("[", "]").get();          // "[content]"
 * TextFormat.of("quoted").surround("\"").get();               // "\"quoted\""
 *
 * // Chaining multiple additions
 * TextFormat.of("message")
 *     .prepend("Start: ")
 *     .append(" :End")
 *     .surround("(", ")")
 *     .get();
 * // Result: "(Start: message :End)"
 * }</pre>
 *
 * <h3>Conditional Operations</h3>
 * <pre>{@code
 * // Basic conditional
 * int priority = 7;
 * String alert = TextFormat.of("important")
 *     .when(priority > 5, tf -> tf.toUpperCase())
 *     .prepend("Alert: ")
 *     .get();
 * // Result: "Alert: IMPORTANT"
 *
 * // Multiple conditions
 * boolean isUrgent = true;
 * boolean isPublic = false;
 * String message = TextFormat.of("message")
 *     .when(isUrgent, tf -> tf.toUpperCase())
 *     .when(isPublic, tf -> tf.prepend("PUBLIC: "))
 *     .when(!isPublic, tf -> tf.prepend("PRIVATE: "))
 *     .get();
 * // Result: "PRIVATE: MESSAGE"
 * }</pre>
 *
 * <h3>Custom Transformations</h3>
 * <pre>{@code
 * // Replace vowels
 * TextFormat.of("data")
 *     .transform(s -> s.replaceAll("[aeiou]", "*"))
 *     .toUpperCase()
 *     .get();
 * // Result: "D*T*"
 *
 * // Remove numbers
 * TextFormat.of("Test123Example456")
 *     .transform(s -> s.replaceAll("\\d", ""))
 *     .get();
 * // Result: "TestExample"
 *
 * // Custom cipher
 * TextFormat.of("Secret")
 *     .transform(s -> {
 *         StringBuilder result = new StringBuilder();
 *         for (char c : s.toCharArray()) {
 *             result.append((char)(c + 1));
 *         }
 *         return result.toString();
 *     })
 *     .get();
 * // Result: "Tfdsfu"
 * }</pre>
 *
 * <h3>Query Methods</h3>
 * <pre>{@code
 * TextFormat text = TextFormat.of("Hello World Example");
 *
 * // Text properties
 * int words = text.wordCount();                              // 3
 * int length = text.length();                                 // 19
 * boolean blank = text.isBlank();                            // false
 * boolean notBlank = text.isNotBlank();                      // true
 *
 * // Case checking
 * boolean isTitle = text.isCase(TextFormatter.CaseType.TITLE_CASE);     // true
 * boolean isCamel = TextFormat.of("helloWorld")
 *     .isCase(TextFormatter.CaseType.CAMEL_CASE);                       // true
 *
 * // Copying for branching
 * TextFormat base = TextFormat.of("original");
 * TextFormat upper = base.copy().toUpperCase();              // "ORIGINAL"
 * TextFormat lower = base.copy().toLowerCase();              // "original"
 * String original = base.get();                              // "original" (unchanged)
 * }</pre>
 *
 * <h3>Real-World Use Cases</h3>
 * <pre>{@code
 * // Email formatter
 * String email = TextFormat.of("  John DOE  ")
 *     .trim()
 *     .toLowerCase()
 *     .replace(" ", ".")
 *     .append("@company.com")
 *     .get();
 * // Result: "john.doe@company.com"
 *
 * // Variable name generator
 * String varName = TextFormat.of("Get User Name By ID")
 *     .toCamelCase()
 *     .prepend("private String ")
 *     .append(";")
 *     .get();
 * // Result: "private String getUserNameById;"
 *
 * // SQL column formatter
 * String column = TextFormat.of("First Name")
 *     .toSnakeCase()
 *     .toUpperCase()
 *     .prepend("USER_TABLE.")
 *     .get();
 * // Result: "USER_TABLE.FIRST_NAME"
 *
 * // File name sanitizer
 * String fileName = TextFormat.of("My File: Version 2.0 (Final)")
 *     .replace(":", "-")
 *     .replace("(", "")
 *     .replace(")", "")
 *     .trim()
 *     .toKebabCase()
 *     .append(".txt")
 *     .get();
 * // Result: "my-file--version-2.0-final.txt"
 *
 * // Log message formatter
 * String logMsg = TextFormat.of("Database connection failed")
 *     .when(true, tf -> tf.toUpperCase())
 *     .prepend("[ERROR] ")
 *     .append(" at " + System.currentTimeMillis())
 *     .truncate(80)
 *     .get();
 * // Result: "[ERROR] DATABASE CONNECTION FAILED at 1234567890123..." (truncated at 80 chars)
 * }</pre>
 *
 * <h2>Supported Case Types</h2>
 * <table>
 *   <caption>Available case format conversions</caption>
 *   <tr><th>Case Type</th><th>Example</th><th>Description</th></tr>
 *   <tr><td>UPPER_CASE</td><td>HELLO WORLD</td><td>All uppercase letters</td></tr>
 *   <tr><td>LOWER_CASE</td><td>hello world</td><td>All lowercase letters</td></tr>
 *   <tr><td>CAMEL_CASE</td><td>helloWorld</td><td>First word lowercase, subsequent words capitalized</td></tr>
 *   <tr><td>PASCAL_CASE</td><td>HelloWorld</td><td>All words capitalized, no separators</td></tr>
 *   <tr><td>SNAKE_CASE</td><td>hello_world</td><td>Lowercase with underscore separators</td></tr>
 *   <tr><td>KEBAB_CASE</td><td>hello-world</td><td>Lowercase with hyphen separators</td></tr>
 *   <tr><td>TITLE_CASE</td><td>Hello World</td><td>First letter of each word capitalized</td></tr>
 *   <tr><td>SENTENCE_CASE</td><td>Hello world</td><td>First letter capitalized only</td></tr>
 * </table>
 *
 * <h2>Thread Safety</h2>
 * <p>The {@code TextFormat} class is not thread-safe as it maintains mutable state.
 * Create separate instances for concurrent use or use the {@code copy()} method to create
 * independent instances.</p>
 *
 * <h2>Performance Considerations</h2>
 * <p>The fluent API creates a new {@code TextFormat} instance only at the entry point.
 * Chained operations modify the internal state, making it efficient for multiple transformations.
 * For performance-critical applications with simple operations, consider using the
 * {@code TextFormatter} interface directly.</p>
 *
 * @since 1.0
 * @author Joshua Salcedo
 * @see java.lang.String
 * @see java.util.function.Function
 */
package io.joshuasalcedo.library.io.core.text.format;