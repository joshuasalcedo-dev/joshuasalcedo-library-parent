/**
 * Maven Executor Exception Classes - Custom exceptions for Maven operations.
 * 
 * <p>This package contains all custom exception classes used by the Maven Executor
 * library. These exceptions provide specific error information for various failure
 * scenarios that can occur during Maven operations.</p>
 * 
 * 
 * <h2>Exception Hierarchy:</h2>
 * <ul>
 *   <li>{@link io.joshuasalcedo.library.maven.core.exception.ModelExecutorException} -
 *       Base exception for general model execution errors</li>
 *   <li>{@link io.joshuasalcedo.library.maven.core.exception.DependencyParseException} -
 *       Thrown when POM file parsing fails</li>
 *   <li>{@link io.joshuasalcedo.library.maven.core.exception.PomSaveException} -
 *       Thrown when saving POM file fails</li>
 *   <li>{@link io.joshuasalcedo.library.maven.core.exception.RepositoryAccessException} -
 *       Thrown when repository access fails</li>
 *   <li>{@link io.joshuasalcedo.library.maven.core.exception.VersionResolutionException} -
 *       Thrown when version resolution fails</li>
 *   <li>{@link io.joshuasalcedo.library.maven.core.exception.VersionUpdateException} -
 *       Thrown when version update fails</li>
 * </ul>
 * 
 * <h2>Exception Design:</h2>
 * <p>All exceptions in this package:</p>
 * <ul>
 *   <li>Extend from standard Java exception classes</li>
 *   <li>Provide detailed error messages with context</li>
 *   <li>Include cause exceptions when applicable</li>
 *   <li>Follow consistent naming convention ending with "Exception"</li>
 * </ul>
 * 
 * <h2>Usage Guidelines:</h2>
 * <ul>
 *   <li>Catch specific exceptions for targeted error handling</li>
 *   <li>Use exception messages to provide meaningful feedback to users</li>
 *   <li>Check cause exceptions for underlying error details</li>
 * </ul>
 * 
 * <h2>Example:</h2>
 * <pre>{@code
 * try {
 *     maven.updateDependencyVersion("com.example", "my-lib", "2.0.0");
 * } catch (VersionUpdateException e) {
 *     logger.error("Failed to update version: " + e.getMessage());
 *     // Handle specific version update failure
 * } catch (DependencyParseException e) {
 *     logger.error("Invalid POM file: " + e.getMessage());
 *     // Handle POM parsing failure
 * }
 * }</pre>
 * 
 * @since 1.0.0
 * @author Joshua Salcedo
 */
package io.joshuasalcedo.library.maven.core.exception;