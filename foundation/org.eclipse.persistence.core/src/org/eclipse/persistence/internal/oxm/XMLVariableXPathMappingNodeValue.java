/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.5.1 - Initial Implementation
package org.eclipse.persistence.internal.oxm;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.CompositeObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.DirectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.internal.oxm.mappings.VariableXPathObjectMapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class XMLVariableXPathMappingNodeValue extends XMLRelationshipMappingNodeValue{


    /**
     * INTERNAL:
     * @param xPathFragment
     * @return
     */
     public boolean isOwningNode(XPathFragment xPathFragment) {
            return null == xPathFragment;
     }

    protected Descriptor findReferenceDescriptor(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts, Mapping mapping, UnmarshalKeepAsElementPolicy policy) {
       return (Descriptor)mapping.getReferenceDescriptor();
    }

     public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
            Descriptor referenceDescriptor = (Descriptor) getMapping().getReferenceDescriptor();
            ObjectBuilder treeObjectBuilder = (ObjectBuilder) referenceDescriptor.getObjectBuilder();
            MappingNodeValue textMappingNodeValue = (MappingNodeValue) treeObjectBuilder.getRootXPathNode().getTextNode().getNodeValue();
            Mapping textMapping = textMappingNodeValue.getMapping();
            Object childObject = referenceDescriptor.getInstantiationPolicy().buildNewInstance();
            if(textMapping.isAbstractDirectMapping()) {
                DirectMapping xmlDirectMapping = (DirectMapping) textMappingNodeValue.getMapping();
                Field xmlField = (Field) xmlDirectMapping.getField();
                Object realValue = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, (ConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager(), unmarshalRecord);
                Object convertedValue = xmlDirectMapping.getAttributeValue(realValue, unmarshalRecord.getSession(), unmarshalRecord);
                xmlDirectMapping.setAttributeValueInObject(childObject, convertedValue);
            } else {
                Object oldChildObject = unmarshalRecord.getCurrentObject();
                CompositeObjectMapping nestedXMLCompositeObjectMapping = (CompositeObjectMapping) textMappingNodeValue.getMapping();
                unmarshalRecord.setCurrentObject(childObject);
                textMappingNodeValue.attribute(unmarshalRecord, namespaceURI, localName, value);
                unmarshalRecord.setCurrentObject(oldChildObject);
            }
            setXPathInObject(namespaceURI, localName, childObject);
            setOrAddAttributeValue(unmarshalRecord, childObject, null, null);
        }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (null == value) {
            return false;
        }

        Object originalValue = value;
        VariableXPathObjectMapping mapping = (VariableXPathObjectMapping)this.getMapping();
        Descriptor descriptor = (Descriptor)mapping.getReferenceDescriptor();

        if(descriptor.hasInheritance()){
              Class objectValueClass = value.getClass();
               if(!(objectValueClass == descriptor.getJavaClass())){
                   descriptor = (Descriptor) session.getDescriptor(objectValueClass);
               }
        }

        Marshaller marshaller = marshalRecord.getMarshaller();
        ObjectBuilder objectBuilder = (ObjectBuilder)descriptor.getObjectBuilder();
        List extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session, true, true);
        //Change to get the value from the object
        String defaultRootElementString = descriptor.getDefaultRootElement();

        marshalRecord.beforeContainmentMarshal(value);
        XPathFragment rootFragment = mapping.getXPathFragmentForValue(value, marshalRecord.getNamespaceResolver(), marshalRecord.isNamespaceAware(), marshalRecord.getNamespaceSeparator());

        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        if(mapping.isAttribute()){
               ObjectBuilder tob = (ObjectBuilder) mapping.getReferenceDescriptor().getObjectBuilder();
                MappingNodeValue textMappingNodeValue = (MappingNodeValue) tob.getRootXPathNode().getTextNode().getMarshalNodeValue();
                Mapping textMapping = textMappingNodeValue.getMapping();
                if(textMapping.isAbstractDirectMapping()) {
                    DirectMapping xmlDirectMapping = (DirectMapping) textMapping;
                    Object fieldValue = xmlDirectMapping.getFieldValue(xmlDirectMapping.valueFromObject(value, xmlDirectMapping.getField(), session), session, marshalRecord);
                    QName schemaType = ((Field) xmlDirectMapping.getField()).getSchemaTypeForValue(fieldValue, session);
                    marshalRecord.attribute(rootFragment, namespaceResolver, fieldValue, schemaType);
                    //marshalRecord.closeStartGroupingElements(groupingFragment);
                    return true;
                } else {
                    return textMappingNodeValue.marshalSingleValue(rootFragment, marshalRecord, value, textMapping.getAttributeValueFromObject(value), session, namespaceResolver, marshalContext);
                }
        }else{
            marshalRecord.closeStartGroupingElements(groupingFragment);
        getXPathNode().startElement(marshalRecord, rootFragment, object, session, marshalRecord.getNamespaceResolver(), objectBuilder, value);
        writeExtraNamespaces(extraNamespaces, marshalRecord, session);

        marshalRecord.addXsiTypeAndClassIndicatorIfRequired(descriptor, (Descriptor)mapping.getReferenceDescriptor(), (Field)mapping.getField(), originalValue, value, false, false);
        objectBuilder.buildRow(marshalRecord, value, session, marshaller, null);
        marshalRecord.afterContainmentMarshal(object, value);
        marshalRecord.endElement(rootFragment, namespaceResolver);
        marshalRecord.removeExtraNamespacesFromNamespaceResolver(extraNamespaces, session);
        }
        return true;
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        UnmarshalRecord childRecord = unmarshalRecord.getChildRecord();
        if(childRecord != null){
            Object childObject = childRecord.getCurrentObject();
            String localName = xPathFragment.getLocalName();

            setXPathInObject(xPathFragment.getNamespaceURI(), localName, childObject);

            childObject = getMapping().convertDataValueToObjectValue(childObject, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
            setOrAddAttributeValue(unmarshalRecord, childObject, xPathFragment, null);
            unmarshalRecord.setChildRecord(null);
        } else {
             unmarshalRecord.resetStringBuffer();
        }
    }

    public abstract VariableXPathObjectMapping getMapping();

    public void setXPathInObject(String uri, String localName, Object childObject) {
        CoreAttributeAccessor variableAttributeAccessor = ((VariableXPathObjectMapping)this.getMapping()).getVariableAttributeAccessor();
        if(!variableAttributeAccessor.isWriteOnly()){
        Object value = null;
         if(((VariableXPathObjectMapping)getMapping()).getVariableAttributeAccessor().getAttributeClass() == CoreClassConstants.QNAME){
                 if(uri != null && uri.length() > 0) {
                     value =  new QName(uri, localName);
                 }else{
                     value =  new QName( localName);
                 }
            }else{
                value = localName;
            }

            variableAttributeAccessor.setAttributeValueInObject(childObject, value);
        }
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
           try {
            processChild(xPathFragment, unmarshalRecord, atts, (Descriptor) getMapping().getReferenceDescriptor(), getMapping());
        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
         return true;
     }

    @Override
    public boolean isMixedContentNodeValue() {
         return true;
    }

}
