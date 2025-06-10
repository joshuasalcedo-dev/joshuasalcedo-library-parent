# Joshua Salcedo Library Maven Parent

Welcome to the Joshua Salcedo Library Maven Parent documentation. This module provides Maven plugin utilities, build tooling, and standardized configurations for Maven-based projects.

## Overview

The Library Maven Parent module serves as a foundation for Maven-based builds in the Joshua Salcedo ecosystem. It provides custom Maven plugins, shared configurations, build profiles, and utilities that ensure consistent and efficient builds across all projects.

## Key Features

### Build Configuration
- **Standardized POM Structure**: Consistent project configuration
- **Plugin Management**: Pre-configured plugin versions and settings
- **Dependency Management**: Centralized dependency versioning
- **Profile Management**: Environment-specific build profiles

### Custom Maven Plugins
- **Code Generation**: Automated code generation from templates
- **Documentation**: Enhanced documentation generation
- **Validation**: Project structure and configuration validation
- **Deployment**: Simplified deployment procedures

### Build Optimization
- **Parallel Builds**: Optimized for multi-core systems
- **Incremental Compilation**: Faster rebuilds
- **Caching**: Intelligent dependency caching
- **Resource Filtering**: Efficient resource processing

### Quality Assurance
- **Code Analysis**: Integrated static analysis tools
- **Test Coverage**: Coverage reporting and enforcement
- **License Checking**: Automated license compliance
- **Security Scanning**: Dependency vulnerability checks

## Getting Started

### Parent POM Configuration

Use as a parent POM in your project:

```xml
<parent>
    <groupId>io.joshuasalcedo.library</groupId>
    <artifactId>library-maven-parent</artifactId>
    <version>${project.version}</version>
</parent>
```

### Inheriting Configuration

Projects inheriting from this parent automatically get:

- Optimized compiler settings
- Standard plugin configurations
- Common dependency versions
- Build profiles for different environments

## Build Profiles

### Development Profile

```bash
mvn clean install -Pdevelopment
```

Features:
- Fast builds with minimal checks
- Skip lengthy validations
- Enable debugging options
- Hot reload support

### Production Profile

```bash
mvn clean install -Pproduction
```

Features:
- Full optimization enabled
- All validations active
- Minification and compression
- License headers verification

### Release Profile

```bash
mvn clean deploy -Prelease
```

Features:
- Source and JavaDoc generation
- GPG signing
- Version validation
- Distribution packaging

## Plugin Configuration

### Compiler Plugin

Pre-configured for optimal performance:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <release>24</release>
        <parameters>true</parameters>
        <compilerArgs>
            <arg>-Xlint:all</arg>
            <arg>-parameters</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

### Surefire Plugin

Test execution configuration:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
        <forkCount>1C</forkCount>
        <reuseForks>true</reuseForks>
    </configuration>
</plugin>
```

### Site Plugin

Documentation generation:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-site-plugin</artifactId>
    <configuration>
        <locales>en</locales>
        <outputDirectory>${project.build.directory}/site</outputDirectory>
    </configuration>
</plugin>
```

## Custom Goals

### Code Generation

Generate boilerplate code:

```bash
mvn joshuasalcedo:generate -Dtype=service -Dname=UserService
```

### Project Validation

Validate project structure:

```bash
mvn joshuasalcedo:validate
```

### Dependency Analysis

Analyze dependencies:

```bash
mvn joshuasalcedo:analyze-deps
```

## Build Properties

Commonly used properties:

```xml
<properties>
    <!-- Java Version -->
    <java.version>24</java.version>
    <maven.compiler.source>${java.version}</maven.compiler.source>
    <maven.compiler.target>${java.version}</maven.compiler.target>
    
    <!-- Encoding -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    
    <!-- Plugin Versions -->
    <maven-compiler-plugin.version>3.12.1</maven-compiler-plugin.version>
    <maven-surefire-plugin.version>3.2.5</maven-surefire-plugin.version>
    <maven-site-plugin.version>3.12.1</maven-site-plugin.version>
    
    <!-- Testing -->
    <junit.version>5.10.1</junit.version>
    <mockito.version>5.8.0</mockito.version>
    
    <!-- Build Settings -->
    <skipTests>false</skipTests>
    <maven.build.timestamp.format>yyyy-MM-dd HH:mm:ss</maven.build.timestamp.format>
</properties>
```

## Dependency Management

Pre-configured dependency versions:

```xml
<dependencyManagement>
    <dependencies>
        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        
        <!-- Logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
        </dependency>
        
        <!-- Utilities -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>${commons-lang3.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## Build Optimization Tips

### Parallel Builds

Enable parallel builds for multi-module projects:

```bash
mvn -T 1C clean install
```

### Offline Mode

Build without checking for updates:

```bash
mvn -o clean install
```

### Skip Tests

For faster builds during development:

```bash
mvn clean install -DskipTests
```

### Incremental Builds

Use the incremental build feature:

```bash
mvn compile -Dmaven.compiler.useIncrementalCompilation=true
```

## Reporting

Generate comprehensive project reports:

```bash
mvn site
```

Available reports:
- Project Information
- Dependencies
- Plugins
- License
- Source Code Cross Reference
- JavaDoc
- Test Results
- Code Coverage

## Continuous Integration

### GitHub Actions

Pre-configured workflow:

```yaml
name: Build
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '24'
      - run: mvn clean verify
```

### Jenkins

Jenkinsfile template:

```groovy
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }
    }
}
```

## Troubleshooting

### Common Issues

1. **Dependency Conflicts**: Use `mvn dependency:tree` to analyze
2. **Build Failures**: Check with `mvn -X` for debug output
3. **Plugin Issues**: Verify plugin versions and compatibility
4. **Memory Issues**: Increase heap with `MAVEN_OPTS="-Xmx2g"`

## Best Practices

1. **Inherit Don't Override**: Use parent configurations when possible
2. **Profile Usage**: Use profiles for environment-specific settings
3. **Version Properties**: Define versions as properties
4. **Clean Builds**: Regularly perform clean builds
5. **Update Dependencies**: Keep dependencies current

## Documentation

- [Maven Plugin Documentation](plugins.html)
- [Build Profiles Guide](profiles.html)
- [CI/CD Integration](ci-cd.html)
- [Troubleshooting Guide](troubleshooting.html)

## Support

For questions or issues:

- **GitHub Issues**: [Report an issue](https://github.com/joshuasalcedo/joshuasalcedo-parent/issues)
- **Maven Documentation**: [Official Maven Docs](https://maven.apache.org)
- **Stack Overflow**: Tag with `joshuasalcedo-maven`

## License

This module is licensed under the Apache License 2.0. See the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0.txt) file for details.