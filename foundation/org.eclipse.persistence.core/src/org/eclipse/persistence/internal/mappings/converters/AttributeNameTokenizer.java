/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     11/05/2014-2.6 Tomas Kraus
//       - 449818: Initial API and implementation.
package org.eclipse.persistence.internal.mappings.converters;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * INTERNAL:
 * Attribute name tokenizer.
 * @author Tomas Kraus
 */
public class AttributeNameTokenizer implements Iterable<String> {

    /** INTERNAL: Attribute name dot notation separator. */
    public static final char SEPARATOR = '.';

    /** Regular expression capturing group name for attribute name prefix. */
    private static final String PREFIX = "prefix";

    /** Regular expression matching {@code "value."} substring. */
    private final static Pattern VALUE_DOT_PATTERN = Pattern.compile(AttributeNamePrefix.VALUE.getName() + "\\"
            + AttributeNameTokenizer.SEPARATOR);

    /** Regular expression matching {@code "key."} substring. */
    private final static Pattern KEY_DOT_PATTERN = Pattern.compile(AttributeNamePrefix.KEY.getName() + "\\"
            + AttributeNameTokenizer.SEPARATOR);

    /**
     * INTERNAL:
     * Return an attribute name without {@code value.} dot notation prefix.
     * @param attributeName Attribute name containing {@code value.} prefix.
     * @return Attribute name without {@code value.} prefix or {@code null} when no prefix was found.
     */
    public static String getNameAfterVersion(final String attributeName) {
        final Matcher matcher = VALUE_DOT_PATTERN.matcher(attributeName);
        if (matcher.find()) {
            return attributeName.substring(matcher.end());
        }
        return null;
    }

    /**
     * INTERNAL:
     * Return an attribute name without {@code key.} dot notation prefix.
     * @param attributeName Attribute name containing {@code key.} prefix.
     * @return Attribute name without {@code key.} prefix or {@code null} when no prefix was found.
     */
    public static String getNameAfterKey(final String attributeName) {
        final Matcher matcher = KEY_DOT_PATTERN.matcher(attributeName);
        if (matcher.find()) {
            return attributeName.substring(matcher.end());
        }
        return null;
    }

    /** Attribute name to be parsed. */
    private final String attributeName;

    /**
     * INTERNAL:
     * Creates an instance of attribute name tokenizer.
     */
    public AttributeNameTokenizer(final String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * INTERNAL:
     * Returns an {@link Iterator<String>} over attribute name tokens.
     * @return An {@link Iterator<String>} over attribute name tokens.
     */
    @Override
    public Iterator<String> iterator() {
        return new TokensIterator(attributeName);
    }

    // This duplicates Iterator<String> interface but may be useful to avoid
    // class casting.
    /**
     * INTERNAL:
     * Returns an {@link TokensIterator} over attribute name tokens.
     * @return An {@link TokensIterator} over attribute name tokens.
     */
    public TokensIterator tokensIterator() {
        return new TokensIterator(attributeName);
    }

    /**
     * INTERNAL:
     * Attribute name tokenizer parser implemented as an {@link Iterator<String>} over individual attribute name tokens.
     *
     */
    public static final class TokensIterator
            implements Iterator<String> {

        /** Regular expression used to parse attribute name and extract prefix and tokens from it. There are two basic
         *  capturing groups:<ul>
         *  <li><i>prefix</i> to extract attribute name prefix ({@code "key"} or {@code "value"})</li>
         *  <li><i>name token</i> to extract individual attribute name tokens</li></ul>
         */
        private final static Pattern PREFIX_PATTERN = Pattern.compile("(?:(?<" + PREFIX + ">"
                + AttributeNamePrefix.KEY.getName() + "|" + AttributeNamePrefix.VALUE.getName()+")\\.){0,1}"
                + "([^\\.]+)");

        /** Regular expression used to parse attribute name and extract tokens from it. There is just one capturing
         *  group:<ul>
         *  <li><i>name token</i> to extract individual attribute name tokens</li></ul>
         */
        private final static Pattern SIMPLE_PATTERN = Pattern.compile("([^\\.]+)");

        /** Regular expression used to parse attribute name and extract tokens from it. There is just one capturing
         *  group:<ul>
         *  <li><i>name token</i> to extract individual attribute name tokens</li></ul>
         */
        private final static Pattern NEXT_PATTERN = Pattern.compile("\\.([^\\.]+)");

        /** Regular expression matching engine. */
        private final Matcher matcher;

        /** Prefix found in attribute name. */
        private final AttributeNamePrefix prefix;

        /** Next token to be returned. */
        private String token;

        /**
         * INTERNAL:
         * Creates an instance of attribute name tokenizer iterator. Simple parser without prefix parsing will be used
         * so ({@code "key."} and {@code "value."}) will be returned as regular attribute name tokens.
         * @param attributeName Attribute name to be parsed.
         */
        public TokensIterator(final String attributeName) {
            this(attributeName, false);
        }

        /**
         * INTERNAL:
         * Creates an instance of attribute name tokenizer iterator.
         * @param attributeName Attribute name to be parsed.
         * @param isPrefix Do search for attribute name prefixes ({@code "key."} and {@code "value."})?
         */
        public TokensIterator(final String attributeName, boolean isPrefix) {
            // Use regular expression without prefix. Prefix will be null.
            if (!isPrefix) {
                matcher = SIMPLE_PATTERN.matcher(attributeName);
                // Provided attribute name is matching regular expression.
                if (matcher.lookingAt()) {
                    token = matcher.groupCount() > 0 ? matcher.group(1) : null;
                } else {
                    token = null;
                }
                prefix = null;
            // Use regular expression with prefix. Prefix will be known.
            } else {
                matcher = PREFIX_PATTERN.matcher(attributeName);
                if (matcher.lookingAt()) {
                    final String prefixString = matcher.group(PREFIX);
                    prefix = AttributeNamePrefix.toValue(prefixString != null ? prefixString : "");
                    token = matcher.groupCount() > 1 ? matcher.group(2) : null;
                } else {
                    prefix = AttributeNamePrefix.NULL;
                    token = null;
                }
            }
            matcher.usePattern(NEXT_PATTERN);
            matcher.region(matcher.end(), matcher.regionEnd());
        }

        /**
         * INTERNAL:
         * Get attribute name prefix.
         * @return Attribute name prefix.
         */
        public AttributeNamePrefix getPrefix() {
            return prefix;
        }

        /**
         * INTERNAL:
         * Returns {@code true} if the iteration has more elements. In other words, returns {@code true}
         * if {@see #next()} would return an element rather than throwing an exception.
         * @return Value of {@code true} if the iteration has more elements or {@code false} otherwise.
         */
        @Override
        public boolean hasNext() {
            return token != null;
        }

        /**
         * INTERNAL:
         * Return the next attribute name token from attribute name.
         * @return The next attribute name token.
         * @throws NoSuchElementException when attribute name has no more tokens.
         */
        @Override
        public String next() {
            final String tokenToReturn = token;
            if (matcher.lookingAt()) {
                token =  matcher.groupCount() > 0 ? matcher.group(1) : null;
                matcher.region(matcher.end(), matcher.regionEnd());
            } else {
                token = null;
            }
            return tokenToReturn;
        }

        /**
         * INTERNAL:
         * Removal of attribute name tokens makes no sense.
         * @return Never returns any value.
         * @throws UnsupportedOperationException is always thrown on invocation.
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removal of attribute name tokens makes no sense");
        }
    }

}
