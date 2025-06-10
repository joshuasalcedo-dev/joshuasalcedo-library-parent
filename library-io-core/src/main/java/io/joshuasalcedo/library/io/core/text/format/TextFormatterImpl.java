package io.joshuasalcedo.library.io.core.text.format;

class TextFormatterImpl implements TextFormatter {
    /**
     * @return the case converter implementation
     */
    @Override
    public CaseConverter getCaseConverter() {
        return new CaseConverter() {
            @Override
            public String convert(String text, CaseType caseType) {
                if (text == null) {
                    return "";
                }

                switch (caseType) {
                    case UPPER_CASE:
                        return text.toUpperCase();
                    case LOWER_CASE:
                        return text.toLowerCase();
                    case CAMEL_CASE:
                        if (text.isEmpty()) {
                            return "";
                        }
                        String[] words = text.split("\\s+");
                        StringBuilder result = new StringBuilder(words[0].toLowerCase());
                        for (int i = 1; i < words.length; i++) {
                            if (!words[i].isEmpty()) {
                                result.append(Character.toUpperCase(words[i].charAt(0)))
                                      .append(words[i].substring(1).toLowerCase());
                            }
                        }
                        return result.toString();
                    case PASCAL_CASE:
                        if (text.isEmpty()) {
                            return "";
                        }
                        String[] pascalWords = text.split("\\s+");
                        StringBuilder pascalResult = new StringBuilder();
                        for (String word : pascalWords) {
                            if (!word.isEmpty()) {
                                pascalResult.append(Character.toUpperCase(word.charAt(0)))
                                           .append(word.substring(1).toLowerCase());
                            }
                        }
                        return pascalResult.toString();
                    case SNAKE_CASE:
                        if (text.isEmpty()) {
                            return "";
                        }
                        // First handle camelCase or PascalCase input
                        StringBuilder snakeResult = new StringBuilder();
                        for (int i = 0; i < text.length(); i++) {
                            char c = text.charAt(i);
                            if (Character.isUpperCase(c)) {
                                if (i > 0) {
                                    snakeResult.append('_');
                                }
                                snakeResult.append(Character.toLowerCase(c));
                            } else {
                                snakeResult.append(c);
                            }
                        }
                        // Then handle spaces
                        return snakeResult.toString().replaceAll("\\s+", "_").toLowerCase();
                    case KEBAB_CASE:
                        if (text.isEmpty()) {
                            return "";
                        }
                        // First handle camelCase or PascalCase input
                        StringBuilder kebabResult = new StringBuilder();
                        for (int i = 0; i < text.length(); i++) {
                            char c = text.charAt(i);
                            if (Character.isUpperCase(c)) {
                                if (i > 0) {
                                    kebabResult.append('-');
                                }
                                kebabResult.append(Character.toLowerCase(c));
                            } else {
                                kebabResult.append(c);
                            }
                        }
                        // Then handle spaces
                        return kebabResult.toString().replaceAll("\\s+", "-").toLowerCase();
                    case TITLE_CASE:
                    case SENTENCE_CASE:
                    default:
                        return text; // Simple implementation for now
                }
            }

            @Override
            public boolean isCase(String text, CaseType caseType) {
                if (text == null) {
                    return false;
                }

                switch (caseType) {
                    case UPPER_CASE:
                        return text.equals(text.toUpperCase());
                    case LOWER_CASE:
                        return text.equals(text.toLowerCase());
                    case CAMEL_CASE:
                    case PASCAL_CASE:
                    case SNAKE_CASE:
                    case KEBAB_CASE:
                    case TITLE_CASE:
                    case SENTENCE_CASE:
                    default:
                        return false; // Simple implementation for now
                }
            }
        };
    }

    /**
     * @param text     the input text
     * @param caseType the target case type
     * @return
     */
    @Override
    public String toCase(String text, CaseType caseType) {
        return TextFormatter.super.toCase(text, caseType);
    }

    /**
     * @param text the input text
     * @return the text with first letter capitalized
     */
    @Override
    public String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    /**
     * @param text the input text
     * @return
     */
    @Override
    public String reverse(String text) {
        return "";
    }

    /**
     * @param text the input text
     * @return the trimmed text
     */
    @Override
    public String trim(String text) {
        return text != null ? text.trim() : "";
    }

    /**
     * @param text the input text
     * @return the text with each word capitalized
     */
    @Override
    public String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }

        return result.toString().trim();
    }

    /**
     * @param text the input text
     * @return the text without any whitespace
     */
    @Override
    public String removeWhitespace(String text) {
        if (text == null) {
            return "";
        }

        return text.replaceAll("\\s+", "");
    }

    /**
     * @param text      the input text
     * @param maxLength the maximum length
     * @return the truncated text with default ellipsis
     */
    @Override
    public String truncate(String text, int maxLength) {
        return truncate(text, maxLength, "...");
    }

    /**
     * @param text      the input text
     * @param maxLength the maximum length
     * @param ellipsis  the ellipsis string to append
     * @return the truncated text with custom ellipsis
     */
    @Override
    public String truncate(String text, int maxLength, String ellipsis) {
        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength) + (ellipsis != null ? ellipsis : "");
    }

    /**
     * @param text    the input text
     * @param length  the desired length
     * @param padChar the character to use for padding
     * @param padLeft true to pad on the left, false to pad on the right
     * @return
     */
    @Override
    public String pad(String text, int length, char padChar, boolean padLeft) {
        return "";
    }

    /**
     * @param text    the input text
     * @param length  the desired length
     * @param padChar the character to use for padding
     * @return
     */
    @Override
    public String padLeft(String text, int length, char padChar) {
        return TextFormatter.super.padLeft(text, length, padChar);
    }

    /**
     * @param text    the input text
     * @param length  the desired length
     * @param padChar the character to use for padding
     * @return
     */
    @Override
    public String padRight(String text, int length, char padChar) {
        return TextFormatter.super.padRight(text, length, padChar);
    }

    /**
     * @param text  the input text
     * @param times the number of repetitions
     * @return
     */
    @Override
    public String repeat(String text, int times) {
        return "";
    }

    /**
     * @param text the input text
     * @return
     */
    @Override
    public boolean isBlank(String text) {
        return false;
    }

    /**
     * @param text the input text
     * @return
     */
    @Override
    public int countWords(String text) {
        return 0;
    }

    /**
     * @param text        the input text
     * @param target      the substring to replace
     * @param replacement the replacement string
     * @return
     */
    @Override
    public String replaceAll(String text, String target, String replacement) {
        return "";
    }

    /**
     * @param text       the input text
     * @param lineLength the maximum line length
     * @return
     */
    @Override
    public String wrap(String text, int lineLength) {
        return "";
    }
}
