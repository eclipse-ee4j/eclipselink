/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 ******************************************************************************/    
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
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
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    private List<JoinColumnMetadata> m_joinColumns = new ArrayList<JoinColumnMetadata>();
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
    public CollectionTableMetadata(MetadataAccessibleObject accessibleObject) {
        super(null, accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    public CollectionTableMetadata(MetadataAnnotation collectionTable, MetadataAccessibleObject accessibleObject, boolean isJPACollectionTable) {
        super(collectionTable, accessibleObject);
        
        if (collectionTable != null) {
            if (isJPACollectionTable) {
                for (Object joinColumn : (Object[]) collectionTable.getAttributeArray("joinColumns")) {
                    m_joinColumns.add(new JoinColumnMetadata((MetadataAnnotation)joinColumn, accessibleObject));
                }
            } else {
                for (Object primaryKeyJoinColumn : (Object[]) collectionTable.getAttributeArray("primaryKeyJoinColumns")) {
                    m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata((MetadataAnnotation)primaryKeyJoinColumn, accessibleObject));
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare)&& objectToCompare instanceof CollectionTableMetadata) {
            CollectionTableMetadata collectionTable = (CollectionTableMetadata) objectToCompare;
            
            if (! valuesMatch(m_joinColumns, collectionTable.getJoinColumns())) {
                return false;
            }
            
            return valuesMatch(m_primaryKeyJoinColumns, collectionTable.getPrimaryKeyJoinColumns());
        }
        
        return false;
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
    public List<JoinColumnMetadata> getJoinColumns() {
        return m_joinColumns;
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
    public void setJoinColumns(List<JoinColumnMetadata> joinColumns) {
        m_joinColumns = joinColumns;
    }
    
    /**
     * INTERNAL:
     */
    public void setPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
        m_primaryKeyJoinColumns = primaryKeyJoinColumns;
    }
}
