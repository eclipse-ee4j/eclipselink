/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.deferred.CompositeObjectMappingContentHandler;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sessions.Session;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Composite Object Mapping is handled
 * when used with the TreeObjectBuilder.</p>
 */
public class XMLCompositeObjectMappingNodeValue extends XMLRelationshipMappingNodeValue implements NullCapableValue {
    private XMLCompositeObjectMapping xmlCompositeObjectMapping;

    public XMLCompositeObjectMappingNodeValue(XMLCompositeObjectMapping xmlCompositeObjectMapping) {
        super();
        this.xmlCompositeObjectMapping = xmlCompositeObjectMapping;
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
    public boolean marshalSelfAttributes(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, XMLMarshaller marshaller) {
        Object objectValue = xmlCompositeObjectMapping.getAttributeValueFromObject(object);
        if (xmlCompositeObjectMapping.getConverter() != null) {
            Converter converter = xmlCompositeObjectMapping.getConverter();
            if (converter instanceof XMLConverter) {
                objectValue = ((XMLConverter)converter).convertObjectValueToDataValue(objectValue, session, marshaller);
            } else {
                objectValue = converter.convertObjectValueToDataValue(objectValue, session);
            }
        }
        XMLDescriptor descriptor = (XMLDescriptor)session.getDescriptor(objectValue);
        TreeObjectBuilder objectBuilder = (TreeObjectBuilder)descriptor.getObjectBuilder();
        return objectBuilder.marshalAttributes(marshalRecord, objectValue, session);
    }
    
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlCompositeObjectMapping.isReadOnly()) {
            return false;
        }

        if (xPathFragment.hasLeafElementType()) {
            marshalRecord.setLeafElementType(xPathFragment.getLeafElementType());
        }

        XMLMarshaller marshaller = marshalRecord.getMarshaller();       
        Object objectValue = marshalContext.getAttributeValue(object, xmlCompositeObjectMapping);
        if (xmlCompositeObjectMapping.getConverter() != null) {
            Converter converter = xmlCompositeObjectMapping.getConverter();
            if (converter instanceof XMLConverter) {
                objectValue = ((XMLConverter)converter).convertObjectValueToDataValue(objectValue, session, marshaller);
            } else {
                objectValue = converter.convertObjectValueToDataValue(objectValue, session);
            }
        }
        if (null == objectValue) {
            return xmlCompositeObjectMapping.getNullPolicy().compositeObjectMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
        }

        if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
            marshaller.getMarshalListener().beforeMarshal(objectValue);
        }
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        marshalRecord.closeStartGroupingElements(groupingFragment);
        XMLDescriptor descriptor = (XMLDescriptor)session.getDescriptor(objectValue);
        TreeObjectBuilder objectBuilder = (TreeObjectBuilder)descriptor.getObjectBuilder();

        if (!xPathFragment.isSelfFragment()) {
            getXPathNode().startElement(marshalRecord, xPathFragment, object, session, namespaceResolver, objectBuilder, objectValue);
        }

        List extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session);
        writeExtraNamespaces(extraNamespaces, marshalRecord, session);
        if ((xmlCompositeObjectMapping.getReferenceDescriptor() == null) && (descriptor.getSchemaReference() != null)) {
            addTypeAttributeIfNeeded(descriptor, xmlCompositeObjectMapping, marshalRecord);
        }
        objectBuilder.buildRow(marshalRecord, objectValue, session, marshaller);
        if (!xPathFragment.isSelfFragment()) {
            marshalRecord.endElement(xPathFragment, namespaceResolver);
        }
        objectBuilder.removeExtraNamespacesFromNamespaceResolver(marshalRecord, extraNamespaces, session);
        if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
            marshaller.getMarshalListener().afterMarshal(objectValue);
        }
        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            unmarshalRecord.removeNullCapableValue(this);

            XMLDescriptor xmlDescriptor = (XMLDescriptor)xmlCompositeObjectMapping.getReferenceDescriptor();
            if (null == xmlDescriptor) {
                xmlDescriptor = findReferenceDescriptor(unmarshalRecord, atts, xmlCompositeObjectMapping);
            }

            /**
             * Null Composite Objects are marshalled in 2 ways when the input XML node is empty.
             * (1) as null
             *     - isNullRepresentedByEmptyNode = true
             * (2) as empty object  
             *     - isNullRepresentedByEmptyNode = false 
             *  A deferred contentHandler is used to queue events until we are able to determine
             *  whether we are in one of empty/simple/complex state.
             *  Control is returned to the UnmarshalHandler after creation of (1) or (2) above is started.
             *  Object creation was deferred to the DeferredContentHandler
             */
            // Check if we need to create the DeferredContentHandler based on policy state
            if(xmlCompositeObjectMapping.getNullPolicy().isNullRepresentedByEmptyNode() || xmlCompositeObjectMapping.getNullPolicy().isNullRepresentedByXsiNil()) {
            	if(null != xmlDescriptor) {
            		// Process null capable value
            		String qnameString = xPathFragment.getLocalName();
            		if(xPathFragment.getPrefix() != null) {
            			qnameString = xPathFragment.getPrefix()  +":" + qnameString;
            		}
            		CompositeObjectMappingContentHandler aHandler = new CompositeObjectMappingContentHandler(//
                		unmarshalRecord, this, xmlCompositeObjectMapping, atts, xPathFragment, xmlDescriptor);
            		// Send control to the handler
            		aHandler.startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), qnameString, atts);                          
            		unmarshalRecord.getXMLReader().setContentHandler(aHandler);
            	}
            } else {
                boolean isNull = xmlCompositeObjectMapping.getNullPolicy().valueIsNull(atts);
                if (isNull) {
                    xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), null);
                } else {
                    XMLField xmlFld = (XMLField)this.xmlCompositeObjectMapping.getField();
                    if (xmlFld.hasLastXPathFragment()) {
                        unmarshalRecord.setLeafElementType(xmlFld.getLastXPathFragment().getLeafElementType());
                    }
                    processChild(xPathFragment, unmarshalRecord, atts, xmlDescriptor);
                }
            }
        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
        return true;
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        if (null == unmarshalRecord.getChildRecord()) {
            return;
        }
        Object object = unmarshalRecord.getChildRecord().getCurrentObject();
        if (xmlCompositeObjectMapping.getConverter() != null) {
            Converter converter = xmlCompositeObjectMapping.getConverter();
            if (converter instanceof XMLConverter) {
                object = ((XMLConverter)converter).convertDataValueToObjectValue(object, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
            } else {
                object = converter.convertDataValueToObjectValue(object, unmarshalRecord.getSession());
            }
        }
        // Set the child object on the parent
        unmarshalRecord.setAttributeValue(object, xmlCompositeObjectMapping);
        if(xmlCompositeObjectMapping.getContainerAccessor() != null) {
        	xmlCompositeObjectMapping.getContainerAccessor().setAttributeValueInObject(object, unmarshalRecord.getCurrentObject());
        }        
        unmarshalRecord.setChildRecord(null);
    }

    public UnmarshalRecord buildSelfRecord(UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            XMLDescriptor xmlDescriptor = (XMLDescriptor)xmlCompositeObjectMapping.getReferenceDescriptor();
            if (xmlDescriptor.hasInheritance()) {
                unmarshalRecord.setAttributes(atts);
                Class clazz = xmlDescriptor.getInheritancePolicy().classFromRow(unmarshalRecord, unmarshalRecord.getSession());
                if (clazz == null) {
                    // no xsi:type attribute - look for type indicator on the default root element
                    QName leafElementType = unmarshalRecord.getLeafElementType();

                    // if we have a user-set type, try to get the class from the inheritance policy
                    if (leafElementType != null) {
                        Object indicator = xmlDescriptor.getInheritancePolicy().getClassIndicatorMapping().get(leafElementType);

                        // if the inheritance policy does not contain the user-set type, throw an exception
                        if (indicator == null) {
                            throw DescriptorException.missingClassForIndicatorFieldValue(leafElementType, xmlDescriptor.getInheritancePolicy().getDescriptor());
                        }
                        clazz = (Class)indicator;
                    }
                }

                if (clazz != null) {
                    xmlDescriptor = (XMLDescriptor)unmarshalRecord.getSession().getDescriptor(clazz);
                } else {
                    // since there is no xsi:type attribute, use the reference descriptor set 
                    // on the mapping -  make sure it is non-abstract
                    if (Modifier.isAbstract(xmlDescriptor.getJavaClass().getModifiers())) {
                        // need to throw an exception here
                        throw DescriptorException.missingClassIndicatorField(unmarshalRecord, xmlDescriptor.getInheritancePolicy().getDescriptor());
                    }
                }
            }
            TreeObjectBuilder stob2 = (TreeObjectBuilder)xmlDescriptor.getObjectBuilder();
            UnmarshalRecord childRecord = (UnmarshalRecord)stob2.createRecord(unmarshalRecord.getSession());
            childRecord.setSelfRecord(true);
            unmarshalRecord.setChildRecord(childRecord);
            childRecord.setXMLReader(unmarshalRecord.getXMLReader());
            childRecord.startDocument(this.xmlCompositeObjectMapping);
            xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), childRecord.getCurrentObject());
            if(xmlCompositeObjectMapping.getContainerAccessor() != null) {
            	xmlCompositeObjectMapping.getContainerAccessor().setAttributeValueInObject(childRecord.getCurrentObject(), unmarshalRecord.getCurrentObject());
            }
            return childRecord;
        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public void setNullValue(Object object, Session session) {
       	xmlCompositeObjectMapping.setAttributeValueInObject(object, null);
    }

    public boolean isNullCapableValue() {
        XMLField xmlField = (XMLField)xmlCompositeObjectMapping.getField();
        if (xmlField.getLastXPathFragment().isSelfFragment) {
            return false;
        }
        return xmlCompositeObjectMapping.getNullPolicy().getIsSetPerformedForAbsentNode();
    }

    protected void addTypeAttributeIfNeeded(XMLDescriptor descriptor, DatabaseMapping mapping, MarshalRecord marshalRecord) {
        XMLSchemaReference xmlRef = descriptor.getSchemaReference();
        if (xmlCompositeObjectMapping.shouldAddXsiType(marshalRecord, descriptor) && (xmlRef != null)) {
            addTypeAttribute(descriptor, marshalRecord, xmlRef.getSchemaContext());
        }
    }

    public XMLCompositeObjectMapping getMapping() {
        return xmlCompositeObjectMapping;
    }

}
