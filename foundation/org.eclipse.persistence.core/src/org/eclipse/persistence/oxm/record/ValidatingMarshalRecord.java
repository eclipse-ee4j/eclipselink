/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XMLMarshaller;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidatingMarshalRecord extends MarshalRecord<Marshaller> {

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
        validatingRecord.setEqualNamespaceResolvers(marshalRecord.hasEqualNamespaceResolvers());
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
    public void node(Node node, NamespaceResolver resolver, String uri, String name) {
        validatingRecord.node(node, resolver, uri, name);
        marshalRecord.node(node, resolver, uri, name);
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
    public Node getDOM() {
        return marshalRecord.getDOM();
    }

    @Override
    public String transformToXML() {
        return marshalRecord.transformToXML();
    }

    @Override
    public String resolveNamespacePrefix(String prefix) {
        return marshalRecord.resolveNamespacePrefix(prefix);
    }

    @Override
    public Marshaller getMarshaller() {
        return marshalRecord.getMarshaller();
    }

    @Override
    public void setMarshaller(Marshaller marshaller) {
    	super.setMarshaller(marshaller);

        validatingRecord.setMarshaller(marshaller);
        marshalRecord.setMarshaller(marshaller);
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
    public CoreAbstractSession getSession() {
        return marshalRecord.getSession();
    }

    @Override
    public void setSession(CoreAbstractSession session) {
        super.setSession(session);
        validatingRecord.setSession(session);
        marshalRecord.setSession(session);
    }

    @Override
    public boolean isXOPPackage() {
        return marshalRecord.isXOPPackage();
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

    @Override
    public void writeHeader() {
        marshalRecord.writeHeader();
    }

    /**
     * @since EclipseLink 2.5.0
     */
    @Override
    public List<Namespace> addExtraNamespacesToNamespaceResolver(
            Descriptor descriptor, CoreAbstractSession session,
            boolean allowOverride, boolean ignoreEqualResolvers) {
        validatingRecord.addExtraNamespacesToNamespaceResolver(descriptor, session, allowOverride, ignoreEqualResolvers);
        return marshalRecord.addExtraNamespacesToNamespaceResolver(descriptor, session, allowOverride, ignoreEqualResolvers);
    }

    /**
     * @since EclipseLink 2.5.0
     */
    @Override
    public void removeExtraNamespacesFromNamespaceResolver(
            List<Namespace> extraNamespaces, CoreAbstractSession session) {
        validatingRecord.removeExtraNamespacesFromNamespaceResolver(extraNamespaces, session);
        marshalRecord.removeExtraNamespacesFromNamespaceResolver(extraNamespaces, session);
    }

    /**
     * @since EclipseLink 2.5.0
     */
    @Override
    public void attributeWithoutQName(String namespaceURI, String localName,
            String prefix, String value) {
        validatingRecord.attributeWithoutQName(namespaceURI, localName, prefix, value);
        marshalRecord.attributeWithoutQName(namespaceURI, localName, prefix, value);
    }

    /**
     * @since EclipseLink 2.5.0
     */
    @Override
    public boolean addXsiTypeAndClassIndicatorIfRequired(
            Descriptor xmlDescriptor, Descriptor referenceDescriptor,
            Field xmlField, boolean isRootElement) {
        validatingRecord.addXsiTypeAndClassIndicatorIfRequired(xmlDescriptor, referenceDescriptor, xmlField, isRootElement);
        return marshalRecord.addXsiTypeAndClassIndicatorIfRequired(xmlDescriptor, referenceDescriptor, xmlField, isRootElement);
    }

    /**
     * @since EclipseLink 2.5.0
     */
    @Override
    public boolean addXsiTypeAndClassIndicatorIfRequired(
            Descriptor xmlDescriptor, Descriptor referenceDescriptor,
            Field xmlField, Object originalObject, Object obj,
            boolean wasXMLRoot, boolean isRootElement) {
        validatingRecord.setNamespaceResolver(new NamespaceResolver(marshalRecord.getNamespaceResolver()));
        validatingRecord.addXsiTypeAndClassIndicatorIfRequired(xmlDescriptor, referenceDescriptor, xmlField, originalObject, obj, wasXMLRoot, isRootElement);
        return marshalRecord.addXsiTypeAndClassIndicatorIfRequired(xmlDescriptor, referenceDescriptor, xmlField, originalObject, obj, wasXMLRoot, isRootElement);
    }

    /**
     * @since EclipseLink 2.5.0
     */
    @Override
    public void writeXsiTypeAttribute(Descriptor descriptor, String typeUri,
            String typeLocal, String typePrefix, boolean addToNamespaceResolver) {
        validatingRecord.writeXsiTypeAttribute(descriptor, typeUri, typeLocal, typePrefix, addToNamespaceResolver);
        marshalRecord.writeXsiTypeAttribute(descriptor, typeUri, typeLocal, typePrefix, addToNamespaceResolver);
    }

    /**
     * @since EclipseLink 2.5.0
     */
    @Override
    public void writeXsiTypeAttribute(Descriptor xmlDescriptor,
            XMLSchemaReference xmlRef, boolean addToNamespaceResolver) {
        validatingRecord.writeXsiTypeAttribute(xmlDescriptor, xmlRef, addToNamespaceResolver);
        marshalRecord.writeXsiTypeAttribute(xmlDescriptor, xmlRef, addToNamespaceResolver);
    }

    @Override
    public void setXOPPackage(boolean isXOPPackage) {
        validatingRecord.setXOPPackage(isXOPPackage);
        marshalRecord.setXOPPackage(isXOPPackage);
    }

}