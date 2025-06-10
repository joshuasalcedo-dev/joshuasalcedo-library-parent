package io.joshuasalcedo.library.io.core.text.format;

/**
 * Interface for text formatting operations.
 * Provides methods for common text transformations.
 */
public interface TextFormatter {
    
    /**
     * Enum representing different text case formats.
     */
    enum CaseType {
        UPPER_CASE("UPPERCASE"),
        LOWER_CASE("lowercase"),
        CAMEL_CASE("camelCase"),
        PASCAL_CASE("PascalCase"),
        SNAKE_CASE("snake_case"),
        KEBAB_CASE("kebab-case"),
        TITLE_CASE("Title Case"),
        SENTENCE_CASE("Sentence case");
        
        private final String example;
        
        CaseType(String example) {
            this.example = example;
        }
        
        public String getExample() {
            return example;
        }
    }
    
    /**
     * Inner interface for case conversion implementations.
     */
    interface CaseConverter {
        /**
         * Converts text to the specified case type.
         * @param text the input text
         * @param caseType the target case type
         * @return the converted text
         */
        String convert(String text, CaseType caseType);
        
        /**
         * Checks if the text is already in the specified case.
         * @param text the input text
         * @param caseType the case type to check
         * @return true if text is in the specified case, false otherwise
         */
        boolean isCase(String text, CaseType caseType);
    }
    
    /**
     * Gets the case converter implementation.
     * @return the case converter
     */
    CaseConverter getCaseConverter();
    
    /**
     * Converts text to the specified case type.
     * @param text the input text
     * @param caseType the target case type
     * @return the converted text
     */
    default String toCase(String text, CaseType caseType) {
        return getCaseConverter().convert(text, caseType);
    }
    
    /**
     * Capitalizes the first letter of the text.
     * @param text the input text
     * @return the text with first letter capitalized
     */
    String capitalize(String text);
    
    /**
     * Reverses the text.
     * @param text the input text
     * @return the reversed text
     */
    String reverse(String text);
    
    /**
     * Removes leading and trailing whitespace.
     * @param text the input text
     * @return the trimmed text
     */
    String trim(String text);
    
    /**
     * Capitalizes the first letter of each word.
     * @param text the input text
     * @return the text with each word capitalized
     */
    String capitalizeWords(String text);
    
    /**
     * Removes all whitespace from the text.
     * @param text the input text
     * @return the text without any whitespace
     */
    String removeWhitespace(String text);
    
    /**
     * Truncates text to specified length, adding ellipsis if needed.
     * @param text the input text
     * @param maxLength the maximum length
     * @return the truncated text
     */
    String truncate(String text, int maxLength);
    
    /**
     * Truncates text with custom ellipsis.
     * @param text the input text
     * @param maxLength the maximum length
     * @param ellipsis the ellipsis string to append
     * @return the truncated text
     */
    String truncate(String text, int maxLength, String ellipsis);
    
    /**
     * Pads text to specified length with a padding character.
     * @param text the input text
     * @param length the desired length
     * @param padChar the character to use for padding
     * @param padLeft true to pad on the left, false to pad on the right
     * @return the padded text
     */
    String pad(String text, int length, char padChar, boolean padLeft);
    
    /**
     * Pads text on the left to specified length.
     * @param text the input text
     * @param length the desired length
     * @param padChar the character to use for padding
     * @return the left-padded text
     */
    default String padLeft(String text, int length, char padChar) {
        return pad(text, length, padChar, true);
    }
    
    /**
     * Pads text on the right to specified length.
     * @param text the input text
     * @param length the desired length
     * @param padChar the character to use for padding
     * @return the right-padded text
     */
    default String padRight(String text, int length, char padChar) {
        return pad(text, length, padChar, false);
    }
    
    /**
     * Repeats the text a specified number of times.
     * @param text the input text
     * @param times the number of repetitions
     * @return the repeated text
     */
    String repeat(String text, int times);
    
    /**
     * Checks if the text is empty or contains only whitespace.
     * @param text the input text
     * @return true if empty or whitespace only, false otherwise
     */
    boolean isBlank(String text);
    
    /**
     * Counts the number of words in the text.
     * @param text the input text
     * @return the word count
     */
    int countWords(String text);
    
    /**
     * Replaces all occurrences of a substring.
     * @param text the input text
     * @param target the substring to replace
     * @param replacement the replacement string
     * @return the text with replacements made
     */
    String replaceAll(String text, String target, String replacement);
    
    /**
     * Wraps text at specified line length.
     * @param text the input text
     * @param lineLength the maximum line length
     * @return the wrapped text
     */
    String wrap(String text, int lineLength);
}