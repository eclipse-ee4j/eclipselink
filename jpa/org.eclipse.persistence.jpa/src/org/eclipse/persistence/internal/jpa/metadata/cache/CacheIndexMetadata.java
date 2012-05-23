/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.cache;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.CacheIndex;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto cache index metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class CacheIndexMetadata extends ORMetadata {
    
    private List<String> m_columnNames = new ArrayList();

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public CacheIndexMetadata() {
        super("<cache-index>");
    }

    /**
     * INTERNAL:
     */
    public CacheIndexMetadata(MetadataAnnotation index, MetadataAccessor accessor) {
        super(index, accessor);
        
        if (index != null) {            
            for (Object columnName : (Object[]) index.getAttributeArray("columnNames")) {
                m_columnNames.add((String)columnName);
            }
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof CacheIndexMetadata) {
            CacheIndexMetadata index = (CacheIndexMetadata) objectToCompare;
                        
            return this.m_columnNames.equals(index.getColumnNames());
        }
        
        return false;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public List<String> getColumnNames() {
        return m_columnNames;
    }
    
    /**
     * INTERNAL:
     * Process the index metadata
     */
    public void process(MetadataDescriptor descriptor, String defaultColumnName) {
        if (m_columnNames.isEmpty() && (defaultColumnName != null)) {
            descriptor.getClassDescriptor().getCachePolicy().addCacheIndex(defaultColumnName);
        } else {
            CacheIndex index = new CacheIndex();
            for (String column : m_columnNames) {
                index.addFieldName(column);
            }
            descriptor.getClassDescriptor().getCachePolicy().addCacheIndex(index);            
        }
    }

    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setColumnNames(List<String> columnNames) {
        this.m_columnNames = columnNames;
    }
}
