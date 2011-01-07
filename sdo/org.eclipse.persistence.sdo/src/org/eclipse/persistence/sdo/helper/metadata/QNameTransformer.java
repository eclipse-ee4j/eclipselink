/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rick.barkhouse - added as part of fix for SDO 2.1.1 TCK 'sdoPathXSDQNameTest' 
 ******************************************************************************/
package org.eclipse.persistence.sdo.helper.metadata;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class QNameTransformer implements AttributeTransformer, FieldTransformer {

    private static final char COLON = ':';
    private static final char HASH = '#';
    
    public static final String QNAME_NAMESPACE_PREFIX = "qns";
    
    private static final String DEFAULT_NAMESPACE_PREFIX = "";

    AbstractTransformationMapping transformationMapping;
    private XMLField xPath;

    public QNameTransformer(String xPath) {
        super();
        this.xPath = new XMLField(xPath);
    }

    public void initialize(AbstractTransformationMapping mapping) {
        transformationMapping = mapping;
        NamespaceResolver namespaceResolver = ((XMLDescriptor) mapping.getDescriptor()).getNamespaceResolver();
        xPath.setNamespaceResolver(namespaceResolver);
    }

    public Object buildAttributeValue(Record record, Object object, Session session) {
        if (null == record) {
            return null;
        }

        String value = (String) record.get(xPath);
        if (null == value) {
            return null;
        }

        int index = value.lastIndexOf(COLON);
        if (index > -1) {
            String prefix = value.substring(0, index);
            String localName = value.substring(index + 1);
            String namespaceURI = ((XMLRecord) record).resolveNamespacePrefix(prefix);
            if (namespaceURI != null) {
                return namespaceURI + HASH + localName;
            } else {
                return localName;
            }
        } else {
            String namespaceURI = ((XMLRecord) record).resolveNamespacePrefix(DEFAULT_NAMESPACE_PREFIX);
            if (namespaceURI != null) {
                return namespaceURI + HASH + value;    
            } else {
                return value;
            }
        }
    }

    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        if (null == instance) {
            return null;
        }

        String value = (String) transformationMapping.getAttributeValueFromObject(instance);

        int index = value.lastIndexOf(HASH);
        if (index > -1) {
            String namespaceURI = value.substring(0, index);
            String localName = value.substring(index + 1);
            String prefix = null;
            NamespaceResolver namespaceResolver = xPath.getNamespaceResolver();
            if(null != namespaceResolver) {
                prefix = namespaceResolver.resolveNamespaceURI(namespaceURI);
                if (prefix == null) {
                    if (namespaceURI.equals(namespaceResolver.getDefaultNamespaceURI())) {
                        return localName;
                    }
                }
                return prefix + COLON + localName;
            }
            return localName;
        } else {
            return value;
        }
    }

}