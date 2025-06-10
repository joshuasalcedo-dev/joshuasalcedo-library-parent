package io.joshuasalcedo.library.io.core.find;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

/**
 * Interface for finding files and directories based on patterns and filters.
 *
 * <p>Implementations of this interface provide functionality to search for files
 * and directories in a file system using various pattern matching strategies
 * such as glob patterns, regular expressions, or custom search algorithms.</p>
 *
 * <p>All search operations are recursive by default, traversing the entire
 * directory tree starting from the specified directory.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * Finder finder = new GlobFinder();
 * Path projectDir = Paths.get("/home/user/project");
 *
 * // Find all Java files
 * List&lt;Path&gt; javaFiles = finder.find(projectDir, "**&#47;*.java");
 *
 * // Find all text files larger than 1MB
 * List&lt;Path&gt; largeTxtFiles = finder.find(projectDir, "**&#47;*.txt",
 *     path -&gt; Files.size(path) &gt; 1024 * 1024);
 * </pre>
 *
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public interface Finder {

    /**
     * Finds all files and directories matching the specified pattern within the given directory
     * and its subdirectories.
     *
     * <p>The pattern syntax depends on the implementation. Common patterns include:</p>
     * <ul>
     *   <li>Glob patterns (e.g., "*.txt", "**&#47;*.java")</li>
     *   <li>Regular expressions</li>
     *   <li>Simple wildcards</li>
     * </ul>
     *
     * @param directory the starting directory for the recursive search
     * @param pattern the pattern to match files and directories against
     * @return a list of paths matching the pattern, or an empty list if no matches found
     * @throws IllegalArgumentException if the directory or pattern is null, or if pattern is invalid
     * @throws java.io.UncheckedIOException if an I/O error occurs during the search
     */
    List<Path> find(Path directory, String pattern);

    /**
     * Finds all files and directories matching the specified pattern and filter predicate
     * within the given directory and its subdirectories.
     *
     * <p>This method first applies the pattern matching, then applies the additional
     * filter predicate to the results. The filter receives the Path object for evaluation.</p>
     *
     * <p>Example filters:</p>
     * <pre>
     * // Find only readable files
     * finder.find(logsDir, "*.log", path -&gt; Files.isReadable(path));
     *
     * // Find files modified today
     * finder.find(srcDir, "**&#47;*", path -&gt; {
     *     FileTime lastModified = Files.getLastModifiedTime(path);
     *     return lastModified.toInstant().isAfter(Instant.now().minus(1, ChronoUnit.DAYS));
     * });
     * </pre>
     *
     * @param directory the starting directory for the recursive search
     * @param pattern the pattern to match files and directories against
     * @param filter additional predicate to filter the results;
     *               receives the Path object
     * @return a list of paths matching both the pattern and filter,
     *         or an empty list if no matches found
     * @throws IllegalArgumentException if the directory, pattern, or filter is null, or if pattern is invalid
     * @throws java.io.UncheckedIOException if an I/O error occurs during the search
     */
    List<Path> find(Path directory, String pattern, Predicate<Path> filter);

    /**
     * Finds all files matching the specified pattern, filter predicate, and containing
     * the specified content within the file.
     *
     * <p>This method performs a content search within files that match the pattern and
     * filter criteria. The search recursively traverses all subdirectories. It's useful
     * for finding files that contain specific text, such as searching for configuration
     * files containing certain parameters or source files containing specific code patterns.</p>
     *
     * <p>The content search is case-sensitive. Directories are excluded from content
     * searching as they don't have searchable content.</p>
     *
     * <p>Example usage:</p>
     * <pre>
     * Path srcDir = Paths.get("src");
     *
     * // Find all Java files containing "TODO" comments
     * List&lt;Path&gt; todoFiles = finder.find(srcDir, "*.java", path -&gt; true, "TODO");
     *
     * // Find configuration files containing database connection strings
     * List&lt;Path&gt; dbConfigs = finder.find(configDir, "*.properties",
     *     path -&gt; path.getFileName().toString().contains("config"),
     *     "jdbc:postgresql");
     * </pre>
     *
     * @param directory the starting directory for the recursive search
     * @param pattern the pattern to match files against
     * @param filter additional predicate to filter the results;
     *               receives the Path object
     * @param content the text content to search for within matching files;
     *                if null or empty, behaves like the two-parameter version
     * @return a list of paths matching the pattern, filter, and containing the specified content,
     *         or an empty list if no matches found
     * @throws IllegalArgumentException if the directory, pattern, or filter is null, or if pattern is invalid
     * @throws java.io.UncheckedIOException if an I/O error occurs during the search or file reading
     * @throws SecurityException if a security manager exists and denies read access to a file
     * @since 1.2.0
     */
    List<Path> find(Path directory, String pattern, Predicate<Path> filter, String content);
}