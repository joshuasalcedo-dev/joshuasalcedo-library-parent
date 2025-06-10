package io.joshuasalcedo.library.maven.core.exception;

/**
 * Exception thrown when a Maven operation fails during execution.
 * 
 * <p>This exception wraps any underlying exceptions that occur during
 * the execution of Maven operations in the fluent API.</p>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class MavenExecutionException extends RuntimeException {
    
    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message
     */
    public MavenExecutionException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public MavenExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new exception with the specified cause.
     * 
     * @param cause the cause
     */
    public MavenExecutionException(Throwable cause) {
        super(cause);
    }
}