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
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * Object to hold onto an association override meta data.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class AssociationOverrideMetadata extends OverrideMetadata {
    private List<JoinColumnMetadata> m_joinColumns = new ArrayList<JoinColumnMetadata>();
    
    /**
     * INTERNAL:
     * Assumed to be used solely for OX loading.
     */
    public AssociationOverrideMetadata() {
        super("<association-override>");
    }
    
    /**
     * INTERNAL:
     */
    public AssociationOverrideMetadata(Annotation associationOverride, MetadataAccessibleObject accessibleObject) {
        super(associationOverride, accessibleObject);
        
        for (Annotation joinColumn : (Annotation[]) MetadataHelper.invokeMethod("joinColumns", associationOverride)) {
            m_joinColumns.add(new JoinColumnMetadata(joinColumn, accessibleObject));
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof AssociationOverrideMetadata) {
            return valuesMatch(m_joinColumns, ((AssociationOverrideMetadata) objectToCompare).getJoinColumns());
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
    public void setJoinColumns(List<JoinColumnMetadata> joinColumns) {
        m_joinColumns = joinColumns;
    }
}
