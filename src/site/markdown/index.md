# Joshua Salcedo Library Parent

Welcome to the Joshua Salcedo Library Parent project documentation. This is the parent project for all Joshua Salcedo library modules, providing a comprehensive suite of Java libraries for modern application development.

## Overview

The Joshua Salcedo Library Parent project is a multi-module Maven project that serves as the foundation for a collection of high-quality, reusable Java libraries. It provides consistent configuration, dependency management, and build processes across all library modules.

## Project Structure

The library parent project consists of five main modules:

### Core Modules

1. **[Library API](library-api/index.html)** - Core interfaces and contracts
2. **[Library BOM](library-bom/index.html)** - Bill of Materials for dependency management
3. **[Library Core](library-core/index.html)** - Core implementations and utilities
4. **[Library IO Parent](library-io-parent/index.html)** - I/O utilities and abstractions
5. **[Library Maven Parent](library-maven-parent/index.html)** - Maven build utilities

## Features

### Consistent Architecture
- Unified API design across all modules
- Standardized coding conventions
- Common exception hierarchy
- Shared utility classes

### Modern Java Support
- Java 24+ compatibility
- Module system support
- Record types and pattern matching
- Virtual threads support

### Quality Assurance
- Comprehensive test coverage
- Static code analysis
- Security vulnerability scanning
- Performance benchmarks

### Developer Experience
- Clear, comprehensive documentation
- Extensive JavaDoc coverage
- Example code and tutorials
- Active community support

## Getting Started

### Prerequisites

- Java 24 or higher
- Maven 3.8 or higher
- Git (for source code access)

### Building from Source

```bash
# Clone the repository
git clone https://github.com/joshuasalcedo/joshuasalcedo-parent.git

# Navigate to the library parent directory
cd joshuasalcedo-parent/joshuasalcedo-library-parent

# Build all modules
mvn clean install

# Generate documentation
mvn site
```

### Using the Libraries

Add the BOM to your project's dependency management:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.joshuasalcedo.library</groupId>
            <artifactId>library-bom</artifactId>
            <version>1.1.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Then add specific modules as needed:

```xml
<dependencies>
    <dependency>
        <groupId>io.joshuasalcedo.library</groupId>
        <artifactId>library-core</artifactId>
    </dependency>
    <dependency>
        <groupId>io.joshuasalcedo.library</groupId>
        <artifactId>library-io-parent</artifactId>
    </dependency>
</dependencies>
```

## Module Overview

### Library API
Defines the core interfaces and contracts that all other modules implement. This ensures consistency and allows for easy extension and customization.

### Library BOM
Provides centralized dependency management, ensuring all modules use compatible versions of third-party libraries.

### Library Core
Contains the fundamental implementations of the API interfaces, including utilities for:
- String manipulation
- Collection operations
- Configuration management
- Resource handling

### Library IO Parent
Extends Java's I/O capabilities with:
- Enhanced file operations
- Stream processing utilities
- Serialization framework
- Network I/O abstractions

### Library Maven Parent
Provides Maven-specific utilities and configurations for:
- Build optimization
- Plugin management
- Custom Maven goals
- CI/CD integration

## Architecture Principles

1. **Modularity**: Each module has a specific purpose and minimal dependencies
2. **Extensibility**: Easy to extend without modifying core code
3. **Performance**: Optimized for both development and runtime efficiency
4. **Testability**: Designed with testing in mind, including test utilities
5. **Documentation**: Comprehensive documentation at all levels

## Development Guidelines

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Write self-documenting code
- Add JavaDoc for all public APIs

### Testing
- Write unit tests for all new functionality
- Maintain minimum 80% code coverage
- Include integration tests where appropriate
- Use the provided test utilities

### Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## Version Strategy

The project follows semantic versioning:

- **Major version**: Breaking API changes
- **Minor version**: New features, backward compatible
- **Patch version**: Bug fixes, backward compatible

Current version: 1.1.0-SNAPSHOT

## Roadmap

### Upcoming Features
- Enhanced async/reactive support
- Additional serialization formats
- Cloud-native utilities
- Performance monitoring tools

### Future Modules
- Library Security - Security utilities
- Library Data - Data access abstractions
- Library Web - Web framework utilities
- Library Test - Advanced testing utilities

## Community

### Support Channels
- **GitHub Issues**: Bug reports and feature requests
- **Stack Overflow**: Technical questions (tag: joshuasalcedo-library)
- **Gitter Chat**: Real-time discussion
- **Email**: contact@joshuasalcedo.io

### Contributing
We welcome contributions! Please see our [Contributing Guide](contributing.html) for details.

## License

Copyright 2024 Joshua Salcedo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

---

For more information, visit [joshuasalcedo.io](https://joshuasalcedo.io)