/**
 * Provides a fluent API for finding files and directories in the file system.
 *
 * <p>This package offers a powerful and intuitive way to search for files and directories
 * using pattern matching, custom filters, and content searching capabilities. The API is
 * designed to be simple for common use cases while providing flexibility for complex
 * search requirements.</p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Fluent, chainable API for building search queries</li>
 *   <li>Glob pattern matching (e.g., {@code *.java}, {@code **&#47;*.xml})</li>
 *   <li>Custom filtering with predicates</li>
 *   <li>Content searching within files</li>
 *   <li>Cross-platform path handling</li>
 *   <li>Recursive directory traversal</li>
 *   <li>Performance optimizations for large file trees</li>
 * </ul>
 *
 * <h2>Main Components</h2>
 * <dl>
 *   <dt>{@link io.joshuasalcedo.library.io.core.find.Find}</dt>
 *   <dd>The main entry point providing a fluent API for building and executing file searches</dd>
 *
 *   <dt>{@link io.joshuasalcedo.library.io.core.find.Finder}</dt>
 *   <dd>The core interface defining file finding operations</dd>
 *
 *   <dt>FinderImpl</dt>
 *   <dd>The default implementation using Java NIO.2 for efficient file tree walking</dd>
 * </dl>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Finding files by pattern</h3>
 * <pre>{@code
 * // Find all Java files
 * List<Path> javaFiles = Find.in("/src")
 *     .matching("*.java")
 *     .execute();
 *
 * // Find all XML files in any subdirectory
 * List<Path> xmlFiles = Find.in(projectDir)
 *     .matching("**&#47;*.xml")
 *     .execute();
 * }</pre>
 *
 * <h3>Filtering results</h3>
 * <pre>{@code
 * // Find large log files
 * List<Path> largeLogs = Find.in("/var/log")
 *     .matching("*.log")
 *     .filter(path -> Files.size(path) > 10_000_000)  // > 10MB
 *     .execute();
 *
 * // Find recently modified config files
 * List<Path> recentConfigs = Find.in(".")
 *     .matching("*.conf")
 *     .filter(path -> {
 *         FileTime time = Files.getLastModifiedTime(path);
 *         return time.toInstant().isAfter(
 *             Instant.now().minus(7, ChronoUnit.DAYS)
 *         );
 *     })
 *     .execute();
 * }</pre>
 *
 * <h3>Content searching</h3>
 * <pre>{@code
 * // Find Java files containing TODO comments
 * List<Path> todoFiles = Find.in("src")
 *     .matching("*.java")
 *     .containing("TODO")
 *     .execute();
 *
 * // Find configuration files with database connections
 * List<Path> dbConfigs = Find.in("config")
 *     .matching("*.properties")
 *     .containing("jdbc:postgresql")
 *     .execute();
 * }</pre>
 *
 * <h3>Path filtering</h3>
 * <pre>{@code
 * // Exclude build directories
 * List<Path> sourceFiles = Find.in(".")
 *     .matching("*.java")
 *     .excluding("target")
 *     .excluding("build")
 *     .execute();
 *
 * // Include only test files
 * List<Path> testFiles = Find.in(".")
 *     .matching("*Test.java")
 *     .including("src/test")
 *     .execute();
 *
 * // Exclude multiple directories at once
 * List<Path> appFiles = Find.in(".")
 *     .matching("*.js")
 *     .excludingAny("node_modules", "dist", "build", ".git")
 *     .execute();
 * }</pre>
 *
 * <h3>Convenience methods</h3>
 * <pre>{@code
 * // Get first match only
 * Path readme = Find.in(".")
 *     .matching("README.md")
 *     .executeFirst();
 *
 * // Count matches without retrieving paths
 * long javaFileCount = Find.in("src")
 *     .matching("*.java")
 *     .count();
 *
 * // Check if any matches exist
 * boolean hasTests = Find.in("src/test")
 *     .matching("*Test.java")
 *     .exists();
 * }</pre>
 *
 * <h2>Pattern Syntax</h2>
 * <p>The package uses glob patterns with the following wildcards:</p>
 * <ul>
 *   <li>{@code *} - matches any number of characters (excluding directory separators)</li>
 *   <li>{@code **} - matches any number of characters including directory separators</li>
 *   <li>{@code ?} - matches exactly one character</li>
 *   <li>{@code [...]} - matches any single character in the brackets</li>
 *   <li>{@code [!...]} - matches any single character NOT in the brackets</li>
 *   <li>{@code [a-z]} - matches any character in the range</li>
 * </ul>
 *
 * <h2>Performance Considerations</h2>
 * <ul>
 *   <li>The implementation automatically skips hidden directories and common build/cache
 *       directories (node_modules, .git, target, etc.) for better performance</li>
 *   <li>Content searching uses different strategies based on file size:
 *       <ul>
 *         <li>Files &lt; 1MB are read entirely into memory</li>
 *         <li>Files &gt;= 1MB are streamed line by line</li>
 *         <li>Files &gt; 100MB are skipped for content search</li>
 *       </ul>
 *   </li>
 *   <li>Binary files are automatically detected and skipped during content search</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>The Find builder is not thread-safe and should not be shared between threads.
 * However, the search execution itself is thread-safe, and multiple searches can
 * be performed concurrently from different Find instances.</p>
 *
 * <h2>Error Handling</h2>
 * <ul>
 *   <li>{@link java.lang.IllegalArgumentException} - thrown for invalid inputs (null values, invalid patterns)</li>
 *   <li>{@link java.lang.IllegalStateException} - thrown when execute() is called without matching() pattern</li>
 *   <li>{@link java.io.UncheckedIOException} - wraps IOException during file operations</li>
 *   <li>Access denied errors are logged but don't stop the search</li>
 * </ul>
 *
 * @since 1.0.0
 * @author Joshua Salcedo
 */
package io.joshuasalcedo.library.io.core.find;