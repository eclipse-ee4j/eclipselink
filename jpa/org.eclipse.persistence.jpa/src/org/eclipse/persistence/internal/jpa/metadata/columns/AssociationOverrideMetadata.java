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
 *       - 218084: Implement metadata merging functionality between mapping files
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.tables.JoinTableMetadata;

/**
 * Object to hold onto an association override meta data.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class AssociationOverrideMetadata extends OverrideMetadata {
    private JoinTableMetadata m_joinTable;
    private List<JoinColumnMetadata> m_joinColumns = new ArrayList<JoinColumnMetadata>();
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public AssociationOverrideMetadata() {
        super("<association-override>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public AssociationOverrideMetadata(MetadataAnnotation associationOverride, MetadataAccessor accessor) {
        super(associationOverride, accessor);
        
        // Set the join columns. 
        for (Object joinColumn : (Object[]) associationOverride.getAttributeArray("joinColumns")) {
            m_joinColumns.add(new JoinColumnMetadata((MetadataAnnotation)joinColumn, accessor));
        }
        
        // Set the join table.
        m_joinTable = new JoinTableMetadata((MetadataAnnotation) associationOverride.getAttribute("joinTable"), accessor);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof AssociationOverrideMetadata) {
            AssociationOverrideMetadata associationOverride = (AssociationOverrideMetadata) objectToCompare;
            
            if (! valuesMatch(m_joinColumns, associationOverride.getJoinColumns())) {
                return false;
            }
            
            return valuesMatch(m_joinTable, associationOverride.getJoinTable());
        }
        
        return false;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String getIgnoreMappedSuperclassContext() {
        return MetadataLogger.IGNORE_MAPPED_SUPERCLASS_ASSOCIATION_OVERRIDE;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<JoinColumnMetadata> getJoinColumns() {
        return m_joinColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public JoinTableMetadata getJoinTable() {
        return m_joinTable;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJoinColumns(List<JoinColumnMetadata> joinColumns) {
        m_joinColumns = joinColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJoinTable(JoinTableMetadata joinTable) {
        m_joinTable = joinTable;
    }
}
