package io.joshuasalcedo.library.maven.core.exception;

/**
 * Exception thrown when there's an error accessing a Maven repository.
 * 
 * <p>This exception is thrown when the dependency executor cannot access
 * a Maven repository to check for latest versions or download metadata.
 * This includes network errors, HTTP errors, and authentication failures.</p>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class RepositoryAccessException extends RuntimeException {
    
    private final String repositoryUrl;
    private final int statusCode;
    
    /**
     * Constructs a new RepositoryAccessException with the specified repository URL and message.
     * 
     * @param repositoryUrl the URL of the repository that could not be accessed
     * @param message the detail message
     */
    public RepositoryAccessException(String repositoryUrl, String message) {
        super(String.format("Failed to access repository %s - %s", repositoryUrl, message));
        this.repositoryUrl = repositoryUrl;
        this.statusCode = -1;
    }
    
    /**
     * Constructs a new RepositoryAccessException with the specified repository URL, HTTP status code, and message.
     * 
     * @param repositoryUrl the URL of the repository that could not be accessed
     * @param statusCode the HTTP status code returned by the repository
     * @param message the detail message
     */
    public RepositoryAccessException(String repositoryUrl, int statusCode, String message) {
        super(String.format("Failed to access repository %s (HTTP %d) - %s", 
                            repositoryUrl, statusCode, message));
        this.repositoryUrl = repositoryUrl;
        this.statusCode = statusCode;
    }
    
    /**
     * Constructs a new RepositoryAccessException with the specified repository URL and cause.
     * 
     * @param repositoryUrl the URL of the repository that could not be accessed
     * @param cause the cause of the exception
     */
    public RepositoryAccessException(String repositoryUrl, Throwable cause) {
        super(String.format("Failed to access repository %s", repositoryUrl), cause);
        this.repositoryUrl = repositoryUrl;
        this.statusCode = -1;
    }
    
    /**
     * Gets the URL of the repository that could not be accessed.
     * 
     * @return the repository URL
     */
    public String getRepositoryUrl() {
        return repositoryUrl;
    }
    
    /**
     * Gets the HTTP status code returned by the repository.
     * 
     * @return the HTTP status code, or -1 if not applicable
     */
    public int getStatusCode() {
        return statusCode;
    }
}