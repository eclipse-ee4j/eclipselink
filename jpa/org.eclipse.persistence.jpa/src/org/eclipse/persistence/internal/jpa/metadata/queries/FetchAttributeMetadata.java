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
 *     01/19/2010-2.1 Guy Pelletier 
 *       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto a fetch attribute metadata from a named fetch group 
 * metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.1
 */
public class FetchAttributeMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    protected String m_name;

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public FetchAttributeMetadata() {
        super("<fetch-attribute>");
    }

    /**
     * INTERNAL:
     */
    public FetchAttributeMetadata(MetadataAnnotation fetchAttribute, MetadataAccessibleObject accessibleObject) {
        super(fetchAttribute, accessibleObject);
        
        m_name = (String) fetchAttribute.getAttribute("name");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof FetchAttributeMetadata) {
            FetchAttributeMetadata fetchAttribute = (FetchAttributeMetadata) objectToCompare;
            return valuesMatch(m_name, fetchAttribute.getName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
}
