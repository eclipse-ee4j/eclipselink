/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools.utility;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.tools.utility.XmlEscapeCharacterConverter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The unit-tests for {@link XmlEscapeCharacterConverter}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class XmlEscapeCharacterConverterTest {

    @Test
    public void test_escape_01() {

        String value = null;
        String result = XmlEscapeCharacterConverter.escape(value, null);
        assertEquals(value, result);
    }

    @Test
    public void test_escape_02() {

        String value = ExpressionTools.EMPTY_STRING;
        String result = XmlEscapeCharacterConverter.escape(value, null);
        assertEquals(value, result);
    }

    @Test
    public void test_escape_03() {

        String value = "SELECT e FROM Employee e";
        int[] position = { value.length() };

        String result = XmlEscapeCharacterConverter.escape(value, position);

        assertEquals(value, result);
        assertEquals(value.length(), position[0]);
    }

    @Test
    public void test_escape_04() {

        String value = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int position = "SELECT e FROM Employee e".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.escape(value, positions);
        String expected = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";

        assertEquals(expected, result);
        assertEquals(position, positions[0]);
    }

    @Test
    public void test_escape_05() {

        String value = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int[] positions = { value.length() };

        String result = XmlEscapeCharacterConverter.escape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int expectedPosition = expectedValue.length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_escape_06() {

        String value = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int position = "SELECT e FROM Employee e WHERE e.name ".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.escape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int expectedPosition = position;

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_escape_07() {

        String value = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int position = "SELECT e FROM Employee e WHERE e.name <".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.escape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int expectedPosition = "SELECT e FROM Employee e WHERE e.name &lt;".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_escape_08() {

        String value = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int position = "SELECT e FROM Employee e WHERE e.name <>".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.escape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int expectedPosition = "SELECT e FROM Employee e WHERE e.name &lt;&gt;".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_getCharacter() {

        assertNull(XmlEscapeCharacterConverter.getCharacter(null));
        assertNull(XmlEscapeCharacterConverter.getCharacter(ExpressionTools.EMPTY_STRING));
        assertNull(XmlEscapeCharacterConverter.getCharacter("#"));
        assertNull(XmlEscapeCharacterConverter.getCharacter("#-"));
        assertNull(XmlEscapeCharacterConverter.getCharacter("#-20"));
        assertNull(XmlEscapeCharacterConverter.getCharacter("#x"));
        assertNull(XmlEscapeCharacterConverter.getCharacter("bonjour"));

        assertEquals("<",  XmlEscapeCharacterConverter.getCharacter("lt"));
        assertEquals(">",  XmlEscapeCharacterConverter.getCharacter("gt"));
        assertEquals("&",  XmlEscapeCharacterConverter.getCharacter("amp"));
        assertEquals("'",  XmlEscapeCharacterConverter.getCharacter("apos"));
        assertEquals("\"", XmlEscapeCharacterConverter.getCharacter("quot"));

        // @ - Commercial At
        assertEquals("@", XmlEscapeCharacterConverter.getCharacter("#64"));
        assertEquals("@", XmlEscapeCharacterConverter.getCharacter("#x40"));

        // For all
        assertEquals("\u2200", XmlEscapeCharacterConverter.getCharacter("forall"));
        assertEquals("\u2200", XmlEscapeCharacterConverter.getCharacter("#8704"));
        assertEquals("\u2200", XmlEscapeCharacterConverter.getCharacter("#x2200"));

        // Greek Capital Letter Alpha
        assertEquals("\u0391", XmlEscapeCharacterConverter.getCharacter("Alpha"));
        assertEquals("\u0391", XmlEscapeCharacterConverter.getCharacter("#913"));
        assertEquals("\u0391", XmlEscapeCharacterConverter.getCharacter("#x391"));
        assertEquals("\u0391", XmlEscapeCharacterConverter.getCharacter("#x0391"));

        // Greek Small Letter Alpha
        assertEquals("\u03B1", XmlEscapeCharacterConverter.getCharacter("alpha"));
        assertEquals("\u03B1", XmlEscapeCharacterConverter.getCharacter("#945"));
        assertEquals("\u03B1", XmlEscapeCharacterConverter.getCharacter("#x3B1"));
        assertEquals("\u03B1", XmlEscapeCharacterConverter.getCharacter("#x03B1"));

        // ô - Latin Small Letter O With Circumflex
        assertEquals("\u00F4", XmlEscapeCharacterConverter.getCharacter("ocirc"));
        assertEquals("\u00F4", XmlEscapeCharacterConverter.getCharacter("#244"));
        assertEquals("\u00F4", XmlEscapeCharacterConverter.getCharacter("#xF4"));
        assertEquals("\u00F4", XmlEscapeCharacterConverter.getCharacter("#x00F4"));

        // Black Spade Suit
        assertEquals("\u2660", XmlEscapeCharacterConverter.getCharacter("spades"));
        assertEquals("\u2660", XmlEscapeCharacterConverter.getCharacter("#9824"));
        assertEquals("\u2660", XmlEscapeCharacterConverter.getCharacter("#x2660"));
    }

    @Test
    public void test_getEscapeCharacter() {

        assertEquals(XmlEscapeCharacterConverter.AMPERSAND_ENTITY_NAME,    XmlEscapeCharacterConverter.getEscapeCharacter('&'));
        assertEquals(XmlEscapeCharacterConverter.LESS_THAN_ENTITY_NAME,    XmlEscapeCharacterConverter.getEscapeCharacter('<'));
        assertEquals(XmlEscapeCharacterConverter.GREATER_THAN_ENTITY_NAME, XmlEscapeCharacterConverter.getEscapeCharacter('>'));
        assertEquals(XmlEscapeCharacterConverter.APOSTROPHE_ENTITY_NAME,   XmlEscapeCharacterConverter.getEscapeCharacter('\''));
        assertEquals(XmlEscapeCharacterConverter.QUOTATION_MARK_NAME,      XmlEscapeCharacterConverter.getEscapeCharacter('\"'));

        assertNull(XmlEscapeCharacterConverter.getEscapeCharacter('\0'));
        assertNull(XmlEscapeCharacterConverter.getEscapeCharacter('a'));
        assertNull(XmlEscapeCharacterConverter.getEscapeCharacter('\u00F7'));
    }

    @Test
    public void test_isReserved() {

        assertFalse(XmlEscapeCharacterConverter.isReserved('\0'));
        assertFalse(XmlEscapeCharacterConverter.isReserved('a'));
        assertFalse(XmlEscapeCharacterConverter.isReserved('\u00F7'));

        assertTrue (XmlEscapeCharacterConverter.isReserved('&'));
        assertTrue (XmlEscapeCharacterConverter.isReserved('<'));
        assertTrue (XmlEscapeCharacterConverter.isReserved('>'));
        assertTrue (XmlEscapeCharacterConverter.isReserved('\''));
        assertTrue (XmlEscapeCharacterConverter.isReserved('\"'));
    }

    @Test
    public void test_reposition_01() {

        String jpqlQuery = "SELECT e FROM Employee e";

        int[] positions = { 0, 0 };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        assertEquals(0, positions[0]);
        assertEquals(0, positions[1]);
    }

    @Test
    public void test_reposition_02() {

        String jpqlQuery = "SELECT e FROM Employee e";
        int position = jpqlQuery.length();

        int[] positions = { position, position };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        assertEquals(position, positions[0]);
        assertEquals(position, positions[1]);
    }

    @Test
    public void test_reposition_03() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.".length();

        int[] positions = { position, position };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        assertEquals(position, positions[0]);
        assertEquals(position, positions[1]);
    }

    @Test
    public void test_reposition_04() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name ".length();

        int[] positions = { position, position };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        assertEquals(position, positions[0]);
        assertEquals(position, positions[1]);
    }

    @Test
    public void test_reposition_05() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name <".length();

        int[] positions = { position, position };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        int expectedPosition = "SELECT e FROM Employee e WHERE e.name &lt;".length();
        assertEquals(expectedPosition, positions[0]);
        assertEquals(expectedPosition, positions[1]);
    }

    @Test
    public void test_reposition_06() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name <>".length();

        int[] positions = { position, position };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        int expectedPosition = "SELECT e FROM Employee e WHERE e.name &lt;&gt;".length();
        assertEquals(expectedPosition, positions[0]);
        assertEquals(expectedPosition, positions[1]);
    }

    @Test
    public void test_reposition_07() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name <> 'JPQL".length();

        int[] positions = { position, position };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        int expectedPosition = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL".length();
        assertEquals(expectedPosition, positions[0]);
        assertEquals(expectedPosition, positions[1]);
    }

    @Test
    public void test_reposition_08() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position1 = "SELECT e FROM Employee e WHERE e.name <>".length();
        int position2 = "SELECT e FROM Employee e WHERE e.name <> 'JPQL".length();

        int[] positions = { position1, position2 };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        int expectedPosition1 = "SELECT e FROM Employee e WHERE e.name &lt;&gt;".length();
        int expectedPosition2 = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL".length();
        assertEquals(expectedPosition1, positions[0]);
        assertEquals(expectedPosition2, positions[1]);
    }

    @Test
    public void test_reposition_09() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position1 = "SELECT e FROM Employee e WHERE e.name ".length();
        int position2 = "SELECT e FROM Employee e WHERE e.name <".length();

        int[] positions = { position1, position2 };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        int expectedPosition1 = "SELECT e FROM Employee e WHERE e.name ".length();
        int expectedPosition2 = "SELECT e FROM Employee e WHERE e.name &lt;".length();
        assertEquals(expectedPosition1, positions[0]);
        assertEquals(expectedPosition2, positions[1]);
    }

    @Test
    public void test_reposition_10() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position1 = "SELECT e FROM Employee e WHERE e.name ".length();
        int position2 = "SELECT e FROM Employee e WHERE e.name <>".length();

        int[] positions = { position1, position2 };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        int expectedPosition1 = "SELECT e FROM Employee e WHERE e.name ".length();
        int expectedPosition2 = "SELECT e FROM Employee e WHERE e.name &lt;&gt;".length();
        assertEquals(expectedPosition1, positions[0]);
        assertEquals(expectedPosition2, positions[1]);
    }

    @Test
    public void test_reposition_11() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position1 = 0;
        int position2 = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'".length();

        int[] positions = { position1, position2 };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        int expectedPosition1 = 0;
        int expectedPosition2 = jpqlQuery.length();
        assertEquals(expectedPosition1, positions[0]);
        assertEquals(expectedPosition2, positions[1]);
    }

    @Test
    public void test_reposition_12() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position1 = "SELECT e FROM Employee e WHERE ".length();
        int position2 = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'".length();

        int[] positions = { position1, position2 };
        XmlEscapeCharacterConverter.reposition(jpqlQuery, positions);

        int expectedPosition1 = "SELECT e FROM Employee e WHERE ".length();
        int expectedPosition2 = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;".length();
        assertEquals(expectedPosition1, positions[0]);
        assertEquals(expectedPosition2, positions[1]);
    }

    @Test
    public void test_unescape_01() {

        String value = null;
        String result = XmlEscapeCharacterConverter.unescape(value, null);
        assertEquals(value, result);
    }

    @Test
    public void test_unescape_02() {

        String value = ExpressionTools.EMPTY_STRING;
        String result = XmlEscapeCharacterConverter.unescape(value, null);
        assertEquals(value, result);
    }

    @Test
    public void test_unescape_03() {

        String value = "SELECT e FROM Employee e";
        int[] position = { value.length() };

        String result = XmlEscapeCharacterConverter.unescape(value, position);

        assertEquals(value, result);
        assertEquals(value.length(), position[0]);
    }

    @Test
    public void test_unescape_04() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expected = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";

        assertEquals(expected, result);
        assertEquals(position, positions[0]);
    }

    @Test
    public void test_unescape_05() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int[] positions = { value.length() };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = expectedValue.length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_06() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name ".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = position;

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_07() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name &".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = "SELECT e FROM Employee e WHERE e.name ".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_08() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name &l".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = "SELECT e FROM Employee e WHERE e.name ".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_09() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name &lt".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = "SELECT e FROM Employee e WHERE e.name ".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_10() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name &lt;".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = "SELECT e FROM Employee e WHERE e.name <".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_11() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name &lt;&".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = "SELECT e FROM Employee e WHERE e.name <".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_12() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name &lt;&g".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = "SELECT e FROM Employee e WHERE e.name <".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_13() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name &lt;&gt".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = "SELECT e FROM Employee e WHERE e.name <".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_14() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT e FROM Employee e WHERE e.name &lt;&gt;".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = "SELECT e FROM Employee e WHERE e.name <>".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_15() {

        String value = "SELECT e FROM Employee e WHERE e.name &lt;&gt; &apos;JPQL&apos;";
        int position = value.length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = expectedValue.length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }

    @Test
    public void test_unescape_16() {

        String value = "SELECT &#101; FROM Employee &#101; WHERE &#101;.name &lt;&gt; &apos;JPQL&apos;";
        int position = "SELECT &#101; FROM Employee &#101;".length();
        int[] positions = { position };

        String result = XmlEscapeCharacterConverter.unescape(value, positions);
        String expectedValue = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        int expectedPosition = "SELECT e FROM Employee e".length();

        assertEquals(expectedValue,    result);
        assertEquals(expectedPosition, positions[0]);
    }
}
