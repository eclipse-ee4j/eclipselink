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
package org.eclipse.persistence.internal.oxm.record;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.mappings.Login;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.namespaces.StackUnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

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
public class DOMReader extends XMLReaderAdapter {

    private Node currentNode;
    private DocumentPreservationPolicy docPresPolicy;

    public DOMReader() {
        super();
    }

    public DOMReader(Unmarshaller xmlUnmarshaller) {
        super(xmlUnmarshaller);
    }

    @Override
    public void parse(InputSource input) throws SAXException {
        if(input instanceof DOMInputSource) {
            Node node = ((DOMInputSource) input).getNode();
            if(contentHandler != null && contentHandler.getClass() == SAXUnmarshallerHandler.class){
                ((SAXUnmarshallerHandler)contentHandler).setUnmarshalNamespaceResolver(new StackUnmarshalNamespaceResolver());
            }
            parse(node);
        }
    }

    public void parse (Node node, String newURI, String newName) throws SAXException {
        if(null == contentHandler) {
            return;
        }
        Element rootNode = null;
        if(node.getNodeType() == Node.DOCUMENT_NODE) {
            rootNode = ((Document)node).getDocumentElement();
        }  else {
            rootNode = (Element)node;
        }
        processParentNamespaces(rootNode);
        startDocument();
        setupLocator(rootNode.getOwnerDocument());
              
        reportElementEvents(rootNode, newURI, newName);
        
        
        endDocument();
    }
    
    public void parse (Node node) throws SAXException {
        parse(node, null, null);
    }

    /**
     * Process namespace declarations on parent elements if not the root.
     * For each parent node from current to root push each onto a stack, 
     * then pop each off, calling startPrefixMapping for each XMLNS 
     * attribute.  Using a stack ensures that the parent nodes are 
     * processed top down.
     * 
     * @param element
     */
    protected void processParentNamespaces(Element element) throws SAXException {
        Node parent = element.getParentNode();
        // If we're already at the root, do nothing
        if (parent != null && parent.getNodeType() == Node.DOCUMENT_NODE) {
            return;
        }
        // Add each parent node up to root to the stack
        List<Node> parentElements = new ArrayList<Node>();
        while (parent != null && parent.getNodeType() != Node.DOCUMENT_NODE) {
            parentElements.add(parent);
            parent = parent.getParentNode();
        }        
        // Pop off each node and call startPrefixMapping for each XMLNS attribute
        for (Iterator stackIt = parentElements.iterator(); stackIt.hasNext(); ) {
            NamedNodeMap attrs = parentElements.remove(parentElements.size() - 1).getAttributes();
            if (attrs != null) {
                for (int i=0, length = attrs.getLength(); i < length; i++) {
                    Attr next = (Attr)attrs.item(i);
                    String attrPrefix = next.getPrefix();
                    if (attrPrefix != null && attrPrefix.equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)) {
                        contentHandler.startPrefixMapping(next.getLocalName(), next.getValue());
                    }
                }
            }
        }
    }
    protected void reportElementEvents(Element elem) throws SAXException {
        reportElementEvents(elem, null, null);
    }
    protected void reportElementEvents(Element elem, String newUri, String newName) throws SAXException {
        this.currentNode = elem;
        IndexedAttributeList attributes = buildAttributeList(elem);
        String namespaceUri = null;
        String qname = null;
        String lname = null;

        if(newName == null){
            // Handle null local name           
            lname = elem.getLocalName();
            if (lname == null) {
                // If local name is null, use the node name
                lname = elem.getNodeName();
                qname = lname;
                handlePrefixedAttribute(elem);
            } else {
                qname = getQName(elem);
            }
            namespaceUri = elem.getNamespaceURI();
            if(namespaceUri == null) {
                namespaceUri = "";
            }
        } else {
            namespaceUri = newUri;
            lname = newName;            
            qname = newName;
            if(namespaceUri != null && isNamespaceAware()){
                NamespaceResolver tmpNR = new NamespaceResolver();
                tmpNR.setDOM(elem);

                 String prefix = tmpNR.resolveNamespaceURI(namespaceUri);
                 if(prefix == null || prefix.length() == 0){
                     String defaultNamespace = elem.getAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE);
            
                     if(defaultNamespace == null){
                         prefix = tmpNR.generatePrefix();    
                         contentHandler.startPrefixMapping(prefix, namespaceUri);
                     }else if(defaultNamespace != namespaceUri){
                         prefix = tmpNR.generatePrefix();
                         contentHandler.startPrefixMapping(prefix, namespaceUri);
                     }else{
                         prefix = Constants.EMPTY_STRING;
                     }      
                 }                 
                 
                 if(prefix != null && prefix.length() >0){
                    qname = prefix + Constants.COLON + qname;      
                 }
            }
           
        }
        
      
        
        contentHandler.startElement(namespaceUri, lname, qname, attributes);
       
        handleChildNodes(elem.getChildNodes());
        contentHandler.endElement(namespaceUri, lname, qname);
        endPrefixMappings(elem);
    }
 
    protected IndexedAttributeList buildAttributeList(Element elem) throws SAXException {
        IndexedAttributeList attributes = new IndexedAttributeList();
        NamedNodeMap attrs = elem.getAttributes();
        for (int i = 0, length = attrs.getLength(); i < length; i++) {
            Attr next = (Attr)attrs.item(i);
            String attrPrefix = next.getPrefix();
            if(attrPrefix != null && attrPrefix.equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)) {
                contentHandler.startPrefixMapping(next.getLocalName(), next.getValue());
                // Handle XMLNS prefixed attributes
                handleXMLNSPrefixedAttribute(elem, next);
            } else if(attrPrefix == null) {
                String name = next.getLocalName();
                if(name == null) {
                    name = next.getNodeName();
                }
                if(name != null && name.equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)) {
                    contentHandler.startPrefixMapping(Constants.EMPTY_STRING, next.getValue());
                }
            }
            if(next.getNamespaceURI() != null && next.getNamespaceURI().equals(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI) && next.getLocalName().equals("type")) {
                handleXsiTypeAttribute(next);
            }
            attributes.addAttribute(next);
        }
        return attributes;
    }

    protected void endPrefixMappings(Element elem) throws SAXException {
        NamedNodeMap attrs = elem.getAttributes();
        for(int i = 0, numOfAtts = attrs.getLength(); i < numOfAtts; i++) {
            Attr next = (Attr)attrs.item(i);
            String attrPrefix = next.getPrefix();
            if (attrPrefix != null && attrPrefix.equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)) {
                contentHandler.endPrefixMapping(next.getLocalName());
            } else if(attrPrefix == null) {
                String name = next.getLocalName();
                if(name == null) {
                    name = next.getNodeName();
                }
                if(javax.xml.XMLConstants.XMLNS_ATTRIBUTE.equals(name)) {
                    contentHandler.endPrefixMapping(Constants.EMPTY_STRING);
                }
            }
        }
    }

    protected String getQName(Element elem) throws SAXException {
        handlePrefixedAttribute(elem);
        String prefix = elem.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            String qname = prefix + Constants.COLON + elem.getLocalName();
            return qname;
        } else {
            return elem.getLocalName();
        }
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
    
    protected void handleXsiTypeAttribute(Attr attr) throws SAXException {
        
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
        Node nextChild = null;
        if(children.getLength() > 0) {
            nextChild = children.item(0);
        }
        while(nextChild != null) {
            if(nextChild.getNodeType() == Node.TEXT_NODE) {
                char[] value = ((Text)nextChild).getNodeValue().toCharArray();
                contentHandler.characters(value, 0, value.length);
            } else if(nextChild.getNodeType() == Node.COMMENT_NODE) {
                char[] value = ((Comment)nextChild).getNodeValue().toCharArray();
                if (lexicalHandler != null) {
                    lexicalHandler.comment(value, 0, value.length);
                }
            } else if(nextChild.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element)nextChild;
                reportElementEvents(childElement);
            } else if(nextChild.getNodeType() == Node.CDATA_SECTION_NODE) {
                if(lexicalHandler != null) {
                    lexicalHandler.startCDATA();
                }
                char[] value = ((CDATASection)nextChild).getData().toCharArray();
                contentHandler.characters(value, 0, value.length);
                if(lexicalHandler != null) {
                    lexicalHandler.endCDATA();
                }
            }
            nextChild = nextChild.getNextSibling();
        }
    }

    /**
     * Trigger an endDocument event on the contenthandler.
     */
    protected void endDocument() throws SAXException {
        contentHandler.endDocument();
    }

    /**
     * Trigger a startDocument event on the contenthandler.
     */
    protected void startDocument() throws SAXException {
        contentHandler.startDocument();
    }

    /**
     * An EclipseLink specific callback into the Reader. This allows Objects to be 
     * associated with the XML Nodes they came from.
     */
    @Override
    public void newObjectEvent(Object object, Object parent, Mapping selfRecordMapping) {
        docPresPolicy.addObjectToCache(object, currentNode, selfRecordMapping);
    }

    @Override
    public Object getCurrentObject(CoreAbstractSession session, Mapping selfRecordMapping) {
        //if session == null then this is a marshal of a non-root
        //if docPres policy is null, then we never unmarshalled anything, and can
        //safely return null;
        if(session == null && docPresPolicy == null) {
            return null;
        }
        if(docPresPolicy == null) {
            Login login = (Login)session.getDatasourceLogin();
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

    protected void setupLocator(Document doc) {
        LocatorImpl locator = new LocatorImpl();
        try {
            Method getEncoding = PrivilegedAccessHelper.getMethod(doc.getClass(), "getXmlEncoding", new Class[]{}, true);
            Method getVersion = PrivilegedAccessHelper.getMethod(doc.getClass(), "getXmlVersion", new Class[]{}, true);
            
            String encoding = (String)PrivilegedAccessHelper.invokeMethod(getEncoding, doc, new Object[]{});
            String version = (String)PrivilegedAccessHelper.invokeMethod(getVersion, doc, new Object[]{});
            
            locator.setEncoding(encoding);
            locator.setXMLVersion(version);
        } catch(Exception ex) {
            //if unable to invoke these methods, just return and don't invoke
            return;
        }
        this.contentHandler.setDocumentLocator(locator);
    }

    /**
     * Implementation of Attributes - used to pass along a given node's attributes
     * to the startElement method of the reader's content handler.
     */
    protected class IndexedAttributeList implements org.xml.sax.Attributes {

        private List<Attr> attrs;

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
                return Constants.EMPTY_STRING;
            } catch (IndexOutOfBoundsException iobe) {
                return null;
            }
        }

        public String getType(String namespaceUri, String localName) {
            return Constants.CDATA;
        }

        public String getType(int index) {
            return Constants.CDATA;
        }

        public String getType(String qname) {
            return Constants.CDATA;
        }

        public int getIndex(String qname) {
            for (int i=0, size = attrs.size(); i<size; i++) {
                if (attrs.get(i).getName().equals(qname)) {
                    return i;
                }
            }
            return -1;
        }

        public int getIndex(String uri, String localName) {
            for (int i=0, size = attrs.size(); i<size; i++) {
                Attr item = attrs.get(i);
                try {
                    if (item.getNamespaceURI().equals(uri) && item.getLocalName().equals(localName)) {
                        return i;
                    }
                } catch (Exception x) {}
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
                return item.getName();
            } catch (IndexOutOfBoundsException iobe) {
                return null;
            }
        }

        public String getURI(int index) {
            String uri = attrs.get(index).getNamespaceURI();
            if(uri == null) {
                uri = Constants.EMPTY_STRING;
            }
            return uri;
        }

        public String getValue(int index) {
            return (attrs.get(index)).getValue();
        }

        public String getValue(String qname) {
            for (int i=0, size = attrs.size(); i<size; i++) {
                Attr item = attrs.get(i);
                if (item.getName().equals(qname)) {
                    return item.getValue();
                }
            }
            return null;
        }

        public String getValue(String uri, String localName) {
            for (int i=0, size = attrs.size(); i<size; i++) {
                Attr item = attrs.get(i);
                if (item != null) {
                    String itemNS = item.getNamespaceURI();  
                    // Need to handle null/empty URI
                    if (item.getNamespaceURI() == null) {
                        itemNS = Constants.EMPTY_STRING;
                    }
                    
                    String itemName = item.getLocalName();
                    if(itemName == null){
                    	itemName = item.getNodeName();
                    }
                    if ((itemNS.equals(uri)) && (itemName != null && itemName.equals(localName))) {
                 	   return item.getValue();   
                    }                   
                }
            }
            return null;
        }
    }

    protected class LocatorImpl implements Locator2 {

        private String encoding;
        private String version;

        public LocatorImpl() {
            encoding = "UTF-8";
            version = "1.0";
        }

        public String getEncoding() {
            return encoding;
        }

        public int getColumnNumber() {
            //not supported here
            return 0;
        }

        public String getSystemId() {
            return Constants.EMPTY_STRING;
        }

        public String getPublicId() {
            return Constants.EMPTY_STRING;
        }

        public String getXMLVersion() {
            return version;
        }

        public int getLineNumber() {
            //not supported
            return 0;
        }

        protected void setEncoding(String enc) {
            this.encoding = enc;
        }

        protected void setXMLVersion(String xmlVersion) {
            this.version = xmlVersion;
        }

    }

}