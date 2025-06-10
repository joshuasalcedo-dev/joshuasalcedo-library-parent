package io.joshuasalcedo.library.maven.core;



import io.joshuasalcedo.library.maven.core.impl.Maven;
import io.joshuasalcedo.library.maven.core.impl.ModuleExecutorImpl;
import org.apache.maven.model.Model;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Interface for executing operations on Maven modules.
 * 
 * <p>This interface provides methods for discovering and loading Maven modules
 * from the filesystem. It supports finding POM files and parsing them into
 * Maven model objects.</p>
 * 
 * <p>Implementations should handle multi-module Maven projects and properly
 * traverse directory structures to find all relevant POM files.</p>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 * @see ModuleExecutorImpl
 * @see Maven
 */
public interface ModuleExecutor {

    /**
     * Finds Maven models from a given path.
     *
     * @param path the path to search for Maven models
     * @return a list of Maven models found in the path
     */
    List<Model> findModels(Path path);

    /**
     * Finds POM files in a given path.
     *
     * @param path the path to search for POM files
     * @return a list of paths to POM files found
     */
    List<Path> findPoms(Path path);

    /**
     * Finds Maven models from a list of paths to POM files.
     *
     * @param paths the list of paths to POM files
     * @return a list of Maven models created from the POM files
     */
    List<Model> findModels(List<Path> paths);

    /**
     * Creates a mapped structure of Maven models organized by their file paths.
     *
     * <p>This method processes Maven projects starting from the given path and returns
     * a nested map structure where each path is mapped to another map containing
     * relationships between Maven models (such as parent-child relationships).</p>
     *
     * @param path the root path to search for Maven projects
     * @return a nested map structure where outer keys are file paths, and inner maps represent
     *         relationships between Maven models (typically mapping original models to resolved models)
     */
    Map<Path,Map<Model,Model>> mappedModels(Path path);

}
