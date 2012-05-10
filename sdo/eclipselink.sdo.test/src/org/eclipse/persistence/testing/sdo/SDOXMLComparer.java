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
package org.eclipse.persistence.testing.sdo;

import java.util.List;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SDOXMLComparer extends XMLComparer {
    public SDOXMLComparer() {
      super();
    }

    protected boolean isAttributeEqual(Attr control, Attr test) {
        String name = control.getLocalName();
        if (name.equals("delete") || name.equals("create") || name.equals("unset")) {
            String controlValue = control.getNodeValue();
            String testValue = test.getNodeValue();
            if (controlValue.length() == testValue.length()) {
                try {
                    List controlList = (List)XMLConversionManager.getDefaultXMLManager().convertObject(controlValue, List.class);
                    List testList = (List)XMLConversionManager.getDefaultXMLManager().convertObject(testValue, List.class);
                    if (controlList.size() == testList.size()) {
                        if ((controlList.containsAll(testList)) && testList.containsAll(controlList)) {
                            return true;
                        }
                    }
                    return super.isAttributeEqual(control, test);
                } catch (ConversionException ce) {                    
                    ce.printStackTrace();
                    return false;
                }
            } else {
                return super.isAttributeEqual(control, test);
            }
        } else {
            return super.isAttributeEqual(control, test);
        }    
    }
    
    /**
     * Compares two XML documents which represent XML Schemas. Order of declarations should
     * be ignored. 
     */
    public boolean isSchemaEqual(Document control, Document test) {
        Element controlRoot = control.getDocumentElement();
        Element testRoot = test.getDocumentElement();
        
        if(!(controlRoot.getChildNodes().getLength() == testRoot.getChildNodes().getLength())) {
            return false;
        }
        
        //compare attributes:
        NamedNodeMap controlAttributes = controlRoot.getAttributes();
        NamedNodeMap testAttributes = testRoot.getAttributes();
        int numberOfControlAttributes = controlAttributes.getLength();
        int numberOfTestAttributes = testAttributes.getLength();
        if (numberOfControlAttributes != numberOfTestAttributes) {
            return false;
        }
        Attr controlAttribute;
        Attr testAttribute;
        for (int x = 0; x < numberOfControlAttributes; x++) {
            controlAttribute = (Attr)controlAttributes.item(x);
            if (null == controlAttribute.getNamespaceURI()) {
                testAttribute = (Attr)testAttributes.getNamedItem(controlAttribute.getNodeName());
            } else {
                testAttribute = (Attr)testAttributes.getNamedItemNS(controlAttribute.getNamespaceURI(), controlAttribute.getLocalName());
            }
            if (null == testAttribute) {
                return false;
            } else if (!isAttributeEqual(controlAttribute, testAttribute)) {
                return false;
            }
        }
        
        //compare definitions ignoring order and text
        NodeList controlChildren = controlRoot.getChildNodes();
        for(int i = 0; i < controlChildren.getLength(); i++) {
            Node controlChild = controlChildren.item(i);
            if(controlChild.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            NamedNodeMap controlChildAttributes = controlChild.getAttributes();
            Node testChild = null;
            //Find the equivalent node in the test children.
            NodeList testChildren = testRoot.getElementsByTagNameNS(controlChild.getNamespaceURI(), controlChild.getLocalName());
            for(int j = 0; j < testChildren.getLength(); j++) {
                Node next = testChildren.item(j);
                NamedNodeMap testChildAttributes = next.getAttributes();
                numberOfControlAttributes = controlChildAttributes.getLength();
                numberOfTestAttributes = testChildAttributes.getLength();
                boolean equalAttributes = true;
                if(numberOfControlAttributes != numberOfTestAttributes) {
                    equalAttributes = false;
                }
                for (int x = 0; x < numberOfControlAttributes; x++) {
                    controlAttribute = (Attr)controlChildAttributes.item(x);
                    if (null == controlAttribute.getNamespaceURI()) {
                        testAttribute = (Attr)testChildAttributes.getNamedItem(controlAttribute.getNodeName());
                    } else {
                        testAttribute = (Attr)testChildAttributes.getNamedItemNS(controlAttribute.getNamespaceURI(), controlAttribute.getLocalName());
                    }
                    if (testAttribute == null) {
                        equalAttributes = false;
                    } else if (!isAttributeEqual(controlAttribute, testAttribute)) {
                        equalAttributes = false;
                    }
                }
                if(equalAttributes) {
                    //if the attributes are all equal, then these are the same definition
                    testChild = next;
                    break;
                }
            }
            if(testChild == null || !isNodeEqual(controlChild, testChild)) {
                return false;
            }
        }
        return true;
    }
}
