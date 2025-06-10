package io.joshuasalcedo.library.maven.core.exception;

import java.nio.file.Path;

/**
 * Exception thrown when there's an error parsing dependencies from a POM file.
 * 
 * <p>This exception is thrown when the dependency executor encounters problems
 * reading or parsing a POM file, such as malformed XML, I/O errors, or
 * missing required elements.</p>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class DependencyParseException extends RuntimeException {
    
    private final Path pomPath;
    
    /**
     * Constructs a new DependencyParseException with the specified message.
     * 
     * @param message the detail message
     */
    public DependencyParseException(String message) {
        super(message);
        this.pomPath = null;
    }
    
    /**
     * Constructs a new DependencyParseException with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DependencyParseException(String message, Throwable cause) {
        super(message, cause);
        this.pomPath = null;
    }
    
    /**
     * Constructs a new DependencyParseException for a specific POM file with a cause.
     * 
     * @param pomPath the path to the POM file that failed to parse
     * @param cause the cause of the exception
     */
    public DependencyParseException(Path pomPath, Throwable cause) {
        super(String.format("Failed to parse dependencies from POM file: %s", pomPath), cause);
        this.pomPath = pomPath;
    }
    
    /**
     * Constructs a new DependencyParseException for a specific POM file with a reason.
     * 
     * @param pomPath the path to the POM file that failed to parse
     * @param reason the reason for the parse failure
     */
    public DependencyParseException(Path pomPath, String reason) {
        super(String.format("Failed to parse dependencies from POM file: %s. Reason: %s", pomPath, reason));
        this.pomPath = pomPath;
    }
    
    /**
     * Gets the path to the POM file that failed to parse.
     * 
     * @return the POM file path, or null if not associated with a specific file
     */
    public Path getPomPath() {
        return pomPath;
    }
}