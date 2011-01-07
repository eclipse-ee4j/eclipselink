/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * INTERNAL:
 * Object to hold onto a secondary table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class SecondaryTableMetadata extends TableMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
    
    /**
     * INTERNAL:
     */
    public SecondaryTableMetadata() {
        super("<secondary-table>");
    }
    
    /**
     * INTERNAL:
     */
    public SecondaryTableMetadata(MetadataAnnotation secondaryTable, MetadataAccessibleObject accessibleObject) {
        super(secondaryTable, accessibleObject);
       
        if (secondaryTable != null) {
            for (Object primaryKeyJoinColumn : (Object[]) secondaryTable.getAttributeArray("pkJoinColumns")) {
                m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata((MetadataAnnotation)primaryKeyJoinColumn, accessibleObject));
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof SecondaryTableMetadata) {
            SecondaryTableMetadata secondaryTable = (SecondaryTableMetadata) objectToCompare;
            return valuesMatch(m_primaryKeyJoinColumns, secondaryTable.getPrimaryKeyJoinColumns());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getCatalogContext() {
        return MetadataLogger.SECONDARY_TABLE_CATALOG;
    }
    
    /**
     * INTERNAL:
     */
    @Override
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
     * INTERNAL:
     */
    @Override
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
}
