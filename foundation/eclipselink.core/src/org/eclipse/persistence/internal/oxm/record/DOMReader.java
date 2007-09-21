/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import java.util.ArrayList;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.mappings.XMLMapping;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;

/**
 *  INTERNAL:
 *  <p><b>Purpose:</b> An implementation of XMLReader for parsing DOM Nodes into SAX events.
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Walk the DOM tree and report sax events to the provided content handler</li>
 *  <li>Report lexical events to the lexical handler if it's provided</li>
 *  <li>Listen for callbacks from the Mapping-Level framework to handle caching nodes for document preservation</li>
 *  </ul>
 *  
 */
public class DOMReader extends XMLReader {
    ContentHandler contentHandler;
    LexicalHandler lexicalHandler;
    private Node currentNode;
    private DocumentPreservationPolicy docPresPolicy;
    
    public boolean getFeature (String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return false;
    }
    
    public void setFeature (String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {}
    
    public Object getProperty (String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return null;
    }

    public void setProperty (String name, Object value)	throws SAXNotRecognizedException, SAXNotSupportedException {
        if(name.equals("http://xml.org/sax/properties/lexical-handler")) {
            lexicalHandler = (LexicalHandler)value;
        }
    }

    public void setEntityResolver (EntityResolver resolver) {}

    public EntityResolver getEntityResolver () {
        return null;
    }

    public void setDTDHandler (DTDHandler handler) {}

    public DTDHandler getDTDHandler () {
        return null;
    }

    public void setContentHandler (ContentHandler handler) {
        this.contentHandler = handler;
    }

    public ContentHandler getContentHandler () {
        return this.contentHandler;
    }

    public void setErrorHandler (ErrorHandler handler) {}

    public ErrorHandler getErrorHandler () {
        return null;
    }

    public void parse(InputSource input) {}
    public void parse(String systemId) {}
 
    public void parse (Node node) throws SAXException {
        if(getContentHandler() == null) {
            return;
        }
        Element rootNode = null;
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            rootNode = ((Document)node).getDocumentElement();
        } else {
            rootNode = (Element)node;
        }
        startDocument();
        reportElementEvents(rootNode);
        endDocument();
    }
    
    protected void reportElementEvents(Element elem) throws SAXException {
        this.currentNode = elem;
        IndexedAttributeList attributes = buildAttributeList(elem);
        // Handle null local name
        String qname;
        String lname = elem.getLocalName();
        if (lname == null) {
            // If local name is null, use the node name
            lname = elem.getNodeName();
            qname = lname;
        } else {
            qname = getQName(elem);
        }
        getContentHandler().startElement(elem.getNamespaceURI(), lname, qname, attributes);
        handleChildNodes(elem.getChildNodes());
        endPrefixMappings(elem);
        getContentHandler().endElement(elem.getNamespaceURI(), lname, qname);
    }
 
    protected IndexedAttributeList buildAttributeList(Element elem) throws SAXException {
        IndexedAttributeList attributes = new IndexedAttributeList();
        NamedNodeMap attrs = elem.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr next = (Attr)attrs.item(i);
            String attrPrefix = next.getPrefix();
            if(attrPrefix != null && attrPrefix.equals(XMLConstants.XMLNS)) {
                getContentHandler().startPrefixMapping(next.getLocalName(), next.getValue());
                // Handle XMLNS prefixed attributes
                handleXMLNSPrefixedAttribute(elem, next);
            }
            attributes.addAttribute(next);
        }
        return attributes;
    }
    
    protected void endPrefixMappings(Element elem) throws SAXException {
        NamedNodeMap attrs = elem.getAttributes();
        for(int i = 0; i < attrs.getLength(); i++) {
            Attr next = (Attr)attrs.item(i);
            String attrPrefix = next.getPrefix();
            if (attrPrefix != null && attrPrefix.equals(XMLConstants.XMLNS)) {
                getContentHandler().endPrefixMapping(next.getLocalName());
            }
        }
    }
    
    protected String getQName(Element elem) throws SAXException {
        if (elem.getLocalName() == null) {
            return elem.getNodeName();
        }
        String qname = "";
        String prefix = elem.getPrefix();
        if (prefix != null && !(prefix.equals(""))) {
            qname = prefix + ":" + elem.getLocalName();
            handlePrefixedAttribute(elem);
        } else {
            qname = elem.getLocalName();
        }
        return qname;
    }
    
    /**
     * Handle XMLNS prefixed attribute.
     * 
     * @param prefix
     * @param localName
     * @param value
     */
    protected void handleXMLNSPrefixedAttribute(Element elem, Attr attr) {
        // DO NOTHING
    }

    /**
     * Handle prefixed attribute - may need to declare the namespace 
     * URI locally.
     * 
     */
    protected void handlePrefixedAttribute(Element elem) throws SAXException {
        // DO NOTHING
    }

    protected void handleChildNodes(NodeList children) throws SAXException {
        for(int i = 0; i < children.getLength(); i++) {
            Node next = children.item(i);
            if(next.getNodeType() == Node.TEXT_NODE) {
                char[] value = ((Text)next).getNodeValue().toCharArray();
                getContentHandler().characters(value, 0, value.length);
            } else if(next.getNodeType() == Node.COMMENT_NODE) {
                char[] value = ((Comment)next).getNodeValue().toCharArray();
                if (lexicalHandler != null) {
                	lexicalHandler.comment(value, 0, value.length);
                }
            } else if(next.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element)next;
                reportElementEvents(childElement);
            } else if(next.getNodeType() == Node.CDATA_SECTION_NODE) {
                if(lexicalHandler != null) {
                    lexicalHandler.startCDATA();
                }
                char[] value = ((CDATASection)next).getData().toCharArray();
                getContentHandler().characters(value, 0, value.length);
                if(lexicalHandler != null) {
                    lexicalHandler.endCDATA();
                }
            }
        }
    }
    /**
     * Trigger an endDocument event on the contenthandler.
     */
    protected void endDocument() throws SAXException {
        getContentHandler().endDocument();
    }

    /**
     * Trigger a startDocument event on the contenthandler.
     */
    protected void startDocument() throws SAXException {
        getContentHandler().startDocument();
    }
    /**
     * A toplink specific callback into the Reader. This allows Objects to be 
     * associated with the XML Nodes they came from.
     */
    public void newObjectEvent(Object object, Object parent, XMLMapping selfRecordMapping) {
        docPresPolicy.addObjectToCache(object, currentNode, selfRecordMapping);
    }

    public Object getCurrentObject(AbstractSession session, XMLMapping selfRecordMapping) {
        //if session == null then this is a marshal of a non-root
        //if docPres policy is null, then we never unmarshalled anything, and can
        //safely return null;
        if(session == null && docPresPolicy == null) {
            return null;
        }
        if(docPresPolicy == null) {
            XMLLogin login = (XMLLogin)session.getDatasourceLogin();
            docPresPolicy = login.getDocumentPreservationPolicy();
        }
        return docPresPolicy.getObjectForNode(currentNode, selfRecordMapping);
    }

    public DocumentPreservationPolicy getDocPresPolicy() {
        return docPresPolicy;
    }
    
    public void setDocPresPolicy(DocumentPreservationPolicy policy) {
        docPresPolicy = policy;
    }
    
    /**
     * Implementation of Attributes - used to pass along a given node's attributes
     * to the startElement method of the reader's content handler.
     */
    protected class IndexedAttributeList implements org.xml.sax.Attributes {
        private ArrayList<Attr> attrs;
        
        public IndexedAttributeList() {
            attrs = new ArrayList();
        }
        
        public void addAttribute(Attr attribute) {
            attrs.add(attribute);
        }
        
        public String getQName(int index) {
            try {
                Attr item = attrs.get(index);
                if (item.getName() != null) {
                    return item.getName();
                }
                return "";
            } catch (IndexOutOfBoundsException iobe) {
                return null;
            }
        }
        
        public String getType(String namespaceUri, String localName) {
            return "CDATA";
        }
        
        public String getType(int index) {
            return "CDATA";
        }
        
        public String getType(String qname) {
            return "CDATA";
        }
        
        public int getIndex(String qname) {
            Attr item;
            for (int i=0; i<attrs.size(); i++) {
                item = attrs.get(i);
                if (item.getName().equals(qname)) {
                    return i;
                }
            }
            return -1;
        }
        
        public int getIndex(String uri, String localName) {
            Attr item;
            for (int i=0; i<attrs.size(); i++) {
                item = attrs.get(i);
                if (item.getNamespaceURI().equals(uri) && item.getLocalName().equals(localName)) {
                    return i;
                }
            }
            return -1;
        }
        
        public int getLength() {
            return attrs.size();
        }
        
        public String getLocalName(int index) {
            try {
                Attr item = attrs.get(index);
                if (item.getLocalName() != null) {
                    return item.getLocalName();
                }
                return "";
            } catch (IndexOutOfBoundsException iobe) {
                return null;
            }
        }        

        public String getURI(int index) {
            return attrs.get(index).getNamespaceURI();
        }
        
        public String getValue(int index) {
            return ((Attr)attrs.get(index)).getValue();
        }
        
        public String getValue(String qname) {
            Attr item;
            for (int i=0; i<attrs.size(); i++) {
                item = attrs.get(i);
                if (item.getName().equals(qname)) {
                    return item.getValue();
                }
            }
            return null;
        }

        public String getValue(String uri, String localName) {
            Attr item;
            for (int i=0; i<attrs.size(); i++) {
                item = attrs.get(i);
                if (item != null) {
                    String itemNS = item.getNamespaceURI();  
                    // Need to handle null/empty URI
                    if (item.getNamespaceURI() == null) {
                        itemNS = "";
                    }
                    if ((itemNS.equals(uri)) && (item.getLocalName() != null && item.getLocalName().equals(localName))) {
                        return item.getValue();
                    }
                }
            }
            return null;
        }
    }
}
