/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.record;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.IDResolver;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.ObjectBuilder;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.ReferenceResolver;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.SAXFragmentBuilder;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.XPathQName;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.oxm.unmapped.DefaultUnmappedContentHandler;
import org.eclipse.persistence.internal.oxm.unmapped.UnmappedContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * This class represents unmarshal record behaviour that is specific to the SAX
 * platform.
 */
public interface UnmarshalRecord<
    ABSTRACT_SESSION extends CoreAbstractSession,
    FIELD extends CoreField,
    ID_RESOLVER extends IDResolver,
    OBJECT_BUILDER extends ObjectBuilder,
    TRANSFORMATION_RECORD extends TransformationRecord,
    UNMARSHALLER extends Unmarshaller> extends AbstractUnmarshalRecord<ABSTRACT_SESSION, FIELD, UNMARSHALLER>, ExtendedContentHandler, LexicalHandler {

    UnmappedContentHandler DEFAULT_UNMAPPED_CONTENT_HANDLER = new DefaultUnmappedContentHandler();

    void addAttributeValue(ContainerValue containerValue, Object value);

    void addAttributeValue(ContainerValue containerValue, Object value, Object collection);

    Root createRoot();

    void endUnmappedElement(String uri, String localName, String name) throws SAXException;

    NodeValue getAttributeChildNodeValue(String namespace, String localName);

    Attributes getAttributes();

    CharSequence getCharacters();

    UnmarshalRecord getChildRecord();

    UnmarshalRecord getChildUnmarshalRecord(OBJECT_BUILDER targetObjectBuilder);

    Object getContainerInstance(ContainerValue containerValue);

    Object getContainerInstance(ContainerValue containerValue, boolean b);

    Object getCurrentObject();

    Descriptor getDescriptor();

    /**
     * Gets the encoding for this document. Only set on the root-level UnmarshalRecord
     * @return a String representing the encoding for this doc
     */
    String getEncoding();

    SAXFragmentBuilder getFragmentBuilder();

    XPathQName getLeafElementType();

    int getLevelIndex();

    String getLocalName();

    String getNoNamespaceSchemaLocation();

    XPathNode getNonAttributeXPathNode(String namespaceURI, String localName, String qName, Attributes attributes);

    List<NullCapableValue> getNullCapableValues();

    org.eclipse.persistence.internal.oxm.record.UnmarshalRecord getParentRecord();

    Map<String, String> getPrefixesForFragment();

    ReferenceResolver getReferenceResolver();

    String getRootElementName();

    String getRootElementNamespaceUri();

    String getSchemaLocation();

    XPathFragment getTextWrapperFragment();

    TRANSFORMATION_RECORD getTransformationRecord();

    QName getTypeQName();

    UnmarshalContext getUnmarshalContext();

    @Override UNMARSHALLER getUnmarshaller();

    UnmarshalNamespaceResolver getUnmarshalNamespaceResolver();

    /**
     * Gets the XML Version for this document. Only set on the root-level
     * UnmarshalRecord, if supported by the parser.
     */
    String getVersion();

    XMLReader getXMLReader();

    XPathNode getXPathNode();

    UnmarshalRecord initialize(OBJECT_BUILDER objectBuilder);

    void initializeRecord(Mapping mapping) throws SAXException;

    boolean isBufferCDATA();

    boolean isNil();

    boolean isSelfRecord();

    void reference(Reference reference);

    void removeNullCapableValue(NullCapableValue nullCapableValue);

    void resetStringBuffer();

    String resolveNamespaceUri(String namespaceURI);

    void resolveReferences(ABSTRACT_SESSION session, ID_RESOLVER idResolver);

    void setAttributes(Attributes atts);

    void setAttributeValue(Object object, Mapping mapping);

    void setAttributeValueNull(ContainerValue containerValue);

    void setChildRecord(UnmarshalRecord unmarshalRecord);

    void setContainerInstance(int index, Object containerInstance);

    void setCurrentObject(Object object);

    void setFragmentBuilder(SAXFragmentBuilder fragmentBuilder);

    void setLeafElementType(QName leafElementType);

    void setLocalName(String localName);

    @Override void setNil(boolean isNil);

    void setParentRecord(UnmarshalRecord unmarshalRecord);

    void setReferenceResolver(ReferenceResolver referenceResolver);

    void setRootElementName(String rootElementName);

    void setRootElementNamespaceUri(String rootElementNamespaceUri);

    void setSelfRecord(boolean isSelfRecord);

    void setSession(ABSTRACT_SESSION session);

    void setTextWrapperFragment(XPathFragment textWrapperFragment);

    void setTransformationRecord(TRANSFORMATION_RECORD transformationRecord);

    void setTypeQName(QName qname);

    void setUnmarshalContext(UnmarshalContext unmarshalContext);

    void setUnmarshaller(UNMARSHALLER unmarshaller);

    void setUnmarshalNamespaceResolver(UnmarshalNamespaceResolver unmarshalNamespaceResolver);

    void setXMLReader(XMLReader xmlReader);

    void unmappedContent();

    CoreAttributeGroup getUnmarshalAttributeGroup();

    void setUnmarshalAttributeGroup(CoreAttributeGroup group);

}
