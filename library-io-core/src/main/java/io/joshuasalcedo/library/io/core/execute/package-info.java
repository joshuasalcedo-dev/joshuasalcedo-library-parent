/**
 * Provides a fluent API for executing file operations in the file system.
 *
 * <p>This package offers a powerful and intuitive way to perform various file operations
 * such as copying, moving, deleting, renaming, backing up, and zipping files. The API is
 * designed to be chainable and easy to use while providing flexibility for complex
 * file operation requirements.</p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Fluent, chainable API for building file operations</li>
 *   <li>Support for multiple file operations in sequence</li>
 *   <li>Dry run mode for testing operations without making changes</li>
 *   <li>Verbose logging for operation tracking</li>
 *   <li>Custom filtering of files</li>
 *   <li>Confirmation prompts for critical operations</li>
 *   <li>Error handling and recovery</li>
 * </ul>
 *
 * <h2>Main Components</h2>
 * <dl>
 *   <dt>{@link io.joshuasalcedo.library.io.core.execute.Execute}</dt>
 *   <dd>The main class providing a fluent API for building and executing file operations</dd>
 * </dl>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Copying files</h3>
 * <pre>{@code
 * // Copy all Java files to a backup directory
 * Execute.on(Paths.get("src"))
 *     .filter(path -> path.toString().endsWith(".java"))
 *     .copyTo(Paths.get("backup"))
 *     .execute();
 * }</pre>
 *
 * <h3>Moving files with confirmation</h3>
 * <pre>{@code
 * // Move log files to archive with confirmation
 * Execute.on(Paths.get("logs"))
 *     .filter(path -> path.toString().endsWith(".log"))
 *     .withConfirmation("Move log files to archive?")
 *     .moveTo(Paths.get("archive"))
 *     .execute();
 * }</pre>
 *
 * <h3>Deleting files</h3>
 * <pre>{@code
 * // Delete temporary files
 * Execute.on(Paths.get("temp"))
 *     .filter(path -> {
 *         try {
 *             FileTime time = Files.getLastModifiedTime(path);
 *             return time.toInstant().isBefore(
 *                 Instant.now().minus(7, ChronoUnit.DAYS)
 *             );
 *         } catch (IOException e) {
 *             return false;
 *         }
 *     })
 *     .verbose()
 *     .delete()
 *     .execute();
 * }</pre>
 *
 * <h3>Renaming files</h3>
 * <pre>{@code
 * // Rename files by adding timestamp
 * DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
 * String timestamp = LocalDateTime.now().format(formatter);
 * 
 * Execute.on(Paths.get("reports"))
 *     .filter(path -> path.toString().endsWith(".pdf"))
 *     .rename(name -> timestamp + "_" + name)
 *     .execute();
 * }</pre>
 *
 * <h3>Creating backups</h3>
 * <pre>{@code
 * // Backup configuration files
 * Execute.on(Paths.get("config"))
 *     .filter(path -> path.toString().endsWith(".conf"))
 *     .backup(Paths.get("config/backup"))
 *     .execute();
 * }</pre>
 *
 * <h3>Creating zip archives</h3>
 * <pre>{@code
 * // Zip source files
 * Execute.on(Paths.get("src"))
 *     .filter(path -> path.toString().endsWith(".java"))
 *     .zipTo(Paths.get("source_backup.zip"))
 *     .execute();
 * }</pre>
 *
 * <h3>Custom operations</h3>
 * <pre>{@code
 * // Perform custom operation on each file
 * Execute.on(Paths.get("data"))
 *     .filter(path -> path.toString().endsWith(".csv"))
 *     .forEach(path -> {
 *         // Process each CSV file
 *         System.out.println("Processing: " + path);
 *     })
 *     .execute();
 * }</pre>
 *
 * <h3>Chaining operations</h3>
 * <pre>{@code
 * // Backup files, then delete originals
 * Execute.on(Paths.get("logs"))
 *     .filter(path -> path.toString().endsWith(".log"))
 *     .backup(Paths.get("logs/backup"))
 *     .then()
 *     .delete()
 *     .execute();
 * }</pre>
 *
 * <h2>Error Handling</h2>
 * <ul>
 *   <li>IOException during file operations are caught and logged</li>
 *   <li>Operations continue even if some files fail (best-effort approach)</li>
 *   <li>The execute() method returns the list of successfully processed files</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>The Execute class is not thread-safe and should not be shared between threads.
 * Each thread should create its own Execute instance.</p>
 *
 * @since 1.0.0
 * @author Joshua Salcedo
 */
package io.joshuasalcedo.library.io.core.execute;
