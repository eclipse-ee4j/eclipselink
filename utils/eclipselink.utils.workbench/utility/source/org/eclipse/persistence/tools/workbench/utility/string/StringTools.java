/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.string;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public final class StringTools {

	/** carriage return */
	public static final String CR = System.getProperty("line.separator");

	/**
	 * The minimum count to be consider a match between two strings; which is 2.
	 */
	public static final int MINIMUM_MATCHING_LETTER = 2;

	/**
	 * The lowest weight for matching two strings. This means there is no match.
	 */
	public static final int NO_MATCH = 0;

	/**
	 * one of the weight for matching two strings. This means the first
	 * string contains the second string and is longer than the second one.
	 */
	public static final int STRING_CONTAINS = 6;

	/**
	 * The second lowest weight for matching two strings. This means the first
	 * string contains a section of the second string.
	 */
	public static final int STRING_CONTAINS_PART = 2;

	/**
	 * One of the weight for matching two strings. This means ...
	 */
	public static final int STRING_CONTAINS_PART_CONTINUOUS = 4;

	/**
	 * The weight of a potiential match between two strings. The value is 10
	 * since both strings are equals and we have a match.
	 */
	public static final int STRINGS_EQUAL = 10;

	/**
	 * The weight of a potiential match between two strings. The value is 8.
	 * Both strings are equals but one of them contains a delimitor between
	 * charater. 
	 */
	public static final int STRINGS_EQUAL_WITH_IGNORE_CHARACTERS = 8;



	// ********** padding/truncating **********

	/**
	 * Pad the specified string to the specified length.
	 * If the string is already the specified length, it is returned unchanged.
	 * If it is longer than the specified length, an IllegalArgumentException is thrown.
	 * If it is shorter than the specified length, it is padded with spaces at the end.
	 * String#pad(int)
	 */
	public static String pad(String string, int length) {
		int stringLength = string.length();
		if (stringLength > length) {
			throw new IllegalArgumentException("String is too long: " + stringLength + " > " + length);
		}
		if (stringLength == length) {
			return string;
		}
		return padInternal(string, length);
	}

	/**
	 * Pad or truncate the specified string to the specified length.
	 * If the string is already the specified length, it is returned unchanged.
	 * If it is longer than the specified length, it is truncated.
	 * If it is shorter than the specified length, it is padded with spaces at the end.
	 * String#padOrTruncate(int)
	 */
	public static String padOrTruncate(String string, int length) {
		int stringLength = string.length();
		if (stringLength == length) {
			return string;
		}
		if (stringLength > length) {
			return string.substring(0, length);
		}
		return padInternal(string, length);
	}

	/**
	 * Pad the specified string without validating the parms.
	 */
	private static String padInternal(String string, int length) {
		char[] a = new char[length];
		int stringLength = string.length();
		string.getChars(0, stringLength, a, 0);
		Arrays.fill(a, stringLength, length, ' ');
		return new String(a);
	}

	/**
	 * Pad the specified string to the specified length.
	 * If the string is already the specified length, it is returned unchanged.
	 * If it is longer than the specified length, an IllegalArgumentException is thrown.
	 * If it is shorter than the specified length, it is padded with zeros at the front.
	 * String#zeroPad(int)
	 */
	public static String zeroPad(String string, int length) {
		int stringLength = string.length();
		if (stringLength > length) {
			throw new IllegalArgumentException("String is too long: " + stringLength + " > " + length);
		}
		if (stringLength == length) {
			return string;
		}
		return zeroPadInternal(string, length);
	}

	/**
	 * Pad or truncate the specified string to the specified length.
	 * If the string is already the specified length, it is returned unchanged.
	 * If it is longer than the specified length, only the last part of the string is returned.
	 * If it is shorter than the specified length, it is padded with zeros at the front.
	 * String#zeroPadOrTruncate(int)
	 */
	public static String zeroPadOrTruncate(String string, int length) {
		int stringLength = string.length();
		if (stringLength == length) {
			return string;
		}
		if (stringLength > length) {
			return string.substring(stringLength - length);
		}
		return zeroPadInternal(string, length);
	}

	/**
	 * Zero-pad the specified string without validating the parms.
	 */
	private static String zeroPadInternal(String string, int length) {
		char[] a = new char[length];
		int stringLength = string.length();
		int padLength = length - stringLength;
		string.getChars(0, stringLength, a, padLength);
		Arrays.fill(a, 0, padLength, '0');
		return new String(a);
	}


	// ********** removing characters **********

	/**
	 * Remove the first occurrence of the specified character
	 * from the specified string and return the result.
	 * String#removeFirstOccurrence(char)
	 */
	public static String removeFirstOccurrence(String string, char character) {
		int index = string.indexOf(character);
		if (index == -1) {
			// character not found
			return string;
		}
		if (index == 0) {
			// character found at the front of string
			return string.substring(1);
		}
		int last = string.length() - 1;
		if (index == last) {
			// character found at the end of string
			return string.substring(0, last);
		}
		// character found somewhere in the middle of the string
		return string.substring(0, index).concat(string.substring(index + 1));
	}

	/**
	 * Remove all occurrences of the specified character
	 * from the specified string and return the result.
	 * String#removeAllOccurrences(char)
	 */
	public static String removeAllOccurrences(String string, char character) {
		StringBuffer sb = new StringBuffer(string.length());
		removeAllOccurrencesOn(string, character, sb);
		return sb.toString();
	}

	/**
	 * Remove all occurrences of the specified character
	 * from the specified string and append the result to the
	 * specified string buffer.
	 * String#removeAllOccurrencesOn(char, StringBuffer)
	 */
	public static void removeAllOccurrencesOn(String string, char character, StringBuffer sb) {
		int len = string.length();
		for (int i = 0; i < len; i++) {
			char c = string.charAt(i);
			if (c != character) {
				sb.append(c);
			}
		}
	}

	/**
	 * Remove all the spaces from the specified string and return the result.
	 * String#removeAllSpaces()
	 */
	public static String removeAllSpaces(String string) {
		return removeAllOccurrences(string, ' ');
	}


	// ********** common prefix **********

	/**
	 * Return the length of the common prefix shared by the specified strings.
	 * String#commonPrefixLength(String)
	 */
	public static int commonPrefixLength(String s1, String s2) {
		return commonPrefixLength(s1.toCharArray(), s2.toCharArray());
	}

	/**
	 * Return the length of the common prefix shared by the specified strings.
	 */
	public static int commonPrefixLength(char[] s1, char[] s2) {
		return commonPrefixLengthInternal(s1, s2, Math.min(s1.length, s2.length));
	}

	/**
	 * Return the length of the common prefix shared by the specified strings;
	 * but limit the length to the specified maximum.
	 * String#commonPrefixLength(String, int)
	 */
	public static int commonPrefixLength(String s1, String s2, int max) {
		return commonPrefixLength(s1.toCharArray(), s2.toCharArray());
	}

	/**
	 * Return the length of the common prefix shared by the specified strings;
	 * but limit the length to the specified maximum.
	 */
	public static int commonPrefixLength(char[] s1, char[] s2, int max) {
		return commonPrefixLengthInternal(s1, s2, Math.min(max, Math.min(s1.length, s2.length)));
	}

	/**
	 * Return the length of the common prefix shared by the specified strings;
	 * but limit the length to the specified maximum. Assume the specified
	 * maximum is less than the lengths of the specified strings.
	 */
	private static int commonPrefixLengthInternal(char[] s1, char[] s2, int max) {
		for (int i = 0; i < max; i++) {
			if (s1[i] != s2[i]) {
				return i;
			}
		}
		return max;	// all the characters up to 'max' are the same
	}


	// ********** capitalization **********

	/**
	 * no zero-length check or lower case check
	 */
	private static char[] capitalizeInternal(char[] string) {
		string[0] = Character.toUpperCase(string[0]);
		return string;
	}

	/**
	 * Modify and return the specified string with
	 * its first letter capitalized.
	 */
	public static char[] capitalize(char[] string) {
		if ((string.length == 0) || Character.isUpperCase(string[0])) {
			return string;
		}
		return capitalizeInternal(string);
	}

	/**
	 * Return the specified string with its first letter capitalized.
	 * String#capitalize()
	 */
	public static String capitalize(String string) {
		if ((string.length() == 0) || Character.isUpperCase(string.charAt(0))) {
			return string;
		}
		return new String(capitalizeInternal(string.toCharArray()));
	}

	/**
	 * no zero-length check or upper case check
	 */
	private static void capitalizeOnInternal(char[] string, StringBuffer sb) {
		sb.append(Character.toUpperCase(string[0]));
		sb.append(string, 1, string.length - 1);
	}

	/**
	 * Append the specified string to the specified string buffer
	 * with its first letter capitalized.
	 */
	public static void capitalizeOn(char[] string, StringBuffer sb) {
		if (string.length == 0) {
			return;
		}
		if (Character.isUpperCase(string[0])) {
			sb.append(string);
		} else {
			capitalizeOnInternal(string, sb);
		}
	}

	/**
	 * Append the specified string to the specified string buffer
	 * with its first letter capitalized.
	 * String#capitalizeOn(StringBuffer)
	 */
	public static void capitalizeOn(String string, StringBuffer sb) {
		if (string.length() == 0) {
			return;
		}
		if (Character.isUpperCase(string.charAt(0))) {
			sb.append(string);
		} else {
			capitalizeOnInternal(string.toCharArray(), sb);
		}
	}

	/**
	 * no zero-length check or upper case check
	 */
	private static void capitalizeOnInternal(char[] string, Writer writer) {
		try {
			writer.write(Character.toUpperCase(string[0]));
			writer.write(string, 1, string.length - 1);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * checked exceptions suck
	 */
	private static void writeStringOn(char[] string, Writer writer) {
		try {
			writer.write(string);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Append the specified string to the specified string buffer
	 * with its first letter capitalized.
	 */
	public static void capitalizeOn(char[] string, Writer writer) {
		if (string.length == 0) {
			return;
		}
		if (Character.isUpperCase(string[0])) {
			writeStringOn(string, writer);
		} else {
			capitalizeOnInternal(string, writer);
		}
	}

	/**
	 * checked exceptions suck
	 */
	private static void writeStringOn(String string, Writer writer) {
		try {
			writer.write(string);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Append the specified string to the specified string buffer
	 * with its first letter capitalized.
	 * String#capitalizeOn(Writer)
	 */
	public static void capitalizeOn(String string, Writer writer) {
		if (string.length() == 0) {
			return;
		}
		if (Character.isUpperCase(string.charAt(0))) {
			writeStringOn(string, writer);
		} else {
			capitalizeOnInternal(string.toCharArray(), writer);
		}
	}

	/**
	 * no zero-length check or lower case check
	 */
	private static char[] uncapitalizeInternal(char[] string) {
		string[0] = Character.toLowerCase(string[0]);
		return string;
	}

	private static boolean stringNeedNotBeUncapitalized(char[] string) {
		if (string.length == 0) {
			return true;
		}
		if (Character.isLowerCase(string[0])) {
			return true;
		}
		return false;
	}
	
	private static boolean stringNeedNotBeUncapitalizedJavaBean(char[] string) {
		if (stringNeedNotBeUncapitalized(string)) {
			return true;
		}
		// if both the first and second characters are capitalized,
		// return the string unchanged
		if ((string.length > 1)
				&& Character.isUpperCase(string[1])
				&& Character.isUpperCase(string[0])){
			return true;
		}
		return false;
	}

	/**
	 * Modify and return the specified string with its
	 * first letter converted to lower case.
	 * (Unless both the first and second letters are upper case,
	 * in which case the string is returned unchanged.)
	 */
	
	public static char[] uncapitalizeJavaBean(char[] string) {
		if (stringNeedNotBeUncapitalizedJavaBean(string)) {
			return string;
		}
		return uncapitalizeInternal(string);
	}
	
	/**
	 * Modify and return the specified string with its
	 * first letter converted to lower case.
	 */
	
	public static char[] uncapitalize(char[] string) {
		if (stringNeedNotBeUncapitalized(string)) {
			return string;
		}
		return uncapitalizeInternal(string);
	}

	private static boolean stringNeedNotBeUncapitalized(String string) {
		if (string.length() == 0) {
			return true;
		}
		if (Character.isLowerCase(string.charAt(0))) {
			return true;
		}
		return false;
	}
	
	private static boolean stringNeedNotBeUncapitalizedJavaBean(String string) {
		if (stringNeedNotBeUncapitalized(string)) {
			return true;
		}
		// if both the first and second characters are capitalized,
		// return the string unchanged
		if ((string.length() > 1)
				&& Character.isUpperCase(string.charAt(1))
				&& Character.isUpperCase(string.charAt(0))){
			return true;
		}
		return false;
	}

	/**
	 * Return the specified string with its first letter converted to lower case.
	 * (Unless both the first and second letters are upper case,
	 * in which case the string is returned unchanged.)
	 * String#uncapitalize()
	 */
	
	public static String uncapitalizeJavaBean(String string) {
		if (stringNeedNotBeUncapitalizedJavaBean(string)) {
			return string;
		}
		return new String(uncapitalizeInternal(string.toCharArray()));
	}

	/**
	 * Return the specified string with its first letter converted to lower case.
	 */
	public static String uncapitalize(String string) {
		if (stringNeedNotBeUncapitalized(string)) {
			return string;
		}
		return new String(uncapitalizeInternal(string.toCharArray()));
	}

	/**
	 * no zero-length check or lower case check
	 */
	private static void uncapitalizeOnInternal(char[] string, StringBuffer sb) {
		sb.append(Character.toLowerCase(string[0]));
		sb.append(string, 1, string.length - 1);
	}

	/**
	 * Append the specified string to the specified string buffer
	 * with its first letter converted to lower case.
	 * (Unless both the first and second letters are upper case,
	 * in which case the string is returned unchanged.)
	 */
	public static void uncapitalizeOn(char[] string, StringBuffer sb) {
		if (stringNeedNotBeUncapitalized(string)) {
			sb.append(string);
		} else {
			uncapitalizeOnInternal(string, sb);
		}
	}

	/**
	 * Append the specified string to the specified string buffer
	 * with its first letter converted to lower case.
	 * (Unless both the first and second letters are upper case,
	 * in which case the string is returned unchanged.)
	 * String#uncapitalizeOn(StringBuffer)
	 */
	public static void uncapitalizeOn(String string, StringBuffer sb) {
		if (stringNeedNotBeUncapitalized(string)) {
			sb.append(string);
		} else {
			uncapitalizeOnInternal(string.toCharArray(), sb);
		}
	}

	/**
	 * no zero-length check or upper case check
	 */
	private static void uncapitalizeOnInternal(char[] string, Writer writer) {
		try {
			writer.write(Character.toLowerCase(string[0]));
			writer.write(string, 1, string.length - 1);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Append the specified string to the specified string buffer
	 * with its first letter converted to lower case.
	 * (Unless both the first and second letters are upper case,
	 * in which case the string is returned unchanged.)
	 */
	public static void uncapitalizeOn(char[] string, Writer writer) {
		if (stringNeedNotBeUncapitalized(string)) {
			writeStringOn(string, writer);
		} else {
			uncapitalizeOnInternal(string, writer);
		}
	}

	/**
	 * Append the specified string to the specified string buffer
	 * with its first letter converted to lower case.
	 * (Unless both the first and second letters are upper case,
	 * in which case the string is returned unchanged.)
	 * String#uncapitalizeOn(Writer)
	 */
	public static void uncapitalizeOn(String string, Writer writer) {
		if (stringNeedNotBeUncapitalized(string)) {
			writeStringOn(string, writer);
		} else {
			uncapitalizeOnInternal(string.toCharArray(), writer);
		}
	}


	// ********** #toString() helper methods **********

	/**
	 * Build a "standard" #toString() result for the specified object
	 * and additional information:
	 * 	ClassName[00F3EE42] (add'l info)
	 */
	public static String buildToStringFor(Object o, Object additionalInfo) {
		StringBuffer sb = new StringBuffer();
		buildSimpleToStringOn(o, sb);
		sb.append(" (");
		sb.append(additionalInfo);
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Build a "standard" simple #toString() result for the specified object:
	 * 	ClassName[00F3EE42]
	 */
	public static String buildToStringFor(Object o) {
		StringBuffer sb = new StringBuffer();
		buildSimpleToStringOn(o, sb);
		return sb.toString();
	}

	/**
	 * Append a "standard" simple #toString() for the specified object to
	 * the specified string buffer:
	 * 	ClassName[00F3EE42]
	 */
	public static void buildSimpleToStringOn(Object o, StringBuffer sb) {
		sb.append(ClassTools.toStringClassNameForObject(o));
		sb.append('[');
		// use System#identityHashCode(Object), since Object#hashCode() may be overridden
		sb.append(zeroPad(Integer.toHexString(System.identityHashCode(o)).toUpperCase(), 8));
		sb.append(']');
	}


	// ********** queries **********

	/**
	 * Return whether the specified string is null, empty, or contains
	 * only whitespace characters.
	 */
	public static boolean stringIsEmpty(String text) {
		if ((text == null) || (text.length() == 0)) {
			return true;
		}
		for (int i = text.length(); i-- > 0; ) {
			if ( ! Character.isWhitespace(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return whether the specified strings are equal, ignoring case.
	 * Check for nulls.
	 */
	public static boolean stringsAreEqualIgnoreCase(String s1, String s2) {
		if ((s1 == null) && (s2 == null)) {
			return true;	// both are null
		}
		if ((s1 == null) || (s2 == null)) {
			return false;	// one is null but the other is not
		}
		return s1.equalsIgnoreCase(s2);
	}
	
	/**
	 * Return whether the specified strings are equal.
	 * Check for nulls.
	 */
	public static boolean stringsAreEqual(String s1, String s2) {
		if ((s1 == null) && (s2 == null)) {
			return true;	// both are null
		}
		if ((s1 == null) || (s2 == null)) {
			return false;	// one is null but the other is not
		}
		return s1.equals(s2);
	}

	private static final char[] VOWELS = new char[] {'a', 'e', 'i', 'o', 'u'};

	public static boolean charIsVowel(char c) {
		return CollectionTools.contains(VOWELS, Character.toLowerCase(c));
	}


	// ********** conversions **********

	public static String replaceHTMLBreaks(String string) {
		return string.replaceAll("<br>", CR).replaceAll("<BR>", CR);
	}

	/**
	 * Convert the specified "camel back" string to an "all caps" string:
	 * "largeProject" -> "LARGE_PROJECT"
	 */
	public static String convertCamelBackToAllCaps(String camelBackString) {
		int len = camelBackString.length();
		if (len == 0) {
			return "";
		}
		char prev = 0;	// assume 0 is not a valid char
		char c = 0;
		char next = camelBackString.charAt(0);
		StringBuffer sb = new StringBuffer(len * 2);
		for (int i = 1; i <= len; i++) {	// NB: start at 1 and end at len!
			c = next;
			next = ((i == len) ? 0 : camelBackString.charAt(i));
			if (camelBackWordBreak(prev, c, next)) {
				sb.append('_');
			}
			sb.append(Character.toUpperCase(c));
			prev = c;
		}
		return sb.toString();
	}

	/**
	 * Convert the specified "camel back" string to an "all caps" string:
	 * "largeProject" -> "LARGE_PROJECT"
	 * Limit the resulting string to the specified maximum length.
	 */
	public static String convertCamelBackToAllCaps(String camelBackString, int maxLength) {
//		String result = convertToAllCaps(camelBackString);
//		return (result.length() > maxLength) ? result.substring(0, maxLength) : result;
		int len = camelBackString.length();
		if ((len == 0) || (maxLength == 0)) {
			return "";
		}
		char prev = 0;	// assume 0 is not a valid char
		char c = 0;
		char next = camelBackString.charAt(0);
		StringBuffer sb = new StringBuffer(maxLength);
		for (int i = 1; i <= len; i++) {	// NB: start at 1 and end at len!
			c = next;
			next = ((i == len) ? 0 : camelBackString.charAt(i));
			if (camelBackWordBreak(prev, c, next)) {
				sb.append('_');
				if (sb.length() == maxLength) {
					return sb.toString();
				}
			}
			sb.append(Character.toUpperCase(c));
			if (sb.length() == maxLength) {
				return sb.toString();
			}
			prev = c;
		}
		return sb.toString();
	}

	/**
	 * Return whether the specified series of characters occur at
	 * a "camel back" work break:
	 *     "*aa" -> false
	 *     "*AA" -> false
	 *     "*Aa" -> false
	 *     "AaA" -> false
	 *     "AAA" -> false
	 *     "aa*" -> false
	 *     "AaA" -> false
	 *     "aAa" -> true
	 *     "AA*" -> false
	 *     "AAa" -> true
	 * where '*' == any char
	 */
	private static boolean camelBackWordBreak(char prev, char c, char next) {
		if (prev == 0) {	// start of string
			return false;
		}
		if (Character.isLowerCase(c)) {
			return false;
		}
		if (Character.isLowerCase(prev)) {
			return true;
		}
		if (next == 0) {	// end of string
			return false;
		}
		return Character.isLowerCase(next);
	}

	/**
	 * Convert the specified "all caps" string to a "camel back" string:
	 * "LARGE_PROJECT" -> "LargeProject"
	 * Capitalize the first letter.
	 */
	public static String convertAllCapsToCamelBack(String allCapsString) {
		return convertAllCapsToCamelBack(allCapsString, true);
	}

	/**
	 * Convert the specified "all caps" string to a "camel back" string:
	 * "LARGE_PROJECT" -> "largeProject"
	 * Optionally capitalize the first letter.
	 */
	public static String convertAllCapsToCamelBack(String allCapsString, boolean capitalizeFirstLetter) {
		int len = allCapsString.length();
		if (len == 0) {
			return "";
		}
		char prev = 0;
		char c = 0;
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {	// NB: start at 1
			prev = c;
			c = allCapsString.charAt(i);
			if (c == '_') {
				continue;
			}
			if (sb.length() == 0) {
				if (capitalizeFirstLetter) {
					sb.append(Character.toUpperCase(c));
				} else {
					sb.append(Character.toLowerCase(c));
				}
			} else {
				if (prev == '_') {
					sb.append(Character.toUpperCase(c));
				} else {
					sb.append(Character.toLowerCase(c));
				}
			}
		}
		return sb.toString();
	}


	// ********** constructor **********

	/**
	 * Suppress default constructor, ensuring non-instantiability.
	 */
	private StringTools() {
		super();
		throw new UnsupportedOperationException();
	}
	
	//************* comparison **************
	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @return
	 */
	public static double calculateHighestMatchWeight(String string1,String string2) {
		return calculateHighestMatchWeight(string1, string2, new char[0], MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @return
	 */
	public static double calculateHighestMatchWeight(String string1, String string2, char ignoreCharacter) {
		return calculateHighestMatchWeight(string1, string2, new char[] { ignoreCharacter }, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static double calculateHighestMatchWeight(String string1, String string2, char ignoreCharacter, int minimumMatchingLetterCount) {
		return calculateHighestMatchWeight(string1, string2, new char[] { ignoreCharacter }, minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @return
	 */
	public static double calculateHighestMatchWeight(String string1, String string2, char[] ignoreCharacters) {
		return calculateHighestMatchWeight(string1, string2, ignoreCharacters, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static double calculateHighestMatchWeight(String string1, String string2, char[] ignoreCharacters, int minimumMatchingLetterCount) {
		if ((string1 == null) || (string2 == null))
			return 0.0;

		// Test 1: Look if they are equals
		if (string1.equals(string2)) {
			return 1.0;
		}

		// Test 2: Look if string1 and string2 are the same except for the list of
		//         characters to ignore
		if (equals(string1, string2, ignoreCharacters)) {
			double top = string2.length();
			double bottom = string1.length();
			return top/bottom;
		}

		// Test 3: Look if string2 is a substring of string1
		if (string1.indexOf(string2) > -1) {
			double top = string2.length();
			double bottom = string1.length();
			return top/bottom;
		}

		// Test 4: Look if a part - continuous - of string2 is a substring of string1
		int weight = weightContainsPartContinuous(string1, string2, ignoreCharacters, minimumMatchingLetterCount);

		if (weight >= minimumMatchingLetterCount) {
			double weight1 = (double) weight / (double) string1.length();
			double weight2 = (double) weight / (double) string2.length();
			return (weight1 + weight2) / 2;
		}

		// Test 5: Find all the substrings and then the common letters between
		//         string1 and string2 and return the count
		weight = weightContainsPart(string1, string2, ignoreCharacters, minimumMatchingLetterCount);

		if (weight >= minimumMatchingLetterCount) {
			double weight1 = (double) weight / (double) string1.length();
			double weight2 = (double) weight / (double) string2.length();
			return (weight1 + weight2) / 2;
		}

		return 0.0;
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static double calculateHighestMatchWeight(String string1, String string2, int minimumMatchingLetterCount) {
		return calculateHighestMatchWeight(string1, string2, new char[0], minimumMatchingLetterCount);
	}

	/**
	 * Calculates the maximum weight between the given two strings. The case is
	 * not used.
	 * <p>
	 * The weight can be one of the following:
	 * <ul>
	 * <li>{@link #NO_MATCH} No match was found
	 * <li>{@link #STRING_CONTAINS_PART_OF_THE_OTHER}
	 * <li>{@link #STRING_CONTAINS_THE_OTHER}
	 * <li>{@link #STRINGS_SUBSET_NON_CONTINUOUS}
	 * <li>{@link #STRINGS_SUBSET_CONTINUOUS}
	 * <li>{@link #STRINGS_EQUAL_WITH_DELIMITOR} Both strings are equals by
	 * ignoring one character
	 * <li>{@link #STRINGS_EQUAL} Both strings are equals
	 * </ul>
	 *
	 * @param string1 The string that is used to verify if the sub-string
	 * could be the equals, contained or a sub-set
	 * @param string2 The string to use to check if it is equal, contained or
	 * partially contained in the main string
	 * @return The weight of the match
	 */
	public static int calculateMatchWeight(String string1, String string2) {
		return calculateMatchWeight(string1, string2, new char[0], MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates the maximum weight between the given two strings. The case is
	 * not used.
	 * <p>
	 * The weight can be one of the following:
	 * <ul>
	 * <li>{@link #NO_MATCH} No match was found
	 * <li>{@link #STRING_CONTAINS_PART_OF_THE_OTHER}
	 * <li>{@link #STRING_CONTAINS_THE_OTHER}
	 * <li>{@link #STRINGS_SUBSET_CONTINUOUS}
	 * <li>{@link #STRINGS_EQUAL_WITH_DELIMITOR} Both strings are equals by
	 * ignoring one character
	 * <li>{@link #STRINGS_EQUAL} Both strings are equals
	 * </ul>
	 *
	 * @param string1 The string that is used to verify if the sub-string
	 * could be the equals, contained or a sub-set
	 * @param string2 The string to use to check if it is equal, contained or
	 * partially contained in the main string
	 * @param ignoreCharacter The character that can be ignored during the
	 * calculation or '\0' to not ignore any character
	 * @return The weight of the match
	 */
	public static int calculateMatchWeight(String string1, String string2, char ignoreCharacter) {
		return calculateMatchWeight(string1, string2, new char[] { ignoreCharacter }, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static int calculateMatchWeight(String string1, String string2, char ignoreCharacter, int minimumMatchingLetterCount) {
		return calculateMatchWeight(string1, string2, new char[] { ignoreCharacter }, minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @return
	 */
	public static int calculateMatchWeight(String string1, String string2, char[] ignoreCharacters) {
		return calculateMatchWeight(string1, string2, ignoreCharacters, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates the maximum weight between the given two strings. The case is
	 * not used.
	 * <p>
	 * The weight can be one of the following:
	 * <ul>
	 * <li>{@link #NO_MATCH} No match was found
	 * <li>{@link #STRING_CONTAINS_PART_OF_THE_OTHER}
	 * <li>{@link #STRING_CONTAINS_THE_OTHER}
	 * <li>{@link #STRINGS_SUBSET_CONTINUOUS}
	 * <li>{@link #STRINGS_EQUAL_WITH_DELIMITOR} Both strings are equals by
	 * ignoring one character
	 * <li>{@link #STRINGS_EQUAL} Both strings are equals
	 * </ul>
	 *
	 * @param string1 The string that is used to verify if the sub-string
	 * could be the equals, contained or a sub-set
	 * @param string2 The string to use to check if it is equal, contained or
	 * partially contained in the main string
	 * @param ignoreCharacter The character that can be ignored during the
	 * calculation or '\0' to not ignore any character
	 * @return The weight of the match
	 */
	public static int calculateMatchWeight(String string1, String string2, char[] ignoreCharacters, int minimumMatchingLetterCount) {
		if ((string1 == null) || (string2 == null))
			return NO_MATCH;

		// Test 1: Look if they are equals
		if (string1.equals(string2)) {
			return STRINGS_EQUAL;
		}

		// Test 2: Look if they are the same except for the list of characters to
		//         ignore
		if (equals(string1, string2, ignoreCharacters)) {
			return STRINGS_EQUAL_WITH_IGNORE_CHARACTERS;
		}

		// Test 3: Look if string2 is a substring of string1
		if (string1.indexOf(string2) > -1) {
			return STRING_CONTAINS;
		}

		// Test 4: Look if a part - continuous - of string2 is a substring of string1
		if (containsPartContinuous(string1, string2, ignoreCharacters, minimumMatchingLetterCount)) {
			return STRING_CONTAINS_PART_CONTINUOUS;
		}

		// Test 5: Look if a part of string2 is a substring of string1
		if (containsPart(string1, string2, ignoreCharacters, minimumMatchingLetterCount)) {
			return STRING_CONTAINS_PART;
		}

		return NO_MATCH;
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static int calculateMatchWeight(String string1, String string2, int minimumMatchingLetterCount) {
		return calculateMatchWeight(string1, string2, new char[0], minimumMatchingLetterCount);
	}

	/**
	 * Calculates the maximum weight between the given two strings. The case is
	 * not used.
	 * <p>
	 * The weight can be one of the following:
	 * <ul>
	 * <li>{@link #NO_MATCH} No match was found
	 * <li>{@link #STRING_CONTAINS_PART_OF_THE_OTHER}
	 * <li>{@link #STRING_CONTAINS_THE_OTHER}
	 * <li>{@link #STRINGS_SUBSET_NON_CONTINUOUS}
	 * <li>{@link #STRINGS_SUBSET_CONTINUOUS}
	 * <li>{@link #STRINGS_EQUAL_WITH_DELIMITOR} Both strings are equals by
	 * ignoring one character
	 * <li>{@link #STRINGS_EQUAL} Both strings are equals
	 * </ul>
	 *
	 * @param string1 The string that is used to verify if the sub-string
	 * could be the equals, contained or a sub-set
	 * @param string2 The string to use to check if it is equal, contained
	 * or partially contained in the main string
	 * @return The weight of the match
	 */
	public static int calculateMatchWeightIgnoreCase(String string1, String string2) {
		return calculateMatchWeightIgnoreCase(string1, string2, new char[0], MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates the maximum weight between the given two strings. The case is
	 * not used.
	 * <p>
	 * The weight can be one of the following:
	 * <ul>
	 * <li>{@link #NO_MATCH} No match was found
	 * <li>{@link #STRING_CONTAINS_PART_OF_THE_OTHER}
	 * <li>{@link #STRING_CONTAINS_THE_OTHER}
	 * <li>{@link #STRINGS_SUBSET_CONTINUOUS}
	 * <li>{@link #STRINGS_EQUAL_WITH_DELIMITOR} Both strings are equals by
	 * ignoring one character
	 * <li>{@link #STRINGS_EQUAL} Both strings are equals
	 * </ul>
	 *
	 * @param string1 The string that is used to verify if the sub-string
	 * could be the equals, contained or a sub-set
	 * @param string2 The string to use to check if it is equal, contained
	 * or partially contained in the main string
	 * @param ignoreCharacter The character that can be ignored during the
	 * calculation or '\0' to not ignore any character
	 * @return The weight of the match
	 */
	public static int calculateMatchWeightIgnoreCase(String string1, String string2, char ignoreCharacter) {
		return calculateMatchWeightIgnoreCase(string1, string2, new char[] { ignoreCharacter }, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates the maximum weight between the given two strings. The case is
	 * not used.
	 * <p>
	 * The weight can be one of the following:
	 * <ul>
	 * <li>{@link #NO_MATCH} No match was found
	 * <li>{@link #STRING_CONTAINS_PART_OF_THE_OTHER}
	 * <li>{@link #STRING_CONTAINS_THE_OTHER}
	 * <li>{@link #STRINGS_SUBSET_CONTINUOUS}
	 * <li>{@link #STRINGS_EQUAL_WITH_DELIMITOR} Both strings are equals by
	 * ignoring one character
	 * <li>{@link #STRINGS_EQUAL} Both strings are equals
	 * </ul>
	 *
	 * @param string1 The string that is used to verify if the sub-string
	 * could be the equals, contained or a sub-set
	 * @param string2 The string to use to check if it is equal, contained
	 * or partially contained in the main string
	 * @param ignoreCharacter The character that can be ignored during the
	 * calculation or '\0' to not ignore any character
	 * @return The weight of the match
	 */
	public static int calculateMatchWeightIgnoreCase(String string1, String string2, char[] ignoreCharacters) {
		return calculateMatchWeightIgnoreCase(string1, string2, ignoreCharacters, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static int calculateMatchWeightIgnoreCase(String string1, String string2, char[] ignoreCharacters, int minimumMatchingLetterCount) {
		if ((string1 == null) || (string2 == null))
			return NO_MATCH;

		return calculateMatchWeight(string1.toLowerCase(), string2.toLowerCase(), ignoreCharacters, minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static int calculateMatchWeightIgnoreCase(String string1, String string2, int minimumMatchingLetterCount) {
		return calculateMatchWeightIgnoreCase(string1, string2, new char[0], minimumMatchingLetterCount);
	}
	
	/**
	 * Determines whether the given two strings are equals. A <code>null</code>
	 * string is also checked.
	 *
	 * @param string1 The first string to compare
	 * @param string2 The second string to compare
	 * @return <code>true</code> if both string are equals; <code>false</code> if
	 * one of them is <code>null</code> or both are <code>null</code>
	 */
	public static boolean equals(String string1, String string2) {
		return (string1 != null) && string1.equals(string2) || (string2 != null) && string2.equals(string1);
	}

	/**
	 * Determines if the first string and the second string are the same minus
	 * ignoring the given character.
	 * <p>
	 * Example:<br>
	 * equals("INT_DOUBLE", "_INT_DOUBLE_", '_') returns <code>true</code><br>
	 * 
	 * @param string1 The first string to compare
	 * @param string2 The second string to compare
	 * @param ignoreCharacter The character to ignore when comparing the strings
	 * @return <code>true</code> if both strings are equivalent; <code>false</code>
	 * otherwise
	 */
	public static boolean equals(String string1, String string2, char ignoreCharacter) {
		return equals(string1, string2, new char[] { ignoreCharacter });
	}

	/**
	 * Determines if the first string and the second string are the same minus
	 * the given list of characters to ignore.
	 * <p>
	 * Example:<br>
	 * equals("INT_DOUBLE", "_INT_DOUBLE_", '_') returns <code>true</code><br>
	 * 
	 * @param string1 The first string to compare
	 * @param string2 The second string to compare
	 * @param ignoreCharacters The list of characters to ignore when comparing
	 * the strings
	 * @return <code>true</code> if both strings are equivalent; <code>false</code>
	 * otherwise
	 */
	public static boolean equals(String string1, String string2, char[] ignoreCharacters) {
		if ((string1 == null) || (string2 == null))
			return false;

		for (int index = 0; index < ignoreCharacters.length; index++) {
			String character = String.valueOf(ignoreCharacters[index]);

			string1 = string1.replaceAll(character, "");
			string2 = string2.replaceAll(character, "");
		}

		return string1.equals(string2);
	}

	/**
	 * Determines whether the given two strings are equals by ignoring the case
	 * sensitivity. A <code>null</code> string is also checked.
	 *
	 * @param string1 The first string to compare
	 * @param string2 The second string to compare
	 * @return <code>true</code> if both string are equals; <code>false</code> if
	 * one of them is <code>null</code> or both are <code>null</code>
	 */
	public static boolean equalsIgnoreCase(String string1, String string2) {
		return (string1 != null) && string1.equalsIgnoreCase(string2) || (string2 != null) && string2.equalsIgnoreCase(string1);
	}

	/**
	 * Determines if the first string and the second string are the same minus
	 * the given list of characters to ignore.
	 * <p>
	 * Example:<br>
	 * equals("INT_DOUBLE", "_INT_double_", '_') returns <code>true</code><br>
	 * 
	 * @param string1 The first string to compare
	 * @param string2 The second string to compare
	 * @param ignoreCharacter The character to ignore when comparing the strings
	 * @return <code>true</code> if both strings are equivalent; <code>false</code>
	 * otherwise
	 * @return <code>true</code> if both strings are equivalent; <code>false</code>
	 * otherwise
	 */
	public static boolean equalsIgnoreCase(String string1, String string2, char ignoreCharacter) {
		if ((string1 == null) || (string2 == null))
			return false;

		return equals(string1.toLowerCase(), string2.toLowerCase(), new char[] { ignoreCharacter });
	}

	/**
	 * Determines if the first string and the second string are the same minus
	 * the given list of characters to ignore.
	 * <p>
	 * Example:<br>
	 * equals("INT_DOUBLE", "_INT_DOUBLE_", '_') returns <code>true</code><br>
	 * 
	 * @param string1 The first string to compare
	 * @param string2 The second string to compare
	 * @param ignoreCharacters The list of characters to ignore when comparing
	 * the strings
	 * @return <code>true</code> if both strings are equivalent; <code>false</code>
	 * otherwise
	 */
	public static boolean equalsIgnoreCase(String string1, String string2, char[] ignoreCharacters) {
		if ((string1 == null) || (string2 == null))
			return false;

		return equals(string1.toLowerCase(), string2.toLowerCase(), ignoreCharacters);
	}
	
	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @return
	 */
	public static int weightContainsPart(String string1, String string2) {
		return weightContainsPart(string1, string2, new char[0], MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @return
	 */
	public static int weightContainsPart(String string1, String string2, char ignoreCharacter) {
		return weightContainsPart(string1, string2, new char[] { ignoreCharacter }, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static int weightContainsPart(String string1, String string2, char ignoreCharacter, int minimumMatchingLetterCount) {
		return weightContainsPart(string1, string2, new char[] { ignoreCharacter }, minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @return
	 */
	public static int weightContainsPart(String string1, String string2, char[] ignoreCharacters) {
		return weightContainsPart(string1, string2, ignoreCharacters, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static int weightContainsPart(String string1, String string2, char[] ignoreCharacters, int minimumMatchingLetterCount) {
		if ((string1 == null) || (string2 == null) ||(minimumMatchingLetterCount < 2))
			return 0;

		if (string1.length() < minimumMatchingLetterCount)
			minimumMatchingLetterCount = string1.length();

		string1 = removeIgnoreCharacters(string1, ignoreCharacters);
		string2 = removeIgnoreCharacters(string2, ignoreCharacters);

		Position[] positions = (string1.length() < string2.length()) ?
										weightContainsPartImp(string2, string1) :
										weightContainsPartImp(string1, string2);
		int count = 0;

		// TODO: Need to check the order and remove any substrings that are not
		// allowed. Here an example: string1=JohnPascalandAnuj and
		// string2=JohnAnujPascal, the result is 14 when it should be 10
		for (int index = positions.length; --index >= 0;) {
			if (Position.canAddWeight(positions, index)) {
				count += positions[index].length;
			}
			else if (index > 0) {
				Position[] oldPositions = positions;
				positions = new Position[positions.length - 1];

				System.arraycopy(oldPositions, 0, positions, 0, index);
				System.arraycopy(oldPositions, index, positions, index, oldPositions.length - index - 1);
			}
		}

		if (count < minimumMatchingLetterCount)
			return 0;

		return count;
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static int weightContainsPart(String string1, String string2, int minimumMatchingLetterCount) {
		return weightContainsPart(string1, string2, new char[0], minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @return
	 */
	public static int weightContainsPartContinuous(String string1, String string2) {
		return weightContainsPartContinuous(string1, string2, new char[0], MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @return
	 */
	public static int weightContainsPartContinuous(String string1, String string2, char ignoreCharacter) {
		return weightContainsPartContinuous(string1, string2, new char[] { ignoreCharacter }, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static int weightContainsPartContinuous(String string1, String string2, char ignoreCharacter, int minimumMatchingLetterCount) {
		return weightContainsPartContinuous(string1, string2, new char[] { ignoreCharacter }, minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @return
	 */
	public static int weightContainsPartContinuous(String string1, String string2, char[] ignoreCharacters) {
		return weightContainsPartContinuous(string1, string2, ignoreCharacters, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static int weightContainsPartContinuous(String string1, String string2, char[] ignoreCharacters, int minimumMatchingLetterCount) {
		if ((string1 == null) || (string2 == null) || (minimumMatchingLetterCount < 2))
			return 0;

		string1 = removeIgnoreCharacters(string1, ignoreCharacters);
		string2 = removeIgnoreCharacters(string2, ignoreCharacters);

//		if (string2.length() < minimumMatchingLetterCount)
//			minimumMatchingLetterCount = string2.length();
//
//		if (string1.length() < minimumMatchingLetterCount)
//			minimumMatchingLetterCount = Math.min(string1.length(), minimumMatchingLetterCount);

		return weightContainsPartContinuousImp(string1, string2, minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static int weightContainsPartContinuous(String string1, String string2, int minimumMatchingLetterCount) {
		return weightContainsPartContinuous(string1, string2, new char[0], minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	private static int weightContainsPartContinuousImp(String string1, String string2, int minimumMatchingLetterCount) {
		int currentLength = string2.length();

		// Can't go smaller than the minimum count
		if (currentLength < minimumMatchingLetterCount)
			return 0;

		// First test to see if string2 is a substring of string1
		if (string1.indexOf(string2) > -1)
			return currentLength;

		// Truncate on the left and start to test it recursively
		int weightLeft = weightContainsPartContinuousImp(string1, string2.substring(1, currentLength), minimumMatchingLetterCount);

		// Truncate on the right and start to test it recursively
		int weightRight = weightContainsPartContinuousImp(string1, string2.substring(0, currentLength - 1), minimumMatchingLetterCount);

		return Math.max(weightLeft, weightRight);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @return
	 */
	private static Position[] weightContainsPartImp(String string1, String string2) {
		int currentLength = string2.length();

		// Can't go smaller than the minimum count
		if (currentLength == 0)
			return new Position[0];

		// First test to see if string2 is a substring of string1
		int index = string1.indexOf(string2);

		if (index > -1) {
			Position position = new Position(index, currentLength);
			int string1Length = string1.length();

			// Gather all the locations of string2 in string1
			while ((index > -1) && (string1Length - index - 2*currentLength >= 0)) {
				index = string1.indexOf(string2, index + currentLength);

				if (index > -1)
					position.add(index);
			}

			return new Position[] { position };
		}

		// One letter string was testing with indexOf(String)
		if (currentLength > 1) {
			// Truncate to the left and start to test it recursively
			String leftSubString = string2.substring(1, currentLength);
			Position[] leftPos = weightContainsPartImp(string1, leftSubString);

			// Truncate to the right and start to test it recursively
			String rightSubString = string2.substring(0, currentLength - 1);
			Position[] rightPos = weightContainsPartImp(string1, rightSubString);

			return Position.merge(leftPos, rightPos);
		}

		return new Position[0];
	}
	
	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @return
	 */
	public static boolean contains(String string1, String string2, char ignoreCharacter) {
		return contains(string1, string2, new char[] { ignoreCharacter });
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @return
	 */
	public static boolean contains(String string1, String string2, char[] ignoreCharacters) {
		if ((string1 == null) || (string2 == null))
			return false;

		string1 = removeIgnoreCharacters(string1, ignoreCharacters);
		string2 = removeIgnoreCharacters(string2, ignoreCharacters);

		return string1.indexOf(string2) > -1;
	}

	/**
	 * Checks to see if the given collection contains the specified string. The
	 * check is not case-sensitive and <code>toString()</code> is used to retrieve
	 * a string representation for each object. The collection can contain
	 * <code>null</code> values.
	 * 
	 * @param iter The collection of objects to be tested with the given string
	 * @param string The string to be compared (case ignored) with all the objects
	 * contained in the given collection
	 * @return <code>true</code> if the collection contains the given string,
	 * case ignored; <code>false</code> otherwise
	 */
	public static boolean containsIgnoreCase(Collection collection, String string) {
		return containsIgnoreCase(collection.iterator(), string);
	}

	/**
	 * Checks to see if the given collection contains the specified string. The
	 * check is not case-sensitive and <code>toString()</code> is used to retrieve
	 * a string representation for each object. The collection can contain
	 * <code>null</code> values.
	 * 
	 * @param iter The <code>Iterator</code> over the collection of objects to be
	 * tested with the given string
	 * @param string The string to be compared (case ignored) with all the objects
	 * contained in the collection iterated with the given <code>Iterator</code>
	 * @return <code>true</code> if the collection iterated with the given
	 * <code>Iterator</code> contains the given string, case ignored;
	 * <code>false</code> otherwise
	 */
	public static boolean containsIgnoreCase(Iterator iter, String string) {
		if (string == null)
			return false;

		while (iter.hasNext()) {
			Object item = iter.next();

			if ((item != null) && string.equalsIgnoreCase(item.toString()))
				return true;
		}

		return false;
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @return
	 */
	public static boolean containsPart(String string1, String string2) {
		return containsPart(string1, string2, new char[0], MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @return
	 */
	public static boolean containsPart(String string1, String string2, char ignoreCharacter) {
		return containsPart(string1, string2, new char[] { ignoreCharacter }, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static boolean containsPart(String string1, String string2, char ignoreCharacter, int minimumMatchingLetterCount) {
		return containsPart(string1, string2, new char[] { ignoreCharacter }, minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @return
	 */
	public static boolean containsPart(String string1, String string2, char[] ignoreCharacters) {
		return containsPart(string1, string2, ignoreCharacters, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static boolean containsPart(String string1, String string2, char[] ignoreCharacters, int minimumMatchingLetterCount) {
		return weightContainsPart(string1, string2, ignoreCharacters, minimumMatchingLetterCount) >= minimumMatchingLetterCount;
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static boolean containsPart(String string1, String string2, int minimumMatchingLetterCount) {
		return containsPart(string1, string2, new char[0], minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @return
	 */
	public static boolean containsPartContinuous(String string1, String string2) {
		return containsPartContinuous(string1, string2, new char[0], MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @return
	 */
	public static boolean containsPartContinuous(String string1, String string2, char ignoreCharacter) {
		return containsPartContinuous(string1, string2, new char[] { ignoreCharacter }, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacter
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static boolean containsPartContinuous(String string1, String string2, char ignoreCharacter, int minimumMatchingLetterCount) {
		return containsPartContinuous(string1, string2, new char[0], minimumMatchingLetterCount);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @return
	 */
	public static boolean containsPartContinuous(String string1, String string2, char[] ignoreCharacters) {
		return containsPartContinuous(string1, string2, ignoreCharacters, MINIMUM_MATCHING_LETTER);
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param ignoreCharacters
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static boolean containsPartContinuous(String string1, String string2, char[] ignoreCharacters, int minimumMatchingLetterCount) {
		return weightContainsPartContinuous(string1, string2, ignoreCharacters, minimumMatchingLetterCount) >= minimumMatchingLetterCount;
	}

	/**
	 * Calculates
	 *
	 * @param string1
	 * @param string2
	 * @param minimumMatchingLetterCount
	 * @return
	 */
	public static boolean containsPartContinuous(String string1, String string2, int minimumMatchingLetterCount) {
		return containsPartContinuous(string1, string2, new char[0], minimumMatchingLetterCount);
	}

	/**
	 * Removes
	 *
	 * @param string
	 * @param ignoreCharacters
	 * @return
	 */
	public static String removeIgnoreCharacters(String string, char[] ignoreCharacters) {
		if ((string == null) || (string.length() == 0) || (ignoreCharacters.length == 0)) {
			return string;
		}

		for (int index = 0; index < ignoreCharacters.length; index++) {
			String character = String.valueOf(ignoreCharacters[index]);
			string = string.replaceAll(character, "");
		}
		return string;
	}
	/**
	 * This <code>Position</code> class is used by
	 * {@link StringUtility#weightContainsPartImp(String, String)}.
	 */
	private static class Position {
		int indices[];
		int length;

		Position() {
			super();
			indices = new int[1];
			indices[0] = -1;
		}

		Position(int index, int length) {
			this();
			indices[0] = index;
			this.length = length;
		}

		private static List asList(Position[] array) {
			ArrayList list = new ArrayList(array.length);

			for (int index = 0; index < array.length; index++)
				list.add(array[index]);

			return list;
		}

		private static boolean canAddWeight(Position[] positions, int index) {
			// The last one can always be added
			if (index + 1 == positions.length)
				return true;

			Position position1 = positions[index];
			Position position2 = positions[index + 1];

			for (int index1 = 0; index1 < position1.indices.length; index1++) {
				for (int index2 = 0; index2 < position2.indices.length; index2++) {
					int posIndex1 = position1.indices[index1];
					int posIndex2 = position2.indices[index2];

					if (posIndex1 > posIndex2)
						return true;
				}
			}

			return false;
		}

		static Position[] merge(Position[] positions1, Position[] positions2) {
			List positions1List = asList(positions1);
			List positions2List = asList(positions2);

			Iterator iter1 = positions1List.iterator();

			while (iter1.hasNext()) {
				Position firstPosition = (Position) iter1.next();

				Iterator iter2 = positions2List.iterator();

				while (iter2.hasNext()) {
					Position secondPosition = (Position) iter2.next();

					if (firstPosition.inside(secondPosition))
						iter2.remove();
					else if (secondPosition.inside(firstPosition))
						iter1.remove();
				}
			}

			// Return the result of the merge, which is the remaining of both arrays
			Position[] positions = new Position[positions1List.size() + positions2List.size()];

			System.arraycopy(positions1List.toArray(), 0, positions, 0,                     positions1List.size());
			System.arraycopy(positions2List.toArray(), 0, positions, positions1List.size(), positions2List.size());

			return positions;
		}

		void add(int index) {
			ensureCapacity();
			indices[indices.length - 1] = index;
		}

		private void ensureCapacity() {
			int[] oldIndices = indices;
			indices = new int[oldIndices.length + 1];
			System.arraycopy(oldIndices, 0, indices, 0, oldIndices.length);
		}

		boolean inside(Position position) {
			for (int index1 = 0; index1 < indices.length; index1++) {
				for (int index2 = 0; index2 < position.indices.length; index2++) {
					if (indices[index1] + length >= position.indices[index2] + position.length && indices[index1] + length >= position.length && indices[index1] <= position.indices[index2]) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
