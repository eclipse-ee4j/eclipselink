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

import org.eclipse.persistence.internal.helper.DatabaseTable;

/**
 * Object to hold onto join column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataPrimaryKeyJoinColumns {
    private DatabaseTable m_sourceTable;
    private DatabaseTable m_targetTable;
    protected List<MetadataPrimaryKeyJoinColumn> m_pkJoinColumns;
    
    /**
     * INTERNAL:
     */
    public MetadataPrimaryKeyJoinColumns(DatabaseTable sourceTable, DatabaseTable targetTable) {
        m_sourceTable = sourceTable;
        m_targetTable = targetTable;
        m_pkJoinColumns = new ArrayList<MetadataPrimaryKeyJoinColumn>();
    }
    
    /**
     * INTERNAL:
     */
    public MetadataPrimaryKeyJoinColumns(PrimaryKeyJoinColumn[] primaryKeyJoinColumns, DatabaseTable sourceTable, DatabaseTable targetTable) {
        this(sourceTable, targetTable);
        
        // Process the primary key join column array.
        for (PrimaryKeyJoinColumn pkJoinColumn : primaryKeyJoinColumns) {
            m_pkJoinColumns.add(new MetadataPrimaryKeyJoinColumn(pkJoinColumn, sourceTable, targetTable));
        }
    }
    
    /**
     * INTERNAL:
     */
    public MetadataPrimaryKeyJoinColumns(PrimaryKeyJoinColumns primaryKeyJoinColumns, PrimaryKeyJoinColumn primaryKeyJoinColumn, DatabaseTable sourceTable, DatabaseTable targetTable) {
        this(sourceTable, targetTable);
        
        // Process all the primary key join columns first.
        if (primaryKeyJoinColumns != null) {
            for (PrimaryKeyJoinColumn pkJoinColumn : primaryKeyJoinColumns.value()) {
                m_pkJoinColumns.add(new MetadataPrimaryKeyJoinColumn(pkJoinColumn, sourceTable, targetTable));
            }
        }
        
        // Process the single primary key join column second.
        if (primaryKeyJoinColumn != null) {
            m_pkJoinColumns.add(new MetadataPrimaryKeyJoinColumn(primaryKeyJoinColumn, sourceTable, targetTable));
        }
    }
    
    /**
     * INTERNAL:
     * 
     * This method is called when it is time to process the primary key join
     * columns. So if the user didn't specify any, then we need to default
     * accordingly.
     */
    public List<MetadataPrimaryKeyJoinColumn> values(MetadataDescriptor descriptor) {
        // If no primary key join columns are specified ...
        if (m_pkJoinColumns.isEmpty()) {
            if (descriptor.hasCompositePrimaryKey()) {
                // Add a default one for each part of the composite primary
                // key. Foreign and primary key to have the same name.
                for (String primaryKeyField : descriptor.getPrimaryKeyFieldNames()) {
                    m_pkJoinColumns.add(new MetadataPrimaryKeyJoinColumn(m_sourceTable, m_targetTable, primaryKeyField));
                }
            } else {
                // Add a default one for the single case, not setting any
                // foreign and primary key names. They will default based
                // on which accessor is using them.
                m_pkJoinColumns.add(new MetadataPrimaryKeyJoinColumn(m_sourceTable, m_targetTable));
            }
        }
        
        return m_pkJoinColumns;
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return false;
    }
}
