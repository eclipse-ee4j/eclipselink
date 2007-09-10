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

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataJoinColumns;

/**
 * Object to hold onto table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataJoinTable extends MetadataTable {
    private JoinColumn[] m_joinColumns;
    private JoinColumn[] m_inverseJoinColumns;
    protected MetadataJoinColumns m_jColumns;
    protected MetadataJoinColumns m_inverseJColumns;
    
    /**
     * INTERNAL:
     */
    public MetadataJoinTable(MetadataLogger logger) {
        super(logger);
        
        m_joinColumns = new JoinColumn[] {};
        m_inverseJoinColumns = new JoinColumn[] {};
    }
    
    /**
     * INTERNAL:
     */
    public MetadataJoinTable(JoinTable joinTable, MetadataLogger logger) {
        this(logger);
        
        if (joinTable != null) {
            m_name = joinTable.name();
            m_schema = joinTable.schema();
            m_catalog = joinTable.catalog();
            m_joinColumns = joinTable.joinColumns();
            m_inverseJoinColumns = joinTable.inverseJoinColumns();
            
            processName();
            processUniqueConstraints(joinTable.uniqueConstraints());
        }
    }
    
    /**
     * INTERNAL:
     */
    public String getCatalogContext() {
        return m_logger.JOIN_TABLE_CATALOG;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataJoinColumns getInverseJoinColumns() {
        if (m_inverseJColumns == null) {
            m_inverseJColumns = processInverseJoinColumns();
        }
        
        return m_inverseJColumns;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataJoinColumns getJoinColumns() {
        if (m_jColumns == null) {
            m_jColumns = processJoinColumns();
        }
        
        return m_jColumns;
    }
    
    /**
     * INTERNAL:
     */
    public String getNameContext() {
        return m_logger.JOIN_TABLE_NAME;
    }
    
    /**
     * INTERNAL:
     */
    public String getSchemaContext() {
        return m_logger.JOIN_TABLE_SCHEMA;
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return false;
    }
    
    /**
     * INTERNAL: (Overridden in XMLJoinTable)
     */
    protected MetadataJoinColumns processInverseJoinColumns() {
        return new MetadataJoinColumns(m_inverseJoinColumns);
    }
    
    /**
     * INTERNAL: (Overridden in XMLJoinTable)
     */
    protected MetadataJoinColumns processJoinColumns() {
        return new MetadataJoinColumns(m_joinColumns);
    }
}
