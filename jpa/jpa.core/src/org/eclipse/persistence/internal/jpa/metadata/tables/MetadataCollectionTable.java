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

import org.eclipse.persistence.annotations.CollectionTable;

import javax.persistence.PrimaryKeyJoinColumn;

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataPrimaryKeyJoinColumns;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.helper.DatabaseTable;

/**
 * Object to hold onto a collection table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class MetadataCollectionTable extends MetadataTable {
    private PrimaryKeyJoinColumn[] m_primaryKeyJoinColumns;
    protected MetadataPrimaryKeyJoinColumns m_metadataPrimaryKeyJoinColumns;
    
    /**
     * INTERNAL:
     */
    public MetadataCollectionTable(MetadataLogger logger) {
        super(logger);
        m_primaryKeyJoinColumns = new PrimaryKeyJoinColumn[]{};
    }
    
    /**
     * INTERNAL:
     */
    public MetadataCollectionTable(CollectionTable collectionTable, MetadataLogger logger) {
        this(logger);
        
        if (collectionTable != null) {
            m_name = collectionTable.name();
            m_schema = collectionTable.schema();
            m_catalog = collectionTable.catalog();
            m_primaryKeyJoinColumns = collectionTable.primaryKeyJoinColumns();
            
            processName();
            processUniqueConstraints(collectionTable.uniqueConstraints());
        }
    }
    
    /**
     * INTERNAL:
     */
    public String getCatalogContext() {
        return m_logger.COLLECTION_TABLE_CATALOG;
    }
    
    /**
     * INTERNAL:
     */
    public String getNameContext() {
        return m_logger.COLLECTION_TABLE_NAME;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataPrimaryKeyJoinColumns getPrimaryKeyJoinColumns(DatabaseTable sourceTable, DatabaseTable collectionTable) {
        if (m_metadataPrimaryKeyJoinColumns == null) {
            m_metadataPrimaryKeyJoinColumns = processPrimaryKeyJoinColumns(sourceTable, collectionTable);
        }
        
        return m_metadataPrimaryKeyJoinColumns;
    }
    
    /**
     * INTERNAL:
     */
    public String getSchemaContext() {
        return m_logger.COLLECTION_TABLE_SCHEMA;
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return false;
    }
    
    /**
     * INTERNAL: (Overridden in XMLCollectionTable)
     */
    protected MetadataPrimaryKeyJoinColumns processPrimaryKeyJoinColumns(DatabaseTable sourceTable, DatabaseTable collectionTable) {
        return new MetadataPrimaryKeyJoinColumns(m_primaryKeyJoinColumns, sourceTable, collectionTable);
    }
}
