/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

/**
 * Object to hold onto join column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class PrimaryKeyJoinColumnsMetadata {
    private List<PrimaryKeyJoinColumnMetadata> m_pkJoinColumns;
    
    /**
     * INTERNAL:
     */
    public PrimaryKeyJoinColumnsMetadata(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
    	// These primary key join columns could have been loaded from XML, 
    	// meaning they could be null (eg. we not specified within a secondary
    	// table), therefore initialize an empty list.
    	if (primaryKeyJoinColumns == null) {
    		m_pkJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();	
    	} else {
    		m_pkJoinColumns = primaryKeyJoinColumns;
    	}
    }
    
    /**
     * INTERNAL:
     */
    public PrimaryKeyJoinColumnsMetadata(PrimaryKeyJoinColumn[] primaryKeyJoinColumns) {
    	m_pkJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
        
        // Process the primary key join column array.
        for (PrimaryKeyJoinColumn pkJoinColumn : primaryKeyJoinColumns) {
            m_pkJoinColumns.add(new PrimaryKeyJoinColumnMetadata(pkJoinColumn));
        }
    }
    
    /**
     * INTERNAL:
     */
    public PrimaryKeyJoinColumnsMetadata(PrimaryKeyJoinColumns primaryKeyJoinColumns, PrimaryKeyJoinColumn primaryKeyJoinColumn) {
    	m_pkJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
        
        // Process all the primary key join columns first.
        if (primaryKeyJoinColumns != null) {
            for (PrimaryKeyJoinColumn pkJoinColumn : primaryKeyJoinColumns.value()) {
                m_pkJoinColumns.add(new PrimaryKeyJoinColumnMetadata(pkJoinColumn));
            }
        }
        
        // Process the single primary key join column second.
        if (primaryKeyJoinColumn != null) {
            m_pkJoinColumns.add(new PrimaryKeyJoinColumnMetadata(primaryKeyJoinColumn));
        }
    }
    
    /**
     * INTERNAL:
     * 
     * This method is called when it is time to process the primary key join
     * columns. So if the user didn't specify any, then we need to default
     * accordingly.
     */
    public List<PrimaryKeyJoinColumnMetadata> values(MetadataDescriptor descriptor) {
        // If no primary key join columns are specified ...
        if (m_pkJoinColumns.isEmpty()) {
            if (descriptor.hasCompositePrimaryKey()) {
                // Add a default one for each part of the composite primary
                // key. Foreign and primary key to have the same name.
                for (String primaryKeyField : descriptor.getPrimaryKeyFieldNames()) {
                	PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn = new PrimaryKeyJoinColumnMetadata();
                	primaryKeyJoinColumn.setReferencedColumnName(primaryKeyField);
                	primaryKeyJoinColumn.setName(primaryKeyField);
                    m_pkJoinColumns.add(primaryKeyJoinColumn);
                }
            } else {
                // Add a default one for the single case, not setting any
                // foreign and primary key names. They will default based
                // on which accessor is using them.
                m_pkJoinColumns.add(new PrimaryKeyJoinColumnMetadata());
            }
        }
        
        return m_pkJoinColumns;
    }
}
