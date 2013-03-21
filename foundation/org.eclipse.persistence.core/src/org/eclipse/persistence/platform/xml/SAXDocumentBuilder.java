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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.StrBuffer;
import org.eclipse.persistence.internal.oxm.record.ExtendedContentHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * <p><b>Purpose</b>:  Build a DOM from SAX events.</p>
 */

public class SAXDocumentBuilder implements ExtendedContentHandler {
    protected Document document;
    protected List<Node> nodes;
    protected XMLPlatform xmlPlatform;
    protected Map namespaceDeclarations;
    protected StrBuffer stringBuffer;

    protected Locator locator;

    public SAXDocumentBuilder() {
        super();
        nodes = new ArrayList<Node>();
        xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        stringBuffer = new StrBuffer();
        namespaceDeclarations = new HashMap();
    }

    public Document getDocument() {
        return document;
    }

    public Document getInitializedDocument() throws SAXException {
        if (document == null) {
            try {
                document = xmlPlatform.createDocument();
                nodes.add(document);
            } catch (Exception e) {
                throw new SAXException(e);
            }
        }
        return document;
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public void startDocument() throws SAXException {
        try {
            document = xmlPlatform.createDocument();
            nodes.add(document);
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    public void endDocument() throws SAXException {
    	nodes.remove(nodes.size() - 1);
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if(null == prefix) {
            prefix = Constants.EMPTY_STRING;
        }
        if(null == uri) {
            uri = Constants.EMPTY_STRING;
        }
        if (namespaceDeclarations == null) {
            namespaceDeclarations = new HashMap();
        }
        namespaceDeclarations.put(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    	if (null != namespaceURI && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        Element element = getInitializedDocument().createElementNS(namespaceURI, qName);
        Node parentNode = nodes.get(nodes.size()-1);

        if ((stringBuffer.length() > 0) && !(nodes.size() == 1)) {
            Text text = getInitializedDocument().createTextNode(stringBuffer.toString());
            parentNode.appendChild(text);
            stringBuffer.reset();
        }
        appendChildNode(parentNode, element);
        nodes.add(element);

        if (namespaceDeclarations != null) {
        	
        	Iterator<Entry<String, String>> namespaceEntries = namespaceDeclarations.entrySet().iterator();
            String prefix;
            String uri;
            while (namespaceEntries.hasNext()) {
            	Entry<String, String> nextEntry = namespaceEntries.next();
            	prefix = nextEntry.getKey();
            	uri = nextEntry.getValue();

                boolean prefixEmpty = prefix.length() == 0;
                String elemNamespaceURI = element.getNamespaceURI();
                boolean elementNamespaceNull = elemNamespaceURI == null;
                boolean elementNamespaceEmpty = elemNamespaceURI != null && elemNamespaceURI.length() == 0;
                boolean isRootElement = element.getParentNode().getNodeType() == Node.DOCUMENT_NODE;

                if (prefixEmpty && isRootElement && (elementNamespaceEmpty || elementNamespaceNull)) {
                     // Don't add namespace
                } else {
                    addNamespaceDeclaration(element, prefix, uri);
                }
            }
            namespaceDeclarations = null;
        }

        int numberOfAttributes = atts.getLength();
        String attributeNamespaceURI;
        for (int x = 0; x < numberOfAttributes; x++) {
            attributeNamespaceURI = atts.getURI(x); 
            if (null != attributeNamespaceURI && attributeNamespaceURI.length() == 0) {
                attributeNamespaceURI = null;
            }
            
            // Handle case where prefix/uri are not set on an xmlns prefixed attribute            
            if (attributeNamespaceURI == null && (atts.getQName(x).startsWith(javax.xml.XMLConstants.XMLNS_ATTRIBUTE + ":") || atts.getQName(x).equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE))) {            	
                attributeNamespaceURI = javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
            }
            
            String value = atts.getValue(x);
            element.setAttributeNS(attributeNamespaceURI, atts.getQName(x), value);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    	Element endedElement = (Element)nodes.remove(nodes.size()-1);
        if (stringBuffer.length() > 0) {
            Text text = getInitializedDocument().createTextNode(stringBuffer.toString());
            endedElement.appendChild(text);
            stringBuffer.reset();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        stringBuffer.append(ch, start, length);
    }

    public void characters(CharSequence characters) {
        stringBuffer.append(characters.toString());
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
        ProcessingInstruction pi = getInitializedDocument().createProcessingInstruction(target, data);
        Node parentNode = nodes.get(nodes.size() -1);
        parentNode.appendChild(pi);
    }

    public void skippedEntity(String name) throws SAXException {
    }

    protected void addNamespaceDeclaration(Element parentElement, String prefix, String uri) {
        if (prefix.length() == 0 || javax.xml.XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
            //handle default/target namespaces
            parentElement.setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE, uri);
        } else {
            parentElement.setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON + prefix, uri);
        }
    }
    
    public void appendChildNode(Node parentNode, Node childNode) {
        parentNode.appendChild(childNode);
    }
    @Override
    public void setNil(boolean isNil) {}
}
