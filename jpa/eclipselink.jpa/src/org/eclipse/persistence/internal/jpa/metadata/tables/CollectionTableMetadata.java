/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/    
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * INTERNAL:
 * Object to hold onto a collection table metadata in an EclipseLink 
 * database table.
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
    public CollectionTableMetadata(Object collectionTable, String annotatedElementName) {
    	super(annotatedElementName);
    	
        if (collectionTable != null) {
            setName((String)MetadataHelper.invokeMethod("name", collectionTable));
            setSchema((String)MetadataHelper.invokeMethod("schema", collectionTable));
            setCatalog((String)MetadataHelper.invokeMethod("catalog", collectionTable));
            setUniqueConstraints((Object[])MetadataHelper.invokeMethod("uniqueConstraints", collectionTable));
            setPrimaryKeyJoinColumns((Object[])MetadataHelper.invokeMethod("primaryKeyJoinColumns", collectionTable));
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
    protected void setPrimaryKeyJoinColumns(Object[] primaryKeyJoinColumns) {
    	m_primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
    	
    	for (Object primaryKeyJoinColumn : primaryKeyJoinColumns) {
    		m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata(primaryKeyJoinColumn));
    	}
    }
}
