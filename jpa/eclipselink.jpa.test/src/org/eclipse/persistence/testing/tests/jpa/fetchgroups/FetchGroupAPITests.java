/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.fetchgroups;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.queries.FetchGroup;

import org.junit.Test;

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
        assertEquals("test.test", testFI2.getGroup().getName());
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
        assertEquals("test.test", testFI2.getGroup().getName());
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
