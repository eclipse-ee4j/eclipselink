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

import java.io.UnsupportedEncodingException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.ObjectBuilder;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper;
import org.eclipse.persistence.internal.oxm.XPathPredicate;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.AbstractMarshalRecordImpl;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshalListener;
import org.eclipse.persistence.oxm.record.ValidatingMarshalRecord.MarshalSAXParseException;
import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * <p>A MarshalRecord encapsulates the marshal target.</p>
 *
 * <p>MarshalRecords are stateful and state changes are triggered by different
 * event notifications, therefore this class is not thread safe.</p>
 *
 * <p>XML document creation will differ depending on the subclass of MarshalRecord
 * used.  For example when NodeRecord is used a child element is created on the
 * openStartElement event, and when the ContentHandlerRecord is used a child
 * element is not created until the closeStartMethod event.</p>
 *
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public abstract class MarshalRecord<MARSHALLER extends Marshaller> extends AbstractMarshalRecordImpl<CoreAbstractSession, DatabaseField, MARSHALLER, NamespaceResolver> implements org.eclipse.persistence.internal.oxm.record.MarshalRecord<CoreAbstractSession, DatabaseField, MARSHALLER, NamespaceResolver> {
    private ArrayList<XPathNode> groupingElements;
    private HashMap positionalNodes;

    protected XPathFragment textWrapperFragment;

    private CycleDetectionStack<Object> cycleDetectionStack = new CycleDetectionStack<Object>();

    private Stack<CoreAttributeGroup> attributeGroupStack;
    
    protected static final String COLON_W_SCHEMA_NIL_ATTRIBUTE = Constants.COLON + Constants.SCHEMA_NIL_ATTRIBUTE;
    protected static final String TRUE = "true";
    
    
    public MarshalRecord() {
        super(null);
        namespaceResolver = new NamespaceResolver();
    }

    public HashMap getPositionalNodes() {
        if (positionalNodes == null) {
            positionalNodes = new HashMap();
        }
        return positionalNodes;
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

    @Override
    public Node getDOM() {
        return null;
    }

    /**
     * INTERNAL:
     * If an XPathNode does not have an associated NodeValue then add it to the
     * MarshalRecord as a grouping element.
     * @param xPathNode
     */
    public void addGroupingElement(XPathNode xPathNode) {
        if (null == groupingElements) {
            groupingElements = new ArrayList(2);
        }
        groupingElements.add(xPathNode);
    }

    /**
     *
     * INTERNAL:
     * @param xPathNode
     */
    public void removeGroupingElement(XPathNode xPathNode) {
        if (null != groupingElements) {
            groupingElements.remove(xPathNode);
        }
    }

    public String transformToXML() {
        return null;
    }
    
    public void setSession(CoreAbstractSession session) {
        super.setSession(session);
        if (session != null && session.getDatasourceLogin() instanceof XMLLogin) {
            this.equalNamespaceResolvers = ((XMLLogin) session.getDatasourceLogin()).hasEqualNamespaceResolvers();
        }
    }

    /**
     * INTERNAL:
     * Add the field-value pair to the document.
     */
    public void add(DatabaseField key, Object value) {
        if (null == value) {
            return;
        }
        Field xmlField = convertToXMLField(key);
        XPathFragment lastFragment = xmlField.getLastXPathFragment();
        if (lastFragment.nameIsText()) {
            characters(xmlField.getSchemaType(), value, null, xmlField.isCDATA());
            
        } else if (lastFragment.isAttribute()) {
            attribute(lastFragment, xmlField.getNamespaceResolver(), value, xmlField.getSchemaType());
        } else {
            element(lastFragment);
        }
    }

    /**
     * INTERNAL:
     * Add the field-value pair to the document.
     */
    public Object put(DatabaseField key, Object value) {
        add(key, value);
        return null;
    }

    /**
     * INTERNAL:
     * Add the namespace declarations to the XML document.
     * @param namespaceResolver The NamespaceResolver contains the namespace
     * prefix and URI pairings that need to be declared.
     */
    public void namespaceDeclarations(NamespaceResolver namespaceResolver) {
        if (namespaceResolver == null) {
            return;
        }
        String namespaceURI = namespaceResolver.getDefaultNamespaceURI();
        if(null != namespaceURI) {            
            defaultNamespaceDeclaration(namespaceURI);
        }
        if(namespaceResolver.hasPrefixesToNamespaces()) {
            for(Entry<String, String> entry: namespaceResolver.getPrefixesToNamespaces().entrySet()) {
                String namespacePrefix = entry.getKey();                
                namespaceDeclaration(namespacePrefix, entry.getValue());
            }
        }
    }
    
    /**
     * Handle marshal of an empty collection.  
     * @param xPathFragment
     * @param namespaceResolver
     * @param openGrouping if grouping elements should be marshalled for empty collections
     * @return
     */
    public boolean emptyCollection(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, boolean openGrouping) {
    	 if (openGrouping) {
             XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
             closeStartGroupingElements(groupingFragment);
             return true;
         } else {
             return false;
         }           
    }
    
	/**
	 * Add the specified namespace declaration
	 * @param prefix
	 * @param namespaceURI
	 */
    public void namespaceDeclaration(String prefix, String namespaceURI){
        attribute(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON + prefix, namespaceURI);
    }
    
    /**
     * Add the defaultNamespace declaration
     * @param defaultNamespace
     */
    public void defaultNamespaceDeclaration(String defaultNamespace){
        attribute(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE, javax.xml.XMLConstants.XMLNS_ATTRIBUTE, defaultNamespace);
    }

    /**
     * Receive notification that a document is being started.
     * @param encoding The XML document will be encoded using this encoding.
     * @param version This specifies the version of XML.
     */
    public abstract void startDocument(String encoding, String version);

    /**
     * INTERNAL
     * Writes the header, if appropriate.
     */
    public void writeHeader() {
    }

    /**
     * Recieve notification that a document is being ended.
     */
    public abstract void endDocument();

    /**
     * INTERNAL
     */
    public void marshalWithoutRootElement(ObjectBuilder treeObjectBuilder, Object object, Descriptor descriptor, Root root, boolean isXMLRoot){    
    }
    
    /**
     * Receive notification that a namespace has been declared.
     * @param prefix The namespace prefix.
     * @param namespaceURI The namespace URI.
     */
    public void startPrefixMapping(String prefix, String namespaceURI) {
    }

    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
        if (namespaceResolver != null && namespaceResolver.hasPrefixesToNamespaces()) {
            for(Entry<String, String> entry: namespaceResolver.getPrefixesToNamespaces().entrySet()) {
                startPrefixMapping(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Receive notification that the scope of this namespace declaration has
     * ended.
     * @param prefix The namespace prefix.
     */
    public void endPrefixMapping(String prefix) {
    }

    public void endPrefixMappings(NamespaceResolver namespaceResolver) {      
    }

    /**
     * Receive notification that an element is being started.
     * @param xPathFragment The XPathFragment contains the name and prefix
     * information about the XML element being ended.
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     * namespace URI for the namespace prefix held by the XPathFragment (if
     * required).
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        this.addPositionalNodes(xPathFragment, namespaceResolver);
    }

    /**
     * Receive notification of an element.
     * @param frag The XPathFragment of the element
     */
    public abstract void element(XPathFragment frag);

    /**
     * Receive notification of an attribute.
     * @param xPathFragment The XPathFragment contains the name and prefix
     * information about the XML element being ended.
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     * namespace URI for the namespace prefix held by the XPathFragment (if
     * required).
     * @param value This is the complete value for the attribute.
     */
    public abstract void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value);

    /**
     * Receive notification of an attribute.
     * @param namespaceURI The namespace URI, if the attribute is not namespace
     * qualified the value of this parameter wil be null.
     * @param localName The local name of the attribute.
     * @param qName The qualified name of the attribute.
     * @param value This is the complete value for the attribute.
     */
    @Override
    public abstract void attribute(String namespaceURI, String localName, String qName, String value);

    
    /**
     * Receive notification that all of the attribute events have occurred for
     * the most recent element that has been started.
     */
    public abstract void closeStartElement();

    /**
     * Receive notification that an element is being ended.
     * @param xPathFragment The XPathFragment contains the name and prefix
     * information about the XML element being ended.
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     * namespace URI for the namespace prefix held by the XPathFragment (if
     * required).
     */
    public abstract void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver);

    /**
     * Receive notification of character data.
     * @param value This is the entire value of the text node.
     */
    public abstract void characters(String value);

    /**
     * Convert the value if necessary and write out the attribute and converted value.
     * @since EclipseLink 2.4 
     */    
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver,  Object value, QName schemaType){
    	 if(schemaType != null && Constants.QNAME_QNAME.equals(schemaType)){
    	     String convertedValue = getStringForQName((QName)value);
             attribute(xPathFragment, namespaceResolver, convertedValue);
    	 } else{
             String convertedValue = ((String) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, ClassConstants.STRING, schemaType));         	
             attribute(xPathFragment, namespaceResolver, convertedValue);
         }               
    }     
    
    /**
     * Convert the value if necessary and write out the converted value.
     * @since EclipseLink 2.4 
     */    
    public void characters(QName schemaType, Object value, String mimeType, boolean isCDATA){  
        if(mimeType != null) {
            value = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                    value, marshaller, mimeType).getData();
        }
        if(schemaType != null && Constants.QNAME_QNAME.equals(schemaType)){
            String convertedValue = getStringForQName((QName)value);
            characters(convertedValue);
        }else{
            String convertedValue = ((String) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, ClassConstants.STRING, schemaType));
            if(isCDATA){
                cdata(convertedValue);        	    
            }else{
                characters(convertedValue);
            }
        }
    }
    
    public String getValueToWrite(QName schemaType, Object value, XMLConversionManager xmlConversionManager) {
    	if(value == null){
    		return null;
    	}
        if(schemaType != null && Constants.QNAME_QNAME.equals(schemaType)){
            return getStringForQName((QName)value);
        }else if(value.getClass() == String.class){
        	return (String) value;
        }
        return (String) xmlConversionManager.convertObject(value, ClassConstants.STRING, schemaType);
    }   
    
    protected String getStringForQName(QName qName){
        if(null == qName) {
            return null;
        }
        String namespaceURI = qName.getNamespaceURI();
        if(null == namespaceURI || 0 == namespaceURI.length()) {
            if(getNamespaceResolver() != null && getNamespaceResolver().getDefaultNamespaceURI() != null) {
                //need to add a default namespace declaration.                
                defaultNamespaceDeclaration(namespaceURI);
            }
            return qName.getLocalPart();
        } else {
            NamespaceResolver namespaceResolver = getNamespaceResolver();
            if(namespaceResolver == null){
                throw XMLMarshalException.namespaceResolverNotSpecified(namespaceURI);
            }
            if(namespaceURI.equals(namespaceResolver.getDefaultNamespaceURI())) {
                return qName.getLocalPart();
            }
            String prefix = namespaceResolver.resolveNamespaceURI(namespaceURI);
            if(null == prefix) {
                prefix = namespaceResolver.generatePrefix();                
                namespaceDeclaration(prefix, namespaceURI);
            }
            if(Constants.EMPTY_STRING.equals(prefix)){
            	return qName.getLocalPart();
            }
            return prefix + Constants.COLON + qName.getLocalPart();
        }
    }
    
    
    /**
     * Receive notification of character data to be wrapped in a CDATA node.
     * @param value This is the value of the text to be wrapped
     */
    public abstract void cdata(String value);

    /**
     * Receive notification of a node.
     * @param node The Node to be added to the document
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     * namespace URI/prefix of the node
     */
     public void node(Node node, NamespaceResolver resolver ){
        node(node, resolver, null, null);
     }

     /**
      * Receive notification of a node.
      * @param node The Node to be added to the document
      * @param namespaceResolver The NamespaceResolver can be used to resolve the
      * @param name replacement root name for the node
      * @param rootUri replacement root namespace for the node
      * namespace URI/prefix of the node
      */
    public abstract void node(Node node, NamespaceResolver resolver, String qualifiedName, String rootUri);

    /**
     * INTERNAL:
     * Trigger that the grouping elements should be written.  This is normally
     * done when something like a mapping has a non-null value that is
     * marshalled.
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     * namespace URI for the namespace prefix held by the XPathFragment (if
     * required).
     */
    public XPathFragment openStartGroupingElements(NamespaceResolver namespaceResolver) {
        if (null == groupingElements) {
            return null;
        }
        XPathFragment xPathFragment = null;
        for (int x = 0, groupingElementsSize = groupingElements.size(); x < groupingElementsSize; x++) {
            XPathNode xPathNode = groupingElements.get(x);
            xPathFragment = xPathNode.getXPathFragment();
            openStartElement(xPathFragment, namespaceResolver);

            predicateAttribute(xPathFragment, namespaceResolver);

            if (x != (groupingElementsSize - 1)) {
                closeStartElement();
            }
        }
        groupingElements = null;
        return xPathFragment;
    }

    public void closeStartGroupingElements(XPathFragment groupingFragment) {
        if (null != groupingFragment) {
            this.closeStartElement();
        }
    }

    protected void addPositionalNodes(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        if (xPathFragment.containsIndex()) {
            String shortName = xPathFragment.getShortName();
            Integer index = (Integer)getPositionalNodes().get(shortName);
            int start;
            if (null == index) {
                start = 1;
            } else {
                start = index.intValue();
            }
            for (int x = start; x < xPathFragment.getIndexValue(); x++) {
                element(xPathFragment);
            }
            getPositionalNodes().put(shortName, xPathFragment.getIndexValue() + 1);
        }
    }

    public void beforeContainmentMarshal(Object child) {
        if(null != marshaller) {
            XMLMarshalListener marshalListener = marshaller.getMarshalListener();
            if(null != marshalListener) {
                try {
                    marshalListener.beforeMarshal(child);
                } catch(EclipseLinkException e) {
                    ErrorHandler errorHandler = marshaller.getErrorHandler();
                    if(null == errorHandler) {
                        throw e;
                    } else {
                        try {
                            MarshalSAXParseException saxParseException = new MarshalSAXParseException(null, null, null, -1, -1, e, child);
                            errorHandler.error(saxParseException);
                        } catch(SAXException saxParseException) {
                            throw e;
                        }
                    }
                }
            }
        }
        setOwningObject(child);
    }

    public void afterContainmentMarshal(Object parent, Object child) {
        if(null != marshaller) {
            XMLMarshalListener marshalListener = marshaller.getMarshalListener();
            if(null != marshalListener) {
                try {
                    marshalListener.afterMarshal(child);
                } catch(EclipseLinkException e) {
                    ErrorHandler errorHandler = marshaller.getErrorHandler();
                    if(null == errorHandler) {
                        throw e;
                    } else {
                        try {
                            MarshalSAXParseException saxParseException = new MarshalSAXParseException(null, null, null, -1, -1, e, child);
                            errorHandler.error(saxParseException);
                        } catch(SAXException saxParseException) {
                            throw e;
                        }
                    }
                }
            }
        }
        setOwningObject(parent);
    }
    
    /**
     * INTERNAL:
     * Returns the list of grouping elements currently stored on the MarshalRecord
     */
    public ArrayList<XPathNode> getGroupingElements() {
        return this.groupingElements;
    }
    
    /**
     * INTERNAL:
     * Sets the list of grouping elements to be marshalled on this record. 
     */
    public void setGroupingElement(ArrayList<XPathNode> elements) {
        this.groupingElements = elements;
    }

    /**
     * Marshal the attribute for the predicate if one was specified. 
     */
    public void predicateAttribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        if(null != xPathFragment) {
            XPathPredicate predicate = xPathFragment.getPredicate();
            if(null != predicate) {
                XPathFragment predicateXPathFragment = predicate.getXPathFragment();
                if(predicateXPathFragment.isAttribute()) {
                    attribute(predicateXPathFragment, namespaceResolver, predicate.getValue());
                }
            }
        }
    }

    /**
     * This method is used to inform the MarshalRecord that the element events
     * it is about to receive are part of a collection.
     * @since EclipseLink 2.4
     * @see endCollection
     */
    public void startCollection() {
    }
    
    /**
     * Used when an nil attribute should be written
     * @since EclipseLink 2.4
     */    
    public void emptyAttribute(XPathFragment xPathFragment,NamespaceResolver namespaceResolver){
    	XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
        // We mutate the null into an empty string 
        attribute(xPathFragment, namespaceResolver, Constants.EMPTY_STRING);
        closeStartGroupingElements(groupingFragment);
    }

    
    /**
     * Used when an nil attribute should be written
     * @since EclipseLink 2.4
     */    
    public void emptyComplex(XPathFragment xPathFragment, NamespaceResolver namespaceResolver){
        XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
        closeStartGroupingElements(groupingFragment);
        openStartElement(xPathFragment, namespaceResolver);
        closeStartElement();
        endElement(xPathFragment, namespaceResolver);
    }
    
    /**
     * Used when an nil attribute should be written
     * @since EclipseLink 2.4
     */    
    public void emptySimple(NamespaceResolver namespaceResolver){
    	XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
    	closeStartGroupingElements(groupingFragment);
    }
    
    /**
     * Used when an nil attribute should be written
     * @since EclipseLink 2.4
     */    
    public void nilSimple(NamespaceResolver namespaceResolver){
    	 XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
         String xsiPrefix = processNamespaceResolverForXSIPrefix(namespaceResolver);
         StringBuilder qName = new StringBuilder(Constants.ATTRIBUTE); // Unsynchronized
         qName.append(xsiPrefix).append(COLON_W_SCHEMA_NIL_ATTRIBUTE);
         XPathFragment nilFragment = new XPathFragment(qName.toString());
         nilFragment.setNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
         attribute(nilFragment, namespaceResolver, TRUE);
         closeStartGroupingElements(groupingFragment);
    }
    
    /**
     * Used when an nil attribute should be written
     * @since EclipseLink 2.4
     */    
    public void nilComplex(XPathFragment xPathFragment, NamespaceResolver namespaceResolver){
    	 XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
    	 closeStartGroupingElements(groupingFragment);
    	 openStartElement(xPathFragment, namespaceResolver);
    	 String xsiPrefix = processNamespaceResolverForXSIPrefix(namespaceResolver);
    	 XPathFragment nilFragment = new XPathFragment(Constants.ATTRIBUTE + xsiPrefix + COLON_W_SCHEMA_NIL_ATTRIBUTE);
    	 nilFragment.setNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
    	 attribute(nilFragment, namespaceResolver, TRUE);
    	 closeStartElement();
    	 endElement(xPathFragment, namespaceResolver);
    }
   

    /**
     * This method is used to inform the MarshalRecord that it is done receiving
     * element events that are part of a collection.
     * @since EclipseLink 2.4
     * @see startCollection
     */
    public void endCollection() {
    }

  
    /**
     * INTERNAL:
     * Private function to process or create an entry in the NamespaceResolver for the xsi prefix.
     * @param namespaceResolver
     * @return xsi prefix
     * @since EclipseLink 2.4
     */
    protected String processNamespaceResolverForXSIPrefix(NamespaceResolver namespaceResolver) {
        String xsiPrefix;
        if (null == namespaceResolver) {
            // add new xsi entry into the properties map
            xsiPrefix = Constants.SCHEMA_INSTANCE_PREFIX;
            namespaceResolver = new NamespaceResolver();
            namespaceResolver.put(xsiPrefix, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);            
            namespaceDeclaration(xsiPrefix, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        } else {
            // find an existing xsi entry in the map
            xsiPrefix = namespaceResolver.resolveNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            if (null == xsiPrefix) {
                xsiPrefix = namespaceResolver.generatePrefix(Constants.SCHEMA_INSTANCE_PREFIX);                
                namespaceDeclaration(xsiPrefix, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            }
        }
        return xsiPrefix;
    }
    
    /**
     * INTERNAL:
     * The optional fragment used to wrap the text() mappings
     * @since 2.4
     */
    public XPathFragment getTextWrapperFragment() {
    	//return null as this is not supported by default
    	//subclass records can return the fragment if supported.
        return null;
    }
     
    protected String getNameForFragment(XPathFragment xPathFragment) {
        if(!this.hasCustomNamespaceMapper()) {
            return xPathFragment.getShortName();
        }
        if(xPathFragment.getNamespaceURI() != null && xPathFragment.getNamespaceURI().length() > 0) {
            String prefix = this.getPrefixForFragment(xPathFragment);
            if(prefix != null && prefix.length() > 0) {
                return prefix + Constants.COLON + xPathFragment.getLocalName();
            }
        }
        return xPathFragment.getLocalName();
    }
    
    protected byte[] getNameForFragmentBytes(XPathFragment xPathFragment) {
        if(!this.hasCustomNamespaceMapper()) {
            return xPathFragment.getShortNameBytes();
        }
        String name = xPathFragment.getLocalName();
        if(xPathFragment.getNamespaceURI() != null && xPathFragment.getNamespaceURI().length() > 0) {
            String prefix = this.getPrefixForFragment(xPathFragment);
            if(prefix != null && prefix.length() > 0) {
                name = prefix + Constants.COLON + xPathFragment.getLocalName();
            }
        }
        byte[] bytes = null;
        try {
            bytes = name.getBytes(Constants.DEFAULT_XML_ENCODING);
        } catch(UnsupportedEncodingException ex) {}
        return bytes;
    }    
    
    protected String getPrefixForFragment(XPathFragment xPathFragment) {
        if(!hasCustomNamespaceMapper) { 
            return xPathFragment.getPrefix();
        }
        String uri = xPathFragment.getNamespaceURI();
        if(uri == null || uri.length() == 0) {
            return Constants.EMPTY_STRING;
        }
        
        String defaultNamespace = getNamespaceResolver().getDefaultNamespaceURI();
        
        if(defaultNamespace != null && defaultNamespace.equals(uri)) {
            return Constants.EMPTY_STRING;
        }
        String prefix = this.getNamespaceResolver().resolveNamespaceURI(uri);
        
        if(prefix != null) {
            return prefix;
        } 
        for(Object next:getNamespaceResolver().getNamespaces()) {
            Namespace ns = (Namespace)next;
            uri = ns.getNamespaceURI();
            prefix = ns.getPrefix();
        }
        return xPathFragment.getPrefix();
    }

    /**
     * INTERNAL
     */
    public CycleDetectionStack<Object> getCycleDetectionStack() {
        return this.cycleDetectionStack;
    }

    private Field convertToXMLField(CoreField field) {
        return (Field) field;
    }

    // === Inner Classes ======================================================

    private static final Object[] EMPTY_CYCLE_DATA = new Object[8];
    
    /**
     * A Stack-like List, used to detect object cycles during marshal operations.
     */
    public static class CycleDetectionStack<E> extends AbstractList<Object> {

        private Object[] data = EMPTY_CYCLE_DATA;
        
        int currentIndex = 0;

        public void push(E item) {
            if (currentIndex == data.length) {
            	growArray();
            }
        	data[currentIndex] = item;
        	currentIndex++;
        }

        private void growArray() {
        	Object[] newArray = new Object[data.length * 2];
        	System.arraycopy(data, 0, newArray, 0, data.length);
        	data = newArray;
        }
        
        public Object pop() {
        	Object o = data[currentIndex - 1];
        	data[currentIndex - 1] = null;
        	currentIndex--;
        	return o;
        }

        public boolean contains(Object item, boolean equalsUsingIdentity) {
            if (equalsUsingIdentity) {
            	for (int i = 0; i < currentIndex; i++) {
            		if (data[i] == item) {
                        return true;
                    }
                }
            } else {
            	for (int i = 0; i < currentIndex; i++) {
            		if (data[i] != null && data[i].equals(item)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public String getCycleString() {
            StringBuilder sb = new StringBuilder();
            int i = size() - 1;
            Object obj = get(i);
            sb.append(obj);
            Object x;
            do {
                sb.append(" -> ");
                x = get(--i);
                sb.append(x);
            } while (obj != x);

            return sb.toString();
        }

        @Override
        public Object get(int index) {
            return data[index];
        }

        @Override
        public int size() {
        	return currentIndex;
        }

    }

    @Override
    public boolean isWrapperAsCollectionName() {
        return false;
    }
    
    public CoreAttributeGroup getCurrentAttributeGroup() {
        if(this.attributeGroupStack == null || this.attributeGroupStack.isEmpty()) {
            return DEFAULT_ATTRIBUTE_GROUP;
        }
        return attributeGroupStack.peek();
    }
    
    public void pushAttributeGroup(CoreAttributeGroup group) {
        if(group == DEFAULT_ATTRIBUTE_GROUP && this.attributeGroupStack == null) {
            return;
        }
        if(this.attributeGroupStack == null) {
            this.attributeGroupStack = new Stack<CoreAttributeGroup>();
        }
        this.attributeGroupStack.push(group);
    }
    
    public void popAttributeGroup() {
        if(attributeGroupStack != null) {
            attributeGroupStack.pop();
        }
    }
}