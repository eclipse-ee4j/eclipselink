/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* bdoughan - January 7/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.oxm.record.MarshalRecord;

/**
 * A node value corresponding to mapping. 
 */
public abstract class MappingNodeValue extends NodeValue {

    /**
     * Return the mapping associated with this node value. 
     */
    public abstract DatabaseMapping getMapping();
    
    public boolean isMappingNodeValue() {
        return true;
    }
    protected String getValueToWrite(QName schemaType, Object value, XMLConversionManager xmlConversionManager, MarshalRecord marshalRecord) {
        if(schemaType != null && XMLConstants.QNAME_QNAME.equals(schemaType)){
            return getStringForQName((QName)value, marshalRecord);
        }
        return (String) xmlConversionManager.convertObject(value, ClassConstants.STRING, schemaType);
    }

    protected QName getSchemaType(XMLField xmlField, Object value, AbstractSession session) {
        if(xmlField.getLeafElementType() != null){
            return xmlField.getLeafElementType();
        }else if (xmlField.isTypedTextField()) {
            return xmlField.getXMLType(value.getClass());
        } else if (xmlField.isUnionField()) {
            return getSingleValueToWriteForUnion((XMLUnionField) xmlField, value, session);
        }
        return xmlField.getSchemaType();        
    }

    protected QName getSingleValueToWriteForUnion(XMLUnionField xmlField, Object value, AbstractSession session) {
        ArrayList schemaTypes = xmlField.getSchemaTypes();
        QName schemaType = null;
        for (int i = 0, schemaTypesSize = schemaTypes.size(); i < schemaTypesSize; i++) {
            QName nextQName = (QName) (xmlField).getSchemaTypes().get(i);
            try {
                if (nextQName != null) {
                    Class javaClass = xmlField.getJavaClass(nextQName);
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

    protected String getStringForQName(QName qName, MarshalRecord marshalRecord){
        if(null == qName) {
            return null;
        }
        String namespaceURI = qName.getNamespaceURI();
        if(null == namespaceURI || 0 == namespaceURI.length()) {
            if(marshalRecord.getNamespaceResolver() != null && marshalRecord.getNamespaceResolver().getDefaultNamespaceURI() != null) {
                //need to add a default namespace declaration.
                marshalRecord.attribute(XMLConstants.XMLNS_URL, XMLConstants.XMLNS, XMLConstants.XMLNS, namespaceURI);
            }
            return qName.getLocalPart();
        } else {
            NamespaceResolver namespaceResolver = marshalRecord.getNamespaceResolver();
            if(namespaceResolver == null){
                throw XMLMarshalException.namespaceResolverNotSpecified(namespaceURI);
            }
            if(namespaceURI.equals(namespaceResolver.getDefaultNamespaceURI())) {
                return qName.getLocalPart();
            }
            String prefix = namespaceResolver.resolveNamespaceURI(namespaceURI);
            if(null == prefix) {
                prefix = namespaceResolver.generatePrefix();
                marshalRecord.attribute(XMLConstants.XMLNS_URL, prefix, XMLConstants.XMLNS + XMLConstants.COLON + prefix, namespaceURI);
            }
            return prefix + XMLConstants.COLON + qName.getLocalPart();
        }
    }

    protected void updateNamespaces(QName qname, MarshalRecord marshalRecord, XMLField xmlField){
        if (qname != null){        
            if(xmlField != null){
                if(xmlField.isTypedTextField()){           
                    if(xmlField.getSchemaType() == null){
                        if(qname.equals(XMLConstants.STRING_QNAME)){
                            return;
                        }
                    }else{
                    	if(xmlField.isSchemaType(qname)){
                    		return;
                    	}
                    }
                }else{
                    return;
                }
            }
               
            String prefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI(qname.getNamespaceURI());
            if ((prefix == null) || prefix.length() == 0) {
            	
            	if(XMLConstants.SCHEMA_URL.equals(qname.getNamespaceURI())){
                    prefix = marshalRecord.getNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_PREFIX);	
                }else{            	
                    prefix = marshalRecord.getNamespaceResolver().generatePrefix();              
                }
                marshalRecord.attribute(XMLConstants.XMLNS_URL, prefix, XMLConstants.XMLNS + XMLConstants.COLON + prefix, qname.getNamespaceURI());
            }
            String typeValue = prefix + XMLConstants.COLON + qname.getLocalPart();

            addTypeAttribute(marshalRecord, typeValue);
        }
    }
    
    protected void addTypeAttribute(MarshalRecord marshalRecord, String typeValue) {        
        String xsiPrefix = null;
        if (marshalRecord.getNamespaceResolver() != null) {
            xsiPrefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
        } else {
            xsiPrefix = XMLConstants.SCHEMA_INSTANCE_PREFIX;
            marshalRecord.attribute(XMLConstants.XMLNS_URL, xsiPrefix, XMLConstants.XMLNS + XMLConstants.COLON + xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);
        }
        if (xsiPrefix == null) {
            xsiPrefix = marshalRecord.getNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_INSTANCE_PREFIX);
            marshalRecord.attribute(XMLConstants.XMLNS_URL, xsiPrefix, XMLConstants.XMLNS + XMLConstants.COLON + xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);
        }
        marshalRecord.attribute(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_TYPE_ATTRIBUTE, xsiPrefix + XMLConstants.COLON + XMLConstants.SCHEMA_TYPE_ATTRIBUTE, typeValue);
    }
    
}
