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
package org.eclipse.persistence.internal.oxm;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;

/**
 *  @version $Header: SAXFragmentBuilder.java 18-sep-2007.14:36:11 dmahar Exp $
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
        boolean bufferContainsOnlyWhitespace = stringBuffer.toString().trim().length() == 0;
        if (bufferContainsOnlyWhitespace) {
            stringBuffer.reset();
        }

        if ((stringBuffer.length() > 0) && !(nodes.size() == 1)) {
            Text text = getInitializedDocument().createTextNode(stringBuffer.toString());
            Node parent = this.nodes.get(nodes.size() - 1);
            parent.appendChild(text);
            processNamespacesForText(text.getTextContent(), (Element)parent);
            stringBuffer.reset();
        }
        if (null != namespaceURI && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if(qName == null){        	
            qName = localName;
            if(namespaceURI != null){
                if(owningRecord != null){
                    String prefix = owningRecord.resolveNamespaceUri(namespaceURI);
                    if(prefix != null && prefix.length() > 0){
                        qName = prefix +Constants.COLON+ qName;
                    }
                }
             }
        }
        int qNameColonIndex = qName.indexOf(Constants.COLON);
        if ((namespaceURI != null) && (qNameColonIndex == -1)) {
            //check for a prefix from the unmarshal record:
            String prefix = owningRecord.resolveNamespaceUri(namespaceURI);
            if (prefix != null && prefix.length() >0){
                qName = prefix + Constants.COLON + qName;
                qNameColonIndex = prefix.length();
            }
        }

        Element element = getInitializedDocument().createElementNS(namespaceURI, qName);
        Node parentNode = nodes.get(nodes.size() - 1);

        appendChildNode(parentNode, element);
        nodes.add(element);

        if (qNameColonIndex > -1) {
            String prefix = qName.substring(0, qNameColonIndex);
            String parentUri = null;
            if (element.getParentNode() != null) {
                parentUri = XMLPlatformFactory.getInstance().getXMLPlatform().resolveNamespacePrefix(element.getParentNode(), prefix);
            }
            if ((parentUri == null) || parentUri.length() == 0) {
                startPrefixMapping(prefix, namespaceURI);
            }
        }

        if (null != namespaceDeclarations) {
            Iterator namespaces = namespaceDeclarations.entrySet().iterator();
            while (namespaces.hasNext()) {
            	Map.Entry entry = (Map.Entry)namespaces.next();
                addNamespaceDeclaration(element, (String)entry.getKey(), (String)entry.getValue());
            }
            namespaceDeclarations = null;
        }

        int numberOfAttributes = atts.getLength();
        String attributeNamespaceURI;
        for (int x = 0; x < numberOfAttributes; x++) {
            attributeNamespaceURI = atts.getURI(x);
            // Empty string will be treated as a null URI
            if (null != attributeNamespaceURI && attributeNamespaceURI.length() == 0) {
            	attributeNamespaceURI = null;
            }
            // Handle case where prefix/uri are not set on an xmlns prefixed attribute
            if (attributeNamespaceURI == null && atts.getQName(x).startsWith(javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON)) {
        		attributeNamespaceURI = javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
            }
            String value = atts.getValue(x);
            if (attributeNamespaceURI == null) {
                element.setAttribute(atts.getQName(x), value);
            } else {
                element.setAttributeNS(attributeNamespaceURI, atts.getQName(x), value == null ? Constants.EMPTY_STRING : value);
            }
            if(value != null) {
                processNamespacesForText(value, element);
            }
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (super.nodes.size() == 2) {
            Element endedElement = (Element)nodes.get(nodes.size() -1);
            if (stringBuffer.length() > 0) {
                Text text = getInitializedDocument().createTextNode(stringBuffer.toString());
                endedElement.appendChild(text);
                stringBuffer.reset();
                processNamespacesForText(text.getTextContent(), endedElement);
            }

            //just the doc left in the stack. Finish this off.
            owningRecord.getXMLReader().setContentHandler(owningRecord);
            owningRecord.endElement(namespaceURI, localName, qName);

        } else {
            super.endElement(namespaceURI, localName, qName);
        }
    }

    
    
    public void endSelfElement(String namespaceURI, String localName, String qName) throws SAXException {        
    	
    	if (super.nodes.size() == 2) {
            Element endedElement = (Element)nodes.get(nodes.size() -1);
            if (stringBuffer.length() > 0) {
                Text text = getInitializedDocument().createTextNode(stringBuffer.toString());
                endedElement.appendChild(text);
                stringBuffer.reset();
            }         
        } else {
            super.endElement(namespaceURI, localName, qName);
        }
    }
    public List<Node> getNodes() {
        return super.nodes;
    }

    public void setOwningRecord(UnmarshalRecord record) {
        this.owningRecord = record;
    }

    public void appendChildNode(Node parent, Node child) {
        if (parent != this.getDocument()) {
            parent.appendChild(child);
        }
    }

    public Attr buildAttributeNode(String namespaceURI, String localName, String value) {
        try {
            Attr attribute = getInitializedDocument().createAttributeNS(namespaceURI, localName);
            attribute.setValue(value);
            return attribute;
        } catch (SAXException ex) {
        }
        return null;
    }

    public Text buildTextNode(String textValue) {
        try {
            Text text = getInitializedDocument().createTextNode(textValue);
            return text;
        } catch (SAXException ex) {
        }
        return null;

    } 
    
    /**
     * Adds a namespace declaration to the parent element if the textValue represents a
     * prefixed qualified name. The determination of a qname is based on the existance of a
     * colon character and the ability to resolve the characters before the colon to a 
     * namespace uri. 
     * @param textValue
     * @param parentNode
     */
    private void processNamespacesForText(String textValue, Element parentNode) {
        //If the text value is a qname, we may need to do namespace processing
        int colon = textValue.indexOf(':');
        if(colon != -1) {
            String prefix = textValue.substring(0, colon);
            XMLPlatform platform = XMLPlatformFactory.getInstance().getXMLPlatform();
            String uri = platform.resolveNamespacePrefix(parentNode, prefix);
            if(uri == null) {
                uri = this.owningRecord.resolveNamespacePrefix(prefix);
                if(uri != null) {
                    //add namespace declaration
                    addNamespaceDeclaration(parentNode, prefix, uri);
                }
            }
        }
    }
    
}