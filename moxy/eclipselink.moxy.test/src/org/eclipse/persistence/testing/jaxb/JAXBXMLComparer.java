package org.eclipse.persistence.testing.jaxb;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JAXBXMLComparer extends XMLComparer{
	
    public JAXBXMLComparer() {
        super();
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
