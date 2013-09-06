/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.mappings;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * INTERNAL:
 * Object to represent the cascade types specified for a relationship
 * mapping element.
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
 * @since EclipseLink 2.5.1
 */
public class BatchFetchMetadata extends ORMetadata {
    private Integer m_size;
    private String m_type;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public BatchFetchMetadata() {
        super("<batch-fetch>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public BatchFetchMetadata(MetadataAnnotation batchFetch, MetadataAccessor accessor) {
        super(batchFetch, accessor);
        
        m_type = batchFetch.getAttributeString("value");
        m_size = batchFetch.getAttributeInteger("size");

    }
    
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof BatchFetchMetadata) {
            BatchFetchMetadata batchFetch = (BatchFetchMetadata) objectToCompare;
            
            if (! valuesMatch(m_type, batchFetch.getType())) {
                return false;
            }
           
            return valuesMatch(m_size, batchFetch.getSize());
        }
        
        return false;
    } 

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getSize() {
        return m_size;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getType() {
        return m_type;
    }

    /**
     * INTERNAL:
     * Process the batch fetch metadata for the given mapping.
     */
    public void process(ForeignReferenceMapping mapping) {
        mapping.setBatchFetchType(BatchFetchType.valueOf(getType()));
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSize(Integer size) {
        m_size = size;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setType(String type) {
        m_type = type;
    }
}
