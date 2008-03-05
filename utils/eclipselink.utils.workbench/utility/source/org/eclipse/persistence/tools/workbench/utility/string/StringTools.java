/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import java.util.Arrays;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public final class StringTools {

	/** carriage return */
	public static final String CR = System.getProperty("line.separator");


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

}
