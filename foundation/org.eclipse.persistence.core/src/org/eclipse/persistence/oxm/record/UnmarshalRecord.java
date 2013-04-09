/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.record;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.ReferenceResolver;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.SAXFragmentBuilder;
import org.eclipse.persistence.internal.oxm.TreeObjectBuilder;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.UnmarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecordImpl;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.IDResolver;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class UnmarshalRecord extends XMLRecord implements org.eclipse.persistence.internal.oxm.record.UnmarshalRecord<AbstractSession, DatabaseField, IDResolver, TreeObjectBuilder, XMLUnmarshaller> {

    private org.eclipse.persistence.internal.oxm.record.UnmarshalRecord unmarshalRecord;

    public UnmarshalRecord(org.eclipse.persistence.internal.oxm.record.UnmarshalRecord unmarshalRecord) {
        this.unmarshalRecord = unmarshalRecord;
    }

    public UnmarshalRecord(TreeObjectBuilder treeObjectBuilder) {
        unmarshalRecord = new UnmarshalRecordImpl(treeObjectBuilder);
    }

    @Override
    public void addAttributeValue(ContainerValue containerValue, Object value) {
        unmarshalRecord.addAttributeValue(containerValue, value);
    }

    @Override
    public void addAttributeValue(ContainerValue containerValue, Object value,
            Object collection) {
        unmarshalRecord.addAttributeValue(containerValue, value, collection);
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        unmarshalRecord.characters(ch, start, length);
    }

    @Override
    public void characters(CharSequence characters) throws SAXException {
        unmarshalRecord.characters(characters);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        unmarshalRecord.comment(ch, start, length);
    }

    @Override
    public Root createRoot() {
         return unmarshalRecord.createRoot();
    }

    @Override
    public void endCDATA() throws SAXException {
        unmarshalRecord.endCDATA();
    }

    @Override
    public void endDocument() throws SAXException {
        unmarshalRecord.endDocument();
    }

    @Override
    public void endDTD() throws SAXException {
        unmarshalRecord.endDTD();
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        unmarshalRecord.endElement(uri, localName, qName);
    }

    @Override
    public void endEntity(String name) throws SAXException {
        unmarshalRecord.endEntity(name);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        unmarshalRecord.endPrefixMapping(prefix);
    }

    @Override
    public void endUnmappedElement(String uri, String localName, String name)
            throws SAXException {
        unmarshalRecord.endUnmappedElement(uri, localName, name);
    }

    @Override
    public Object get(DatabaseField key) {
        return ((org.eclipse.persistence.internal.oxm.record.UnmarshalRecord) unmarshalRecord)
                .get(key);
    }

    @Override
    public NodeValue getAttributeChildNodeValue(String namespace,
            String localName) {
        return unmarshalRecord.getAttributeChildNodeValue(namespace, localName);
    }

    @Override
    public Attributes getAttributes() {
        return unmarshalRecord.getAttributes();
    }

    @Override
    public CharSequence getCharacters() {
        return unmarshalRecord.getCharacters();
    }

    /**
     * INTERNAL
     */
    @Override
    public org.eclipse.persistence.internal.oxm.record.UnmarshalRecord getChildRecord() {
        return unmarshalRecord.getChildRecord();
    }

    /**
     * INTERNAL
     */
    @Override
    public org.eclipse.persistence.internal.oxm.record.UnmarshalRecord getChildUnmarshalRecord(
            TreeObjectBuilder targetObjectBuilder) {
        return unmarshalRecord.getChildUnmarshalRecord(targetObjectBuilder);
    }

    @Override
    public Object getContainerInstance(ContainerValue containerValue) {
        return unmarshalRecord.getContainerInstance(containerValue);
    }

    @Override
    public Object getContainerInstance(ContainerValue containerValue, boolean b) {
        return unmarshalRecord.getContainerInstance(containerValue);
    }

    @Override
    public Object getCurrentObject() {
        return unmarshalRecord.getCurrentObject();
    }

    @Override
    public Descriptor getDescriptor() {
        return unmarshalRecord.getDescriptor();
    }

    @Override
    public Document getDocument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getDOM() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the encoding for this document. Only set on the root-level UnmarshalRecord
     * @return a String representing the encoding for this doc
     */
    @Override
    public String getEncoding() {
        return unmarshalRecord.getEncoding();
    }

    @Override
    public SAXFragmentBuilder getFragmentBuilder() {
        return unmarshalRecord.getFragmentBuilder();
    }

    @Override
    public int getLevelIndex() {
        return unmarshalRecord.getLevelIndex();
    }

    @Override
    public String getLocalName() {
        return unmarshalRecord.getLocalName();
    }

    @Override
    public char getNamespaceSeparator() {
        return unmarshalRecord.getNamespaceSeparator();
    }

    @Override
    public String getNamespaceURI() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNoNamespaceSchemaLocation() {
        return unmarshalRecord.getNoNamespaceSchemaLocation();
    }

    @Override
    public XPathNode getNonAttributeXPathNode(String namespaceURI,
            String localName, String qName, Attributes attributes) {
        return unmarshalRecord.getNonAttributeXPathNode(namespaceURI,
                localName, qName, attributes);
    }

    @Override
    public List<NullCapableValue> getNullCapableValues() {
        return unmarshalRecord.getNullCapableValues();
    }

    /**
     * INTERNAL
     */
    @Override
    public org.eclipse.persistence.internal.oxm.record.UnmarshalRecord getParentRecord() {
        return unmarshalRecord.getParentRecord();
    }

    @Override
    public Map<String, String> getPrefixesForFragment() {
        return unmarshalRecord.getPrefixesForFragment();
    }

    @Override
    public ReferenceResolver getReferenceResolver() {
        return unmarshalRecord.getReferenceResolver();
    }

    @Override
    public String getRootElementName() {
        return unmarshalRecord.getRootElementName();
    }

    @Override
    public String getRootElementNamespaceUri() {
        return unmarshalRecord.getRootElementNamespaceUri();
    }

    @Override
    public String getSchemaLocation() {
        return unmarshalRecord.getSchemaLocation();
    }

    @Override
    public XPathFragment getTextWrapperFragment() {
        return unmarshalRecord.getTextWrapperFragment();
    }

    @Override
    public DOMRecord getTransformationRecord() {
        return unmarshalRecord.getTransformationRecord();
    }

    @Override
    public QName getTypeQName() {
        return unmarshalRecord.getTypeQName();
    }

    @Override
    public UnmarshalContext getUnmarshalContext() {
        return unmarshalRecord.getUnmarshalContext();
    }

    @Override
    public XMLUnmarshaller getUnmarshaller() {
        return (XMLUnmarshaller) unmarshalRecord.getUnmarshaller();
    }

    @Override
    public UnmarshalNamespaceResolver getUnmarshalNamespaceResolver() {
        return unmarshalRecord.getUnmarshalNamespaceResolver();
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public org.eclipse.persistence.internal.oxm.record.UnmarshalRecord getUnmarshalRecord() {
        return unmarshalRecord;
    }

    /**
     * Gets the XML Version for this document. Only set on the root-level
     * UnmarshalRecord, if supported by the parser.
     */
    @Override
    public String getVersion() {
        return unmarshalRecord.getVersion();

    }

    @Override
    public XMLReader getXMLReader() {
        return unmarshalRecord.getXMLReader();
    }

    @Override
    public XPathNode getXPathNode() {
        return unmarshalRecord.getXPathNode();
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        unmarshalRecord.ignorableWhitespace(ch, start, length);
    }

    /**
     * INTERNAL
     */
    @Override
    public org.eclipse.persistence.internal.oxm.record.UnmarshalRecord initialize(TreeObjectBuilder objectBuilder) {
        return unmarshalRecord.initialize(objectBuilder);
    }

    @Override
    public void initializeRecord(Mapping mapping) throws SAXException {
        unmarshalRecord.initializeRecord(mapping);
    }

    @Override
    public boolean isBufferCDATA() {
        return unmarshalRecord.isBufferCDATA();
    }

    @Override
    public boolean isNamespaceAware() {
        return unmarshalRecord.isNamespaceAware();
    }

    @Override
    public boolean isNil() {
        return unmarshalRecord.isNil();
    }

    @Override
    public boolean isSelfRecord() {
        return unmarshalRecord.isSelfRecord();
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        unmarshalRecord.processingInstruction(target, data);
    }

    @Override
    public void reference(Reference reference) {
        unmarshalRecord.reference(reference);
    }

    @Override
    public void removeNullCapableValue(NullCapableValue nullCapableValue) {
        unmarshalRecord.removeNullCapableValue(nullCapableValue);
    }

    @Override
    public void resetStringBuffer() {
        unmarshalRecord.resetStringBuffer();
    }

    @Override
    public String resolveNamespacePrefix(String prefix) {
        return unmarshalRecord.resolveNamespacePrefix(prefix);
    }

    @Override
    public String resolveNamespaceUri(String namespaceURI) {
        return unmarshalRecord.resolveNamespaceUri(namespaceURI);
    }

    @Override
    public void resolveReferences(AbstractSession session, IDResolver idResolver) {
        unmarshalRecord.resolveReferences(session, idResolver);
    }

    @Override
    public void setAttributes(Attributes atts) {
        unmarshalRecord.setAttributes(atts);
    }

    @Override
    public void setAttributeValue(Object object, Mapping mapping) {
        unmarshalRecord.setAttributeValue(object, mapping);
    }
    @Override
    public void setAttributeValueNull(ContainerValue containerValue) {
        unmarshalRecord.setAttributeValueNull(containerValue);
    }

    @Override
    public void setChildRecord(org.eclipse.persistence.internal.oxm.record.UnmarshalRecord childRecord) {
        unmarshalRecord.setChildRecord(childRecord);
    }

    @Override
    public void setContainerInstance(int index, Object containerInstance) {
        unmarshalRecord.setContainerInstance(index, containerInstance);
    }

    @Override
    public void setCurrentObject(Object object) {
        this.currentObject = object;
        this.unmarshalRecord.setCurrentObject(object);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        unmarshalRecord.setDocumentLocator(locator);
    }

    @Override
    public void setFragmentBuilder(SAXFragmentBuilder fragmentBuilder) {
        unmarshalRecord.setFragmentBuilder(fragmentBuilder);
    }

    @Override
    public void setLocalName(String localName) {
        unmarshalRecord.setLocalName(localName);
    }

    @Override
    public void setNil(boolean isNil) {
        unmarshalRecord.setNil(isNil);
    }

    /**
     * INTERNAL
     */
    @Override
    public void setParentRecord(org.eclipse.persistence.internal.oxm.record.UnmarshalRecord parentRecord) {
        unmarshalRecord.setParentRecord(parentRecord);
    }

    @Override
    public void setReferenceResolver(ReferenceResolver referenceResolver) {
        unmarshalRecord.setReferenceResolver(referenceResolver);
    }

    @Override
    public void setRootElementName(String rootElementName) {
        unmarshalRecord.setRootElementName(rootElementName);
    }

    @Override
    public void setRootElementNamespaceUri(String rootElementNamespaceUri) {
        unmarshalRecord.setRootElementNamespaceUri(rootElementNamespaceUri);
    }

    @Override
    public void setSelfRecord(boolean isSelfRecord) {
        unmarshalRecord.setSelfRecord(isSelfRecord);
    }

    @Override
    public void setTextWrapperFragment(XPathFragment textWrapperFragment) {
        unmarshalRecord.setTextWrapperFragment(textWrapperFragment);
    }

    @Override
    public void setTransformationRecord(DOMRecord transformationRecord) {
        unmarshalRecord.setTransformationRecord(transformationRecord);
    }

    @Override
    public void setTypeQName(QName qname) {
        unmarshalRecord.setTypeQName(qname);
    }

    @Override
    public void setUnmarshalContext(UnmarshalContext unmarshalContext) {
        unmarshalRecord.setUnmarshalContext(unmarshalContext);
    }

    @Override
    public void setUnmarshaller(XMLUnmarshaller unmarshaller) {
        unmarshalRecord.setUnmarshaller(unmarshaller);
    }

    @Override
    public void setUnmarshalNamespaceResolver(
            UnmarshalNamespaceResolver unmarshalNamespaceResolver) {
        unmarshalRecord
                .setUnmarshalNamespaceResolver(unmarshalNamespaceResolver);
    }

    @Override
    public void setXMLReader(XMLReader xmlReader) {
        unmarshalRecord.setXMLReader(xmlReader);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        unmarshalRecord.skippedEntity(name);
    }

    @Override
    public void startCDATA() throws SAXException {
        unmarshalRecord.startCDATA();
    }

    @Override
    public void startDocument() throws SAXException {
        unmarshalRecord.startDocument();
    }

    @Override
    public void startDTD(String name, String publicId, String systemId)
            throws SAXException {
        unmarshalRecord.startDTD(name, publicId, systemId);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        unmarshalRecord.startElement(uri, localName, qName, atts);
    }

    @Override
    public void startEntity(String name) throws SAXException {
        unmarshalRecord.startEntity(name);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        unmarshalRecord.startPrefixMapping(prefix, uri);
    }

    @Override
    public String transformToXML() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unmappedContent() {
        unmarshalRecord.unmappedContent();
    }

    @Override
    public CoreAttributeGroup getUnmarshalAttributeGroup() {
        return unmarshalRecord.getUnmarshalAttributeGroup();
    }

    @Override
    public void setUnmarshalAttributeGroup(CoreAttributeGroup group) {
        unmarshalRecord.setUnmarshalAttributeGroup(group);
    }
}