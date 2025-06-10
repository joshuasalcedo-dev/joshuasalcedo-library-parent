package io.joshuasalcedo.library.maven.core.model;

import lombok.Data;
import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Result container for Maven operations.
 * 
 * <p>This class holds all possible results from Maven operations, allowing
 * the fluent API to accumulate results as operations are executed.</p>
 * 
 * @author Joshua Salcedo
 * @since 1.0.0
 */
@Data
public class MavenResult {

    private List<Model> models = new ArrayList<>();
    private List<Path> pomPaths = new ArrayList<>();
    private List<Dependency> dependencies = new ArrayList<>();
    private List<String> formattedDependencies = new ArrayList<>();
    private List<InspectDependencyResult> inspectionResults = new ArrayList<>();
    private Map<Path, Map<Model, Model>> mappedModels = new LinkedHashMap<>();

    /**
     * Gets the list of models.
     * 
     * @return the list of models
     */
    public List<Model> getModels() {
        return models;
    }

    /**
     * Sets the list of models.
     * 
     * @param models the list of models to set
     */
    public void setModels(List<Model> models) {
        this.models = models;
    }

    /**
     * Gets the list of POM paths.
     * 
     * @return the list of POM paths
     */
    public List<Path> getPomPaths() {
        return pomPaths;
    }

    /**
     * Sets the list of POM paths.
     * 
     * @param pomPaths the list of POM paths to set
     */
    public void setPomPaths(List<Path> pomPaths) {
        this.pomPaths = pomPaths;
    }

    /**
     * Gets the list of dependencies.
     * 
     * @return the list of dependencies
     */
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    /**
     * Sets the list of dependencies.
     * 
     * @param dependencies the list of dependencies to set
     */
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * Gets the list of formatted dependencies.
     * 
     * @return the list of formatted dependencies
     */
    public List<String> getFormattedDependencies() {
        return formattedDependencies;
    }

    /**
     * Sets the list of formatted dependencies.
     * 
     * @param formattedDependencies the list of formatted dependencies to set
     */
    public void setFormattedDependencies(List<String> formattedDependencies) {
        this.formattedDependencies = formattedDependencies;
    }

    /**
     * Gets the list of inspection results.
     * 
     * @return the list of inspection results
     */
    public List<InspectDependencyResult> getInspectionResults() {
        return inspectionResults;
    }

    /**
     * Sets the list of inspection results.
     * 
     * @param inspectionResults the list of inspection results to set
     */
    public void setInspectionResults(List<InspectDependencyResult> inspectionResults) {
        this.inspectionResults = inspectionResults;
    }

    /**
     * Gets the map of mapped models.
     * 
     * @return the map of mapped models
     */
    public Map<Path, Map<Model, Model>> getMappedModels() {
        return mappedModels;
    }

    /**
     * Sets the map of mapped models.
     * 
     * @param mappedModels the map of mapped models to set
     */
    public void setMappedModels(Map<Path, Map<Model, Model>> mappedModels) {
        this.mappedModels = mappedModels;
    }

    /**
     * Checks if any models were found.
     * 
     * @return true if models exist
     */
    public boolean hasModels() {
        return !models.isEmpty();
    }

    /**
     * Checks if any POM paths were found.
     * 
     * @return true if POM paths exist
     */
    public boolean hasPomPaths() {
        return !pomPaths.isEmpty();
    }

    /**
     * Checks if any dependencies were found.
     * 
     * @return true if dependencies exist
     */
    public boolean hasDependencies() {
        return !dependencies.isEmpty();
    }

    /**
     * Checks if any formatted dependencies exist.
     * 
     * @return true if formatted dependencies exist
     */
    public boolean hasFormattedDependencies() {
        return !formattedDependencies.isEmpty();
    }

    /**
     * Checks if any inspection results exist.
     * 
     * @return true if inspection results exist
     */
    public boolean hasInspectionResults() {
        return !inspectionResults.isEmpty();
    }

    /**
     * Checks if any mapped models exist.
     * 
     * @return true if mapped models exist
     */
    public boolean hasMappedModels() {
        return !mappedModels.isEmpty();
    }

    /**
     * Gets the first model if available.
     * 
     * @return the first model or null
     */
    public Model getFirstModel() {
        return models.isEmpty() ? null : models.get(0);
    }

    /**
     * Gets the first dependency if available.
     * 
     * @return the first dependency or null
     */
    public Dependency getFirstDependency() {
        return dependencies.isEmpty() ? null : dependencies.get(0);
    }

    /**
     * Merges another result into this one.
     * 
     * @param other the result to merge
     */
    public void merge(MavenResult other) {
        if (other == null) return;

        models.addAll(other.getModels());
        pomPaths.addAll(other.getPomPaths());
        dependencies.addAll(other.getDependencies());
        formattedDependencies.addAll(other.getFormattedDependencies());
        inspectionResults.addAll(other.getInspectionResults());
        mappedModels.putAll(other.getMappedModels());
    }

    /**
     * Clears all results.
     */
    public void clear() {
        models.clear();
        pomPaths.clear();
        dependencies.clear();
        formattedDependencies.clear();
        inspectionResults.clear();
        mappedModels.clear();
    }

    /**
     * Creates a summary of the results.
     * 
     * @return a summary string
     */
    public String summary() {
        return String.format(
            "MavenResult[models=%d, pomPaths=%d, dependencies=%d, formatted=%d, inspections=%d, mapped=%d]",
            models.size(),
            pomPaths.size(),
            dependencies.size(),
            formattedDependencies.size(),
            inspectionResults.size(),
            mappedModels.size()
        );
    }
}
