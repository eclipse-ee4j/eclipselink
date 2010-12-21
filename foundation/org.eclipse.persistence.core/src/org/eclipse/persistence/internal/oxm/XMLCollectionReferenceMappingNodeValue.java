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
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;

import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Class to handle (un)marshal operations for 
 * XMLCollectionReferenceMappings.</p>  
 * 
 * <p>An instance of this class is required for each XMLField set on the 
 * mapping, that is, for each source field in the source-target key field 
 * association list.</p>
 * 
 * <p>When unmarshalling, an instance of org.eclipse.persistence.internal.oxm.Reference is
 * created on a per mapping basis (keyed on source object instance) and sorted 
 * on the associated session's org.eclipse.persistence.internal.oxm.ReferenceResolver 
 * instance.  Each target primary key value is stored in the Reference instance 
 * for use during mapping resolution phase after unmarshalling completes.</p>
 * 
 * <p>When marshalling, the target object's primary key value that is mapped to 
 * this NodeValue's XMLField (in the XMLObjectReferenceMapping's source-target 
 * key field association list) is retrieved and written out.</p>
 * 
 * @see org.eclipse.persistence.internal.oxm.Reference
 * @see org.eclipse.persistence.internal.oxm.ReferenceResolver
 * @see org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping
 */

public class XMLCollectionReferenceMappingNodeValue extends MappingNodeValue implements ContainerValue {
    private XMLCollectionReferenceMapping xmlCollectionReferenceMapping;
    private XMLField xmlField;
    private static final String SPACE = " ";

    /**
     * This constructor sets the XMLCollectionReferenceMapping and XMLField members to
     * the provided values.
     * 
     * @param xmlCollectionReferenceMapping
     */
    public XMLCollectionReferenceMappingNodeValue(XMLCollectionReferenceMapping xmlCollectionReferenceMapping, XMLField xmlField) {
        super();
        this.xmlCollectionReferenceMapping = xmlCollectionReferenceMapping;
        this.xmlField = xmlField;
    }

    /**
     * Handle attribute operation.  Here we will create and populate an 
     * org.eclipse.persistence.internal.oxm.Reference instance to be used during 
     * the mapping resolution stage.  In particular, the primary key value
     * for this element will be added to the Reference object's map of
     * target primary key values - based on the target key field name.  Note 
     * that if a reference already exists for the xmlCollectionReferenceMapping's 
     * source object instance, we will simply add to the target pk value list.  
     * The Reference object is stored on the ReferenceResolver associated with 
     * the UnmarshalRecord's session.
     */
    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        if (value != null) {
            Object realValue = xmlField.convertValueBasedOnSchemaType(value, (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager(),unmarshalRecord);
            // build a reference which will be resolved after unmarshalling is complete
            xmlCollectionReferenceMapping.buildReference(unmarshalRecord, xmlField, realValue, unmarshalRecord.getSession());
        }
    }

    /**
     * Handle endElement operation.  Here we will create and populate an 
     * org.eclipse.persistence.internal.oxm.Reference instance to be used during 
     * the mapping resolution stage.  In particular, the primary key value
     * for this element will be added to the Reference object's map of
     * target primary key values - based on the target key field name.  Note 
     * that if a reference already exists for the xmlCollectionReferenceMapping's 
     * source object instance, we will simply add to the target pk value list.  
     * The Reference object is stored on the ReferenceResolver associated with 
     * the UnmarshalRecord's session.
     */
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        if (!xmlField.getLastXPathFragment().nameIsText()) {
            return;
        }

        Object value = unmarshalRecord.getStringBuffer().toString();
        unmarshalRecord.resetStringBuffer();

        XMLConversionManager xmlConversionManager = (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager();
        if (unmarshalRecord.getTypeQName() != null) {
            Class typeClass = xmlField.getJavaClass(unmarshalRecord.getTypeQName());
            value = xmlConversionManager.convertObject(value, typeClass, unmarshalRecord.getTypeQName());
        } else {
            value = xmlField.convertValueBasedOnSchemaType(value, xmlConversionManager, unmarshalRecord);
        }

        // build a reference which will be resolved after unmarshalling is complete
        xmlCollectionReferenceMapping.buildReference(unmarshalRecord, xmlField, value, unmarshalRecord.getSession());
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Object container) {
        this.endElement(xPathFragment, unmarshalRecord);
    }
    
    /**
     * Indicate if the next XPathFragment is an attribute or text() node.
     */
    public boolean isOwningNode(XPathFragment xPathFragment) {
        if(isMarshalNodeValue()) {
            if (xmlCollectionReferenceMapping.usesSingleNode()) {
                return xPathFragment.nameIsText() || xPathFragment.isAttribute();
            }
            XPathFragment nextFragment = xPathFragment.getNextFragment();
            return (nextFragment != null) && (nextFragment.nameIsText() || nextFragment.isAttribute());
        }
        return super.isOwningNode(xPathFragment);
    }

    public boolean isContainerValue() {
        return true;
    }

    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlCollectionReferenceMapping.setAttributeValueInObject(object, containerInstance);
    }

    public ContainerPolicy getContainerPolicy() {
        return xmlCollectionReferenceMapping.getContainerPolicy();
    }

    /**
     * Handle the marshal operation for this NodeValue.  Each of the target
     * object's primary key values that are mapped to the collection mapping's fields 
     * (in the XMLCollectionReferenceMapping's source-target key field association list)
     * are retrieved and written out. 
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        if(this.xmlCollectionReferenceMapping.isReadOnly()) {
            return false;
        }
        ContainerPolicy cp = xmlCollectionReferenceMapping.getContainerPolicy();
        Object collection = xmlCollectionReferenceMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (collection == null) {
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
        StringBuilder stringValueStringBuilder = new StringBuilder();
        String newValue;
        QName schemaType;

        if (xmlCollectionReferenceMapping.usesSingleNode()) {
            while (cp.hasNext(iterator)) {
                objectValue = cp.next(iterator, session);
                Object fieldValue = xmlCollectionReferenceMapping.buildFieldValue(objectValue, xmlField, session);
                if (fieldValue == null) {
                    if(null != objectValue) {
                        XMLField fkField = (XMLField) xmlCollectionReferenceMapping.getSourceToTargetKeyFieldAssociations().get(xmlField);
                        fieldValue = marshalRecord.getMarshaller().getXMLContext().getValueByXPath(objectValue, fkField.getXPath(), fkField.getNamespaceResolver(), Object.class);
                    }
                    if(null == fieldValue) {
                        return false;
                    }
                }
                schemaType = getSchemaType(xmlField, fieldValue, session);
                newValue = getValueToWrite(schemaType, fieldValue, (XMLConversionManager) session.getDatasourcePlatform().getConversionManager(), marshalRecord);
                if (newValue != null) {
                    stringValueStringBuilder.append(newValue);
                    if (cp.hasNext(iterator)) {
                        stringValueStringBuilder.append(SPACE);
                    }
                }
            }
            marshalSingleValue(xPathFragment, marshalRecord, object, stringValueStringBuilder.toString(), session, namespaceResolver, ObjectMarshalContext.getInstance());
        } else {
            while (cp.hasNext(iterator)) {
                objectValue = cp.next(iterator, session);
                marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
            }
        }
        return true;
    }

    /**
     * @override
     * @param xPathFragment
     * @param unmarshalRecord
     * @param atts
     */
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        if (xmlField.getLastXPathFragment().isAttribute()) {
            if (!this.xmlCollectionReferenceMapping.usesSingleNode()) {
                String namespaceURI = xmlField.getLastXPathFragment().getNamespaceURI();
                String value;
                if (namespaceURI == null) {
                    value = atts.getValue(xmlField.getLastXPathFragment().getLocalName());
                } else {
                    value = atts.getValue(namespaceURI, xmlField.getLastXPathFragment().getLocalName());
                }
                xmlCollectionReferenceMapping.buildReference(unmarshalRecord, xmlField, value, unmarshalRecord.getSession());
                return true;
            }
        }
        return true;
    }

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
            QName schemaType;
            Object fieldValue = xmlCollectionReferenceMapping.buildFieldValue(value, xmlField, session);
            if (fieldValue == null) {
                return false;
            }
            schemaType = getSchemaType(xmlField, fieldValue, session);
            String stringValue = getValueToWrite(schemaType, fieldValue, (XMLConversionManager) session.getDatasourcePlatform().getConversionManager(), marshalRecord);
            if (stringValue != null) {
                marshalRecord.openStartElement(xPathFragment, namespaceResolver);
                XPathFragment nextFragment = xPathFragment.getNextFragment();
                if (nextFragment.isAttribute()) {
                    marshalRecord.attribute(nextFragment, namespaceResolver, stringValue);
                    marshalRecord.closeStartElement();
                } else {
                    marshalRecord.closeStartElement();
                    marshalRecord.characters(stringValue);
                }
                marshalRecord.endElement(xPathFragment, namespaceResolver);
            }
        }
        return true;
    }

    public XMLCollectionReferenceMapping getMapping() {
        return xmlCollectionReferenceMapping;
    }

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }

    @Override
    public boolean isMarshalNodeValue() {
        return xmlCollectionReferenceMapping.getFields().size() == 1 || xmlCollectionReferenceMapping.usesSingleNode();
    }

}