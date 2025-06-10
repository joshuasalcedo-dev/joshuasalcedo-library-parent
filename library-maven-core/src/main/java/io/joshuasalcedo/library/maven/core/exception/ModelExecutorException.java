package io.joshuasalcedo.library.maven.core.exception;

import java.nio.file.Path;

/**
 * Exception thrown when there's an error executing operations on Maven models.
 * 
 * <p>This exception is thrown when the module executor encounters problems
 * loading or processing Maven model files, typically due to I/O errors or
 * malformed POM files.</p>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class ModelExecutorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ModelExecutorException for a specific path.
     * 
     * @param path the path where the error occurred
     */
    public ModelExecutorException(final Path path) {
        super("ModelExecutorException: " + path.toAbsolutePath() +"\n" +
                "There is a problem with the path");
    }

    /**
     * Constructs a new ModelExecutorException with a formatted message and cause.
     * 
     * @param format the message format string
     * @param cause the cause of the exception
     */
    public ModelExecutorException(String format, Throwable cause) {
        super(format, cause);
    }
}
