/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping file
 ******************************************************************************/    
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
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
    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
    
    /**
     * INTERNAL:
     */
    public CollectionTableMetadata() {
        super("<collection-table>");
    }
    
    /**
     * INTERNAL:
     */
    public CollectionTableMetadata(Annotation collectionTable, MetadataAccessibleObject accessibleObject) {
        super(collectionTable, accessibleObject);
        
        if (collectionTable != null) {
            for (Annotation primaryKeyJoinColumn : (Annotation[]) MetadataHelper.invokeMethod("primaryKeyJoinColumns", collectionTable)) {
                m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata(primaryKeyJoinColumn, accessibleObject));
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getCatalogContext() {
        return MetadataLogger.COLLECTION_TABLE_CATALOG;
    }
    
    /**
     * INTERNAL:
     */
    @Override
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
     * INTERNAL:
     */
    @Override
    public String getSchemaContext() {
        return MetadataLogger.COLLECTION_TABLE_SCHEMA;
    }
    
    /**
     * INTERNAL:
     */
    public void setPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
        m_primaryKeyJoinColumns = primaryKeyJoinColumns;
    }
}
