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
package org.eclipse.persistence.internal.oxm;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.deferred.CompositeCollectionMappingContentHandler;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Composite Collection Mapping is
 * handled when used with the TreeObjectBuilder.</p>
 */
/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Composite Collection Mapping is
 * handled when used with the TreeObjectBuilder.</p>
 */
public class XMLCompositeCollectionMappingNodeValue extends XMLRelationshipMappingNodeValue implements ContainerValue {
    private XMLCompositeCollectionMapping xmlCompositeCollectionMapping;
    private int index = -1;

    public XMLCompositeCollectionMappingNodeValue(XMLCompositeCollectionMapping xmlCompositeCollectionMapping) {
        super();
        this.xmlCompositeCollectionMapping = xmlCompositeCollectionMapping;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlCompositeCollectionMapping.isReadOnly()) {
            return false;
        }

        ContainerPolicy cp = getContainerPolicy();
        Object collection = xmlCompositeCollectionMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (null == collection) {
            AbstractNullPolicy wrapperNP = xmlCompositeCollectionMapping.getWrapperNullPolicy();
            if (wrapperNP != null && wrapperNP.getMarshalNullRepresentation().equals(XMLNullRepresentationType.XSI_NIL)) {
                marshalRecord.nilSimple(namespaceResolver);
                return true;
            } else {
                return false;
            }
        }
        Object iterator = cp.iteratorFor(collection);
        if (cp.hasNext(iterator)) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
            if (xmlCompositeCollectionMapping.getWrapperNullPolicy() != null ) {
                XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            } else {
                return false;
            }
        }
        Object objectValue;
        while (cp.hasNext(iterator)) {
            objectValue = cp.next(iterator, session);
            marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }
        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            XMLDescriptor xmlDescriptor = (XMLDescriptor)xmlCompositeCollectionMapping.getReferenceDescriptor();
            if (xmlDescriptor == null) {
                xmlDescriptor = findReferenceDescriptor(xPathFragment,unmarshalRecord, atts, xmlCompositeCollectionMapping, xmlCompositeCollectionMapping.getKeepAsElementPolicy());
                
                if(xmlDescriptor == null){
                    if(xmlCompositeCollectionMapping.getField() != null){
                        //try leaf element type
                        QName leafType = ((XMLField)xmlCompositeCollectionMapping.getField()).getLastXPathFragment().getLeafElementType();
                        if (leafType != null) {
                            XPathFragment frag = new XPathFragment();
                            String xpath = leafType.getLocalPart();
                            String uri = leafType.getNamespaceURI();
                            if (uri != null && uri.length() > 0) {
                                frag.setNamespaceURI(uri);
                                String prefix = ((XMLDescriptor)xmlCompositeCollectionMapping.getDescriptor()).getNonNullNamespaceResolver().resolveNamespaceURI(uri);
                                if (prefix != null && prefix.length() > 0) {
                                    xpath = prefix + XMLConstants.COLON + xpath;
                                }
                            }
                            frag.setXPath(xpath);     
                            XMLContext xmlContext = unmarshalRecord.getUnmarshaller().getXMLContext();
                            xmlDescriptor = xmlContext.getDescriptorByGlobalType(frag);
                        }
                    }
                } 
                    
                UnmarshalKeepAsElementPolicy policy = xmlCompositeCollectionMapping.getKeepAsElementPolicy();
                if (((xmlDescriptor == null) && (policy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT)) || (policy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) {
                    if(unmarshalRecord.getTypeQName() != null){
                        Class theClass = (Class)((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(unmarshalRecord.getTypeQName());
                        if(theClass == null){
                            setupHandlerForKeepAsElementPolicy(unmarshalRecord, xPathFragment, atts);
                            return true;
                        }
                    }else{
                        setupHandlerForKeepAsElementPolicy(unmarshalRecord, xPathFragment, atts);
                        return true;
                    }
                }          
            }

            if(xmlCompositeCollectionMapping.getNullPolicy().isNullRepresentedByEmptyNode()) {
                String qnameString = xPathFragment.getLocalName();
                if(xPathFragment.getPrefix() != null) {
                    qnameString = xPathFragment.getPrefix()  + XMLConstants.COLON + qnameString;
                }
                if(null != xmlDescriptor) {
                    // Process null capable value
                    CompositeCollectionMappingContentHandler aHandler = new CompositeCollectionMappingContentHandler(//
                        unmarshalRecord, this, xmlCompositeCollectionMapping, atts, xPathFragment, xmlDescriptor);
                    // Send control to the handler
                    aHandler.startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), qnameString, atts);
                    XMLReader xmlReader = unmarshalRecord.getXMLReader();
                    xmlReader.setContentHandler(aHandler);
                    xmlReader.setLexicalHandler(aHandler);
                }
            } else if (xmlCompositeCollectionMapping.getNullPolicy().valueIsNull(atts)) {
                getContainerPolicy().addInto(null, unmarshalRecord.getContainerInstance(this), unmarshalRecord.getSession());
            } else {
                XMLField xmlFld = (XMLField) this.xmlCompositeCollectionMapping.getField();
                if (xmlFld.hasLastXPathFragment()) {
                    unmarshalRecord.setLeafElementType(xmlFld.getLastXPathFragment().getLeafElementType());
                }
                processChild(xPathFragment, unmarshalRecord, atts, xmlDescriptor, xmlCompositeCollectionMapping);
            }
        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
        return true;
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        Object collection = unmarshalRecord.getContainerInstance(this);
        endElement(xPathFragment, unmarshalRecord, collection);
    }
    
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Object collection) {
        if (null == unmarshalRecord.getChildRecord()) {
               SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
               UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlCompositeCollectionMapping.getKeepAsElementPolicy();        
                              
               if ((((keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT))) && (builder.getNodes().size() != 0)) {
                   if(unmarshalRecord.getTypeQName() != null){
                       Class theClass = (Class)((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(unmarshalRecord.getTypeQName());
                       if(theClass != null){
                           //handle simple text
                           endElementProcessText(unmarshalRecord, xmlCompositeCollectionMapping.getConverter(), xPathFragment, collection);
                           return;
                       }
                   }
            	   
                   if (builder.getDocument() != null) {
                       setOrAddAttributeValueForKeepAsElement(builder, (XMLMapping) xmlCompositeCollectionMapping, (XMLConverter) xmlCompositeCollectionMapping.getConverter(), unmarshalRecord, true, collection);
                       return;
                   }
               }else{
                    //handle simple text
                    endElementProcessText(unmarshalRecord, xmlCompositeCollectionMapping.getConverter(), xPathFragment, collection);
                    return;
               }

               return;
        }
        // convert the value - if necessary
        Object objectValue = unmarshalRecord.getChildRecord().getCurrentObject();
        if (xmlCompositeCollectionMapping.hasConverter()) {
            Converter converter = xmlCompositeCollectionMapping.getConverter();
            if (converter instanceof XMLConverter) {
                objectValue = ((XMLConverter)converter).convertDataValueToObjectValue(objectValue, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
            } else {
                objectValue = converter.convertObjectValueToDataValue(objectValue, unmarshalRecord.getSession());
            }
        }
        unmarshalRecord.addAttributeValue(this, objectValue, collection);

        XMLInverseReferenceMapping inverseReferenceMapping = xmlCompositeCollectionMapping.getInverseReferenceMapping();
        if(null != inverseReferenceMapping) {
            if(inverseReferenceMapping.getContainerPolicy() == null) {
                inverseReferenceMapping.getAttributeAccessor().setAttributeValueInObject(objectValue, unmarshalRecord.getCurrentObject());
            } else {
                Object backpointerContainer = inverseReferenceMapping.getAttributeAccessor().getAttributeValueFromObject(objectValue);
                if(backpointerContainer == null) {
                    backpointerContainer = inverseReferenceMapping.getContainerPolicy().containerInstance();
                    inverseReferenceMapping.getAttributeAccessor().setAttributeValueInObject(objectValue, backpointerContainer);
                }
                inverseReferenceMapping.getContainerPolicy().addInto(unmarshalRecord.getCurrentObject(), backpointerContainer, unmarshalRecord.getSession());
            }
        }
        unmarshalRecord.setChildRecord(null);

    }

    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlCompositeCollectionMapping.setAttributeValueInObject(object, containerInstance);
    }

    public ContainerPolicy getContainerPolicy() {
        return xmlCompositeCollectionMapping.getContainerPolicy();
    }
    
    public boolean isContainerValue() {
        return true;
    }
    
    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xPathFragment.hasLeafElementType()) {
            marshalRecord.setLeafElementType(xPathFragment.getLeafElementType());
        }
        XMLMarshaller marshaller = marshalRecord.getMarshaller();
        // convert the value - if necessary
        if (xmlCompositeCollectionMapping.hasConverter()) {
            Converter converter = xmlCompositeCollectionMapping.getConverter();
            if (converter instanceof XMLConverter) {
                value = ((XMLConverter)converter).convertObjectValueToDataValue(value, session, marshaller);
            } else {
                value = converter.convertObjectValueToDataValue(value, session);
            }
        }
        if (null == value) {
            return xmlCompositeCollectionMapping.getNullPolicy().compositeObjectMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
        }

        XMLDescriptor descriptor = (XMLDescriptor)xmlCompositeCollectionMapping.getReferenceDescriptor();
        if(descriptor == null || (descriptor.hasInheritance() && !(descriptor.getJavaClass() == value.getClass()))){
        	descriptor = (XMLDescriptor)session.getDescriptor(value.getClass());
        }
        
        UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlCompositeCollectionMapping.getKeepAsElementPolicy();
        if (((keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) && value instanceof org.w3c.dom.Node) {
            marshalRecord.node((org.w3c.dom.Node) value, marshalRecord.getNamespaceResolver());
            return true;
        }
        
        if(descriptor != null){                    
            marshalRecord.beforeContainmentMarshal(value);

            TreeObjectBuilder objectBuilder = (TreeObjectBuilder)descriptor.getObjectBuilder();
            getXPathNode().startElement(marshalRecord, xPathFragment, object, session, namespaceResolver, objectBuilder, value);            
    
            List extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session);
            writeExtraNamespaces(extraNamespaces, marshalRecord, session);

            objectBuilder.addXsiTypeAndClassIndicatorIfRequired(marshalRecord, descriptor, (XMLDescriptor) xmlCompositeCollectionMapping.getReferenceDescriptor(), (XMLField)xmlCompositeCollectionMapping.getField(), false);
            
            objectBuilder.buildRow(marshalRecord, value, session, marshaller, xPathFragment, WriteType.UNDEFINED);
            marshalRecord.afterContainmentMarshal(object, value);
            marshalRecord.endElement(xPathFragment, namespaceResolver);
            objectBuilder.removeExtraNamespacesFromNamespaceResolver(marshalRecord, extraNamespaces, session);    
           
        } else {
            getXPathNode().startElement(marshalRecord, xPathFragment, object, session, namespaceResolver, null, value);
            
            QName schemaType = getSchemaType((XMLField) xmlCompositeCollectionMapping.getField(), value, session);
            String stringValue = getValueToWrite(schemaType, value, (XMLConversionManager) session.getDatasourcePlatform().getConversionManager(), marshalRecord);
            updateNamespaces(schemaType, marshalRecord,((XMLField)xmlCompositeCollectionMapping.getField()));

            marshalRecord.characters(stringValue);
            marshalRecord.endElement(xPathFragment, namespaceResolver);
        }
        return true;
    }

    public XMLCompositeCollectionMapping getMapping() {
        return xmlCompositeCollectionMapping;
    }

    protected void setOrAddAttributeValue(UnmarshalRecord unmarshalRecord, Object value, XPathFragment xPathFragment, Object collection){
        unmarshalRecord.addAttributeValue(this, value, collection);            	
    }

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }
    
    /**
     *  INTERNAL:
     *  Used to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord 
     */  
    public void setIndex(int index){
    	this.index = index;
    }
    
    /**
     * INTERNAL:
     * Set to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord
     * Set during TreeObjectBuilder initialization 
     */
    public int getIndex(){
    	return index;
    }

    /**
     * INTERNAL
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     * @since EclipseLink 2.3.3
     */
    public boolean isDefaultEmptyContainer() {
        return getMapping().isDefaultEmptyContainer();
    }

}