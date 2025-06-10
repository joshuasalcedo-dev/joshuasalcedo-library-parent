package io.joshuasalcedo.library.script;

import io.joshuasalcedo.library.io.core.find.Find;
import io.joshuasalcedo.library.io.core.text.format.TextFormat;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RunMain {
    
    private static final String SCRIPTS_DIR = "scripts";
    private static final String RUN_LOGS_DIR = "run_logs";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    public static void main(String[] args) {
        // Get git root directory
        String gitRoot = getGitRoot();
        if (gitRoot == null) {
            System.err.println(TextFormat.of("ERROR: Not in a git repository!")
                .toUpperCase()
                .surround("❌ ", " ❌")
                .get());
            System.exit(1);
        }
        
        System.out.println(TextFormat.of("Git Root: " + gitRoot)
            .surround("📁 ", "")
            .get());
        
        // Create logs directory
        Path logsDir = Paths.get(gitRoot, SCRIPTS_DIR, RUN_LOGS_DIR);
        try {
            Files.createDirectories(logsDir);
        } catch (IOException e) {
            System.err.println("Failed to create logs directory: " + e.getMessage());
            System.exit(1);
        }
        
        // Find all executable classes
        List<ExecutableClass> executableClasses = findExecutableClassesAdvanced(gitRoot);
        
        if (executableClasses.isEmpty()) {
            System.out.println(TextFormat.of("No executable classes found!")
                .toUpperCase()
                .surround("⚠️  ", " ⚠️")
                .get());
            return;
        }
        
        // Display found classes
        displayExecutableClasses(executableClasses);
        
        // Run all classes and collect statistics
        RunStatistics stats = runAllClasses(executableClasses, logsDir);
        
        // Display statistics
        displayStatistics(stats, logsDir);
    }
    
    private static String getGitRoot() {
        try {
            Process process = new ProcessBuilder("git", "rev-parse", "--show-toplevel")
                .redirectErrorStream(true)
                .start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0) {
                    return line.trim();
                }
            }
        } catch (Exception e) {
            // Git command failed
        }
        return null;
    }
    
    private static List<ExecutableClass> findExecutableClassesAdvanced(String startPath) {
        List<ExecutableClass> executableClasses = new ArrayList<>();
        
        System.out.println(TextFormat.of("Searching for executable classes...")
            .surround("🔍 ", "")
            .get());
        
        // Find Java files containing "public static void main"
        List<Path> candidateFiles = Find.in(startPath)
            .matching("*.java")
            .excluding("test")
                .excluding("script")
            .containing("public static void main")
            .execute();
        
        System.out.println(TextFormat.of("Found " + candidateFiles.size() + " candidate files")
            .prepend("→ ")
            .get());
        
        // Get current class name to exclude it
        String currentClassName = RunMain.class.getName();
        
        // Further filter to ensure it's actually a main method
        for (Path javaFile : candidateFiles) {
            if (hasMainMethod(javaFile)) {
                ExecutableClass ec = parseExecutableClass(javaFile);
                if (ec != null && !ec.fullyQualifiedName.equals(currentClassName)) {
                    executableClasses.add(ec);
                }
            }
        }
        
        // Sort by fully qualified name for consistent ordering
        executableClasses.sort(Comparator.comparing(ec -> ec.fullyQualifiedName));
        
        return executableClasses;
    }
    
    private static boolean hasMainMethod(Path javaFile) {
        try {
            String content = Files.readString(javaFile);
            Pattern mainPattern = Pattern.compile(
                "public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*(\\[\\]|\\.\\.\\.)\\s+\\w+\\s*\\)",
                Pattern.MULTILINE
            );
            return mainPattern.matcher(content).find();
        } catch (IOException e) {
            return false;
        }
    }
    
    private static ExecutableClass parseExecutableClass(Path javaFile) {
        try {
            String content = Files.readString(javaFile);
            
            // Extract package name
            String packageName = "";
            Pattern packagePattern = Pattern.compile("package\\s+([\\w\\.]+)\\s*;");
            Matcher packageMatcher = packagePattern.matcher(content);
            if (packageMatcher.find()) {
                packageName = packageMatcher.group(1);
            }
            
            // Extract class name
            String fileName = javaFile.getFileName().toString().replace(".java", "");
            String className = fileName;
            
            Pattern classPattern = Pattern.compile("public\\s+class\\s+(\\w+)");
            Matcher classMatcher = classPattern.matcher(content);
            if (classMatcher.find()) {
                className = classMatcher.group(1);
            }
            
            // Build fully qualified name
            String fullyQualifiedName = packageName.isEmpty() ? className : packageName + "." + className;
            
            return new ExecutableClass(
                className,
                packageName,
                fullyQualifiedName,
                javaFile
            );
            
        } catch (IOException e) {
            return null;
        }
    }
    
    private static void displayExecutableClasses(List<ExecutableClass> classes) {
        System.out.println("\n" + TextFormat.of("EXECUTABLE CLASSES TO RUN")
            .toUpperCase()
            .surround("═══ ", " ═══")
            .get());
        
        for (int i = 0; i < classes.size(); i++) {
            ExecutableClass ec = classes.get(i);
            System.out.println(TextFormat.of((i + 1) + ". " + ec.fullyQualifiedName)
                .prepend("  ")
                .get());
        }
        
        System.out.println(TextFormat.of("─").repeat(60).get() + "\n");
    }
    
    private static RunStatistics runAllClasses(List<ExecutableClass> classes, Path logsDir) {
        RunStatistics stats = new RunStatistics();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        System.out.println(TextFormat.of("STARTING BATCH EXECUTION")
            .toUpperCase()
            .surround("🚀 ", " 🚀")
            .get());
        
        for (ExecutableClass ec : classes) {
            stats.totalRuns++;
            System.out.println("\n" + TextFormat.of("Running: " + ec.className)
                .surround("▶️  ", "")
                .get());
            
            // Create log file name in snake_case
            String logFileName = TextFormat.of(ec.className)
                .toSnakeCase()
                .append("_" + timestamp + ".log")
                .get();
            
            Path logFile = logsDir.resolve(logFileName);
            
            boolean success = runClassWithLogging(ec, logFile);
            
            if (success) {
                stats.successfulRuns++;
                System.out.println(TextFormat.of("✅ SUCCESS")
                    .prepend("   ")
                    .get());
            } else {
                stats.failedClasses.add(ec.fullyQualifiedName);
                System.out.println(TextFormat.of("❌ FAILED")
                    .prepend("   ")
                    .get());
            }
            
            System.out.println(TextFormat.of("Log: " + logFile.toString())
                .prepend("   📄 ")
                .get());
        }
        
        return stats;
    }
    
    private static boolean runClassWithLogging(ExecutableClass ec, Path logFile) {
        try (PrintWriter logWriter = new PrintWriter(new FileWriter(logFile.toFile()))) {
            // Write header to log
            logWriter.println(TextFormat.of("EXECUTION LOG")
                .toUpperCase()
                .surround("=== ", " ===")
                .get());
            logWriter.println("Class: " + ec.fullyQualifiedName);
            logWriter.println("Start Time: " + LocalDateTime.now());
            logWriter.println(TextFormat.of("─").repeat(60).get());
            logWriter.println();
            
            // Determine classpath
            String classpath = determineClasspath();
            
            // Build command
            List<String> command = Arrays.asList(
                "java",
                "-cp",
                classpath,
                ec.fullyQualifiedName
            );
            
            // Create process
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            
            // Create threads to capture output
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logWriter.println("[OUT] " + line);
                        logWriter.flush();
                    }
                } catch (IOException e) {
                    logWriter.println("[ERROR] Failed to read output: " + e.getMessage());
                }
            });
            
            Thread errorThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logWriter.println("[ERR] " + line);
                        logWriter.flush();
                    }
                } catch (IOException e) {
                    logWriter.println("[ERROR] Failed to read error stream: " + e.getMessage());
                }
            });
            
            // Start threads
            outputThread.start();
            errorThread.start();
            
            // Wait for process with timeout
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);
            
            if (!completed) {
                process.destroyForcibly();
                logWriter.println("\n[TIMEOUT] Process killed after 30 seconds");
                return false;
            }
            
            // Wait for output threads
            outputThread.join(1000);
            errorThread.join(1000);
            
            int exitCode = process.exitValue();
            logWriter.println("\n" + TextFormat.of("─").repeat(60).get());
            logWriter.println("End Time: " + LocalDateTime.now());
            logWriter.println("Exit Code: " + exitCode);
            
            return exitCode == 0;
            
        } catch (Exception e) {
            // Try to write error to log file
            try (PrintWriter errorWriter = new PrintWriter(new FileWriter(logFile.toFile(), true))) {
                errorWriter.println("\n[EXCEPTION] " + e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace(errorWriter);
            } catch (IOException ioe) {
                // Ignore
            }
            return false;
        }
    }
    
    private static String determineClasspath() {
        List<String> classpathEntries = new ArrayList<>();
        
        // Common build output directories
        String[] possibleDirs = {
            "target/classes",
            "build/classes/java/main",
            "out/production",
            "bin",
            "classes"
        };
        
        for (String dir : possibleDirs) {
            File f = new File(dir);
            if (f.exists() && f.isDirectory()) {
                classpathEntries.add(f.getAbsolutePath());
            }
        }
        
        // Add current classpath
        String currentClasspath = System.getProperty("java.class.path");
        if (currentClasspath != null && !currentClasspath.isEmpty()) {
            classpathEntries.add(currentClasspath);
        }
        
        return String.join(File.pathSeparator, classpathEntries);
    }
    
    private static void displayStatistics(RunStatistics stats, Path logsDir) {
        System.out.println("\n" + TextFormat.of("EXECUTION SUMMARY")
            .toUpperCase()
            .surround("═══ ", " ═══")
            .get());
        
        String successRate = String.format("%.1f%%", 
            stats.totalRuns > 0 ? (stats.successfulRuns * 100.0 / stats.totalRuns) : 0);
        
        System.out.println(TextFormat.of("Total Runs: " + stats.totalRuns)
            .prepend("📊 ")
            .get());
        
        System.out.println(TextFormat.of("Successful: " + stats.successfulRuns + "/" + stats.totalRuns + " (" + successRate + ")")
            .prepend("✅ ")
            .get());
        
        System.out.println(TextFormat.of("Failed: " + stats.failedClasses.size())
            .prepend("❌ ")
            .get());
        
        if (!stats.failedClasses.isEmpty()) {
            System.out.println("\n" + TextFormat.of("Failed Classes:")
                .toUpperCase()
                .prepend("⚠️  ")
                .get());
            for (String failedClass : stats.failedClasses) {
                System.out.println(TextFormat.of(failedClass)
                    .prepend("   • ")
                    .get());
            }
        }
        
        System.out.println("\n" + TextFormat.of("Log Directory:")
            .toUpperCase()
            .prepend("📁 ")
            .get());
        
        System.out.println(TextFormat.of(logsDir.toAbsolutePath().toString())
            .surround("   ", "")
            .get());
        
        // Make the path clickable in terminals that support it
        System.out.println("\n" + TextFormat.of("file://" + logsDir.toAbsolutePath().toString())
            .prepend("   🔗 ")
            .get());
    }
    
    // Helper classes
    static class ExecutableClass {
        final String className;
        final String packageName;
        final String fullyQualifiedName;
        final Path path;
        
        ExecutableClass(String className, String packageName, String fullyQualifiedName, Path path) {
            this.className = className;
            this.packageName = packageName;
            this.fullyQualifiedName = fullyQualifiedName;
            this.path = path;
        }
    }
    
    static class RunStatistics {
        int totalRuns = 0;
        int successfulRuns = 0;
        List<String> failedClasses = new ArrayList<>();
    }
}