/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql;

/**
 * This "parser/scanner" holds onto the string version of the JPQL query that is parsed into a
 * parsed tree. It uses a cursor that lets the current {@link
 * org.eclipse.persistence.jpa.jpql.parser.Expression Expression} object to parse its fragment of the query.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5.1
 * @since 2.3
 * @author Pascal Filion
 */
public final class WordParser {

    /**
     * The current position of the cursor within the JPQL query.
     */
    private int cursor;

    /**
     * The length of the JPQL query.
     */
    private final int length;

    /**
     * The string representation of the JPQL query.
     */
    private final CharSequence text;

    /**
     * When {@link WordParser#word()}, {@link WordParser#wordEndPosition()}, or {@link
     * WordParser#wordEndPosition(int)} is called, this is updated to reflect the type of word that
     * is scanned.
     *
     * @since 2.4
     */
    private WordType wordType;

    /**
     * Creates a new <code>WordParser</code>.
     *
     * @param text The string representation of the JPQL query
     */
    public WordParser(CharSequence text) {
        super();
        this.cursor   = 0;
        this.text     = text;
        this.length   = text.length();
        this.wordType = WordType.UNDEFINED;
    }

    /**
     * Retrieves the character at the current cursor position.
     *
     * @return The character retrieved from the string at the current cursor position or '\0' if the
     * position is beyond the end of the text
     */
    public char character() {
        return character(cursor);
    }

    /**
     * Retrieves the character at the given cursor position.
     *
     * @param position The position of the character to return
     * @return The character retrieved from the string at the given position or '\0' if the position
     * is beyond the end of the text
     */
    public char character(int position) {
        return (position >= length) ? '\0' : text.charAt(position);
    }

    /**
     * Determines whether the query ends with the given suffix and the end position is the end of the
     * range for testing.
     *
     * @param endPosition The position where the check stops
     * @param suffix The suffix is the text that is used to match it with the substring within the text
     * @return <code>true</code> if the character sequence represented by the argument is a suffix of
     * the query; <code>false</code> otherwise
     */
    public boolean endsWith(int endPosition, String suffix) {
        return startsWith(suffix, endPosition - suffix.length());
    }

    /**
     * Determines whether the query ends with the given suffix and the end position is the end of the
     * range for testing. The case of the character is ignored.
     *
     * @param endPosition The position where the check stops
     * @param suffix The suffix is the text that is used to match it with the substring within the text
     * @return <code>true</code> if the character sequence represented by the argument is a suffix of
     * the query; <code>false</code> otherwise
     * @since 2.5
     */
    public boolean endsWithIgnoreCase(int endPosition, String suffix) {
        return startsWithIgnoreCase(suffix, endPosition - suffix.length());
    }

    /**
     * Retrieves a word starting at the current position. The text before and after the position will
     * be part of the returned value.
     * <p>
     * For instance, "<b>SELECT</b> <b>AVG</b>(e.age) <b>FROM</b> Employee e":
     * <ul>
     * <li>Position 3, result is "SELECT";
     * <li>Position 6, result is "SELECT";
     * <li>Position 7, result is an empty string.
     * <li>Position 11, result is an empty string.
     * <li>Position 13, result is "e.".
     * </ul>
     *
     * @return The word in which the cursor is
     */
    public String entireWord() {
        return entireWord(cursor);
    }

    /**
     * Retrieves a word starting at the given position. The text before and after the position will
     * be part of the returned value.
     * <p>
     * For instance, "<b>SELECT</b> <b>AVG</b>(e.age) <b>FROM</b> Employee e":
     * <ul>
     * <li>Position 3, result is "SELECT";
     * <li>Position 6, result is "SELECT";
     * <li>Position 7, result is an empty string.
     * <li>Position 11, result is an empty string.
     * <li>Position 13, result is "e.".
     * </ul>
     *
     * @param position The position where to retrieve the word
     * @return The word in which the cursor is
     */
    public String entireWord(int position) {
        int startPosition = partialWordStartPosition(position);
        int endPosition   = wordEndPosition(position);
        return substring(startPosition, endPosition);
    }

    /**
     * Returns what the type of word {@link #word()} returns.
     *
     * @return The category of the word returned by {@link #word()}
     * @since 2.4
     */
    public WordType getWordType() {
        return wordType;
    }

    /**
     * Determines whether the given character is an arithmetic symbol, which is one of the following:
     * { {@literal '>', '<', '/', '*', '-', '+', '=', '{'} }.
     *
     * @param character The character to test if it's a math symbol
     * @return <code>true</code> if the given character is one of the valid math symbols;
     * <code>false</code> otherwise
     */
    public boolean isArithmeticSymbol(char character) {
        return character == '>' ||
               character == '!' ||
               character == '<' ||
               character == '/' ||
               character == '*' ||
               character == '-' ||
               character == '+' ||
               character == '=' ||
               character == '{';
    }

    /**
     * Determines whether the given character is a delimiter. The delimiter are '(', ')' and ','.
     *
     * @param character The character to test
     * @return <code>true</code> if the given character is a delimiter; <code>false</code> otherwise
     */
    public boolean isDelimiter(char character) {
        return character == '(' ||
               character == ')' ||
               character == ',';
    }

    /**
     * Determines whether the given character is a character that can be used in a number. This only
     * includes the numeric characters [0, 9] and the period character. This method should only be
     * used to determine if a word starts with a digit character.
     *
     * @param character The character to test if it's a digit
     * @return <code>true</code> if the given character is a digit; <code>false</code> otherwise
     */
    public boolean isDigit(char character) {
        return (character == '.') || Character.isDigit(character);
    }

    /**
     * Determines whether the position of the cursor is at the end of the text.
     *
     * @return <code>true</code> if the position of the cursor is at the end of the text;
     * <code>false</code> otherwise
     */
    public boolean isTail() {
        return cursor >= length;
    }

    /**
     * Determines whether the given character is not considered to be part of a word (which is
     * usually comprise of alphanumeric characters).
     *
     * @param character The character used to determine if it should be part of a word or not
     * @return <code>true</code> if the character can be part of a word; <code>false</code> if it is
     * not an alphanumeric character, which usually means is a whitespace, a delimiter or an
     * arithmetic symbol
     * @see Character#isWhitespace(char)
     * @see #isArithmeticSymbol(char)
     * @see #isDelimiter(char)
     */
    public boolean isWordSeparator(char character) {
        return Character.isWhitespace(character) ||
               isDelimiter(character)            ||
               isArithmeticSymbol(character);
    }

    /**
     * Returns the length of the string value.
     *
     * @return The total count of characters
     */
    public int length() {
        return length;
    }

    /**
     * Moves the position of the cursor by the length of the given word.
     *
     * @param word The word used to determine how much to move the position forward
     */
    public void moveBackward(CharSequence word) {
        cursor -= word.length();
    }

    /**
     * Moves backward the position of the cursor by the given amount.
     *
     * @param position The amount to remove from the current position
     */
    public void moveBackward(int position) {
        cursor -= position;
    }

    /**
     * Moves the position of the cursor by the length of the given word.
     *
     * @param word The word used to determine how much to move the position forward
     * @return The actual portion of the text that was skipped
     */
    public String moveForward(CharSequence word) {
        return moveForward(word.length());
    }

    /**
     * Moves forward the position of the cursor by the given amount.
     *
     * @param position The amount to add to the current position
     * @return The actual portion of the text that was skipped
     */
    public String moveForward(int position) {
        String word = substring(cursor, cursor + position);
        cursor += position;
        return word;
    }

    /**
     * Moves the position of the cursor by the length of the given word and ignore any different in
     * whitespace count. If the text has more than one whitespace and the given word usually has one,
     * then only one will be part of the returned substring.
     *
     * @param word The word used to determine how much to move the position forward
     * @return The actual portion of the text that was skipped
     * @since 2.4.3
     */
    public String moveForwardIgnoreWhitespace(CharSequence word) {

        StringBuilder sb = new StringBuilder();
        int pc = word.length();
        int po = 0;

        while (--pc >= 0) {

            char c1 = text.charAt(cursor++);
            char c2 = word.charAt(po++);

            // Handle testing
            // 1. "... GROUP    BY" and "GROUP BY"
            // 2. "... GROUP\nBY" and "GROUP BY"
            if (Character.isWhitespace(c1)) {

                if (Character.isWhitespace(c2)) {
                    sb.append(' ');
                    continue;
                }

                pc++;
                po--;
                continue;
            }

            sb.append(c1);
        }

        return sb.toString();
    }

    /**
     * Retrieves the numeric literal that should be the current word to parse.
     *
     * @return The numeric literal value
     */
    public String numericLiteral() {
        return substring(cursor, scanNumericLiteral(cursor));
    }

    /**
     * Retrieves a word before the current position of the cursor, which determines when the parsing
     * stop.
     * <p>
     * For instance, "<b>SELECT</b> <b>AVG</b>(e.age) <b>FROM</b> Employee e":
     * <ul>
     * <li>Position 3, result is "SEL";
     * <li>Position 6, result is "SELECT";
     * <li>Position 7, result is an empty string.
     * <li>Position 11, result is an empty string.
     * <li>Position 13, result is "e.".
     * </ul>
     *
     * @return The sub-string that is before the position
     */
    public String partialWord() {
        int startIndex = partialWordStartPosition(cursor);
        return substring(startIndex, cursor);
    }

    /**
     * Retrieves a word before the specified position, which determines when the parsing stop.
     * <p>
     * For instance, "<b>SELECT</b> <b>AVG</b>(e.age) <b>FROM</b> Employee e":
     * <ul>
     * <li>Position 3, result is "SEL";
     * <li>Position 6, result is "SELECT";
     * <li>Position 7, result is an empty string.
     * <li>Position 11, result is an empty string.
     * <li>Position 13, result is "e.".
     * </ul>
     *
     * @param position The position of the cursor
     * @return The sub-string that is before the position
     */
    public String partialWord(int position) {
        int startIndex = partialWordStartPosition(position);
        return substring(startIndex, position);
    }

    /**
     * Finds the beginning of the word and the given position is within that word.
     * <p>
     * For instance, "<b>SELECT</b> <b>AVG</b>(e.age) <b>FROM</b> Employee e":
     * <ul>
     * <li>Position 3, result is 0;
     * <li>Position 8, result is 7;
     * </ul>
     *
     * @param position The position from which the search ends
     * @return The position, which is a smaller number or equal, than the given position
     */
    public int partialWordStartPosition(int position) {

        int startIndex = position;

        for (int index = position; --index >= 0; ) {
            char character = text.charAt(index);

            if (Character.isWhitespace(character) ||
                isDelimiter(character) ||
                isArithmeticSymbol(character)) {

                break;
            }

            startIndex--;
        }

        return startIndex;
    }

    /**
     * Returns the current position of the cursor.
     *
     * @return The current position of the cursor
     */
    public int position() {
        return cursor;
    }

    private int scanNumericLiteral(int startPosition) {

        int endIndex = startPosition;
        boolean digitParsed = false;
        boolean dotParsed = false;

        for (; endIndex < length; endIndex++) {
            char character = text.charAt(endIndex);

            // Usual digit
            if (character >= '0' && character <= '9') {
                digitParsed = true;
            }
            // The arithmetic sign before the number
            else if ((character == '+' || character == '-')) {

                // '+' or '-' is only valid at the beginning of the literal
                if (endIndex > startPosition) {
                    break;
                }
            }
            // The separator of integer and decimal values
            else if (character == '.') {

                // A '.' was already parsed, it is not a valid numeric literal
                if (dotParsed) {
                    endIndex = startPosition + 1;
                    wordType = WordType.WORD;
                    break;
                }

                dotParsed = true;
            }
            // Hexadecimal value
            else if (character == 'x') {
                boolean powerParsed = false;

                for (; endIndex < length; endIndex++) {
                    character = text.charAt(endIndex);

                    if (character == 'p' || character == 'P') {
                        powerParsed = true;
                    }
                    else if (powerParsed && (character == '+' || character == '-')) {
                        continue;
                    }
                    else if (isWordSeparator(character)) {
                        break;
                    }
                }

                break;
            }
            // Parse the exponent
            else if (character == 'e' || character == 'E') {

                if (!digitParsed) {
                    wordType = WordType.WORD;
                    break;
                }

                for (int index = ++endIndex; index < length; index++) {
                    character = text.charAt(index);

                    // The first character can be '+', '-' or a number
                    if ((index == endIndex) && (character == '-' || character == '+') ||
                        character >= '0' && character <= '9') {

                        endIndex++;
                        continue;
                    }

                    if (character == '.') {
                        wordType = WordType.WORD;
                    }

                    // If it is not a character like '(', then it's not a valid number
                    if (isWordSeparator(character)) {
                        break;
                    }

                    endIndex++;
                }

                break;
            }
            // A float/double or long number
            else if (character == 'f' || character == 'F' ||
                     character == 'd' || character == 'D' ||
                     character == 'l' || character == 'L') {

                // A single arithmetic symbol
                // Example: "-LENGTH..." -> "-"
                if (!digitParsed) {
                    wordType = WordType.WORD;
                    break;
                }

                endIndex++;

                // End of the text
                if (endIndex == length) {
                    break;
                }

                character = text.charAt(endIndex);

                // Done parsing the numeric literal
                if (isWordSeparator(character)) {
                    break;
                }
            }
            // Example: "-AVG..." -> "-"
            else if (!digitParsed && Character.isJavaIdentifierPart(character)) {
                wordType = WordType.WORD;
                break;
            }
            // Done parsing the numeric literal
            else if (isWordSeparator(character)) {
                break;
            }
        }

        return endIndex;
    }

    /**
     * Retrieves the first word from the given text starting at the specified position.
     *
     * @param startPosition the current position that will be used to parse the literal
     * @return The first word contained in the text, if none could be found, then an empty string is
     * returned
     */
    private int scanStringLiteral(int startPosition) {

        int endIndex = startPosition;
        char startQuote = text.charAt(endIndex);

        for (endIndex++; endIndex < length; endIndex++) {

            char character = text.charAt(endIndex);

            if (character == startQuote) {
                endIndex++;

                // Verify the single quote is escaped with another single quote
                if ((startQuote == '\'') && (endIndex < length)) {
                    char nextCharacter = text.charAt(endIndex);

                    // The single quote is escaped, continue
                    if (nextCharacter == '\'') {
                        continue;
                    }
                }
                // Verify the double quote is escaped with backslash
                else if ((startQuote == '\"') && (endIndex - 2 > startPosition)) {
                    char previousCharacter = text.charAt(endIndex - 2);

                    // The double quote is escaped, continue
                    if (previousCharacter == '\\') {
                        continue;
                    }
                }

                // Reached the end of the string literal
                break;
            }
        }

        return endIndex;
    }

    /**
     * Manually sets the position of the cursor within the string. If the position is a negative
     * number, the position will be 0.
     *
     * @param position The new position of the cursor
     */
    public void setPosition(int position) {
        this.cursor = (position < 0) ? 0 : position;
    }

    /**
     * Removes the whitespace that starts the given text.
     *
     * @return The number of whitespace removed
     */
    public int skipLeadingWhitespace() {

        int count = 0;

        while (cursor < length) {

            char character = text.charAt(cursor);
            if (!Character.isWhitespace(character)) {
                break;
            }

            cursor++;
            count++;
        }

        return count;
    }

    /**
     * Determines whether the text starts with the given character. The case of the character is not
     * ignored.
     *
     * @param possibleCharacter The possible character at the current position
     * @return <code>true</code> if the text starts with the given character at the current position;
     * <code>false</code> otherwise
     */
    public boolean startsWith(char possibleCharacter) {
        return possibleCharacter == character();
    }

    /**
     * Tests whether the query starts with the specified prefix from the current position.
     *
     * @param prefix The prefix
     * @return <code>true</code> if the character sequence represented by the argument is a prefix of
     * the text; <code>false</code> otherwise
     */
    public boolean startsWith(CharSequence prefix) {
        return startsWith(prefix, cursor);
    }

    /**
     * Tests whether the substring of the query beginning at the specified index starts with the
     * specified prefix.
     *
     * @param prefix The prefix
     * @param startIndex Where to begin looking in the query
     * @return <code>true</code> if the character sequence represented by the
     * argument is a prefix of the substring of this object starting at index <code>startIndex</code>;
     * <code>false</code> otherwise
     */
    public boolean startsWith(CharSequence prefix, int startIndex) {

        int prefixLength = prefix.length();

        // Note: startIndex might be near -1 >>> 1
        if ((startIndex < 0) || (startIndex > length - prefixLength)) {
            return false;
        }

        int prefixIndex = 0;

        while (--prefixLength >= 0) {
            if (text.charAt(startIndex++) != prefix.charAt(prefixIndex++)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether the character at the current position is one of the arithmetic operators:
     * { '+', '-', '*', '/' },
     *
     * @return <code>true</code> if the character at the current position is an arithmetic operator;
     * <code>false</code> otherwise
     */
    public boolean startsWithArithmeticOperator() {

        char character = text.charAt(cursor);

        return (character == '+') ||
               (character == '/') ||
               (character == '-') ||
               (character == '*');
    }

    /**
     * Determines if the text starts with a digit (<code>true</code>), an arithmetic term
     * (<code>false</code>) or anything else (<code>null</code>).
     *
     * @return <code>true</code> if the text starts with a digit (we'll assume it is a digit if the
     * text starts with a digit or an arithmetic sign followed by a digit), <code>false</code> if it
     * starts with an arithmetic term (we'll assume it is a digit followed by a non-digit character);
     * otherwise returns <code>null</code>
     */
    public Boolean startsWithDigit() {

        char character = character();

        // Check if the first character is either '+' or '-' and make sure it's not used for a numeric
        // value, which in that case, a numeric value will be created
        if (character == '-' ||
            character == '+') {

            moveForward(1);
            int count = skipLeadingWhitespace();
            character = character(cursor);
            moveBackward(count + 1);

            if (isDigit(character)) {
                return Boolean.TRUE;
            }

            if (character == '-' ||
                character == '+' ||
                character == '*' ||
                character == '/') {

                return null;
            }

            return Boolean.FALSE;
        }

        if (character == '.') {
            return isDigit(character(cursor + 1));
        }

        if (isDigit(character)) {
            return Boolean.TRUE;
        }

        return null;
    }

    /**
     * Determines whether the text at the current position start with the following identifier.
     *
     * @param identifier The JPQL identifier to match with the text at the current position
     * @return <code>true</code> if the text starts with the given text (case is ignored) and the
     * cursor is at the end of the text or is following by a word separator character; <code>false</code>
     * otherwise
     */
    public boolean startsWithIdentifier(CharSequence identifier) {
        return startsWithIdentifier(identifier, cursor);
    }

    /**
     * Determines whether the text at the current position start with the following identifier.
     *
     * @param identifier The JPQL identifier to match with the text at the current position
     * @param position The position to start matching the characters
     * @return <code>true</code> if the text starts with the given text (case is ignored) and the
     * cursor is at the end of the text or is following by a word separator character; <code>false</code>
     * otherwise
     */
    public boolean startsWithIdentifier(CharSequence identifier, int position) {

        int pc = identifier.length();

        // Note: offset might be near -1 >>> 1
        if ((position < 0) || (position > length - pc)) {
            return false;
        }

        int po = 0;
        int to = position;

        while (--pc >= 0) {

            char c1 = text.charAt(to++);
            char c2 = identifier.charAt(po++);

            if (c1 == c2) {
                continue;
            }

            // Handle testing
            // 1. "... GROUP    BY" and "GROUP BY"
            // 2. "... GROUP\nBY" and "GROUP BY"
            if (Character.isWhitespace(c1)) {

                if (Character.isWhitespace(c2)) {
                    continue;
                }

                if (to == length) {
                    return false;
                }

                pc++;
                po--;
                continue;
            }

            // If characters don't match but case may be ignored, try converting
            // both characters to uppercase. If the results match, then the
            // comparison scan should continue
            char u1 = Character.toUpperCase(c1);
            char u2 = Character.toUpperCase(c2);

            if (u1 != u2) {
                return false;
            }

            // Unfortunately, conversion to uppercase does not work properly for
            // the Georgian alphabet, which has strange rules about case
            // conversion. So we need to make one last check before exiting
            if (Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                return false;
            }
        }

        // End of the text
        if (to == length) {
            return true;
        }

        // Check to see if the next character is a word separator
        char character = text.charAt(to);
        return isWordSeparator(character);
    }

    /**
     * Determines whether the text starts with the given character. The case of the character is ignored.
     *
     * @param possibleCharacter The possible character at the current position
     * @return <code>true</code> if the text starts with the given character at the current position;
     * <code>false</code> otherwise
     */
    public boolean startsWithIgnoreCase(char possibleCharacter) {

        char character = character();

        return possibleCharacter == character ||
               possibleCharacter == Character.toUpperCase(character);
    }

    /**
     * Tests if the string starts with the specified prefix. The case of the character is ignored.
     *
     * @param prefix The prefix to test against
     * @return <code>true</code> if the character sequence represented by the argument is a prefix of
     * the character sequence represented by this string; <code>false</code> otherwise. Note also
     * that <code>true</code> will be returned if the argument is an empty string or is equal to this
     * <code>String</code> object as determined by the {@link #equals(Object)} method
     */
    public boolean startsWithIgnoreCase(CharSequence prefix) {
        return startsWithIgnoreCase(prefix, cursor);
    }

    /**
     * Tests if the string starts with the specified prefix. The case of the character is ignored.
     *
     * @param prefix The prefix to test against
     * @param offset Where to begin looking in this string
     * @return <code>true</code> if the character sequence represented by the argument is a prefix of
     * the character sequence represented by this string; <code>false</code> otherwise
     */
    public boolean startsWithIgnoreCase(CharSequence prefix, int offset) {

        int pc = prefix.length();

        // Note: offset might be near -1 >>> 1
        if ((offset < 0) || (offset > length - pc)) {
            return false;
        }

        int po = 0;
        int to = offset;

        while (--pc >= 0) {

            char c1 = text.charAt(to++);
            char c2 = prefix.charAt(po++);

            if (c1 == c2) {
                continue;
            }

            // If characters don't match but case may be ignored, try converting
            // both characters to uppercase. If the results match, then the
            // comparison scan should continue
            char u1 = Character.toUpperCase(c1);
            char u2 = Character.toUpperCase(c2);

            if (u1 != u2) {
                return false;
            }

            // Unfortunately, conversion to uppercase does not work properly for
            // the Georgian alphabet, which has strange rules about case
            // conversion. So we need to make one last check before exiting
            if (Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a substring that is within the current position of the cursor and the end of the text.
     *
     * @return The remain of the string starting at the current position
     */
    public String substring() {
        return substring(cursor);
    }

    /**
     * Returns a substring that is within the given position and the end of the text.
     *
     * @param startIndex The beginning of the substring, inclusive
     * @return The remain of the string starting at the given position
     */
    public String substring(int startIndex) {
        return substring(startIndex, length);
    }

    /**
     * Returns a substring that is within the given positions.
     *
     * @param startIndex The beginning of the substring, inclusive
     * @param endIndex The end of the substring, exclusive
     * @return The remain of the string that is within the given positions
     */
    public String substring(int startIndex, int endIndex) {
        return text.subSequence(startIndex, endIndex).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return isTail() ? ExpressionTools.EMPTY_STRING : substring();
    }

    /**
     * Calculates the number of whitespace that are in the query. The check starts at the current position.
     *
     * @return The count of consecutive whitespace found from the current position
     */
    public int whitespaceCount() {
        return whitespaceCount(cursor);
    }

    /**
     * Calculates the number of whitespace that are in the query. The check starts at the current position.
     *
     * @param position The position from where the scan starts
     * @return The count of consecutive whitespace found from the given position
     */
    public int whitespaceCount(int position) {

        for (int index = position; index < length; index++) {
            char character = text.charAt(index);

            if (!Character.isWhitespace(character)) {
                return index - position;
            }
        }

        return 0;
    }

    /**
     * Retrieves the first word starting at the current position.
     *
     * @return The first word contained in the text, if none could be found,
     * then an empty string is returned
     */
    public String word() {
        return substring(cursor, wordEndPosition());
    }

    /**
     * Returns the position a word would end based on the current cursor position. {@link #getWordType()}
     * can be used to determine the type of word that was scanned.
     *
     * @return The position where the current word ends
     * @see #word() WordParser.word()
     * @see WordType
     */
    public int wordEndPosition() {
        return wordEndPosition(cursor);
    }

    /**
     * Returns the position a word would end based on the given start position. {@link #getWordType()}
     * can be used to determine the type of word that was scanned.
     *
     * @param position The position to start scanning the text
     * @return The position where the current word ends
     * @see #word() WordParser.word()
     * @see WordType
     */
    public int wordEndPosition(int position) {

        if (position >= length) {
            wordType = WordType.UNDEFINED;
            return position;
        }

        char character = text.charAt(position);
        int endIndex = position + 1;
        wordType = WordType.WORD;

        // Parse a string literal
        if (ExpressionTools.isQuote(character)) {
            wordType = WordType.STRING_LITERAL;

            // The quote is the end quote
            if (position > 1) {
                character = character(position - 1);
                if (ExpressionTools.isQuote(character)) {
                    return endIndex;
                }
            }

            return scanStringLiteral(position);
        }

        // Parse an input parameter
        if (ExpressionTools.isParameter(character)) {
            wordType = WordType.INPUT_PARAMETER;

            for (; endIndex < length; endIndex++) {
                character = text.charAt(endIndex);

                // Special case for '!='
                if ((character == '!') && (endIndex + 1 < length)) {
                    character = text.charAt(endIndex + 1);

                    if (character == '=') {
                        break;
                    }

                    endIndex++;
                    continue;
                }

                if (isWordSeparator(character)) {
                    break;
                }
            }

            return endIndex;
        }

        // Parse an arithmetic symbol
        if (character == '/' ||
            character == '*' ||
            character == '+' ||
            character == '-') {

            return endIndex;
        }

        // Parse JDBC date
        if (character == '{') {
            // TODO
            return endIndex;
        }

        // Parse a numeric literal
        if (isDigit(character)) {

            wordType = WordType.NUMERIC_LITERAL;
            endIndex = scanNumericLiteral(position);

            // The word is a valid numeric literal, stop now,
            // otherwise scan for the entire word
            if (wordType == WordType.NUMERIC_LITERAL) {
                return endIndex;
            }
        }

        // '='
        else if (character == '=') {
            return endIndex;
        }

        // <, <>, <=
        else if (character == '<') {

            if (endIndex < length) {
                character = text.charAt(endIndex);

                if (character == '>' ||
                    character == '=') {

                    return endIndex + 1;
                }

                return endIndex;
            }
        }

        // >, >=, !=
        else if (character == '>' ||
                 character == '!') {

            // End of the text
            if (endIndex == length) {
                return endIndex;
            }

            // Scan the next character
            char nextCharacter = text.charAt(endIndex);

            if (nextCharacter == '=') {
                return ++endIndex;
            }

            if (character == '>') {
                return endIndex;
            }
        }

        // Done scanning
        else if (isWordSeparator(character)) {
            return --endIndex;
        }

        // Scan for an entire word
        for (int index = endIndex; index < length; index++) {
            character = text.charAt(index);

            // Special case for '!='
            if ((character == '!') && (endIndex + 1 < length)) {
                character = text.charAt(index + 1);

                if (character == '=') {
                    return endIndex;
                }

                endIndex++;
                continue;
            }

            if (Character.isWhitespace(character) ||
                isDelimiter(character) ||
                character == '>' ||
               character == '<' ||
               character == '/' ||
               character == '*' ||
               character == '-' ||
               character == '+' ||
               character == '=') {

                break;
            }

            // Continue to the next character
            endIndex++;
        }

        return endIndex;
    }

    /**
     * This enumeration determines the type of word that was scanned. It will be set by {@link
     * WordParser#word()}, {@link WordParser#wordEndPosition()}, {@link WordParser#wordEndPosition(int)}.
     */
    public enum WordType {

        /**
         * The word being scanned is an input parameter, it starts with either ':' or '?'.
         */
        INPUT_PARAMETER,

        /**
         * The word being scanned is a numeric literal (decimal or hexadecimal number).
         */
        NUMERIC_LITERAL,

        /**
         * The word being scanned is a string literal, it starts with either ''' or '"'.
         */
        STRING_LITERAL,

        /**
         * No word was scanned, this is usually set when the cursor is at the end of the text.
         */
        UNDEFINED,

        /**
         * The word being scanned anything else other than an input parameter, numeric literal or
         * string literal.
         */
        WORD
    }
}
