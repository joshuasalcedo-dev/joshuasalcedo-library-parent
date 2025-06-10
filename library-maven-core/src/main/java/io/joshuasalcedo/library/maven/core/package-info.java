/**
 * Maven Executor API - Core interfaces and implementations for Maven operations.
 * 
 * <p>This package contains the primary API interfaces and their implementations
 * that form the foundation of the Maven Executor library. It provides a clean,
 * fluent interface for performing various Maven-related operations programmatically.</p>
 * 
 * <h2>Core Interfaces:</h2>
 * <ul>
 *   <li>{@link io.joshuasalcedo.library.maven.core.impl.Maven} - The main entry
 *       point providing a fluent builder API for configuring and executing Maven
 *       operations</li>
 *   <li>{@link io.joshuasalcedo.library.maven.core.DependencyExecutor} -
 *       Interface for dependency management operations including inspection,
 *       addition, removal, and version updates</li>
 *   <li>{@link io.joshuasalcedo.library.maven.core.ModuleExecutor} -
 *       Interface for module discovery and loading in multi-module Maven projects</li>
 * </ul>
 * 
 * <h2>Implementations:</h2>
 * <ul>
 *   <li>{@link io.joshuasalcedo.library.maven.core.impl.DependencyExecutorImpl} -
 *       Default implementation of DependencyExecutor</li>
 *   <li>{@link io.joshuasalcedo.library.maven.core.impl.ModuleExecutorImpl} -
 *       Default implementation of ModuleExecutor</li>
 * </ul>
 * 
 * <h2>Design Patterns:</h2>
 * <p>The API follows several design patterns to ensure ease of use and flexibility:</p>
 * <ul>
 *   <li><strong>Fluent Interface</strong> - Methods return the instance to allow
 *       method chaining</li>
 *   <li><strong>Builder Pattern</strong> - Maven class uses builder pattern for
 *       configuration</li>
 *   <li><strong>Strategy Pattern</strong> - Different executors for different
 *       operation types</li>
 * </ul>
 * 
 * <h2>Thread Safety:</h2>
 * <p>The API implementations are not thread-safe. Each thread should use its own
 * instance of the Maven executor.</p>
 * 
 * @since 1.0.0
 * @author Joshua Salcedo
 */
package io.joshuasalcedo.library.maven.core;