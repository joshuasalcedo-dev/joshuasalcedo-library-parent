# Find Package

The `find` package provides a fluent API for finding files and directories in the file system. It offers a powerful and intuitive way to search for files and directories using pattern matching, custom filters, and content searching capabilities.

## Overview

The Find API is designed to be simple for common use cases while providing flexibility for complex search requirements. It supports glob pattern matching, custom filtering with predicates, content searching within files, and cross-platform path handling.

## Key Features

- **Fluent, chainable API** for building search queries
- **Glob pattern matching** (e.g., `*.java`, `**/*.xml`)
- **Custom filtering** with predicates
- **Content searching** within files
- **Cross-platform path handling**
- **Recursive directory traversal**
- **Performance optimizations** for large file trees

## Main Components

### Find Class

The main entry point providing a fluent API for building and executing file searches.

### Finder Interface

The core interface defining file finding operations.

## Usage Examples

### Finding Files by Pattern

```java
// Find all Java files
List<Path> javaFiles = Find.in("/src")
    .matching("*.java")
    .execute();

// Find all XML files in any subdirectory
List<Path> xmlFiles = Find.in(projectDir)
    .matching("**/*.xml")
    .execute();
```

### Filtering Results

```java
// Find large log files
List<Path> largeLogs = Find.in("/var/log")
    .matching("*.log")
    .filter(path -> Files.size(path) > 10_000_000)  // > 10MB
    .execute();

// Find recently modified config files
List<Path> recentConfigs = Find.in(".")
    .matching("*.conf")
    .filter(path -> {
        FileTime time = Files.getLastModifiedTime(path);
        return time.toInstant().isAfter(
            Instant.now().minus(7, ChronoUnit.DAYS)
        );
    })
    .execute();
```

### Content Searching

```java
// Find Java files containing TODO comments
List<Path> todoFiles = Find.in("src")
    .matching("*.java")
    .containing("TODO")
    .execute();

// Find configuration files with database connections
List<Path> dbConfigs = Find.in("config")
    .matching("*.properties")
    .containing("jdbc:postgresql")
    .execute();
```

### Path Filtering

```java
// Exclude build directories
List<Path> sourceFiles = Find.in(".")
    .matching("*.java")
    .excluding("target")
    .excluding("build")
    .execute();

// Include only test files
List<Path> testFiles = Find.in(".")
    .matching("*Test.java")
    .including("src/test")
    .execute();

// Exclude multiple directories at once
List<Path> appFiles = Find.in(".")
    .matching("*.js")
    .excludingAny("node_modules", "dist", "build", ".git")
    .execute();
```

### Convenience Methods

```java
// Get first match only
Path readme = Find.in(".")
    .matching("README.md")
    .executeFirst();

// Count matches without retrieving paths
long javaFileCount = Find.in("src")
    .matching("*.java")
    .count();

// Check if any matches exist
boolean hasTests = Find.in("src/test")
    .matching("*Test.java")
    .exists();
```

## Advanced Features

### Pattern Syntax

The package uses glob patterns with the following wildcards:

- `*` - matches any number of characters (excluding directory separators)
- `**` - matches any number of characters including directory separators
- `?` - matches exactly one character
- `[...]` - matches any single character in the brackets
- `[!...]` - matches any single character NOT in the brackets
- `[a-z]` - matches any character in the range

### Performance Considerations

The implementation includes several optimizations for better performance:

- Automatically skips hidden directories and common build/cache directories
- Uses different strategies for content searching based on file size:
  - Files < 1MB are read entirely into memory
  - Files >= 1MB are streamed line by line
  - Files > 100MB are skipped for content search
- Binary files are automatically detected and skipped during content search

## Error Handling

The Find API handles errors gracefully:

- `IllegalArgumentException` - thrown for invalid inputs (null values, invalid patterns)
- `IllegalStateException` - thrown when execute() is called without matching() pattern
- `UncheckedIOException` - wraps IOException during file operations
- Access denied errors are logged but don't stop the search

```java
try {
    List<Path> files = Find.in(directory)
        .matching("*.txt")
        .execute();
} catch (IllegalArgumentException e) {
    // Handle invalid input
} catch (UncheckedIOException e) {
    // Handle I/O errors
}
```

## Best Practices

1. **Be specific with patterns** to improve performance
2. **Use content searching sparingly** on large directories
3. **Combine pattern matching with filtering** for complex searches
4. **Use executeFirst()** when you only need one result
5. **Use count() or exists()** when you don't need the actual paths
6. **Handle exceptions** appropriately

## Thread Safety

The Find builder is not thread-safe and should not be shared between threads. However, the search execution itself is thread-safe, and multiple searches can be performed concurrently from different Find instances.

## See Also

- [JavaDoc API](apidocs/io/joshuasalcedo/library/io/core/find/package-summary.html)
- [File Operations](index.html#file-operations)