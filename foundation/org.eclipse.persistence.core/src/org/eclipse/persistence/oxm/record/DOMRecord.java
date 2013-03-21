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
package org.eclipse.persistence.oxm.record;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.ReferenceResolver;
import org.eclipse.persistence.internal.oxm.UnmarshalXPathEngine;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.IDResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * PUBLIC:
 * Provides a Record/Map API on an XML DOM element.
 */
public class DOMRecord extends XMLRecord {
    private Node dom;
    private Node currentNode;
    private XMLField lastUpdatedField;
    private ReferenceResolver referenceResolver;

    /**
     * INTERNAL:
     * Default constructor.
     */
    public DOMRecord() {
        super();
        setNamespaceResolver(new NamespaceResolver());
        referenceResolver = new ReferenceResolver();
        // Required for subclasses.
    }

    /**
     * INTERNAL:
     * Create a record with the root element name.
     */
    public DOMRecord(String rootElementName) {
        this(rootElementName, (NamespaceResolver)null);
    }

    /**
     * INTERNAL:
     * Create a record with the root element name get the namespace URI from the namespaceResolver.
     */
    public DOMRecord(String rootElementName, NamespaceResolver namespaceResolver) {
        this();
        String rootElementNamespaceURI = resolveNamespace(namespaceResolver, rootElementName);
        setDOM(createNewDocument(rootElementName, rootElementNamespaceURI));
    }

     /**
     * INTERNAL:
     * Create a record with the root element name get the namespace URI from the namespaceResolver.
     */
    public DOMRecord(String rootElementName, String rootElementNamespaceURI) {
        this();
        setDOM(createNewDocument(rootElementName, rootElementNamespaceURI));
    }

    /**
     * INTERNAL:
     * Create a record with the local root element name, that is a child of the parent.
     */
    public DOMRecord(String localRootElementName, Node parent) {
        this(localRootElementName, (NamespaceResolver)null, parent);
    }

    /**
     * INTERNAL:
     * Create a record with the local root element name, that is a child of the parent.
     * Lookup the namespace URI from the namespaceResolver.
     */
    public DOMRecord(String localRootElementName, NamespaceResolver namespaceResolver, Node parent) {
        this();
        Document document;
        if (parent instanceof Document) {
            document = (Document)parent;
        } else {
            document = parent.getOwnerDocument();
        }

        String localRootElementNamespaceURI = resolveNamespace(namespaceResolver, localRootElementName);
        Element child = document.createElementNS(localRootElementNamespaceURI, localRootElementName);
        parent.appendChild(child);
        setDOM(child);
    }

    /**
     * INTERNAL:
     * Create a record with the element.
     */
    public DOMRecord(Element element) {
        this();
        setDOM(element);
    }

    public DOMRecord(Node node) {
        this();
        setDOM(node);
    }

    /**
     * INTERNAL:
     * Create a record with the element.
     */
    public DOMRecord(Document document) {
        this();
        setDOM(document.getDocumentElement());
    }

    /**
     * PUBLIC:
     * Get the local name of the context root element.
     */
    public String getLocalName() {
        String localName = getDOM().getLocalName();
        if (null != localName) {
            return localName;
        }
        return getDOM().getNodeName();
    }

    /**
     * PUBLIC:
     *  Get the namespace URI for the context root element.
     */
    public String getNamespaceURI() {
        return getDOM().getNamespaceURI();
    }

    /**
     * INTERNAL:
     * The ReferenceResolver that is leveraged by key based mappings.
     * @since EclipseLink 2.5.0
     */
    public ReferenceResolver getReferenceResolver() {
        if(null == referenceResolver) {
            referenceResolver = new ReferenceResolver();
        }
    	return referenceResolver;
    }

    /**
     * INTERNAL:
     * Set the ReferenceResolver that will be leveraged by key based mappings.
     * @since EclipseLink 2.5.0
     */
    public void setReferenceResolver(ReferenceResolver referenceResolver) {
    	this.referenceResolver = referenceResolver;
    }

    /**
     * INTERNAL:
     * Add the field-value pair to the document.
     */
    public void add(DatabaseField key, Object value) {
        // Value may be a direct value, nested record, or collection of values.
        Object nodeValue = convertToNodeValue(value);
        XPathEngine.getInstance().create(convertToXMLField(key), dom, nodeValue, session);
    }

    /**
     * INTERNAL:
     * Convert the value which may be a direct value, nested record, or set of nested records,
     * to a node value usable with the XPathEngine.
     */
    private Object convertToNodeValue(Object value) {
        if (value instanceof List) {
            List values = (List)value;
            Vector nodeValues = new Vector(values.size());
            for (int index = 0; index < values.size(); index++) {
                Object nestedValue = values.get(index);
                nodeValues.add(convertToNodeValue(nestedValue));
            }
            return nodeValues;
        } else if (value instanceof DOMRecord) {
            return ((DOMRecord)value).getDOM();
        } else if (value != null && value.getClass() == XMLEntry.class) {
            XMLEntry entry = (XMLEntry)value;
            entry.setValue(convertToNodeValue(entry.getValue()));
            return entry;
        } else {
            return value;
        }
    }

    /**
     * PUBLIC:
     * Clear the sub-nodes of the DOM.
     */
    public void clear() {
        if(getDOM() instanceof Element) {
            String domName = ((Element)getDOM()).getTagName();
            this.dom = createNewDocument(domName, null);
            this.currentNode = this.dom;
        }
    }

    /**
     * INTERNAL:
     * Clone the row and its values.
     */
    public DOMRecord clone() {
        DOMRecord clone = (DOMRecord)super.clone();
        if (clone != null) {
            clone.setDOM((Element)dom.cloneNode(true));
        }
        return clone;
    }

    /**
     * INTERNAL:
     * Creates a new Document and returns the root element of that document
     */
    public Node createNewDocument(String defaultRootElementName) {
        return createNewDocument(defaultRootElementName, null);
    }

    /**
     * INTERNAL:
     * Creates a new Document and returns the root element of that document
     */
    public Node createNewDocument(String defaultRootElementName, String namespaceURI) {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        Document document = xmlPlatform.createDocument();

        if (defaultRootElementName == null || defaultRootElementName.length() == 0) {
            DocumentFragment fragment = document.createDocumentFragment();
            return fragment;
        } else {
            Node rootNode = document.createElementNS(namespaceURI, defaultRootElementName);
            document.appendChild(rootNode);
            return document.getDocumentElement();
        }
    }

    /**
     * PUBLIC:
     * Return the document.
     */
    public Document getDocument() {
        return getDOM().getOwnerDocument();
    }

    /**
     * INTERNAL:
     * Check if the field is contained in the row.
     */
    public boolean containsKey(DatabaseField key) {
        XMLField xmlField = convertToXMLField(key);
        NodeList nodeList = UnmarshalXPathEngine.getInstance().selectNodes(dom, xmlField, xmlField.getNamespaceResolver());
        return nodeList.getLength() > 0;
    }

    /**
     * PUBLIC:
     * Check if the value is contained in the row.
     */
    public boolean contains(Object value) {
        return values().contains(value);
    }

    public Object get(DatabaseField key) {
        Object value = getIndicatingNoEntry(key);
        if(value == noEntry) {
            return null;
        }
        return value;
    }
    /**
     * INTERNAL:
     * Given a DatabaseField return the corresponding value from the document
     */
    public Object getIndicatingNoEntry(DatabaseField key) {
        return getIndicatingNoEntry(key, false, false);
    }

    public Object getIndicatingNoEntry(DatabaseField key, boolean shouldReturnNode) {
        return getIndicatingNoEntry(key, shouldReturnNode, false);
    }

    public Object getIndicatingNoEntry(DatabaseField key, boolean shouldReturnNode, boolean checkForXsiNil) {
        XMLField field = convertToXMLField(key);

        // handle 'self' xpath
        if (field.isSelfField()) {
            return this;
        }

        Object result = UnmarshalXPathEngine.getInstance().selectSingleNode(dom, field, field.getNamespaceResolver(), checkForXsiNil);

        if(result == noEntry) {
            if(shouldReturnNode) {
                return null;
            }
            return noEntry;
        }

        if (result == NIL) {
            return NIL;
        }

        Node node = (Node)result;
        if(shouldReturnNode) {
            return node;
        }
        // If a node was not found return null
        if (null == node) {
            return null;
        }

        // For Attributes and Text nodes return their value
        if (Node.ELEMENT_NODE != node.getNodeType()) {
            if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                getValueFromAttribute((Attr)node, field);
            }
            // For Text, must handle typed elements
            return getValueFromElement((Element)node.getParentNode(), node, field);
        }

        // If an element was found
        return buildNestedRow((Element)node);
    }

    /**
     * INTERNAL:
     * Retrieve the value for the field name.
     */
    public Object getValues(String key) {
        Object value = getValuesIndicatingNoEntry(new XMLField(key));

        if (value == AbstractRecord.noEntry) {
            return null;
        }

        return value;
    }

    /**
     * INTERNAL:
     * Given a DatabaseField, return the corresponding values from the document
     */
    public Object getValues(DatabaseField key) {
        return this.getValues(key, null);
    }

    public Object getValues(DatabaseField key, AbstractNullPolicy nullPolicy) {
        Object value = getValuesIndicatingNoEntry(key, nullPolicy);

        if (value == AbstractRecord.noEntry) {
            return null;
        }

        return value;
    }

    public Object getValuesIndicatingNoEntry(DatabaseField key) {
        return this.getValuesIndicatingNoEntry(key, null);
    }

    public Object getValuesIndicatingNoEntry(DatabaseField key, AbstractNullPolicy nullPolicy) {
        return getValuesIndicatingNoEntry(key, false, nullPolicy);
    }

    public List<XMLEntry> getValuesIndicatingNoEntry(List<DatabaseField> keys) {
        return getValuesIndicatingNoEntry(keys, false);
    }

    public List<XMLEntry> getValuesIndicatingNoEntry(List<DatabaseField> keys, boolean shouldReturnNodes) {
        List<XMLField> xmlFields = convertToXMLField(keys);
        List<XMLEntry> values = UnmarshalXPathEngine.getInstance().selectNodes(dom, xmlFields, xmlFields.get(0).getNamespaceResolver());
        if(shouldReturnNodes) {
            return values;
        }
        for(XMLEntry next:values) {
            Node nextNode = (Node)next.getValue();
            if(!(nextNode.getNodeType() == Node.ELEMENT_NODE)) {
                Object value = getValueFromElement((Element)nextNode.getParentNode(), nextNode, next.getXMLField());
                next.setValue(value);
            } else {
                next.setValue(buildNestedRow((Element)nextNode));
            }
        }
        return values;
    }

    /**
     * INTERNAL:
     * Given a DatabaseField, return the corresponding values from the document
     */
    public Object getValuesIndicatingNoEntry(DatabaseField key, boolean shouldReturnNodes) {
        return this.getValuesIndicatingNoEntry(key, shouldReturnNodes, null);
    }

    public Object getValuesIndicatingNoEntry(DatabaseField key, boolean shouldReturnNodes, AbstractNullPolicy nullPolicy) {
        XMLField field = convertToXMLField(key);
        NodeList nodeList = UnmarshalXPathEngine.getInstance().selectNodes(dom, field, field.getNamespaceResolver(), nullPolicy);

        // If a node was not found return null
        if (null == nodeList) {
            return null;
        }
        int resultSize = nodeList.getLength();
        Vector result = new Vector(resultSize);
        if (resultSize == 0) {
            return result;
        }
        if(shouldReturnNodes) {
            //just copy all the nodes into the result vector and return it
            for(int i = 0; i < resultSize; i++) {
                result.add(nodeList.item(i));
            }
            return result;
        }
        // Assumption:  NodeList contains nodes of the same type
        Node firstNode = nodeList.item(0);
        if ((firstNode == null) || (firstNode.getNodeType() != Node.ELEMENT_NODE)) {
            if (field.usesSingleNode() && (resultSize == 1)) {
                Node next = nodeList.item(0);
                if (next == null) {
                    result.add(null);
                } else {
                    Vector list = new Vector();
                    String sourceObject = next.getNodeValue();

                    StringTokenizer tokenizer = new StringTokenizer(sourceObject, " ");
                    while (tokenizer.hasMoreElements()) {
                        String token = tokenizer.nextToken();
                        Object nextItem = convertValue((Element)next.getParentNode(), field, token);
                        list.add(nextItem);
                    }
                    return list;
                }
            }
            for (int x = 0; x < resultSize; x++) {
                Node next = nodeList.item(x);
                if (next == null) {
                    result.add(null);
                } else {
                    result.add(getValueFromElement((Element)next.getParentNode(), next, field));
                }
            }
        } else {
            for (int x = 0; x < resultSize; x++) {
                result.add(buildNestedRow((Element)nodeList.item(x)));
            }
        }

        return result;
    }

    private Object getValueFromAttribute(Attr node, XMLField key) {
    	currentNode = node.getOwnerElement();
    	Object convertedValue = key.convertValueBasedOnSchemaType(node.getNodeValue(), (XMLConversionManager) session.getDatasourcePlatform().getConversionManager(), this);
    	currentNode = getDOM();
    	return convertedValue;

    }

    private Object getValueFromElement(Element node, Node textChild, Field key) {
        Object value = textChild.getNodeValue();
        return convertValue(node, key, value);
    }

    private Object convertValue(Element node, Field key, Object value) {
        XMLConversionManager xmlCnvMgr = (XMLConversionManager) session.getDatasourcePlatform().getConversionManager();
        if (key.isTypedTextField() && (node != null)) {
            String schemaType = node.getAttributeNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
            if ((null != schemaType) && (schemaType.length() > 0)) {
                QName qname = null;
                int index = schemaType.indexOf(XMLConstants.COLON);
                if (index == -1) {
                    qname = new QName(schemaType);
                    Class convertClass = key.getJavaClass(qname);
                    return xmlCnvMgr.convertObject(value, convertClass);
                } else {
                    String prefix = schemaType.substring(0, index);
                    String localPart = schemaType.substring(index + 1);
                    XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
                    String url = xmlPlatform.resolveNamespacePrefix(node, prefix);
                    qname = new QName(url, localPart);
                    Class convertClass = key.getJavaClass(qname);
                    return xmlCnvMgr.convertObject(value, convertClass, qname);
                }
            }
        }
        currentNode = node;
        Object convertedValue = key.convertValueBasedOnSchemaType(value, xmlCnvMgr, this);
        currentNode = getDOM();
        return convertedValue;
    }

    /**
     * INTERNAL:
     * Build the nested record, this can be overwriten by subclasses to use their subclass instance.
     */
    public XMLRecord buildNestedRow(Element element) {
        DOMRecord record = new DOMRecord(element);
        record.setUnmarshaller(this.getUnmarshaller());
        record.setOwningObject(this.getCurrentObject());
        record.setDocPresPolicy(this.getDocPresPolicy());
        record.setReferenceResolver(referenceResolver);
        return record;
    }

    /**
    * PUBLIC:
    * Return the DOM.
    */
    public Node getDOM() {
        return dom;
    }

    /**
    * INTERNAL:
    * Set the field value into the DOM.
    * The field name must be a valid simple XPath expression.
    */
    public Object put(DatabaseField key, Object value) {
        // Value may be a direct value, nested record, or collection of values.
        XMLField field = convertToXMLField(key);
        Object nodeValue = convertToNodeValue(value);
        NodeList replaced = null;
        boolean isEmptyCollection = false;
        if (nodeValue instanceof Collection) {
            isEmptyCollection = ((Collection)nodeValue).size() == 0;
            replaced = XPathEngine.getInstance().replaceCollection(convertToXMLField(key), dom, (Collection)nodeValue, session);
        } else {
            replaced = XPathEngine.getInstance().replaceValue(convertToXMLField(key), dom, nodeValue, session);
        }
        if (replaced.getLength() == 0) {
            // Replace does nothing if the node did not exist, return no nodes.
            XPathEngine.getInstance().create(convertToXMLField(key), dom, nodeValue, lastUpdatedField, getDocPresPolicy(), session);
        } else if (replaced.item(0) == getDOM()) {
            // If the root element/record element was changed must update the record's reference.
            setDOM(getDocument().getDocumentElement());
        }
        if(!field.getXPathFragment().isAttribute() && !field.getXPathFragment().nameIsText()) {
            if(value != null && !isEmptyCollection) {
                this.lastUpdatedField = field;
            }
        }
        return replaced;
    }

    public Object put(List<XMLField> xmlFields, List<XMLEntry> values) {
        Vector valuesToWrite = (Vector)convertToNodeValue(values);
        List<XMLEntry> replaced = null;

        replaced = XPathEngine.getInstance().replaceCollection(xmlFields, valuesToWrite, dom, getDocPresPolicy(), lastUpdatedField, session);
        if(replaced.size() == 0) {
            XPathEngine.getInstance().create(xmlFields, dom, valuesToWrite, lastUpdatedField, getDocPresPolicy(), session);
        }
        return replaced;
    }

    public Object put(Object key, Object value) throws ValidationException {
        if (key instanceof String) {
            return put((String)key, value);
        } else if (key instanceof DatabaseField) {
            return put((DatabaseField)key, value);
        } else if (key instanceof List) {
            return put((List<XMLField>)key, (List<XMLEntry>)value);
        } else {
            throw ValidationException.onlyFieldsAreValidKeysForDatabaseRows();
        }
    }

    /**
     * INTERNAL:
     * Remove the field key from the row.
     */
    public Object remove(DatabaseField key) {
        return XPathEngine.getInstance().remove(convertToXMLField(key), dom, true);
    }

    /**
     * INTERNAL:
     * replaces the value at index with value
     */
    public void replaceAt(Object value, int index) {
        throw XMLMarshalException.operationNotSupported("replaceAt(Object value, int index)");
    }

    /**
     * PUBLIC:
     */
    public Set entrySet() {
        int size = this.size();
        Map tempMap = new HashMap(size);
        Vector fields = getFields();
        Vector values = getValues();
        for (int i = 0; i < size; i++) {
            tempMap.put(fields.elementAt(i), values.elementAt(i));
        }
        return tempMap.entrySet();
    }

    /**
     * INTERNAL:
     * Setting fields vector will not update the document so this is not supported
     */
    protected void setFields(Vector fields) throws XMLMarshalException {
        throw XMLMarshalException.operationNotSupported("setField(Vector fields)");
    }

    /**
     * INTERNAL:
     * This should not be used, but added some support for it as
     * is called from some places such as sdk call used in the descriptor to define operation not supported,
     * may also be called from toplin in some places.
     */
    public Vector getFields() {
        int length = getDOM().getChildNodes().getLength();
        Node nextNode = null;
        if(length > 0) {
            nextNode = getDOM().getChildNodes().item(0);
        }
        Vector fields = new Vector(length);
        while(nextNode != null) {
            fields.add(new DatabaseField(nextNode.getNodeName()));
            nextNode = nextNode.getNextSibling();
        }
        return fields;
    }

    /**
     * INTERNAL:
     * This should not be used, but added some support for it as
     * is called from some places such as sdk call used in the descriptor to define operation not supported,
     * may also be called from TopLink in some places.
     */
    public Vector getValues() {
        int length = getDOM().getChildNodes().getLength();
        Node nextNode = null;
        if(length > 0) {
            nextNode = getDOM().getFirstChild();
        }
        Vector values = new Vector(length);
        while(nextNode != null) {
            values.add(nextNode);
            nextNode = nextNode.getNextSibling();
        }
        return values;
    }

    /**
     * INTERNAL:
     * Setting values vector will not update the document so this is not supported
     */
    protected void setValues(Vector values) throws XMLMarshalException {
        throw XMLMarshalException.operationNotSupported("setValues(Vector values)");
    }

    /**
     * INTERNAL:
     * Sets the dom and updated document to be the owner document of the given element
     */
    public void setDOM(Node element) {
        this.dom = element;
        this.currentNode = element;
        this.getNamespaceResolver().setDOM(element);
    }

    public void setDOM(Element element) {
        this.dom = element;
        this.currentNode = element;
        this.getNamespaceResolver().setDOM(element);
    }

    /**
     * INTERNAL:
     * Print the dom XML string.
     */
    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write(Helper.getShortClassName(getClass()));
        writer.write("(");
        transformToWriter(writer);
        writer.write(")");
        return writer.toString();
    }

    /**
     * PUBLIC:
     * Return the set of element names from the DOM.
     */
    public Set keySet() {
        int length = getDOM().getChildNodes().getLength();
        HashSet keys = new HashSet(length);
        for (int index = 0; index < length; index++) {
            keys.add(getDOM().getChildNodes().item(index).getNodeName());
        }
        return keys;
    }

    /**
     * PUBLIC:
     * Return the collection of element values from the DOM.
     */
    public Collection values() {
        int length = getDOM().getChildNodes().getLength();
        Vector values = new Vector(length);
        for (int index = 0; index < length; index++) {
            values.add(getDOM().getChildNodes().item(index));
        }
        return values;
    }

    /**
     * Return the number of elements in the DOM.
     */
    public int size() {
        return getDOM().getAttributes().getLength() + getDOM().getChildNodes().getLength();
    }

    /**
     * Set the XML from an XML string.
     */
    public void transformFromXML(String xml) {
        Reader reader = new StringReader(xml);
        transformFromXML(reader);
    }

    /**
     * INTERNAL:
     * Return the namespace uri for the prefix of the given local name
     */
    private String resolveNamespace(NamespaceResolver namespaceResolver, String localName) {
        if(localName == null) {
            return null;
        }
        int colonIndex = localName.indexOf(XMLConstants.COLON);
        if (colonIndex < 0) {
            // handle target/default namespace
            if (namespaceResolver != null) {
                return namespaceResolver.getDefaultNamespaceURI();
            }
            return null;
        } else {
            if (namespaceResolver == null) {
                //throw an exception if the name has a : in it but the namespaceresolver is null
                throw XMLMarshalException.namespaceResolverNotSpecified(localName);
            }
            String prefix = localName.substring(0, colonIndex);
            String uri = namespaceResolver.resolveNamespacePrefix(prefix);
            if (uri == null) {
                //throw an exception if the prefix is not found in the namespaceresolver
                throw XMLMarshalException.namespaceNotFound(prefix);
            }
            return uri;
        }
    }

    public void setSession(AbstractSession session) {
        this.session = session;
        if (session != null && session.getDatasourceLogin() instanceof XMLLogin) {
            this.equalNamespaceResolvers = ((XMLLogin) session.getDatasourceLogin()).hasEqualNamespaceResolvers();
        }
    }
    
    /**
     * Set the XML from an XML reader.
     */
    public void transformFromXML(Reader reader) {
        XMLParser parser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();
        Document document = parser.parse(reader);
        setDOM(document.getDocumentElement());
    }

    /**
     * Return the XML string representation of the DOM.
     */
    public String transformToXML() {
        StringWriter writer = new StringWriter();
        transformToWriter(writer);
        return writer.toString();
    }

    /**
     * Write the XML string representation of the DOM.
     */
    public void transformToWriter(Writer writer) {
        XMLTransformer xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        xmlTransformer.transform(this.getDOM(), writer);
    }

    public String resolveNamespacePrefix(String prefix) {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        return xmlPlatform.resolveNamespacePrefix(currentNode, prefix);
    }

    /**
     * INTERNAL:
     * If the UnmarshalRecord has a ReferenceResolver, tell it to resolve its
     * references.
     * @since EclipseLink 2.5.0
     */
    public void resolveReferences(CoreAbstractSession abstractSession, IDResolver idResolver) {
        if(null != referenceResolver) {
            referenceResolver.resolveReferences(abstractSession, idResolver, unmarshaller.getErrorHandler());
        }
    }

}