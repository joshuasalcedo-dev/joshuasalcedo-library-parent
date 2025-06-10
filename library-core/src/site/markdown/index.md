# Joshua Salcedo Library Core

Welcome to the Joshua Salcedo Library Core documentation. This module provides the core implementations and utilities that power the Joshua Salcedo library ecosystem.

## Overview

The Library Core module contains the fundamental implementations of the interfaces defined in the Library API module. It provides robust, well-tested implementations of core functionality that other modules and applications can build upon.

## Key Components

### Core Implementations

- **Service Implementations**: Production-ready service classes
- **Utility Classes**: Common utilities for string manipulation, collections, I/O
- **Data Structures**: Efficient implementations of common data structures
- **Algorithms**: Optimized algorithms for common operations
- **Builders and Factories**: Fluent APIs for object construction

### Feature Areas

1. **Configuration Management**
   - Property file loading
   - Environment variable integration
   - Configuration validation
   - Hot-reload support

2. **Logging Framework**
   - SLF4J integration
   - Custom appenders
   - Performance logging
   - Structured logging support

3. **Exception Handling**
   - Consistent error handling
   - Exception translation
   - Error recovery strategies
   - Detailed error reporting

4. **Resource Management**
   - Resource pooling
   - Lifecycle management
   - Automatic cleanup
   - Memory optimization

## Getting Started

### Maven Dependency

```xml
<dependency>
    <groupId>io.joshuasalcedo.library</groupId>
    <artifactId>library-core</artifactId>
    <version>${project.version}</version>
</dependency>
```

### Basic Usage

```java
import io.joshuasalcedo.library.core.*;

// Initialize core services
CoreService service = CoreServiceFactory.createDefault();
service.initialize();

// Use utility classes
String result = StringUtils.capitalize("hello world");
List<String> filtered = CollectionUtils.filter(list, predicate);

// Work with configurations
Configuration config = ConfigurationBuilder.create()
    .withProperty("key", "value")
    .withEnvironment()
    .build();
```

## Core Services

### ServiceManager

Central service registry and lifecycle management:

```java
ServiceManager manager = ServiceManager.getInstance();
manager.register("myService", new MyServiceImpl());
MyService service = manager.getService("myService", MyService.class);
```

### ConfigurationService

Flexible configuration management:

```java
ConfigurationService configService = new ConfigurationService();
configService.loadProperties("application.properties");
String value = configService.getString("app.name");
int port = configService.getInt("server.port", 8080);
```

### ResourceManager

Efficient resource handling:

```java
ResourceManager resources = ResourceManager.getInstance();
try (Resource resource = resources.acquire("database")) {
    // Use resource
} // Automatic cleanup
```

## Utility Classes

### StringUtils

Common string operations:

- `capitalize()`, `uncapitalize()`
- `isEmpty()`, `isNotEmpty()`
- `join()`, `split()`
- `truncate()`, `pad()`

### CollectionUtils

Collection manipulation:

- `filter()`, `map()`, `reduce()`
- `partition()`, `group()`
- `sort()`, `shuffle()`
- `isEmpty()`, `size()`

### IOUtils

I/O operations:

- `copy()`, `toString()`
- `readLines()`, `writeLines()`
- `closeQuietly()`
- `toByteArray()`

### DateUtils

Date and time utilities:

- `format()`, `parse()`
- `addDays()`, `addMonths()`
- `isBefore()`, `isAfter()`
- `getDaysBetween()`

## Best Practices

1. **Use Factory Methods**: Prefer factory methods over constructors
2. **Resource Management**: Always use try-with-resources for managed resources
3. **Configuration**: Externalize configuration, don't hardcode values
4. **Error Handling**: Use the provided exception hierarchy
5. **Thread Safety**: Check documentation for thread-safety guarantees

## Performance Considerations

- Most utilities are optimized for common use cases
- Collection utilities use lazy evaluation where possible
- Resource pooling reduces allocation overhead
- Caching is employed for expensive operations

## Extension Points

The core module is designed for extensibility:

1. **Service Providers**: Implement custom services
2. **Configuration Sources**: Add custom configuration loaders
3. **Resource Types**: Define new resource types
4. **Utility Extensions**: Extend utility classes with custom methods

## Examples

### Configuration Example

```java
Configuration config = ConfigurationBuilder.create()
    .withSystemProperties()
    .withEnvironmentVariables()
    .withPropertiesFile("app.properties")
    .withDefault("timeout", "30")
    .build();
```

### Service Example

```java
@Service
public class EmailService extends AbstractService {
    @Override
    protected void doStart() {
        // Initialize email service
    }
    
    @Override
    protected void doStop() {
        // Cleanup resources
    }
    
    public void sendEmail(Email email) {
        // Send email implementation
    }
}
```

## Testing Support

The core module includes testing utilities:

```java
@Test
public void testService() {
    TestConfiguration config = TestConfiguration.create()
        .withProperty("test.mode", "true");
    
    ServiceManager manager = TestServiceManager.create();
    // Test your services
}
```

## Documentation

- [JavaDoc API](apidocs/index.html)
- [Architecture Guide](architecture.html)
- [Performance Tuning](performance.html)
- [Migration Guide](migration.html)

## Support

For questions or issues:

- **GitHub Issues**: [Report an issue](https://github.com/joshuasalcedo/joshuasalcedo-parent/issues)
- **Stack Overflow**: Tag with `joshuasalcedo-library`
- **Gitter Chat**: [Join the discussion](https://gitter.im/joshuasalcedo/library)

## License

This module is licensed under the Apache License 2.0. See the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0.txt) file for details.