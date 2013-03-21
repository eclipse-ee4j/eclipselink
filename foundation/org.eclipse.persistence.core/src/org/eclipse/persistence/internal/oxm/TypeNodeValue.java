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
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.DirectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.UnionField;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

public class TypeNodeValue extends NodeValue {
    private DirectMapping directMapping;

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return (null != xPathFragment) && xPathFragment.isAttribute();
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return this.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        Object objectValue = directMapping.getAttributeValueFromObject(object);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        Object fieldValue = directMapping.getFieldValue(objectValue, session, marshalRecord);
        if ((null == fieldValue) || (null == namespaceResolver)) {
            return false;
        }
        Field xmlField = (Field) directMapping.getField();
        QName schemaType = getSchemaType(xmlField, fieldValue, session);
        if (null == schemaType) {
            return false;
        }
        if(xmlField.getSchemaType() == null){
            if(schemaType.equals(Constants.STRING_QNAME)){
                return false;
            }
        }else{
            if(xmlField.isSchemaType(schemaType)){
                return false;
            }
        }

        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        String typeQName = namespaceResolver.resolveNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI) + Constants.COLON + Constants.SCHEMA_TYPE_ATTRIBUTE;
        String schemaTypePrefix = namespaceResolver.resolveNamespaceURI(schemaType.getNamespaceURI());
        if(schemaTypePrefix == null){
            if(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(schemaType.getNamespaceURI())){
                schemaTypePrefix = namespaceResolver.generatePrefix(Constants.SCHEMA_PREFIX);	
            }else{
                schemaTypePrefix = namespaceResolver.generatePrefix();
            }            
            marshalRecord.namespaceDeclaration(schemaTypePrefix, schemaType.getNamespaceURI());
        }
        marshalRecord.attribute(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE, typeQName, schemaTypePrefix + Constants.COLON + schemaType.getLocalPart());
        marshalRecord.closeStartGroupingElements(groupingFragment);
        return true;
    }

    public DirectMapping getDirectMapping() {
        return directMapping;
    }

    public void setDirectMapping(DirectMapping directMapping) {
        this.directMapping = directMapping;
    }

    private QName getSchemaType(Field xmlField, Object value, CoreAbstractSession session) {
        QName schemaType = null;
        if (xmlField.isTypedTextField()) {
            schemaType = xmlField.getXMLType(value.getClass());
        } else if (xmlField.isUnionField()) {
            return getSchemaTypeForUnion((UnionField) xmlField, value, session);
        } else if (xmlField.getSchemaType() != null) {
            schemaType = xmlField.getSchemaType();
        }
        return schemaType;
    }

    private QName getSchemaTypeForUnion(UnionField xmlField, Object value, CoreAbstractSession session) {
        List schemaTypes = xmlField.getSchemaTypes();
        QName schemaType = null;
        QName nextQName;
        Class javaClass;
        for (int i = 0; i < schemaTypes.size(); i++) {
            nextQName = (QName) xmlField.getSchemaTypes().get(i);
            try {
                if (nextQName != null) {
                    javaClass = xmlField.getJavaClass(nextQName);
                    value = ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, javaClass, nextQName);
                    schemaType = nextQName;
                    break;
                }
            } catch (ConversionException ce) {
                if (i == (schemaTypes.size() - 1)) {
                    schemaType = nextQName;
                }
            }
        }
        return schemaType;
    }

    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        //assume this is being called for xsi:type field
        if (value != null) {
            String namespace = null;
            int colonIndex = value.indexOf(Constants.COLON);
            if (colonIndex > -1) {
                String prefix = value.substring(0, colonIndex);
                namespace = unmarshalRecord.resolveNamespacePrefix(prefix);
                value = value.substring(colonIndex + 1);
            }
            unmarshalRecord.setTypeQName(new QName(namespace, value));
        }
    }

}
