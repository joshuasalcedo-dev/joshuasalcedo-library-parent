package io.joshuasalcedo.library.io.core.find;

/**
 * Factory class for creating instances of the Finder implementation.
 * 
 * <p>This factory provides access to the package-private FinderImpl class
 * from outside the package while keeping the implementation details hidden.</p>
 *
 * @author Joshua Salcedo
 */
 class FinderFactory {
    
    /**
     * Creates a new instance of the Finder implementation.
     *
     * @return a new Finder instance
     */
    public static Finder createFinder() {
        return new FinderImpl();
    }
    
    // Private constructor to prevent instantiation
    private FinderFactory() {
        throw new AssertionError("FinderFactory is not meant to be instantiated");
    }
}