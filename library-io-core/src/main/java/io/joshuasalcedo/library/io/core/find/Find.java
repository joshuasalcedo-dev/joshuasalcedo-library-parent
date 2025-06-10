package io.joshuasalcedo.library.io.core.find;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A fluent API utility class for finding files and directories in the file system.
 *
 * <p>This class provides a convenient, chainable interface for building file search
 * queries with pattern matching, filtering, and content searching capabilities.
 * All searches are recursive by default, traversing the entire directory tree.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // Find all Java files
 * List&lt;Path&gt; javaFiles = Find.in("/src/main/java")
 *     .matching("*.java")
 *     .execute();
 *
 * // Find Java files larger than 10KB containing "TODO"
 * List&lt;Path&gt; todoFiles = Find.in(projectDir)
 *     .matching("*.java")
 *     .filter(path -&gt; Files.size(path) &gt; 10240)
 *     .containing("TODO")
 *     .execute();
 *
 * // Find all test files excluding those in target directory
 * List&lt;Path&gt; testFiles = Find.in(".")
 *     .matching("*Test.java")
 *     .excluding("target")  // Cross-platform path filtering
 *     .execute();
 * </pre>
 *
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class Find {

    private final Path directory;
    private String pattern;
    private final List<Predicate<Path>> filters = new ArrayList<>();
    private String content;
    private static final Finder finder = FinderFactory.createFinder();

    /**
     * Private constructor to enforce fluent API usage.
     *
     * @param directory the starting directory for the search
     */
    private Find(Path directory) {
        this.directory = directory;
    }

    /**
     * Creates a new Find builder for searching in the specified directory.
     *
     * <p>This is the entry point for the fluent API. The search will recursively
     * traverse all subdirectories starting from this location.</p>
     *
     * @param directory the starting directory path as a string
     * @return a new Find builder instance
     * @throws IllegalArgumentException if directory is null
     * @throws java.nio.file.InvalidPathException if the path string cannot be converted to a Path
     */
    public static Find in(String directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        return new Find(Paths.get(directory));
    }

    /**
     * Creates a new Find builder for searching in the specified directory.
     *
     * <p>This is the entry point for the fluent API. The search will recursively
     * traverse all subdirectories starting from this location.</p>
     *
     * @param directory the starting directory path
     * @return a new Find builder instance
     * @throws IllegalArgumentException if directory is null
     */
    public static Find in(Path directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        return new Find(directory);
    }

    /**
     * Sets the pattern to match files and directories against.
     *
     * <p>The pattern uses glob syntax, supporting wildcards such as:</p>
     * <ul>
     *   <li>{@code *} - matches any number of characters (but not directory separators)</li>
     *   <li>{@code **} - matches any number of characters including directory separators</li>
     *   <li>{@code ?} - matches exactly one character</li>
     *   <li>{@code [...]} - matches any single character in the brackets</li>
     * </ul>
     *
     * <p>Examples:</p>
     * <ul>
     *   <li>{@code "*.java"} - all Java files in any directory</li>
     *   <li>{@code "**&#47;*.xml"} - all XML files in the directory tree</li>
     *   <li>{@code "test?.txt"} - files like test1.txt, testA.txt</li>
     *   <li>{@code "*.[ch]"} - all .c and .h files</li>
     * </ul>
     *
     * @param pattern the glob pattern to match
     * @return this Find instance for method chaining
     * @throws IllegalArgumentException if pattern is null
     */
    public Find matching(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }
        this.pattern = pattern;
        return this;
    }

    /**
     * Adds a filter predicate to further refine the search results.
     *
     * <p>The filter is applied after pattern matching, allowing for complex
     * selection criteria based on file attributes, size, permissions, etc.
     * Multiple filters can be added and will be combined with AND logic.</p>
     *
     * <p>Common filter examples:</p>
     * <pre>
     * // Filter by size
     * .filter(path -&gt; Files.size(path) &gt; 1024 * 1024)  // larger than 1MB
     *
     * // Filter by modification time
     * .filter(path -&gt; {
     *     FileTime time = Files.getLastModifiedTime(path);
     *     return time.toInstant().isAfter(Instant.now().minus(7, ChronoUnit.DAYS));
     * })
     *
     * // Filter by file permissions
     * .filter(path -&gt; Files.isReadable(path) && Files.isWritable(path))
     * </pre>
     *
     * @param filter the predicate to test each matching path
     * @return this Find instance for method chaining
     * @throws IllegalArgumentException if filter is null
     */
    public Find filter(Predicate<Path> filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }
        this.filters.add(filter);
        return this;
    }

    /**
     * Excludes paths containing the specified text in a cross-platform manner.
     *
     * <p>This method handles platform-specific path separators automatically,
     * so you don't need to worry about differences between Windows (\) and
     * Unix (/) path separators.</p>
     *
     * <p>Examples:</p>
     * <pre>
     * // Exclude all files in 'target' directories
     * .excluding("target")
     *
     * // Exclude test files
     * .excluding("test")
     *
     * // Can be chained for multiple exclusions
     * .excluding("target")
     * .excluding("build")
     * .excluding(".git")
     * </pre>
     *
     * @param text the text to exclude from paths (case-sensitive)
     * @return this Find instance for method chaining
     * @throws IllegalArgumentException if text is null
     */
    public Find excluding(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Exclusion text cannot be null");
        }
        return filter(path -> {
            String normalizedPath = normalizePath(path);
            return !normalizedPath.contains(text);
        });
    }

    /**
     * Includes only paths containing the specified text in a cross-platform manner.
     *
     * <p>This method handles platform-specific path separators automatically.</p>
     *
     * <p>Examples:</p>
     * <pre>
     * // Include only files in 'src' directories
     * .including("src")
     *
     * // Include only main source files
     * .including("main")
     * </pre>
     *
     * @param text the text that must be present in paths (case-sensitive)
     * @return this Find instance for method chaining
     * @throws IllegalArgumentException if text is null
     */
    public Find including(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Inclusion text cannot be null");
        }
        return filter(path -> {
            String normalizedPath = normalizePath(path);
            return normalizedPath.contains(text);
        });
    }

    /**
     * Excludes paths that match any of the provided texts in a cross-platform manner.
     *
     * <p>This is a convenience method for excluding multiple patterns at once.</p>
     *
     * <p>Example:</p>
     * <pre>
     * // Exclude common build and VCS directories
     * .excludingAny("target", "build", ".git", "node_modules", ".idea")
     * </pre>
     *
     * @param texts the texts to exclude from paths
     * @return this Find instance for method chaining
     * @throws IllegalArgumentException if texts array is null or contains null elements
     */
    public Find excludingAny(String... texts) {
        if (texts == null) {
            throw new IllegalArgumentException("Exclusion texts cannot be null");
        }
        return filter(path -> {
            String normalizedPath = normalizePath(path);
            for (String text : texts) {
                if (text == null) {
                    throw new IllegalArgumentException("Exclusion text cannot be null");
                }
                if (normalizedPath.contains(text)) {
                    return false;
                }
            }
            return true;
        });
    }

    /**
     * Includes only paths where the specified segment appears as a complete path component.
     *
     * <p>Unlike {@link #including(String)} which does substring matching, this method
     * only matches complete path segments. For example, "test" will match
     * "src/test/java" but not "src/testing/java".</p>
     *
     * @param segment the path segment to match
     * @return this Find instance for method chaining
     * @throws IllegalArgumentException if segment is null
     */
    public Find havingPathSegment(String segment) {
        if (segment == null) {
            throw new IllegalArgumentException("Path segment cannot be null");
        }
        return filter(path -> {
            for (Path part : path) {
                if (part.toString().equals(segment)) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Excludes paths where the specified segment appears as a complete path component.
     *
     * <p>This is the opposite of {@link #havingPathSegment(String)}.</p>
     *
     * @param segment the path segment to exclude
     * @return this Find instance for method chaining
     * @throws IllegalArgumentException if segment is null
     */
    public Find excludingPathSegment(String segment) {
        if (segment == null) {
            throw new IllegalArgumentException("Path segment cannot be null");
        }
        return filter(path -> {
            for (Path part : path) {
                if (part.toString().equals(segment)) {
                    return false;
                }
            }
            return true;
        });
    }

    /**
     * Normalizes a path to use forward slashes regardless of platform.
     *
     * @param path the path to normalize
     * @return the normalized path string
     */
    private String normalizePath(Path path) {
        return path.toString().replace('\\', '/');
    }

    /**
     * Combines all filters into a single predicate.
     *
     * @return a combined predicate or null if no filters were added
     */
    private Predicate<Path> getCombinedFilter() {
        if (filters.isEmpty()) {
            return null;
        }
        return filters.stream()
                .reduce(Predicate::and)
                .orElse(path -> true);
    }

    /**
     * Adds a content search requirement to find files containing specific text.
     *
     * <p>Only regular files are searched for content; directories are excluded
     * from content searching. The search is case-sensitive and looks for exact
     * matches of the specified string within the file.</p>
     *
     * <p>For performance, files are read using different strategies based on size:</p>
     * <ul>
     *   <li>Files &lt; 1MB are read entirely into memory</li>
     *   <li>Files &gt;= 1MB are read line by line to conserve memory</li>
     * </ul>
     *
     * <p>Binary files and files that cannot be read as text are automatically
     * skipped without causing the search to fail.</p>
     *
     * @param content the text to search for within files
     * @return this Find instance for method chaining
     */
    public Find containing(String content) {
        this.content = content;
        return this;
    }

    /**
     * Executes the search and returns the list of matching paths.
     *
     * <p>This terminal operation runs the configured search and returns all paths
     * that match the specified criteria. If no matches are found, an empty list
     * is returned.</p>
     *
     * <p>The search is performed eagerly, meaning all matching files are found
     * and collected before returning. For very large directory trees, this may
     * take some time and consume memory proportional to the number of matches.</p>
     *
     * @return a list of paths matching all specified criteria, or empty list if none found
     * @throws IllegalStateException if no pattern has been specified via {@link #matching(String)}
     * @throws java.io.UncheckedIOException if an I/O error occurs during the search
     */
    public List<Path> execute() {
        if (pattern == null) {
            throw new IllegalStateException("Pattern must be specified using matching() method");
        }

        Predicate<Path> combinedFilter = getCombinedFilter();

        // Determine which finder method to call based on what's configured
        if (content != null && !content.isEmpty()) {
            return finder.find(directory, pattern, combinedFilter, content);
        } else if (combinedFilter != null) {
            return finder.find(directory, pattern, combinedFilter);
        } else {
            return finder.find(directory, pattern);
        }
    }

    /**
     * Convenience method that executes the search and returns the first matching path.
     *
     * <p>This is useful when you expect only one match or only care about the first
     * match found. The search may terminate early once a match is found, providing
     * better performance than {@link #execute()} when only one result is needed.</p>
     *
     * @return the first matching path, or null if no matches found
     * @throws IllegalStateException if no pattern has been specified
     * @throws java.io.UncheckedIOException if an I/O error occurs during the search
     */
    public Path executeFirst() {
        List<Path> results = execute();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Convenience method that executes the search and returns the count of matches.
     *
     * <p>This is useful when you only need to know how many files match the criteria
     * without needing the actual paths.</p>
     *
     * @return the number of paths matching all specified criteria
     * @throws IllegalStateException if no pattern has been specified
     * @throws java.io.UncheckedIOException if an I/O error occurs during the search
     */
    public long count() {
        return execute().size();
    }

    /**
     * Convenience method that executes the search and checks if any matches exist.
     *
     * <p>This is more efficient than {@code count() > 0} as it can potentially
     * terminate the search as soon as the first match is found.</p>
     *
     * @return true if at least one path matches all criteria, false otherwise
     * @throws IllegalStateException if no pattern has been specified
     * @throws java.io.UncheckedIOException if an I/O error occurs during the search
     */
    public boolean exists() {
        return !execute().isEmpty();
    }
}
