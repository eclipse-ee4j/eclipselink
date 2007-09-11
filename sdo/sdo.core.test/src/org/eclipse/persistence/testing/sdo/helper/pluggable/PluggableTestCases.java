/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.pluggable;

import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class PluggableTestCases extends SDOTestCase {
    //protected String rootTypeName = "dataObject";    
    protected String rootTypeName = "EmployeeType";

    //protected String rootTypeUri = "commonj.sdo";    
    protected String rootTypeUri = "http://www.example.org";
    protected SDODataObject root;

    //public static final String DATAOBJECT_XML_PATH = "org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderNSDeep.xml";
    //public static final String DATAOBJECT_XSD_PATH = "org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeep.xsd";    
    public static final String DATAOBJECT_XML_PATH = "org/eclipse/persistence/testing/sdo/helper/xmlhelper/Employee.xml";
    public static final String DATAOBJECT_XSD_PATH = "org/eclipse/persistence/testing/sdo/helper/xmlhelper/Employee.xsd";

    public PluggableTestCases(String name) {
        super(name);//, HelperProvider.getDefaultContext());
    }

    /**
     * This function is invoked by the JUnit framework before test cases are executed
     */
    public void setUp() {
        FileInputStream inStream = null;

        // clear defined schemas
        super.setUp();
        try {
            // load in the schema
            String xsdString = getXSDString(DATAOBJECT_XSD_PATH);

            // Define Types so that processing attributes completes
            List types = xsdHelper.define(xsdString);

            // first we set up root data object
            inStream = new FileInputStream(DATAOBJECT_XML_PATH);

            //XMLDocument document = xmlHelper.load(inStream);
            //root = (SDODataObject)document.getRootObject();
            inStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            fail("PluggableTestCases.setup() failed to load DataObject");
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public SDODataObject load(String filename) {
        FileInputStream inStream = null;
        SDODataObject anObject = null;
        try {
            // 
            inStream = new FileInputStream(filename);

            XMLDocument document = xmlHelper.load(inStream);
            anObject = (SDODataObject)document.getRootObject();
            inStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            fail("PluggableTestCases.load() failed to load DataObject: " + filename);
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
    private String dataObjectToString(SDODataObject anObject) {
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

    private String getXSDString(String filename) {
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