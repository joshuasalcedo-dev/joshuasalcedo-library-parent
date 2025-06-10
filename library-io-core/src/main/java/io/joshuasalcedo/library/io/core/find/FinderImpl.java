package io.joshuasalcedo.library.io.core.find;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

/**
 * Implementation of the Finder interface that walks file trees to find matching paths.
 *
 * <p>This implementation uses the Java NIO.2 file tree walking API for efficient
 * traversal and supports glob pattern matching, custom filtering, and content searching.</p>
 *
 * @author Joshua Salcedo
 */
class FinderImpl implements Finder {
    private static final Logger logger = LoggerFactory.getLogger(FinderImpl.class);

    // Constants for performance tuning
    private static final int SMALL_FILE_THRESHOLD = 1024 * 1024; // 1MB
    private static final int MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB max for content search

    @Override
    public List<Path> find(Path directory, String pattern) {
        return find(directory, pattern, null, null);
    }

    @Override
    public List<Path> find(Path directory, String pattern, Predicate<Path> filter) {
        return find(directory, pattern, filter, null);
    }

    @Override
    public List<Path> find(Path directory, String pattern, Predicate<Path> filter, String content) {
        validateInputs(directory, pattern);

        FinderVisitor finder = new FinderVisitor(pattern, filter, content);
        try {
            Files.walkFileTree(directory, finder);
            List<Path> results = finder.getMatches();

            if (results.isEmpty()) {
                logger.debug("No files found matching pattern '{}' in directory '{}'", pattern, directory);
            } else {
                logger.debug("Found {} files matching pattern '{}' in directory '{}'",
                        results.size(), pattern, directory);
            }

            return results;
        } catch (IOException e) {
            logger.error("Error finding files in directory '{}' with pattern '{}': {}",
                    directory, pattern, e.getMessage(), e);
            throw new UncheckedIOException("Failed to search directory: " + directory, e);
        }
    }

    /**
     * Validates input parameters.
     */
    private void validateInputs(Path directory, String pattern) {
        if (directory == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }
        if (!Files.exists(directory)) {
            throw new IllegalArgumentException("Directory does not exist: " + directory);
        }
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Path is not a directory: " + directory);
        }
    }

    /**
     * A FileVisitor implementation that searches for files matching a glob pattern,
     * optional filter predicate, and optional content search string.
     *
     * <p>This visitor walks through a file tree and collects paths that match
     * the specified criteria. It supports:
     * <ul>
     *   <li>Glob pattern matching (e.g., "*.java", "test*.xml")</li>
     *   <li>Custom path filtering via predicates</li>
     *   <li>Content searching within files</li>
     * </ul>
     */
    private static class FinderVisitor extends SimpleFileVisitor<Path> {
        private final PathMatcher matcher;
        private final List<Path> matches = Collections.synchronizedList(new ArrayList<>());
        private final Predicate<Path> filter;
        private final String searchContent;
        private final boolean contentSearch;
        private final String pattern;
        private final AtomicBoolean hasAccessErrors = new AtomicBoolean(false);

        FinderVisitor(String pattern, Predicate<Path> filter, String content) {
            this.pattern = pattern;
            this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
            this.filter = filter != null ? filter : path -> true;
            this.searchContent = content;
            this.contentSearch = content != null && !content.isEmpty();
        }

        /**
         * Returns an unmodifiable view of the matches.
         */
        List<Path> getMatches() {
            return Collections.unmodifiableList(new ArrayList<>(matches));
        }

        /**
         * Evaluates whether a path should be added to the matches list.
         */
        void find(Path file) {
            boolean isMatch = false;

            try {
                if (pattern.contains("**") || pattern.contains("/") || pattern.contains("\\")) {
                    // For patterns with path separators or recursive wildcards, match against full path
                    isMatch = matcher.matches(file);
                } else {
                    // For simple patterns, match against filename only
                    Path name = file.getFileName();
                    isMatch = name != null && matcher.matches(name);
                }

                if (isMatch && filter.test(file)) {
                    // If no content search needed, add immediately
                    if (!contentSearch) {
                        matches.add(file);
                        return;
                    }

                    // For content search, only check regular files
                    if (Files.isRegularFile(file) && shouldSearchContent(file) && containsContent(file)) {
                        matches.add(file);
                    }
                }
            } catch (Exception e) {
                logger.warn("Error processing file '{}': {}", file, e.getMessage());
                // Continue processing other files
            }
        }

        /**
         * Determines if a file should be searched for content based on its characteristics.
         */
        private boolean shouldSearchContent(Path file) {
            try {
                long size = Files.size(file);

                // Skip very large files
                if (size > MAX_FILE_SIZE) {
                    logger.debug("Skipping content search for large file ({}MB): {}",
                            size / (1024 * 1024), file);
                    return false;
                }

                // Skip files that are likely binary based on extension
                String fileName = file.getFileName().toString().toLowerCase();
                if (isBinaryFileExtension(fileName)) {
                    logger.trace("Skipping binary file: {}", file);
                    return false;
                }

                return true;
            } catch (IOException e) {
                logger.trace("Cannot determine file size for '{}': {}", file, e.getMessage());
                return false;
            }
        }

        /**
         * Checks if a filename has a known binary extension.
         */
        private boolean isBinaryFileExtension(String fileName) {
            return fileName.endsWith(".class") || fileName.endsWith(".jar") ||
                    fileName.endsWith(".zip") || fileName.endsWith(".gz") ||
                    fileName.endsWith(".tar") || fileName.endsWith(".bin") ||
                    fileName.endsWith(".exe") || fileName.endsWith(".dll") ||
                    fileName.endsWith(".so") || fileName.endsWith(".dylib") ||
                    fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                    fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                    fileName.endsWith(".pdf") || fileName.endsWith(".doc") ||
                    fileName.endsWith(".docx") || fileName.endsWith(".xls") ||
                    fileName.endsWith(".xlsx");
        }

        /**
         * Checks if a file contains the search content string.
         */
        private boolean containsContent(Path file) {
            try {
                long fileSize = Files.size(file);

                if (fileSize == 0) {
                    return false;
                }


                if (fileSize < SMALL_FILE_THRESHOLD) {
                    // For small files, read entire file
                    try {
                        String content = Files.readString(file, StandardCharsets.UTF_8);
                        return content.contains(searchContent);
                    } catch (CharacterCodingException e) {
                        // Try with ISO-8859-1 for files with different encoding
                        try {
                            String content = Files.readString(file, StandardCharsets.ISO_8859_1);
                            return content.contains(searchContent);
                        } catch (IOException ex) {
                            logger.trace("Cannot read file '{}' as text", file);
                            return false;
                        }
                    }
                } else {
                    // For larger files, use streaming approach
                    try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                        return reader.lines()
                                .anyMatch(line -> line.contains(searchContent));
                    } catch (CharacterCodingException e) {
                        // Try with ISO-8859-1
                        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.ISO_8859_1)) {
                            return reader.lines()
                                    .anyMatch(line -> line.contains(searchContent));
                        } catch (IOException ex) {
                            logger.trace("Cannot read file '{}' as text", file);
                            return false;
                        }
                    }
                }
            } catch (IOException | UncheckedIOException e) {
                // Log at trace level to avoid spam for binary/inaccessible files
                logger.trace("Cannot search content in file '{}': {}", file, e.getMessage());
                return false;
            }
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            find(file);
            return CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            // Check if directory should be skipped
            String dirName = dir.getFileName() != null ? dir.getFileName().toString() : "";

            // Skip hidden directories (except the root)
            if (dirName.startsWith(".") && !dirName.equals(".")) {
                logger.trace("Skipping hidden directory: {}", dir);
                return SKIP_SUBTREE;
            }

            // Skip common build/dependency directories for better performance
            if (dirName.equals("node_modules") || dirName.equals(".git") ||
                    dirName.equals("target") || dirName.equals("build") ||
                    dirName.equals("dist") || dirName.equals(".idea") ||
                    dirName.equals(".vscode") || dirName.equals("__pycache__")) {
                logger.trace("Skipping known build/cache directory: {}", dir);
                return SKIP_SUBTREE;
            }

            // Check directory against pattern if not doing content search
            if (!contentSearch) {
                find(dir);
            }

            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            if (exc instanceof AccessDeniedException) {
                if (!hasAccessErrors.getAndSet(true)) {
                    logger.warn("Access denied to some files/directories. First occurrence: {}", file);
                }
                logger.trace("Access denied: {}", file);
            } else {
                logger.warn("Failed to visit file '{}': {}", file, exc.getMessage());
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            if (exc != null) {
                logger.warn("Error after visiting directory '{}': {}", dir, exc.getMessage());
            }
            return CONTINUE;
        }
    }
}
