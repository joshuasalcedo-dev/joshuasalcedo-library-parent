package io.joshuasalcedo.library.io.core.text.format;

/**
 * Fluent API for text formatting operations.
 * Provides a chainable interface for applying multiple text transformations.
 */
public class TextFormat {
    
    private String text;
    private final TextFormatter formatter;
    
    /**
     * Private constructor to enforce factory method usage.
     * @param text the initial text
     */
    private TextFormat(String text) {
        this.text = text;
        this.formatter = new TextFormatterImpl();
    }
    
    /**
     * Creates a new TextFormat instance with the given text.
     * @param text the text to format
     * @return a new TextFormat instance
     */
    public static TextFormat of(String text) {
        return new TextFormat(text);
    }
    
    /**
     * Creates a new TextFormat instance with an empty string.
     * @return a new TextFormat instance
     */
    public static TextFormat empty() {
        return new TextFormat("");
    }
    
    /**
     * Converts the text to the specified case type.
     * @param caseType the target case type
     * @return this TextFormat instance for chaining
     */
    public TextFormat toCase(TextFormatter.CaseType caseType) {
        text = formatter.toCase(text, caseType);
        return this;
    }
    
    /**
     * Converts the text to UPPERCASE.
     * @return this TextFormat instance for chaining
     */
    public TextFormat toUpperCase() {
        return toCase(TextFormatter.CaseType.UPPER_CASE);
    }
    
    /**
     * Converts the text to lowercase.
     * @return this TextFormat instance for chaining
     */
    public TextFormat toLowerCase() {
        return toCase(TextFormatter.CaseType.LOWER_CASE);
    }
    
    /**
     * Converts the text to camelCase.
     * @return this TextFormat instance for chaining
     */
    public TextFormat toCamelCase() {
        return toCase(TextFormatter.CaseType.CAMEL_CASE);
    }
    
    /**
     * Converts the text to PascalCase.
     * @return this TextFormat instance for chaining
     */
    public TextFormat toPascalCase() {
        return toCase(TextFormatter.CaseType.PASCAL_CASE);
    }
    
    /**
     * Converts the text to snake_case.
     * @return this TextFormat instance for chaining
     */
    public TextFormat toSnakeCase() {
        return toCase(TextFormatter.CaseType.SNAKE_CASE);
    }
    
    /**
     * Converts the text to kebab-case.
     * @return this TextFormat instance for chaining
     */
    public TextFormat toKebabCase() {
        return toCase(TextFormatter.CaseType.KEBAB_CASE);
    }
    
    /**
     * Converts the text to Title Case.
     * @return this TextFormat instance for chaining
     */
    public TextFormat toTitleCase() {
        return toCase(TextFormatter.CaseType.TITLE_CASE);
    }
    
    /**
     * Converts the text to Sentence case.
     * @return this TextFormat instance for chaining
     */
    public TextFormat toSentenceCase() {
        return toCase(TextFormatter.CaseType.SENTENCE_CASE);
    }
    
    /**
     * Capitalizes the first letter of the text.
     * @return this TextFormat instance for chaining
     */
    public TextFormat capitalize() {
        text = formatter.capitalize(text);
        return this;
    }
    
    /**
     * Capitalizes the first letter of each word.
     * @return this TextFormat instance for chaining
     */
    public TextFormat capitalizeWords() {
        text = formatter.capitalizeWords(text);
        return this;
    }
    
    /**
     * Reverses the text.
     * @return this TextFormat instance for chaining
     */
    public TextFormat reverse() {
        text = formatter.reverse(text);
        return this;
    }
    
    /**
     * Removes leading and trailing whitespace.
     * @return this TextFormat instance for chaining
     */
    public TextFormat trim() {
        text = formatter.trim(text);
        return this;
    }
    
    /**
     * Removes all whitespace from the text.
     * @return this TextFormat instance for chaining
     */
    public TextFormat removeWhitespace() {
        text = formatter.removeWhitespace(text);
        return this;
    }
    
    /**
     * Truncates text to specified length with default ellipsis.
     * @param maxLength the maximum length
     * @return this TextFormat instance for chaining
     */
    public TextFormat truncate(int maxLength) {
        text = formatter.truncate(text, maxLength);
        return this;
    }
    
    /**
     * Truncates text with custom ellipsis.
     * @param maxLength the maximum length
     * @param ellipsis the ellipsis string to append
     * @return this TextFormat instance for chaining
     */
    public TextFormat truncate(int maxLength, String ellipsis) {
        text = formatter.truncate(text, maxLength, ellipsis);
        return this;
    }
    
    /**
     * Pads text on the left to specified length.
     * @param length the desired length
     * @param padChar the character to use for padding
     * @return this TextFormat instance for chaining
     */
    public TextFormat padLeft(int length, char padChar) {
        text = formatter.padLeft(text, length, padChar);
        return this;
    }
    
    /**
     * Pads text on the left with spaces.
     * @param length the desired length
     * @return this TextFormat instance for chaining
     */
    public TextFormat padLeft(int length) {
        return padLeft(length, ' ');
    }
    
    /**
     * Pads text on the right to specified length.
     * @param length the desired length
     * @param padChar the character to use for padding
     * @return this TextFormat instance for chaining
     */
    public TextFormat padRight(int length, char padChar) {
        text = formatter.padRight(text, length, padChar);
        return this;
    }
    
    /**
     * Pads text on the right with spaces.
     * @param length the desired length
     * @return this TextFormat instance for chaining
     */
    public TextFormat padRight(int length) {
        return padRight(length, ' ');
    }
    
    /**
     * Repeats the text a specified number of times.
     * @param times the number of repetitions
     * @return this TextFormat instance for chaining
     */
    public TextFormat repeat(int times) {
        text = formatter.repeat(text, times);
        return this;
    }
    
    /**
     * Replaces all occurrences of a substring.
     * @param target the substring to replace
     * @param replacement the replacement string
     * @return this TextFormat instance for chaining
     */
    public TextFormat replace(String target, String replacement) {
        text = formatter.replaceAll(text, target, replacement);
        return this;
    }
    
    /**
     * Wraps text at specified line length.
     * @param lineLength the maximum line length
     * @return this TextFormat instance for chaining
     */
    public TextFormat wrap(int lineLength) {
        text = formatter.wrap(text, lineLength);
        return this;
    }
    
    /**
     * Appends text to the current text.
     * @param suffix the text to append
     * @return this TextFormat instance for chaining
     */
    public TextFormat append(String suffix) {
        if (suffix != null) {
            text = text + suffix;
        }
        return this;
    }
    
    /**
     * Prepends text to the current text.
     * @param prefix the text to prepend
     * @return this TextFormat instance for chaining
     */
    public TextFormat prepend(String prefix) {
        if (prefix != null) {
            text = prefix + text;
        }
        return this;
    }
    
    /**
     * Surrounds the text with the given prefix and suffix.
     * @param prefix the prefix to add
     * @param suffix the suffix to add
     * @return this TextFormat instance for chaining
     */
    public TextFormat surround(String prefix, String suffix) {
        return prepend(prefix).append(suffix);
    }
    
    /**
     * Surrounds the text with the given delimiter.
     * @param delimiter the delimiter to use on both sides
     * @return this TextFormat instance for chaining
     */
    public TextFormat surround(String delimiter) {
        return surround(delimiter, delimiter);
    }
    
    /**
     * Applies a custom transformation function to the text.
     * @param transformer the transformation function
     * @return this TextFormat instance for chaining
     */
    public TextFormat transform(java.util.function.Function<String, String> transformer) {
        if (transformer != null) {
            text = transformer.apply(text);
        }
        return this;
    }
    
    /**
     * Conditionally applies a transformation if the condition is true.
     * @param condition the condition to check
     * @param action the action to perform if condition is true
     * @return this TextFormat instance for chaining
     */
    public TextFormat when(boolean condition, java.util.function.Function<TextFormat, TextFormat> action) {
        if (condition && action != null) {
            return action.apply(this);
        }
        return this;
    }
    
    /**
     * Checks if the text is blank (null, empty, or whitespace only).
     * @return true if blank, false otherwise
     */
    public boolean isBlank() {
        return formatter.isBlank(text);
    }
    
    /**
     * Checks if the text is not blank.
     * @return true if not blank, false otherwise
     */
    public boolean isNotBlank() {
        return !isBlank();
    }
    
    /**
     * Checks if the text is in the specified case.
     * @param caseType the case type to check
     * @return true if text is in the specified case, false otherwise
     */
    public boolean isCase(TextFormatter.CaseType caseType) {
        return formatter.getCaseConverter().isCase(text, caseType);
    }
    
    /**
     * Gets the word count of the text.
     * @return the number of words
     */
    public int wordCount() {
        return formatter.countWords(text);
    }
    
    /**
     * Gets the length of the text.
     * @return the text length
     */
    public int length() {
        return text != null ? text.length() : 0;
    }
    
    /**
     * Returns the formatted text.
     * @return the current text value
     */
    public String get() {
        return text;
    }
    
    /**
     * Returns the formatted text.
     * @return the current text value
     */
    @Override
    public String toString() {
        return text;
    }
    
    /**
     * Creates a new TextFormat instance with the current text.
     * Useful for branching operations.
     * @return a new TextFormat instance
     */
    public TextFormat copy() {
        return new TextFormat(text);
    }
}