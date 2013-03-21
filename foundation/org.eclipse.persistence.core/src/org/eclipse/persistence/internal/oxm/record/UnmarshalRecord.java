/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.oxm.record.DOMRecord;
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
    TREE_OBJECT_BUILDER extends ObjectBuilder,
    UNMARSHALLER extends Unmarshaller> extends AbstractUnmarshalRecord<ABSTRACT_SESSION, FIELD, UNMARSHALLER>, ExtendedContentHandler, LexicalHandler {

    public static final UnmappedContentHandler DEFAULT_UNMAPPED_CONTENT_HANDLER = new DefaultUnmappedContentHandler();

    public void addAttributeValue(ContainerValue containerValue, Object value);

    public void addAttributeValue(ContainerValue containerValue, Object value, Object collection);

    public Root createRoot();

    public void endUnmappedElement(String uri, String localName, String name) throws SAXException;

    public NodeValue getAttributeChildNodeValue(String namespace, String localName);

    public Attributes getAttributes();

    public CharSequence getCharacters();

    public  UnmarshalRecord getChildRecord();

    public UnmarshalRecord getChildUnmarshalRecord(TREE_OBJECT_BUILDER targetObjectBuilder);

    public Object getContainerInstance(ContainerValue containerValue);

    public Object getContainerInstance(ContainerValue containerValue, boolean b);

    public Object getCurrentObject();

    public Descriptor getDescriptor();

    /**
     * Gets the encoding for this document. Only set on the root-level UnmarshalRecord
     * @return a String representing the encoding for this doc
     */
    public String getEncoding();

    public SAXFragmentBuilder getFragmentBuilder();

    public XPathQName getLeafElementType();

    public int getLevelIndex();

    public String getLocalName();

    public String getNoNamespaceSchemaLocation();

    public XPathNode getNonAttributeXPathNode(String namespaceURI, String localName, String qName, Attributes attributes);

    public List<NullCapableValue> getNullCapableValues();

    public org.eclipse.persistence.internal.oxm.record.UnmarshalRecord getParentRecord();

    public Map<String, String> getPrefixesForFragment();

    public ReferenceResolver getReferenceResolver();

    public String getRootElementName();
 
    public String getRootElementNamespaceUri();

    public String getSchemaLocation();

    public XPathFragment getTextWrapperFragment();

    public DOMRecord getTransformationRecord();

    public QName getTypeQName();

    public UnmarshalContext getUnmarshalContext();

    public UNMARSHALLER getUnmarshaller();

    public UnmarshalNamespaceResolver getUnmarshalNamespaceResolver();

    /**
     * Gets the XML Version for this document. Only set on the root-level
     * UnmarshalRecord, if supported by the parser.
     */
    public String getVersion();

    public XMLReader getXMLReader();

    public XPathNode getXPathNode();

    public UnmarshalRecord initialize(TREE_OBJECT_BUILDER objectBuilder);

    public void initializeRecord(Mapping mapping) throws SAXException;

    public boolean isBufferCDATA();

    public boolean isNil();

    public boolean isSelfRecord();

    public void reference(Reference reference);

    public void removeNullCapableValue(NullCapableValue nullCapableValue);

    public void resetStringBuffer();

    public String resolveNamespaceUri(String namespaceURI);

    public void resolveReferences(ABSTRACT_SESSION session, ID_RESOLVER idResolver);

    public void setAttributes(Attributes atts);

    public void setAttributeValue(Object object, Mapping mapping);

    public void setAttributeValueNull(ContainerValue containerValue);

    public void setChildRecord(UnmarshalRecord unmarshalRecord);

    public void setContainerInstance(int index, Object containerInstance);

    public void setCurrentObject(Object object);

    public void setFragmentBuilder(SAXFragmentBuilder fragmentBuilder);

    public void setLeafElementType(QName leafElementType);

    public void setLocalName(String localName);

    public void setNil(boolean isNil);

    public void setParentRecord(UnmarshalRecord unmarshalRecord);

    public void setReferenceResolver(ReferenceResolver referenceResolver);

    public void setRootElementName(String rootElementName);
    
    public void setRootElementNamespaceUri(String rootElementNamespaceUri);

    public void setSelfRecord(boolean isSelfRecord);

    public void setSession(ABSTRACT_SESSION session);

    public void setTextWrapperFragment(XPathFragment textWrapperFragment);

    public void setTransformationRecord(DOMRecord transformationRecord);

    public void setTypeQName(QName qname);

    public void setUnmarshalContext(UnmarshalContext unmarshalContext);

    public void setUnmarshaller(UNMARSHALLER unmarshaller);

    public void setUnmarshalNamespaceResolver(UnmarshalNamespaceResolver unmarshalNamespaceResolver);

    public void setXMLReader(XMLReader xmlReader);

    public void unmappedContent();

    public CoreAttributeGroup getUnmarshalAttributeGroup();

    public void setUnmarshalAttributeGroup(CoreAttributeGroup group);

}