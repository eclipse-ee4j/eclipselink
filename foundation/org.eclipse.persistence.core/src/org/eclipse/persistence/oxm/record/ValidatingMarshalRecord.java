/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.record;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidatingMarshalRecord extends MarshalRecord {

    private MarshalRecord marshalRecord;
    private ContentHandlerRecord validatingRecord;

    public ValidatingMarshalRecord(MarshalRecord marshalRecord, XMLMarshaller xmlMarshaller) {
        this.marshalRecord = marshalRecord;
        Schema schema = xmlMarshaller.getSchema();
        ValidatorHandler validatorHandler = schema.newValidatorHandler();
        validatorHandler.setErrorHandler(new ValidatingMarshalRecordErrorHandler(marshalRecord, xmlMarshaller.getErrorHandler()));
        if(xmlMarshaller.isFragment()) {
            try {
                validatorHandler.startDocument();
            } catch (SAXException e) {
            }
        }
        validatingRecord = new ContentHandlerRecord();
        validatingRecord.setMarshaller(xmlMarshaller);
        validatingRecord.setContentHandler(validatorHandler);
    }

    @Override
    public void startDocument(String encoding, String version) {
        validatingRecord.startDocument(encoding, version);
        marshalRecord.startDocument(encoding, version);
    }

    @Override
    public void endDocument() {
        validatingRecord.endDocument();
        marshalRecord.endDocument();
    }

    @Override
    public void element(XPathFragment frag) {
        validatingRecord.element(frag);
        marshalRecord.element(frag);
    }

    @Override
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        validatingRecord.attribute(xPathFragment, namespaceResolver, value);
        marshalRecord.attribute(xPathFragment, namespaceResolver, value);
    }

    @Override
    public void attribute(String namespaceURI, String localName, String qName, String value) {
        validatingRecord.attribute(namespaceURI, localName, qName, value);
        marshalRecord.attribute(namespaceURI, localName, qName, value);
    }

    @Override
    public void closeStartElement() {
        validatingRecord.closeStartElement();
        marshalRecord.closeStartElement();
    }

    @Override
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        validatingRecord.endElement(xPathFragment, namespaceResolver);
        marshalRecord.endElement(xPathFragment, namespaceResolver);
    }

    @Override
    public HashMap getPositionalNodes() {
        return marshalRecord.getPositionalNodes();
    }

    @Override
    public void addGroupingElement(XPathNode xPathNode) {
        validatingRecord.addGroupingElement(xPathNode);
        marshalRecord.addGroupingElement(xPathNode);
    }

    @Override
    public void removeGroupingElement(XPathNode xPathNode) {
        validatingRecord.removeGroupingElement(xPathNode);
        marshalRecord.removeGroupingElement(xPathNode);
    }

    @Override
    public void add(DatabaseField key, Object value) {
        validatingRecord.add(key, value);
        marshalRecord.add(key, value);
    }

    @Override
    public Object put(DatabaseField key, Object value) {
        validatingRecord.put(key, value);
        return marshalRecord.put(key, value);
    }

    @Override
    public void namespaceDeclarations(NamespaceResolver namespaceResolver) {
        validatingRecord.namespaceDeclarations(namespaceResolver);
        marshalRecord.namespaceDeclarations(namespaceResolver);
    }

    @Override
    public void startPrefixMapping(String prefix, String namespaceURI) {
        validatingRecord.startPrefixMapping(prefix, namespaceURI);
        marshalRecord.startPrefixMapping(prefix, namespaceURI);
    }

    @Override
    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
        validatingRecord.startPrefixMappings(namespaceResolver);
        marshalRecord.startPrefixMappings(namespaceResolver);
    }

    @Override
    public void endPrefixMapping(String prefix) {
        validatingRecord.endPrefixMapping(prefix);
        marshalRecord.endPrefixMapping(prefix);
    }

    @Override
    public void endPrefixMappings(NamespaceResolver namespaceResolver) {
        validatingRecord.endPrefixMappings(namespaceResolver);
        marshalRecord.endPrefixMappings(namespaceResolver);
    }

    @Override
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        validatingRecord.openStartElement(xPathFragment, namespaceResolver);
        marshalRecord.openStartElement(xPathFragment, namespaceResolver);
    }

    @Override
    public XPathFragment openStartGroupingElements(NamespaceResolver namespaceResolver) {
        validatingRecord.openStartGroupingElements(namespaceResolver);
        return marshalRecord.openStartGroupingElements(namespaceResolver);
    }

    @Override
    public void closeStartGroupingElements(XPathFragment groupingFragment) {
        validatingRecord.closeStartGroupingElements(groupingFragment);
        marshalRecord.closeStartGroupingElements(groupingFragment);
    }

    @Override
    protected void addPositionalNodes(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        validatingRecord.addPositionalNodes(xPathFragment, namespaceResolver);
        marshalRecord.addPositionalNodes(xPathFragment, namespaceResolver);
    }

    @Override
    public void characters(String value) {
        validatingRecord.characters(value);
        marshalRecord.characters(value);
    }

    @Override
    public void cdata(String value) {
        validatingRecord.cdata(value);
        marshalRecord.cdata(value);
    }

    @Override
    public void node(Node node, NamespaceResolver resolver) {
        validatingRecord.node(node, resolver);
        marshalRecord.node(node, resolver);
    }

    @Override
    public String getLocalName() {
        return marshalRecord.getLocalName();
    }

    @Override
    public String getNamespaceURI() {
        return marshalRecord.getNamespaceURI();
    }

    @Override
    public void clear() {
        validatingRecord.clear();
        marshalRecord.clear();
    }

    @Override
    public Document getDocument() {
        return marshalRecord.getDocument();
    }

    @Override
    public Element getDOM() {
        return marshalRecord.getDOM();
    }

    @Override
    public String transformToXML() {
        return marshalRecord.transformToXML();
    }

    @Override
    public boolean contains(Object value) {
        return marshalRecord.contains(value);
    }

    @Override
    public Object get(DatabaseField key) {
        return marshalRecord.get(key);
    }

    @Override
    public Object getIndicatingNoEntry(String fieldName) {
        return marshalRecord.getIndicatingNoEntry(fieldName);
    }

    @Override
    public String resolveNamespacePrefix(String prefix) {
        return marshalRecord.resolveNamespacePrefix(prefix);
    }

    @Override
    public XMLMarshaller getMarshaller() {
        return marshalRecord.getMarshaller();
    }

    @Override
    public void setMarshaller(XMLMarshaller marshaller) {
    	super.setMarshaller(marshaller);

        validatingRecord.setMarshaller(marshaller);
        marshalRecord.setMarshaller(marshaller);
    }

    @Override
    public XMLUnmarshaller getUnmarshaller() {
        return marshalRecord.getUnmarshaller();
    }

    @Override
    public void setUnmarshaller(XMLUnmarshaller unmarshaller) {
        validatingRecord.setUnmarshaller(unmarshaller);
        marshalRecord.setUnmarshaller(unmarshaller);
    }

    @Override
    public void setDocPresPolicy(DocumentPreservationPolicy policy) {
        validatingRecord.setDocPresPolicy(policy);
        marshalRecord.setDocPresPolicy(policy);
    }

    @Override
    public DocumentPreservationPolicy getDocPresPolicy() {
        return marshalRecord.getDocPresPolicy();
    }

    @Override
    public Object getOwningObject() {
        return marshalRecord.getOwningObject();
    }

    @Override
    public void setOwningObject(Object obj) {
        validatingRecord.setOwningObject(obj);
        marshalRecord.setOwningObject(obj);
    }

    @Override
    public Object getCurrentObject() {
        return marshalRecord.getCurrentObject();
    }

    @Override
    public void setCurrentObject(Object obj) {
        marshalRecord.setCurrentObject(obj);
        validatingRecord.setCurrentObject(obj);
    }

    @Override
    public QName getLeafElementType() {
        return marshalRecord.getLeafElementType();
    }

    @Override
    public void setLeafElementType(QName type) {
        validatingRecord.setLeafElementType(type);
        marshalRecord.setLeafElementType(type);
    }

    @Override
    public void setNamespaceResolver(NamespaceResolver nr) {
        validatingRecord.setNamespaceResolver(nr);
        marshalRecord.setNamespaceResolver(nr);
    }

    @Override
    public NamespaceResolver getNamespaceResolver() {
        return marshalRecord.getNamespaceResolver();
    }

    @Override
    public AbstractSession getSession() {
        return marshalRecord.getSession();
    }

    @Override
    public void setSession(AbstractSession session) {
    	this.session = session;
        validatingRecord.setSession(session);
        marshalRecord.setSession(session);
    }

    @Override
    public boolean isXOPPackage() {
        return marshalRecord.isXOPPackage();
    }

    @Override
    public void setXOPPackage(boolean isXOPPackage) {
        validatingRecord.setXOPPackage(isXOPPackage);
        marshalRecord.setXOPPackage(isXOPPackage);
    }

    @Override
    public boolean containsKey(Object key) {
        return marshalRecord.containsKey(key);
    }

    @Override
    public boolean containsKey(String fieldName) {
        return marshalRecord.containsKey(fieldName);
    }

    @Override
    public boolean containsKey(DatabaseField key) {
        return marshalRecord.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return marshalRecord.containsValue(value);
    }

    @Override
    public Enumeration elements() {
        return marshalRecord.elements();
    }

    @Override
    public Set entrySet() {
        return marshalRecord.entrySet();
    }

    @Override
    public Object get(Object key) {
        return marshalRecord.get(key);
    }

    @Override
    public Object get(String fieldName) {
        return marshalRecord.get(fieldName);
    }

    @Override
    public Object getValues(DatabaseField key) {
        return marshalRecord.getValues(key);
    }

    @Override
    public Object getValues(String key) {
        return marshalRecord.getValues(key);
    }

    @Override
    public Object getIndicatingNoEntry(DatabaseField key) {
        return marshalRecord.getIndicatingNoEntry(key);
    }

    @Override
    public DatabaseField getField(DatabaseField key) {
        return marshalRecord.getField(key);
    }

    @Override
    public Vector getFields() {
        return marshalRecord.getFields();
    }

    @Override
    public Vector getValues() {
        return marshalRecord.getValues();
    }

    @Override
    public boolean isEmpty() {
        return marshalRecord.isEmpty();
    }

    @Override
    public boolean hasNullValueInFields() {
        return marshalRecord.hasNullValueInFields();
    }

    @Override
    public Enumeration keys() {
        return marshalRecord.keys();
    }

    @Override
    public Set keySet() {
        return marshalRecord.keySet();
    }

    @Override
    public void mergeFrom(AbstractRecord row) {
        validatingRecord.mergeFrom(row);
        marshalRecord.mergeFrom(row);
    }

    @Override
    public Object put(Object key, Object value) throws ValidationException {
        validatingRecord.put(key, value);
        return marshalRecord.put(key, value);
    }

    @Override
    public Object put(String key, Object value) {
        validatingRecord.put(key, value);
        return marshalRecord.put(key, value);
    }

    @Override
    public void putAll(Map map) {
        validatingRecord.putAll(map);
        marshalRecord.putAll(map);
    }

    @Override
    public Object remove(Object key) {
        validatingRecord.remove(key);
        return marshalRecord.remove(key);
    }

    @Override
    public Object remove(String fieldName) {
        validatingRecord.remove(fieldName);
        return marshalRecord.remove(fieldName);
    }

    @Override
    public Object remove(DatabaseField key) {
        validatingRecord.remove(key);
        return marshalRecord.remove(key);
    }

    @Override
    public void replaceAt(Object value, int index) {
        validatingRecord.replaceAt(value, index);
        marshalRecord.replaceAt(value, index);
    }

    @Override
    public void setNullValueInFields(boolean nullValueInFields) {
        validatingRecord.setNullValueInFields(nullValueInFields);
        marshalRecord.setNullValueInFields(nullValueInFields);
    }

    @Override
    public int size() {
        return marshalRecord.size();
    }

    @Override
    public Collection values() {
        return marshalRecord.values();
    }

    @Override
    public void beforeContainmentMarshal(Object child) {
        marshalRecord.beforeContainmentMarshal(child);
    }

    @Override
    public void afterContainmentMarshal(Object parent, Object child) {
        marshalRecord.afterContainmentMarshal(parent, child);
    }

    private static class ValidatingMarshalRecordErrorHandler implements ErrorHandler {

        private MarshalRecord marshalRecord;
        private ErrorHandler errorHandler;

        public ValidatingMarshalRecordErrorHandler(MarshalRecord marshalRecord, ErrorHandler errorHandler) {
            this.marshalRecord = marshalRecord;
            this.errorHandler = errorHandler;
        }

        public void warning(SAXParseException exception) throws SAXException {
            if(null == errorHandler) {
                throw exception;
            }
            errorHandler.warning(marshalSAXParseException(exception));
        }

        public void error(SAXParseException exception) throws SAXException {
            if(null == errorHandler) {
                throw exception;
            }
            errorHandler.error(marshalSAXParseException(exception));
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            if(null == errorHandler) {
                throw exception;
            }
            errorHandler.fatalError(marshalSAXParseException(exception));
        }

        private MarshalSAXParseException marshalSAXParseException(SAXParseException exception) {
            return new MarshalSAXParseException(exception.getLocalizedMessage(), exception.getPublicId(), exception.getSystemId(), exception.getLineNumber(), exception.getColumnNumber(), exception.getException(), marshalRecord.getOwningObject());
        }

    }

    public static class MarshalSAXParseException extends SAXParseException {

        private Object object;

        public MarshalSAXParseException(String message, String publicId,
                String systemId, int lineNumber, int columnNumber, Exception e, Object object) {
            super(message, publicId, systemId, lineNumber, columnNumber, e);
            this.object = object;
        }

        public Object getObject() {
            return object;
        }

    }

}