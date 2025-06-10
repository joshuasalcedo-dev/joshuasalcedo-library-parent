package io.joshuasalcedo.library.maven.core.exception;

import java.nio.file.Path;

/**
 * Exception thrown when there's an error saving a POM file.
 * 
 * <p>This exception is thrown when the dependency executor cannot write
 * changes back to a POM file, typically due to I/O errors, permission
 * issues, or disk space problems.</p>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class PomSaveException extends RuntimeException {
    
    private final Path pomPath;
    
    /**
     * Constructs a new PomSaveException with the specified POM path and message.
     * 
     * @param pomPath the path to the POM file that failed to save
     * @param message the detail message
     */
    public PomSaveException(Path pomPath, String message) {
        super(String.format("Failed to save POM file %s - %s", pomPath, message));
        this.pomPath = pomPath;
    }
    
    /**
     * Constructs a new PomSaveException with the specified POM path and cause.
     * 
     * @param pomPath the path to the POM file that failed to save
     * @param cause the cause of the exception
     */
    public PomSaveException(Path pomPath, Throwable cause) {
        super(String.format("Failed to save POM file %s", pomPath), cause);
        this.pomPath = pomPath;
    }
    
    /**
     * Gets the path to the POM file that failed to save.
     * 
     * @return the POM file path
     */
    public Path getPomPath() {
        return pomPath;
    }
}