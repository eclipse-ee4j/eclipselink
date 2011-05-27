/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.XPathPredicate;
import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.MappingNodeValue;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.SAXFragmentBuilder;
import org.eclipse.persistence.internal.oxm.StrBuffer;
import org.eclipse.persistence.internal.oxm.TreeObjectBuilder;
import org.eclipse.persistence.internal.oxm.XMLChoiceCollectionMappingUnmarshalNodeValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.record.ExtendedContentHandler;
import org.eclipse.persistence.internal.oxm.record.ObjectUnmarshalContext;
import org.eclipse.persistence.internal.oxm.record.SequencedUnmarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmappedContentHandlerWrapper;
import org.eclipse.persistence.internal.oxm.record.UnmarshalContext;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLUnmarshalListener;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler;
import org.eclipse.persistence.oxm.unmapped.DefaultUnmappedContentHandler;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.namespaces.StackUnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceResolver;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;

/**
 * <p><b>Purpose:</b>Provide an implementation of ContentHandler that is used by TopLink OXM to
 * build mapped Java Objects from SAX events.
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement the ContentHandler and LexicalHandler interfaces</li>
 * <li>Make calls into the appropriate NodeValues based on the incoming SAXEvents</li>
 * <li>Make callbacks into XMLReader for newObject events</li>
 * <li>Maintain a map of Collections to be populated for collection mappings.</li>
 *
 * @see org.eclipse.persistence.internal.oxm.XPathNode
 * @see org.eclipse.persistence.internal.oxm.NodeValue
 * @see org.eclipse.persistence.internal.oxm.TreeObjectBuilder
 * @author bdoughan
 *
 */
public class UnmarshalRecord extends XMLRecord implements ExtendedContentHandler, LexicalHandler {
    public static final UnmappedContentHandler DEFAULT_UNMAPPED_CONTENT_HANDLER = new DefaultUnmappedContentHandler();
    protected XMLReader xmlReader;
    private TreeObjectBuilder treeObjectBuilder;
    private XPathFragment xPathFragment;
    private XPathNode xPathNode;
    private int levelIndex;
    private UnmarshalRecord childRecord;
    protected UnmarshalRecord parentRecord;
    private DOMRecord transformationRecord;
    private List<UnmarshalRecord> selfRecords;
    private Map<XPathFragment, Integer> indexMap;
    private List<NullCapableValue> nullCapableValues;
    private Map<ContainerValue, Object> containersMap;
    private boolean isBufferCDATA;
    private Attributes attributes;
    private QName typeQName;
    protected String rootElementLocalName;
    protected String rootElementName;
    protected String rootElementNamespaceUri;
    private SAXFragmentBuilder fragmentBuilder;
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

    protected List<UnmarshalRecord> childRecordPool;

    public UnmarshalRecord(TreeObjectBuilder treeObjectBuilder) {
        super();
        this.xPathFragment = new XPathFragment();
        this.childRecordPool = new ArrayList<UnmarshalRecord>();
        initialize(treeObjectBuilder);
    }

    protected UnmarshalRecord initialize(TreeObjectBuilder treeObjectBuilder) {
        this.isBufferCDATA = false;
        this.treeObjectBuilder = treeObjectBuilder;
        if (null != treeObjectBuilder) {
            this.xPathNode = treeObjectBuilder.getRootXPathNode();
            if (null != treeObjectBuilder.getNullCapableValues()) {
                this.nullCapableValues = new ArrayList<NullCapableValue>(treeObjectBuilder.getNullCapableValues());
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
        if(null != containersMap) {
            containersMap.clear();
        }
        isBufferCDATA = false;
        attributes = null;
        typeQName = null;
        isSelfRecord = false;
        unmarshalContext = null;
        isXsiNil = false;
        this.childRecordPool.add(this);
        unmappedLevel = -1;
    }

    @Override
    public String getLocalName() {
        return rootElementLocalName;
    }

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

    public Element getDOM() {
        throw XMLMarshalException.operationNotSupported("getDOM");
    }

    public String transformToXML() {
        throw XMLMarshalException.operationNotSupported("transformToXML");
    }

    public XMLReader getXMLReader() {
        return this.xmlReader;
    }

    public void setXMLReader(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

    public UnmarshalRecord getChildRecord() {
        return this.childRecord;
    }

    public void setChildRecord(UnmarshalRecord childRecord) {
        this.childRecord = childRecord;
        if (null != childRecord) {
            childRecord.setParentRecord(this);
        }
    }

    public UnmarshalRecord getParentRecord() {
        return this.parentRecord;
    }

    /**
     * Return the root element's prefix qualified name
     */
    public String getRootElementName() {
        return rootElementName;
    }

    public void setRootElementName(String qName) {
        this.rootElementName = qName;
    }

    /**
     * Return the root element's namespace URI
     */
    public String getRootElementNamespaceUri() {
        return rootElementNamespaceUri;
    }

    public void setRootElementNamespaceUri(String uri) {
        this.rootElementNamespaceUri = uri;
    }

    public void setParentRecord(UnmarshalRecord parentRecord) {
        this.parentRecord = parentRecord;
    }

    public DOMRecord getTransformationRecord() {
        return this.transformationRecord;
    }

    public void setTransformationRecord(DOMRecord transformationRecord) {
        this.transformationRecord = transformationRecord;
    }

    public UnmarshalNamespaceResolver getUnmarshalNamespaceResolver() {
        if(null == unmarshalNamespaceResolver) {
            this.unmarshalNamespaceResolver = new StackUnmarshalNamespaceResolver();
        }
        return this.unmarshalNamespaceResolver;
    }

    public void setUnmarshalNamespaceResolver(UnmarshalNamespaceResolver anUnmarshalNamespaceResolver) {
        this.unmarshalNamespaceResolver = anUnmarshalNamespaceResolver;
    }

    public List getNullCapableValues() {
        if (null == nullCapableValues) {
            this.nullCapableValues = new ArrayList<NullCapableValue>();
        }
        return this.nullCapableValues;
    }

    public void removeNullCapableValue(NullCapableValue nullCapableValue) {
        if(null != nullCapableValues) {
            nullCapableValues.remove(nullCapableValue);
        }
    }

    public Object getContainerInstance(ContainerValue containerValue) {
        if (null == containersMap) {
            return null;
        }
        return containersMap.get(containerValue);
    }

    /**
     * PUBLIC:
     * Gets the encoding for this document. Only set on the root-level UnmarshalRecord
     * @return a String representing the encoding for this doc
     */
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
    public String getVersion() {
        return version;
    }

    /**
     * INTERNAL:
     */
    public void setVersion(String version) {
        this.version = version;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getNoNamespaceSchemaLocation() {
        return noNamespaceSchemaLocation;
    }

    public void setNoNamespaceSchemaLocation(String location) {
        this.noNamespaceSchemaLocation = location;
    }

    protected StrBuffer getStringBuffer() {
        return getUnmarshaller().getStringBuffer();
    }

    public CharSequence getCharacters() {
        return getUnmarshaller().getStringBuffer();
    }

    public Attributes getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public QName getTypeQName() {
        return this.typeQName;
    }

    public void setTypeQName(QName typeQName) {
        this.typeQName = typeQName;
    }

    public void setDocumentLocator(Locator locator) {
        if ((this.getParentRecord() == null) && locator instanceof Locator2) {
            Locator2 loc = (Locator2)locator;
            this.setEncoding(loc.getEncoding());
            this.setVersion(loc.getXMLVersion());
        }
    }

    public Object get(DatabaseField key) {
        XMLField xmlField = this.convertToXMLField(key);
        XPathFragment lastFragment = xmlField.getLastXPathFragment();
        NamespaceResolver namespaceResolver = xmlField.getNamespaceResolver();
        String namespaceURI = XMLConstants.EMPTY_STRING;
        if (null != namespaceResolver) {
            namespaceURI = namespaceResolver.resolveNamespacePrefix(lastFragment.getPrefix());
            if (null == namespaceURI) {
                namespaceURI = XMLConstants.EMPTY_STRING;
            }
        }
        return attributes.getValue(namespaceURI, lastFragment.getLocalName());
    }

    public XPathNode getXPathNode() {
        return xPathNode;
    }

    public XMLDescriptor getDescriptor() {
        return (XMLDescriptor) treeObjectBuilder.getDescriptor();
    }

    public UnmarshalContext getUnmarshalContext() {
        return unmarshalContext;
    }

    public void setUnmarshalContext(UnmarshalContext unmarshalContext) {
        this.unmarshalContext = unmarshalContext;
    }

    public boolean isNil() {
        return this.isXsiNil;
    }

    public void setNil(boolean nil) {
        this.isXsiNil = nil;
    }

    public void startDocument() throws SAXException {
    }
    
    private void initializeRecord(Attributes attrs) throws SAXException{
    	XMLDescriptor xmlDescriptor = (XMLDescriptor) treeObjectBuilder.getDescriptor();    	
    	if(!xmlDescriptor.hasInheritance() || xmlDescriptor.getInheritancePolicy().getClassIndicatorField() == null){
    		initialize((TreeObjectBuilder)xmlDescriptor.getObjectBuilder());
    		initializeRecord((XMLMapping)null);
        	return;
        }
    	this.setAttributes(attrs);
    	Class classValue = xmlDescriptor.getInheritancePolicy().classFromRow(this, session);
    	 if (classValue == null) {
             // no xsi:type attribute - look for type indicator on the default root element
             QName leafElementType = xmlDescriptor.getDefaultRootElementType();

             // if we have a user-set type, try to get the class from the inheritance policy
             if (leafElementType != null) {
                 Object indicator = xmlDescriptor.getInheritancePolicy().getClassIndicatorMapping().get(leafElementType);
                 if(indicator != null) {
                     classValue = (Class)indicator;
                 }
             }
         }
         if (classValue != null) {
             xmlDescriptor = (XMLDescriptor)session.getDescriptor(classValue);             
         }
         initialize((TreeObjectBuilder)xmlDescriptor.getObjectBuilder());         
         initializeRecord((XMLMapping)null);
    }
    
    public void initializeRecord(XMLMapping selfRecordMapping) throws SAXException {
    	try {
            XMLDescriptor xmlDescriptor = (XMLDescriptor) treeObjectBuilder.getDescriptor();
            if(xmlDescriptor.isSequencedObject()) {
                unmarshalContext = new SequencedUnmarshalContext();
            } else {
                unmarshalContext = ObjectUnmarshalContext.getInstance();
            }

            Object object = this.xmlReader.getCurrentObject(session, selfRecordMapping);
            if (object == null) {
                object = treeObjectBuilder.buildNewInstance();
            }
            this.setCurrentObject(object);
            XMLUnmarshalListener xmlUnmarshalListener = unmarshaller.getUnmarshalListener();
            if (null != xmlUnmarshalListener) {
                if (null == this.parentRecord) {
                    xmlUnmarshalListener.beforeUnmarshal(object, null);
                } else {
                    xmlUnmarshalListener.beforeUnmarshal(object, parentRecord.getCurrentObject());
                }
            }
            if (null == parentRecord) {
                this.xmlReader.newObjectEvent(object, null, selfRecordMapping);
            } else {
                this.xmlReader.newObjectEvent(object, parentRecord.getCurrentObject(), selfRecordMapping);
            }
            List containerValues = treeObjectBuilder.getContainerValues();
            if (null != containerValues) {
                containersMap = new HashMap(containerValues.size());
                for (int x = 0, containerValuesSize = containerValues.size(); x < containerValuesSize; x++) {
                    ContainerValue containerValue = (ContainerValue)containerValues.get(x);
                    Object containerInstance = null;
                    if(containerValue.getReuseContainer()) {
                        containerInstance = containerValue.getMapping().getAttributeAccessor().getAttributeValueFromObject(object);
                    }
                    if(null == containerInstance) {
                        containerInstance = containerValue.getContainerInstance();
                    }
                    containersMap.put(containerValue, containerInstance);
                    if(containerValue.getMapping() instanceof XMLChoiceCollectionMapping) {
                        XMLChoiceCollectionMappingUnmarshalNodeValue nodeValue = (XMLChoiceCollectionMappingUnmarshalNodeValue)containerValue;
                        for(NodeValue next:nodeValue.getAllNodeValues()) {
                            NodeValue nestedNodeValue = ((XMLChoiceCollectionMappingUnmarshalNodeValue)next).getChoiceElementNodeValue();
                            containersMap.put((ContainerValue)nestedNodeValue, containerInstance);
                        }
                    }
                }
            }

            if (null != xPathNode.getSelfChildren()) {
                int selfChildrenSize = xPathNode.getSelfChildren().size();
                selfRecords = new ArrayList<UnmarshalRecord>(selfChildrenSize);
                for (int x = 0; x < selfChildrenSize; x++) {
                    XPathNode selfNode = xPathNode.getSelfChildren().get(x);
                    if (null != selfNode.getNodeValue()) {
                        selfRecords.add(selfNode.getNodeValue().buildSelfRecord(this, attributes));
                    }
                }
            }
        } catch (EclipseLinkException e) {
            if (null == xmlReader.getErrorHandler()) {
                throw e;
            } else {
                SAXParseException saxParseException = new SAXParseException(null, null, null, 0, 0, e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }
    }

    public void endDocument() throws SAXException {
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

        try {
            // PROCESS COLLECTION MAPPINGS
            if (null != containersMap) {
                for(Entry<ContainerValue, Object> entry : containersMap.entrySet()) {
                    entry.getKey().setContainerInstance(currentObject, entry.getValue());
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
            List transformationMappings = treeObjectBuilder.getTransformationMappings();
            if (null != transformationMappings) {
                ReadObjectQuery query = new ReadObjectQuery();
                query.setSession(session);
                for (int x = 0, transformationMappingsSize = transformationMappings.size(); x < transformationMappingsSize; x++) {
                    AbstractTransformationMapping transformationMapping = (AbstractTransformationMapping)transformationMappings.get(x);
                    transformationMapping.readFromRowIntoObject(transformationRecord, null, currentObject, null, query, session, true);
                }
            }

            if (getUnmarshaller().getUnmarshalListener() != null) {
                if (this.parentRecord != null) {
                    getUnmarshaller().getUnmarshalListener().afterUnmarshal(currentObject, parentRecord.getCurrentObject());
                } else {
                    getUnmarshaller().getUnmarshalListener().afterUnmarshal(currentObject, null);
                }
            }

            // HANDLE POST BUILD EVENTS
            ClassDescriptor xmlDescriptor = treeObjectBuilder.getDescriptor();
            if (xmlDescriptor.getEventManager().hasAnyEventListeners()) {
                DescriptorEvent event = new DescriptorEvent(currentObject);
                event.setSession(session);
                event.setRecord(this);
                event.setEventCode(DescriptorEventManager.PostBuildEvent);
                xmlDescriptor.getEventManager().executeEvent(event);
            }
        } catch (EclipseLinkException e) {
            if (null == xmlReader.getErrorHandler()) {
                throw e;
            } else {
                SAXParseException saxParseException = new SAXParseException(null, null, null, 0, 0, e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }

        // if the object has any primary key fields set, add it to the cache
        if (session.isUnitOfWork()) {
            ClassDescriptor xmlDescriptor = treeObjectBuilder.getDescriptor();
            if(null != xmlDescriptor) {
                List primaryKeyFields = xmlDescriptor.getPrimaryKeyFields();
                int primaryKeyFieldsSize = primaryKeyFields.size();
                if (primaryKeyFieldsSize > 0) {
                    Object pk = treeObjectBuilder.extractPrimaryKeyFromObject(currentObject, session);
                    for (int x=0; x<primaryKeyFieldsSize; x++) {
                        Object value = ((CacheId)pk).getPrimaryKey()[x];
                        if (null == value) {
                            XMLField pkField = (XMLField) xmlDescriptor.getPrimaryKeyFields().get(x);
                            ((CacheId)pk).set(x, getUnmarshaller().getXMLContext().getValueByXPath(currentObject, pkField.getXPath(), pkField.getNamespaceResolver(), Object.class));
                        }
                    }
                    CacheKey key = session.getIdentityMapAccessorInstance().acquireDeferredLock(pk, xmlDescriptor.getJavaClass(), xmlDescriptor);
                    key.setRecord(this);
                    key.setObject(currentObject);
                    key.releaseDeferredLock();
                }
            }
        }

        if(null != parentRecord) {
            reset();
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        getUnmarshalNamespaceResolver().push(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        getUnmarshalNamespaceResolver().pop(prefix);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    	if(getCurrentObject() == null){
    		initializeRecord(atts);
    	}
    	
        if((null != xPathNode.getXPathFragment() && xPathNode.getXPathFragment().nameIsText()) || xpathNodeIsMixedContent) {
            xpathNodeIsMixedContent = false;
            NodeValue xPathNodeUnmarshalNodeValue = xPathNode.getUnmarshalNodeValue();
            if (null != xPathNodeUnmarshalNodeValue) {
                xPathNodeUnmarshalNodeValue.endElement(xPathFragment, this);
                if (xPathNode.getParent() != null) {
                    xPathNode = xPathNode.getParent();
                }
            }
        }

        // set the root element's local name and namespace prefix and look for
        // schema locations etc.
        if (null == rootElementName  && null == rootElementLocalName) {
            rootElementLocalName = localName;
            rootElementName = qName;
            rootElementNamespaceUri = namespaceURI;
            schemaLocation = atts.getValue(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_LOCATION);
            noNamespaceSchemaLocation = atts.getValue(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.NO_NS_SCHEMA_LOCATION);
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

            XPathNode node = getNonAttributeXPathNode(namespaceURI, localName, qName, atts);
            if (null == node) {
                NodeValue parentNodeValue = xPathNode.getUnmarshalNodeValue();
                if ((null == xPathNode.getXPathFragment()) && (parentNodeValue != null)) {
                    XPathFragment parentFragment = new XPathFragment();
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

                isXsiNil = atts.getIndex(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_NIL_ATTRIBUTE) >= 0;
                NodeValue nodeValue = node.getUnmarshalNodeValue();
                if (null != nodeValue) {
                    if (!nodeValue.startElement(xPathFragment, this, atts)) {
                        // UNMAPPED CONTENT
                        startUnmappedElement(namespaceURI, localName, qName, atts);
                        return;
                    }
                }

                //Handle Attributes
                for (int i = 0, size=atts.getLength(); i < size; i++) {
                    String attNamespace = atts.getURI(i);
                    String attLocalName = atts.getLocalName(i);
                    String value = atts.getValue(i);
                    NodeValue attributeNodeValue = null;

                    // Some parsers don't set the URI/local name for namespace
                    // attributes
                    if ((attLocalName == null) || (attLocalName.length() == 0)) {
                        String qname = atts.getQName(i);
                        if ((qname != null) && (qname.length() > 0)) {
                            int idx = qname.indexOf(XMLConstants.COLON);
                            if(idx > 0){
                            	attLocalName = qname.substring(idx + 1, qname.length());
                            	String attPrefix = qname.substring(0, idx);
                            	if (attPrefix.equals(XMLConstants.XMLNS)){
                                    attNamespace = XMLConstants.XMLNS_URL;
                                }
                            }else{
                            	attLocalName = qname;
                            	if(attLocalName.equals(XMLConstants.XMLNS)){
                            		attNamespace = XMLConstants.XMLNS_URL;
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
                        if (attributeNodeValue != null) {
                            attributeNodeValue.attribute(this, attNamespace, attLocalName, value);
                        } else {
                            if (xPathNode.getAnyAttributeNodeValue() != null) {
                                xPathNode.getAnyAttributeNodeValue().attribute(this, attNamespace, attLocalName, value);
                            }
                        }
                    }
                }
            }
        } catch (EclipseLinkException e) {
            if ((null == xmlReader) || (null == xmlReader.getErrorHandler())) {
                throw e;
            } else {
                SAXParseException saxParseException = new SAXParseException(null, null, null, 0, 0, e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }
    }

    public void startUnmappedElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if ((null != selfRecords) || (null == xmlReader) || isSelfRecord()) {
            if(-1 == unmappedLevel) {
                this.unmappedLevel = this.levelIndex;
            }
            return;
        }
        Class unmappedContentHandlerClass = getUnmarshaller().getUnmappedContentHandlerClass();
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
            if (null != xPathNode.getUnmarshalNodeValue()) {
                xPathNode.getUnmarshalNodeValue().endElement(xPathFragment, this);
            } else {
                XPathNode textNode = xPathNode.getTextNode();

                if (null != textNode && textNode.isWhitespaceAware() && getStringBuffer().length() == 0) {
                    NodeValue textNodeUnmarshalNodeValue = textNode.getUnmarshalNodeValue();
                    if (!isXsiNil) {
                        if (textNodeUnmarshalNodeValue.isMappingNodeValue()) {
                            textNodeUnmarshalNodeValue.endElement(xPathFragment, this);
                        }
                    } else {
                        if(textNodeUnmarshalNodeValue.isMappingNodeValue()) {
                            DatabaseMapping mapping = ((MappingNodeValue)textNodeUnmarshalNodeValue).getMapping();
                            if(mapping.isAbstractDirectMapping()) {
                                Object nullValue = ((AbstractDirectMapping)mapping).getNullValue();
                                if(!(XMLConstants.EMPTY_STRING.equals(nullValue))) {
                                    setAttributeValue(null, mapping);
                                    this.removeNullCapableValue((NullCapableValue)textNodeUnmarshalNodeValue);
                                }
                            }
                            isXsiNil = false;
                        }
                    }
                }
            }
            XPathFragment xPathFragment = xPathNode.getXPathFragment();
            if(null != xPathFragment && xPathFragment.nameIsText()) {
                xPathNode = xPathNode.getParent();
            }
            if (null != xPathNode.getParent()) {
                xPathNode = xPathNode.getParent();
            }

            xpathNodeIsMixedContent = false;
            unmarshalContext.endElement(this);

            typeQName = null;
            levelIndex--;
            if ((0 == levelIndex) && (null != getParentRecord()) && !isSelfRecord()) {
                endDocument();
                // don't endElement on, or pass control to, a 'self' parent
                UnmarshalRecord pRec = getParentRecord();
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
                SAXParseException saxParseException = new SAXParseException(null, null, null, 0, 0, e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }
    }
    
    public void endUnmappedElement(String namespaceURI, String localName, String qName) throws SAXException {
        typeQName = null;
        levelIndex--;
        if ((0 == levelIndex) && (null != parentRecord) && !isSelfRecord()) {
            endDocument();
            // don't endElement on, or pass control to, a 'self' parent
            UnmarshalRecord pRec = parentRecord;
            while (pRec.isSelfRecord()) {
                pRec = pRec.parentRecord;
            }
            pRec.endElement(namespaceURI, localName, qName);
            xmlReader.setContentHandler(pRec);
            xmlReader.setLexicalHandler(pRec);
        }
       
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            if (null != selfRecords) {
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
                    String tmpString = new String(ch, start, length);
                    if (!textNode.isWhitespaceAware() && tmpString.trim().length() == 0) {
                        return;
                    }
                }
            }

            if (null != textNode) {
                xPathNode = textNode;
                unmarshalContext.characters(this);
            }
            if (null != xPathNode.getUnmarshalNodeValue()) {
                getStringBuffer().append(ch, start, length);
            }
        } catch (EclipseLinkException e) {
            if (null == xmlReader.getErrorHandler()) {
                throw e;
            } else {
                SAXParseException saxParseException = new SAXParseException(null, null, null, 0, 0, e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }
    }

    public void characters(CharSequence characters) throws SAXException {
        if(null != characters) {
            String string = characters.toString();
            characters(string.toCharArray(), 0, string.length());
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    protected XPathNode getNonAttributeXPathNode(String namespaceURI, String localName, String qName, Attributes attributes) {
        if (0 == levelIndex) {
            return xPathNode;
        }
        if(namespaceURI !=null && namespaceURI.length() == 0){
            xPathFragment.setLocalName(qName);
            xPathFragment.setNamespaceURI(null);
        } else {
            xPathFragment.setLocalName(localName);
            xPathFragment.setNamespaceURI(namespaceURI);
        }

        XPathNode resultNode = null;
        Map nonAttributeChildrenMap = xPathNode.getNonAttributeChildrenMap();
        if (null != nonAttributeChildrenMap) {
            resultNode = (XPathNode)nonAttributeChildrenMap.get(xPathFragment);
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
                predicateFragment.setNamespaceURI(xPathFragment.getNamespaceURI());
                predicateFragment.setLocalName(xPathFragment.getLocalName());
                predicateFragment.setIndexValue(newIndex);
                resultNode = (XPathNode)nonAttributeChildrenMap.get(predicateFragment);
                if (null == resultNode) {
                    predicateFragment.setIndexValue(-1);
                    for(int x = 0, length = attributes.getLength(); x<length; x++) {
                        XPathFragment conditionFragment = new XPathFragment();
                        conditionFragment.setLocalName(attributes.getLocalName(x));
                        conditionFragment.setNamespaceURI(attributes.getURI(x));
                        conditionFragment.setAttribute(true);
                        XPathPredicate condition = new XPathPredicate(conditionFragment, attributes.getValue(x));
                        predicateFragment.setPredicate(condition);
                        resultNode = (XPathNode) nonAttributeChildrenMap.get(predicateFragment);
                        if(null != resultNode) {
                            break;
                        }
                    }
                    if(null == resultNode) {
                        // ANY MAPPING
                        resultNode = xPathNode.getAnyNode();
                    }
                }
            }
            return resultNode;
        }
        return null;
    }

    public String resolveNamespacePrefix(String prefix) {
        String namespaceURI = getUnmarshalNamespaceResolver().getNamespaceURI(prefix);
        if(null == namespaceURI && null != getParentRecord()) {
            namespaceURI = getParentRecord().resolveNamespacePrefix(prefix);
        }
        return namespaceURI;
    }

    public String resolveNamespaceUri(String uri) {
        String prefix = getUnmarshalNamespaceResolver().getPrefix(uri);
        if (null == prefix) {
            if (null != getParentRecord()) {
                prefix = getParentRecord().resolveNamespaceUri(uri);
            }
        }
        return prefix;
    }

    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write(Helper.getShortClassName(getClass()));
        writer.write("()");
        return writer.toString();
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

    public NodeValue getAttributeChildNodeValue(String namespace, String localName) {
        Map attributeChildrenMap = xPathNode.getAttributeChildrenMap();
        if (attributeChildrenMap != null) {
            xPathFragment.setLocalName(localName);
            if(namespace != null && namespace.length() == 0){
                xPathFragment.setNamespaceURI(null);
            } else {
                xPathFragment.setNamespaceURI(namespace);
            }
            XPathNode node = (XPathNode)attributeChildrenMap.get(xPathFragment);
            if (node != null) {
                return node.getUnmarshalNodeValue();
            }
        }
        return null;
    }

    public SAXFragmentBuilder getFragmentBuilder() {
        if(this.fragmentBuilder == null){
        	fragmentBuilder = new SAXFragmentBuilder(this);
        }
        return fragmentBuilder;
    }

    public void setFragmentBuilder(SAXFragmentBuilder builder) {
        this.fragmentBuilder = builder;
    }

    public void resetStringBuffer() {
        this.getStringBuffer().reset();
        this.isBufferCDATA = false;
    }

    public boolean isBufferCDATA() {
        return isBufferCDATA;
    }

    public void comment(char[] data, int start, int length) {
    }

    public void startCDATA() {
        if (xPathNode.getUnmarshalNodeValue() != null) {
            this.isBufferCDATA = true;
        }
    }

    public void endCDATA() {
    }

    public void startEntity(String entity) {
    }

    public void endEntity(String entity) {
    }

    public void startDTD(String a, String b, String c) {
    }

    public void endDTD() {
    }

    /**
     * Sets the flag which indicates if this UnmarshalRecord
     * represents a 'self' record
     *
     * @param isSelfRecord true if this record represents
     * 'self', false otherwise
     */
    public void setSelfRecord(boolean isSelfRecord) {
        this.isSelfRecord = isSelfRecord;
    }

    /**
     * Indicates if this UnmarshalRecord represents a 'self' record
     *
     * @return true if this record represents 'self', false otherwise
     */
    public boolean isSelfRecord() {
        return isSelfRecord;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setAttributeValue(Object value, DatabaseMapping mapping) {
        this.unmarshalContext.setAttributeValue(this, value, mapping);
    }

    public void addAttributeValue(ContainerValue containerValue, Object value) {
        this.unmarshalContext.addAttributeValue(this, containerValue, value);
    }

    public void addAttributeValue(ContainerValue containerValue, Object value, Object collection) {
        this.unmarshalContext.addAttributeValue(this, containerValue, value, collection);
    }

    public void reference(Reference reference) {
        this.unmarshalContext.reference(reference);
    }

    public void unmappedContent() {
        if(this.xPathNode.getParent() != null) {
            xPathNode = xPathNode.getParent();
        }
        this.unmarshalContext.unmappedContent(this);
    }

    public UnmarshalRecord getChildUnmarshalRecord(TreeObjectBuilder treeObjectBuilder) {
        if(childRecordPool.isEmpty()) {
            UnmarshalRecord childRecord = (UnmarshalRecord) treeObjectBuilder.createRecord(session);
            childRecord.setUnmarshaller(unmarshaller);
            childRecord.session = this.session;
            childRecord.xmlReader = this.xmlReader;
            childRecord.setFragmentBuilder(fragmentBuilder);
            childRecord.setUnmarshalNamespaceResolver(this.getUnmarshalNamespaceResolver());
            childRecord.childRecordPool = this.childRecordPool;
            return childRecord;
        } else {
            return childRecordPool.remove(childRecordPool.size() - 1).initialize(treeObjectBuilder);
        }
    }

}