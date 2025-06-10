package io.joshuasalcedo.library.maven.core;



import io.joshuasalcedo.library.maven.core.impl.DependencyExecutorImpl;
import io.joshuasalcedo.library.maven.core.impl.Maven;
import io.joshuasalcedo.library.maven.core.model.InspectDependencyResult;
import org.apache.maven.model.Dependency;

import java.nio.file.Path;
import java.util.List;

/**
 * Interface for executing operations on Maven dependencies.
 * 
 * <p>This interface provides a comprehensive set of methods for managing Maven dependencies
 * including parsing from POM files, formatting, updating to latest versions, and inspecting
 * for vulnerabilities and version information.</p>
 * 
 * <p>Implementations should handle both direct dependencies and dependency management
 * sections in POM files.</p>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 * @see DependencyExecutorImpl
 * @see Maven
 */
public interface DependencyExecutor {

    /**
     * Parses dependencies from a POM file.
     *
     * @param pom the path to the POM file
     * @return a list of dependencies found in the POM file
     */
    List<Dependency> parseDependency(Path pom);

    /**
     * Converts a list of dependencies to their string representations.
     *
     * @param dependencies the list of dependencies to convert
     * @return a list of string representations of the dependencies
     */
    List<String> viewDependency(List<Dependency> dependencies);

    /**
     * Formats dependencies in a POM file.
     *
     * @param pom the path to the POM file to format
     */
    void formatDependency(Path pom);

    /**
     * Updates dependencies in a POM file to their latest versions.
     *
     * @param pom the path to the POM file to update
     */
    void updateDependencyToLatestVersion(Path pom);

    /**
     * Inspects a list of dependencies.
     *
     * @param dependencies the list of dependencies to inspect
     * @return list of inspection results containing version and vulnerability information
     */
    List<InspectDependencyResult> inspectDependency(List<Dependency> dependencies);

    /**
     * Inspects a single dependency.
     *
     * @param dependency the dependency to inspect
     * @return inspection result containing version and vulnerability information
     */
    InspectDependencyResult inspectDependency(Dependency dependency);
}
