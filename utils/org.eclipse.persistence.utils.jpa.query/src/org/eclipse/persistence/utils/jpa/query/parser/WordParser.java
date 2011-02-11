/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * This "parser" holds onto the string version of the Java Persistence query
 * that is parsed into a parsed tree. It uses a cursor that let the current
 * {@link Expression} object to parse its section of the query.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class WordParser
{
	/**
	 * The current position of the cursor within the text.
	 */
	private int cursor;

	/**
	 * The total length of the string.
	 */
	private final int length;

	/**
	 * The string representation of the Java Persistence query to parse.
	 */
	private final CharSequence text;

	/**
	 * Creates a new <code>WordParser</code>.
	 *
	 * @param text The string representation of the Java Persistence query to
	 * parse
	 */
	public WordParser(CharSequence query)
	{
		super();

		checkText(query);

		this.text   = query;
		this.length = text.length();
		this.cursor = 0;
	}

	/**
	 * Retrieves the character at the current position of the cursor.
	 *
	 * @return The character retrieved from the string at the cursor position
	 */
	public char character()
	{
		return character(cursor);
	}

	/**
	 * Retrieves the character at the given position.
	 *
	 * @param position The position of the character to return
	 * @return The character retrieved from the string at the given position
	 */
	public char character(int position)
	{
		return (position >= length) ? '\0' : text.charAt(position);
	}

	private void checkText(CharSequence query)
	{
		if (query == null)
		{
			throw new NullPointerException("The query cannot be null");
		}
	}

	/**
	 * Determines whether the query ends with the given suffix and the end
	 * position is the end of the range for testing.
	 *
	 * @param endPosition The position where the check stops
	 * @param suffix The suffix is the text that is used to match it with the
	 * substring within the text
	 * @return <code>true</code> if the character sequence represented by the
	 * argument is a suffix of the query; <code>false</code> otherwise
	 */
	public boolean endsWith(int endPosition, String suffix)
	{
		// Skip whitespace between the endPosition and the character before them
		for (int index = endPosition; --index >= 0; )
		{
			if (Character.isWhitespace(text.charAt(index)))
			{
				endPosition--;
			}
		}

		return startsWith(suffix, endPosition - suffix.length());
	}

	/**
	 * Retrieves a word starting at the current position. The text before and
	 * after the position will be part of the returned value.
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
	public String entireWord()
	{
		return entireWord(cursor);
	}

	/**
	 * Retrieves a word starting at the given position. The text before and
	 * after the position will be part of the returned value.
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
	public String entireWord(int position)
	{
		int startPosition = partialWordStartPosition(position);
		int endPosition   = wordEndPosition(position);
		return substring(startPosition, endPosition);
	}

	/**
	 * Determines whether the given character is an arithmetic symbol, which is one of
	 * the following: { '>', '<', '/', '*', '-', '+', '=', '{' }.
	 *
	 * @param character The character to test if it's a math symbol
	 * @return <code>true</code> if the given character is one of the valid math
	 * symbols; <code>false</code> otherwise
	 */
	public boolean isArithmeticSymbol(char character)
	{
		return character == '>' ||
		       character == '<' ||
		       character == '/' ||
		       character == '*' ||
		       character == '-' ||
		       character == '+' ||
		       character == '=' ||
		       character == '{';
	}

	/**
	 * Determines whether the given character is a delimiter. The delimiter are
	 * '(', ')' and ','.
	 *
	 * @param character The character to test
	 * @return <code>true</code> if the given character is a delimiter,
	 * <code>false</code> otherwise
	 */
	public boolean isDelimitor(char character)
	{
		return character == '(' ||
		       character == ')' ||
		       character == ',';
	}

	/**
	 * Determines whether the given character is a character that can be used in
	 * a number. This includes all numeric characters [0, 9] and the period
	 * character.
	 *
	 * @param character The character to test if it's a digit
	 * @return <code>true</code> if the given character is a digit; <code>false</code>
	 * otherwise
	 */
	public boolean isDigit(char character)
	{
		return (character == '.') ||
		        Character.isDigit(character);
	}

	public boolean isParsingComplete(char character)
	{
		return Character.isWhitespace(character) ||
		       isDelimitor(character)            ||
		       character == '>' ||
		       character == '<' ||
		       character == '/' ||
		       character == '*' ||
		       character == '=';
	}

	/**
	 * Determines whether the position of the cursor is at the end of the text.
	 *
	 * @return <code>true</code> if the position of the cursor is at the end of
	 * the text; <code>false</code> otherwise
	 */
	public boolean isTail()
	{
		return cursor >= length;
	}

	/**
	 * Returns the length of the string value.
	 *
	 * @return The total count of characters
	 */
	public int length()
	{
		return length;
	}

	/**
	 * Moves the position of the cursor by the length of the given word.
	 *
	 * @param word The word used to determine how much to move the position forward
	 */
	public void moveBackward(CharSequence word)
	{
		moveBackward(word.length());
	}

	/**
	 * Moves backward the position of the cursor by the given amount.
	 *
	 * @param position The amount to remove from the current position
	 */
	public void moveBackward(int position)
	{
		cursor -= position;
	}

	/**
	 * Moves the position of the cursor by the length of the given word.
	 *
	 * @param word The word used to determine how much to move the position forward
	 */
	public void moveForward(CharSequence word)
	{
		moveForward(word.length());
	}

	/**
	 * Moves forward the position of the cursor by the given amount.
	 *
	 * @param position The amount to add to the current position
	 */
	public void moveForward(int position)
	{
		cursor += position;
	}

	/**
	 * Retrieves a word and the specified position is where the parsing stop.
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
	public String partialWord(int position)
	{
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
	 * @return The position, which is a smaller number or equal, than the given
	 * position
	 */
	public int partialWordStartPosition(int position)
	{
		int startIndex = position;

		for (int index = position; --index >= 0; )
		{
			char character = text.charAt(index);

			if (character == '='                  ||
			    Character.isWhitespace(character) ||
			    isDelimitor(character))
			{
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
	public int position()
	{
		return cursor;
	}

	/**
	 * Retrieves
	 *
	 * @return
	 */
	public String potentialWord()
	{
		return substring(cursor, potentialWordEndPosition());
	}

	/**
	 * Retrieves
	 *
	 * @return
	 */
	public int potentialWordEndPosition()
	{
		int endIndex = cursor;

		for (int index = cursor; index < length; index++)
		{
			char character = text.charAt(index);

			if (isArithmeticSymbol(character))
			{
				if (endIndex == cursor)
				{
					endIndex++;
				}

				break;
			}

			if (Character.isWhitespace(character) ||
			    isDelimitor(character))
			{
				index = length;
			}
			else
			{
				endIndex++;
			}
		}

		return endIndex;
	}

	/**
	 * Retrieves the previous word from the current position. Any character from
	 * the position to the first whitespace will be ignore.
	 * <p>
	 * For instance, "<b>SELECT</b> <b>AVG</b>(e.age) <b>FROM</b> Employee e":
	 * <ul>
	 * <li>Position 3, result is an empty string;
	 * <li>Position 6, result is an empty string;
	 * <li>Position 7, result is "SELECT".
	 * <li>Position 10, result is "SELECT".
	 * <li>Position 13, result is "SELECT".
	 * </ul>
	 *
	 * @param position The position to start looking for a word in reverse
	 * @return The previous word that is separated by whitespace with the position
	 */
	public String previousWord(int position)
	{
		// Find the first whitespace before the word at the given position
		while (--position >= 0)
		{
			char character = text.charAt(position);

			if (Character.isWhitespace(character))
			{
				break;
			}
		}

		if (position <= 0)
		{
			return AbstractExpression.EMPTY_STRING;
		}

		return entireWord(position);
	}

	/**
	 * Manually sets the position of the cursor within the string.
	 *
	 * @param position The new position of the cursor
	 */
	public void setPosition(int position)
	{
		this.cursor = position;
	}

	/**
	 * Removes the whitespace that starts the given text.
	 *
	 * @param text The text to have the whitespace removed from the beginning of
	 * the string
	 * @return The number of whitespace removed
	 */
	public int skipLeadingWhitespace()
	{
		int count = 0;

		while (cursor < length)
		{
			if (!Character.isWhitespace(text.charAt(cursor)))
			{
				break;
			}

			cursor++;
			count++;
		}

		return count;
	}

	/**
	 * Determines whether the text starts with the given character. The case of
	 * the character is not ignored.
	 *
	 * @param possibleCharacter The possible character at the current position
	 * @return <code>true</code> if the text starts with the given character at
	 * the current position; <code>false</code> otherwise
	 */
	public boolean startsWith(char possibleCharacter)
	{
		return possibleCharacter == character();
	}

	/**
	 * Tests wether the query starts with the specified prefix from the current
	 * position.
	 *
	 * @param prefix The prefix
	 * @return <code>true</code> if the character sequence represented by the
	 * argument is a prefix of the text; <code>false</code> otherwise
	 */
	public boolean startsWith(CharSequence prefix)
	{
		return startsWith(prefix, cursor);
	}

	/**
	 * Tests whether the substring of the query beginning at the specified index
	 * starts with the specified prefix.
	 *
	 * @param prefix The prefix
	 * @param offset Where to begin looking in the query
	 * @return <code>true</code> if the character sequence represented by the
	 * argument is a prefix of the substring of this object starting at index
	 * <code>toffset</code>; <code>false</code> otherwise
	 */
	public boolean startsWith(CharSequence prefix, int offset)
	{
		int pc = prefix.length();

		// Note: toffset might be near -1 >>> 1
		if ((offset < 0) || (offset > length - pc))
		{
			return false;
		}

		int to = offset;
		int po = 0;

		while (--pc >= 0)
		{
			if (text.charAt(to++) != prefix.charAt(po++))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Determines whether the character at the current position is one of the
	 * arithmetic operators: { '+', '-', '*', '/' },
	 *
	 * @return <code>true</code> if the character at the current position is an
	 * arithmetic operator; <code>false</code> otherwise
	 */
	public boolean startsWithArithmeticOperator()
	{
		char character = text.charAt(cursor);

		return (character == '+') ||
		       (character == '/') ||
		       (character == '-') ||
		       (character == '*');
	}

	/**
	 * Determines if the text starts with a digit (<code>true</code>), an
	 * arithmetic term (<code>false</code>) or anything else (<code>null</code>).
	 *
	 * @return <code>true</code> if the text starts with a digit (we'll assume it
	 * is a digit if the text starts with a digit or an arithmetic sign followed
	 * by a digit), <code>false</code> if it starts with an arithmetic term
	 * (we'll assume it is a digit followed by a non-digit character); otherwise
	 * returns <code>null</code>
	 */
	public Boolean startsWithDigit()
	{
		char character = character();

		// Check if the first character is either '+' or '-' and make sure
		// it's not used for a numeric value, which in that case, a numeric
		// value will be created
		if (character == '-' ||
		    character == '+')
		{
			if ((cursor + 1 < length) && isDigit(character(cursor + 1)))
			{
				return Boolean.TRUE;
			}

			return Boolean.FALSE;
		}
		else if (character == '.')
		{
			return isDigit(character(cursor + 1));
		}
		else if (isDigit(character))
		{
			return Boolean.TRUE;
		}

		return null;
	}

	public boolean startsWithIdentifier(CharSequence prefix)
	{
		return startsWithIdentifier(prefix, cursor);
	}

	public boolean startsWithIdentifier(CharSequence prefix, int toffset)
	{
		if (startsWithIgnoreCase(prefix, toffset))
		{
			int nextCharacterIndex = toffset + prefix.length();

			if (nextCharacterIndex == length)
			{
				return true;
			}

			char character = text.charAt(nextCharacterIndex);

			if (Character.isWhitespace(character) ||
			    isDelimitor(character))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines whether the text starts with the given character. The case of
	 * the character is ignored.
	 *
	 * @param possibleCharacter The possible character at the current position
	 * @return <code>true</code> if the text starts with the given character at
	 * the current position; <code>false</code> otherwise
	 */
	public boolean startsWithIgnoreCase(char possibleCharacter)
	{
		char character = character();

		return possibleCharacter == character ||
		       possibleCharacter == Character.toUpperCase(character);
	}

	/**
	 * Tests if the string starts with the specified prefix.
	 *
	 * @param prefix The prefix
	 * @return <code>true</code> if the character sequence represented by the
	 * argument is a prefix of the character sequence represented by this string;
	 * <code>false</code> otherwise. Note also that <code>true</code> will be
	 * returned if the argument is an empty string or is equal to this
	 * <code>String</code> object as determined by the {@link #equals(Object)}
	 * method
	 */
	public boolean startsWithIgnoreCase(CharSequence prefix)
	{
		return startsWithIgnoreCase(prefix, cursor);
	}

	/**
	 * Tests if the string starts with the specified prefix.
	 *
	 * @param prefix The prefix
	 * @param offset Where to begin looking in this string
	 * @return <code>true</code> if the character sequence represented by the
	 * argument is a prefix of the character sequence represented by this string;
	 * <code>false</code> otherwise
	 */
	public boolean startsWithIgnoreCase(CharSequence prefix, int offset)
	{
		int pc = prefix.length();

		// Note: toffset might be near -1 >>> 1
		if ((offset < 0) || (offset > length - pc))
		{
			return false;
		}

		int po = 0;
		int to = offset;

		while (--pc >= 0)
		{
			char c1 = text.charAt(to++);
			char c2 = prefix.charAt(po++);

			if (c1 == c2)
			{
				continue;
			}

			// If characters don't match but case may be ignored, try converting
			// both characters to uppercase. If the results match, then the
			// comparison scan should continue
			char u1 = Character.toUpperCase(c1);
			char u2 = Character.toUpperCase(c2);

			if (u1 != u2)
			{
				return false;
			}

			// Unfortunately, conversion to uppercase does not work properly for
			// the Georgian alphabet, which has strange rules about case
			// conversion. So we need to make one last check before exiting
			if (Character.toLowerCase(u1) != Character.toLowerCase(u2))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns a substring that is within the current position of the cursor and
	 * the end of the text.
	 *
	 * @return The remain of the string starting at the current position
	 */
	public String substring()
	{
		return substring(cursor);
	}

	/**
	 * Returns a substring that is within the given position and the end of the
	 * text.
	 *
	 * @param startIndex The beginning of the substring, inclusive
	 * @return The remain of the string starting at the given position
	 */
	public String substring(int startIndex)
	{
		return substring(startIndex, length);
	}

	/**
	 * Returns a substring that is within the given positions.
	 *
	 * @param startIndex The beginning of the substring, inclusive
	 * @param endIndex The end of the substring, exclusive
	 * @return The remain of the string that is within the given positions
	 */
	public String substring(int startIndex, int endIndex)
	{
		return text.subSequence(startIndex, endIndex).toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return isTail() ? AbstractExpression.EMPTY_STRING : substring();
	}

	/**
	 * Calculates the number of whitespace that are in the query. The check
	 * starts at the current position.
	 *
	 * @return The count of consecutive whitespace found from the current position
	 */
	public int whitespaceCount()
	{
		return whitespaceCount(cursor);
	}

	/**
	 * Calculates the number of whitespace that are in the query. The check
	 * starts at the current position.
	 *
	 * @param position The position from where the scan starts
	 * @return The count of consecutive whitespace found from the given position
	 */
	public int whitespaceCount(int position)
	{
		for (int index = position; index < length; index++)
		{
			char character = text.charAt(index);

			if (!Character.isWhitespace(character))
			{
				return index - position;
			}
		}

		return 0;
	}

	/**
	 * Retrieves the first word starting at the current position.
	 *
	 * @return The first word contained in the text, if none could be found, then
	 * an empty string is returned
	 */
	public String word()
	{
		return substring(cursor, wordEndPosition());
	}

	/**
	 * Returns the position at which a word ends, the position is determined by
	 * the current position of the cursor.
	 *
	 * @return The position where the current word ends, it ends when a whitespace,
	 * a delimitor {',', '(', ')'} or a math symbol is encountered
	 */
	public int wordEndPosition()
	{
		return wordEndPosition(cursor);
	}

	/**
	 * Returns the position at which a word ends.
	 *
	 * @param position The beginning of the search where the word ends
	 * @return The position where the current word ends, it ends when a whitespace,
	 * a delimitor {',', '(', ')'} or a math symbol is encountered
	 */
	public int wordEndPosition(int position)
	{
		int endIndex = position;

		for (int index = position; index < length; index++)
		{
			char character = text.charAt(index);

			if (isParsingComplete(character))
			{
				index = length;
			}
			else
			{
				endIndex++;
			}
		}

		return endIndex;
	}
}