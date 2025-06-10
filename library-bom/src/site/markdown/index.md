# Joshua Salcedo Library BOM

Welcome to the Joshua Salcedo Library Bill of Materials (BOM) documentation. This module provides centralized dependency management for all Joshua Salcedo library projects.

## Overview

The Library BOM (Bill of Materials) is a special POM that provides a curated set of compatible library versions. By importing this BOM, projects can ensure they use a consistent and tested set of dependencies without having to specify individual versions.

## Purpose

The Library BOM serves several critical functions:

- **Version Management**: Centralizes version specifications for all dependencies
- **Compatibility Assurance**: Ensures all included dependencies work well together
- **Simplified Updates**: Update all related dependencies by changing a single version
- **Reduced Conflicts**: Minimizes dependency version conflicts across modules

## Usage

### Import the BOM

Add the following to your project's dependency management section:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.joshuasalcedo.library</groupId>
            <artifactId>library-bom</artifactId>
            <version>${joshuasalcedo.library.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Use Dependencies Without Versions

Once the BOM is imported, you can declare dependencies without specifying versions:

```xml
<dependencies>
    <dependency>
        <groupId>io.joshuasalcedo.library</groupId>
        <artifactId>library-core</artifactId>
    </dependency>
    <dependency>
        <groupId>io.joshuasalcedo.library</groupId>
        <artifactId>library-api</artifactId>
    </dependency>
</dependencies>
```

## Included Dependencies

The BOM manages versions for:

### Core Libraries
- `library-api` - Core API interfaces
- `library-core` - Core implementations
- `library-io-parent` - I/O utilities
- `library-maven-parent` - Maven plugin utilities

### Third-Party Dependencies
- **Spring Framework**: Core, Context, Web
- **Jackson**: Databind, Core, Annotations
- **SLF4J**: API and implementations
- **JUnit**: Jupiter for testing
- **Mockito**: For mocking in tests

## Version Alignment

All versions in the BOM are carefully selected to ensure:

1. **Compatibility**: All libraries work together without conflicts
2. **Stability**: Only stable, production-ready versions are included
3. **Security**: Regular updates for security patches
4. **Performance**: Versions are tested for performance regressions

## Benefits

### For Developers
- No need to remember or look up version numbers
- Reduced dependency conflicts
- Simplified dependency upgrades
- Consistent behavior across projects

### For Projects
- Centralized dependency governance
- Easier maintenance and updates
- Reduced build complexity
- Better reproducibility

## Best Practices

1. **Always use the BOM** for Joshua Salcedo library dependencies
2. **Avoid overriding versions** unless absolutely necessary
3. **Update the BOM version** regularly to get security updates
4. **Test thoroughly** when upgrading the BOM version

## Version Matrix

| BOM Version | Java Version | Spring Version | Jackson Version |
|-------------|--------------|----------------|-----------------|
| 1.1.0       | 21+          | 6.1.x          | 2.16.x          |
| 1.0.0       | 17+          | 6.0.x          | 2.15.x          |

## Migration Guide

### From Individual Dependencies to BOM

1. Remove version specifications from your dependencies
2. Add the BOM import to dependency management
3. Clean and rebuild your project
4. Run tests to ensure compatibility

### Upgrading BOM Version

1. Update the BOM version in your POM
2. Run `mvn dependency:tree` to check for conflicts
3. Test your application thoroughly
4. Address any breaking changes

## Troubleshooting

### Dependency Conflicts

If you encounter conflicts:

1. Use `mvn dependency:tree` to identify the conflict
2. Check if you're overriding any versions
3. Ensure you're using the latest BOM version
4. Report persistent issues on GitHub

### Version Override

If you must override a version:

```xml
<properties>
    <jackson.version>2.16.1</jackson.version>
</properties>
```

## Support

For questions or issues:

- **GitHub Issues**: [Report an issue](https://github.com/joshuasalcedo/joshuasalcedo-parent/issues)
- **Documentation**: [Dependency Management](dependency-management.html)
- **Version History**: [Release Notes](release-notes.html)

## License

This module is licensed under the Apache License 2.0. See the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0.txt) file for details.