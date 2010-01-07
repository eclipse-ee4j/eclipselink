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
*     bdoughan - October 21/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import java.util.List;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.record.MarshalRecord;

public class XMLCollectionReferenceMappingMarshalNodeValue extends MappingNodeValue implements ContainerValue {

    private XMLCollectionReferenceMapping xmlCollectionReferenceMapping;
    private XPathNode branchNode;

    public XMLCollectionReferenceMappingMarshalNodeValue(XMLCollectionReferenceMapping xmlCollectionReferenceMapping) {
        this.xmlCollectionReferenceMapping = xmlCollectionReferenceMapping;
        branchNode = new XPathNode();
        NamespaceResolver namespaceResolver = ((XMLDescriptor) xmlCollectionReferenceMapping.getDescriptor()).getNamespaceResolver();
        List fkFields = xmlCollectionReferenceMapping.getFields();
        for(int x=0, fkFieldsSize=fkFields.size(); x<fkFieldsSize; x++) {
            XMLField fkField = (XMLField) fkFields.get(x);
            branchNode.addChild(fkField.getXPathFragment(), new XMLCollectionReferenceMappingFKMarshalNodeValue(xmlCollectionReferenceMapping, fkField), namespaceResolver);
        }
    }

    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public ContainerPolicy getContainerPolicy() {
        return xmlCollectionReferenceMapping.getContainerPolicy();
    }

    @Override
    public XMLCollectionReferenceMapping getMapping() {
        return xmlCollectionReferenceMapping;
    }

    public boolean getReuseContainer() {
        return xmlCollectionReferenceMapping.getReuseContainer();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlCollectionReferenceMapping.setAttributeValueInObject(object, containerInstance);
    }

    @Override
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlCollectionReferenceMapping.isReadOnly()) {
            return false;
        }

        Object collection = xmlCollectionReferenceMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (null == collection) {
            return false;
        }
        ContainerPolicy cp = getContainerPolicy();
        Object iterator = cp.iteratorFor(collection);
        if (cp.hasNext(iterator)) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
            return false;
        }
        if(xPathFragment != XPathFragment.SELF_FRAGMENT) {
            marshalRecord.openStartElement(xPathFragment, namespaceResolver);
        }
        while (cp.hasNext(iterator)) {
            Object objectValue = cp.next(iterator, session);
            marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }
        if(xPathFragment != XPathFragment.SELF_FRAGMENT) {
            marshalRecord.endElement(xPathFragment, namespaceResolver);
        }
        return true;
    }

    @Override
    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlCollectionReferenceMapping.usesSingleNode()) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            if (xPathFragment.isAttribute()) {
                marshalRecord.attribute(xPathFragment, namespaceResolver, (String) value);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            } else {
                marshalRecord.closeStartGroupingElements(groupingFragment);
                marshalRecord.characters((String) value);
            }
        } else {
            for (int x = 0, size = marshalContext.getNonAttributeChildrenSize(branchNode); x < size; x++) {
                XPathNode xPathNode = (XPathNode)marshalContext.getNonAttributeChild(x, branchNode);
                xPathNode.marshal(marshalRecord, value, session, namespaceResolver, marshalRecord.getMarshaller(), marshalContext.getMarshalContext(x));
            }
            
        }
        return true;
    }

    @Override
    public boolean isMarshalNodeValue() {
        return !xmlCollectionReferenceMapping.usesSingleNode();
    }

    @Override
    public boolean isUnmarshalNodeValue() {
        return false;
    }

    private static class XMLCollectionReferenceMappingFKMarshalNodeValue extends MappingNodeValue {

        private XMLCollectionReferenceMapping xmlCollectionReferenceMapping;
        private XMLField xmlField;

        public XMLCollectionReferenceMappingFKMarshalNodeValue(XMLCollectionReferenceMapping xmlCollectionReferenceMapping, XMLField xmlField) {
            this.xmlCollectionReferenceMapping = xmlCollectionReferenceMapping;
            this.xmlField = xmlField;
        }

        @Override
        public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
            return marshalSingleValue(xPathFragment, marshalRecord, null, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }

        @Override
        public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
            Object fieldValue = xmlCollectionReferenceMapping.buildFieldValue(value, xmlField, session);
            if (fieldValue == null) {
                if(null != value) {
                    XMLField f2 = (XMLField) xmlCollectionReferenceMapping.getSourceToTargetKeyFieldAssociations().get(xmlField);
                    fieldValue = marshalRecord.getMarshaller().getXMLContext().getValueByXPath(value, f2.getXPath(), f2.getNamespaceResolver(), Object.class);
                }
                if(null == fieldValue) {
                    return false;
                }
            }
            QName schemaType = getSchemaType(xmlField, fieldValue, session);
            String stringValue = getValueToWrite(schemaType, fieldValue, (XMLConversionManager) session.getDatasourcePlatform().getConversionManager(), namespaceResolver);
            if (stringValue != null) {
                if (xPathFragment.isAttribute()) {
                    marshalRecord.attribute(xPathFragment, namespaceResolver, stringValue);
                    marshalRecord.closeStartElement();
                } else {
                    marshalRecord.closeStartElement();
                    marshalRecord.characters(stringValue);
                }
                return true;
            }
            return false;
        }

        @Override
        public XMLCollectionReferenceMapping getMapping() {
            return xmlCollectionReferenceMapping;
        }

    }

}