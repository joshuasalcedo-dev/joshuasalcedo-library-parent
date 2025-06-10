package io.joshuasalcedo.library.maven.core.model;

import java.util.List;
import java.util.Optional;

/**
 * Represents the result of inspecting a Maven dependency.
 * 
 * <p>This record contains comprehensive information about a dependency including its
 * coordinates, version information, vulnerabilities, transitive dependencies,
 * and optional documentation links.</p>
 * 
 * <p>This is a value-based class; instances should be treated as immutable.</p>
 * 
 * @param groupId the group ID of the dependency
 * @param artifactId the artifact ID of the dependency
 * @param currentVersion the current version of the dependency in the project
 * @param latestVersion the latest available version of the dependency
 * @param type the packaging type of the dependency (e.g., jar, war, pom)
 * @param scope the dependency scope (e.g., compile, test, provided, runtime)
 * @param availableVersions list of all available versions for this dependency
 * @param transitives list of transitive dependencies
 * @param vulnerabilities list of known security vulnerabilities
 * @param isLatest true if the current version is the latest available version
 * @param isVulnerable true if the dependency has known security vulnerabilities
 * @param documentation optional URL to the dependency's documentation
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public record InspectDependencyResult(
        String groupId,
        String artifactId,
        String currentVersion,
        String latestVersion,
        String type,
        String scope,
        List<String> availableVersions,
        List<String> transitives,
        List<String> vulnerabilities,
        boolean isLatest,
        boolean isVulnerable,
        Optional<String> documentation
) {
    /**
     * Creates an InspectDependencyResult with default values for optional fields.
     * 
     * @param groupId the group ID of the dependency
     * @param artifactId the artifact ID of the dependency
     * @param currentVersion the current version
     * @param latestVersion the latest version
     * @param type the packaging type
     * @param scope the dependency scope
     */
    public InspectDependencyResult(String groupId, String artifactId, String currentVersion,
                                   String latestVersion, String type, String scope) {
        this(groupId, artifactId, currentVersion, latestVersion, type, scope,
             List.of(), List.of(), List.of(),
             currentVersion.equals(latestVersion), false, Optional.empty());
    }
    
    /**
     * Returns the version field for backward compatibility.
     * This delegates to currentVersion.
     * 
     * @return the current version
     * @deprecated Use {@link #currentVersion()} instead
     */
    @Deprecated
    public String version() {
        return currentVersion;
    }
}
