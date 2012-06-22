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
/* $Header: SDOHelperContextTestCases.java 23-apr-2007.15:14:55 mfobrien Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    mfobrien    11/23/06 - Creation
 */

/**
 *  @version $Header: SDOHelperContextTestCases.java 23-apr-2007.15:14:55 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.1
 */

package org.eclipse.persistence.testing.sdo.helper.helpercontext;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

public class SDOHelperContextTestCases extends SDOTestCase {
    protected String rootTypeName = "EmployeeType";
    protected String rootTypeUri = "http://www.example.org";
	protected HelperContext aNonStaticHelperContext1;
	protected HelperContext aNonStaticHelperContext2;	
	protected HelperContext aStaticHelperContext;
	
	protected SDODataObject aNonStaticHelperContext1DataObject;
	protected SDODataObject aNonStaticHelperContext2DataObject;	
	protected SDODataObject aStaticHelperContextDataObject;

    public static final String CONTEXT1_DATAOBJECT_XSD_PATH = "org/eclipse/persistence/testing/sdo/schemas/EmployeeForHelperContext1.xsd";
    public static final String CONTEXT1_DATAOBJECT_XML_PATH = "org/eclipse/persistence/testing/sdo/helper/helpercontext/EmployeeForHelperContext1.xml";
    public static final String CONTEXT2_DATAOBJECT_XSD_PATH = "org/eclipse/persistence/testing/sdo/schemas/EmployeeForHelperContext2.xsd";
    public static final String CONTEXT2_DATAOBJECT_XML_PATH = "org/eclipse/persistence/testing/sdo/helper/helpercontext/EmployeeForHelperContext2.xml";
    public static final String STATIC_CONTEXT_DATAOBJECT_XSD_PATH = "org/eclipse/persistence/testing/sdo/schemas/EmployeeForStaticHelperContext.xsd";
    public static final String STATIC_CONTEXT_DATAOBJECT_XML_PATH = "org/eclipse/persistence/testing/sdo/helper/helpercontext/EmployeeForStaticHelperContext.xml";

    public SDOHelperContextTestCases(String name) {
        super(name);
    }

    //public SDOHelperContextTestCases(String name, HelperContext aContext) {
    //    super(name, aContext);
    //}

    /**
     * This function is invoked by the JUnit framework before test cases are executed
     */
    public void setUp() {
        super.setUp();
    }

    public SDODataObject load(String filename, HelperContext aContext) {
        FileInputStream inStream = null;
        SDODataObject anObject = null;
        try {
            // 
            inStream = new FileInputStream(filename);

            XMLDocument document = aContext.getXMLHelper().load(inStream);
            anObject = (SDODataObject)document.getRootObject();
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("SDOHelperContextTestCases.load() failed to load DataObject: " + filename);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return anObject;
    }

    /**
     * Write an object representation of the SDODataObject to the stream
     * @param anObject
     * @return
     * String
     */
    protected String dataObjectToString(SDODataObject anObject) {
        if (anObject == null) {
            return SDOConstants.EMPTY_STRING;
        }
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(anObject.toString());
        aBuffer.append("\n\t root: ");
        aBuffer.append(anObject.getRootObject());
        aBuffer.append("\n\t type: ");
        aBuffer.append(anObject.getType());
        aBuffer.append(" name: ");
        aBuffer.append(anObject.getType().getName());
        aBuffer.append("\n\t properties: (");
        // iterate any properties
        List properties = anObject.getInstanceProperties();
        if (!properties.isEmpty()) {
            List keys = anObject.getInstanceProperties();
            Iterator anIterator = keys.iterator();
            while (anIterator.hasNext()) {
                Property aKey = (Property)anIterator.next();
                aBuffer.append(aKey.getName());
                aBuffer.append(":");
                aBuffer.append(anObject.get(aKey));
                aBuffer.append(",\n\t\t");
            }
        }
        aBuffer.append(")");
        return aBuffer.toString();
    }

    protected String getXSDString(String filename) {
        try {
            FileInputStream inStream = new FileInputStream(filename);
            byte[] bytes = new byte[inStream.available()];
            inStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
