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

import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLUnionField;

public abstract class XMLSimpleMappingNodeValue extends MappingNodeValue {
    protected String getValueToWrite(QName schemaType, Object value, XMLConversionManager xmlConversionManager, NamespaceResolver namespaceResolver) {
    	if(schemaType != null && XMLConstants.QNAME_QNAME.equals(schemaType)){
    		return getStringForQName((QName)value, namespaceResolver);
    	}
        return (String) xmlConversionManager.convertObject(value, ClassConstants.STRING, schemaType);
    }

    protected QName getSchemaType(XMLField xmlField, Object value, AbstractSession session) {
        QName schemaType = null;
        if (xmlField.isTypedTextField()) {
            schemaType = xmlField.getXMLType(value.getClass());
        } else if (xmlField.isUnionField()) {
            return getSingleValueToWriteForUnion((XMLUnionField) xmlField, value, session);
        } else if (xmlField.getSchemaType() != null) {
            schemaType = xmlField.getSchemaType();
        }
        return schemaType;
    }

    protected QName getSingleValueToWriteForUnion(XMLUnionField xmlField, Object value, AbstractSession session) {
        ArrayList schemaTypes = xmlField.getSchemaTypes();
        QName schemaType = null;
        QName nextQName;
        Class javaClass;
        for (int i = 0; i < schemaTypes.size(); i++) {
            nextQName = (QName) (xmlField).getSchemaTypes().get(i);
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
    protected String getStringForQName(QName qName, NamespaceResolver namespaceResolver){
    	if(null == qName) {
			return null;
		}
		    
		if(null == qName.getNamespaceURI()) {
			return qName.getLocalPart();
		} else {		
			String namespaceURI = qName.getNamespaceURI();
			if(namespaceResolver == null){
				throw XMLMarshalException.namespaceResolverNotSpecified(namespaceURI);
			}
			String prefix = namespaceResolver.resolveNamespaceURI(namespaceURI);
			if(null == prefix) {
				return qName.getLocalPart();
			} else {
				return prefix + COLON + qName.getLocalPart();
			}
		}

    }
}