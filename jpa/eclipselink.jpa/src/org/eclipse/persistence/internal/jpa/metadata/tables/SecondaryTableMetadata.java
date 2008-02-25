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
 * Object to hold onto a secondary table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class SecondaryTableMetadata extends TableMetadata  {
    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns;
    
    /**
     * INTERNAL:
     */
    public SecondaryTableMetadata() {}
    
    /**
     * INTERNAL:
     */
    public SecondaryTableMetadata(Object secondaryTable, String entityClassName) {
    	super(entityClassName);
	   
        if (secondaryTable != null) {
            setName((String)MetadataHelper.invokeMethod("name", secondaryTable, (Object[])null));
            setSchema((String)MetadataHelper.invokeMethod("schema", secondaryTable, (Object[])null));
            setCatalog((String)MetadataHelper.invokeMethod("catalog", secondaryTable, (Object[])null));
            setUniqueConstraints((Object[])MetadataHelper.invokeMethod("uniqueConstraints", secondaryTable, (Object[])null));
            setPrimaryKeyJoinColumns((Object[])MetadataHelper.invokeMethod("pkJoinColumns", secondaryTable, (Object[])null));
        }
    }
    
    /**
     * INTERNAL: (Override from MetadataTable)
     */
    public String getCatalogContext() {
        return MetadataLogger.SECONDARY_TABLE_CATALOG;
    }
    
    /**
     * INTERNAL: (Override from MetadataTable)
     */
    public String getNameContext() {
        return MetadataLogger.SECONDARY_TABLE_NAME;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<PrimaryKeyJoinColumnMetadata> getPrimaryKeyJoinColumns() {
        return m_primaryKeyJoinColumns;
    }
    
    /**
     * INTERNAL: (Override from MetadataTable)
     */
    public String getSchemaContext() {
        return MetadataLogger.SECONDARY_TABLE_SCHEMA;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
    	m_primaryKeyJoinColumns = primaryKeyJoinColumns;
    }
    
    /**
     * INTERNAL:
     * Called from annotation population.
     */
    protected void setPrimaryKeyJoinColumns(Object[] primaryKeyJoinColumns) {
    	m_primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
    	
    	for (Object primaryKeyJoinColumn : primaryKeyJoinColumns) {
    		m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata(primaryKeyJoinColumn));
    	}
    }
}
