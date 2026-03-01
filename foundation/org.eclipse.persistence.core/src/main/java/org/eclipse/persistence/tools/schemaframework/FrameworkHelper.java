/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.tools.schemaframework;

final class FrameworkHelper {

    private FrameworkHelper() {}

    /**
     * Return constraint name built from the table and field name with the specified maximum length. To
     * make the name short enough we
     * 1. Drop the prefix, such as "FK_", "IX_" or "UNQ_".
     * 2. Drop the underscore characters if any.
     * 3. Drop the vowels from the table and field name.
     * 4. Truncate the table name to zero length if necessary.
     */
    static String buildConstraintName(String tableName, String fieldName, String namePrefix,
                                      String startDelimiter, String endDelimiter, int maximumNameLength) {
        String startDelim = "";
        String endDelim = "";
        boolean useDelimiters = !startDelimiter.isEmpty() && (tableName.startsWith(startDelimiter) || fieldName.startsWith(startDelimiter));
        // we will only delimit our generated constraint name if either of the names that composes them is already delimited
        if (useDelimiters){
            startDelim = startDelimiter;
            endDelim = endDelimiter;
        }
        String adjustedTableName = tableName;
        if(adjustedTableName.indexOf(' ') != -1 || adjustedTableName.indexOf('\"') != -1 || adjustedTableName.indexOf('`') != -1) {
            //if table name has spaces and/or is quoted, remove this from the constraint name.
            StringBuilder buff = new StringBuilder();
            for(int i = 0; i < tableName.length(); i++) {
                char c = tableName.charAt(i);
                if(c != ' ' && c != '\"' && c != '`') {
                    buff.append(c);
                }
            }
            adjustedTableName = buff.toString();
        }
        StringBuilder buff = new StringBuilder();
        for(int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if(c != ' ' && c != '\"' && c != '`') {
                buff.append(c);
            }
        }
        String adjustedFieldName = buff.toString();
        String prefix = namePrefix == null ? "" : namePrefix;
        String constraintName = startDelim + prefix + adjustedTableName + "_" + adjustedFieldName + endDelim;
        if (constraintName.length() > maximumNameLength) {
            // First Remove the prefix.
            constraintName = startDelim + adjustedTableName + "_" + adjustedFieldName + endDelim;
            if (constraintName.length() > maximumNameLength) {
                // Still too long: remove the underscore characters
                constraintName = startDelim + removeAllButAlphaNumericToFit(adjustedTableName + adjustedFieldName, maximumNameLength) + endDelim;
                if (constraintName.length() > maximumNameLength) {
                    // Still too long: remove vowels from the table name and field name.
                    String onlyAlphaNumericTableName = removeAllButAlphaNumericToFit(adjustedTableName, 0);
                    String onlyAlphaNumericFieldName = removeAllButAlphaNumericToFit(adjustedFieldName, 0);
                    constraintName = startDelim + shortenStringsByRemovingVowelsToFit(onlyAlphaNumericTableName, onlyAlphaNumericFieldName, maximumNameLength) + endDelim;
                    if (constraintName.length() > maximumNameLength) {
                        // Still too long: remove vowels from the table name and field name and truncate the table name.
                        String shortenedFieldName = removeVowels(onlyAlphaNumericFieldName);
                        String shortenedTableName = removeVowels(onlyAlphaNumericTableName);
                        int delimiterLength = startDelim.length() + endDelim.length();
                        if (shortenedFieldName.length() + delimiterLength >= maximumNameLength) {
                            constraintName = startDelim + truncate(shortenedFieldName, maximumNameLength - delimiterLength) + endDelim;
                        } else {
                            constraintName = startDelim + truncate(shortenedTableName, maximumNameLength - shortenedFieldName.length() - delimiterLength) + shortenedFieldName + endDelim;
                        }
                    }
                }
            }
        }
        return constraintName;
    }

    /**
     * Returns true if the character given is a vowel. I.e. one of a,e,i,o,u,A,E,I,O,U.
     */
    static boolean isVowel(char c) {
        return (c == 'A') || (c == 'a') || (c == 'e') || (c == 'E') || (c == 'i') || (c == 'I') || (c == 'o') || (c == 'O') || (c == 'u') || (c == 'U');
    }

    /**
     * Returns a String which has had enough non-alphanumeric characters removed to be equal to
     * the maximumStringLength.
     */
    static String removeAllButAlphaNumericToFit(String s1, int maximumStringLength) {
        int s1Size = s1.length();
        if (s1Size <= maximumStringLength) {
            return s1;
        }

        // Remove the necessary number of characters
        StringBuilder buf = new StringBuilder();
        int numberOfCharsToBeRemoved = s1.length() - maximumStringLength;
        int s1Index = 0;
        while ((numberOfCharsToBeRemoved > 0) && (s1Index < s1Size)) {
            char currentChar = s1.charAt(s1Index);
            if (Character.isLetterOrDigit(currentChar)) {
                buf.append(currentChar);
            } else {
                numberOfCharsToBeRemoved--;
            }
            s1Index++;
        }

        // Append the rest of the character that were not parsed through.
        // Is it quicker to build a substring and append that?
        while (s1Index < s1Size) {
            buf.append(s1.charAt(s1Index));
            s1Index++;
        }

        //
        return buf.toString();
    }

    /**
     * Returns a String which is a concatenation of two string which have had enough
     * vowels removed from them so that the sum of the sized of the two strings is less than
     * or equal to the specified size.
     */
    static String shortenStringsByRemovingVowelsToFit(String s1, String s2, int maximumStringLength) {
        int size = s1.length() + s2.length();
        if (size <= maximumStringLength) {
            return s1 + s2;
        }

        // Remove the necessary number of characters
        int s1Size = s1.length();
        int s2Size = s2.length();
        StringBuilder buf1 = new StringBuilder();
        StringBuilder buf2 = new StringBuilder();
        int numberOfCharsToBeRemoved = size - maximumStringLength;
        int s1Index = 0;
        int s2Index = 0;
        int modulo2 = 0;

        // While we still want to remove characters, and not both string are done.
        while ((numberOfCharsToBeRemoved > 0) && !((s1Index >= s1Size) && (s2Index >= s2Size))) {
            if ((modulo2 % 2) == 0) {
                // Remove from s1
                if (s1Index < s1Size) {
                    if (isVowel(s1.charAt(s1Index))) {
                        numberOfCharsToBeRemoved--;
                    } else {
                        buf1.append(s1.charAt(s1Index));
                    }
                    s1Index++;
                }
            } else {
                // Remove from s2
                if (s2Index < s2Size) {
                    if (isVowel(s2.charAt(s2Index))) {
                        numberOfCharsToBeRemoved--;
                    } else {
                        buf2.append(s2.charAt(s2Index));
                    }
                    s2Index++;
                }
            }
            modulo2++;
        }

        // Append the rest of the character that were not parsed through.
        // Is it quicker to build a substring and append that?
        while (s1Index < s1Size) {
            buf1.append(s1.charAt(s1Index));
            s1Index++;
        }
        while (s2Index < s2Size) {
            buf2.append(s2.charAt(s2Index));
            s2Index++;
        }

        //
        return buf1.toString() + buf2;
    }

    /**
     * If the size of the original string is larger than the passed in size,
     * this method will remove the vowels from the original string.
     * <p>
     * The removal starts backward from the end of original string, and stops if the
     * resulting string size is equal to the passed in size.
     * <p>
     * If the resulting string is still larger than the passed in size after
     * removing all vowels, the end of the resulting string will be truncated.
     */
    static String truncate(String originalString, int size) {
        if (originalString.length() <= size) {
            //no removal and truncation needed
            return originalString;
        }
        String vowels = "AaEeIiOoUu";
        StringBuilder newStringBufferTmp = new StringBuilder(originalString.length());

        //need to remove the extra characters
        int counter = originalString.length() - size;
        for (int index = (originalString.length() - 1); index >= 0; index--) {
            //search from the back to the front, if vowel found, do not append it to the resulting (temp) string!
            //i.e. if vowel not found, append the chararcter to the new string buffer.
            if (vowels.indexOf(originalString.charAt(index)) == -1) {
                newStringBufferTmp.append(originalString.charAt(index));
            } else {
                //vowel found! do NOT append it to the temp buffer, and decrease the counter
                counter--;
                if (counter == 0) {
                    //if the exceeded characters (counter) of vowel haven been removed, the total
                    //string size should be equal to the limits, so append the reversed remaining string
                    //to the new string, break the loop and return the shrunk string.
                    StringBuilder newStringBuffer = new StringBuilder(size);
                    newStringBuffer.append(originalString.substring(0, index));
                    //need to reverse the string
                    //bug fix: 3016423. append(BunfferString) is jdk1.4 version api. Use append(String) instead
                    //in order to support jdk1.3.
                    newStringBuffer.append(newStringBufferTmp.reverse());
                    return newStringBuffer.toString();
                }
            }
        }

        //the shrunk string still too long, revrese the order back and truncate it!
        return newStringBufferTmp.reverse().substring(0, size);
    }

    /**
     * Returns a String which has had enough of the specified character removed to be equal to
     * the maximumStringLength.
     */
    static String removeVowels(String s1) {
        // Remove the vowels
        StringBuilder buf = new StringBuilder();
        int s1Size = s1.length();
        int s1Index = 0;
        while (s1Index < s1Size) {
            char currentChar = s1.charAt(s1Index);
            if (!isVowel(currentChar)) {
                buf.append(currentChar);
            }
            s1Index++;
        }

        //
        return buf.toString();
    }
}
