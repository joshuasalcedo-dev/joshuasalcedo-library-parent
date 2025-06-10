package io.joshuasalcedo.library.maven.core.impl;


import io.joshuasalcedo.library.io.core.find.Find;
import io.joshuasalcedo.library.maven.core.ModuleExecutor;
import io.joshuasalcedo.library.maven.core.exception.ModelExecutorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Enhanced implementation of the ModuleExecutor interface with parent POM resolution.
 *
 * <p>This class provides concrete implementations for module discovery operations
 * with full support for Maven inheritance. It resolves groupId and version from
 * parent POMs when they are not explicitly defined in child POMs.</p>
 *
 * @author Joshua Salcedo
 * @since 1.0.0
 */
public class ModuleExecutorImpl implements ModuleExecutor {

    private static final String POM_FILE_NAME = "pom.xml";
    private static final String PARENT_POM_PATH = "../pom.xml";

    private final Map<Path, Model> modelCache = new ConcurrentHashMap<>();
    private final Map<String, Model> parentCache = new ConcurrentHashMap<>();
    private final MavenXpp3Reader mavenReader = new MavenXpp3Reader();
    Logger log = LoggerFactory.getLogger(ModuleExecutorImpl.class);
    @Override
    public List<Model> findModels(Path path) {
        List<Path> pomPaths = findPoms(path);
        return loadAndResolveModels(pomPaths);
    }

    @Override
    public List<Path> findPoms(Path path) {
        return Find.in(path)
                .matching(POM_FILE_NAME)
                .execute();
    }

    @Override
    public List<Model> findModels(List<Path> paths) {
        return loadAndResolveModels(paths);
    }

    @Override
    public Map<Path, Map<Model, Model>> mappedModels(Path path) {
        List<Path> pomPaths = findPoms(path);
        Map<Path, Map<Model, Model>> mappedModels = new LinkedHashMap<>();

        for (Path pomPath : pomPaths) {
            try {
                Model originalModel = loadModel(pomPath);
                if (originalModel != null) {
                    Model resolvedModel = resolveInheritance(originalModel);
                    mappedModels.put(pomPath, createModelMapping(originalModel, resolvedModel));
                }
            } catch (Exception e) {
                log.error("Failed to process POM at: {}", pomPath, e);
                // Continue processing other POMs
            }
        }

        return mappedModels;
    }

    /**
     * Creates a mapping from original model to resolved model.
     *
     * @param originalModel The original model as loaded from file
     * @param resolvedModel The model with inheritance resolved
     * @return Map with single entry mapping original to resolved
     */
    private Map<Model, Model> createModelMapping(Model originalModel, Model resolvedModel) {
        Map<Model, Model> mapping = new LinkedHashMap<>(1);
        mapping.put(originalModel, resolvedModel);
        return mapping;
    }

    /**
     * Loads models from POM files and resolves their inheritance.
     *
     * @param pomPaths List of paths to POM files
     * @return List of fully resolved Maven models
     */
    private List<Model> loadAndResolveModels(List<Path> pomPaths) {
        return pomPaths.stream()
                .map(this::loadModel)
                .filter(Objects::nonNull)
                .map(this::resolveInheritance)
                .collect(Collectors.toList());
    }

    /**
     * Loads a Maven model from a POM file.
     *
     * @param pomPath Path to the POM file
     * @return Loaded model or null if loading fails
     */
    private Model loadModel(Path pomPath) {
        return modelCache.computeIfAbsent(pomPath, path -> {
            try (Reader reader = new FileReader(path.toFile())) {
                Model model = mavenReader.read(reader);
                model.setPomFile(path.toFile());
                return model;
            } catch (Exception e) {
                log.error("Failed to load POM from: {}", path, e);
                throw new ModelExecutorException(path);
            }
        });
    }

    /**
     * Resolves model inheritance by applying parent POM values.
     *
     * @param model The model to resolve
     * @return Model with inherited values from parent
     */
    private Model resolveInheritance(Model model) {
        if (hasCompleteCoordinates(model)) {
            return model;
        }

        Parent parent = model.getParent();
        if (parent == null) {
            return model;
        }

        Model parentModel = resolveParentModel(parent, model.getPomFile() != null ? model.getPomFile().toPath() : null);
        if (parentModel == null) {
            return model;
        }

        return inheritFromParent(model, parentModel);
    }

    /**
     * Checks if a model has complete Maven coordinates (GAV).
     *
     * @param model The model to check
     * @return true if groupId and version are present
     */
    private boolean hasCompleteCoordinates(Model model) {
        return model.getGroupId() != null && model.getVersion() != null;
    }

    /**
     * Resolves a parent model by its coordinates.
     *
     * @param parent Parent coordinates
     * @param childPomPath Path of the child POM
     * @return Resolved parent model or null
     */
    private Model resolveParentModel(Parent parent, Path childPomPath) {
        String gav = createGavKey(parent);

        Model cachedParent = parentCache.get(gav);
        if (cachedParent != null) {
            return cachedParent;
        }

        Model parentModel = findParentModel(parent, childPomPath);
        if (parentModel != null) {
            parentCache.put(gav, parentModel);
        }

        return parentModel;
    }

    /**
     * Creates a GAV (GroupId:ArtifactId:Version) key for caching.
     *
     * @param parent Parent coordinates
     * @return GAV string
     */
    private String createGavKey(Parent parent) {
        return String.format("%s:%s:%s",
                parent.getGroupId(),
                parent.getArtifactId(),
                parent.getVersion());
    }

    /**
     * Finds the parent model using various strategies.
     *
     * @param parent Parent coordinates
     * @param childPomPath Path of the child POM
     * @return Parent model or null
     */
    private Model findParentModel(Parent parent, Path childPomPath) {
        // Try relative path first
        Model parentModel = tryRelativePath(parent, childPomPath);
        if (parentModel != null) {
            return parentModel;
        }

        // Try default parent location
        return tryDefaultParentLocation(parent, childPomPath);
    }

    /**
     * Attempts to load parent POM from relative path.
     *
     * @param parent Parent coordinates
     * @param childPomPath Path of the child POM
     * @return Parent model or null
     */
    private Model tryRelativePath(Parent parent, Path childPomPath) {
        String relativePath = parent.getRelativePath();
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        Path parentPath = resolveParentPath(childPomPath, relativePath);
        if (!parentPath.toFile().exists()) {
            return null;
        }

        Model candidate = loadModel(parentPath);
        return matchesParentCoordinates(candidate, parent) ? candidate : null;
    }

    /**
     * Resolves the parent POM path from relative path.
     *
     * @param childPomPath Child POM path
     * @param relativePath Relative path to parent
     * @return Resolved parent path
     */
    private Path resolveParentPath(Path childPomPath, String relativePath) {
        Path parentPath = childPomPath.getParent().resolve(relativePath);
        if (!parentPath.endsWith(POM_FILE_NAME)) {
            parentPath = parentPath.resolve(POM_FILE_NAME);
        }
        return parentPath;
    }

    /**
     * Attempts to load parent POM from default location (../pom.xml).
     *
     * @param parent Parent coordinates
     * @param childPomPath Path of the child POM
     * @return Parent model or null
     */
    private Model tryDefaultParentLocation(Parent parent, Path childPomPath) {
        Path parentPath = childPomPath.getParent().getParent().resolve(POM_FILE_NAME);
        if (!parentPath.toFile().exists()) {
            return null;
        }

        Model candidate = loadModel(parentPath);
        return matchesParentCoordinates(candidate, parent) ? candidate : null;
    }

    /**
     * Checks if a model matches the specified parent coordinates.
     *
     * @param model Model to check
     * @param parent Parent coordinates
     * @return true if coordinates match
     */
    private boolean matchesParentCoordinates(Model model, Parent parent) {
        if (model == null) {
            return false;
        }

        return Objects.equals(model.getGroupId(), parent.getGroupId()) &&
                Objects.equals(model.getArtifactId(), parent.getArtifactId()) &&
                Objects.equals(model.getVersion(), parent.getVersion());
    }

    /**
     * Creates a new model with values inherited from parent.
     *
     * @param child Child model
     * @param parent Parent model
     * @return Model with inherited values
     */
    private Model inheritFromParent(Model child, Model parent) {
        // Clone the child model to avoid modifying the original
        Model result = child.clone();

        if (result.getGroupId() == null && parent.getGroupId() != null) {
            result.setGroupId(parent.getGroupId());
        }

        if (result.getVersion() == null && parent.getVersion() != null) {
            result.setVersion(parent.getVersion());
        }

        // Additional inheritance could be added here (e.g., properties, dependencies)

        return result;
    }
}