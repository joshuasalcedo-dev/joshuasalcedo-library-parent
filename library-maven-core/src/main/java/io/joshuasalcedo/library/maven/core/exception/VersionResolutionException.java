package io.joshuasalcedo.library.maven.core.exception;

/**
 * Exception thrown when there's an error resolving dependency versions.
 * 
 * <p>This exception is thrown when the dependency executor cannot determine
 * the version of a dependency, either the current version or the latest
 * available version. This can occur when version information is missing,
 * malformed, or cannot be retrieved from repositories.</p>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class VersionResolutionException extends RuntimeException {
    
    private final String groupId;
    private final String artifactId;
    
    /**
     * Constructs a new VersionResolutionException with the specified dependency coordinates and message.
     * 
     * @param groupId the group ID of the dependency
     * @param artifactId the artifact ID of the dependency
     * @param message the detail message
     */
    public VersionResolutionException(String groupId, String artifactId, String message) {
        super(String.format("Failed to resolve version for %s:%s - %s", groupId, artifactId, message));
        this.groupId = groupId;
        this.artifactId = artifactId;
    }
    
    /**
     * Constructs a new VersionResolutionException with the specified dependency coordinates and cause.
     * 
     * @param groupId the group ID of the dependency
     * @param artifactId the artifact ID of the dependency
     * @param cause the cause of the exception
     */
    public VersionResolutionException(String groupId, String artifactId, Throwable cause) {
        super(String.format("Failed to resolve version for %s:%s", groupId, artifactId), cause);
        this.groupId = groupId;
        this.artifactId = artifactId;
    }
    
    /**
     * Gets the group ID of the dependency that failed version resolution.
     * 
     * @return the group ID
     */
    public String getGroupId() {
        return groupId;
    }
    
    /**
     * Gets the artifact ID of the dependency that failed version resolution.
     * 
     * @return the artifact ID
     */
    public String getArtifactId() {
        return artifactId;
    }
}