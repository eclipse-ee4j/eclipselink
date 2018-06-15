/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup
package org.eclipse.persistence.testing.tests.jpa.fetchgroups;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.core.queries.CoreAttributeConverter;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.queries.FetchGroup;
import org.junit.Test;
import static org.eclipse.persistence.internal.helper.StringHelper.DOT;

/**
 * Simple tests to verify the functionality of FetchGroup API
 *
 * @author dclarke
 * @since EclipseLink 2.1
 */
public class FetchGroupAPITests extends TestCase {

    public FetchGroupAPITests() {
        super();
    }

    public FetchGroupAPITests(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("FetchGroupAPITests");

        suite.addTest(new FetchGroupAPITests("verifyConverterWithValidSingleElements"));
        suite.addTest(new FetchGroupAPITests("verifyConverterWithValidComplexElements"));
        suite.addTest(new FetchGroupAPITests("verifyConverterWithValidArrays"));
        suite.addTest(new FetchGroupAPITests("verifyConverterWithInvalidSingleElements"));
        suite.addTest(new FetchGroupAPITests("verifyConverterWithInvalidComplexElements"));
        suite.addTest(new FetchGroupAPITests("verifyConverterWithInvalidArrays"));
        suite.addTest(new FetchGroupAPITests("verifyDefaultConstructor"));
        suite.addTest(new FetchGroupAPITests("verifyNameConstructor"));
        suite.addTest(new FetchGroupAPITests("verifyNameConstructor_Null"));
        suite.addTest(new FetchGroupAPITests("verifyInvalidAdd_null"));
        suite.addTest(new FetchGroupAPITests("verifyInvalidAdd_empty"));
        suite.addTest(new FetchGroupAPITests("verifyInvalidAdd_dot"));
        suite.addTest(new FetchGroupAPITests("verifyInvalidAdd_startWithDot"));
        suite.addTest(new FetchGroupAPITests("verifyInvalidAdd_endWithDot"));
        suite.addTest(new FetchGroupAPITests("verifyInvalidAdd_space"));
        suite.addTest(new FetchGroupAPITests("verifygetItem_EmptyFG"));
        suite.addTest(new FetchGroupAPITests("verifyAddAttribute"));
        suite.addTest(new FetchGroupAPITests("verifyAddAttribute_Nested"));
        suite.addTest(new FetchGroupAPITests("verifyAdd2AttributesNestedFG"));
        suite.addTest(new FetchGroupAPITests("verifyAdd2AttributesNestedFG_parentFirst"));
        suite.addTest(new FetchGroupAPITests("verifyAddAttribute_Nested2"));
        suite.addTest(new FetchGroupAPITests("verifyAdd"));
        suite.addTest(new FetchGroupAPITests("verifyAdd_Nested"));

        return suite;
    }

    /**
     * Verify attribute converter with set of simple path elements.
     * Every argument is a single {@link String} with valid path element without
     * separator.
     */
    @Test
    public void verifyConverterWithValidSingleElements() {
        final String[][] elements = {
                {"a"}, {"ab"}, {"abc"}, {"abcd"},
                {"a b"}, {"a b c"}, {"ab cd"}};
        for (String[] element : elements) {
            String[] result = CoreAttributeConverter.convert(element);
            assertNotNull("Converter returned null for input: \"" + element[0] + "\"", result);
            assertEquals("Size of returned array differs from input: \"" + element[0] + "\"",
                    element.length, result.length);
            assertTrue("Returned String does not match input: \"" + element[0] + "\"",
                    element[0].equals(result[0]));
        }
    }

    /**
     * Verify attribute converter with set of complex path elements.
     * Every argument is a single {@link String} with valid path with
     * one or more separators.
     */
    @Test
    public void verifyConverterWithValidComplexElements() {
        final String[][] paths = {
                {"a", "b"}, {"a", "b", "c"}, {"a", "b", "c", "d"},
                {"a b", "c d"}, {"a b", "c d", "e f"}};
        for (String[] path : paths) {
            int length = 0;
            int i;
            for (i = 0; i < path.length; i++) {
                length += path[i].length() + 1;
            }
            StringBuilder element = new StringBuilder(length);
            for (i = 0; i < path.length; i++) {
                if (i > 0)
                    element.append(DOT);
                element.append(path[i]);
            }
            String elStr = element.toString();
            String[] result = CoreAttributeConverter.convert(elStr);
            assertNotNull("Converter returned null for input: \"" + elStr + "\"", result);
            assertEquals("Size of returned array differs from input: \"" + elStr + "\"",
                    path.length, result.length);
            for (i = 0; i < path.length; i++) {
                assertTrue("Returned String array does not match input: \"" + elStr + "\"",
                        path[i].equals(result[i]));
            }
        }
    }

    /**
     * Verify attribute converter with set of path elements as arrays.
     * Every argument is a {@link String} array with valid path elements.
     */
    @Test
    public void verifyConverterWithValidArrays() {
        final String[][] paths = {
                {"a", "b"}, {"a", "b", "c"}, {"a", "b", "c", "d"},
                {"a b", "c d"}, {"a b", "c d", "e f"}};
        for (String[] path : paths) {
            String[] result = CoreAttributeConverter.convert(path);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append('"').append(path[i]).append('"');
            }
            assertNotNull("Converter returned null for input: " + sb.toString(), result);
            assertEquals("Size of returned array differs from input: " + sb.toString(),
                    path.length, result.length);
            for (int i = 0; i < path.length; i++) {
                assertTrue("Returned String array does not match input array: " + sb.toString(),
                        path[i].equals(result[i]));
            }
        }
    }

    /**
     * Verify attribute converter with set of invalid simple path elements.
     * Every argument is a single {@link String} with invalid path element without
     * separator.
     */
    @Test
    public void verifyConverterWithInvalidSingleElements() {
        final String[][] elements = {
                {null}, {""}, {" "}, {" a"}, {"a "}, {" a "}, {" a b"}, {"a b "}, {" a b "}};
        boolean exception = false;
        for (String[] element : elements) try {
            exception = false;
            String[] result = CoreAttributeConverter.convert(element);
        } catch (IllegalArgumentException ex) {
            exception = true;
        } finally {
            assertTrue("Converter did not throw IllegalArgumentException on invalid input: \""
                    + element[0] + "\"", exception);
        }
    }

    /**
     * Verify attribute converter with set of complex path elements.
     * Every argument is a single {@link String} with valid path with
     * one or more separators.
     */
    @Test
    public void verifyConverterWithInvalidComplexElements() {
        final String[][] paths = {
                {"", "b"}, {"a", ""}, {" ", "b"}, {"a", " "}, {"", ""}, {" ", " "},
                {" a", "b"}, {"a", " b"}, {"a ", "b"}, {"a", "b "},
                {" a ", "b"}, {"a", " b "}};
        boolean exception = false;
        for (String[] path : paths) {
            int length = 0;
            int i;
            for (i = 0; i < path.length; i++) {
                length += path[i].length() + 1;
            }
            StringBuilder element = new StringBuilder(length);
            for (i = 0; i < path.length; i++) {
                if (i > 0)
                    element.append(DOT);
                element.append(path[i]);
            }
            String elStr = element.toString();
            try {
                exception = false;
                String[] result = CoreAttributeConverter.convert(elStr);
            } catch (IllegalArgumentException ex) {
                exception = true;
            } finally {
                assertTrue("Converter did not throw IllegalArgumentException on invalid input: \""
                        + elStr + "\"", exception);
            }
        }
    }

    /**
     * Verify attribute converter with set of path elements as arrays.
     * Every argument is a {@link String} array with valid path elements.
     */
    @Test
    public void verifyConverterWithInvalidArrays() {
        final String[][] paths = {
                {"", "b"}, {"a", ""}, {" ", "b"}, {"a", " "}, {"", ""}, {" ", " "},
                {" a", "b"}, {"a", " b"}, {"a ", "b"}, {"a", "b "},
                {" a ", "b"}, {"a", " b "}};
        boolean exception = false;
        for (String[] path : paths) try {
            exception = false;
            String[] result = CoreAttributeConverter.convert(path);
        } catch (IllegalArgumentException ex) {
            exception = true;
        } finally {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append('"').append(path[i]).append('"');
            }
            assertTrue("Converter did not throw IllegalArgumentException on invalid input: "
                    + sb.toString(), exception);
        }
    }

    @Test
    public void verifyDefaultConstructor() {
        FetchGroup fg = new FetchGroup();

        assertEquals("default FetchGroup name is not an empty string", fg.getName(), "");
        assertTrue(fg.getItems().isEmpty());
    }

    @Test
    public void verifyNameConstructor() {
        FetchGroup fg = new FetchGroup("Test");

        assertEquals("Test name not set", "Test", fg.getName());
        assertTrue(fg.getItems().isEmpty());
        fg.toString();
    }

    @Test
    public void verifyNameConstructor_Null() {
        FetchGroup fg = new FetchGroup(null);

        assertNull("Null name not set", fg.getName());
        assertTrue(fg.getItems().isEmpty());
        fg.toString();
    }

    private void verifyInvalid(String arg) {
        FetchGroup fg = new FetchGroup();

        try {
            fg.addAttribute(arg);
        } catch (IllegalArgumentException iae) {
            return;
        }
        fail("IllegalArgumentException expected but not caught.");
    }

    @Test
    public void verifyInvalidAdd_null() {
        verifyInvalid(null);
    }

    @Test
    public void verifyInvalidAdd_empty() {
        verifyInvalid("");
    }

    @Test
    public void verifyInvalidAdd_dot() {
        verifyInvalid(".");
        verifyInvalid("..");
        verifyInvalid(". ");
        verifyInvalid(" .");
        verifyInvalid(" . ");
        verifyInvalid(". .");
    }

    @Test
    public void verifyInvalidAdd_startWithDot() {
        verifyInvalid(".name");
    }

    @Test
    public void verifyInvalidAdd_endWithDot() {
        verifyInvalid("name.");
    }

    @Test
    public void verifyInvalidAdd_space() {
        verifyInvalid(" ");
        verifyInvalid("\t");
        verifyInvalid("\n");
        verifyInvalid("\r");
    }

    /**
     * Verify that {@link FetchGroup#getItem(String)} works properly on an
     * empty FetchGroup.
     */
    @Test
    public void verifygetItem_EmptyFG() {
        FetchGroup fg = new FetchGroup();

        assertTrue(fg.getItems().isEmpty());

        assertNull(fg.getItem("test"));
        assertNull(fg.getItem("a.b"));

        assertTrue(fg.getItems().isEmpty());
    }

    @Test
    public void verifyAddAttribute() {
        FetchGroup fg = new FetchGroup();

        fg.addAttribute("test");

        assertEquals(1, fg.getItems().size());
        assertTrue(fg.getItems().containsKey("test"));
        assertNotNull(fg.getItems().get("test"));

        AttributeItem item = fg.getItem("test");
        assertNotNull(item);

        assertEquals("test", item.getAttributeName());
    }

    @Test
    public void verifyAddAttribute_Nested() {
        FetchGroup fg = new FetchGroup();

        fg.addAttribute("test.test");

        assertEquals(1, fg.getItems().size());
        assertTrue(fg.getItems().containsKey("test"));

        AttributeItem testFI = (AttributeItem) fg.getItems().get("test");
        assertNotNull(testFI);
        assertEquals("test", testFI.getAttributeName());
        assertNotNull(testFI.getGroup());
        assertEquals("test", testFI.getGroup().getName());

        testFI = fg.getItem("test");
        assertNotNull(testFI);
        assertEquals("test", testFI.getAttributeName());
        assertEquals(1, testFI.getGroup().getItems().size());
        assertTrue(testFI.getGroup().getItems().containsKey("test"));
        assertNotNull(testFI.getGroup().getItem("test"));
    }

    @Test
    public void verifyAdd2AttributesNestedFG() {
        FetchGroup fg = new FetchGroup();

        fg.addAttribute("a.b");
        fg.addAttribute("a.c");

        assertEquals(1, fg.getItems().size());
        assertTrue(fg.getItems().containsKey("a"));

        AttributeItem aItem = fg.getItem("a");
        FetchGroup aFG = (FetchGroup)aItem.getGroup();

        assertNotNull(aItem);
        assertNotNull(aFG);
//        assertFalse(aItem.useDefaultFetchGroup());
        assertEquals(2, aFG.getItems().size());
        assertEquals("a", aFG.getName());

        AttributeItem bItem = aFG.getItem("b");
        assertNotNull(bItem);
        assertEquals("b", bItem.getAttributeName());
        assertNull(bItem.getGroup());
//        assertTrue(bItem.useDefaultFetchGroup());
        assertSame(bItem, fg.getItem("a.b"));

        AttributeItem cItem = aFG.getItem("c");
        assertNotNull(cItem);
        assertEquals("c", cItem.getAttributeName());
        assertNull(cItem.getGroup());
//        assertTrue(cItem.useDefaultFetchGroup());
        assertSame(cItem, fg.getItem("a.c"));
    }

    @Test
    public void verifyAdd2AttributesNestedFG_parentFirst() {
        FetchGroup fg = new FetchGroup();

        fg.addAttribute("a");
        fg.addAttribute("a.b");
        fg.addAttribute("a.c");

        assertEquals(1, fg.getItems().size());
        assertTrue(fg.getItems().containsKey("a"));

        AttributeItem aItem = fg.getItem("a");
        FetchGroup aFG = (FetchGroup)aItem.getGroup();

        assertNotNull(aItem);
        assertNotNull(aFG);
//        assertFalse(aItem.useDefaultFetchGroup());
        assertEquals(2, aFG.getItems().size());
        assertEquals("a", aFG.getName());

        AttributeItem bItem = aFG.getItem("b");
        assertNotNull(bItem);
        assertEquals("b", bItem.getAttributeName());
        assertNull(bItem.getGroup());
//        assertTrue(bItem.useDefaultFetchGroup());
        assertSame(bItem, fg.getItem("a.b"));

        AttributeItem cItem = aFG.getItem("c");
        assertNotNull(cItem);
        assertEquals("c", cItem.getAttributeName());
        assertNull(cItem.getGroup());
//        assertTrue(cItem.useDefaultFetchGroup());
        assertSame(cItem, fg.getItem("a.c"));
    }

    @Test
    public void verifyAddAttribute_Nested2() {
        FetchGroup fg = new FetchGroup();

        fg.addAttribute("test.test.test");

        assertEquals(1, fg.getItems().size());
        assertTrue(fg.getItems().containsKey("test"));

        AttributeItem testFI = (AttributeItem) fg.getItems().get("test");
        assertNotNull(testFI);
        assertEquals("test", testFI.getAttributeName());

        testFI = fg.getItem("test");
        assertNotNull(testFI);
        assertEquals("test", testFI.getAttributeName());
        assertNotNull(testFI.getGroup());

        AttributeItem testFI2 = (AttributeItem) testFI.getGroup().getItems().get("test");
        assertNotNull(testFI2);
        assertEquals("test", testFI2.getAttributeName());
        assertNotNull(testFI2.getGroup());
        // as of bug 397772 we no longer cascade the name.  The functionality was not used anywhere and
        //was problematic to maintain for EntityGraph support
        assertEquals("test", testFI2.getGroup().getName());
        assertFalse(testFI2.getGroup().getItems().isEmpty());

        testFI2 = testFI.getGroup().getItem("test");
        assertNotNull(testFI2);
        assertEquals("test", testFI2.getAttributeName());
        assertFalse(testFI2.getGroup().getItems().isEmpty());
        assertEquals(1, testFI2.getGroup().getItems().size());
        assertTrue(testFI2.getGroup().getItems().containsKey("test"));
        assertNotNull(testFI2.getGroup().getItems().get("test"));

        testFI2 = fg.getItem("test.test");
        assertNotNull(testFI2);
        assertEquals("test", testFI2.getGroup().getName());
    }

    @Test
    public void verifyAdd() {
        FetchGroup fg = new FetchGroup();

        fg.addAttribute("test");

        assertEquals(1, fg.getItems().size());
        assertTrue(fg.getItems().containsKey("test"));
        assertNotNull(fg.getItems().get("test"));
        assertNotNull(fg.getItem("test"));
    }

    @Test
    public void verifyAdd_Nested() {
        FetchGroup fg = new FetchGroup();
        fg.addAttribute("test.test");

        assertEquals(1, fg.getItems().size());
        assertTrue(fg.getItems().containsKey("test"));

        AttributeItem testFI = fg.getItems().get("test");
        assertNotNull(testFI);
        assertEquals("test", testFI.getAttributeName());

        testFI = fg.getItem("test");
        assertNotNull(testFI);
        assertEquals("test", testFI.getAttributeName());
        assertEquals(1, testFI.getGroup().getItems().size());
        assertTrue(testFI.getGroup().getItems().containsKey("test"));
        assertNotNull(testFI.getGroup().getItem("test"));
    }

}
