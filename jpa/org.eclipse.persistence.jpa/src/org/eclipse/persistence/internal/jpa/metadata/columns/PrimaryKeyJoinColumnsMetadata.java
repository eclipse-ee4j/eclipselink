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
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto join column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class PrimaryKeyJoinColumnsMetadata {
    private List<PrimaryKeyJoinColumnMetadata> m_pkJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
    
    /**
     * INTERNAL:
     */
    public PrimaryKeyJoinColumnsMetadata(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
        // These primary key join columns could have been loaded from XML, 
        // meaning they could be null (eg. were not specified within a 
        // secondary table), therefore check against null.
        if (primaryKeyJoinColumns != null) {
            for (PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn : primaryKeyJoinColumns) {
                m_pkJoinColumns.add(primaryKeyJoinColumn);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public PrimaryKeyJoinColumnsMetadata(MetadataAnnotation[] primaryKeyJoinColumns, MetadataAccessibleObject accessibleObject) {
        // Process the primary key join column array.
        for (MetadataAnnotation pkJoinColumn : primaryKeyJoinColumns) {
            m_pkJoinColumns.add(new PrimaryKeyJoinColumnMetadata(pkJoinColumn, accessibleObject));
        }
    }
    
    /**
     * INTERNAL:
     */
    public PrimaryKeyJoinColumnsMetadata(MetadataAnnotation primaryKeyJoinColumns, MetadataAnnotation primaryKeyJoinColumn, MetadataAccessibleObject accessibleObject) {        
        // Process all the primary key join columns first.
        if (primaryKeyJoinColumns != null) {
            for (Object pkJoinColumn : (Object[])primaryKeyJoinColumns.getAttributeArray("value")) { 
                m_pkJoinColumns.add(new PrimaryKeyJoinColumnMetadata((MetadataAnnotation)pkJoinColumn, accessibleObject));
            }
        }
        
        // Process the single primary key join column second.
        if (primaryKeyJoinColumn != null) {
            m_pkJoinColumns.add(new PrimaryKeyJoinColumnMetadata(primaryKeyJoinColumn, accessibleObject));
        }
    }
    
    /**
     * INTERNAL:
     * 
     * This method is called when it is time to process the primary key join
     * columns. So if the user didn't specify any, then we need to default
     * accordingly.
     */
    public List<PrimaryKeyJoinColumnMetadata> values(MetadataDescriptor descriptor) {
        // If no primary key join columns are specified ...
        if (m_pkJoinColumns.isEmpty()) {
            if (descriptor.hasCompositePrimaryKey()) {
                // Add a default one for each part of the composite primary
                // key. Foreign and primary key to have the same name.
                for (String primaryKeyField : descriptor.getPrimaryKeyFieldNames()) {
                    PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn = new PrimaryKeyJoinColumnMetadata();
                    primaryKeyJoinColumn.setReferencedColumnName(primaryKeyField);
                    primaryKeyJoinColumn.setName(primaryKeyField);
                    m_pkJoinColumns.add(primaryKeyJoinColumn);
                }
            } else {
                // Add a default one for the single case, not setting any
                // foreign and primary key names. They will default based
                // on which accessor is using them.
                m_pkJoinColumns.add(new PrimaryKeyJoinColumnMetadata());
            }
        }
        
        return m_pkJoinColumns;
    }
}
