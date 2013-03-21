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

import java.lang.reflect.Modifier;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.core.queries.CoreAttributeItem;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.CompositeObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.DirectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.InverseReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.internal.oxm.record.deferred.CompositeObjectMappingContentHandler;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Composite Object Mapping is handled
 * when used with the TreeObjectBuilder.</p>
 */
public class XMLCompositeObjectMappingNodeValue extends XMLRelationshipMappingNodeValue implements NullCapableValue {
    private CompositeObjectMapping xmlCompositeObjectMapping;
    private boolean isInverseReference;  

    public XMLCompositeObjectMappingNodeValue(CompositeObjectMapping xmlCompositeObjectMapping) {
        this.xmlCompositeObjectMapping = xmlCompositeObjectMapping;
    }

    public XMLCompositeObjectMappingNodeValue(CompositeObjectMapping xmlCompositeObjectMapping, boolean isInverse) {
        this(xmlCompositeObjectMapping);
        isInverseReference = isInverse;
    }

    @Override
    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        unmarshalRecord.removeNullCapableValue(this);

        Descriptor referenceDescriptor = (Descriptor) getMapping().getReferenceDescriptor();
        ObjectBuilder treeObjectBuilder = (ObjectBuilder) referenceDescriptor.getObjectBuilder();
        MappingNodeValue textMappingNodeValue = (MappingNodeValue) treeObjectBuilder.getRootXPathNode().getTextNode().getNodeValue();
        Mapping textMapping = textMappingNodeValue.getMapping();
        Object childObject = referenceDescriptor.getInstantiationPolicy().buildNewInstance();
        if(textMapping.isAbstractDirectMapping()) {
            DirectMapping xmlDirectMapping = (DirectMapping) textMappingNodeValue.getMapping();
            Field xmlField = (Field) xmlDirectMapping.getField();
            Object realValue = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager(), unmarshalRecord);
            Object convertedValue = xmlDirectMapping.getAttributeValue(realValue, unmarshalRecord.getSession(), unmarshalRecord);
            xmlDirectMapping.setAttributeValueInObject(childObject, convertedValue);
        } else {
            Object oldChildObject = unmarshalRecord.getCurrentObject();
            CompositeObjectMapping nestedXMLCompositeObjectMapping = (CompositeObjectMapping) textMappingNodeValue.getMapping();
            unmarshalRecord.setCurrentObject(childObject);
            textMappingNodeValue.attribute(unmarshalRecord, namespaceURI, localName, value);
            unmarshalRecord.setCurrentObject(oldChildObject);
        }
        setAttributeValue(childObject, unmarshalRecord);
    }

    /**
     * Marshal any 'self' mapped attributes.
     *
     * @param xPathFragment
     * @param marshalRecord
     * @param object
     * @param session
     * @param namespaceResolver
     * @param marshaller
     * @return
     */
    @Override
    public boolean marshalSelfAttributes(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, Marshaller marshaller) {
        Object objectValue = xmlCompositeObjectMapping.getAttributeValueFromObject(object);
        objectValue = xmlCompositeObjectMapping.convertObjectValueToDataValue(objectValue, session, marshaller);
        Descriptor descriptor = (Descriptor)session.getDescriptor(objectValue);
        if(descriptor != null){
            ObjectBuilder objectBuilder = (ObjectBuilder)descriptor.getObjectBuilder();
            return objectBuilder.marshalAttributes(marshalRecord, objectValue, session);
        } else {
            UnmarshalKeepAsElementPolicy keepAsElementPolicy = getMapping().getKeepAsElementPolicy();
            if(null != keepAsElementPolicy && (keepAsElementPolicy.isKeepAllAsElement() || keepAsElementPolicy.isKeepUnknownAsElement())) {
                if(objectValue instanceof Node) {
                    Node rootNode = (Node)objectValue;
                    NamedNodeMap attributes = rootNode.getAttributes();
                    for(int i = 0; i < attributes.getLength(); i++) {
                        Attr next = (Attr)attributes.item(i);
                        if(!(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(next.getNamespaceURI()))) {
                            marshalRecord.node(next, namespaceResolver);
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlCompositeObjectMapping.isReadOnly()) {
            return false;
        }
    	int size =marshalRecord.getCycleDetectionStack().size(); 
        Object objectValue = marshalContext.getAttributeValue(object, xmlCompositeObjectMapping);
        
        if((isInverseReference || xmlCompositeObjectMapping.getInverseReferenceMapping() !=null)&& objectValue !=null && size >= 2){        	
    	    Object owner = marshalRecord.getCycleDetectionStack().get(size - 2);
    	    if(owner.equals(objectValue)){
    	    	return false;
    	    }        	    	
        }

        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        objectValue = xmlCompositeObjectMapping.convertObjectValueToDataValue(objectValue, session, marshalRecord.getMarshaller());
        if (null == objectValue) {
            return xmlCompositeObjectMapping.getNullPolicy().compositeObjectMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
        }
        
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        if(xPathFragment.hasAttribute) {
            ObjectBuilder tob = (ObjectBuilder) xmlCompositeObjectMapping.getReferenceDescriptor().getObjectBuilder();
            MappingNodeValue textMappingNodeValue = (MappingNodeValue) tob.getRootXPathNode().getTextNode().getMarshalNodeValue();
            Mapping textMapping = textMappingNodeValue.getMapping();
            if(textMapping.isAbstractDirectMapping()) {
                DirectMapping xmlDirectMapping = (DirectMapping) textMapping;
                Object fieldValue = xmlDirectMapping.getFieldValue(xmlDirectMapping.valueFromObject(objectValue, xmlDirectMapping.getField(), session), session, marshalRecord);
                QName schemaType = ((Field) xmlDirectMapping.getField()).getSchemaTypeForValue(fieldValue, session);
                marshalRecord.attribute(xPathFragment, namespaceResolver, fieldValue, schemaType);
                marshalRecord.closeStartGroupingElements(groupingFragment);
                return true;
            } else {
                return textMappingNodeValue.marshalSingleValue(xPathFragment, marshalRecord, objectValue, textMapping.getAttributeValueFromObject(objectValue), session, namespaceResolver, marshalContext);
            }
        }
        boolean isSelfFragment = xPathFragment.isSelfFragment;
        marshalRecord.closeStartGroupingElements(groupingFragment);

        UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlCompositeObjectMapping.getKeepAsElementPolicy();
        if (null != keepAsElementPolicy && (keepAsElementPolicy.isKeepUnknownAsElement() || keepAsElementPolicy.isKeepAllAsElement()) && objectValue instanceof Node) {
            if (isSelfFragment) {
                NodeList children = ((org.w3c.dom.Element) objectValue).getChildNodes();
                for (int i = 0, childrenLength = children.getLength(); i < childrenLength ; i++) {
                    Node next = children.item(i);
                    short nodeType = next.getNodeType();
                    if (nodeType == Node.ELEMENT_NODE) {
                        marshalRecord.node(next, marshalRecord.getNamespaceResolver());
                        return true;
                    } else if (nodeType == Node.TEXT_NODE) {
                        marshalRecord.characters(((Text) next).getNodeValue());
                        return true;
                    }
                }
                return false;
            } else {
                marshalRecord.node((Node) objectValue, marshalRecord.getNamespaceResolver());
                return true;
            }
        }
        Descriptor descriptor = (Descriptor)xmlCompositeObjectMapping.getReferenceDescriptor();  
        if(descriptor == null){
        	descriptor = (Descriptor) session.getDescriptor(objectValue.getClass());
        }else if(descriptor.hasInheritance()){
        	Class objectValueClass = objectValue.getClass();
        	if(!(objectValueClass == descriptor.getJavaClass())){
        		descriptor = (Descriptor) session.getDescriptor(objectValueClass);
        	}
        }

        if(descriptor != null){
            marshalRecord.beforeContainmentMarshal(objectValue);
            ObjectBuilder objectBuilder = (ObjectBuilder)descriptor.getObjectBuilder();

            if (!(isSelfFragment || xPathFragment.nameIsText)) {
                xPathNode.startElement(marshalRecord, xPathFragment, object, session, namespaceResolver, objectBuilder, objectValue);
            }

            List extraNamespaces = null;
            if (!marshalRecord.hasEqualNamespaceResolvers()) {
                extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session, true, false);
                writeExtraNamespaces(extraNamespaces, marshalRecord, session);
            }
            if(!isSelfFragment) {
                marshalRecord.addXsiTypeAndClassIndicatorIfRequired(descriptor, (Descriptor) xmlCompositeObjectMapping.getReferenceDescriptor(), (Field)xmlCompositeObjectMapping.getField(), false);
            }

            CoreAttributeGroup group = marshalRecord.getCurrentAttributeGroup();
            CoreAttributeItem item = group.getItem(getMapping().getAttributeName());
            CoreAttributeGroup nestedGroup = XMLRecord.DEFAULT_ATTRIBUTE_GROUP;
            if(item != null) {
                if(item.getGroups() != null) {
                    nestedGroup = item.getGroup(descriptor.getJavaClass());
                } 
                if(nestedGroup == null) {
                    nestedGroup = item.getGroup() == null?XMLRecord.DEFAULT_ATTRIBUTE_GROUP:item.getGroup();
                }
            }
            marshalRecord.pushAttributeGroup(nestedGroup);
            objectBuilder.buildRow(marshalRecord, objectValue, session, marshalRecord.getMarshaller(), xPathFragment);
            marshalRecord.afterContainmentMarshal(object, objectValue);
            marshalRecord.popAttributeGroup();

            if (!(isSelfFragment || xPathFragment.nameIsText())) {
                marshalRecord.endElement(xPathFragment, namespaceResolver);
            }
            marshalRecord.removeExtraNamespacesFromNamespaceResolver(extraNamespaces, session);
        } else {            
            if(Constants.UNKNOWN_OR_TRANSIENT_CLASS.equals(xmlCompositeObjectMapping.getReferenceClassName())){
                throw XMLMarshalException.descriptorNotFoundInProject(objectValue.getClass().getName());
            }
            
            if (!(isSelfFragment || xPathFragment.nameIsText())) {
                xPathNode.startElement(marshalRecord, xPathFragment, object, session, namespaceResolver, null, objectValue);
            }
            QName schemaType = ((Field) xmlCompositeObjectMapping.getField()).getSchemaTypeForValue(objectValue,session);

            updateNamespaces(schemaType, marshalRecord,((Field)xmlCompositeObjectMapping.getField()));
            marshalRecord.characters(schemaType, objectValue, null, false);

            if (!(isSelfFragment || xPathFragment.nameIsText())) {
                marshalRecord.endElement(xPathFragment, namespaceResolver);
            }
        }
        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            unmarshalRecord.removeNullCapableValue(this);

            Descriptor xmlDescriptor = (Descriptor)xmlCompositeObjectMapping.getReferenceDescriptor();
            if (null == xmlDescriptor) {
                xmlDescriptor = findReferenceDescriptor(xPathFragment, unmarshalRecord, atts, xmlCompositeObjectMapping,xmlCompositeObjectMapping.getKeepAsElementPolicy());

                if(xmlDescriptor == null){
                    if(xmlCompositeObjectMapping.getField() != null){
                        //try leaf element type
                        QName leafType = ((Field)xmlCompositeObjectMapping.getField()).getLastXPathFragment().getLeafElementType();
                        if (leafType != null) {
                            XPathFragment frag = new XPathFragment();
                            frag.setNamespaceAware(unmarshalRecord.isNamespaceAware());
                            String xpath = leafType.getLocalPart();
                            String uri = leafType.getNamespaceURI();
                            if (uri != null && uri.length() > 0) {
                                frag.setNamespaceURI(uri);
                                String prefix = ((Descriptor)xmlCompositeObjectMapping.getDescriptor()).getNonNullNamespaceResolver().resolveNamespaceURI(uri);
                                if (prefix != null && prefix.length() > 0) {
                                    xpath = prefix + Constants.COLON + xpath;
                                }
                            }
                            frag.setXPath(xpath);
                            Context xmlContext = unmarshalRecord.getUnmarshaller().getContext();
                            xmlDescriptor = xmlContext.getDescriptorByGlobalType(frag);
                        }
                    }
                }

                UnmarshalKeepAsElementPolicy policy = xmlCompositeObjectMapping.getKeepAsElementPolicy();
                if (null != policy && ((xmlDescriptor == null && policy.isKeepUnknownAsElement()) || policy.isKeepAllAsElement())) {
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

            //
            //  Null Composite Objects are marshalled in 2 ways when the input XML node is empty.
             // (1) as null
             //     - isNullRepresentedByEmptyNode = true
            //  (2) as empty object
            //      - isNullRepresentedByEmptyNode = false
            //   A deferred contentHandler is used to queue events until we are able to determine
            //   whether we are in one of empty/simple/complex state.
            //   Control is returned to the UnmarshalHandler after creation of (1) or (2) above is started.
            //   Object creation was deferred to the DeferredContentHandler
            //
            // Check if we need to create the DeferredContentHandler based on policy state
            AbstractNullPolicy nullPolicy = xmlCompositeObjectMapping.getNullPolicy();
            if(nullPolicy.isNullRepresentedByEmptyNode()) {
                String qnameString = xPathFragment.getLocalName();
                if(xPathFragment.getPrefix() != null) {
                    qnameString = xPathFragment.getPrefix()  + Constants.COLON + qnameString;
                }
                if(null != xmlDescriptor) {
                    // Process null capable value
                    CompositeObjectMappingContentHandler aHandler = new CompositeObjectMappingContentHandler(//
                        unmarshalRecord, this, xmlCompositeObjectMapping, atts, xPathFragment, xmlDescriptor);
                    // Send control to the handler
                    aHandler.startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), qnameString, atts);
                    XMLReader xmlReader = unmarshalRecord.getXMLReader();
                    xmlReader.setContentHandler(aHandler);
                    xmlReader.setLexicalHandler(aHandler);
                }
            } else {
            	if(unmarshalRecord.getXMLReader().isNullRepresentedByXsiNil(nullPolicy) && unmarshalRecord.isNil()){
                    xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), null);
                } else {
                	Field xmlFld = (Field)this.xmlCompositeObjectMapping.getField();
                    if (xmlFld.hasLastXPathFragment()) {
                        unmarshalRecord.setLeafElementType(xmlFld.getLastXPathFragment().getLeafElementType());
                    }
                    processChild(xPathFragment, unmarshalRecord, atts, xmlDescriptor, xmlCompositeObjectMapping);
                }
            }

        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
        return true;
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        if(unmarshalRecord.isNil() && xmlCompositeObjectMapping.getNullPolicy().isNullRepresentedByXsiNil()){
            unmarshalRecord.resetStringBuffer();
            return;
        }
        
        if (null == unmarshalRecord.getChildRecord()) {
            SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
            UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlCompositeObjectMapping.getKeepAsElementPolicy();

            if (null != keepAsElementPolicy && (keepAsElementPolicy.isKeepUnknownAsElement() || keepAsElementPolicy.isKeepAllAsElement()) && builder.getNodes().size() != 0) {

                if(unmarshalRecord.getTypeQName() != null){
                    Class theClass = (Class)((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(unmarshalRecord.getTypeQName());
                    if(theClass != null){
                        //handle simple text
                        endElementProcessText(unmarshalRecord, xmlCompositeObjectMapping, xPathFragment, null);
                        return;
                    }
                }

                if (builder.getDocument() != null) {
                    setOrAddAttributeValueForKeepAsElement(builder, xmlCompositeObjectMapping, xmlCompositeObjectMapping, unmarshalRecord, false, null);
                    return;
                }
            }else{
                //handle simple text
                endElementProcessText(unmarshalRecord, xmlCompositeObjectMapping, xPathFragment, null);
                return;
            }

        } else {
            Object object = unmarshalRecord.getChildRecord().getCurrentObject();
            setAttributeValue(object, unmarshalRecord);
            unmarshalRecord.setChildRecord(null);
        }
    }

    private void setAttributeValue(Object object, UnmarshalRecord unmarshalRecord) {
        InverseReferenceMapping inverseReferenceMapping = xmlCompositeObjectMapping.getInverseReferenceMapping();
        
        //If isInverseReference then this mapping is an inlineMapping of an InverseReference
        if(null != inverseReferenceMapping){
            if(inverseReferenceMapping.getContainerPolicy() == null) {
            	Object currentValue = inverseReferenceMapping.getAttributeAccessor().getAttributeValueFromObject(object);
                if( !isInverseReference || (currentValue == null && isInverseReference)) {
                    inverseReferenceMapping.getAttributeAccessor().setAttributeValueInObject(object, unmarshalRecord.getCurrentObject());
                }
            } else {
                Object backpointerContainer = inverseReferenceMapping.getAttributeAccessor().getAttributeValueFromObject(object);
                if(backpointerContainer == null) {
                    backpointerContainer = inverseReferenceMapping.getContainerPolicy().containerInstance();
                    inverseReferenceMapping.getAttributeAccessor().setAttributeValueInObject(object, backpointerContainer);
                }
                inverseReferenceMapping.getContainerPolicy().addInto(unmarshalRecord.getCurrentObject(), backpointerContainer, unmarshalRecord.getSession());
            }
        }

        object = xmlCompositeObjectMapping.convertDataValueToObjectValue(object, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
        // Set the child object on the parent
        unmarshalRecord.setAttributeValue(object, xmlCompositeObjectMapping); 
        
    }

    public void endSelfNodeValue(UnmarshalRecord unmarshalRecord, UnmarshalRecord selfRecord, Attributes attributes) {
        if(xmlCompositeObjectMapping.getNullPolicy().valueIsNull(attributes)){
            xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), null);
            return;
        }
        unmarshalRecord.removeNullCapableValue(this);
        if (unmarshalRecord.getFragmentBuilder().getDocument() != null) {
            UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlCompositeObjectMapping.getKeepAsElementPolicy();

            SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
            if ((((keepAsElementPolicy.isKeepUnknownAsElement()) || (keepAsElementPolicy.isKeepAllAsElement())))&& (builder.getNodes().size() != 0) ) {
                if(unmarshalRecord.getTypeQName() != null){
                    Class theClass = (Class)((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(unmarshalRecord.getTypeQName());
                    if(theClass != null){
                        //handle simple text
                        endElementProcessText(unmarshalRecord, xmlCompositeObjectMapping, null, null);
                        return;
                    }
                }
                Element element = (Element) builder.getNodes().remove(builder.getNodes().size() -1);

                String xsiType = null;
                if(null != element) {
                	if(unmarshalRecord.isNamespaceAware()){
                            xsiType = element.getAttributeNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE);
                	}else{
               		    xsiType = element.getAttribute(Constants.SCHEMA_TYPE_ATTRIBUTE);
                	}
                }
                if(null != xsiType) {
                    xsiType = xsiType.trim();
                    Object value = element;
                    String namespace = null;
                    int colonIndex = xsiType.indexOf(unmarshalRecord.getNamespaceSeparator());
                    if (colonIndex > -1) {
                        String prefix = xsiType.substring(0, colonIndex);
                        namespace = unmarshalRecord.resolveNamespacePrefix(prefix);
                        if(null == namespace) {
                            namespace = XMLPlatformFactory.getInstance().getXMLPlatform().resolveNamespacePrefix(element, prefix);
                        }
                        String name = xsiType.substring(colonIndex + 1);
                        QName qName = new QName(namespace, xsiType.substring(colonIndex + 1));
                        Class theClass = (Class) XMLConversionManager.getDefaultXMLTypes().get(qName);
                        if (theClass != null) {
                            value = ((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).convertObject(element.getTextContent(), theClass, qName);
                        }
                    }else{
                    	if(!unmarshalRecord.isNamespaceAware()){
                            QName qName = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, xsiType);

                    		Class theClass = (Class) XMLConversionManager.getDefaultXMLTypes().get(qName);
                            if (theClass != null) {
                                value = ((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).convertObject(element.getTextContent(), theClass, qName);
                            }
                    	}
                    }
                    xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), value);
                } else {
                    xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), element);
                }
            }
        } else {
            Object valueToSet = selfRecord.getCurrentObject();
            valueToSet = xmlCompositeObjectMapping.convertDataValueToObjectValue(valueToSet, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
            xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), valueToSet);
            InverseReferenceMapping inverseReferenceMapping = xmlCompositeObjectMapping.getInverseReferenceMapping();
            if (null != inverseReferenceMapping) {
                inverseReferenceMapping.getAttributeAccessor().setAttributeValueInObject(valueToSet, unmarshalRecord.getCurrentObject());
            }
        }
    }

    public UnmarshalRecord buildSelfRecord(UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
        	Descriptor xmlDescriptor = (Descriptor)xmlCompositeObjectMapping.getReferenceDescriptor();
            if (null == xmlDescriptor) {
                xmlDescriptor = findReferenceDescriptor(null, unmarshalRecord, atts, xmlCompositeObjectMapping,xmlCompositeObjectMapping.getKeepAsElementPolicy());
            }

            if(xmlDescriptor != null){
	            if (xmlDescriptor.hasInheritance()) {
	                unmarshalRecord.setAttributes(atts);
	                Class clazz = ((ObjectBuilder)xmlDescriptor.getObjectBuilder()).classFromRow(unmarshalRecord, unmarshalRecord.getSession());
	                if (clazz == null) {
	                    // no xsi:type attribute - look for type indicator on the default root element
	                    XPathQName leafElementType = unmarshalRecord.getLeafElementType();

	                    // if we have a user-set type, try to get the class from the inheritance policy
	                    if (leafElementType != null) {
	                        Object indicator = xmlDescriptor.getInheritancePolicy().getClassIndicatorMapping().get(leafElementType);
	                        if(indicator != null) {
	                            clazz = (Class)indicator;
	                        }
	                    }
	                }

	                if (clazz != null) {
	                    xmlDescriptor = (Descriptor)unmarshalRecord.getSession().getDescriptor(clazz);
	                } else {
	                    // since there is no xsi:type attribute, use the reference descriptor set
	                    // on the mapping -  make sure it is non-abstract
	                    if (Modifier.isAbstract(xmlDescriptor.getJavaClass().getModifiers())) {
	                        // need to throw an exception here
	                        throw DescriptorException.missingClassIndicatorField(unmarshalRecord, (org.eclipse.persistence.oxm.XMLDescriptor)xmlDescriptor.getInheritancePolicy().getDescriptor());
	                    }
	                }
	            }
	            ObjectBuilder stob2 = (ObjectBuilder)xmlDescriptor.getObjectBuilder();
	            org.eclipse.persistence.oxm.record.UnmarshalRecord wrapper = (org.eclipse.persistence.oxm.record.UnmarshalRecord) stob2.createRecord(unmarshalRecord.getSession());
	            UnmarshalRecord childRecord = wrapper.getUnmarshalRecord();
	            childRecord.setUnmarshaller(unmarshalRecord.getUnmarshaller());
	            childRecord.setSelfRecord(true);
	            unmarshalRecord.setChildRecord(childRecord);
	            childRecord.setXMLReader(unmarshalRecord.getXMLReader());
	            childRecord.startDocument();
	            childRecord.initializeRecord(this.xmlCompositeObjectMapping);

	            return childRecord;
            } else{
            	return null;
            }

        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public void setNullValue(Object object, CoreSession session) {
        xmlCompositeObjectMapping.setAttributeValueInObject(object, null);
    }

    public boolean isNullCapableValue() {
        if(xmlCompositeObjectMapping.getAttributeAccessor().isInstanceVariableAttributeAccessor() && !xmlCompositeObjectMapping.hasConverter()) {
            return false;
        }
        Field xmlField = (Field)xmlCompositeObjectMapping.getField();
        if (xmlField.getLastXPathFragment().isSelfFragment) {
            return false;
        }
        return xmlCompositeObjectMapping.getNullPolicy().getIsSetPerformedForAbsentNode();
    }

    public CompositeObjectMapping getMapping() {
        return xmlCompositeObjectMapping;
    }

    protected void setOrAddAttributeValue(UnmarshalRecord unmarshalRecord, Object value, XPathFragment xPathFragment, Object collection){
        unmarshalRecord.setAttributeValue(value, xmlCompositeObjectMapping);
    }

}