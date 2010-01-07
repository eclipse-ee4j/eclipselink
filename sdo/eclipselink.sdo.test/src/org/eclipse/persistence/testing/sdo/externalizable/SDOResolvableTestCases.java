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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.externalizable;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.helper.DataObjectInputStream;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOResolvableTestCases extends SDOTestCase {
    //protected String rootTypeName = "dataObject";    
    protected String rootTypeName = "PurchaseOrderType";

    //protected String rootTypeUri = "commonj.sdo";    
    protected String rootTypeUri = "http://www.example.org";
    protected DataObject root;

    public SDOResolvableTestCases(String name) {
        // use static context
        super(name);

        //super(name, HelperProvider.getDefaultContext());
        // override setUp() - use instance context
        //super(name, SDOHelperContext.getInstance());
    }

    /**
     * This function is invoked by the JUnit framework before test cases are executed
     */
    public void setUp() {
        try {
            super.setUp();

            // load in the schema
            String xsdString = getXSDString("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeep.xsd");

            // Define Types so that processing attributes completes
            List types = xsdHelper.define(xsdString);

            // first we set up root data object
            FileInputStream inStream = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderNSDeep.xml");

            XMLDocument document = xmlHelper.load(inStream);
            root = (DataObject)document.getRootObject();
            inStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            fail("SDOResolvableTestCases.setup() failed to load DataObject");
        }
    }

    /**
     * This wrapper function around the ObjectOutputStream.writeObject(object) method
     * will invoke the Externalizable framework
     * @param anObject
     * @param filename
     * @throws IOException
     *             void
     *
     */
    public void serialize(DataObject anObject, String filename) {
        // declare streams and objects
        FileOutputStream aFileOutputStream = null;
        ObjectOutputStream anObjectInputStream = null;
        try {
            // serialize
            aFileOutputStream = new FileOutputStream(filename);
            anObjectInputStream = new ObjectOutputStream(aFileOutputStream);
            anObjectInputStream.writeObject(anObject);
            // display object representation to the stream
            //log("Serialized Object: " + dataObjectToString(anObject));
            // display XML representation to the stream
            //if (anObject != null) {
            //    xmlHelper.save(anObject,//                                                      
            //                   rootTypeUri,//
            //                   rootTypeName,//                                                      
            //                   System.out);
            //}
            anObjectInputStream.flush();
            aFileOutputStream.close();
            anObjectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("SDOResolvableTestCases.serialize() failed to write Object");
        }
    }

    public void serializeList(List theList, String filename) {
        // declare streams and objects
        FileOutputStream aFileOutputStream = null;
        ObjectOutputStream anObjectOutputStream = null;
        try {
            // serialize
            aFileOutputStream = new FileOutputStream(filename);
            anObjectOutputStream = new ObjectOutputStream(aFileOutputStream);
            anObjectOutputStream.writeObject(theList);
            anObjectOutputStream.flush();
            aFileOutputStream.close();
            anObjectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("SDOResolvableTestCases.serialize() failed to write Object");
        }
    }

    /**
     * This wrapper function around the ObjectOutputStream.readObject() method
     * will invoke the Externalizable framework.
     * Use our SDO DataObjectInputStream class in order to pass in a custom context
     * so we have type preservation across JVM instances.
     *
     * @param filename
     * @return
     * @throws IOException
     *             SDODataObject
     */
    public DataObject deserialize(String filename) {
        // declare streams and objects
        FileInputStream aFileInputStream = null;

        // ObjectInputStream wrapper (to pass a custom context)
        DataObjectInputStream aDataObjectInputStream = null;

        DataObject anObject = null;
        try {
            // DeSerialize
            aFileInputStream = new FileInputStream(filename);
            // use our wrapper for InputStream that maintains context
            aDataObjectInputStream = new DataObjectInputStream(aFileInputStream, aHelperContext);
            // read into context
            anObject = (DataObject)aDataObjectInputStream.readObject();
            // display object representation to the stream			
            //log("deSerialized Object: " + dataObjectToString(anObject));
            // display XML representation to the stream
            //if (anObject != null) {
            //    xmlHelper.save(anObject,//
            //                   rootTypeUri,//
            //                   rootTypeName,//                                                      
            //                   System.out);
            // }
            aDataObjectInputStream.close();
            aFileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("SDOResolvableTestCases.serialize() failed to read Object");
        }
        return anObject;
    }

    public List deserializeList(String filename) {
        // declare streams and objects
        FileInputStream aFileInputStream = null;

        // ObjectInputStream wrapper (to pass a custom context)
        DataObjectInputStream aDataObjectInputStream = null;

        List anObject = null;
        try {
            // DeSerialize
            aFileInputStream = new FileInputStream(filename);
            //use our wrapper for InputStream that maintains context
            aDataObjectInputStream = new DataObjectInputStream(aFileInputStream, aHelperContext);
            // read into context
            anObject = (List)aDataObjectInputStream.readObject();

            aFileInputStream.close();
            aDataObjectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("SDOResolvableTestCases.serialize() failed to read Object");
        }
        return anObject;
    }

    /**
     * Write an object representation of the SDODataObject to the stream
     * @param anObject
     * @return
     * String
     *
     */
    private String dataObjectToString(DataObject anObject) {
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
