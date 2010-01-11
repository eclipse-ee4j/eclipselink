/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Stack;
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.SAXFragmentBuilder;
import org.eclipse.persistence.internal.oxm.StrBuffer;
import org.eclipse.persistence.internal.oxm.TreeObjectBuilder;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.record.ObjectUnmarshalContext;
import org.eclipse.persistence.internal.oxm.record.SequencedUnmarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmappedContentHandlerWrapper;
import org.eclipse.persistence.internal.oxm.record.UnmarshalContext;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler;
import org.eclipse.persistence.oxm.unmapped.DefaultUnmappedContentHandler;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
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
public class UnmarshalRecord extends XMLRecord implements ContentHandler, LexicalHandler {
    protected static final String EMPTY_STRING = "";
    public static final UnmappedContentHandler DEFAULT_UNMAPPED_CONTENT_HANDLER = new DefaultUnmappedContentHandler();
    private XMLReader xmlReader;
    private TreeObjectBuilder treeObjectBuilder;
    private XPathFragment xPathFragment;
    private XPathNode xPathNode;
    private int levelIndex;
    private UnmarshalRecord childRecord;
    private UnmarshalRecord parentRecord;
    private DOMRecord transformationRecord;
    private List selfRecords;
    private Map indexMap;
    private Map namespaceMap;
    private Map uriToPrefixMap;
    private List nullCapableValues;
    private Map containersMap;
    private StrBuffer stringBuffer;
    private boolean isBufferCDATA;
    private Attributes attributes;
    private QName typeQName;
    private String rootElementName;
    private String rootElementNamespaceUri;
    private XMLUnmarshaller unmarshaller;
    private SAXFragmentBuilder fragmentBuilder;
    private String encoding;
    private String version;
    private String schemaLocation;
    private String noNamespaceSchemaLocation;
    private boolean isSelfRecord;
    private UnmarshalContext unmarshalContext;

    public UnmarshalRecord(TreeObjectBuilder treeObjectBuilder) {
        super();
        this.levelIndex = 0;
        this.xPathFragment = new XPathFragment();
        this.stringBuffer = new StrBuffer();
        this.isBufferCDATA = false;
        this.treeObjectBuilder = treeObjectBuilder;
        if (null != treeObjectBuilder) {
            this.xPathNode = treeObjectBuilder.getRootXPathNode();
            if (null != treeObjectBuilder.getNullCapableValues()) {
                nullCapableValues = new ArrayList();
                nullCapableValues.addAll(treeObjectBuilder.getNullCapableValues());
            }
        }
        fragmentBuilder = new SAXFragmentBuilder(this);
        isSelfRecord = false;
    }

    public String getLocalName() {
        throw XMLMarshalException.operationNotSupported("getLocalName");
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
            childRecord.session = this.session;
            childRecord.xmlReader = this.xmlReader;
            childRecord.setFragmentBuilder(this.getFragmentBuilder());
        }
    }

    public UnmarshalRecord getParentRecord() {
        return this.parentRecord;
    }

    /**
     * Return the root element's prefix qualified name
     *
     * @return
     */
    public String getRootElementName() {
        return rootElementName;
    }

    /**
     * Return the root element's namespace URI
     *
     * @return
     */
    public String getRootElementNamespaceUri() {
        return rootElementNamespaceUri;
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

    public Map getNamespaceMap() {
        return this.namespaceMap;
    }

    public void setNamespaceMap(Map namespaceMap) {
        this.namespaceMap = namespaceMap;
    }

    public Map getUriToPrefixMap() {
        return this.uriToPrefixMap;
    }

    public void setUriToPrefixMap(Map uriToPrefixMap) {
        this.uriToPrefixMap = uriToPrefixMap;
    }

    public List getNullCapableValues() {
        return this.nullCapableValues;
    }

    public void removeNullCapableValue(NullCapableValue nullCapableValue) {
        if (null == getNullCapableValues()) {
            return;
        }
        getNullCapableValues().remove(nullCapableValue);
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

    public StrBuffer getStringBuffer() {
        return this.stringBuffer;
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
        String namespaceURI = "";
        if (null != namespaceResolver) {
            namespaceURI = namespaceResolver.resolveNamespacePrefix(lastFragment.getPrefix());
            if (null == namespaceURI) {
                namespaceURI = EMPTY_STRING;
            }
        }
        return attributes.getValue(namespaceURI, lastFragment.getLocalName());
    }

    public XPathNode getXPathNode() {
        return xPathNode;
    }
    
    public void startDocument() throws SAXException {
        startDocument(null);
    }

    public void startDocument(XMLMapping selfRecordMapping) throws SAXException {
        try {
            XMLDescriptor xmlDescriptor = (XMLDescriptor) treeObjectBuilder.getDescriptor();
            if(xmlDescriptor.isSequencedObject()) {
                unmarshalContext = new SequencedUnmarshalContext();
            } else {
                unmarshalContext = ObjectUnmarshalContext.getInstance();
            }
            
            Object object = this.getXMLReader().getCurrentObject(session, selfRecordMapping);
            if (object == null) {
                object = treeObjectBuilder.buildNewInstance();
            }
            this.setCurrentObject(object);
            if ((this.unmarshaller != null) && (this.unmarshaller.getUnmarshalListener() != null)) {
                if (this.parentRecord != null) {
                    this.unmarshaller.getUnmarshalListener().beforeUnmarshal(object, parentRecord.getCurrentObject());
                } else {
                    this.unmarshaller.getUnmarshalListener().beforeUnmarshal(object, null);
                }
            }
            if (parentRecord != null) {
                this.xmlReader.newObjectEvent(object, parentRecord.getCurrentObject(), selfRecordMapping);
            } else {
                this.xmlReader.newObjectEvent(object, null, selfRecordMapping);
            }
            List containerValues = treeObjectBuilder.getContainerValues();
            if (null != containerValues) {
                containersMap = new HashMap(containerValues.size());
                ContainerValue containerValue;
                Object containerInstance;
                int containerValuesSize = containerValues.size();
                for (int x = 0; x < containerValuesSize; x++) {
                    containerValue = (ContainerValue)containerValues.get(x);
                    containerInstance = containerValue.getContainerInstance();
                    containersMap.put(containerValue, containerInstance);
                }
            }

            if (null != xPathNode.getSelfChildren()) {
                int selfChildrenSize = xPathNode.getSelfChildren().size();
                selfRecords = new ArrayList(selfChildrenSize);
                XPathNode selfNode;
                for (int x = 0; x < selfChildrenSize; x++) {
                    selfNode = (XPathNode)xPathNode.getSelfChildren().get(x);
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
        Object object = this.getCurrentObject();
        if (null != selfRecords) {
            int selfRecordsSize = selfRecords.size();
            for (int x = 0; x < selfRecordsSize; x++) {
                ((UnmarshalRecord)selfRecords.get(x)).endDocument();
            }
        }
        try {
            // PROCESS COLLECTION MAPPINGS
            if (null != containersMap) {
                Iterator containersMapKeys = containersMap.keySet().iterator();
                ContainerValue containerValue;
                Object containerInstance;
                while (containersMapKeys.hasNext()) {
                    containerValue = (ContainerValue)containersMapKeys.next();
                    containerInstance = containersMap.get(containerValue);
                    containerValue.setContainerInstance(object, containerInstance);
                }
            }

            // PROCESS NULL CAPABLE VALUES
            // This must be done because the node may not have existed to 
            // trigger the mapping.
            if (null != getNullCapableValues()) {
                int nullValuesSize = getNullCapableValues().size();
                NullCapableValue nullCapableValue;
                for (int x = 0; x < nullValuesSize; x++) {
                    nullCapableValue = (NullCapableValue)getNullCapableValues().get(x);
                    nullCapableValue.setNullValue(object, session);
                }
            }

            // PROCESS TRANSFORMATION MAPPINGS
            List transformationMappings = treeObjectBuilder.getTransformationMappings();
            if (null != transformationMappings) {
                ReadObjectQuery query = new ReadObjectQuery();
                query.setSession(session);
                int transformationMappingsSize = transformationMappings.size();
                AbstractTransformationMapping transformationMapping;
                for (int x = 0; x < transformationMappingsSize; x++) {
                    transformationMapping = (AbstractTransformationMapping)transformationMappings.get(x);
                    transformationMapping.readFromRowIntoObject(transformationRecord, null, object, query, session);
                }
            }
            
            if ((this.unmarshaller != null) && (unmarshaller.getUnmarshalListener() != null)) {
                if (this.parentRecord != null) {
                    unmarshaller.getUnmarshalListener().afterUnmarshal(object, parentRecord.getCurrentObject());
                } else {
                    unmarshaller.getUnmarshalListener().afterUnmarshal(object, null);
                }
            }
            
            // HANDLE POST BUILD EVENTS
            XMLDescriptor xmlDescriptor = (XMLDescriptor) session.getDescriptor(object);
            if ((xmlDescriptor != null) && (xmlDescriptor.getEventManager().hasAnyEventListeners())) {
                DescriptorEvent event = new DescriptorEvent(object);
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
            XMLDescriptor xmlDescriptor = (XMLDescriptor)session.getDescriptor(object);
            if ((xmlDescriptor != null) && (xmlDescriptor.getPrimaryKeyFieldNames().size() > 0)) {
                Vector pk = treeObjectBuilder.extractPrimaryKeyFromObject(object, session);
                CacheKey key = session.getIdentityMapAccessorInstance().acquireDeferredLock(pk, xmlDescriptor.getJavaClass(), xmlDescriptor);
                key.setRecord(this);
                key.setObject(object);
                key.releaseDeferredLock();
            }
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (null == namespaceMap) {
            namespaceMap = new HashMap();
        }
        if (uriToPrefixMap == null) {
            uriToPrefixMap = new HashMap();
        }
        Stack uriStack = (Stack)namespaceMap.get(prefix);
        if(uriStack == null) {
            uriStack = new Stack();
            namespaceMap.put(prefix, uriStack);
        }
        uriStack.push(uri);
        Stack prefixStack = (Stack)uriToPrefixMap.get(uri);
        if(prefixStack == null) {
            prefixStack = new Stack();
            uriToPrefixMap.put(uri, prefixStack);
        }            
        prefixStack.push(prefix);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        if (null == namespaceMap) {
            return;
        }
        Stack uriStack = (Stack)namespaceMap.get(prefix);
        String uri = null;
        if(uriStack != null && uriStack.size() > 0) {
            uri = (String)uriStack.pop();
        }
        if(uri != null && uriToPrefixMap != null) {
            Stack prefixStack = (Stack)uriToPrefixMap.get(uri);
            if(prefixStack != null && prefixStack.size() > 0) {
                prefixStack.pop();
            }
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if(null != xPathNode.getXPathFragment() && xPathNode.getXPathFragment().nameIsText()) {
            if (null != xPathNode.getUnmarshalNodeValue()) {
                xPathNode.getUnmarshalNodeValue().endElement(xPathFragment, this);
                if (xPathNode.getParent() != null) {
                    xPathNode = xPathNode.getParent();
                }
            }
        }

        // set the root element's local name and namespace prefix and look for
        // schema locations etc.
        if (rootElementName == null) {
            rootElementName = qName;
            rootElementNamespaceUri = namespaceURI;
            schemaLocation = atts.getValue(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_LOCATION);
            noNamespaceSchemaLocation = atts.getValue(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.NO_NS_SCHEMA_LOCATION);
        }

        try {
            if (null != selfRecords) {
                int selfRecordsSize = selfRecords.size();
                for (int x = 0; x < selfRecordsSize; x++) {
                    ((UnmarshalRecord)selfRecords.get(x)).startElement(namespaceURI, localName, qName, atts);
                }
            }

            XPathNode node = getNonAttributeXPathNode(namespaceURI, localName, qName);
            if (null == node) {
                NodeValue parentNodeValue = xPathNode.getUnmarshalNodeValue();
                if ((null == xPathNode.getXPathFragment()) && (parentNodeValue != null)) {
                    XPathFragment parentFragment = new XPathFragment();
                    if (EMPTY_STRING.equals(namespaceURI)) {
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

                NodeValue nodeValue = node.getUnmarshalNodeValue();
                if (null != nodeValue) {
                    if (!nodeValue.startElement(xPathFragment, this, atts)) {
                        // UNMAPPED CONTENT
                        startUnmappedElement(namespaceURI, localName, qName, atts);
                        return;
                    }
                }

                //Handle Attributes
                for (int i = 0; i < atts.getLength(); i++) {
                    String attNamespace = atts.getURI(i);
                    String attLocalName = atts.getLocalName(i);
                    String value = atts.getValue(i);
                    NodeValue attributeNodeValue = null;

                    // Some parsers don't set the URI/local name for namespace
                    // attributes
                    if ((attLocalName == null) || (attLocalName.length() == 0)) {
                        String qname = atts.getQName(i);
                        if ((qname != null) && (qname.length() > 0)) {
                            int idx = qname.indexOf(":");
                            attLocalName = qname.substring((idx <= 0) ? 0 : (idx + 1), qname.length());

                            String attPrefix = (idx == -1) ? null : qname.substring(0, idx);
                            if (((attPrefix != null) && attPrefix.equalsIgnoreCase("xmlns")) || ((attPrefix == null) && attLocalName.equalsIgnoreCase("xmlns"))) {
                                attNamespace = XMLConstants.XMLNS_URL;
                            }
                        }
                    }

                    //Look for any Self-Mapping nodes that may want this attribute.
                    if (this.selfRecords != null) {
                        for (int j = 0; j < selfRecords.size(); j++) {
                            UnmarshalRecord nestedRecord = ((UnmarshalRecord)selfRecords.get(j));
                            attributeNodeValue = nestedRecord.getAttributeChildNodeValue(attNamespace, attLocalName);
                            if (attributeNodeValue != null) {
                                attributeNodeValue.attribute(nestedRecord, attNamespace, attLocalName, value);
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
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            if (null != selfRecords) {
                int selfRecordsSize = selfRecords.size();
                for (int x = 0; x < selfRecordsSize; x++) {
                    ((UnmarshalRecord)selfRecords.get(x)).endElement(namespaceURI, localName, qName);
                }
            }

            if (null != xPathNode.getUnmarshalNodeValue()) {
                xPathNode.getUnmarshalNodeValue().endElement(xPathFragment, this);
                if (xPathNode.getParent() != null) {
                    xPathNode = xPathNode.getParent();
                }
            }

            if (null != xPathNode.getParent()) {
                if (EMPTY_STRING.equals(namespaceURI)) {
                    xPathFragment.setLocalName(qName);
                    xPathFragment.setNamespaceURI(null);
                } else {
                    xPathFragment.setLocalName(localName);
                    xPathFragment.setNamespaceURI(namespaceURI);
                }
                if (xPathFragment.qNameEquals(xPathNode.getXPathFragment())) {
                    if (xPathNode.getParent() != null) {
                        xPathNode = xPathNode.getParent();
                    }
                }
            }

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

    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            if (null != selfRecords) {
                int selfRecordsSize = selfRecords.size();
                for (int x = 0; x < selfRecordsSize; x++) {
                    ((UnmarshalRecord)selfRecords.get(x)).characters(ch, start, length);
                }
            }

            XPathNode textNode = null;
            if (null != xPathNode.getNonAttributeChildrenMap()) {
                textNode = (XPathNode)xPathNode.getNonAttributeChildrenMap().get(XPathFragment.TEXT_FRAGMENT);
                if (null == textNode) {
                    textNode = (XPathNode)xPathNode.getNonAttributeChildrenMap().get(XPathFragment.ANY_FRAGMENT);

                    if (textNode != null) {
                        if (0 == length) {
                            return;
                        }
                        String tmpString = new String(ch, start, length);
                        if (EMPTY_STRING.equals(tmpString.trim())) {
                            return;
                        }
                    }
                }
            }
            if (null != textNode) {
                xPathNode = textNode;
                unmarshalContext.characters(this);
            }
            if (null != xPathNode.getUnmarshalNodeValue()) {
                stringBuffer.append(ch, start, length);
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

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    protected XPathNode getNonAttributeXPathNode(String namespaceURI, String localName, String qName) {
        if (0 == levelIndex) {
            return xPathNode;
        }
        if (EMPTY_STRING.equals(namespaceURI)) {
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
                Integer newIndex;
                if (null == this.indexMap) {
                    this.indexMap = new HashMap();
                    newIndex = new Integer(1);
                } else {
                    Integer oldIndex = (Integer)indexMap.get(xPathFragment);
                    if (null == oldIndex) {
                        newIndex = new Integer(1);
                    } else {
                        newIndex = new Integer(oldIndex.intValue() + 1);
                    }
                }
                indexMap.put(xPathFragment, newIndex);
                XPathFragment positionalFragment = new XPathFragment();
                positionalFragment.setNamespaceURI(xPathFragment.getNamespaceURI());
                positionalFragment.setLocalName(xPathFragment.getLocalName());
                positionalFragment.setIndexValue(newIndex.intValue());
                resultNode = (XPathNode)nonAttributeChildrenMap.get(positionalFragment);
                if (null == resultNode) {
                    // ANY MAPPING
                    resultNode = (XPathNode)nonAttributeChildrenMap.get(XPathFragment.ANY_FRAGMENT);
                }
            }
        }
        return resultNode;
    }

    public String resolveNamespacePrefix(String prefix) {
        String namespaceURI = null;
        if(prefix == null) {
            prefix = "";
        } 
        if (null != namespaceMap) {
            Stack uriStack = (Stack)namespaceMap.get(prefix);
            if(uriStack != null && uriStack.size() > 0) {
                namespaceURI = (String)uriStack.peek();
            }
        }
        
        if (null == namespaceURI) {
            if (null != getParentRecord()) {
                namespaceURI = getParentRecord().resolveNamespacePrefix(prefix);
            }
        }
        return namespaceURI;
    }

    public String resolveNamespaceUri(String uri) {
        String prefix = null;
        if (null != uriToPrefixMap) {
            Stack prefixStack = (Stack)uriToPrefixMap.get(uri);
            if(prefixStack != null && prefixStack.size() > 0) {
                prefix = (String)prefixStack.peek();
            }
        }
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
            for (int i = 0; i < selfRecords.size(); i++) {
                UnmarshalRecord nestedRecord = ((UnmarshalRecord)selfRecords.get(i));
                NodeValue node = nestedRecord.getAttributeChildNodeValue(namespace, localName);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }

    public NodeValue getAttributeChildNodeValue(String namespace, String localName) {
        Map attributeChildrenMap = xPathNode.getAttributeChildrenMap();
        if (attributeChildrenMap != null) {
            xPathFragment.setLocalName(localName);
            if (EMPTY_STRING.equals(namespace)) {
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

    public XMLUnmarshaller getUnmarshaller() {
        return this.unmarshaller;
    }

    public void setUnmarshaller(XMLUnmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public SAXFragmentBuilder getFragmentBuilder() {
        return this.fragmentBuilder;
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
        this.unmarshalContext.unmappedContent(this);
    }
    
}
