/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import java.util.List;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
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
            return false;
        }
        Object iterator = cp.iteratorFor(collection);
        if (cp.hasNext(iterator)) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
            return false;
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
                xmlDescriptor = findReferenceDescriptor(unmarshalRecord, atts, xmlCompositeCollectionMapping);
            }

            XMLField xmlFld = (XMLField)this.xmlCompositeCollectionMapping.getField();
            if (xmlFld.hasLastXPathFragment()) {
                unmarshalRecord.setLeafElementType(xmlFld.getLastXPathFragment().getLeafElementType());
            }
            processChild(xPathFragment, unmarshalRecord, atts, xmlDescriptor);
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

    protected void addTypeAttributeIfNeeded(XMLDescriptor descriptor, DatabaseMapping mapping, MarshalRecord marshalRecord) {
        XMLSchemaReference xmlRef = descriptor.getSchemaReference();
        if (xmlCompositeCollectionMapping.shouldAddXsiType(marshalRecord, descriptor) && (xmlRef != null)) {
            addTypeAttribute(descriptor, marshalRecord, xmlRef.getSchemaContext());
        }
    }
    
    public void marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
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
        XMLDescriptor descriptor = (XMLDescriptor)session.getDescriptor(value);
        TreeObjectBuilder objectBuilder = (TreeObjectBuilder)descriptor.getObjectBuilder();
        if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
            marshaller.getMarshalListener().beforeMarshal(value);
        }
        getXPathNode().startElement(marshalRecord, xPathFragment, object, session, namespaceResolver, objectBuilder, value);

        if ((xmlCompositeCollectionMapping.getReferenceDescriptor() == null) && (descriptor.getSchemaReference() != null)) {
            addTypeAttributeIfNeeded(descriptor, xmlCompositeCollectionMapping, marshalRecord);
        }

        List extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session);
        writeExtraNamespaces(extraNamespaces, marshalRecord, session);
        objectBuilder.buildRow(marshalRecord, value, session, marshaller);
        marshalRecord.endElement(xPathFragment, namespaceResolver);
        objectBuilder.removeExtraNamespacesFromNamespaceResolver(marshalRecord, extraNamespaces, session);

        if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
            marshaller.getMarshalListener().afterMarshal(value);
        }
        
    }

    public XMLCompositeCollectionMapping getMapping() {
        return xmlCompositeCollectionMapping;
    }

}
