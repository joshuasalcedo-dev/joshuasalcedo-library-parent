package io.joshuasalcedo.library.io.core.execute;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A fluent API utility class for executing operations on file paths.
 *
 * <p>This class provides a convenient, chainable interface for performing bulk
 * operations on collections of paths, typically obtained from the Find API.
 * Operations include copying, moving, deleting, renaming, zipping, and more.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // Find and copy all Java files to a backup directory
 * List&lt;Path&gt; copied = Execute.on(Find.in("/src").matching("*.java").execute())
 *     .copyTo("/backup/java-files")
 *     .execute();
 *
 * // Find and delete old log files, with confirmation
 * Execute.on(Find.in("/logs").matching("*.log").execute())
 *     .filter(path -&gt; isOlderThan(path, 30))
 *     .withConfirmation("Delete %d old log files?")
 *     .delete()
 *     .execute();
 *
 * // Find and zip all config files
 * Execute.on(Find.in("/config").matching("*.conf").execute())
 *     .zipTo("/backup/configs.zip")
 *     .execute();
 *
 * // Chain operations: backup then delete
 * Execute.on(Find.in("/temp").matching("*.tmp").execute())
 *     .backup("/archive")
 *     .then()
 *     .delete()
 *     .execute();
 * </pre>
 *
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class Execute {
    
    private final List<Path> paths;
    private final List<Operation> operations = new ArrayList<>();
    private boolean dryRun = false;
    private boolean verbose = false;
    private String confirmationMessage = null;
    private Consumer<String> logger = System.out::println;
    
    /**
     * Represents a file operation to be executed.
     */
    @FunctionalInterface
    private interface Operation {
        List<Path> execute(List<Path> paths) throws IOException;
    }
    
    /**
     * Private constructor to enforce fluent API usage.
     *
     * @param paths the paths to operate on
     */
    private Execute(List<Path> paths) {
        this.paths = new ArrayList<>(paths);
    }
    
    /**
     * Creates a new Execute builder for operating on the specified paths.
     *
     * @param paths the collection of paths to operate on
     * @return a new Execute builder instance
     * @throws IllegalArgumentException if paths is null
     */
    public static Execute on(List<Path> paths) {
        if (paths == null) {
            throw new IllegalArgumentException("Paths cannot be null");
        }
        return new Execute(paths);
    }
    
    /**
     * Creates a new Execute builder for operating on a single path.
     *
     * @param path the path to operate on
     * @return a new Execute builder instance
     * @throws IllegalArgumentException if path is null
     */
    public static Execute on(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        List<Path> singlePath = new ArrayList<>();
        singlePath.add(path);
        return new Execute(singlePath);
    }
    
    /**
     * Enables dry-run mode where operations are logged but not executed.
     *
     * @return this Execute instance for method chaining
     */
    public Execute dryRun() {
        this.dryRun = true;
        this.verbose = true; // Dry run implies verbose
        return this;
    }
    
    /**
     * Enables verbose logging of operations.
     *
     * @return this Execute instance for method chaining
     */
    public Execute verbose() {
        this.verbose = true;
        return this;
    }
    
    /**
     * Sets a custom logger for operation messages.
     *
     * @param logger the logging consumer
     * @return this Execute instance for method chaining
     */
    public Execute withLogger(Consumer<String> logger) {
        if (logger != null) {
            this.logger = logger;
        }
        return this;
    }
    
    /**
     * Requires user confirmation before executing operations.
     *
     * @param message the confirmation message (use %d for file count)
     * @return this Execute instance for method chaining
     */
    public Execute withConfirmation(String message) {
        this.confirmationMessage = message;
        return this;
    }
    
    /**
     * Filters the paths before applying operations.
     *
     * @param predicate the filter predicate
     * @return this Execute instance for method chaining
     */
    public Execute filter(Predicate<Path> predicate) {
        if (predicate != null) {
            paths.removeIf(predicate.negate());
        }
        return this;
    }
    
    /**
     * Copies all paths to the specified directory.
     *
     * @param targetDir the target directory
     * @return this Execute instance for method chaining
     */
    public Execute copyTo(String targetDir) {
        return copyTo(Paths.get(targetDir));
    }
    
    /**
     * Copies all paths to the specified directory.
     *
     * @param targetDir the target directory
     * @return this Execute instance for method chaining
     */
    public Execute copyTo(Path targetDir) {
        operations.add(paths -> {
            List<Path> results = new ArrayList<>();
            Files.createDirectories(targetDir);
            
            for (Path source : paths) {
                Path target = targetDir.resolve(source.getFileName());
                if (verbose) {
                    log("Copying: " + source + " -> " + target);
                }
                if (!dryRun) {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
                results.add(target);
            }
            return results;
        });
        return this;
    }
    
    /**
     * Moves all paths to the specified directory.
     *
     * @param targetDir the target directory
     * @return this Execute instance for method chaining
     */
    public Execute moveTo(String targetDir) {
        return moveTo(Paths.get(targetDir));
    }
    
    /**
     * Moves all paths to the specified directory.
     *
     * @param targetDir the target directory
     * @return this Execute instance for method chaining
     */
    public Execute moveTo(Path targetDir) {
        operations.add(paths -> {
            List<Path> results = new ArrayList<>();
            Files.createDirectories(targetDir);
            
            for (Path source : paths) {
                Path target = targetDir.resolve(source.getFileName());
                if (verbose) {
                    log("Moving: " + source + " -> " + target);
                }
                if (!dryRun) {
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
                results.add(target);
            }
            return results;
        });
        return this;
    }
    
    /**
     * Deletes all paths.
     *
     * @return this Execute instance for method chaining
     */
    public Execute delete() {
        operations.add(paths -> {
            List<Path> deleted = new ArrayList<>();
            
            for (Path path : paths) {
                if (verbose) {
                    log("Deleting: " + path);
                }
                if (!dryRun) {
                    if (Files.isDirectory(path)) {
                        deleteRecursively(path);
                    } else {
                        Files.deleteIfExists(path);
                    }
                }
                deleted.add(path);
            }
            return deleted;
        });
        return this;
    }
    
    /**
     * Renames files using the provided function.
     *
     * @param renamer function that takes old filename and returns new filename
     * @return this Execute instance for method chaining
     */
    public Execute rename(Function<String, String> renamer) {
        operations.add(paths -> {
            List<Path> results = new ArrayList<>();
            
            for (Path source : paths) {
                String oldName = source.getFileName().toString();
                String newName = renamer.apply(oldName);
                Path target = source.getParent().resolve(newName);
                
                if (verbose) {
                    log("Renaming: " + source + " -> " + target);
                }
                if (!dryRun) {
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
                results.add(target);
            }
            return results;
        });
        return this;
    }
    
    /**
     * Creates a backup of all paths with timestamp.
     *
     * @param backupDir the backup directory
     * @return this Execute instance for method chaining
     */
    public Execute backup(String backupDir) {
        return backup(Paths.get(backupDir));
    }
    
    /**
     * Creates a backup of all paths with timestamp.
     *
     * @param backupDir the backup directory
     * @return this Execute instance for method chaining
     */
    public Execute backup(Path backupDir) {
        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        );
        Path timestampedDir = backupDir.resolve("backup_" + timestamp);
        
        operations.add(paths -> {
            List<Path> results = new ArrayList<>();
            Files.createDirectories(timestampedDir);
            
            for (Path source : paths) {
                Path target = timestampedDir.resolve(source.getFileName());
                if (verbose) {
                    log("Backing up: " + source + " -> " + target);
                }
                if (!dryRun) {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
                results.add(target);
            }
            return results;
        });
        return this;
    }
    
    /**
     * Creates a zip archive containing all paths.
     *
     * @param zipFile the path to the zip file to create
     * @return this Execute instance for method chaining
     */
    public Execute zipTo(String zipFile) {
        return zipTo(Paths.get(zipFile));
    }
    
    /**
     * Creates a zip archive containing all paths.
     *
     * @param zipFile the path to the zip file to create
     * @return this Execute instance for method chaining
     */
    public Execute zipTo(Path zipFile) {
        operations.add(paths -> {
            if (verbose) {
                log("Creating zip: " + zipFile);
            }
            
            if (!dryRun) {
                Files.createDirectories(zipFile.getParent());
                
                try (ZipOutputStream zos = new ZipOutputStream(
                        Files.newOutputStream(zipFile))) {
                    
                    for (Path path : paths) {
                        if (Files.isRegularFile(path)) {
                            ZipEntry entry = new ZipEntry(path.getFileName().toString());
                            zos.putNextEntry(entry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                            
                            if (verbose) {
                                log("  Added: " + path);
                            }
                        }
                    }
                }
            }
            
            List<Path> result = new ArrayList<>();
            result.add(zipFile);
            return result;
        });
        return this;
    }
    
    /**
     * Applies a custom operation to each path.
     *
     * @param operation the operation to apply
     * @return this Execute instance for method chaining
     */
    public Execute forEach(Consumer<Path> operation) {
        operations.add(paths -> {
            for (Path path : paths) {
                if (verbose) {
                    log("Processing: " + path);
                }
                if (!dryRun) {
                    operation.accept(path);
                }
            }
            return paths;
        });
        return this;
    }
    
    /**
     * Allows chaining another set of operations on the current paths.
     * This is useful for complex workflows.
     *
     * @return this Execute instance for method chaining
     */
    public Execute then() {
        // This method just returns this for clarity in chaining
        // It serves as a semantic separator between operation groups
        return this;
    }
    
    /**
     * Executes all configured operations on the paths.
     *
     * @return the final list of paths after all operations
     * @throws IOException if an I/O error occurs
     * @throws IllegalStateException if confirmation is required but not provided
     */
    public List<Path> execute() throws IOException {
        if (paths.isEmpty()) {
            if (verbose) {
                log("No paths to process");
            }
            return new ArrayList<>();
        }
        
        if (confirmationMessage != null && !dryRun) {
            String message = String.format(confirmationMessage, paths.size());
            if (!confirm(message)) {
                if (verbose) {
                    log("Operation cancelled by user");
                }
                return new ArrayList<>();
            }
        }
        
        List<Path> currentPaths = new ArrayList<>(paths);
        
        for (Operation operation : operations) {
            currentPaths = operation.execute(currentPaths);
        }
        
        if (verbose) {
            log("Completed " + operations.size() + " operations on " + 
                paths.size() + " paths");
        }
        
        return currentPaths;
    }
    
    /**
     * Executes operations and returns the count of processed paths.
     *
     * @return the number of paths processed
     * @throws IOException if an I/O error occurs
     */
    public int executeCount() throws IOException {
        return execute().size();
    }
    
    /**
     * Helper method to recursively delete a directory.
     */
    private void deleteRecursively(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) 
                    throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    /**
     * Logs a message using the configured logger.
     */
    private void log(String message) {
        logger.accept(message);
    }
    
    /**
     * Simple console confirmation (you may want to inject this for testing).
     */
    private boolean confirm(String message) {
        System.out.print(message + " (y/n): ");
        try {
            int response = System.in.read();
            // Clear the input buffer
            while (System.in.available() > 0) {
                System.in.read();
            }
            return response == 'y' || response == 'Y';
        } catch (IOException e) {
            return false;
        }
    }
}