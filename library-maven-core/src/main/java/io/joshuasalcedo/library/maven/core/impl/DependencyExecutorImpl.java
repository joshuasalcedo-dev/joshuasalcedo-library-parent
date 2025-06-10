package io.joshuasalcedo.library.maven.core.impl;


import io.joshuasalcedo.library.maven.core.DependencyExecutor;
import io.joshuasalcedo.library.maven.core.exception.*;
import io.joshuasalcedo.library.maven.core.model.InspectDependencyResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the DependencyExecutor interface.
 * 
 * <p>This class provides concrete implementations for all dependency management operations
 * including parsing, formatting, updating, and inspecting Maven dependencies. It uses a
 * fluent internal API pattern via the DependencyModel inner class to chain operations.</p>
 * 
 * <p>The implementation supports:</p>
 * <ul>
 *   <li>Reading dependencies from POM files using Maven's XPP3 reader</li>
 *   <li>Formatting dependency versions to use property placeholders</li>
 *   <li>Updating dependencies to their latest versions from various repositories</li>
 *   <li>Inspecting dependencies for version information and vulnerabilities</li>
 * </ul>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 * @see DependencyExecutor
 */
public class DependencyExecutorImpl implements DependencyExecutor {

    @Override
    public List<Dependency> parseDependency(Path pom) {
        return DependencyModel.from(pom)
                .loadModel()
                .extractDependencies()
                .getDependencies();
    }

    @Override
    public List<String> viewDependency(List<Dependency> dependencies) {
        return DependencyModel.fromDependencies(dependencies)
                .formatToStrings()
                .getFormattedDependencies();
    }

    @Override
    public void formatDependency(Path pom) {
        DependencyModel.from(pom)
                .loadModel()
                .formatDependencyVersions()
                .saveModel();
    }

    @Override
    public void updateDependencyToLatestVersion(Path pom) {
        DependencyModel.from(pom)
                .loadModel()
                .updateToLatestVersions()
                .saveModel();
    }

    @Override
    public List<InspectDependencyResult> inspectDependency(List<Dependency> dependencies) {
        return DependencyModel.fromDependencies(dependencies)
                .inspect()
                .getInspectionResults();
    }

    @Override
    public InspectDependencyResult inspectDependency(Dependency dependency) {
        List<InspectDependencyResult> results = DependencyModel.fromDependency(dependency)
                .inspect()
                .getInspectionResults();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Internal model class that implements a fluent API for dependency operations.
     * 
     * <p>This class encapsulates the state and operations for dependency management,
     * allowing for a clean chain of operations pattern. It handles both direct
     * dependencies and dependency management sections.</p>
     * 
     * @author Joshua Salcedo
     * @since 1.0.0
     */
    @Data
    private static class DependencyModel {
        private final Path pomPath;
        private org.apache.maven.model.Model model;
        private List<Dependency> dependencies;
        private List<String> formattedDependencies;
        private List<InspectDependencyResult> inspectionResults;
        Logger log = LoggerFactory.getLogger(DependencyModel.class);
        private DependencyModel(Path pomPath) {
            this.pomPath = pomPath;
            this.dependencies = new ArrayList<>();
            this.formattedDependencies = new ArrayList<>();
            this.inspectionResults = new ArrayList<>();
        }

        private DependencyModel(List<Dependency> dependencies) {
            this.pomPath = null;
            this.dependencies = new ArrayList<>(dependencies);
            this.formattedDependencies = new ArrayList<>();
            this.inspectionResults = new ArrayList<>();
        }

        private DependencyModel(Dependency dependency) {
            this.pomPath = null;
            this.dependencies = new ArrayList<>();
            this.dependencies.add(dependency);
            this.formattedDependencies = new ArrayList<>();
            this.inspectionResults = new ArrayList<>();
        }

        // Factory methods
        /**
         * Creates a DependencyModel instance from a POM file path.
         * 
         * @param pomPath the path to the POM file
         * @return a new DependencyModel instance
         */
        public static DependencyModel from(Path pomPath) {
            return new DependencyModel(pomPath);
        }

        /**
         * Creates a DependencyModel instance from a list of dependencies.
         * 
         * @param dependencies the list of dependencies to work with
         * @return a new DependencyModel instance
         */
        public static DependencyModel fromDependencies(List<Dependency> dependencies) {
            return new DependencyModel(dependencies);
        }

        /**
         * Creates a DependencyModel instance from a single dependency.
         * 
         * @param dependency the dependency to work with
         * @return a new DependencyModel instance
         */
        public static DependencyModel fromDependency(Dependency dependency) {
            return new DependencyModel(dependency);
        }

        // Fluent API methods
        /**
         * Loads the Maven model from the POM file.
         * 
         * @return this instance for method chaining
         * @throws DependencyParseException if the POM file cannot be read or parsed
         */
        public DependencyModel loadModel() {
            if (pomPath == null) {
                return this;
            }

            MavenXpp3Reader reader = new MavenXpp3Reader();
            try (Reader fileReader = new FileReader(pomPath.toFile())) {
                this.model = reader.read(fileReader);
            } catch (java.io.FileNotFoundException e) {
                throw new DependencyParseException(pomPath, "POM file not found");
            } catch (java.io.IOException e) {
                throw new DependencyParseException(pomPath, "IO error reading POM file");
            } catch (Exception e) {
                throw new DependencyParseException(pomPath, e);
            }
            return this;
        }

        /**
         * Extracts dependencies from the loaded Maven model.
         * 
         * <p>This method extracts both direct dependencies and dependencies from
         * the dependency management section.</p>
         * 
         * @return this instance for method chaining
         */
        public DependencyModel extractDependencies() {
            if (model == null) {
                return this;
            }

            this.dependencies.clear();
            if (model.getDependencies() != null) {
                this.dependencies.addAll(model.getDependencies());
            }

            // Also include dependency management dependencies
            if (model.getDependencyManagement() != null &&
                    model.getDependencyManagement().getDependencies() != null) {
                this.dependencies.addAll(model.getDependencyManagement().getDependencies());
            }

            return this;
        }

        /**
         * Formats dependencies to human-readable string representations.
         * 
         * <p>The format is: {@code groupId:artifactId:version (scope) [type]}</p>
         * 
         * @return this instance for method chaining
         */
        public DependencyModel formatToStrings() {
            this.formattedDependencies.clear();

            for (Dependency dep : dependencies) {
                String formatted = String.format("%s:%s:%s%s%s",
                        dep.getGroupId(),
                        dep.getArtifactId(),
                        dep.getVersion() != null ? dep.getVersion() : "managed",
                        dep.getScope() != null ? " (" + dep.getScope() + ")" : "",
                        dep.getType() != null && !"jar".equals(dep.getType()) ? " [" + dep.getType() + "]" : ""
                );
                this.formattedDependencies.add(formatted);
            }

            return this;
        }

        /**
         * Formats dependency versions by moving them to properties.
         * 
         * <p>This method creates property placeholders for all dependency versions
         * using the convention {@code ${artifactId.version}}.</p>
         * 
         * @return this instance for method chaining
         */
        public DependencyModel formatDependencyVersions() {
            if (model == null) {
                return this;
            }

            // Initialize properties if not present
            if (model.getProperties() == null) {
                model.setProperties(new java.util.Properties());
            }

            // Process direct dependencies
            if (model.getDependencies() != null) {
                for (Dependency dep : model.getDependencies()) {
                    if (dep.getVersion() != null && !dep.getVersion().startsWith("${")) {
                        String propertyName = dep.getArtifactId() + ".version";
                        model.getProperties().setProperty(propertyName, dep.getVersion());
                        dep.setVersion("${" + propertyName + "}");
                    }
                }
            }

            // Process dependency management dependencies
            if (model.getDependencyManagement() != null &&
                    model.getDependencyManagement().getDependencies() != null) {
                for (Dependency dep : model.getDependencyManagement().getDependencies()) {
                    if (dep.getVersion() != null && !dep.getVersion().startsWith("${")) {
                        String propertyName = dep.getArtifactId() + ".version";
                        model.getProperties().setProperty(propertyName, dep.getVersion());
                        dep.setVersion("${" + propertyName + "}");
                    }
                }
            }

            return this;
        }

        /**
         * Updates all dependencies to their latest available versions.
         * 
         * <p>This method searches for the latest versions in the following order:</p>
         * <ol>
         *   <li>Maven Central repository</li>
         *   <li>Local Maven repository (~/.m2/repository)</li>
         *   <li>Configured remote repositories</li>
         * </ol>
         * 
         * @return this instance for method chaining
         */
        public DependencyModel updateToLatestVersions() {
            if (model == null) {
                return this;
            }

            int updatedCount = 0;
            List<VersionUpdateException> updateErrors = new ArrayList<>();
            
            // Process direct dependencies
            if (model.getDependencies() != null) {
                for (Dependency dep : model.getDependencies()) {
                    try {
                        String currentVersion = resolveVersion(dep);
                        String latestVersion = findLatestVersion(dep);
                        if (latestVersion != null && !latestVersion.equals(currentVersion)) {
                            log.info("Updating {}:{} from {} to {}", 
                                    dep.getGroupId(), dep.getArtifactId(), 
                                    currentVersion, latestVersion);
                            updateDependencyVersion(dep, latestVersion);
                            updatedCount++;
                        }
                    } catch (VersionResolutionException e) {
                        log.warn("Failed to resolve version for {}:{} - {}", 
                                dep.getGroupId(), dep.getArtifactId(), e.getMessage());
                        updateErrors.add(new VersionUpdateException(
                                dep.getGroupId(), dep.getArtifactId(), 
                                dep.getVersion(), "unknown", e.getMessage()));
                    } catch (Exception e) {
                        log.error("Unexpected error updating {}:{}", 
                                dep.getGroupId(), dep.getArtifactId(), e);
                        updateErrors.add(new VersionUpdateException(
                                dep.getGroupId(), dep.getArtifactId(), 
                                dep.getVersion(), "unknown", e.getMessage()));
                    }
                }
            }

            // Process dependency management dependencies
            if (model.getDependencyManagement() != null &&
                    model.getDependencyManagement().getDependencies() != null) {
                for (Dependency dep : model.getDependencyManagement().getDependencies()) {
                    try {
                        String currentVersion = resolveVersion(dep);
                        String latestVersion = findLatestVersion(dep);
                        if (latestVersion != null && !latestVersion.equals(currentVersion)) {
                            log.info("Updating {}:{} from {} to {} (dependency management)", 
                                    dep.getGroupId(), dep.getArtifactId(), 
                                    currentVersion, latestVersion);
                            updateDependencyVersion(dep, latestVersion);
                            updatedCount++;
                        }
                    } catch (VersionResolutionException e) {
                        log.warn("Failed to resolve version for {}:{} in dependency management - {}", 
                                dep.getGroupId(), dep.getArtifactId(), e.getMessage());
                        updateErrors.add(new VersionUpdateException(
                                dep.getGroupId(), dep.getArtifactId(), 
                                dep.getVersion(), "unknown", e.getMessage()));
                    } catch (Exception e) {
                        log.error("Unexpected error updating {}:{} in dependency management", 
                                dep.getGroupId(), dep.getArtifactId(), e);
                        updateErrors.add(new VersionUpdateException(
                                dep.getGroupId(), dep.getArtifactId(), 
                                dep.getVersion(), "unknown", e.getMessage()));
                    }
                }
            }
            
            if (!updateErrors.isEmpty()) {
                log.warn("Failed to update {} dependencies", updateErrors.size());
                for (VersionUpdateException error : updateErrors) {
                    log.warn("  - {}", error.getMessage());
                }
            }
            
            if (updatedCount == 0) {
                log.info("All dependencies are already at their latest versions");
            } else {
                log.info("Updated {} dependencies to their latest versions", updatedCount);
            }

            return this;
        }
        
        /**
         * Resolves the actual version of a dependency, expanding property placeholders.
         * 
         * @param dep the dependency to resolve
         * @return the resolved version string
         */
        private String resolveVersion(Dependency dep) {
            String version = dep.getVersion();
            if (version != null && version.startsWith("${") && version.endsWith("}")) {
                String propertyName = version.substring(2, version.length() - 1);
                if (model.getProperties() != null && model.getProperties().containsKey(propertyName)) {
                    return model.getProperties().getProperty(propertyName);
                }
            }
            return version;
        }

        /**
         * Finds the latest available version for a dependency.
         * 
         * @param dependency the dependency to check
         * @return the latest version found, or the current version if none newer found
         * @throws VersionResolutionException if no version can be found
         */
        private String findLatestVersion(Dependency dependency) {
            String groupId = dependency.getGroupId();
            String artifactId = dependency.getArtifactId();
            
            if (groupId == null || artifactId == null) {
                throw new VersionResolutionException(
                        groupId != null ? groupId : "null", 
                        artifactId != null ? artifactId : "null", 
                        "Missing groupId or artifactId");
            }

            // Try multiple sources in order of preference
            List<Exception> suppressedExceptions = new ArrayList<>();

            try {
                // 1. Try Maven Central API
                String version = fetchFromMavenCentral(groupId, artifactId);
                if (version != null) return version;
            } catch (RepositoryAccessException e) {
                log.debug("Maven Central access failed for {}:{} - {}", 
                         groupId, artifactId, e.getMessage());
                suppressedExceptions.add(e);
            }

            try {
                // 2. Try local repository
                String version = fetchFromLocalRepository(groupId, artifactId);
                if (version != null) return version;
            } catch (Exception e) {
                log.debug("Local repository check failed for {}:{} - {}", 
                         groupId, artifactId, e.getMessage());
                suppressedExceptions.add(e);
            }

            try {
                // 3. Try configured remote repositories
                String version = fetchFromRemoteRepositories(groupId, artifactId);
                if (version != null) return version;
            } catch (Exception e) {
                log.debug("Remote repository check failed for {}:{} - {}", 
                         groupId, artifactId, e.getMessage());
                suppressedExceptions.add(e);
            }

            // 4. Return current version if no newer version found
            String currentVersion = dependency.getVersion();
            if (currentVersion == null) {
                VersionResolutionException ex = new VersionResolutionException(
                        groupId, artifactId, 
                        "No version found in any repository and no current version specified");
                suppressedExceptions.forEach(ex::addSuppressed);
                throw ex;
            }
            
            return currentVersion;
        }

        /**
         * Fetches the latest version from Maven Central repository.
         * 
         * @param groupId the group ID of the artifact
         * @param artifactId the artifact ID
         * @return the latest version found, or null if not found
         * @throws RepositoryAccessException if there's an error accessing Maven Central
         */
        private String fetchFromMavenCentral(String groupId, String artifactId) {
            String url = String.format(
                    "https://search.maven.org/solrsearch/select?q=g:%s+AND+a:%s&rows=1&wt=json",
                    groupId.replace(".", "."),
                    artifactId
            );
            
            try {
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(url))
                        .header("Accept", "application/json")
                        .timeout(java.time.Duration.ofSeconds(10))
                        .build();
                
                java.net.http.HttpResponse<String> response = client.send(request, 
                        java.net.http.HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    String version = extractVersionFromJson(response.body());
                    if (version == null) {
                        log.debug("No version found in Maven Central response for {}:{}", groupId, artifactId);
                    }
                    return version;
                } else if (response.statusCode() == 404) {
                    log.debug("Artifact {}:{} not found in Maven Central", groupId, artifactId);
                    return null;
                } else {
                    throw new RepositoryAccessException(url, response.statusCode(), 
                            "Unexpected response from Maven Central");
                }

            } catch (java.net.http.HttpTimeoutException e) {
                throw new RepositoryAccessException(url, "Request timed out");
            } catch (java.io.IOException e) {
                throw new RepositoryAccessException(url, "Network error: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RepositoryAccessException(url, "Request interrupted");
            } catch (Exception e) {
                // Log error and continue to next source
                log.debug("Failed to fetch from Maven Central for {}:{} - {}", 
                         groupId, artifactId, e.getMessage());
                return null;
            }
        }

        /**
         * Fetches the latest version from the local Maven repository.
         * 
         * @param groupId the group ID of the artifact
         * @param artifactId the artifact ID
         * @return the latest version found, or null if not found
         */
        private String fetchFromLocalRepository(String groupId, String artifactId) {
            try {
                // Check local Maven repository (usually ~/.m2/repository)
                String userHome = System.getProperty("user.home");
                Path localRepo = Path.of(userHome, ".m2", "repository");

                // Convert groupId to path (com.example -> com/example)
                String groupPath = groupId.replace('.', '/');
                Path artifactPath = localRepo.resolve(groupPath).resolve(artifactId);

                if (java.nio.file.Files.exists(artifactPath)) {
                    // Find all version directories
                    List<String> versions = new ArrayList<>();
                    try (var stream = java.nio.file.Files.list(artifactPath)) {
                        stream.filter(java.nio.file.Files::isDirectory)
                                .map(Path::getFileName)
                                .map(Path::toString)
                                .filter(this::isValidVersion)
                                .forEach(versions::add);
                    }

                    // Sort and return latest stable version
                    return findLatestStableVersion(versions);
                }
            } catch (Exception e) {
                // Log error and continue
            }
            return null;
        }

        /**
         * Fetches the latest version from configured remote repositories.
         * 
         * @param groupId the group ID of the artifact
         * @param artifactId the artifact ID
         * @return the latest version found, or null if not found
         */
        private String fetchFromRemoteRepositories(String groupId, String artifactId) {
            // Check repositories defined in POM or settings.xml
            if (model.getRepositories() != null) {
                for (org.apache.maven.model.Repository repo : model.getRepositories()) {
                    String version = fetchFromRepository(repo.getUrl(), groupId, artifactId);
                    if (version != null) return version;
                }
            }

            // Also check common repositories
            String[] commonRepos = {
                    "https://repo1.maven.org/maven2",
                    "https://jcenter.bintray.com",
                    "https://repo.spring.io/release"
            };

            for (String repoUrl : commonRepos) {
                String version = fetchFromRepository(repoUrl, groupId, artifactId);
                if (version != null) return version;
            }

            return null;
        }

        /**
         * Fetches the latest version from a specific repository.
         * 
         * @param repoUrl the repository URL
         * @param groupId the group ID of the artifact
         * @param artifactId the artifact ID
         * @return the latest version found, or null if not found
         */
        private String fetchFromRepository(String repoUrl, String groupId, String artifactId) {
            try {
                // Construct metadata URL
                String groupPath = groupId.replace('.', '/');
                String metadataUrl = String.format("%s/%s/%s/maven-metadata.xml",
                        repoUrl.endsWith("/") ? repoUrl.substring(0, repoUrl.length() - 1) : repoUrl,
                        groupPath,
                        artifactId
                );

                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(metadataUrl))
                        .timeout(java.time.Duration.ofSeconds(10))
                        .build();
                
                java.net.http.HttpResponse<String> response = client.send(request, 
                        java.net.http.HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    return extractVersionFromXml(response.body());
                }

            } catch (Exception e) {
                // Log error silently - repository might not contain this artifact
            }
            return null;
        }

        /**
         * Checks if a string represents a valid version number.
         * 
         * @param version the version string to check
         * @return true if the version is valid, false otherwise
         */
        private boolean isValidVersion(String version) {
            // Filter out non-version directories
            return version.matches("\\d+\\.\\d+.*") &&
                    !version.endsWith(".lastUpdated");
        }

        /**
         * Finds the latest stable version from a list of versions.
         * 
         * <p>Prefers stable versions over snapshots, betas, and alphas.</p>
         * 
         * @param versions the list of versions to search
         * @return the latest stable version, or the latest version if no stable found
         */
        private String findLatestStableVersion(List<String> versions) {
            // Sort versions properly (considering semantic versioning)
            // Prefer stable versions over snapshots/betas
            return versions.stream()
                    .filter(v -> !v.contains("SNAPSHOT") && !v.contains("beta") && !v.contains("alpha"))
                    .sorted((v1, v2) -> compareVersions(v2, v1)) // Reverse order for latest first
                    .findFirst()
                    .orElse(versions.isEmpty() ? null : versions.get(versions.size() - 1));
        }

        /**
         * Compares two version strings using semantic versioning rules.
         * 
         * @param v1 the first version
         * @param v2 the second version
         * @return negative if v1 < v2, positive if v1 > v2, zero if equal
         */
        private int compareVersions(String v1, String v2) {
            // Handle null/empty cases
            if (v1 == null && v2 == null) return 0;
            if (v1 == null) return -1;
            if (v2 == null) return 1;
            
            // Remove common prefixes
            v1 = v1.replaceFirst("^v", "");
            v2 = v2.replaceFirst("^v", "");
            
            // Split into parts and qualifiers
            String[] parts1 = splitVersion(v1);
            String[] parts2 = splitVersion(v2);
            
            // Compare numeric parts
            int minLength = Math.min(parts1.length, parts2.length);
            for (int i = 0; i < minLength; i++) {
                int cmp = compareVersionPart(parts1[i], parts2[i]);
                if (cmp != 0) {
                    return cmp;
                }
            }
            
            // If all parts are equal, longer version is newer
            return Integer.compare(parts1.length, parts2.length);
        }
        
        /**
         * Splits a version string into its component parts.
         * 
         * @param version the version string to split
         * @return array of version parts
         */
        private String[] splitVersion(String version) {
            // Split on dots, hyphens, and underscores
            return version.split("[.\\-_]");
        }
        
        /**
         * Compares individual version parts (numeric or qualifier).
         * 
         * @param part1 the first version part
         * @param part2 the second version part
         * @return comparison result
         */
        private int compareVersionPart(String part1, String part2) {
            // Try numeric comparison first
            boolean isNum1 = part1.matches("\\d+");
            boolean isNum2 = part2.matches("\\d+");
            
            if (isNum1 && isNum2) {
                return Integer.compare(Integer.parseInt(part1), Integer.parseInt(part2));
            }
            
            // Handle qualifiers (alpha < beta < rc < release)
            if (!isNum1 && !isNum2) {
                return compareQualifiers(part1, part2);
            }
            
            // Numbers come before qualifiers
            return isNum1 ? 1 : -1;
        }
        
        /**
         * Compares version qualifiers (alpha, beta, rc, etc.).
         * 
         * @param q1 the first qualifier
         * @param q2 the second qualifier
         * @return comparison result based on qualifier priority
         */
        private int compareQualifiers(String q1, String q2) {
            // Define qualifier priority
            String[] qualifierOrder = {"alpha", "beta", "milestone", "rc", "release", "final", "ga"};
            
            int idx1 = -1, idx2 = -1;
            for (int i = 0; i < qualifierOrder.length; i++) {
                if (q1.toLowerCase().startsWith(qualifierOrder[i])) idx1 = i;
                if (q2.toLowerCase().startsWith(qualifierOrder[i])) idx2 = i;
            }
            
            // If both are known qualifiers, compare by order
            if (idx1 >= 0 && idx2 >= 0) {
                return Integer.compare(idx1, idx2);
            }
            
            // Unknown qualifiers - do string comparison
            return q1.compareToIgnoreCase(q2);
        }

        /**
         * Parses the numeric portion of a version part.
         * 
         * @param part the version part to parse
         * @return the numeric value, or 0 if not numeric
         */
        private int parseVersionPart(String part) {
            // Extract numeric part from version component
            try {
                return Integer.parseInt(part.replaceAll("[^0-9].*", ""));
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        /**
         * Extracts version information from Maven Central JSON response.
         * 
         * @param json the JSON response string
         * @return the extracted version, or null if not found
         */
        private String extractVersionFromJson(String json) {
            try {
                // Simple JSON parsing without external dependencies
                // Looking for pattern: "latestVersion":"x.y.z"
                int startIdx = json.indexOf("\"latestVersion\":\"");
                if (startIdx != -1) {
                    startIdx += "\"latestVersion\":\"".length();
                    int endIdx = json.indexOf("\"", startIdx);
                    if (endIdx != -1) {
                        return json.substring(startIdx, endIdx);
                    }
                }
                
                // Alternative: look for "v":"x.y.z" in docs array
                startIdx = json.indexOf("\"v\":\"");
                if (startIdx != -1) {
                    startIdx += "\"v\":\"".length();
                    int endIdx = json.indexOf("\"", startIdx);
                    if (endIdx != -1) {
                        return json.substring(startIdx, endIdx);
                    }
                }
            } catch (Exception e) {
                // Parsing failed
            }
            return null;
        }

        /**
         * Extracts version information from Maven metadata XML.
         * 
         * @param xml the XML metadata string
         * @return the extracted version, or null if not found
         */
        private String extractVersionFromXml(String xml) {
            try {
                // Look for <release> tag first (most stable)
                String releaseVersion = extractXmlTag(xml, "release");
                if (releaseVersion != null) {
                    return releaseVersion;
                }
                
                // Fall back to <latest> tag
                String latestVersion = extractXmlTag(xml, "latest");
                if (latestVersion != null) {
                    return latestVersion;
                }
                
                // If no release/latest, parse versions and find the highest stable one
                String versionsSection = extractXmlTag(xml, "versions");
                if (versionsSection != null) {
                    List<String> versions = new ArrayList<>();
                    int idx = 0;
                    while ((idx = versionsSection.indexOf("<version>", idx)) != -1) {
                        idx += "<version>".length();
                        int endIdx = versionsSection.indexOf("</version>", idx);
                        if (endIdx != -1) {
                            versions.add(versionsSection.substring(idx, endIdx));
                        }
                    }
                    return findLatestStableVersion(versions);
                }
            } catch (Exception e) {
                // Parsing failed
            }
            return null;
        }

        /**
         * Extracts the content of a specific XML tag.
         * 
         * @param xml the XML string
         * @param tagName the tag name to extract
         * @return the tag content, or null if not found
         */
        private String extractXmlTag(String xml, String tagName) {
            String startTag = "<" + tagName + ">";
            String endTag = "</" + tagName + ">";
            int startIdx = xml.indexOf(startTag);
            if (startIdx != -1) {
                startIdx += startTag.length();
                int endIdx = xml.indexOf(endTag, startIdx);
                if (endIdx != -1) {
                    return xml.substring(startIdx, endIdx).trim();
                }
            }
            return null;
        }

        /**
         * Updates the version of a dependency.
         * 
         * <p>If the version is a property reference, updates the property value.
         * Otherwise, updates the version directly.</p>
         * 
         * @param dep the dependency to update
         * @param newVersion the new version to set
         * @throws VersionUpdateException if the update fails
         */
        private void updateDependencyVersion(Dependency dep, String newVersion) {
            if (newVersion == null || newVersion.trim().isEmpty()) {
                throw new VersionUpdateException(
                        dep.getGroupId(), dep.getArtifactId(), 
                        dep.getVersion(), newVersion, 
                        "New version cannot be null or empty");
            }
            
            String currentVersion = dep.getVersion();
            
            // If version is a property reference, update the property
            if (currentVersion != null && currentVersion.startsWith("${") && currentVersion.endsWith("}")) {
                String propertyName = currentVersion.substring(2, currentVersion.length() - 1);
                if (model.getProperties() != null) {
                    String oldPropertyValue = model.getProperties().getProperty(propertyName);
                    model.getProperties().setProperty(propertyName, newVersion);
                    log.debug("Updated property {} from {} to {}", propertyName, oldPropertyValue, newVersion);
                } else {
                    throw new VersionUpdateException(
                            dep.getGroupId(), dep.getArtifactId(), 
                            currentVersion, newVersion, 
                            "Properties section not found for property: " + propertyName);
                }
            } else {
                // Direct version update
                dep.setVersion(newVersion);
                log.debug("Updated {}:{} version directly from {} to {}", 
                         dep.getGroupId(), dep.getArtifactId(), currentVersion, newVersion);
            }
        }

        /**
         * Inspects dependencies for version and vulnerability information.
         * 
         * <p>Note: Current implementation provides placeholder data. A full
         * implementation would check Maven Central and vulnerability databases.</p>
         * 
         * @return this instance for method chaining
         */
        public DependencyModel inspect() {
            this.inspectionResults.clear();

            for (Dependency dep : dependencies) {
                try {
                    // Get current version
                    String currentVersion = dep.getVersion() != null ? dep.getVersion() : "unknown";
                    
                    // Try to find latest version
                    String latestVersion = currentVersion;
                    try {
                        latestVersion = findLatestVersion(dep);
                        if (latestVersion == null) {
                            latestVersion = currentVersion;
                        }
                    } catch (Exception e) {
                        log.debug("Could not determine latest version for {}:{}", 
                                dep.getGroupId(), dep.getArtifactId());
                    }
                    
                    // Get type and scope
                    String type = dep.getType() != null ? dep.getType() : "jar";
                    String scope = dep.getScope() != null ? dep.getScope() : "compile";
                    
                    // Check if current version is latest
                    boolean isLatest = currentVersion.equals(latestVersion);
                    
                    // TODO: In a real implementation, these would be populated:
                    // - availableVersions: Query Maven repositories for all versions
                    // - transitives: Resolve transitive dependencies
                    // - vulnerabilities: Check OWASP/NVD databases
                    // - documentation: Fetch from project metadata
                    
                    List<String> availableVersions = List.of(currentVersion, latestVersion);
                    List<String> transitives = List.of(); // Would be populated from dependency resolution
                    List<String> vulnerabilities = List.of(); // Would be populated from security databases
                    boolean isVulnerable = false; // Would be set based on vulnerability check
                    Optional<String> documentation = Optional.empty(); // Would fetch actual doc URL
                    
                    InspectDependencyResult result = new InspectDependencyResult(
                            dep.getGroupId(),
                            dep.getArtifactId(),
                            currentVersion,
                            latestVersion,
                            type,
                            scope,
                            availableVersions,
                            transitives,
                            vulnerabilities,
                            isLatest,
                            isVulnerable,
                            documentation
                    );
                    
                    this.inspectionResults.add(result);
                } catch (Exception e) {
                    log.error("Error inspecting dependency {}:{}", 
                            dep.getGroupId(), dep.getArtifactId(), e);
                    // Add a result with error information
                    InspectDependencyResult errorResult = new InspectDependencyResult(
                            dep.getGroupId(),
                            dep.getArtifactId(),
                            dep.getVersion() != null ? dep.getVersion() : "unknown",
                            "unknown",
                            dep.getType() != null ? dep.getType() : "jar",
                            dep.getScope() != null ? dep.getScope() : "compile",
                            List.of(),
                            List.of(),
                            List.of("Error: " + e.getMessage()),
                            false,
                            false,
                            Optional.empty()
                    );
                    this.inspectionResults.add(errorResult);
                }
            }

            return this;
        }

        /**
         * Saves the Maven model back to the POM file.
         * 
         * @return this instance for method chaining
         * @throws PomSaveException if the file cannot be saved
         */
        public DependencyModel saveModel() {
            if (model == null || pomPath == null) {
                return this;
            }

            MavenXpp3Writer writer = new MavenXpp3Writer();
            try (Writer fileWriter = new FileWriter(pomPath.toFile())) {
                writer.write(fileWriter, model);
            } catch (java.io.IOException e) {
                throw new PomSaveException(pomPath, e);
            } catch (Exception e) {
                throw new PomSaveException(pomPath, "Unexpected error: " + e.getMessage());
            }

            return this;
        }

        // Terminal operations
        /**
         * Gets the list of dependencies.
         * 
         * @return a copy of the dependencies list
         */
        public List<Dependency> getDependencies() {
            return new ArrayList<>(dependencies);
        }

        /**
         * Gets the list of formatted dependency strings.
         * 
         * @return a copy of the formatted dependencies list
         */
        public List<String> getFormattedDependencies() {
            return new ArrayList<>(formattedDependencies);
        }

        /**
         * Prints the dependency inspection results to standard output.
         */
        public void printInspectionResults() {
            for (InspectDependencyResult result : inspectionResults) {
                System.out.println("=== Maven Inspection ===");
                System.out.println("Artifact: " + result.groupId() + ":" + result.artifactId());
                System.out.println("Current Version: " + result.currentVersion());
                System.out.println("Latest Version: " + result.latestVersion());
                System.out.println("Type: " + result.type());
                System.out.println("Scope: " + result.scope());
                System.out.println("Is Latest: " + result.isLatest());
                System.out.println("Has Vulnerabilities: " + result.isVulnerable());
                
                if (!result.availableVersions().isEmpty()) {
                    System.out.println("Available Versions: " + String.join(", ", result.availableVersions()));
                }
                
                if (!result.transitives().isEmpty()) {
                    System.out.println("Transitive Dependencies: " + result.transitives().size());
                }
                
                if (!result.vulnerabilities().isEmpty()) {
                    System.out.println("Vulnerabilities:");
                    for (String vuln : result.vulnerabilities()) {
                        System.out.println("  - " + vuln);
                    }
                }
                
                result.documentation().ifPresent(doc ->
                        System.out.println("Documentation: " + doc)
                );
                System.out.println();
            }
        }
        
        /**
         * Gets the inspection results.
         * 
         * @return list of inspection results
         */
        public List<InspectDependencyResult> getInspectionResults() {
            return new ArrayList<>(inspectionResults);
        }
    }
}