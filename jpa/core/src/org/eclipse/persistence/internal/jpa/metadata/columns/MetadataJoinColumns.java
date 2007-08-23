/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * Object to hold onto join column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataJoinColumns {
    protected List<MetadataJoinColumn> m_joinColumns;
    
    /**
     * INTERNAL:
     */
    public MetadataJoinColumns() {
        m_joinColumns = new ArrayList<MetadataJoinColumn>();
    }
    
    /**
     * INTERNAL:
     */
    public MetadataJoinColumns(JoinColumns joinColumns, JoinColumn joinColumn) {
        this();
        
        // Process all the join columns first.
        if (joinColumns != null) {
            for (JoinColumn jColumn : joinColumns.value()) {
                m_joinColumns.add(new MetadataJoinColumn(jColumn));
            }
        }
        
        // Process the single key join column second.
        if (joinColumn != null) {
            m_joinColumns.add(new MetadataJoinColumn(joinColumn));
        }
    }
    
    /**
     * INTERNAL:
     */
    public MetadataJoinColumns(JoinColumn[] joinColumns) {
        this();
        
        for (JoinColumn joinColumn : joinColumns) {
            m_joinColumns.add(new MetadataJoinColumn(joinColumn));
        }
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public List<MetadataJoinColumn> values(MetadataDescriptor descriptor) {
        // If no join columns are specified ...
        if (m_joinColumns.isEmpty()) {
            if (descriptor.hasCompositePrimaryKey()) {
                // Add a default one for each part of the composite primary
                // key. Foreign and primary key to have the same name.
                for (String primaryKeyField : descriptor.getPrimaryKeyFieldNames()) {
                    m_joinColumns.add(new MetadataJoinColumn(primaryKeyField));
                }
            } else {
                // Add a default one for the single case, not setting any
                // foreign and primary key names. They will default based
                // on which accessor is using them.
                m_joinColumns.add(new MetadataJoinColumn());
            }
        } else {
            // Need to update any join columns that use a foreign key name
            // for the primary key name. E.G. User specifies the renamed id
            // field name from a primary key join column as the primary key in
            // an inheritance subclass.
            for (MetadataJoinColumn joinColumn : m_joinColumns) {
                DatabaseField pkField = joinColumn.getPrimaryKeyField();
                pkField.setName(descriptor.getPrimaryKeyJoinColumnAssociation(pkField.getName()));
            }
        }
        
        return m_joinColumns;
    }
}
