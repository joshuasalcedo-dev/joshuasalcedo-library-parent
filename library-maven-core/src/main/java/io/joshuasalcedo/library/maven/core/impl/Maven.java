package io.joshuasalcedo.library.maven.core.impl;



import io.joshuasalcedo.library.maven.core.DependencyExecutor;
import io.joshuasalcedo.library.maven.core.ModuleExecutor;
import io.joshuasalcedo.library.maven.core.exception.MavenExecutionException;
import io.joshuasalcedo.library.maven.core.model.InspectDependencyResult;
import io.joshuasalcedo.library.maven.core.model.MavenResult;
import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fluent API for executing Maven operations.
 *
 * <p>This class provides a unified, fluent interface for working with Maven modules and dependencies.
 * It follows the builder pattern, allowing operations to be chained together and executed lazily.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Find and process models
 * List<Model> models = Maven.from(projectPath)
 *     .findModels()
 *     .execute(MavenResult::getModels);
 *
 * // Chain multiple operations
 * MavenResult result = Maven.from(pomPath)
 *     .parseDependencies()
 *     .formatDependencies()
 *     .inspectDependencies()
 *     .execute();
 *
 * // Use callbacks for processing
 * Maven.from(projectPath)
 *     .findModels()
 *     .onModels(models -> models.forEach(System.out::println))
 *     .execute();
 * }</pre>
 *
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class Maven {

    private static final Logger log = LoggerFactory.getLogger(Maven.class);

    private final ModuleExecutor moduleExecutor;
    private final DependencyExecutor dependencyExecutor;
    private final MavenContext context;
    private final List<MavenOperation> operations;

    /**
     * Private constructor to enforce factory method usage.
     */
    private Maven(MavenContext context) {
        this.moduleExecutor = new ModuleExecutorImpl();
        this.dependencyExecutor = new DependencyExecutorImpl();
        this.context = context;
        this.operations = new ArrayList<>();
    }

    // Factory methods

    /**
     * Creates a Maven instance for operations on a specific path.
     *
     * @param path the path to perform operations on
     * @return a new Maven instance
     */
    public static Maven from(Path path) {
        Objects.requireNonNull(path, "Path cannot be null");
        return new Maven(MavenContext.forPath(path));
    }

    /**
     * Creates a Maven instance for operations on multiple paths.
     *
     * @param paths the paths to perform operations on
     * @return a new Maven instance
     */
    public static Maven from(List<Path> paths) {
        Objects.requireNonNull(paths, "Paths cannot be null");
        if (paths.isEmpty()) {
            throw new IllegalArgumentException("Paths list cannot be empty");
        }
        return new Maven(MavenContext.forPaths(paths));
    }

    /**
     * Creates a Maven instance for operations on dependencies.
     *
     * @param dependencies the dependencies to perform operations on
     * @return a new Maven instance
     */
    public static Maven forDependencies(List<Dependency> dependencies) {
        Objects.requireNonNull(dependencies, "Dependencies cannot be null");
        return new Maven(MavenContext.forDependencies(dependencies));
    }

    /**
     * Creates a Maven instance for operations on a single dependency.
     *
     * @param dependency the dependency to perform operations on
     * @return a new Maven instance
     */
    public static Maven forDependency(Dependency dependency) {
        Objects.requireNonNull(dependency, "Dependency cannot be null");
        return new Maven(MavenContext.forDependency(dependency));
    }

    // Module operations

    /**
     * Finds all Maven models in the target path(s).
     *
     * @return this Maven for method chaining
     */
    public Maven findModels() {
        operations.add(new FindModelsOperation());
        return this;
    }

    /**
     * Finds all POM files in the target path.
     *
     * @return this Maven for method chaining
     */
    public Maven findPoms() {
        operations.add(new FindPomsOperation());
        return this;
    }

    /**
     * Maps models to their original and resolved versions.
     *
     * @return this Maven for method chaining
     */
    public Maven mapModels() {
        operations.add(new MapModelsOperation());
        return this;
    }

    // Dependency operations

    /**
     * Parses dependencies from the target POM file.
     *
     * @return this Maven for method chaining
     */
    public Maven parseDependencies() {
        operations.add(new ParseDependenciesOperation());
        return this;
    }

    /**
     * Formats dependencies to string representations.
     *
     * @return this Maven for method chaining
     */
    public Maven viewDependencies() {
        operations.add(new ViewDependenciesOperation());
        return this;
    }

    /**
     * Formats the POM file by moving dependency versions to properties.
     *
     * @return this Maven for method chaining
     */
    public Maven formatDependencies() {
        operations.add(new FormatDependenciesOperation());
        return this;
    }

    /**
     * Updates all dependencies to their latest versions.
     *
     * @return this Maven for method chaining
     */
    public Maven updateDependenciesToLatest() {
        operations.add(new UpdateDependenciesOperation());
        return this;
    }

    /**
     * Inspects dependencies for version information and vulnerabilities.
     *
     * @return this Maven for method chaining
     */
    public Maven inspectDependencies() {
        operations.add(new InspectDependenciesOperation());
        return this;
    }

    // Callback operations

    /**
     * Adds a callback to be executed when models are available.
     *
     * @param callback the callback to execute
     * @return this Maven for method chaining
     */
    public Maven onModels(Consumer<List<Model>> callback) {
        operations.add(new CallbackOperation<>(
                result -> result.getModels(),
                callback
        ));
        return this;
    }

    /**
     * Adds a callback to be executed when dependencies are available.
     *
     * @param callback the callback to execute
     * @return this Maven for method chaining
     */
    public Maven onDependencies(Consumer<List<Dependency>> callback) {
        operations.add(new CallbackOperation<>(
                result -> result.getDependencies(),
                callback
        ));
        return this;
    }

    /**
     * Adds a callback to be executed when POM paths are available.
     *
     * @param callback the callback to execute
     * @return this Maven for method chaining
     */
    public Maven onPomPaths(Consumer<List<Path>> callback) {
        operations.add(new CallbackOperation<>(
                result -> result.getPomPaths(),
                callback
        ));
        return this;
    }

    // Terminal operations

    /**
     * Executes all queued operations and returns the result.
     *
     * @return the result containing all operation outputs
     */
    public MavenResult execute() {
        MavenResult result = new MavenResult();

        for (MavenOperation operation : operations) {
            try {
                operation.execute(context, result, moduleExecutor, dependencyExecutor);
            } catch (Exception e) {
                log.error("Failed to execute operation: {}", operation.getClass().getSimpleName(), e);
                throw new MavenExecutionException("Operation failed: " + operation.getClass().getSimpleName(), e);
            }
        }

        return result;
    }

    /**
     * Executes operations and extracts a specific result.
     *
     * @param extractor function to extract the desired result
     * @param <T> the type of the result
     * @return the extracted result
     */
    public <T> T execute(Function<MavenResult, T> extractor) {
        return extractor.apply(execute());
    }

    /**
     * Executes operations and returns models directly.
     *
     * @return list of Maven models
     */
    public List<Model> executeForModels() {
        return execute(MavenResult::getModels);
    }

    /**
     * Executes operations and returns POM paths directly.
     *
     * @return list of POM paths
     */
    public List<Path> executeForPomPaths() {
        return execute(MavenResult::getPomPaths);
    }

    /**
     * Executes operations and returns dependencies directly.
     *
     * @return list of dependencies
     */
    public List<Dependency> executeForDependencies() {
        return execute(MavenResult::getDependencies);
    }

    // Operation implementations

    private interface MavenOperation {
        void execute(MavenContext context, MavenResult result,
                     ModuleExecutor moduleExecutor, DependencyExecutor dependencyExecutor);
    }

    private static class FindModelsOperation implements MavenOperation {
        @Override
        public void execute(MavenContext context, MavenResult result,
                            ModuleExecutor moduleExecutor, DependencyExecutor dependencyExecutor) {
            List<Model> models = context.executeOnPaths(
                    path -> moduleExecutor.findModels(path),
                    paths -> moduleExecutor.findModels(paths)
            );
            result.setModels(models);
        }
    }

    private static class FindPomsOperation implements MavenOperation {
        @Override
        public void execute(MavenContext context, MavenResult result,
                            ModuleExecutor moduleExecutor, DependencyExecutor dependencyExecutor) {
            if (context.hasPath()) {
                List<Path> poms = moduleExecutor.findPoms(context.getPath());
                result.setPomPaths(poms);
            }
        }
    }

    private static class MapModelsOperation implements MavenOperation {
        @Override
        public void execute(MavenContext context, MavenResult result,
                            ModuleExecutor moduleExecutor, DependencyExecutor dependencyExecutor) {
            if (context.hasPath()) {
                Map<Path, Map<Model, Model>> mappedModels = moduleExecutor.mappedModels(context.getPath());
                result.setMappedModels(mappedModels);
            }
        }
    }

    private static class ParseDependenciesOperation implements MavenOperation {
        @Override
        public void execute(MavenContext context, MavenResult result,
                            ModuleExecutor moduleExecutor, DependencyExecutor dependencyExecutor) {
            if (context.hasPath()) {
                List<Dependency> dependencies = dependencyExecutor.parseDependency(context.getPath());
                result.setDependencies(dependencies);
            }
        }
    }

    private static class ViewDependenciesOperation implements MavenOperation {
        @Override
        public void execute(MavenContext context, MavenResult result,
                            ModuleExecutor moduleExecutor, DependencyExecutor dependencyExecutor) {
            List<Dependency> dependencies = context.hasDependencies()
                    ? context.getDependencies()
                    : result.getDependencies();

            if (dependencies != null && !dependencies.isEmpty()) {
                List<String> formatted = dependencyExecutor.viewDependency(dependencies);
                result.setFormattedDependencies(formatted);
            }
        }
    }

    private static class FormatDependenciesOperation implements MavenOperation {
        @Override
        public void execute(MavenContext context, MavenResult result,
                            ModuleExecutor moduleExecutor, DependencyExecutor dependencyExecutor) {
            if (context.hasPath()) {
                dependencyExecutor.formatDependency(context.getPath());
            }
        }
    }

    private static class UpdateDependenciesOperation implements MavenOperation {
        @Override
        public void execute(MavenContext context, MavenResult result,
                            ModuleExecutor moduleExecutor, DependencyExecutor dependencyExecutor) {
            if (context.hasPath()) {
                dependencyExecutor.updateDependencyToLatestVersion(context.getPath());
            }
        }
    }

    private static class InspectDependenciesOperation implements MavenOperation {
        @Override
        public void execute(MavenContext context, MavenResult result,
                            ModuleExecutor moduleExecutor, DependencyExecutor dependencyExecutor) {
            List<Dependency> dependencies = context.hasDependencies()
                    ? context.getDependencies()
                    : result.getDependencies();

            if (dependencies != null && !dependencies.isEmpty()) {
                List<InspectDependencyResult> results;
                if (dependencies.size() == 1) {
                    InspectDependencyResult singleResult = dependencyExecutor.inspectDependency(dependencies.get(0));
                    results = singleResult != null ? List.of(singleResult) : List.of();
                } else {
                    results = dependencyExecutor.inspectDependency(dependencies);
                }
                result.setInspectionResults(results);
            }
        }
    }

    private static class CallbackOperation<T> implements MavenOperation {
        private final Function<MavenResult, T> extractor;
        private final Consumer<T> callback;

        public CallbackOperation(Function<MavenResult, T> extractor, Consumer<T> callback) {
            this.extractor = extractor;
            this.callback = callback;
        }

        @Override
        public void execute(MavenContext context, MavenResult result,
                            ModuleExecutor moduleExecutor, DependencyExecutor dependencyExecutor) {
            T value = extractor.apply(result);
            if (value != null) {
                callback.accept(value);
            }
        }
    }
}


/**
 * Context object that holds the input data for Maven operations.
 *
 * <p>This class encapsulates the different types of inputs that can be provided
 * to the Maven fluent API, ensuring type safety and proper state management.</p>
 *
 * @author Joshua Salcedo
 * @since 1.0.0
 */
class MavenContext {

    private final ContextType type;
    private final Path path;
    private final List<Path> paths;
    private final List<Dependency> dependencies;

    private MavenContext(ContextType type, Path path, List<Path> paths, List<Dependency> dependencies) {
        this.type = type;
        this.path = path;
        this.paths = paths;
        this.dependencies = dependencies;
    }

    // Factory methods

    static MavenContext forPath(Path path) {
        return new MavenContext(ContextType.SINGLE_PATH, path, null, null);
    }

    static MavenContext forPaths(List<Path> paths) {
        return new MavenContext(ContextType.MULTIPLE_PATHS, null, new ArrayList<>(paths), null);
    }

    static MavenContext forDependencies(List<Dependency> dependencies) {
        return new MavenContext(ContextType.DEPENDENCIES, null, null, new ArrayList<>(dependencies));
    }

    static MavenContext forDependency(Dependency dependency) {
        List<Dependency> deps = new ArrayList<>();
        deps.add(dependency);
        return new MavenContext(ContextType.DEPENDENCIES, null, null, deps);
    }

    // Query methods

    boolean hasPath() {
        return type == ContextType.SINGLE_PATH && path != null;
    }

    boolean hasPaths() {
        return type == ContextType.MULTIPLE_PATHS && paths != null && !paths.isEmpty();
    }

    boolean hasDependencies() {
        return type == ContextType.DEPENDENCIES && dependencies != null && !dependencies.isEmpty();
    }

    Path getPath() {
        return path;
    }

    List<Path> getPaths() {
        return paths != null ? new ArrayList<>(paths) : List.of();
    }

    List<Dependency> getDependencies() {
        return dependencies != null ? new ArrayList<>(dependencies) : List.of();
    }

    /**
     * Executes different functions based on whether we have a single path or multiple paths.
     *
     * @param singlePathFunction function to execute for single path
     * @param multiplePathsFunction function to execute for multiple paths
     * @param <T> the return type
     * @return the result of the appropriate function
     */
    <T> T executeOnPaths(Function<Path, T> singlePathFunction,
                         Function<List<Path>, T> multiplePathsFunction) {
        if (hasPath()) {
            return singlePathFunction.apply(path);
        } else if (hasPaths()) {
            return multiplePathsFunction.apply(paths);
        }
        return null;
    }

    private enum ContextType {
        SINGLE_PATH,
        MULTIPLE_PATHS,
        DEPENDENCIES
    }
}
