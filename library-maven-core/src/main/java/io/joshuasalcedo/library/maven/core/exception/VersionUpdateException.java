package io.joshuasalcedo.library.maven.core.exception;

/**
 * Exception thrown when there's an error updating dependency versions.
 * 
 * <p>This exception is thrown when the dependency executor cannot update
 * a dependency to a new version. This can occur due to invalid version
 * formats, missing properties, or other update-related issues.</p>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class VersionUpdateException extends RuntimeException {
    
    private final String groupId;
    private final String artifactId;
    private final String currentVersion;
    private final String targetVersion;
    
    /**
     * Constructs a new VersionUpdateException with full details about the failed update.
     * 
     * @param groupId the group ID of the dependency
     * @param artifactId the artifact ID of the dependency
     * @param currentVersion the current version of the dependency
     * @param targetVersion the target version that could not be set
     * @param message the detail message
     */
    public VersionUpdateException(String groupId, String artifactId, String currentVersion, 
                                  String targetVersion, String message) {
        super(String.format("Failed to update %s:%s from version %s to %s - %s", 
                            groupId, artifactId, currentVersion, targetVersion, message));
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.currentVersion = currentVersion;
        this.targetVersion = targetVersion;
    }
    
    /**
     * Constructs a new VersionUpdateException with full details about the failed update and a cause.
     * 
     * @param groupId the group ID of the dependency
     * @param artifactId the artifact ID of the dependency
     * @param currentVersion the current version of the dependency
     * @param targetVersion the target version that could not be set
     * @param cause the cause of the exception
     */
    public VersionUpdateException(String groupId, String artifactId, String currentVersion, 
                                  String targetVersion, Throwable cause) {
        super(String.format("Failed to update %s:%s from version %s to %s", 
                            groupId, artifactId, currentVersion, targetVersion), cause);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.currentVersion = currentVersion;
        this.targetVersion = targetVersion;
    }
    
    /**
     * Gets the group ID of the dependency that failed to update.
     * 
     * @return the group ID
     */
    public String getGroupId() {
        return groupId;
    }
    
    /**
     * Gets the artifact ID of the dependency that failed to update.
     * 
     * @return the artifact ID
     */
    public String getArtifactId() {
        return artifactId;
    }
    
    /**
     * Gets the current version of the dependency before the failed update.
     * 
     * @return the current version
     */
    public String getCurrentVersion() {
        return currentVersion;
    }
    
    /**
     * Gets the target version that the dependency was being updated to.
     * 
     * @return the target version
     */
    public String getTargetVersion() {
        return targetVersion;
    }
}