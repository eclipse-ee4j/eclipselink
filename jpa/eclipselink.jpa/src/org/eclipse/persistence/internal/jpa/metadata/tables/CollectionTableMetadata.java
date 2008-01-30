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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PrimaryKeyJoinColumn;

import org.eclipse.persistence.annotations.CollectionTable;

import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * INTERNAL:
 * Object to hold onto a collection table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class CollectionTableMetadata extends TableMetadata {
    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns;
    
    /**
     * INTERNAL:
     */
    public CollectionTableMetadata() {}
    
    /**
     * INTERNAL:
     */
    public CollectionTableMetadata(CollectionTable collectionTable, String annotatedElementName) {
    	super(annotatedElementName);
    	
        if (collectionTable != null) {
            setName(collectionTable.name());
            setSchema(collectionTable.schema());
            setCatalog(collectionTable.catalog());
            setUniqueConstraints(collectionTable.uniqueConstraints());
            setPrimaryKeyJoinColumns(collectionTable.primaryKeyJoinColumns());
        }
    }
    
    /**
     * INTERNAL: (Override from MetadataTable)
     */
    public String getCatalogContext() {
        return MetadataLogger.COLLECTION_TABLE_CATALOG;
    }
    
    /**
     * INTERNAL: (Override from MetadataTable)
     */
    public String getNameContext() {
        return MetadataLogger.COLLECTION_TABLE_NAME;
    }
    
    /**
     * INTERNAL:
     */
    public List<PrimaryKeyJoinColumnMetadata> getPrimaryKeyJoinColumns() {
        return m_primaryKeyJoinColumns;
    }
    
    /**
     * INTERNAL: (Override from MetadataTable)
     */
    public String getSchemaContext() {
        return MetadataLogger.COLLECTION_TABLE_SCHEMA;
    }
    
    /**
     * INTERNAL:
     */
    public void setPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
    	m_primaryKeyJoinColumns = primaryKeyJoinColumns;
    }
    
    /**
     * INTERNAL:
     */
    protected void setPrimaryKeyJoinColumns(PrimaryKeyJoinColumn[] primaryKeyJoinColumns) {
    	m_primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
    	
    	for (PrimaryKeyJoinColumn primaryKeyJoinColumn : primaryKeyJoinColumns) {
    		m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata(primaryKeyJoinColumn));
    	}
    }
}
