# Joshua Salcedo Library Parent

This is the parent POM for all Joshua Salcedo library modules. It provides:

1. Consistent dependency management for all library modules
2. Common build configuration
3. Centralized version management

## Library Modules

The following library modules are available:

- `library-core`: Core library functionality
- `library-domain`: Domain models and entities
- `library-io-core`: I/O utilities library
- `library-maven-core`: Maven-related utilities

## Using the Library

There are two ways to use the Joshua Salcedo libraries in your project:

### Option 1: Use as Parent POM

```xml
<parent>
    <groupId>io.joshuasalcedo.library</groupId>
    <artifactId>joshuasalcedo-library-parent</artifactId>
    <version>1.3.2-SNAPSHOT</version>
</parent>

<dependencies>
    <!-- Use any library module without specifying a version -->
    <dependency>
        <groupId>io.joshuasalcedo.library</groupId>
        <artifactId>library-core</artifactId>
    </dependency>
</dependencies>
```

### Option 2: Import in dependencyManagement

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.joshuasalcedo.library</groupId>
            <artifactId>joshuasalcedo-library-parent</artifactId>
            <version>1.3.2-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Use any library module without specifying a version -->
    <dependency>
        <groupId>io.joshuasalcedo.library</groupId>
        <artifactId>library-core</artifactId>
    </dependency>
</dependencies>
```

## Benefits

- **Consistent Versions**: All library modules will have consistent versions
- **Simplified Dependency Management**: No need to specify versions for library modules
- **Transitive Dependencies**: When you import `library-core`, it will automatically include its dependencies
- **Third-Party Dependency Management**: Common third-party dependencies are managed through the `library-bom`

## Example

See the `example-usage.xml` file for a complete example of how to use the library modules in your project.