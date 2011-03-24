/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     12/2/2009-2.1 Guy Pelletier 
 *       - 296612:  Add current annotation only metadata support of return insert/update to the EclipseLink-ORM.XML Schema
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/ 
package org.eclipse.persistence.internal.jpa.metadata.mappings;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Object to hold onto return insert metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.1
 */
public class ReturnInsertMetadata extends ORMetadata {
    private Boolean m_returnOnly;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ReturnInsertMetadata() {
        super("<return-insert>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ReturnInsertMetadata(MetadataAnnotation returnInsert, MetadataAccessor accessor) {
        super(returnInsert, accessor);

        m_returnOnly = (Boolean) returnInsert.getAttributeBooleanDefaultFalse("returnOnly");
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getReturnOnly() {
        return m_returnOnly;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ReturnInsertMetadata) {
            ReturnInsertMetadata returnInsert = (ReturnInsertMetadata) objectToCompare;
            return valuesMatch(m_returnOnly, returnInsert.getReturnOnly());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor, DatabaseField field) {
        if (m_returnOnly != null && m_returnOnly) {
            descriptor.addFieldForInsertReturnOnly(field);
        } else {
            descriptor.addFieldForInsert(field);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReturnOnly(Boolean returnOnly) {
        m_returnOnly = returnOnly;
    }   
}
