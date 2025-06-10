# Text Package

The `text` package provides a fluent API for text processing and manipulation. It offers a powerful and intuitive way to perform various text operations such as cleaning, transforming, validating, and generating text.

## Overview

The Text API is designed to be chainable and easy to use while providing flexibility for complex text processing requirements. It supports operations on single strings or collections of strings, with comprehensive validation and transformation capabilities.

## Key Features

- **Fluent, chainable API** for text operations
- **Text manipulation** (capitalize, truncate, reverse, etc.)
- **Text transformation** (replace, replaceAll, transform)
- **Text validation** (require, notBlank, matches, etc.)
- **Text generation** (generateId)
- **Support for bulk operations** on multiple strings
- **Fail-fast validation**

## Main Components

### Text Class

The main class providing a fluent API for text processing operations.

## Usage Examples

### Basic Text Operations

```java
// Capitalize and trim a string
String result = Text.of("  hello world  ")
    .trim()
    .capitalize()
    .get();  // Returns "Hello world"
```

### Text Transformation

```java
// Transform text with multiple operations
String result = Text.of("user_name")
    .replace("_", " ")
    .capitalizeWords()
    .get();  // Returns "User Name"
```

### Text Validation

```java
// Validate text with custom requirements
try {
    String email = Text.of(userInput)
        .trim()
        .toLowerCase()
        .require(Text.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$"), "Invalid email format")
        .getOrThrow();
} catch (IllegalArgumentException e) {
    // Handle validation error
}
```

### Bulk Operations

```java
// Process multiple strings at once
List<String> names = Text.of("john", "mary", "robert")
    .capitalize()
    .execute();  // Returns ["John", "Mary", "Robert"]
```

### Text Generation

```java
// Generate unique IDs
String id = Text.generateId("user-")
    .get();  // Returns something like "user-a1b2c3d4-e5f6-g7h8-i9j0"
```

### Filtering

```java
// Filter a list of strings
List<String> nonEmpty = Text.of("hello", "", "world", null, "java")
    .filter(Text.notBlank())
    .execute();  // Returns ["hello", "world", "java"]
```

### Custom Transformations

```java
// Apply custom transformation function
String result = Text.of("hello world")
    .transform(s -> s.replaceAll("[aeiou]", "*"))
    .get();  // Returns "h*ll* w*rld"
```

### Joining Strings

```java
// Join multiple strings with a delimiter
String csv = Text.of("apple", "banana", "cherry")
    .join(",");  // Returns "apple,banana,cherry"
```

### Default Values

```java
// Provide default value for null or empty strings
String name = Text.of(nullableInput)
    .getOrDefault("Guest");  // Returns "Guest" if input is null or empty
```

## Advanced Features

### Chaining Multiple Operations

```java
// Chain multiple operations for complex text processing
String processed = Text.of(rawInput)
    .trim()
    .toLowerCase()
    .replace("\\s+", " ")
    .truncate(100)
    .get();
```

### Conditional Processing

```java
// Apply transformations conditionally
String result = Text.of(input)
    .transform(s -> {
        if (s.length() > 10) {
            return s.substring(0, 10) + "...";
        }
        return s;
    })
    .get();
```

## Error Handling

The Text API handles errors gracefully:

- By default, null values are handled gracefully (treated as empty strings)
- The failFast() method enables strict validation that throws exceptions for null values
- The require() method allows custom validation with specific error messages
- The getOrThrow() method throws an exception if the result is null or empty

```java
try {
    // This will throw an exception if the input is blank
    String validated = Text.of(input)
        .failFast()
        .require(Text.notBlank(), "Input cannot be blank")
        .get();
} catch (IllegalArgumentException e) {
    // Handle validation error
}
```

## Best Practices

1. **Chain operations** for concise and readable code
2. **Use built-in predicates** (notBlank, matches, etc.) for common validations
3. **Use require()** for validation with specific error messages
4. **Use getOrDefault()** for handling null or empty values
5. **Use transform()** for custom transformations
6. **Use execute()** for bulk operations on multiple strings

## Thread Safety

The Text class is immutable and thread-safe. Each operation returns a new Text instance without modifying the original.

## See Also

- [JavaDoc API](apidocs/io/joshuasalcedo/library/io/core/text/package-summary.html)
- [String Operations](index.html#string-operations)