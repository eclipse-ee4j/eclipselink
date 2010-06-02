/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - Jan 30/2009 - 1.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.helpercontext;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.helper.DataObjectInputStream;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProviderImpl;

public class UserSetContextMapTestCases extends SDOHelperContextTestCases {
	protected final String FILE_NAME = tempFileDir + "/serialized_dataobject";
	protected String implClassname;
	protected HelperContext localCtx;
	protected SDODataObject localDObj;
	protected HelperContext globalCtx;
	
	public UserSetContextMapTestCases(String name) {
		super(name);
	}
	
	protected void resetGlobalContext() {
    	((SDOTypeHelper)globalCtx.getTypeHelper()).reset();
    	((SDOXSDHelper)globalCtx.getXSDHelper()).reset();
    	((SDOXMLHelper)globalCtx.getXMLHelper()).reset();
	}
	
	public void setUp() {
        FileInputStream inStream = null;
        // Clear defined schemas
        super.setUp();
        // Load the schema
        String xsdString = getXSDString(CONTEXT1_DATAOBJECT_XSD_PATH);
        // Define Types
        localCtx = new SDOHelperContext();
        localCtx.getXSDHelper().define(xsdString);
        localDObj = load(CONTEXT1_DATAOBJECT_XML_PATH, localCtx);
    	implClassname = localDObj.getType().getInstanceClassName();
        globalCtx = HelperProviderImpl.getDefaultContext();
	}
	
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.helpercontext.UserSetContextMapTestCases" };
        TestRunner.main(arguments);
    }
    
    /**
     * Test should serialize/deserialize successfully/
     */
    public void testResolveWithHelperContextSetInUserMap() {
        //First overwrite the existing context with an empty one
        SDOHelperContext.putHelperContext(Thread.currentThread().getContextClassLoader(), new SDOHelperContext());
        String xsdString = getXSDString(CONTEXT1_DATAOBJECT_XSD_PATH);
        localCtx = new SDOHelperContext("customId");
        localCtx.getXSDHelper().define(xsdString);
        localDObj = load(CONTEXT1_DATAOBJECT_XML_PATH, localCtx);

        SDOHelperContext.putHelperContext(Thread.currentThread().getContextClassLoader(), localCtx);
        
        serialize(localDObj, FILE_NAME);
        SDODataObject dobj = (SDODataObject) deserialize(FILE_NAME);
        String dobjImplClassName = dobj.getType().getInstanceClassName();
    	assertTrue("Expected ["+implClassname+"] but was ["+dobjImplClassName+"]", dobjImplClassName.equals(implClassname));
    	SDOHelperContext.removeHelperContext(Thread.currentThread().getContextClassLoader());
    }
    
    public void testResolveWithLocalHelperContextSetInUserMap() {
        SDOHelperContext.putHelperContext(Thread.currentThread().getContextClassLoader(), localCtx);
        serialize(localDObj, FILE_NAME);
        SDODataObject dobj = (SDODataObject) deserialize(FILE_NAME);
        String dobjImplClassName = dobj.getType().getInstanceClassName();
        assertTrue("Expected ["+implClassname+"] but was ["+dobjImplClassName+"]", dobjImplClassName.equals(implClassname));
        SDOHelperContext.removeHelperContext(Thread.currentThread().getContextClassLoader());
    }    
    
    /**
     * We do not set the loader/helper context pair in the SDOHelperContext
     * user-set map, so we expect an error upon deserialization.  We will
     * get back org.eclipse.persistence.sdo.dataobjects.OpenSequencedTypeImpl,
     * instead of org.example.EmployeeTypeImpl.
     */
    public void testResolveWithoutHelperContextSetInUserMap() {
    	serialize(localDObj, FILE_NAME);
    	resetGlobalContext();
    	DataObject dobj = deserialize(FILE_NAME);
    	String dobjImplClassName = dobj.getType().getInstanceClass().getName();
    	assertFalse("Expected [org.eclipse.persistence.sdo.dataobjects.OpenSequencedType] but was ["+dobjImplClassName+"]", dobjImplClassName.equals(implClassname));
    }

    public void serialize(DataObject anObject, String filename) {
        FileOutputStream aFileOutputStream = null;
        ObjectOutputStream anObjectInputStream = null;
        try {
            aFileOutputStream = new FileOutputStream(filename);
            anObjectInputStream = new ObjectOutputStream(aFileOutputStream);
            anObjectInputStream.writeObject(anObject);
            anObjectInputStream.flush();
            aFileOutputStream.close();
            anObjectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred during serialize");
        }
    }

    public DataObject deserialize(String filename) {
        FileInputStream aFileInputStream = null;
        DataObjectInputStream aDataObjectInputStream = null;
        DataObject anObject = null;
        try {
            aFileInputStream = new FileInputStream(filename);
            aDataObjectInputStream = new DataObjectInputStream(aFileInputStream, globalCtx);
            anObject = (DataObject)aDataObjectInputStream.readObject();
            aDataObjectInputStream.close();
            aFileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred during deserialize");
        }
        return anObject;
    }
}
