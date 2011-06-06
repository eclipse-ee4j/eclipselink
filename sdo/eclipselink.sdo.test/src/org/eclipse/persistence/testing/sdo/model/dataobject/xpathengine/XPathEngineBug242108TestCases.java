/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rick.barkhouse@oracle.com - initial implementation
 ******************************************************************************/  

package org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import junit.textui.TestRunner;

import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;

public class XPathEngineBug242108TestCases extends SDOTestCase {

	private String schemaLocation = "org/eclipse/persistence/testing/sdo/model/dataobject/xpathengine/bug242108schema.xsd";

	private String testObjectUri  = "http://xmlns.oracle.com/oracle/apps/fnd";
	private String testObjectName = "CustomType";
	
    public XPathEngineBug242108TestCases(String name) {
        super(name);
    }
	
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine.XPathEngineBug242108TestCases" };
        TestRunner.main(arguments);
    }

    // == TESTS - set() ===============================================================
    
    public void testSetBracketIndexZero() throws Exception {
    	InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

		Class expectedExceptionClass = IllegalArgumentException.class;
        Class caughtExceptionClass = null;
        try {
        	do1.set("activity[0]", new String("Test"));
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testSetBracketIndexOutOfBounds() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = IndexOutOfBoundsException.class;
        Class caughtExceptionClass = null;
        try {
        	do1.set("activity[99]", new String("Test"));
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testSetDotIndexOutOfBounds() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = IndexOutOfBoundsException.class;
        Class caughtExceptionClass = null;
        try {
        	do1.set("activity.99", new String("Test"));
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testSetNoIndex() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = IllegalArgumentException.class;
        Class caughtExceptionClass = null;
        try {
        	do1.set("activity", new String("Test"));
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testSetBracketIndexZeroWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

		Class expectedExceptionClass = SDOException.class;
        Class caughtExceptionClass = null;
        try {
            do1.set("activity[0]/what", new String("Test"));
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testSetBracketIndexOutOfBoundsWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = SDOException.class;
        Class caughtExceptionClass = null;
        try {
            do1.set("activity[99]/what", new String("Test"));
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testSetDotIndexOutOfBoundsWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = SDOException.class;
        Class caughtExceptionClass = null;
        try {
            do1.set("activity.99/what", new String("Test"));
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testSetNoIndexWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = SDOException.class;
        Class caughtExceptionClass = null;
        try {
            do1.set("activity/what", new String("Test"));
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    // == TESTS - get() ===============================================================
    
    public void testGetBracketIndexZero() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = null;
        Class caughtExceptionClass = null;
        try {
        	do1.get("activity[0]");
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testGetBracketIndexOutOfBounds() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = null;
        Class caughtExceptionClass = null;
        try {
        	do1.get("activity[99]");
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testGetDotIndexOutOfBounds() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = null;
        Class caughtExceptionClass = null;
        try {
        	do1.get("activity.99");
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testGetBracketIndexZeroWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = null;
        Class caughtExceptionClass = null;
        try {
            do1.get("activity[0]/what");
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testGetBracketIndexOutOfBoundsWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = null;
        Class caughtExceptionClass = null;
        try {
            do1.get("activity[99]/what");
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testGetDotIndexOutOfBoundsWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = null;
        Class caughtExceptionClass = null;
        try {
            do1.get("activity.99/what");
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    public void testGetNoIndex() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        // This should not throw an exception
        Class caughtExceptionClass = null;
        try {
            do1.get("activity");
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertNull("An unexpected exception was thrown.", caughtExceptionClass);
    }
    
    public void testGetNoIndexWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        Class expectedExceptionClass = null;
        Class caughtExceptionClass = null;
        try {
            do1.get("activity/what");
        } catch (Exception e) {
        	caughtExceptionClass = e.getClass();
        }
        
        assertEquals("The expected exception was not thrown.", expectedExceptionClass, caughtExceptionClass);
    }

    // == TESTS - isSet() =============================================================

    public void testIsSetIndexZero() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        boolean isSet = false;
        isSet = do1.isSet("activity[0]");
        
        assertFalse("isSet() did not return 'false' as expected.", isSet);
    }

    public void testIsSetBracketIndexOutOfBounds() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        boolean isSet = false;
        isSet = do1.isSet("activity[99]");
        
        assertFalse("isSet() did not return 'false' as expected.", isSet);
    }

    public void testIsSetDotIndexOutOfBounds() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        boolean isSet = false;
        isSet = do1.isSet("activity.99");
        
        assertFalse("isSet() did not return 'false' as expected.", isSet);
    }

    public void testIsSetNoIndex() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        boolean isSet = false;
        isSet = do1.isSet("activity");
        
        assertFalse("isSet() did not return 'false' as expected.", isSet);
    }

    public void testIsSetIndexZeroWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        boolean isSet = false;
        isSet = do1.isSet("activity[0]/what");
        
        assertFalse("isSet() did not return 'false' as expected.", isSet);
    }

    public void testIsSetBracketIndexOutOfBoundsWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        boolean isSet = false;
        isSet = do1.isSet("activity[99]/what");
        
        assertFalse("isSet() did not return 'false' as expected.", isSet);
    }

    public void testIsSetDotIndexOutOfBoundsWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        boolean isSet = false;
        isSet = do1.isSet("activity.99/what");
        
        assertFalse("isSet() did not return 'false' as expected.", isSet);
    }

    public void testIsSetNoIndexWithChild() throws Exception {
        InputStream is = new FileInputStream(schemaLocation);
        List types = xsdHelper.define(is, null);
        DataObject do1 = dataFactory.create(testObjectUri, testObjectName);

        boolean isSet = false;
        isSet = do1.isSet("activity/what");
        
        assertFalse("isSet() did not return 'false' as expected.", isSet);
    }

}
