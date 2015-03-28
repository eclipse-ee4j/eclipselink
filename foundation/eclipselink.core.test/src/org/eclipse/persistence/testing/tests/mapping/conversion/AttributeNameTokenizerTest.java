/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/06/2014-2.6 Tomas Kraus
 *       - 449818: Initial API and implementation.
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.mapping.conversion;

import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.persistence.internal.mappings.converters.AttributeNamePrefix;
import org.eclipse.persistence.internal.mappings.converters.AttributeNameTokenizer;
import org.eclipse.persistence.internal.mappings.converters.AttributeNameTokenizer.TokensIterator;
import org.junit.Test;

/**
 * Test attribute name tokenizer. This test case is registered
 * in {@link org.eclipse.persistence.testing.tests.mapping.MappingTestModel} test model.
 * @author Tomas Kraus
 */
public class AttributeNameTokenizerTest extends TestCase {

    /** Sample attribute names for testing. */
    private static String[] ATTR_NAMES = {
        "value", "something", "something.somewhere","something.somewhere.under.some.structure"
    };

    /** Regular expression to simply split attribute names into tokens. */
    private static final String ATTR_NAME_SPLIT_REGEX = "\\" + AttributeNameTokenizer.SEPARATOR;

    /**
     * Build attribute names prefixed with specified prefix.
     * @param source Source array with attribute names to be prefixed.
     * @param prefix Prefix to add to source attribute names.
     * @return An array with prefixed attribute names from source array.
     */
    private static String[] buildAttributeNamesWithPrefix(final String[] source, final String prefix) {
        String[] target = new String[source.length];
        for (int i = 0; i < source.length; i++) {
            final String sourceName = source[i];
            final StringBuilder sb = new StringBuilder(sourceName.length() + prefix.length() + 1);
            sb.append(prefix);
            sb.append(AttributeNameTokenizer.SEPARATOR);
            sb.append(sourceName);
            target[i] = sb.toString();
        }
        return target;
    }

    /**
     * Test {@code AttributeNameTokenizer.getNameAfterKey()} functionality.
     */
    @Test
    public void testGetNameAfterKey() {
        final String[] prefixedNames = buildAttributeNamesWithPrefix(ATTR_NAMES, AttributeNamePrefix.KEY.getName());
        for (int i = 0; i < prefixedNames.length; i++) {
            final String prefixedName = prefixedNames[i];
            final String result = AttributeNameTokenizer.getNameAfterKey(prefixedName);
            assertTrue("Removal of " + AttributeNamePrefix.KEY.getName() + " prefix failed.",
                    result.equals(ATTR_NAMES[i]));
        }
    }

    /**
     * Test {@code AttributeNameTokenizer.getNameAfterVersion()} functionality.
     */
    @Test
    public void testGetNameAfterVersion() {
        final String[] prefixedNames = buildAttributeNamesWithPrefix(ATTR_NAMES, AttributeNamePrefix.VALUE.getName());
        for (int i = 0; i < prefixedNames.length; i++) {
            final String prefixedName = prefixedNames[i];
            final String result = AttributeNameTokenizer.getNameAfterVersion(prefixedName);
            assertTrue("Removal of " + AttributeNamePrefix.VALUE.getName() + " prefix failed.",
                    result.equals(ATTR_NAMES[i]));
        }
    }

    /**
     * Test {@code AttributeNameTokenizer.iterator()} functionality. Common iterators do not distinguish prefixes
     * so they should return full name to token decomposition including prefix substring.
     */
    @Test
    public void testIterator() {
        // #0 unprefixed, #1 prefixed with key, #2 prefixed with value
        final String[][] attrNames = new String[3][];
        attrNames[0] = ATTR_NAMES;
        attrNames[1] = buildAttributeNamesWithPrefix(ATTR_NAMES, AttributeNamePrefix.KEY.getName());
        attrNames[2] = buildAttributeNamesWithPrefix(ATTR_NAMES, AttributeNamePrefix.VALUE.getName());
        for (int i = 0; i < attrNames.length; i++) {
            for (int j = 0; j < attrNames[i].length; j++) {
                final String attrName = attrNames[i][j];
                final String[] tokens = attrName.split(ATTR_NAME_SPLIT_REGEX);
                int k = 0;
                // Old style for cycle with iterator.
                for (Iterator<String> it = new AttributeNameTokenizer(attrName).iterator(); it.hasNext(); ) {
                    String token = it.next();
                    assertTrue("Order of tokens did not match", token.equals(tokens[k++]));
                }
                assertEquals("Token count is wrong: " + tokens.length + "!=" + k , tokens.length, k);
                k = 0;
                // New style for cycle.
                for (String token : new AttributeNameTokenizer(attrName)) {
                    assertTrue("Order of tokens did not match", token.equals(tokens[k++]));
                }
                assertEquals("Token count is wrong: " + tokens.length + "!=" + k , tokens.length, k);
            }
        }
    }

    /**
     * Test {@code AttributeNameTokenizer.tokensIterator()} functionality. This iterator initialized
     * with {@code isPrefix = true} does distinguish prefixes so they should return tokens without prefix
     * and also return proper prefix type value.
     */
    @Test
    public void testTokensIterator() {
        // #0 unprefixed, #1 prefixed with key, #2 prefixed with value
        final String[][] attrNames = new String[3][];
        attrNames[0] = ATTR_NAMES;
        attrNames[1] = buildAttributeNamesWithPrefix(ATTR_NAMES, AttributeNamePrefix.KEY.getName());
        attrNames[2] = buildAttributeNamesWithPrefix(ATTR_NAMES, AttributeNamePrefix.VALUE.getName());
        for (int i = 0; i < attrNames.length; i++) {
            for (int j = 0; j < attrNames[i].length; j++) {
                final String attrName = attrNames[i][j];
                final String[] tokens = attrName.split(ATTR_NAME_SPLIT_REGEX);
                // #1 and #2 should have prefix removed.
                int k = i == 0 ? 0 : 1;
                TokensIterator it = new TokensIterator(attrName, true);
                while (it.hasNext()) {
                    String token = it.next();
                    assertTrue("Order of tokens did not match", token.equals(tokens[k++]));
                }
                assertEquals("Token count is wrong: " + tokens.length + "!=" + k , tokens.length, k);
                AttributeNamePrefix prefix = it.getPrefix();
                AttributeNamePrefix expectedPrefix = null;
                switch(i) {
                    case 0: expectedPrefix = AttributeNamePrefix.NULL; break;
                    case 1: expectedPrefix = AttributeNamePrefix.KEY; break;
                    case 2: expectedPrefix = AttributeNamePrefix.VALUE; break;
                    default: fail("Illegal attribute names array iondex.");
                }
                assertTrue("Returned prefix type is wrong", expectedPrefix.equals(prefix));
            }
        }
    }

}
