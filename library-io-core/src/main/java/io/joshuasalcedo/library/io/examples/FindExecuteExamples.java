package io.joshuasalcedo.library.io.examples;

import io.joshuasalcedo.library.io.core.find.Find;
import io.joshuasalcedo.library.io.core.execute.Execute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Examples demonstrating the combined usage of Find and Execute APIs.
 */
public class FindExecuteExamples {

    public static void main(String[] args) throws IOException {
        // Example 1: Find and copy Java files to backup
        example1_findAndCopy();

        // Example 2: Find and delete old log files
        example2_findAndDeleteOldLogs();

        // Example 3: Find, backup, then delete temporary files
        example3_backupThenDelete();

        // Example 4: Find and zip configuration files
        example4_findAndZip();

        // Example 5: Complex rename operation
        example5_complexRename();

        // Example 6: Dry run mode
        example6_dryRunMode();

        // Example 7: Custom operations
        example7_customOperations();
    }

    /**
     * Example 1: Find all Java files and copy them to a backup directory
     */
    private static void example1_findAndCopy() throws IOException {
        System.out.println("=== Example 1: Find and Copy ===");

        // Use relative paths or check if directory exists first
        Path sourceDir = Path.of("src/main/java");
        if (!Files.exists(sourceDir)) {
            System.out.println("Source directory not found: " + sourceDir);
            System.out.println("Using current directory instead for demo");
            sourceDir = Path.of(".");
        }

        List<Path> javaFiles = Find.in(sourceDir)
                .matching("*.java")
                .excluding("test")
                .execute();

        if (javaFiles.isEmpty()) {
            System.out.println("No Java files found in " + sourceDir);
            return;
        }

        List<Path> copied = Execute.on(javaFiles)
                .verbose()
                .copyTo("backup/java-sources")
                .execute();

        System.out.println("Copied " + copied.size() + " files");
    }

    /**
     * Example 2: Find and delete log files older than 30 days
     */
    private static void example2_findAndDeleteOldLogs() throws IOException {
        System.out.println("\n=== Example 2: Delete Old Logs ===");

        // Use a directory that might exist or create a demo
        Path logDir = Path.of("logs");
        if (!Files.exists(logDir)) {
            System.out.println("Log directory not found, skipping example");
            return;
        }

        List<Path> oldLogs = Find.in(logDir)
                .matching("*.log")
                .filter(path -> {
                    try {
                        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                        FileTime lastModified = attrs.lastModifiedTime();
                        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
                        return lastModified.toInstant().isBefore(thirtyDaysAgo);
                    } catch (IOException e) {
                        return false;
                    }
                })
                .execute();

        if (oldLogs.isEmpty()) {
            System.out.println("No old log files found");
            return;
        }

        Execute.on(oldLogs)
                .withConfirmation("Delete %d old log files?")
                .verbose()
                .delete()
                .execute();
    }

    /**
     * Example 3: Backup then delete temporary files
     */
    private static void example3_backupThenDelete() throws IOException {
        System.out.println("\n=== Example 3: Backup Then Delete ===");

        Path tempDir = Path.of("temp");
        if (!Files.exists(tempDir)) {
            // Create a demo temp directory with some files
            Files.createDirectories(tempDir);
            Files.writeString(tempDir.resolve("demo1.tmp"), "Demo file 1");
            Files.writeString(tempDir.resolve("demo2.tmp"), "Demo file 2");
            System.out.println("Created demo temp directory with sample files");
        }

        List<Path> tempFiles = Find.in(tempDir)
                .matching("*.tmp")
                .execute();

        if (tempFiles.isEmpty()) {
            System.out.println("No temp files found");
            return;
        }

        Execute.on(tempFiles)
                .verbose()
                .backup("archive/temp-backup")
                .then()
                .delete()
                .execute();
    }

    /**
     * Example 4: Find configuration files and create a zip archive
     */
    private static void example4_findAndZip() throws IOException {
        System.out.println("\n=== Example 4: Zip Configuration Files ===");

        // Look for config files in current directory or create demo
        Path configDir = Path.of("config");
        if (!Files.exists(configDir)) {
            Files.createDirectories(configDir);
            Files.writeString(configDir.resolve("app.conf"), "demo.enabled=true");
            Files.writeString(configDir.resolve("db.conf"), "db.url=localhost");
            System.out.println("Created demo config directory");
        }

        List<Path> configFiles = Find.in(configDir)
                .matching("*.conf")
                .execute();

        if (configFiles.isEmpty()) {
            System.out.println("No config files found");
            return;
        }

        Execute.on(configFiles)
                .verbose()
                .zipTo("backup/configs-" + System.currentTimeMillis() + ".zip")
                .execute();
    }

    /**
     * Example 5: Complex rename - add prefix and change extension
     */
    private static void example5_complexRename() throws IOException {
        System.out.println("\n=== Example 5: Complex Rename ===");

        Path docsDir = Path.of("documents");
        if (!Files.exists(docsDir)) {
            Files.createDirectories(docsDir);
            Files.writeString(docsDir.resolve("report.txt"), "Sample report");
            Files.writeString(docsDir.resolve("notes.txt"), "Sample notes");
            System.out.println("Created demo documents directory");
        }

        List<Path> textFiles = Find.in(docsDir)
                .matching("*.txt")
                .execute();

        if (textFiles.isEmpty()) {
            System.out.println("No text files found");
            return;
        }

        Execute.on(textFiles)
                .verbose()
                .rename(filename -> {
                    String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));
                    return "archived_" + nameWithoutExt + ".bak";
                })
                .execute();
    }

    /**
     * Example 6: Dry run mode to preview operations
     */
    private static void example6_dryRunMode() throws IOException {
        System.out.println("\n=== Example 6: Dry Run Mode ===");

        Path photosDir = Path.of("photos");
        if (!Files.exists(photosDir)) {
            Files.createDirectories(photosDir);
            // Create some dummy image files for demo
            Files.writeString(photosDir.resolve("vacation1.jpg"), "");
            Files.writeString(photosDir.resolve("vacation2.jpg"), "");
            System.out.println("Created demo photos directory");
        }

        List<Path> images = Find.in(photosDir)
                .matching("*.jpg")
                .execute();

        if (images.isEmpty()) {
            System.out.println("No images found");
            return;
        }

        // First, do a dry run to see what would happen
        System.out.println("DRY RUN - Preview of operations:");
        Execute.on(images)
                .dryRun()
                .moveTo("photos/organized/2024")
                .execute();

        // Then execute for real if satisfied
        // Execute.on(images).moveTo("photos/organized/2024").execute();
    }

    /**
     * Example 7: Custom operations with forEach
     */
    private static void example7_customOperations() throws IOException {
        System.out.println("\n=== Example 7: Custom Operations ===");

        // Use current directory or src if it exists
        Path srcDir = Files.exists(Path.of("src")) ? Path.of("src") : Path.of(".");

        List<Path> sourceFiles = Find.in(srcDir)
                .matching("*.java")
                .containing("@Deprecated")
                .execute();

        if (sourceFiles.isEmpty()) {
            System.out.println("No deprecated Java files found");
            return;
        }

        Execute.on(sourceFiles)
                .verbose()
                .forEach(path -> {
                    // Custom operation: add to a report, update database, etc.
                    System.out.println("Found deprecated code in: " + path);
                    // Could write to a report file, update issue tracker, etc.
                })
                .then()
                .copyTo("review/deprecated-code")
                .execute();
    }

    /**
     * Advanced example: Combining multiple finds with execute
     */
    public static void advancedExample() throws IOException {
        System.out.println("\n=== Advanced Example: Multiple Sources ===");

        // Check for typical Java project structure
        Path mainSrc = Path.of("src/main/java");
        Path testSrc = Path.of("src/test/java");

        List<Path> allJavaFiles = new ArrayList<>();

        if (Files.exists(mainSrc)) {
            List<Path> mainJava = Find.in(mainSrc)
                    .matching("*.java")
                    .execute();
            allJavaFiles.addAll(mainJava);
        }

        if (Files.exists(testSrc)) {
            List<Path> testJava = Find.in(testSrc)
                    .matching("*.java")
                    .execute();
            allJavaFiles.addAll(testJava);
        }

        if (allJavaFiles.isEmpty()) {
            System.out.println("No Java files found in standard Maven/Gradle structure");
            return;
        }

        // Process all Java files
        Execute.on(allJavaFiles)
                .filter(path -> {
                    try {
                        return Files.size(path) > 10_000; // Only large files
                    } catch (IOException e) {
                        // Log error and exclude file if we can't read its size
                        System.err.println("Cannot read size of " + path + ": " + e.getMessage());
                        return false;
                    }
                })
                .verbose()
                .withLogger(msg -> System.err.println("[PROCESS] " + msg))
                .zipTo("backup/large-java-files.zip")
                .execute();
    }

    /**
     * One-liner examples showing the power of combining Find and Execute
     */
    public static void oneLinerExamples() throws IOException {
        System.out.println("\n=== One-liner Examples ===");

        // These are examples of what you can do - adjust paths as needed

        // Delete all .bak files in current directory
        int deletedCount = Execute.on(Find.in(".").matching("*.bak").execute())
                .delete().executeCount();
        System.out.println("Deleted " + deletedCount + " .bak files");

        // Backup and zip all configs (if config directory exists)
        if (Files.exists(Path.of("config"))) {
            Execute.on(Find.in("config").matching("*.conf").execute())
                    .backup("backup").then().zipTo("archive/configs.zip").execute();
        }

        // Move all PDFs to organized folder (if downloads exists)
        if (Files.exists(Path.of("downloads"))) {
            Execute.on(Find.in("downloads").matching("*.pdf").execute())
                    .moveTo("documents/pdfs").execute();
        }
    }
}