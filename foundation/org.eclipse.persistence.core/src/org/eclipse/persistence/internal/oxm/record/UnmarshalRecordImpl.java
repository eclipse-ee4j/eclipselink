/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     Marcel Valovy - 2.6.0 - added case insensitive unmarshalling
package org.eclipse.persistence.internal.oxm.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.descriptors.CoreDescriptorEventManager;
import org.eclipse.persistence.core.descriptors.CoreInheritancePolicy;
import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractRecord;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.ConversionManager;
import org.eclipse.persistence.internal.oxm.IDResolver;
import org.eclipse.persistence.internal.oxm.MappingNodeValue;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.ObjectBuilder;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.ReferenceResolver;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.SAXFragmentBuilder;
import org.eclipse.persistence.internal.oxm.StrBuffer;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.XPathPredicate;
import org.eclipse.persistence.internal.oxm.XPathQName;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.DirectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.TransformationMapping;
import org.eclipse.persistence.internal.oxm.record.namespaces.StackUnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.oxm.unmapped.UnmappedContentHandler;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.CommandProcessor;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2;
import org.xml.sax.ext.Locator2Impl;

/**
 * <p><b>Purpose:</b>Provide an implementation of ContentHandler that is used by TopLink OXM to
 * build mapped Java Objects from SAX events.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Implement the ContentHandler and LexicalHandler interfaces</li>
 * <li>Make calls into the appropriate NodeValues based on the incoming SAXEvents</li>
 * <li>Make callbacks into XMLReader for newObject events</li>
 * <li>Maintain a map of Collections to be populated for collection mappings.</li>
 * </ul>
 *
 * @see org.eclipse.persistence.internal.oxm.XPathNode
 * @see org.eclipse.persistence.internal.oxm.NodeValue
 * @see org.eclipse.persistence.internal.oxm.TreeObjectBuilder
 * @author bdoughan
 *
 */
public class UnmarshalRecordImpl<TRANSFORMATION_RECORD extends TransformationRecord> extends CoreAbstractRecord implements UnmarshalRecord<CoreAbstractSession, CoreField, IDResolver, ObjectBuilder, TRANSFORMATION_RECORD, Unmarshaller> {
    protected XMLReader xmlReader;
    private ObjectBuilder treeObjectBuilder;
    private XPathFragment xPathFragment;
    private XPathNode xPathNode;
    /**
     * Used to increase performance. We are trying to predict next mapping to unmarshal.
     * It can reduce the number of map lookups.
     */
    private XPathNode predictedNextXPathNode;
    private int levelIndex;
    private UnmarshalRecord childRecord;
    protected UnmarshalRecord parentRecord;
    private TRANSFORMATION_RECORD transformationRecord;
    private List<UnmarshalRecord> selfRecords;
    private Map<XPathFragment, Integer> indexMap;
    private List<NullCapableValue> nullCapableValues;
    private Object[] containerInstances;
    private List<ContainerValue> defaultEmptyContainerValues;
    private List<ContainerValue> populatedContainerValues;
    private boolean isBufferCDATA;
    private Attributes attributes;
    private QName typeQName;
    protected String rootElementLocalName;
    protected String rootElementName;
    protected String rootElementNamespaceUri;
    private SAXFragmentBuilder fragmentBuilder;
    private Map<String, String> prefixesForFragment;
    private String encoding;
    private String version;
    private String schemaLocation;
    private String noNamespaceSchemaLocation;
    private boolean isSelfRecord;
    private UnmarshalContext unmarshalContext;
    private UnmarshalNamespaceResolver unmarshalNamespaceResolver;
    private boolean isXsiNil;
    private boolean xpathNodeIsMixedContent = false;
    private int unmappedLevel = -1;
    private ReferenceResolver referenceResolver;


    protected Unmarshaller unmarshaller;
    protected Object currentObject;
    protected CoreAbstractSession session;
    protected boolean namespaceAware;
    private XPathQName leafElementType;
    private NamespaceResolver namespaceResolver;

    private CoreAttributeGroup unmarshalAttributeGroup;

    // The "snapshot" location of this object, for @XmlLocation
    private Locator xmlLocation;

    protected XPathFragment textWrapperFragment;

    private ConversionManager conversionManager;

    protected UnmarshalRecordImpl() {
    }

    public UnmarshalRecordImpl(ObjectBuilder objectBuilder) {
        this(objectBuilder, new ReferenceResolver());
    }

    private UnmarshalRecordImpl(ObjectBuilder objectBuilder, ReferenceResolver referenceResolver) {
        super();
        this.referenceResolver = referenceResolver;
        this.xPathFragment = new XPathFragment();
        xPathFragment.setNamespaceAware(isNamespaceAware());
        this.setUnmarshalAttributeGroup(DEFAULT_ATTRIBUTE_GROUP);
        initialize(objectBuilder);
    }

    @Override
    public UnmarshalRecord initialize(ObjectBuilder treeObjectBuilder) {
        this.isBufferCDATA = false;
        this.treeObjectBuilder = treeObjectBuilder;
        if (null != treeObjectBuilder) {
            this.xPathNode = treeObjectBuilder.getRootXPathNode();
            if (null != treeObjectBuilder.getNullCapableValues()) {
                this.nullCapableValues = new ArrayList<NullCapableValue>(treeObjectBuilder.getNullCapableValues());
            }
            if (null != treeObjectBuilder.getDefaultEmptyContainerValues()){
        this.defaultEmptyContainerValues = new ArrayList<ContainerValue>(treeObjectBuilder.getDefaultEmptyContainerValues());
            }
        }
        isSelfRecord = false;
        return this;
    }

    private void reset() {
        xPathNode = null;
        childRecord = null;
        transformationRecord = null;
        if(null != selfRecords) {
            selfRecords.clear();
        }
        if(null != indexMap) {
            indexMap.clear();
        }
        nullCapableValues = null;
        isBufferCDATA = false;
        attributes = null;
        typeQName = null;
        isSelfRecord = false;
        unmarshalContext = null;
        isXsiNil = false;
        unmappedLevel = -1;
        predictedNextXPathNode = null;
    }

    @Override
    public String getLocalName() {
        return rootElementLocalName;
    }

    @Override
    public void setLocalName(String localName) {
        rootElementLocalName = localName;
    }

    public String getNamespaceURI() {
        throw XMLMarshalException.operationNotSupported("getNamespaceURI");
    }

    public void clear() {
        throw XMLMarshalException.operationNotSupported("clear");
    }

    public Document getDocument() {
        throw XMLMarshalException.operationNotSupported("getDocument");
    }

    public String transformToXML() {
        throw XMLMarshalException.operationNotSupported("transformToXML");
    }

    @Override
    public XMLReader getXMLReader() {
        return this.xmlReader;
    }

    @Override
    public void setXMLReader(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
        namespaceAware = xmlReader.isNamespaceAware();
        if(xPathFragment != null){
            xPathFragment.setNamespaceAware(isNamespaceAware());
        }
    }

    @Override
    public UnmarshalRecord getChildRecord() {
        return this.childRecord;
    }

    @Override
    public void setChildRecord(UnmarshalRecord childRecord) {
        this.childRecord = childRecord;
        if (null != childRecord) {
            childRecord.setParentRecord(this);
        }
    }

    @Override
    public UnmarshalRecord getParentRecord() {
        return this.parentRecord;
    }

    /**
     * INTERNAL:
     * The ReferenceResolver that is leveraged by key based mappings.
     * @since EclipseLink 2.5.0
     */
    @Override
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
    @Override
    public void setReferenceResolver(ReferenceResolver referenceResolver) {
        this.referenceResolver = referenceResolver;
    }

    /**
     * Return the root element's prefix qualified name
     */
    @Override
    public String getRootElementName() {
        return rootElementName;
    }

    @Override
    public void setRootElementName(String qName) {
        this.rootElementName = qName;
    }

    /**
     * Return the root element's namespace URI
     */
    @Override
    public String getRootElementNamespaceUri() {
        return rootElementNamespaceUri;
    }

    @Override
    public void setRootElementNamespaceUri(String uri) {
        this.rootElementNamespaceUri = uri;
    }

    @Override
    public void setParentRecord(UnmarshalRecord parentRecord) {
        this.parentRecord = parentRecord;
    }

    @Override
    public TRANSFORMATION_RECORD getTransformationRecord() {
        return this.transformationRecord;
    }

    @Override
    public void setTransformationRecord(TRANSFORMATION_RECORD transformationRecord) {
        this.transformationRecord = transformationRecord;
    }

    @Override
    public UnmarshalNamespaceResolver getUnmarshalNamespaceResolver() {
        if(null == unmarshalNamespaceResolver) {
            this.unmarshalNamespaceResolver = new StackUnmarshalNamespaceResolver();
        }
        return this.unmarshalNamespaceResolver;
    }

    @Override
    public void setUnmarshalNamespaceResolver(UnmarshalNamespaceResolver anUnmarshalNamespaceResolver) {
        this.unmarshalNamespaceResolver = anUnmarshalNamespaceResolver;
    }

    @Override
    public List getNullCapableValues() {
        if (null == nullCapableValues) {
            this.nullCapableValues = new ArrayList<>();
        }
        return this.nullCapableValues;
    }

    @Override
    public void removeNullCapableValue(NullCapableValue nullCapableValue) {
        if(null != nullCapableValues) {
            nullCapableValues.remove(nullCapableValue);
        }
    }

    @Override
    public Object getContainerInstance(ContainerValue c) {
        return getContainerInstance(c, true);
    }

    @Override
    public Object getContainerInstance(ContainerValue c, boolean createContainerIfNecessary) {
        Object containerInstance = containerInstances[c.getIndex()];

        if (containerInstance == null) {
            Mapping mapping = c.getMapping();
            //don't attempt to do a get on a readOnly property.
            if(c.getReuseContainer() && !(mapping.isReadOnly())) {
        containerInstance = mapping.getAttributeValueFromObject(currentObject);
            }
            if(null == containerInstance && createContainerIfNecessary) {
                containerInstance = c.getContainerInstance();
            }
            containerInstances[c.getIndex()] = containerInstance;
            populatedContainerValues.add(c);
            if(defaultEmptyContainerValues != null){
        defaultEmptyContainerValues.remove(c);
            }
        }

        return containerInstance;
    }

    @Override
    public void setContainerInstance(int index, Object containerInstance) {
        containerInstances[index] = containerInstance;
    }

    /**
     * PUBLIC:
     * Gets the encoding for this document. Only set on the root-level UnmarshalRecord
     * @return a String representing the encoding for this doc
     */
    @Override
    public String getEncoding() {
        return encoding;
    }

    /**
     * INTERNAL:
     */
    public void setEncoding(String enc) {
        this.encoding = enc;
    }

    /**
     * PUBLIC:
     * Gets the XML Version for this document. Only set on the root-level
     * UnmarshalRecord, if supported by the parser.
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * INTERNAL:
     */
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    @Override
    public String getNoNamespaceSchemaLocation() {
        return noNamespaceSchemaLocation;
    }

    public void setNoNamespaceSchemaLocation(String location) {
        this.noNamespaceSchemaLocation = location;
    }

    protected StrBuffer getStringBuffer() {
        return unmarshaller.getStringBuffer();
    }

    @Override
    public CharSequence getCharacters() {
        return unmarshaller.getStringBuffer();
    }

    @Override
    public Attributes getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public QName getTypeQName() {
        return this.typeQName;
    }

    @Override
    public void setTypeQName(QName typeQName) {
        this.typeQName = typeQName;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    if(xmlReader != null){
        xmlReader.setLocator(locator);
        if (null == rootElementName  && null == rootElementLocalName && parentRecord == null && locator instanceof Locator2){
                Locator2 loc = (Locator2)locator;
                this.setEncoding(loc.getEncoding());
                this.setVersion(loc.getXMLVersion());
            }
    }
    }

    public Locator getDocumentLocator() {
    if(xmlReader != null){
        return xmlReader.getLocator();
    }
      return null;
    }

    @Override
    public Object get(CoreField key) {
        Field xmlField = this.convertToXMLField(key);
        XPathFragment lastFragment = xmlField.getLastXPathFragment();
        String namespaceURI = lastFragment.getNamespaceURI();
        if(namespaceURI == null){
        NamespaceResolver namespaceResolver = xmlField.getNamespaceResolver();
            namespaceURI = Constants.EMPTY_STRING;
            if (null != namespaceResolver && !(lastFragment.isAttribute() && lastFragment.getPrefix() == null)) {
                namespaceURI = namespaceResolver.resolveNamespacePrefix(lastFragment.getPrefix());
                if (null == namespaceURI) {
                    namespaceURI = Constants.EMPTY_STRING;
                }
            }
        }
        if(isNamespaceAware()){
            return attributes.getValue(namespaceURI, lastFragment.getLocalName());
        }
        return attributes.getValue(lastFragment.getLocalName());
    }


    @Override
    public XPathNode getXPathNode() {
        return xPathNode;
    }

    @Override
    public Descriptor getDescriptor() {
        return (Descriptor) treeObjectBuilder.getDescriptor();
    }

    @Override
    public UnmarshalContext getUnmarshalContext() {
        return unmarshalContext;
    }

    @Override
    public void setUnmarshalContext(UnmarshalContext unmarshalContext) {
        this.unmarshalContext = unmarshalContext;
    }

    @Override
    public boolean isNil() {
        return this.isXsiNil;
    }

    @Override
    public void setNil(boolean nil) {
        this.isXsiNil = nil;
    }

    @Override
    public void startDocument() throws SAXException {
        if (unmarshaller.getIDResolver() != null && parentRecord == null) {
        unmarshaller.getIDResolver().startDocument(unmarshaller.getErrorHandler());
        }
    }

    private void initializeRecord(Attributes attrs) throws SAXException{
        this.setAttributes(attrs);
    Descriptor xmlDescriptor = (Descriptor) treeObjectBuilder.getDescriptor();
    if(!xmlDescriptor.hasInheritance() || xmlDescriptor.getInheritancePolicy().getClassIndicatorField() == null){
        initialize((ObjectBuilder)xmlDescriptor.getObjectBuilder());
        initializeRecord((Mapping)null);
        return;
        }
    CoreInheritancePolicy inheritancePolicy = xmlDescriptor.getInheritancePolicy();
    Class classValue = treeObjectBuilder.classFromRow(this, session);
     if (classValue == null) {
             // no xsi:type attribute - look for type indicator on the default root element
             QName leafElementType = xmlDescriptor.getDefaultRootElementType();

             // if we have a user-set type, try to get the class from the inheritance policy
             if (leafElementType != null) {
         XPathQName xpathQName = new XPathQName(leafElementType, isNamespaceAware());
                 Object indicator = inheritancePolicy.getClassIndicatorMapping().get(xpathQName);
                 if(indicator != null) {
                     classValue = (Class)indicator;
                 }
             }
         }
         if (classValue != null) {
             xmlDescriptor = (Descriptor)session.getDescriptor(classValue);
         }
         initialize((ObjectBuilder)xmlDescriptor.getObjectBuilder());
         initializeRecord((Mapping)null);
    }

    @Override
    public void initializeRecord(Mapping selfRecordMapping) throws SAXException {
        try {
            Descriptor xmlDescriptor = (Descriptor) treeObjectBuilder.getDescriptor();
            if(xmlDescriptor.isSequencedObject()) {
                unmarshalContext = new SequencedUnmarshalContext();
            } else {
                unmarshalContext = ObjectUnmarshalContext.getInstance();
            }

            currentObject = this.xmlReader.getCurrentObject(session, selfRecordMapping);
            if (currentObject == null) {
                currentObject = treeObjectBuilder.buildNewInstance();
            }
            if (xmlDescriptor.getLocationAccessor() != null && xmlReader.getLocator() != null){
                // Check to see if this Descriptor isLocationAware
                    // Store the snapshot of the current documentLocator
                    xmlLocation  = new Locator2Impl(xmlReader.getLocator());
            }

            Object parentRecordCurrentObject = null;
            if (null != this.parentRecord) {
                parentRecordCurrentObject = parentRecord.getCurrentObject();
            }

            Unmarshaller.Listener xmlUnmarshalListener = unmarshaller.getUnmarshalListener();
            if (null != xmlUnmarshalListener) {
                if (null == this.parentRecord) {
                    xmlUnmarshalListener.beforeUnmarshal(currentObject, null);
                } else {
                    xmlUnmarshalListener.beforeUnmarshal(currentObject, parentRecordCurrentObject);
                }
            }
            if (null == parentRecord) {
                this.xmlReader.newObjectEvent(currentObject, null, selfRecordMapping);
            } else {
                this.xmlReader.newObjectEvent(currentObject, parentRecordCurrentObject, selfRecordMapping);
            }
            List containerValues = treeObjectBuilder.getContainerValues();
            if (null != containerValues) {
                int containerSize = containerValues.size();
                containerInstances = new Object[containerSize];
                populatedContainerValues = new ArrayList(containerSize);
            }

            if (null != xPathNode.getSelfChildren()) {
                int selfChildrenSize = xPathNode.getSelfChildren().size();
                selfRecords = new ArrayList<>(selfChildrenSize);
                for (int x = 0; x < selfChildrenSize; x++) {
                    NodeValue nv = xPathNode.getSelfChildren().get(x).getNodeValue();
                    if (null != nv) {
                        selfRecords.add(nv.buildSelfRecord(this, attributes));
                    }
                }
            }
        } catch (EclipseLinkException e) {
            if (null == xmlReader.getErrorHandler()) {
                throw e;
            } else {
                SAXParseException saxParseException = new SAXParseException(null, getDocumentLocator(), e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }
    }

    @Override
    public void endDocument() throws SAXException {
        if (unmarshaller.getIDResolver() != null && parentRecord == null) {
        unmarshaller.getIDResolver().endDocument();
        }
        if (null != selfRecords) {
            for (int x = 0, selfRecordsSize = selfRecords.size(); x < selfRecordsSize; x++) {
                UnmarshalRecord selfRecord = selfRecords.get(x);
                if(selfRecord != null){
                    selfRecord.endDocument();
                }
            }
        }

        if (null != xPathNode.getSelfChildren()) {
            int selfChildrenSize = xPathNode.getSelfChildren().size();
            for (int x = 0; x < selfChildrenSize; x++) {
                XPathNode selfNode = xPathNode.getSelfChildren().get(x);
                if (null != selfNode.getNodeValue()) {
                    selfNode.getNodeValue().endSelfNodeValue(this, selfRecords.get(x), attributes);
                }
            }
        }

        CoreDescriptor xmlDescriptor = treeObjectBuilder.getDescriptor();

        try {
            // PROCESS COLLECTION MAPPINGS
        //All populated containerValues need to be set on the object
        if(null != populatedContainerValues){
                for (int populatedCVSize=populatedContainerValues.size(), i = populatedCVSize-1; i>=0; i--) {
                ContainerValue cv = (populatedContainerValues.get(i));
                cv.setContainerInstance(currentObject, getContainerInstance(cv, cv.isDefaultEmptyContainer()));
            }
        }

        //Additionally if any containerValues are defaultEmptyContainerValues they need to be set to a new empty container
        if(null != defaultEmptyContainerValues){
                 for (int defaultEmptyCVSize=defaultEmptyContainerValues.size(),i = defaultEmptyCVSize-1; i>=0; i--) {
                     ContainerValue cv = (defaultEmptyContainerValues.get(i));
                     cv.setContainerInstance(currentObject, getContainerInstance(cv, cv.isDefaultEmptyContainer()));
                 }

        }
            // PROCESS NULL CAPABLE VALUES
            // This must be done because the node may not have existed to
            // trigger the mapping.
            if(null != nullCapableValues) {
                for (int x = 0, nullValuesSize = nullCapableValues.size(); x < nullValuesSize; x++) {
                    nullCapableValues.get(x).setNullValue(currentObject, session);
                }
            }

            // PROCESS TRANSFORMATION MAPPINGS
            List<TransformationMapping> transformationMappings = treeObjectBuilder.getTransformationMappings();
            if (null != transformationMappings) {
                for (int x = 0, transformationMappingsSize = transformationMappings.size(); x < transformationMappingsSize; x++) {
                    TransformationMapping transformationMapping = transformationMappings.get(x);
                    transformationMapping.readFromRowIntoObject((XMLRecord) transformationRecord, currentObject, session, true);
                }
            }

            Unmarshaller.Listener listener = unmarshaller.getUnmarshalListener();
            if (listener != null) {
                if (this.parentRecord != null) {
                    listener.afterUnmarshal(currentObject, parentRecord.getCurrentObject());
                } else {
                    listener.afterUnmarshal(currentObject, null);
                }
            }

            // HANDLE POST BUILD EVENTS
            if(xmlDescriptor.hasEventManager()) {
                CoreDescriptorEventManager eventManager = xmlDescriptor.getEventManager();
                if (null != eventManager && eventManager.hasAnyEventListeners()) {
                    DescriptorEvent event = new DescriptorEvent(currentObject);
                    event.setSession((AbstractSession) session);
                    event.setRecord(null); //this);
                    event.setEventCode(DescriptorEventManager.PostBuildEvent);
                    eventManager.executeEvent(event);
                }
            }
        } catch (EclipseLinkException e) {
            if (null == xmlReader.getErrorHandler()) {
                throw e;
            } else {
                SAXParseException saxParseException = new SAXParseException(null, getDocumentLocator(), e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }

        // if the object has any primary key fields set, add it to the cache
        if(null != referenceResolver) {
            if(null != xmlDescriptor) {
                List primaryKeyFields = xmlDescriptor.getPrimaryKeyFields();
                if(null != primaryKeyFields) {
                    int primaryKeyFieldsSize = primaryKeyFields.size();
                    if (primaryKeyFieldsSize > 0) {
                        CacheId pk = (CacheId) treeObjectBuilder.extractPrimaryKeyFromObject(currentObject, session);
                        for (int x=0; x<primaryKeyFieldsSize; x++) {
                            Object value = pk.getPrimaryKey()[x];
                            if (null == value) {
                                Field pkField = (Field) xmlDescriptor.getPrimaryKeyFields().get(x);
                                pk.set(x, unmarshaller.getContext().getValueByXPath(currentObject, pkField.getXPath(), pkField.getNamespaceResolver(), Object.class));
                            }
                        }
                        referenceResolver.putValue(xmlDescriptor.getJavaClass(), pk, currentObject);

                        if (unmarshaller.getIDResolver() != null) {
                            try {
                                if (primaryKeyFieldsSize > 1) {
                                    Map<String, Object> idWrapper = new HashMap<>();
                                    for (int x = 0; x < primaryKeyFieldsSize; x++) {
                                        String idName = (String) xmlDescriptor.getPrimaryKeyFieldNames().get(x);
                                        Object idValue = pk.getPrimaryKey()[x];
                                        idWrapper.put(idName, idValue);
                                    }
                                    unmarshaller.getIDResolver().bind(idWrapper, currentObject);
                                } else {
                    unmarshaller.getIDResolver().bind(pk.getPrimaryKey()[0], currentObject);
                                }
                            } catch (SAXException e) {
                                throw XMLMarshalException.unmarshalException(e);
                            }
                        }

                    }
                }
            }
        }

        if(null != parentRecord) {
            reset();
        }

        // Set XML Location if applicable
        if (xmlLocation != null && ((Descriptor) xmlDescriptor).getLocationAccessor() != null) {
            ((Descriptor) xmlDescriptor).getLocationAccessor().setAttributeValueInObject(getCurrentObject(), xmlLocation);
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        getUnmarshalNamespaceResolver().push(prefix, uri);
        getPrefixesForFragment().put(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        getUnmarshalNamespaceResolver().pop(prefix);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (currentObject == null) {
            initializeRecord(atts);
        }

        XPathFragment xPathNodeXPathFragment = xPathNode.getXPathFragment();
        if((null != xPathNodeXPathFragment && xPathNodeXPathFragment.nameIsText()) || xpathNodeIsMixedContent) {
            xpathNodeIsMixedContent = false;
            NodeValue xPathNodeUnmarshalNodeValue = xPathNode.getUnmarshalNodeValue();
            if (null != xPathNodeUnmarshalNodeValue) {
                boolean isIncludedInAttributeGroup = true;
                if(xPathNodeUnmarshalNodeValue.isMappingNodeValue()) {
                    Mapping mapping = ((MappingNodeValue)xPathNodeUnmarshalNodeValue).getMapping();
                    isIncludedInAttributeGroup = this.unmarshalAttributeGroup.containsAttributeInternal(mapping.getAttributeName());
                }
                if(isIncludedInAttributeGroup) {
                    xPathNodeUnmarshalNodeValue.endElement(xPathFragment, this);
                    if (xPathNode.getParent() != null) {
                        xPathNode = xPathNode.getParent();
                    }
                }
            }
        }

        // set the root element's local name and namespace prefix and look for
        // schema locations etc.
        if (null == rootElementName  && null == rootElementLocalName && parentRecord == null){
            rootElementLocalName = localName;
            rootElementName = qName;
            rootElementNamespaceUri = namespaceURI;
            schemaLocation = atts.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_LOCATION);
            noNamespaceSchemaLocation = atts.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.NO_NS_SCHEMA_LOCATION);
        }

        try {
            if (null != selfRecords) {
                for (int x = 0, selfRecordsSize = selfRecords.size(); x < selfRecordsSize; x++) {
                    UnmarshalRecord selfRecord = selfRecords.get(x);
                    if(selfRecord == null){
                        getFragmentBuilder().startElement(namespaceURI, localName, qName, atts);
                    }else{
                        selfRecord.startElement(namespaceURI, localName, qName, atts);
                    }
                }
            }

            if(unmappedLevel != -1 && unmappedLevel <= levelIndex) {
                levelIndex++;
                return;
            }

            XPathNode node = null;
            if (null != predictedNextXPathNode) {

                XPathFragment xpf = predictedNextXPathNode.getXPathFragment();

                if (null != xpf && xPathNode == predictedNextXPathNode.getParent() && (localName == xpf.getLocalName() || localName.equals(xpf.getLocalName())) && (namespaceURI == xpf.getNamespaceURI() || namespaceURI.equals(xpf.getNamespaceURI())) && null == xpf.getPredicate() && !xpf.containsIndex()) {

                    updateXPathFragment(qName, localName, namespaceURI);
                    node = predictedNextXPathNode;
                }
            }

            if (null == node) {
                node = getNonAttributeXPathNode(namespaceURI, localName, qName, atts);
            }

            if (null == node) {
                NodeValue parentNodeValue = xPathNode.getUnmarshalNodeValue();
                if ((null == xPathNode.getXPathFragment()) && (parentNodeValue != null)) {
                    XPathFragment parentFragment = new XPathFragment();
                    parentFragment.setNamespaceAware(isNamespaceAware());
                    if(namespaceURI != null && namespaceURI.length() == 0){
                        parentFragment.setLocalName(qName);
                        parentFragment.setNamespaceURI(null);
                    } else {
                        parentFragment.setLocalName(localName);
                        parentFragment.setNamespaceURI(namespaceURI);
                    }
                    if (parentNodeValue.startElement(parentFragment, this, atts)) {
                        levelIndex++;
                    } else {
                        // UNMAPPED CONTENT
                        startUnmappedElement(namespaceURI, localName, qName, atts);
                        return;
                    }
                } else {
                    // UNMAPPED CONTENT
                    levelIndex++;
                    startUnmappedElement(namespaceURI, localName, qName, atts);
                    return;
                }
            } else {

                xPathNode = node;
                unmarshalContext.startElement(this);
                levelIndex++;

                if(xmlReader.getMediaType().isApplicationXML()) {
                    String xsiNilValue = atts.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_NIL_ATTRIBUTE);
                    if (xsiNilValue != null) {
                        setNil(xsiNilValue.equals(Constants.BOOLEAN_STRING_TRUE) || xsiNilValue.equals("1"));
                    }
                }

                if(node.getNullCapableValue() != null){
                    getNullCapableValues().add(node.getNullCapableValue());
                }

                NodeValue nodeValue = node.getUnmarshalNodeValue();
                if (null != nodeValue) {
                    boolean isIncludedInAttributeGroup = true;
                    if(nodeValue.isMappingNodeValue()) {
                        Mapping mapping = ((MappingNodeValue)nodeValue).getMapping();
                        isIncludedInAttributeGroup = this.unmarshalAttributeGroup.containsAttributeInternal(mapping.getAttributeName());
                    }
                    if (!isIncludedInAttributeGroup || !nodeValue.startElement(xPathFragment, this, atts)) {
                        // UNMAPPED CONTENT
                        startUnmappedElement(namespaceURI, localName, qName, atts);
                        return;
                    }
                }

                //Handle Attributes
                if(xPathNode.getAttributeChildren() != null || xPathNode.getAnyAttributeNodeValue() != null || selfRecords != null) {
                    for (int i = 0, size=atts.getLength(); i < size; i++) {
                        String attNamespace = atts.getURI(i);
                        String attLocalName = atts.getLocalName(i);
                        String value = atts.getValue(i);
                        NodeValue attributeNodeValue = null;

                        // Some parsers don't set the URI/local name for namespace
                        // attributes
                        if ((attLocalName == null) || (attLocalName.length() == 0)) {
                            String qname = atts.getQName(i);
                            if (qname != null) {
                                int qnameLength = qname.length();
                                if (qnameLength > 0) {
                                    int idx = qname.indexOf(Constants.COLON);
                                    if (idx > 0) {
                                        attLocalName = qname.substring(idx + 1, qnameLength);
                                        String attPrefix = qname.substring(0, idx);
                                        if (attPrefix.equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)) {
                                            attNamespace = javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
                                        }
                                    } else {
                                        attLocalName = qname;
                                        if (attLocalName.equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)) {
                                            attNamespace = javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
                                        }
                                    }
                                }
                            }
                        }

                        //Look for any Self-Mapping nodes that may want this attribute.
                        if (this.selfRecords != null) {
                            for (int j = 0; j < selfRecords.size(); j++) {
                                UnmarshalRecord nestedRecord = selfRecords.get(j);
                                if(nestedRecord != null){
                                    attributeNodeValue = nestedRecord.getAttributeChildNodeValue(attNamespace, attLocalName);
                                    if (attributeNodeValue != null) {
                                        attributeNodeValue.attribute(nestedRecord, attNamespace, attLocalName, value);
                                    }
                                }
                            }
                        }
                        if (attributeNodeValue == null) {
                            attributeNodeValue = this.getAttributeChildNodeValue(attNamespace, attLocalName);

                            try {
                                if (attributeNodeValue != null) {
                                    if(attributeNodeValue.isMappingNodeValue()) {
                                        Mapping mapping = ((MappingNodeValue)attributeNodeValue).getMapping();
                                        if(!unmarshalAttributeGroup.containsAttributeInternal(mapping.getAttributeName())) {
                                            continue;
                                        }
                                    }
                                    attributeNodeValue.attribute(this, attNamespace, attLocalName, value);
                                } else {
                                    if (xPathNode.getAnyAttributeNodeValue() != null) {
                                        xPathNode.getAnyAttributeNodeValue().attribute(this, attNamespace, attLocalName, value);
                                    }
                                }
                            } catch(EclipseLinkException e) {
                                if ((null == xmlReader) || (null == xmlReader.getErrorHandler())) {
                                    throw e;
                                } else {
                                    SAXParseException saxParseException = new SAXParseException(e.getLocalizedMessage(), getDocumentLocator(), e);
                                    xmlReader.getErrorHandler().warning(saxParseException);
                                }
                            }
                        }
                    }
                }
            }
            if(prefixesForFragment != null){
                this.prefixesForFragment.clear();
            }
        } catch (EclipseLinkException e) {
            if ((null == xmlReader) || (null == xmlReader.getErrorHandler())) {
                throw e;
            } else {
                SAXParseException saxParseException = new SAXParseException(e.getLocalizedMessage(), getDocumentLocator(), e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }
    }

    private void updateXPathFragment(String qName, String localName, String namespaceURI) {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            xPathFragment.setLocalName(qName);
            xPathFragment.setNamespaceURI(null);
        } else {
            xPathFragment.setLocalName(localName);
            xPathFragment.setNamespaceURI(namespaceURI);
        }
    }

    public void startUnmappedElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if(xmlReader.getMediaType().isApplicationXML() && null == selfRecords && !isSelfRecord) {
            ErrorHandler errorHandler = xmlReader.getErrorHandler();
            // Bug 452584 - check if a warning exception should be generated when an unmapped element is encountered
            if(null != errorHandler && unmarshaller.shouldWarnOnUnmappedElement()) {
                StringBuilder messageBuilder = new StringBuilder("unexpected element (uri:\"");
                if(null != namespaceURI) {
                    messageBuilder.append(namespaceURI);
                }
                messageBuilder.append("\", local:\"");
                messageBuilder.append(localName);
                messageBuilder.append("\"). Expected elements are ");
                List<XPathNode> nonAttributeChildren = xPathNode.getNonAttributeChildren();
                if(nonAttributeChildren == null || nonAttributeChildren.size() == 0) {
                    messageBuilder.append("(none)");
                } else {
                    for(int x=0, size=nonAttributeChildren.size(); x<size; x++) {
                        XPathFragment nonAttributeChildXPathFragment = nonAttributeChildren.get(x).getXPathFragment();
                        messageBuilder.append("<{");
                        String nonAttributeChildXPathFragmentNamespaceURI = nonAttributeChildXPathFragment.getNamespaceURI();
                        if(null != nonAttributeChildXPathFragmentNamespaceURI) {
                            messageBuilder.append(nonAttributeChildXPathFragmentNamespaceURI);
                        }
                        messageBuilder.append('}');
                        messageBuilder.append(nonAttributeChildXPathFragment.getLocalName());
                        messageBuilder.append('>');
                        if(x<size-1) {
                            messageBuilder.append(',');
                        }
                    }
                }
                errorHandler.warning(new SAXParseException(messageBuilder.toString(), getDocumentLocator()));
            }
        }
        if ((null != selfRecords) || (null == xmlReader) || isSelfRecord()) {
            if(-1 == unmappedLevel) {
                this.unmappedLevel = this.levelIndex;
            }
            return;
        }
        Class unmappedContentHandlerClass = unmarshaller.getUnmappedContentHandlerClass();
        UnmappedContentHandler unmappedContentHandler;
        if (null == unmappedContentHandlerClass) {
            unmappedContentHandler = DEFAULT_UNMAPPED_CONTENT_HANDLER;
        } else {
            try {
                PrivilegedNewInstanceFromClass privilegedNewInstanceFromClass = new PrivilegedNewInstanceFromClass(unmappedContentHandlerClass);
                unmappedContentHandler = (UnmappedContentHandler)privilegedNewInstanceFromClass.run();
            } catch (ClassCastException e) {
                throw XMLMarshalException.unmappedContentHandlerDoesntImplement(e, unmappedContentHandlerClass.getName());
            } catch (IllegalAccessException e) {
                throw XMLMarshalException.errorInstantiatingUnmappedContentHandler(e, unmappedContentHandlerClass.getName());
            } catch (InstantiationException e) {
                throw XMLMarshalException.errorInstantiatingUnmappedContentHandler(e, unmappedContentHandlerClass.getName());
            }
        }
        UnmappedContentHandlerWrapper unmappedContentHandlerWrapper = new UnmappedContentHandlerWrapper(this, unmappedContentHandler);
        unmappedContentHandlerWrapper.startElement(namespaceURI, localName, qName, atts);
        xmlReader.setContentHandler(unmappedContentHandlerWrapper);
        xmlReader.setLexicalHandler(unmappedContentHandlerWrapper);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            if (null != selfRecords) {
                for (int x = 0, selfRecordsSize = selfRecords.size(); x < selfRecordsSize; x++) {
                    UnmarshalRecord selfRecord = selfRecords.get(x);
                    if(selfRecord != null){
                        selfRecord.endElement(namespaceURI, localName, qName);
                    }else{
                        getFragmentBuilder().endSelfElement(namespaceURI, localName, qName);
                    }
                }
            }
            if(-1 != unmappedLevel && unmappedLevel <= levelIndex) {
                if(levelIndex == unmappedLevel) {
                    unmappedLevel = -1;
                }
                levelIndex--;
                return;
            }
            NodeValue unmarshalNodeValue = xPathNode.getUnmarshalNodeValue();
            if (null != unmarshalNodeValue) {
                boolean isIncludedInAttributeGroup = true;
                if(unmarshalNodeValue.isMappingNodeValue()) {
                    Mapping mapping = ((MappingNodeValue)unmarshalNodeValue).getMapping();
                    isIncludedInAttributeGroup = this.unmarshalAttributeGroup.containsAttributeInternal(mapping.getAttributeName());
                }
                try {
                    if(isIncludedInAttributeGroup) {
                        unmarshalNodeValue.endElement(xPathFragment, this);
                    } else {
                        resetStringBuffer();
                    }

                } catch(EclipseLinkException e) {
            if ((null == xmlReader) || (null == xmlReader.getErrorHandler())) {
                        throw e;
                    } else {
                        SAXParseException saxParseException = new SAXParseException(e.getLocalizedMessage(), getDocumentLocator(), e);
                        xmlReader.getErrorHandler().warning(saxParseException);
                    }
                }
            } else {
                XPathNode textNode = xPathNode.getTextNode();

                if (null != textNode && getStringBuffer().length() == 0) {
                    NodeValue textNodeUnmarshalNodeValue = textNode.getUnmarshalNodeValue();
                    if(textNode.isWhitespaceAware()){
                        if (textNodeUnmarshalNodeValue.isMappingNodeValue()) {
                            Mapping mapping = ((MappingNodeValue)textNodeUnmarshalNodeValue).getMapping();
                            if(mapping.isAbstractDirectMapping() && isNil()) {
                                Object nullValue = ((DirectMapping)mapping).getNullValue();
                                if(!(Constants.EMPTY_STRING.equals(nullValue))) {
                                    setAttributeValue(null, mapping);
                                    this.removeNullCapableValue((NullCapableValue)textNodeUnmarshalNodeValue);
                                }
                            } else {
                                textNodeUnmarshalNodeValue.endElement(xPathFragment, this);
                            }
                            setNil(false);
                        }
                    }else{

                       //This means empty tag
                       if(textNodeUnmarshalNodeValue.isMappingNodeValue()) {
                            Mapping mapping = ((MappingNodeValue)textNodeUnmarshalNodeValue).getMapping();
                            if(mapping.isAbstractDirectMapping() && !isNil() && ((DirectMapping)mapping).getNullPolicy().isNullRepresentedByXsiNil()){
                                removeNullCapableValue((NullCapableValue)textNodeUnmarshalNodeValue);
                            }
                        }

                    }
                }
            }
            XPathFragment xPathFragment = xPathNode.getXPathFragment();
            if((null != xPathFragment && xPathFragment.nameIsText()) || (xpathNodeIsMixedContent && xPathNode.getParent() != null)) {
                xPathNode = xPathNode.getParent();
            }

            NodeValue xPathNodeUnmarshalNodeValue = xPathNode.getUnmarshalNodeValue();
            if (null != xPathNodeUnmarshalNodeValue && xPathNodeUnmarshalNodeValue.isContainerValue()) {
                 predictedNextXPathNode = xPathNode;
            } else {
                predictedNextXPathNode = xPathNode.getNextNode();
            }

            if (null != xPathNode.getParent()) {
                xPathNode = xPathNode.getParent();
            }

            xpathNodeIsMixedContent = false;
            unmarshalContext.endElement(this);

            typeQName = null;
            levelIndex--;
            if(isNil() && levelIndex > 0) {
                setNil(false);
            }
            if ((0 == levelIndex) && (null !=parentRecord) && !isSelfRecord()) {
                endDocument();
                // don't endElement on, or pass control to, a 'self' parent
                UnmarshalRecord pRec = parentRecord;
                while (pRec.isSelfRecord()) {
                    pRec = pRec.getParentRecord();
                }
                pRec.endElement(namespaceURI, localName, qName);
                xmlReader.setContentHandler(pRec);
                xmlReader.setLexicalHandler(pRec);
            }

       } catch (EclipseLinkException e) {
            if ((null == xmlReader) || (null == xmlReader.getErrorHandler())) {
                throw e;
            } else {
                Locator locator = xmlReader.getLocator();
                SAXParseException saxParseException = new SAXParseException(null, getDocumentLocator(), e);
                xmlReader.getErrorHandler().warning(saxParseException);
            }
        }
    }

    @Override
    public void endUnmappedElement(String namespaceURI, String localName, String qName) throws SAXException {
        typeQName = null;
        levelIndex--;
        if ((0 == levelIndex) && (null != parentRecord) && !isSelfRecord()) {
            endDocument();
            // don't endElement on, or pass control to, a 'self' parent
            UnmarshalRecord pRec = parentRecord;
            while (pRec.isSelfRecord()) {
                pRec = pRec.getParentRecord();
            }
            pRec.endElement(namespaceURI, localName, qName);
            xmlReader.setContentHandler(pRec);
            xmlReader.setLexicalHandler(pRec);
        }
        setNil(false); // null unmapped element processed. We have to reset nil status
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    if(currentObject == null){
        return;
    }
        try {
            int strBufferInitialLength = -1;
            if (null != selfRecords) {
                strBufferInitialLength = getStringBuffer().length();
                for (int x = 0, selfRecordsSize = selfRecords.size(); x < selfRecordsSize; x++) {
                    UnmarshalRecord selfRecord = selfRecords.get(x);
                    if(selfRecord != null){
                        selfRecord.characters(ch, start, length);
                    } else {
            getFragmentBuilder().characters(ch, start, length);
                    }
                }
            }
            if(-1 != unmappedLevel && unmappedLevel <= levelIndex) {
                return;
            }
            XPathNode textNode = xPathNode.getTextNode();
            if (null == textNode) {
                textNode = xPathNode.getAnyNode();
                if (textNode != null) {
                    xpathNodeIsMixedContent = true;
                    this.xPathFragment.setLocalName(null);
                    this.xPathFragment.setNamespaceURI(null);
                    if (0 == length) {
                        return;
                    }
                }
            }
            if (null != textNode) {
                if(textNode.getUnmarshalNodeValue().isMixedContentNodeValue()) {
                    String tmpString = new String(ch, start, length);
                    if (!textNode.isWhitespaceAware() && tmpString.trim().length() == 0) {
                        return;
                    }
                }
                xPathNode = textNode;
                unmarshalContext.characters(this);
            }

            NodeValue unmarshalNodeValue = xPathNode.getUnmarshalNodeValue();
            if (null != unmarshalNodeValue && !unmarshalNodeValue.isWrapperNodeValue()) {
                if(strBufferInitialLength == -1) {
                    getStringBuffer().append(ch, start, length);
                } else {
                    StrBuffer strBuffer = getStringBuffer();
                    if(strBufferInitialLength == strBuffer.length()) {
                        strBuffer.append(ch, start, length);
                    }
                }
            }
        } catch (EclipseLinkException e) {
            if (null == xmlReader.getErrorHandler()) {
                throw e;
            } else {
                SAXParseException saxParseException = new SAXParseException(null, getDocumentLocator(), e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }
    }

    @Override
    public void characters(CharSequence characters) throws SAXException {
        if(null != characters) {
            String string = characters.toString();
            characters(string.toCharArray(), 0, string.length());
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    /**
     * INTERNAL:
     */
    @Override
    public XPathNode getNonAttributeXPathNode(String namespaceURI, String localName, String qName, Attributes attributes) {
        if (0 == levelIndex) {
            return xPathNode;
        }
        updateXPathFragment(qName, localName, namespaceURI);

        Map<XPathFragment, XPathNode> nonAttributeChildrenMap = xPathNode.getNonAttributeChildrenMap();

        if (null != nonAttributeChildrenMap) {
            XPathNode resultNode;

            if (unmarshaller.isCaseInsensitive()){
                resultNode = getNodeFromLookupTable(nonAttributeChildrenMap, false);
            } else {
                resultNode = nonAttributeChildrenMap.get(xPathFragment);
            }

            XPathNode nonPredicateNode = null;
            if(resultNode != null && resultNode.hasPredicateSiblings()) {
                nonPredicateNode = resultNode;
                resultNode = null;
            }
            if (null == resultNode) {
                // POSITIONAL MAPPING
                int newIndex;
                if (null == this.indexMap) {
                    this.indexMap = new HashMap();
                    newIndex = 1;
                } else {
                    Integer oldIndex = indexMap.get(xPathFragment);
                    if (null == oldIndex) {
                        newIndex = 1;
                    } else {
                        newIndex = oldIndex.intValue() + 1;
                    }
                }
                indexMap.put(xPathFragment, newIndex);
                XPathFragment predicateFragment = new XPathFragment();
                predicateFragment.setNamespaceAware(isNamespaceAware());
                predicateFragment.setNamespaceURI(xPathFragment.getNamespaceURI());
                predicateFragment.setLocalName(xPathFragment.getLocalName());
                predicateFragment.setIndexValue(newIndex);
                resultNode = nonAttributeChildrenMap.get(predicateFragment);
                if (null == resultNode) {
                    predicateFragment.setIndexValue(-1);
                    if(attributes != null){
                        for(int x = 0, length = attributes.getLength(); x<length; x++) {
                            XPathFragment conditionFragment = new XPathFragment();
                            conditionFragment.setLocalName(attributes.getLocalName(x));
                            conditionFragment.setNamespaceURI(attributes.getURI(x));
                            conditionFragment.setAttribute(true);
                            XPathPredicate condition = new XPathPredicate(conditionFragment, attributes.getValue(x));
                            predicateFragment.setPredicate(condition);
                            resultNode = nonAttributeChildrenMap.get(predicateFragment);
                            if(null != resultNode) {
                                break;
                            }
                        }
                    }
                    //if json, check for text wrapper before handing off to the any
                    if(null == resultNode && xPathNode.getTextNode() != null){
                        XPathFragment textWrapperFragment = getTextWrapperFragment();
                        if(textWrapperFragment != null && localName.equals(textWrapperFragment.getLocalName())){
                           resultNode = xPathNode.getTextNode();
                        }
                    }
                    if(null == resultNode && null == nonPredicateNode) {
                        // ANY MAPPING
                        resultNode = xPathNode.getAnyNode();
                    }
                }
            }
            if(resultNode == null && nonPredicateNode != null) {
                return nonPredicateNode;
            }
            return resultNode;
        }
        return null;
    }

    @Override
    public String resolveNamespacePrefix(String prefix) {
        String namespaceURI = getUnmarshalNamespaceResolver().getNamespaceURI(prefix);
        if(null == namespaceURI && null != parentRecord) {
            namespaceURI = parentRecord.resolveNamespacePrefix(prefix);
        }
        return namespaceURI;
    }

    @Override
    public String resolveNamespaceUri(String uri) {
        String prefix = getUnmarshalNamespaceResolver().getPrefix(uri);
        if (null == prefix) {
            if (null != parentRecord) {
                prefix = parentRecord.resolveNamespaceUri(uri);
            }
        }
        return prefix;
    }

    public NodeValue getSelfNodeValueForAttribute(String namespace, String localName) {
        if (this.selfRecords != null) {
            for (int i = 0, selfRecordsSize = selfRecords.size(); i < selfRecordsSize; i++) {
                UnmarshalRecord nestedRecord = selfRecords.get(i);
                if(nestedRecord != null){
                    NodeValue node = nestedRecord.getAttributeChildNodeValue(namespace, localName);
                    if (node != null) {
                        return node;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public NodeValue getAttributeChildNodeValue(String namespace, String localName) {
        Map<XPathFragment, XPathNode> attributeChildrenMap = xPathNode.getAttributeChildrenMap();
        if (attributeChildrenMap != null) {
            XPathNode resultNode;
            xPathFragment.setLocalName(localName);
            xPathFragment.setNamespaceURI(namespace);

            if (unmarshaller.isCaseInsensitive()){
                resultNode = getNodeFromLookupTable(attributeChildrenMap, true);
            } else {
                resultNode = attributeChildrenMap.get(xPathFragment);
            }

            if (resultNode != null) {
                return resultNode.getUnmarshalNodeValue();
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Retrieves the XPathNode by searching in the auxiliary case insensitive lookup table.
     *
     * @param childrenMap Original Map for construction of the auxiliary table.
     * @param isAttribute Determine if searching for an element or an attribute.
     * @return XPathNode object reference, which is also present in the original children map.
     * @since 2.6.0
     */
    private XPathNode getNodeFromLookupTable(Map<XPathFragment, XPathNode> childrenMap, boolean isAttribute) {
        Map<String, XPathNode> lookupTable = xPathNode.getChildrenLookupTable(isAttribute);

        if(!xPathNode.isChildrenLookupTableFilled(isAttribute)){
            this.fillLookupTable(childrenMap, lookupTable);
            xPathNode.setChildrenLookupTableFilled(isAttribute);
        }

        String lowerCaseFragment = xPathFragment.getLocalName().toLowerCase();
        if (!xPathFragment.getChildrenCollisionSet(isAttribute).add(lowerCaseFragment))
            handleCollision(lowerCaseFragment, false);
        return lookupTable.get(lowerCaseFragment);
    }

    /**
     * INTERNAL:
     * Creates an auxiliary lookup table containing lower-cased localNames of XPathFragments.
     *
     * Does NOT pass the Turkey test.
     *
     * For future development: Handle ISO-8859-9 encoding.
     * if (encoding.equals("ISO-8859-9")) {
     *      String auxLocalName = entry.getKey().getLocalName().toLowerCase(Locale.forLanguageTag("tr-TR"));
     * }
     *
     * @param childrenMap Table from which the data is acquired.
     * @param lookupTable Table to which the lower-cased data is stored.
     * @since 2.6.0
     */
    private void fillLookupTable(Map<XPathFragment, XPathNode> childrenMap, Map<String, XPathNode> lookupTable) {
        String lookupName;
        for (Map.Entry<XPathFragment, XPathNode> entry : childrenMap.entrySet()) {
            lookupName = entry.getKey().getLocalName().toLowerCase();
            if (lookupTable.put(lookupName, entry.getValue()) != null){
                handleCollision(lookupName, true);
            }
        }
    }

    /**
     * INTERNAL:
     * Handles collisions, i.e. fragments or fields with the same name, different case.
     *
     * @param lookupName Lookup variant of the localName.
     * @param onXPathNode true - the collision occurred on XPathNode (case for Java fields),
     *                    false - the collision occurred on XPathFragment (case for XML elements/attributes).
     * @since 2.6.0
     */
    private void handleCollision(String lookupName, boolean onXPathNode) {
        StringBuilder sb = new StringBuilder()
                .append(">\nUnmarshalRecordImpl.handleCollision() -->\tCOLLISION on ")
                .append(
                        onXPathNode
                        ? "XPathNode fields by case insensitive localName \""
                        : "XPathFragments by case insensitive localName \""
                ).append(lookupName).append("\".");

//        session.setLogLevel(SessionLog.WARNING); // for debugging
        ((AbstractSession) session).logMessage(CommandProcessor.LOG_WARNING, sb.toString());
    }

    @Override
    public SAXFragmentBuilder getFragmentBuilder() {
        if(this.fragmentBuilder == null){
        fragmentBuilder = new SAXFragmentBuilder(this);
        }
        return fragmentBuilder;
    }

    @Override
    public void setFragmentBuilder(SAXFragmentBuilder builder) {
        this.fragmentBuilder = builder;
    }

    @Override
    public void resetStringBuffer() {
        this.getStringBuffer().reset();
        this.isBufferCDATA = false;
    }

    @Override
    public boolean isBufferCDATA() {
        return isBufferCDATA;
    }

    @Override
    public void comment(char[] data, int start, int length) {
    }

    @Override
    public void startCDATA() {
        if (null != xPathNode && xPathNode.getUnmarshalNodeValue() != null) {
            this.isBufferCDATA = true;
        }
    }

    @Override
    public void endCDATA() {
    }

    @Override
    public void startEntity(String entity) {
    }

    @Override
    public void endEntity(String entity) {
    }

    @Override
    public void startDTD(String a, String b, String c) {
    }

    @Override
    public void endDTD() {
    }

    /**
     * Sets the flag which indicates if this UnmarshalRecord
     * represents a 'self' record
     *
     * @param isSelfRecord true if this record represents
     * 'self', false otherwise
     */
    @Override
    public void setSelfRecord(boolean isSelfRecord) {
        this.isSelfRecord = isSelfRecord;
    }

    /**
     * Indicates if this UnmarshalRecord represents a 'self' record
     *
     * @return true if this record represents 'self', false otherwise
     */
    @Override
    public boolean isSelfRecord() {
        return isSelfRecord;
    }

    @Override
    public int getLevelIndex() {
        return levelIndex;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    @Override
    public void setAttributeValue(Object value, Mapping mapping) {
        this.unmarshalContext.setAttributeValue(this, value, mapping);
    }

    @Override
    public void addAttributeValue(ContainerValue containerValue, Object value) {
        this.unmarshalContext.addAttributeValue(this, containerValue, value);
    }

    @Override
    public void addAttributeValue(ContainerValue containerValue, Object value, Object collection) {
        this.unmarshalContext.addAttributeValue(this, containerValue, value, collection);
    }

    @Override
    public void setAttributeValueNull(ContainerValue containerValue) {
        this.unmarshalContext.setAttributeValue(this, null, containerValue.getMapping());
        int containerIndex = containerValue.getIndex();
        populatedContainerValues.remove(containerValue);
        containerInstances[containerIndex] = null;
    }

    @Override
    public void reference(Reference reference) {
        this.unmarshalContext.reference(reference);
    }

    @Override
    public void unmappedContent() {
        if(this.xPathNode.getParent() != null) {
            xPathNode = xPathNode.getParent();
        }
        this.unmarshalContext.unmappedContent(this);
    }

    @Override
    public UnmarshalRecord getChildUnmarshalRecord(ObjectBuilder treeObjectBuilder) {
    if(childRecord != null && !childRecord.isSelfRecord()){
        childRecord.initialize(treeObjectBuilder);
            childRecord.setParentRecord(this);
            return childRecord;
    }else{
        childRecord = new UnmarshalRecordImpl(treeObjectBuilder, referenceResolver);
        childRecord.setSession(session);
            childRecord.setUnmarshaller(unmarshaller);
            childRecord.setTextWrapperFragment(textWrapperFragment);
            childRecord.setXMLReader(this.xmlReader);
            childRecord.setFragmentBuilder(fragmentBuilder);
            childRecord.setUnmarshalNamespaceResolver(unmarshalNamespaceResolver);
            childRecord.setParentRecord(this);
    }
        return childRecord;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller; //super.setUnmarshaller(unmarshaller);
        if(xPathFragment != null){
        xPathFragment.setNamespaceAware(isNamespaceAware());
        }
    }

    /**
     * INTERNAL
     * Returns a Map of any prefix mappings that were made before the most recent start
     * element event. This Map is used so the prefix mappings can be passed along to a
     * fragment builder in the event that the element in question is going to be unmarshalled
     * as a Node.
     */
    @Override
    public Map<String, String> getPrefixesForFragment() {
    if(prefixesForFragment == null){
        prefixesForFragment = new HashMap<>();
    }

        return prefixesForFragment;
    }


    @Override
    public char getNamespaceSeparator(){
    return xmlReader.getNamespaceSeparator();
    }

    @Override
    public void setTextWrapperFragment(XPathFragment newTextWrapperFragment) {
    textWrapperFragment = newTextWrapperFragment;
    }

    @Override
    public XPathFragment getTextWrapperFragment() {
    if(xmlReader.getMediaType() .isApplicationJSON()){
        if(textWrapperFragment == null){
            textWrapperFragment = new XPathFragment();
            textWrapperFragment.setLocalName(unmarshaller.getValueWrapper());
            textWrapperFragment.setNamespaceAware(isNamespaceAware());
            textWrapperFragment.setNamespaceSeparator(getNamespaceSeparator());
        }
        return textWrapperFragment;
    }
    return null;
    }

    /**
     * INTERNAL:
     * If the UnmarshalRecord has a ReferenceResolver, tell it to resolve its
     * references.
     * @since EclipseLink 2.5.0
     */
    @Override
    public void resolveReferences(CoreAbstractSession abstractSession, IDResolver idResolver) {
        if(null != referenceResolver) {
            referenceResolver.resolveReferences(abstractSession, idResolver, unmarshaller.getErrorHandler());
        }
    }

    /**
     * INTERNAL:
     * @since EclipseLink 2.5.0
     */
    @Override
    public Root createRoot() {
        return unmarshaller.createRoot();
    }

    /**
     * WHAT ABOUT THESE?
     */

    private Field convertToXMLField(CoreField key) {
        return (Field) key;
    }

    @Override
    public CoreAbstractSession getSession() {
        return session;
    }

    @Override
    public Unmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    @Override
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    @Override
    public Object getCurrentObject() {
        return currentObject;
    }

    @Override
    public XPathQName getLeafElementType() {
        return leafElementType;
    }

    @Override
    public void setCurrentObject(Object object) {
        this.currentObject = object;
    }

    @Override
    public void setLeafElementType(QName type) {
        if (type != null) {
            setLeafElementType(new XPathQName(type, isNamespaceAware()));
        } else {
            setLeafElementType((XPathQName) null);
        }
    }

    public void setLeafElementType(XPathQName type) {
        leafElementType = type;
    }

    @Override
    public void setSession(CoreAbstractSession session) {
        this.session = session;
    }

    @Override
    public CoreAttributeGroup getUnmarshalAttributeGroup() {
        return unmarshalAttributeGroup;
    }

    @Override
    public void setUnmarshalAttributeGroup(CoreAttributeGroup unmarshalAttributeGroup) {
        this.unmarshalAttributeGroup = unmarshalAttributeGroup;
    }

    /**
     * @since EclipseLink 2.6.0
     */
    @Override
    public ConversionManager getConversionManager() {
        if(null == conversionManager) {
            conversionManager = (ConversionManager) session.getDatasourcePlatform().getConversionManager();
        }
        return conversionManager;
    }

}
