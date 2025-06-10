package io.joshuasalcedo.library.io.examples;

import io.joshuasalcedo.library.io.core.find.Find;
import io.joshuasalcedo.library.io.core.execute.Execute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Cleanup utility to remove all demo files and directories created by the examples.
 * This demonstrates the power of combining Find and Execute for cleanup operations.
 */
public class CleanupExample {
    
    // List of directories that might have been created by the examples
    private static final List<String> DEMO_DIRECTORIES = Arrays.asList(
        "backup",
        "archive", 
        "temp",
        "config",
        "documents",
        "photos",
        "review",
        "logs"
    );
    
    // List of file patterns that might have been created
    private static final List<String> DEMO_PATTERNS = Arrays.asList(
        "*.tmp",
        "*.bak",
        "demo*.txt",
        "archived_*.bak"
    );
    
    public static void main(String[] args) throws IOException {
        System.out.println("=== Cleanup Demo Files ===");
        System.out.println("This will remove all demo files and directories created by the examples.");
        System.out.println();
        
        // Method 1: Targeted cleanup of known demo directories
        targetedCleanup();
        
        // Method 2: Pattern-based cleanup (commented out for safety)
        // patternBasedCleanup();
        
        // Method 3: Nuclear option - clean everything (commented out for safety)
        // nuclearCleanup();
        
        System.out.println("\nCleanup complete!");
    }
    
    /**
     * Method 1: Targeted cleanup - removes only known demo directories
     */
    private static void targetedCleanup() throws IOException {
        System.out.println("Method 1: Targeted cleanup of demo directories");
        
        List<Path> dirsToDelete = new ArrayList<>();
        
        // Find all demo directories that exist
        for (String dirName : DEMO_DIRECTORIES) {
            Path dir = Path.of(dirName);
            if (Files.exists(dir)) {
                dirsToDelete.add(dir);
            }
        }
        
        if (dirsToDelete.isEmpty()) {
            System.out.println("No demo directories found to clean up.");
            return;
        }
        
        // Show what will be deleted
        System.out.println("\nFound " + dirsToDelete.size() + " directories to delete:");
        dirsToDelete.forEach(dir -> System.out.println("  - " + dir));
        
        // Execute deletion with confirmation
        Execute.on(dirsToDelete)
            .withConfirmation("\nDelete %d demo directories?")
            .verbose()
            .delete()
            .execute();
    }
    
    /**
     * Method 2: Pattern-based cleanup - finds and removes files matching demo patterns
     */
    private static void patternBasedCleanup() throws IOException {
        System.out.println("\nMethod 2: Pattern-based cleanup");
        
        List<Path> allDemoFiles = new ArrayList<>();
        
        // Find all files matching demo patterns
        for (String pattern : DEMO_PATTERNS) {
            List<Path> matchingFiles = Find.in(".")
                .matching(pattern)
                .excluding(".git")  // Never touch git directories
                .excluding("src")   // Don't delete source files
                .execute();
            
            allDemoFiles.addAll(matchingFiles);
        }
        
        if (allDemoFiles.isEmpty()) {
            System.out.println("No demo files found matching patterns.");
            return;
        }
        
        // Remove duplicates (in case of overlapping patterns)
        List<Path> uniqueFiles = allDemoFiles.stream()
            .distinct()
            .toList();
        
        System.out.println("\nFound " + uniqueFiles.size() + " files to delete:");
        uniqueFiles.forEach(file -> System.out.println("  - " + file));
        
        // Execute deletion
        Execute.on(uniqueFiles)
            .withConfirmation("\nDelete %d demo files?")
            .verbose()
            .delete()
            .execute();
    }
    
    /**
     * Method 3: Nuclear option - clean ALL generated files (use with extreme caution!)
     */
    private static void nuclearCleanup() throws IOException {
        System.out.println("\n!!! NUCLEAR CLEANUP - USE WITH EXTREME CAUTION !!!");
        
        // Find all backup/archive related files and directories
        List<Path> allGeneratedContent = new ArrayList<>();
        
        // Find backup directories
        allGeneratedContent.addAll(
            Find.in(".")
                .matching("backup*")
                .execute()
        );
        
        // Find archive directories  
        allGeneratedContent.addAll(
            Find.in(".")
                .matching("archive*")
                .execute()
        );
        
        // Find all zip files in backup locations
        allGeneratedContent.addAll(
            Find.in(".")
                .matching("*.zip")
                .including("backup")
                .execute()
        );
        
        // Find all .bak files
        allGeneratedContent.addAll(
            Find.in(".")
                .matching("*.bak")
                .execute()
        );
        
        if (allGeneratedContent.isEmpty()) {
            System.out.println("No generated content found.");
            return;
        }
        
        // Remove duplicates and sort
        List<Path> uniqueContent = allGeneratedContent.stream()
            .distinct()
            .sorted()
            .toList();
        
        System.out.println("\nFound " + uniqueContent.size() + " items to delete:");
        uniqueContent.forEach(item -> System.out.println("  - " + item));
        
        // Triple confirmation for nuclear option
        Execute.on(uniqueContent)
            .withConfirmation("\n⚠️  WARNING: Delete %d files/directories? This cannot be undone!")
            .verbose()
            .dryRun()  // Always do a dry run first for nuclear option
            .execute();
        
        System.out.println("\nThat was a DRY RUN. To actually delete, comment out .dryRun() above.");
    }
    
    /**
     * Utility method to clean up after a specific example
     */
    public static void cleanupAfterExample(int exampleNumber) throws IOException {
        System.out.println("\nCleaning up after Example " + exampleNumber);
        
        switch (exampleNumber) {
            case 1 -> {
                // Clean up from Example 1: Find and Copy
                Execute.on(Path.of("backup/java-sources"))
                    .delete()
                    .execute();
            }
            case 3 -> {
                // Clean up from Example 3: Backup then Delete
                Execute.on(Find.in(".").matching("archive/temp-backup*").execute())
                    .delete()
                    .execute();
                Execute.on(Path.of("temp"))
                    .delete()
                    .execute();
            }
            case 4 -> {
                // Clean up from Example 4: Zip configs
                Execute.on(Find.in(".").matching("backup/configs-*.zip").execute())
                    .delete()
                    .execute();
                Execute.on(Path.of("config"))
                    .delete()
                    .execute();
            }
            case 5 -> {
                // Clean up from Example 5: Complex rename
                Execute.on(Find.in("documents").matching("archived_*.bak").execute())
                    .delete()
                    .execute();
            }
            case 6 -> {
                // Clean up from Example 6: Dry run mode
                Execute.on(Path.of("photos"))
                    .delete()
                    .execute();
            }
            default -> System.out.println("No specific cleanup for example " + exampleNumber);
        }
    }
    
    /**
     * Safe cleanup - only deletes empty directories
     */
    public static void safeCleanup() throws IOException {
        System.out.println("\nSafe Cleanup - Removing only empty directories");
        
        List<Path> emptyDirs = new ArrayList<>();
        
        for (String dirName : DEMO_DIRECTORIES) {
            Path dir = Path.of(dirName);
            if (Files.exists(dir) && Files.isDirectory(dir)) {
                try (var stream = Files.list(dir)) {
                    if (stream.findAny().isEmpty()) {
                        emptyDirs.add(dir);
                    }
                }
            }
        }
        
        if (emptyDirs.isEmpty()) {
            System.out.println("No empty directories to remove.");
            return;
        }
        
        Execute.on(emptyDirs)
            .verbose()
            .delete()
            .execute();
    }
    
    /**
     * Interactive cleanup - lets user choose what to delete
     */
    public static void interactiveCleanup() throws IOException {
        System.out.println("\n=== Interactive Cleanup ===");
        System.out.println("Choose cleanup method:");
        System.out.println("1. Delete demo directories only");
        System.out.println("2. Delete files by pattern");
        System.out.println("3. Delete specific example outputs");
        System.out.println("4. Safe cleanup (empty dirs only)");
        System.out.println("5. Exit");
        
        // In a real application, you'd read user input here
        // For demo purposes, we'll just show the menu
        System.out.println("\n(In a real app, you'd select an option here)");
    }
}