# Time Package

The `time` package provides a fluent API for date and time operations. It offers a powerful and intuitive way to perform various date and time operations such as creation, manipulation, formatting, and comparison.

## Overview

The Time API is designed to be chainable and easy to use while providing flexibility for complex date and time processing requirements. It builds upon Java's java.time package, adding a fluent interface and additional functionality.

## Key Features

- **Fluent, chainable API** for date and time operations
- **Date/time creation** (now, today, tomorrow, yesterday, parse)
- **Date/time manipulation** (atStartOfDay, atEndOfDay, plus, minus)
- **Date/time navigation** (nextWeekday, previousWeekday)
- **Date/time filtering and transformation**
- **Date/time formatting** with patterns
- **Date/time predicates** (isBefore, isAfter, isBetween, isWeekend)
- **Support for bulk operations** on multiple date/time values

## Main Components

### Time Class

The main class providing a fluent API for date and time operations.

## Usage Examples

### Creating Date/Time Instances

```java
// Get current date/time
LocalDateTime now = Time.now().getDateTime();

// Get today at start of day
LocalDateTime todayStart = Time.today().atStartOfDay().getDateTime();

// Get tomorrow at end of day
LocalDateTime tomorrowEnd = Time.tomorrow().atEndOfDay().getDateTime();
```

### Parsing Date/Time Strings

```java
// Parse with default patterns
LocalDateTime dateTime = Time.parse("2023-01-15T10:30:00").getDateTime();

// Parse with custom pattern
LocalDateTime customDate = Time.parse("15/01/2023 10:30", "dd/MM/yyyy HH:mm").getDateTime();
```

### Date/Time Manipulation

```java
// Add days to a date
LocalDateTime nextWeek = Time.now()
    .plusDays(7)
    .getDateTime();

// Subtract hours
LocalDateTime twoHoursAgo = Time.now()
    .minus(2, ChronoUnit.HOURS)
    .getDateTime();

// Set specific time
LocalDateTime todayAt5pm = Time.today()
    .atTime(17, 0)
    .getDateTime();
```

### Date/Time Navigation

```java
// Get next Monday
LocalDateTime nextMonday = Time.now()
    .nextWeekday(DayOfWeek.MONDAY)
    .getDateTime();

// Get previous Friday
LocalDateTime prevFriday = Time.now()
    .previousWeekday(DayOfWeek.FRIDAY)
    .getDateTime();
```

### Formatting Date/Time

```java
// Format with predefined formatter
String formatted = Time.now()
    .format(DateTimeFormatter.ISO_DATE)
    .get();

// Format with custom pattern
String customFormatted = Time.now()
    .format("yyyy-MM-dd HH:mm")
    .get();
```

### Filtering Date/Time Values

```java
// Filter dates that are in the future
List<LocalDateTime> futureDates = Time.of(date1, date2, date3)
    .filter(Time.isAfter(LocalDateTime.now()))
    .executeDateTime();

// Filter weekend dates
List<LocalDateTime> weekends = Time.of(dates)
    .filter(Time.isWeekend())
    .executeDateTime();
```

### Transforming Date/Time Values

```java
// Transform dates to start of month
List<LocalDateTime> monthStarts = Time.of(dates)
    .transform(date -> date.with(TemporalAdjusters.firstDayOfMonth()))
    .executeDateTime();
```

### Checking Date/Time Conditions

```java
// Check if a date is in the future
boolean isFuture = Time.of(someDate).isFuture();

// Check if a date is expired
boolean isExpired = Time.of(expiryDate).isExpired();
```

### Bulk Operations

```java
// Format multiple dates
List<String> formattedDates = Time.of(date1, date2, date3)
    .execute("yyyy-MM-dd");
```

## Advanced Features

### Date Calculations

```java
// Calculate business days
LocalDateTime businessDay = Time.now()
    .plusDays(1)
    .filter(time -> !Time.isWeekend().test(time))
    .getDateTime();

// Get first day of month
LocalDateTime firstDayOfMonth = Time.now()
    .transform(date -> date.with(TemporalAdjusters.firstDayOfMonth()))
    .getDateTime();

// Get last day of month
LocalDateTime lastDayOfMonth = Time.now()
    .transform(date -> date.with(TemporalAdjusters.lastDayOfMonth()))
    .getDateTime();
```

### Working with Time Zones

```java
// Convert to different time zone
String timeInNewYork = Time.now()
    .transform(dateTime -> dateTime.atZone(ZoneId.systemDefault())
        .withZoneSameInstant(ZoneId.of("America/New_York"))
        .toLocalDateTime())
    .format("yyyy-MM-dd HH:mm:ss")
    .get();
```

## Error Handling

The Time API handles errors gracefully:

- Parse operations handle DateTimeParseException and provide meaningful error messages
- Invalid operations return empty results rather than throwing exceptions
- Null values are handled gracefully

```java
try {
    // This will attempt to parse with multiple formats
    LocalDateTime date = Time.parse("invalid date").getDateTime();
} catch (IllegalArgumentException e) {
    // Handle parsing error
}
```

## Best Practices

1. **Use the fluent API** for concise and readable code
2. **Leverage built-in predicates** for filtering dates
3. **Use transform()** for complex date manipulations
4. **Use format()** for consistent date formatting
5. **Use bulk operations** when working with multiple dates
6. **Handle parsing errors** appropriately

## Thread Safety

The Time class is immutable and thread-safe. Each operation returns a new Time instance without modifying the original.

## See Also

- [JavaDoc API](apidocs/io/joshuasalcedo/library/io/core/time/package-summary.html)
- [Date and Time Operations](index.html#date-and-time-operations)