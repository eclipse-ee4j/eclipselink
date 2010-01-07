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
/**
 *  @version $Header: ChangeSummaryLoadSaveTestCases.java 16-may-2007.13:47:16 mfobrien Exp $
 *  @author  mfobrien
 *  @since   release specific (what release of product did this appear in)
 */
package org.eclipse.persistence.testing.sdo.model.changesummary;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;

public class ChangeSummaryLoadSaveTestCases extends ChangeSummaryTestCases {
    //protected Property changeSummaryProperty;    
    protected static final String ROOT_TYPE_NAME// 		
     = "dataObject";
    protected static final String ROOT_TYPE_URI//	 		
     = "commonj.sdo";
    protected SDODataObject root;
    protected SDODataObject rootCSOnChild;

    public ChangeSummaryLoadSaveTestCases(String name) {
        super(name);
    }

    public SDODataObject loadObjectFromXML(String path) throws IOException {
        FileInputStream inStream = new FileInputStream(path);
        XMLDocument document = xmlHelper.load(inStream);
        SDODataObject anObject = (SDODataObject)document.getRootObject();
        inStream.close();
        return anObject;
    }

    protected void setChangeSummary(SDODataObject aCSRoot) {
        // get and set change summary (temp workaround does not propagate to children)
        SDOChangeSummary aCS = (SDOChangeSummary)aCSRoot.get("myChangeSummary");

        //SDOChangeSummary aCS = new SDOChangeSummary();
        aCS.setRootDataObject(aCSRoot);
        aCSRoot._setChangeSummary(aCS);
        aCSRoot.getChangeSummary().endLogging();

        //aCSRoot.getChangeSummary().beginLogging();
        //ChangeSummary changeSummary = aCSRoot.getChangeSummary();
        //changeSummary.endLogging();
    }

    public void serialize(SDODataObject anObject, String filename) {
        // declare streams and objects
        FileOutputStream fo = null;
        ObjectOutputStream so = null;
        try {
            // serialize
            fo = new FileOutputStream(filename);
            //so = new ObjectOutputStream(fo);
            xmlHelper.save(anObject,//                                                      
                           null,//rootTypeUri,//
                           "root",//rootTypeName,//                                                      
                           fo);
            // display object representation to the stream
            log("Serialized Object: " + dataObjectToString(anObject));
            // display XML representation to the stream
            if (anObject != null) {
                xmlHelper.save(anObject,//                                                      
                               null,//rootTypeUri,//
                               "root",//rootTypeName,//                                                      
                               System.out);
            }

            //so.flush();
            fo.close();
            //so.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("SDOResolvableTestCases.serialize() failed to write Object");
        }
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

    public void printDataObject(DataObject dataObject, int indent) {
        String margin = "";
        for (int i = 0; i < indent; i++) {
            margin += "\t";
        }
        System.out.println(margin + "DataObject: " + dataObject);
        // For each Property
        List properties = dataObject.getInstanceProperties();
        for (int p = 0, size = properties.size(); p < size; p++) {
            if (dataObject.isSet(p)) {
                Property property = (Property)properties.get(p);
                if (property.isMany()) {
                    // For many-valued properties, process a list of values
                    List values = dataObject.getList(p);
                    for (int v = 0, count = values.size(); v < count; v++) {
                        printValue(values.get(v), property, indent);
                    }
                } else {
                    // For single-valued properties, print out the value
                    printValue(dataObject.get(p), property, indent);
                }
            }
        }
    }

    void printValue(Object value, Property property, int indent) {
        // Get the name of the property
        String propertyName = property.getName();

        // Construct a string for the proper indentation
        String margin = "";
        for (int i = 0; i < indent; i++) {
            margin += "\t";
        }
        if ((value != null) && property.isContainment()) {
            // with printDataObject
            Type type = property.getType();
            String typeName = type.getName();
            System.out.println(margin + propertyName + " (" + typeName + "):");
            // our implementation needs the following instanceof check not in the spec
            if (value instanceof DataObject) {
                printDataObject((DataObject)value, indent + 1);
            } else {
                System.out.println(margin + propertyName + ": " + value);
            }
        } else {
            // For non-containment properties, just print the value
            System.out.println(margin + propertyName + ": " + value);
        }
    }
}
