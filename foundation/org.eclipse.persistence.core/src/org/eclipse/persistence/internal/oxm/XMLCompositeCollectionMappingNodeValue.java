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
package org.eclipse.persistence.internal.oxm;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.core.queries.CoreAttributeItem;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.CompositeCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.InverseReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.internal.oxm.record.deferred.CompositeCollectionMappingContentHandler;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
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
    private CompositeCollectionMapping xmlCompositeCollectionMapping;
    private int index = -1;
    private boolean isInverseReference;

    public XMLCompositeCollectionMappingNodeValue(CompositeCollectionMapping xmlCompositeCollectionMapping) {
        super();
        this.xmlCompositeCollectionMapping = xmlCompositeCollectionMapping;
    }
    
    
    public XMLCompositeCollectionMappingNodeValue(CompositeCollectionMapping xmlCompositeCollectionMapping, boolean isInverse) {
        this(xmlCompositeCollectionMapping);
        this.isInverseReference = isInverse;
    }


    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlCompositeCollectionMapping.isReadOnly()) {
            return false;
        }

        Object collection = xmlCompositeCollectionMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (null == collection) {
            AbstractNullPolicy wrapperNP = xmlCompositeCollectionMapping.getWrapperNullPolicy();
            if (wrapperNP != null && wrapperNP.getMarshalNullRepresentation() == XMLNullRepresentationType.XSI_NIL) {
                marshalRecord.nilSimple(namespaceResolver);
                return true;
            } else {
                return false;
            }
        }
        CoreContainerPolicy cp = getContainerPolicy();
        Object iterator = cp.iteratorFor(collection);
        if (null != iterator && cp.hasNext(iterator)) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
        	return marshalRecord.emptyCollection(xPathFragment, namespaceResolver, xmlCompositeCollectionMapping.getWrapperNullPolicy() != null);
        }
        marshalRecord.startCollection(); 
        
        int size =marshalRecord.getCycleDetectionStack().size(); 
        //when writing the collection need to see if any of the objects we are writing are in the parent collection inverse ref
        if((isInverseReference || xmlCompositeCollectionMapping.getInverseReferenceMapping() !=null)&& size >= 2){
            Object owner = marshalRecord.getCycleDetectionStack().get(size - 2);
        	if(cp.contains(owner, collection, session)){
        		return false;
        	}
         }
        iterator = cp.iteratorFor(collection);
        while (cp.hasNext(iterator)) {
            Object objectValue = cp.next(iterator, session);
            marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }
        marshalRecord.endCollection();
        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
        	Descriptor xmlDescriptor = (Descriptor)xmlCompositeCollectionMapping.getReferenceDescriptor();
            if (xmlDescriptor == null) {
                xmlDescriptor = findReferenceDescriptor(xPathFragment,unmarshalRecord, atts, xmlCompositeCollectionMapping, xmlCompositeCollectionMapping.getKeepAsElementPolicy());
                
                if(xmlDescriptor == null){
                	if (unmarshalRecord.getXMLReader().isNullRepresentedByXsiNil(xmlCompositeCollectionMapping.getNullPolicy())){
                		if(unmarshalRecord.isNil()){
                            return true;
                		}
                    } else if(xmlCompositeCollectionMapping.getNullPolicy().valueIsNull(atts)){ 
                    	 getContainerPolicy().addInto(null, unmarshalRecord.getContainerInstance(this), unmarshalRecord.getSession());
                         return true;
                    }
                    if(xmlCompositeCollectionMapping.getField() != null){
                        //try leaf element type
                        QName leafType = ((Field)xmlCompositeCollectionMapping.getField()).getLastXPathFragment().getLeafElementType();
                        if (leafType != null) {
                            XPathFragment frag = new XPathFragment();
                            frag.setNamespaceAware(unmarshalRecord.isNamespaceAware());

                            String xpath = leafType.getLocalPart();
                            String uri = leafType.getNamespaceURI();
                            if (uri != null && uri.length() > 0) {
                                frag.setNamespaceURI(uri);
                                String prefix = ((Descriptor)xmlCompositeCollectionMapping.getDescriptor()).getNonNullNamespaceResolver().resolveNamespaceURI(uri);
                                if (prefix != null && prefix.length() > 0) {
                                    xpath = prefix + Constants.COLON + xpath;
                                }
                            }
                            frag.setXPath(xpath);     
                            Context xmlContext = unmarshalRecord.getUnmarshaller().getContext();
                            xmlDescriptor =  xmlContext.getDescriptorByGlobalType(frag);
                        }
                    }
                } 
                    
                UnmarshalKeepAsElementPolicy policy = xmlCompositeCollectionMapping.getKeepAsElementPolicy();
                if (policy != null && ((xmlDescriptor == null && policy.isKeepUnknownAsElement()) || policy.isKeepAllAsElement())) {
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

            AbstractNullPolicy nullPolicy = xmlCompositeCollectionMapping.getNullPolicy();
            if(nullPolicy.isNullRepresentedByEmptyNode()) {
                String qnameString = xPathFragment.getLocalName();
                if(xPathFragment.getPrefix() != null) {
                    qnameString = xPathFragment.getPrefix()  + Constants.COLON + qnameString;
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
            } else if (!(unmarshalRecord.getXMLReader().isNullRepresentedByXsiNil(nullPolicy) && unmarshalRecord.isNil())) {            	
            	Field xmlFld = (Field) this.xmlCompositeCollectionMapping.getField();
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
        if(unmarshalRecord.isNil() && unmarshalRecord.getXMLReader().isNullRepresentedByXsiNil(xmlCompositeCollectionMapping.getNullPolicy())){
        	if(unmarshalRecord.getXMLReader().isInCollection()){
        		unmarshalRecord.addAttributeValue(this, null);
        	}else{
        		unmarshalRecord.setAttributeValueNull(this);
        	}
            unmarshalRecord.resetStringBuffer();        	
            return;
        }
        if (null == unmarshalRecord.getChildRecord()) {
               SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
               UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlCompositeCollectionMapping.getKeepAsElementPolicy();        
                              
               if (null != keepAsElementPolicy && (keepAsElementPolicy.isKeepUnknownAsElement() || keepAsElementPolicy.isKeepAllAsElement()) && builder.getNodes().size() > 1) {
                   if(unmarshalRecord.getTypeQName() != null){
                       Class theClass = (Class)((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(unmarshalRecord.getTypeQName());
                       if(theClass != null){
                           //handle simple text
                           endElementProcessText(unmarshalRecord, xmlCompositeCollectionMapping, xPathFragment, collection);
                           return;
                       }
                   }
            	   if(builder.getNodes().size() > 1) {
                       setOrAddAttributeValueForKeepAsElement(builder, xmlCompositeCollectionMapping, xmlCompositeCollectionMapping, unmarshalRecord, true, collection);
                       return;
                   }
               }else{
                    //handle simple text
                    endElementProcessText(unmarshalRecord, xmlCompositeCollectionMapping, xPathFragment, collection);
                    return;
               }

               return;
        }
        Object objectValue = unmarshalRecord.getChildRecord().getCurrentObject();

        InverseReferenceMapping inverseReferenceMapping = xmlCompositeCollectionMapping.getInverseReferenceMapping();
        if(null != inverseReferenceMapping) {
            if(inverseReferenceMapping.getContainerPolicy() == null) {
            	Object currentValue = inverseReferenceMapping.getAttributeAccessor().getAttributeValueFromObject(objectValue);
                if( !isInverseReference || (currentValue == null && isInverseReference)) {
                   inverseReferenceMapping.getAttributeAccessor().setAttributeValueInObject(objectValue, unmarshalRecord.getCurrentObject());
                }
            } else {
                Object backpointerContainer = inverseReferenceMapping.getAttributeAccessor().getAttributeValueFromObject(objectValue);
                if(backpointerContainer == null) {
                    backpointerContainer = inverseReferenceMapping.getContainerPolicy().containerInstance();
                    inverseReferenceMapping.getAttributeAccessor().setAttributeValueInObject(objectValue, backpointerContainer);
                }
                inverseReferenceMapping.getContainerPolicy().addInto(unmarshalRecord.getCurrentObject(), backpointerContainer, unmarshalRecord.getSession());
            }
        }
        
        // convert the value - if necessary
        objectValue = xmlCompositeCollectionMapping.convertDataValueToObjectValue(objectValue, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
        unmarshalRecord.addAttributeValue(this, objectValue, collection);

        unmarshalRecord.setChildRecord(null);

    }

    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlCompositeCollectionMapping.setAttributeValueInObject(object, containerInstance);
    }

    public CoreContainerPolicy getContainerPolicy() {
        return xmlCompositeCollectionMapping.getContainerPolicy();
    }
    
    public boolean isContainerValue() {
        return true;
    }
    
	public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
      
        Marshaller marshaller = marshalRecord.getMarshaller();
        // convert the value - if necessary
        value = xmlCompositeCollectionMapping.convertObjectValueToDataValue(value, session, marshaller);
        if (null == value) {
        	   return xmlCompositeCollectionMapping.getNullPolicy().compositeObjectMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
        }
        Descriptor descriptor = (Descriptor)xmlCompositeCollectionMapping.getReferenceDescriptor();
        if(descriptor == null){
        	descriptor = (Descriptor) session.getDescriptor(value.getClass());
        }else if(descriptor.hasInheritance()){
        	Class objectValueClass = value.getClass();
        	if(!(objectValueClass == descriptor.getJavaClass())){
        		descriptor = (Descriptor) session.getDescriptor(objectValueClass);
        	}
        }
        
        UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlCompositeCollectionMapping.getKeepAsElementPolicy();
        if (null != keepAsElementPolicy && (keepAsElementPolicy.isKeepUnknownAsElement() || keepAsElementPolicy.isKeepAllAsElement()) && value instanceof org.w3c.dom.Node) {
            marshalRecord.node((org.w3c.dom.Node) value, marshalRecord.getNamespaceResolver());
            return true;
        }
        
        if(descriptor != null){                    
            marshalRecord.beforeContainmentMarshal(value);

            ObjectBuilder objectBuilder = (ObjectBuilder)descriptor.getObjectBuilder();
            xPathNode.startElement(marshalRecord, xPathFragment, object, session, namespaceResolver, objectBuilder, value);            

            List extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session,true, false);
            writeExtraNamespaces(extraNamespaces, marshalRecord, session);

            marshalRecord.addXsiTypeAndClassIndicatorIfRequired(descriptor, (Descriptor) xmlCompositeCollectionMapping.getReferenceDescriptor(), (Field)xmlCompositeCollectionMapping.getField(), false);
            CoreAttributeGroup group = marshalRecord.getCurrentAttributeGroup();
            CoreAttributeGroup nestedGroup = XMLRecord.DEFAULT_ATTRIBUTE_GROUP;
            CoreAttributeItem item = group.getItem(getMapping().getAttributeName());
            if(item != null) {
                if(item.getGroups() != null) {
                    nestedGroup = item.getGroup(descriptor.getJavaClass());
                } 
                if(nestedGroup == null) {
                    nestedGroup = item.getGroup() == null?XMLRecord.DEFAULT_ATTRIBUTE_GROUP:item.getGroup();
                }
            }
            marshalRecord.pushAttributeGroup(nestedGroup);
            objectBuilder.buildRow(marshalRecord, value, session, marshaller, xPathFragment);
            marshalRecord.afterContainmentMarshal(object, value);
            marshalRecord.popAttributeGroup();
            marshalRecord.endElement(xPathFragment, namespaceResolver);
            marshalRecord.removeExtraNamespacesFromNamespaceResolver(extraNamespaces, session);    
        } else {            
            if(Constants.UNKNOWN_OR_TRANSIENT_CLASS.equals(xmlCompositeCollectionMapping.getReferenceClassName())){                
                throw XMLMarshalException.descriptorNotFoundInProject(value.getClass().getName());                            
            }
            xPathNode.startElement(marshalRecord, xPathFragment, object, session, namespaceResolver, null, value);
            
            QName schemaType = ((Field) xmlCompositeCollectionMapping.getField()).getSchemaTypeForValue(value, session);
            updateNamespaces(schemaType, marshalRecord,((Field)xmlCompositeCollectionMapping.getField()));
            marshalRecord.characters(schemaType, value, null, false);            
            marshalRecord.endElement(xPathFragment, namespaceResolver);
        }
        return true;
    }

    public CompositeCollectionMapping getMapping() {
        return xmlCompositeCollectionMapping;
    }

    protected void setOrAddAttributeValue(UnmarshalRecord unmarshalRecord, Object value, XPathFragment xPathFragment, Object collection){
        unmarshalRecord.addAttributeValue(this, value, collection);            	
    }

    public boolean getReuseContainer() {
        return xmlCompositeCollectionMapping.getReuseContainer();
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
        return xmlCompositeCollectionMapping.isDefaultEmptyContainer();
    }

    @Override
    public boolean isWrapperAllowedAsCollectionName() {
        return true;
    }

}