# Joshua Salcedo Library IO Core

Welcome to the Joshua Salcedo Library IO Core documentation. This module provides comprehensive I/O utilities and abstractions for file operations, stream processing, and data serialization.

## Overview

The Library IO Core module extends Java's I/O capabilities with modern, efficient, and easy-to-use utilities. It provides abstractions for common I/O patterns, performance optimizations, and enhanced error handling for robust file and stream operations.

## Key Features

### File Operations
- **Enhanced File API**: Simplified file reading, writing, and manipulation
- **Directory Operations**: Recursive operations, filtering, watching
- **Temporary Files**: Safe temporary file management
- **File Locking**: Cross-platform file locking mechanisms

### Stream Processing
- **Stream Utilities**: Efficient stream copying, filtering, transformation
- **Buffering Strategies**: Adaptive buffering for optimal performance
- **Stream Monitoring**: Progress tracking for long operations
- **Compression Support**: Built-in support for common compression formats

### Serialization
- **Format Support**: JSON, XML, YAML, Properties, CSV
- **Custom Serializers**: Extensible serialization framework
- **Type Safety**: Generic serialization with type preservation
- **Performance**: Optimized for both small and large datasets

### Network I/O
- **HTTP Utilities**: Simplified HTTP operations
- **Connection Pooling**: Efficient connection management
- **Retry Logic**: Configurable retry strategies
- **Timeout Handling**: Comprehensive timeout configuration

## Getting Started

### Maven Dependency

```xml
<dependency>
    <groupId>io.joshuasalcedo.library</groupId>
    <artifactId>library-io-core</artifactId>
    <version>${project.version}</version>
</dependency>
```

### Basic Usage

```java
import io.joshuasalcedo.library.io.*;

// File operations
String content = FileUtils.readString(Path.of("file.txt"));
FileUtils.writeString(Path.of("output.txt"), "Hello World");

// Stream operations
try (InputStream in = Files.newInputStream(source);
     OutputStream out = Files.newOutputStream(target)) {
    StreamUtils.copy(in, out, new ProgressListener());
}

// Serialization
ObjectMapper mapper = new JsonMapper();
MyObject obj = mapper.readValue(file, MyObject.class);
mapper.writeValue(outputFile, obj);
```

## Core Components

### FileUtils

Comprehensive file operations:

```java
// Reading files
String text = FileUtils.readString(path);
List<String> lines = FileUtils.readLines(path);
byte[] bytes = FileUtils.readBytes(path);

// Writing files
FileUtils.writeString(path, "content");
FileUtils.writeLines(path, lines);
FileUtils.writeBytes(path, bytes);

// File operations
FileUtils.copy(source, target);
FileUtils.move(source, target);
FileUtils.delete(path);
FileUtils.createDirectories(path);
```

### StreamUtils

Stream manipulation utilities:

```java
// Copy with progress
StreamUtils.copy(input, output, new ProgressListener() {
    public void progress(long bytesRead, long totalBytes) {
        System.out.printf("Progress: %.2f%%\n", 
            (double) bytesRead / totalBytes * 100);
    }
});

// Buffered operations
try (BufferedInputStream bis = StreamUtils.buffer(input);
     BufferedOutputStream bos = StreamUtils.buffer(output)) {
    // Use buffered streams
}
```

### DirectoryWatcher

Monitor directory changes:

```java
DirectoryWatcher watcher = new DirectoryWatcher(directory);
watcher.addListener(new WatchListener() {
    public void fileCreated(Path file) { }
    public void fileModified(Path file) { }
    public void fileDeleted(Path file) { }
});
watcher.start();
```

### Serialization Framework

Flexible serialization support:

```java
// JSON serialization
JsonSerializer serializer = new JsonSerializer();
String json = serializer.serialize(object);
MyClass obj = serializer.deserialize(json, MyClass.class);

// XML serialization
XmlSerializer xmlSerializer = new XmlSerializer();
String xml = xmlSerializer.serialize(object);

// YAML serialization
YamlSerializer yamlSerializer = new YamlSerializer();
String yaml = yamlSerializer.serialize(object);
```

## Advanced Features

### Compression Support

```java
// Compress file
CompressionUtils.gzip(inputFile, outputFile);
CompressionUtils.ungzip(compressedFile, outputFile);

// Compress stream
try (OutputStream gzipOut = CompressionUtils.gzipOutputStream(output)) {
    // Write compressed data
}
```

### Network Operations

```java
// HTTP operations
HttpClient client = HttpClient.create()
    .withTimeout(30, TimeUnit.SECONDS)
    .withRetry(3)
    .build();

Response response = client.get("https://api.example.com/data");
String json = response.asString();
```

### File Watching

```java
FileWatcher watcher = FileWatcher.create()
    .watch(path)
    .onChange(file -> System.out.println("Changed: " + file))
    .onDelete(file -> System.out.println("Deleted: " + file))
    .start();
```

## Performance Optimization

### Buffering Strategies

The module automatically selects optimal buffer sizes based on:
- File size
- Available memory
- Operation type
- System capabilities

### Memory Management

- Stream operations use fixed memory regardless of file size
- Large file support through streaming APIs
- Automatic resource cleanup
- Memory-mapped file support for large files

### Parallel Processing

```java
// Parallel file processing
FileProcessor.parallel()
    .withThreads(4)
    .process(files, file -> {
        // Process each file
    });
```

## Error Handling

Comprehensive error handling with detailed exceptions:

```java
try {
    FileUtils.copy(source, target);
} catch (FileNotFoundException e) {
    // Handle missing file
} catch (InsufficientSpaceException e) {
    // Handle disk space issues
} catch (FileIOException e) {
    // Handle general I/O errors
}
```

## Best Practices

1. **Use try-with-resources**: Always for stream operations
2. **Buffer appropriately**: Let the library choose buffer sizes
3. **Handle interruptions**: Check for thread interruption in long operations
4. **Monitor progress**: Use progress listeners for large operations
5. **Validate inputs**: Check file existence and permissions

## Examples

### Copy Large File with Progress

```java
FileUtils.copyLarge(source, target, new ProgressListener() {
    private long lastReport = 0;
    
    public void progress(long bytesRead, long totalBytes) {
        long percent = bytesRead * 100 / totalBytes;
        if (percent != lastReport) {
            System.out.println("Progress: " + percent + "%");
            lastReport = percent;
        }
    }
});
```

### Process Directory Recursively

```java
DirectoryProcessor.process(rootDir)
    .withFilter(path -> path.toString().endsWith(".java"))
    .withDepth(5)
    .forEach(file -> {
        // Process each Java file
    });
```

## Testing Utilities

Test helpers for I/O operations:

```java
@Test
public void testFileOperation() {
    Path tempDir = TestFileUtils.createTempDirectory();
    Path testFile = TestFileUtils.createTempFile(tempDir, "test", ".txt");
    
    // Perform tests
    
    TestFileUtils.deleteRecursively(tempDir);
}
```

## Documentation

- [JavaDoc API](apidocs/index.html)
- [Performance Guide](performance.html)
- [Examples](examples.html)
- [FAQ](faq.html)

## Support

For questions or issues:

- **GitHub Issues**: [Report an issue](https://github.com/joshuasalcedo/joshuasalcedo-parent/issues)
- **Stack Overflow**: Tag with `joshuasalcedo-io`
- **Wiki**: [IO Module Wiki](https://github.com/joshuasalcedo/joshuasalcedo-parent/wiki/IO-Module)

## License

This module is licensed under the Apache License 2.0. See the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0.txt) file for details.