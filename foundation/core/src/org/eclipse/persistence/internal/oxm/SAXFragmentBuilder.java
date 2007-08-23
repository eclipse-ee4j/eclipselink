/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import java.util.Iterator;
import java.util.Stack;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;

/**
 *  @version $Header: SAXFragmentBuilder.java 09-aug-2007.16:14:57 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class SAXFragmentBuilder extends SAXDocumentBuilder {
    private UnmarshalRecord owningRecord;
    
    public SAXFragmentBuilder(UnmarshalRecord unmarshalRecord) {
        super();
        owningRecord = unmarshalRecord;
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if ((null != namespaceURI) && ("".equals(namespaceURI))) {
            namespaceURI = null;
        }
        if(namespaceURI != null && qName.indexOf(":") == -1) {
            //check for a prefix from the unmarshal record:
            String prefix = owningRecord.resolveNamespaceUri(namespaceURI);
            if(prefix != null && (!(prefix.equals("")))) {
                qName = prefix + ":" + qName;
            }
        }
        Element element = getInitializedDocument().createElementNS(namespaceURI, qName);
        Node parentNode = (Node)nodes.peek();

        if ((stringBuffer.length() > 0) && !(nodes.size() == 1)) {
            Text text = getInitializedDocument().createTextNode(stringBuffer.toString());
            parentNode.appendChild(text);
            stringBuffer.reset();
        }
        appendChildNode(parentNode, element);
        nodes.push(element);

        if (null != namespaceDeclarations) {
            Iterator namespacePrefixes = namespaceDeclarations.keySet().iterator();
            String prefix;
            String uri;
            while (namespacePrefixes.hasNext()) {
                prefix = (String)namespacePrefixes.next();
                uri = (String)namespaceDeclarations.get(prefix);
                addNamespaceDeclaration(element, prefix, uri);
            }
            namespaceDeclarations = null;
        }
        
        int numberOfAttributes = atts.getLength();
        String attributeNamespaceURI;
        for (int x = 0; x < numberOfAttributes; x++) {
            attributeNamespaceURI = atts.getURI(x); 
            if ((null != attributeNamespaceURI) && ("".equals(attributeNamespaceURI))) {
                attributeNamespaceURI = null;
            }
            if (attributeNamespaceURI == null) {
                element.setAttribute(atts.getQName(x), atts.getValue(x));
            } else {
                element.setAttributeNS(attributeNamespaceURI, atts.getQName(x), atts.getValue(x));
            }
        }
    }
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if(super.nodes.size() == 2) {
            Element endedElement = (Element)nodes.peek();
            if (stringBuffer.length() > 0) {
                Text text = getInitializedDocument().createTextNode(stringBuffer.toString());
                endedElement.appendChild(text);
                stringBuffer.reset();
            }
            //just the doc left in the stack. Finish this off.
            //mapping.setAttributeValueInObject(owningRecord.getObject(), getDocument().getDocumentElement());
            owningRecord.getXMLReader().setContentHandler(owningRecord);
            owningRecord.endElement(namespaceURI, localName, qName);
        } else {
            super.endElement(namespaceURI, localName, qName);
        }
        
    }
    public Stack getNodes() {
        return super.nodes;
    }
    
    public void setOwningRecord(UnmarshalRecord record) {
        this.owningRecord = record;
    }
    
    public void appendChildNode(Node parent, Node child) {
        if(parent != this.getDocument()) {
            parent.appendChild(child);
        }
    }
    
    public Attr buildAttributeNode(String namespaceURI, String localName, String value) {
        try {
            Attr attribute = getInitializedDocument().createAttributeNS(namespaceURI, localName);
            attribute.setValue(value);
            return attribute;
        } catch(SAXException ex) {}
        return null;
    }
    
    public Text buildTextNode(String textValue) {
        try {
            Text text = getInitializedDocument().createTextNode(textValue);
            return text;
        } catch(SAXException ex) {}
        return null;
        
    }

}