# Execute Package

The `execute` package provides a fluent API for executing file operations in the file system. It offers a powerful and intuitive way to perform various file operations such as copying, moving, deleting, renaming, backing up, and zipping files.

## Overview

The Execute API is designed to be chainable and easy to use while providing flexibility for complex file operation requirements. It supports operations on multiple files, filtering, dry run mode, verbose logging, and confirmation prompts for critical operations.

## Key Features

- **Fluent, chainable API** for building file operations
- **Support for multiple file operations** in sequence
- **Dry run mode** for testing operations without making changes
- **Verbose logging** for operation tracking
- **Custom filtering** of files
- **Confirmation prompts** for critical operations
- **Error handling and recovery**

## Main Components

### Execute Class

The main class providing a fluent API for building and executing file operations.

## Usage Examples

### Copying Files

```java
// Copy all Java files to a backup directory
Execute.on(Paths.get("src"))
    .filter(path -> path.toString().endsWith(".java"))
    .copyTo(Paths.get("backup"))
    .execute();
```

### Moving Files with Confirmation

```java
// Move log files to archive with confirmation
Execute.on(Paths.get("logs"))
    .filter(path -> path.toString().endsWith(".log"))
    .withConfirmation("Move log files to archive?")
    .moveTo(Paths.get("archive"))
    .execute();
```

### Deleting Files

```java
// Delete temporary files
Execute.on(Paths.get("temp"))
    .filter(path -> {
        try {
            FileTime time = Files.getLastModifiedTime(path);
            return time.toInstant().isBefore(
                Instant.now().minus(7, ChronoUnit.DAYS)
            );
        } catch (IOException e) {
            return false;
        }
    })
    .verbose()
    .delete()
    .execute();
```

### Renaming Files

```java
// Rename files by adding timestamp
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
String timestamp = LocalDateTime.now().format(formatter);

Execute.on(Paths.get("reports"))
    .filter(path -> path.toString().endsWith(".pdf"))
    .rename(name -> timestamp + "_" + name)
    .execute();
```

### Creating Backups

```java
// Backup configuration files
Execute.on(Paths.get("config"))
    .filter(path -> path.toString().endsWith(".conf"))
    .backup(Paths.get("config/backup"))
    .execute();
```

### Creating Zip Archives

```java
// Zip source files
Execute.on(Paths.get("src"))
    .filter(path -> path.toString().endsWith(".java"))
    .zipTo(Paths.get("source_backup.zip"))
    .execute();
```

### Custom Operations

```java
// Perform custom operation on each file
Execute.on(Paths.get("data"))
    .filter(path -> path.toString().endsWith(".csv"))
    .forEach(path -> {
        // Process each CSV file
        System.out.println("Processing: " + path);
    })
    .execute();
```

### Chaining Operations

```java
// Backup files, then delete originals
Execute.on(Paths.get("logs"))
    .filter(path -> path.toString().endsWith(".log"))
    .backup(Paths.get("logs/backup"))
    .then()
    .delete()
    .execute();
```

## Advanced Features

### Dry Run Mode

Test operations without making actual changes:

```java
// Test deletion without actually deleting files
Execute.on(Paths.get("logs"))
    .filter(path -> path.toString().endsWith(".tmp"))
    .dryRun()
    .verbose()
    .delete()
    .execute();
```

### Custom Logging

Provide your own logger for operation tracking:

```java
// Use custom logger
Execute.on(Paths.get("data"))
    .withLogger(message -> myLogger.info(message))
    .copyTo(Paths.get("backup"))
    .execute();
```

## Error Handling

The Execute API handles errors gracefully:

- IOException during file operations are caught and logged
- Operations continue even if some files fail (best-effort approach)
- The execute() method returns the list of successfully processed files

```java
try {
    List<Path> processed = Execute.on(Paths.get("data"))
        .copyTo(Paths.get("backup"))
        .execute();
    
    System.out.println("Successfully processed " + processed.size() + " files");
} catch (Exception e) {
    System.err.println("Operation failed: " + e.getMessage());
}
```

## Best Practices

1. **Use filtering** to narrow down the files to operate on
2. **Enable verbose mode** for important operations to track progress
3. **Use confirmation** for destructive operations like delete
4. **Use dry run** to test operations before executing them for real
5. **Chain operations** for complex workflows
6. **Handle exceptions** appropriately

## Thread Safety

The Execute class is not thread-safe and should not be shared between threads. Each thread should create its own Execute instance.

## See Also

- [JavaDoc API](apidocs/io/joshuasalcedo/library/io/core/execute/package-summary.html)
- [File Operations](index.html#file-operations)