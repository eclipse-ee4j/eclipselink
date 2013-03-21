/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * This class is used to compare if two DOM nodes are equal.
 */
public class XMLComparer {
	
    private boolean ignoreOrder;
	
    public XMLComparer() {
        super();
        ignoreOrder = false;
    }

    /**
     * Compare two DOM nodes.
     * @param control The first node in the comparison.
     * @param test The second node in the comparison.
     * @return Return true if the nodes are equal, else false.
     */
    public boolean isNodeEqual(Node control, Node test) {
        if (control == test) {
            return true;
        } else if ((null == control) || (null == test)) {
            return false;
        } else if (control.getNodeType() != test.getNodeType()) {
            return false;
        }
        switch (control.getNodeType()) {
        case (Node.ATTRIBUTE_NODE):
            return isAttributeEqual((Attr)control, (Attr)test);
        case (Node.CDATA_SECTION_NODE):
            return isTextEqual((Text)control, (Text)test);
        case (Node.COMMENT_NODE):
            return isCommentEqual((Comment)control, (Comment)test);
        case (Node.DOCUMENT_FRAGMENT_NODE):
            return isDocumentFragmentEqual((DocumentFragment)control, (DocumentFragment)test);
        case (Node.DOCUMENT_NODE):
            return isDocumentEqual((Document)control, (Document)test);
        case (Node.DOCUMENT_TYPE_NODE):
            return isDocumentTypeEqual((DocumentType)control, (DocumentType)test);
        case (Node.ELEMENT_NODE):
            return isElementEqual((Element)control, (Element)test);
        case (Node.ENTITY_NODE):
            return false;
        case (Node.ENTITY_REFERENCE_NODE):
            return isEntityReferenceEqual((EntityReference)control, (EntityReference)test);
        case (Node.NOTATION_NODE):
            return false;
        case (Node.PROCESSING_INSTRUCTION_NODE):
            return isProcessingInstructionEqual((ProcessingInstruction)control, (ProcessingInstruction)test);
        case (Node.TEXT_NODE):
            return isTextEqual((Text)control, (Text)test);
        default:
            return true;
        }
    }

    protected boolean isAttributeEqual(Attr control, Attr test) {
        if (!isStringEqual(control.getNamespaceURI(), test.getNamespaceURI())) {
            return false;
        }
        if (!isStringEqual(control.getName(), test.getName())) {
            return false;
        }
        if (!isStringEqual(control.getNodeValue(), test.getNodeValue())) {
            return false;
        }
        return true;
    }

    private boolean isCommentEqual(Comment control, Comment test) {
        if (!isStringEqual(control.getNodeValue(), test.getNodeValue())) {
            return false;
        }
        return true;
    }

    private boolean isDocumentEqual(Document control, Document test) {
        if (!isDocumentTypeEqual(control.getDoctype(), test.getDoctype())) {
            return false;
        }

        Element controlRootElement = control.getDocumentElement();
        Element testRootElement = test.getDocumentElement();
        if (controlRootElement == testRootElement) {
            return true;
        } else if ((null == controlRootElement) || (null == testRootElement)) {
            return false;
        }
        return isElementEqual(controlRootElement, testRootElement);
    }

    private boolean isDocumentFragmentEqual(DocumentFragment control, DocumentFragment test) {
        return isNodeListEqual(control.getChildNodes(), test.getChildNodes());
    }

    private boolean isDocumentTypeEqual(DocumentType control, DocumentType test) {
        if (control == test) {
            return true;
        } else if ((null == control) || (null == test)) {
            return false;
        }

        if (!isStringEqual(control.getName(), test.getName())) {
            return false;
        }
        if (!isStringEqual(control.getPublicId(), test.getPublicId())) {
            return false;
        }
        if (!isStringEqual(control.getSystemId(), test.getSystemId())) {
            return false;
        }

        return true;
    }

    private boolean isElementEqual(Element control, Element test) {
        if (!isStringEqual(control.getNamespaceURI(), test.getNamespaceURI())) {
            return false;
        }
        if (!isStringEqual(control.getTagName(), test.getTagName())) {
            return false;
        }

        // COMPARE ATTRIBUTES    
        NamedNodeMap controlAttributes = control.getAttributes();
        NamedNodeMap testAttributes = test.getAttributes();
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

        // COMPARE CHILD NODES
        return isNodeListEqual(control.getChildNodes(), test.getChildNodes());
    }

    private boolean isEntityReferenceEqual(EntityReference control, EntityReference test) {
        if (!isStringEqual(control.getNodeName(), test.getNodeName())) {
            return false;
        }
        return true;
    }

    private boolean isProcessingInstructionEqual(ProcessingInstruction control, ProcessingInstruction test) {
        if (!isStringEqual(control.getTarget(), test.getTarget())) {
            return false;
        }
        if (!isStringEqual(control.getData(), test.getData())) {
            return false;
        }
        return true;
    }

    private boolean isTextEqual(Text control, Text test) {
        return isStringEqual(control.getNodeValue(), test.getNodeValue());
    }

    private boolean isNodeListEqual(NodeList control, NodeList test) {
        int numberOfControlNodes = control.getLength();
        if (numberOfControlNodes != test.getLength()) {
            return false;
        }
        if(ignoreOrder){
            for (int x = 0; x < numberOfControlNodes; x++) {
                if(!isNodeInNodeList(control.item(x), test)){
                    return false;
                }
            }
            for (int x = 0; x < numberOfControlNodes; x++) {
                if(!isNodeInNodeList(test.item(x), control)){
                    return false;
                }
            }
        }else{
            for (int x = 0; x < numberOfControlNodes; x++) {
                if (!isNodeEqual(control.item(x), test.item(x))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isNodeInNodeList(Node node, NodeList nodeList){
        int length = nodeList.getLength();    
        for (int x = 0; x < length; x++) {
            Node nextNode = nodeList.item(x);
            if(isNodeEqual(node, nextNode)){
                return true;
            }
        }
        return false;
    }
    
    private boolean isStringEqual(String control, String test) {
        if (control == test) {
            return true;
        } else if (null == control) {
            return false;
        } else {
            return control.equals(test);
        }
    }

    public boolean isIgnoreOrder() {
        return ignoreOrder;
    }

    public void setIgnoreOrder(boolean ignoreOrder) {
        this.ignoreOrder = ignoreOrder;
    }
}
