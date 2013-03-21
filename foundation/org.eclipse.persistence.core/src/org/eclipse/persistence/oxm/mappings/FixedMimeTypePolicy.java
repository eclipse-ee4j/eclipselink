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
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;

/*
 * <p><b>Purpose:</b> Provides a default implementation of MimeTypePolicy to be used for java
 * properties that have a single static mime type. 
 * The mime type will be obtained from the objects field/property that is mapped
 * to the XML attribute with name "contentType" and namespace URI 
 * "http://www.w3.org/2005/05/xmlmime". If this is not set/present then the 
 * fixed value will be returned.
 * 
 *  @see MimeTypePolicy
 *  @see XMLBinaryDataMapping
 *  @see XMLBinaryDataCollectionMapping
 */
public class FixedMimeTypePolicy implements MimeTypePolicy {

    private boolean initialized = false;
    private String aMimeType;
    private DatabaseMapping binaryMapping;
    private DatabaseMapping contentTypeMapping;

    public FixedMimeTypePolicy() {
        initialized = true;
    }

    public FixedMimeTypePolicy(String aMimeTypeParameter) {
        aMimeType = aMimeTypeParameter;
        initialized = true; 
    }

    public FixedMimeTypePolicy(String aMimeTypeParameter, DatabaseMapping mapping) {
        aMimeType = aMimeTypeParameter;
        this.binaryMapping = mapping;
        initialized = null == binaryMapping; 
    }

    /**
     * The mime type will be obtained from the objects field/property that is 
     * mapped to the XML attribute with name "contentType" and namespace URI 
     * "http://www.w3.org/2005/05/xmlmime". If this is not set/present then the 
     * fixed value will be returned.
     */
    public String getMimeType(Object anObject) {
        if(!initialized && null == contentTypeMapping) {
            ClassDescriptor descriptor = binaryMapping.getDescriptor();
            for(DatabaseMapping mapping : descriptor.getMappings()) {
                XMLField xmlField = (XMLField) mapping.getField();
                if(null != xmlField) {
                    XPathFragment xPathFragment = xmlField.getXPathFragment();
                    if(xPathFragment != null && xPathFragment.isAttribute() && XMLConstants.XML_MIME_URL.equals(xPathFragment.getNamespaceURI()) && XMLConstants.CONTENT_TYPE.equals(xPathFragment.getLocalName())) {
                        contentTypeMapping = mapping;
                        break;
                    }
                }
            }
            initialized = true;
        }
        if(null != contentTypeMapping) {
            String contentType = (String) contentTypeMapping.getAttributeValueFromObject(anObject);
            if(null != contentType) {
                return contentType;
            }
        }
        return aMimeType;
    }

    /**
     * Return the default mime type for this policy.  This mime type will be 
     * returned if there is no field/property with a value mapped to an XML 
     * attribute with name "contentType" and URI 
     * "http://www.w3.org/2005/05/xmlmime".
     */
    public String getMimeType() {
        return aMimeType;
    }

    /**
     * Set the default mime type for this policy.  This mime type will be 
     * returned if there is no field/property with a value mapped to an XML 
     * attribute with name "contentType" and URI 
     * "http://www.w3.org/2005/05/xmlmime".
     */
    public void setMimeType(String aString) {
        aMimeType = aString;
    }

}
