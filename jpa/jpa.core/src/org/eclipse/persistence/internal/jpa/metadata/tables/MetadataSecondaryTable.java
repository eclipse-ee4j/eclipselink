/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.tables;

import javax.persistence.SecondaryTable;
import javax.persistence.PrimaryKeyJoinColumn;

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataPrimaryKeyJoinColumns;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.helper.DatabaseTable;

/**
 * Object to hold onto a secondary table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataSecondaryTable extends MetadataTable  {
    private PrimaryKeyJoinColumn[] m_pkJoinColumns;
    protected MetadataPrimaryKeyJoinColumns m_primaryKeyJoinColumns;
    
    /**
     * INTERNAL:
     */
    public MetadataSecondaryTable(MetadataLogger logger) {
        super(logger);
        m_primaryKeyJoinColumns = null; 
    }
    
    /**
     * INTERNAL:
     */
    public MetadataSecondaryTable(SecondaryTable secondaryTable, MetadataLogger logger) {
        this(logger);
        
        if (secondaryTable != null) {
            m_name = secondaryTable.name();
            m_schema = secondaryTable.schema();
            m_catalog = secondaryTable.catalog();
            m_pkJoinColumns = secondaryTable.pkJoinColumns();
            
            processName();
            processUniqueConstraints(secondaryTable.uniqueConstraints());
        }
    }
    
    /**
     * INTERNAL:
     */
    public String getCatalogContext() {
        return m_logger.SECONDARY_TABLE_CATALOG;
    }
    
    /**
     * INTERNAL:
     */
    public String getNameContext() {
        return m_logger.SECONDARY_TABLE_NAME;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataPrimaryKeyJoinColumns getPrimaryKeyJoinColumns(DatabaseTable sourceTable) {
        if (m_primaryKeyJoinColumns == null) {
            processPrimaryKeyJoinColumns(sourceTable);
        }
        
        return m_primaryKeyJoinColumns;
    }
    
    /**
     * INTERNAL:
     */
    public String getSchemaContext() {
        return m_logger.SECONDARY_TABLE_SCHEMA;
    }
    
    /**
     * INTERNAL: (Overridden in XMLSecondaryTable)
     */
    protected void processPrimaryKeyJoinColumns(DatabaseTable sourceTable) {
        m_primaryKeyJoinColumns = new MetadataPrimaryKeyJoinColumns(m_pkJoinColumns, sourceTable, m_databaseTable);
    }
}
