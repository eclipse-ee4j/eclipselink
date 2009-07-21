/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
import java.util.Stack;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
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
        if ((null != namespaceURI) && ("".equals(namespaceURI))) {
            namespaceURI = null;
        }
        if(qName == null){        	
            qName = localName;
            if(namespaceURI != null){
                if(owningRecord != null){
                    String prefix = owningRecord.resolveNamespaceUri(namespaceURI);
                    if(prefix != null && !prefix.equals("")){
                        qName = prefix +":" + qName;
                    }
                }
             }
        }
        int qNameColonIndex = qName.indexOf(":");
        if ((namespaceURI != null) && (qNameColonIndex == -1)) {
            //check for a prefix from the unmarshal record:
            String prefix = owningRecord.resolveNamespaceUri(namespaceURI);
            if ((prefix != null) && (!(prefix.equals("")))) {
                qName = prefix + ":" + qName;
            }
        }

        Element element = getInitializedDocument().createElementNS(namespaceURI, qName);
        Node parentNode = (Node)nodes.peek();

        boolean bufferContainsOnlyWhitespace = stringBuffer.toString().trim().length() == 0;
        if (bufferContainsOnlyWhitespace) {
            stringBuffer.reset();
        }
        appendChildNode(parentNode, element);
        nodes.push(element);

        qNameColonIndex = qName.indexOf(":");
        if (qNameColonIndex > -1) {
            String prefix = qName.substring(0, qNameColonIndex);
            String parentUri = null;
            if (element.getParentNode() != null) {
                parentUri = XMLPlatformFactory.getInstance().getXMLPlatform().resolveNamespacePrefix(element.getParentNode(), prefix);
            }
            if ((parentUri == null) || parentUri.equals("")) {
                startPrefixMapping(prefix, namespaceURI);
            }
        }

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
            // Empty string will be treated as a null URI
            if ((null != attributeNamespaceURI) && ("".equals(attributeNamespaceURI))) {
            	attributeNamespaceURI = null;
            }
            // Handle case where prefix/uri are not set on an xmlns prefixed attribute
            if (attributeNamespaceURI == null && atts.getQName(x).startsWith(XMLConstants.XMLNS + ":")) {
        		attributeNamespaceURI = XMLConstants.XMLNS_URL;
            }
            
            if (attributeNamespaceURI == null) {
                element.setAttribute(atts.getQName(x), atts.getValue(x));
            } else {
                String value = atts.getValue(x);
                element.setAttributeNS(attributeNamespaceURI, atts.getQName(x), value);

                if (XMLConstants.SCHEMA_INSTANCE_URL.equals(attributeNamespaceURI) && XMLConstants.SCHEMA_TYPE_ATTRIBUTE.equals(atts.getLocalName(x))) {
                    int colonIndex = value.indexOf(':');
                    if (colonIndex > -1) {
                        String prefix = value.substring(0, colonIndex);
                        String uri = XMLPlatformFactory.getInstance().getXMLPlatform().resolveNamespacePrefix(element, prefix);
                        if ((uri == null) || (uri.equals(""))) {                            
                            //walk up unmarshalRecords to find prefix
                            String theUri = owningRecord.resolveNamespacePrefix(prefix);
                            if ((theUri != null) && !(theUri.equals(""))) {
                                element.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + prefix, theUri);
                            }
                        }
                    }
                }
            }
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (super.nodes.size() == 2) {
            Element endedElement = (Element)nodes.peek();
            if (stringBuffer.length() > 0) {
                Text text = getInitializedDocument().createTextNode(stringBuffer.toString());
                endedElement.appendChild(text);
                stringBuffer.reset();
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
            Element endedElement = (Element)nodes.peek();
            if (stringBuffer.length() > 0) {
                Text text = getInitializedDocument().createTextNode(stringBuffer.toString());
                endedElement.appendChild(text);
                stringBuffer.reset();
            }         
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
}