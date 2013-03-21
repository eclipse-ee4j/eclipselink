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

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Class to handle (un)marshal operations for 
 * XMLObjectReferenceMappings.  An instance of this class is required for each 
 * XMLField set on the mapping, that is, for each source field in the 
 * source-target key field association list.</p>  
 * 
 * <p>When unmarshalling, an instance of org.eclipse.persistence.internal.oxm.Reference is 
 * created on a per mapping basis (keyed on source object instance) and sotred 
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

public class XMLObjectReferenceMappingNodeValue extends MappingNodeValue {
    private ObjectReferenceMapping xmlObjectReferenceMapping;
    private Field xmlField;

    /**
     * This constructor sets the XMLObjectReferenceMapping member to the provided 
     * value.
     * 
     * @param xmlObjectReferenceMapping
     */
    public XMLObjectReferenceMappingNodeValue(ObjectReferenceMapping xmlObjectReferenceMapping) {
        super();
        this.xmlObjectReferenceMapping = xmlObjectReferenceMapping;
    }

    /**
     * This constructor sets the XMLObjectReferenceMapping and XMLField members to
     * the provided values.
     * 
     * @param xmlObjectReferenceMapping
     * @param xmlField
     */
    public XMLObjectReferenceMappingNodeValue(ObjectReferenceMapping xmlObjectReferenceMapping, Field xmlField) {
        super();
        this.xmlObjectReferenceMapping = xmlObjectReferenceMapping;
        this.xmlField = xmlField;
    }

    /**
     * Handle attribute operation.  Here we will create and populate an 
     * org.eclipse.persistence.internal.oxm.Reference instance to be used during 
     * the mapping resolution stage.  In particular, the primary key value
     * for this element will be added to the Reference object's list of
     * target primary key values.  Note that if a reference already exists 
     * for the xmlObjectReferenceMapping's source object instance, we will
     * simply add to the target pk value list.  The Reference object will 
     * is stored on the ReferenceResolver associated with the UnmarshalRecord's
     * session.
     */
    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        if (value != null) {           
            Object realValue = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager(),unmarshalRecord);
            
            // build a reference which will be resolved after unmarshalling is complete
            xmlObjectReferenceMapping.buildReference(unmarshalRecord, xmlField, realValue, unmarshalRecord.getSession());
        }
    }

    /**
     * Handle endElement operation.  Here we will create and populate an 
     * org.eclipse.persistence.internal.oxm.Reference instance to be used during 
     * the mapping resolution stage.  In particular, the primary key value
     * for this element will be added to the Reference object's list of
     * target primary key values.  Note that if a reference already exists 
     * for the xmlObjectReferenceMapping's source object instance, we will
     * simply add to the target pk value list.  The Reference object will 
     * is stored on the ReferenceResolver associated with the UnmarshalRecord's
     * session.
     */
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        if (!xmlField.getLastXPathFragment().nameIsText()) {
            return;
        }
        Object value = unmarshalRecord.getCharacters().toString().trim();
        unmarshalRecord.resetStringBuffer();
        XMLConversionManager xmlConversionManager = (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager();
        if (unmarshalRecord.getTypeQName() != null) {
            Class typeClass = xmlField.getJavaClass(unmarshalRecord.getTypeQName());
            value = xmlConversionManager.convertObject(value, typeClass, unmarshalRecord.getTypeQName());
        } else {
            value = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, xmlConversionManager, unmarshalRecord);
        }

        // build a reference which will be resolved after unmarshalling is complete
        xmlObjectReferenceMapping.buildReference(unmarshalRecord, xmlField, value, unmarshalRecord.getSession());
    }
    
    /**
     * Indicate if the XPathFragment is an attribute or text() node.
     */
    public boolean isOwningNode(XPathFragment xPathFragment) {
        return xPathFragment.isAttribute() || xPathFragment.nameIsText();
    }

    /**
     * Handle the marshal operation for this NodeValue's XMLField.  The target
     * object's primary key value that is mapped to this NodeValue's XMLField 
     * (in the XMLObjectReferenceMapping's source-target key field association list)
     * is retrieved and written out. 
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    /**
     * Handle the marshal operation for this NodeValue's XMLField.  The target
     * object's primary key value that is mapped to this NodeValue's XMLField 
     * (in the XMLObjectReferenceMapping's source-target key field association list)
     * is retrieved and written out. 
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlObjectReferenceMapping.isReadOnly()) {
            return false;
        }
        Object targetObject = marshalContext.getAttributeValue(object, xmlObjectReferenceMapping);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, targetObject, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object targetObject, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        Object fieldValue = xmlObjectReferenceMapping.buildFieldValue(targetObject, xmlField, session);
        if (fieldValue == null) {
            if(null != targetObject) {
            	Field fkField = (Field) xmlObjectReferenceMapping.getSourceToTargetKeyFieldAssociations().get(xmlField);
                if(null == fkField) {
                	Descriptor targetDescriptor = (Descriptor) session.getDescriptor(targetObject);
                    fieldValue = marshalRecord.getMarshaller().getContext().getValueByXPath(targetObject, ((CoreField) targetDescriptor.getPrimaryKeyFields().get(0)).getName(), targetDescriptor.getNamespaceResolver(), Object.class);
                } else {
                    fieldValue = marshalRecord.getMarshaller().getContext().getValueByXPath(targetObject, fkField.getXPath(), fkField.getNamespaceResolver(), Object.class);
                }
            }
            if(null == fieldValue) {
                return false;
            }
        }

        QName schemaType = xmlField.getSchemaTypeForValue(fieldValue, session);
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);

        if (xPathFragment.isAttribute()) {
            marshalRecord.attribute(xPathFragment, namespaceResolver, fieldValue, schemaType);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
            marshalRecord.closeStartGroupingElements(groupingFragment);
            marshalRecord.characters(schemaType, fieldValue, null, false);
        }
        return true;
    }

    public ObjectReferenceMapping getMapping() {
        return xmlObjectReferenceMapping;
    }

}
