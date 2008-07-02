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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping file
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;

/**
 * INTERNAL:
 * Object to hold onto table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class JoinTableMetadata extends TableMetadata {
    private List<JoinColumnMetadata> m_joinColumns = new ArrayList<JoinColumnMetadata>();
    private List<JoinColumnMetadata> m_inverseJoinColumns = new ArrayList<JoinColumnMetadata>();
    
    /**
     * INTERNAL:
     */
    public JoinTableMetadata() {
        super("<join-table>");
    }
    
    /**
     * INTERNAL:
     */
    public JoinTableMetadata(Annotation joinTable, MetadataAccessibleObject accessibleObject) {
        super(joinTable, accessibleObject);
        
        if (joinTable != null) {
            for (Annotation joinColumn : (Annotation[]) MetadataHelper.invokeMethod("joinColumns", joinTable)) {
                m_joinColumns.add(new JoinColumnMetadata(joinColumn, accessibleObject));
            }  
        
            for (Annotation inverseJoinColumn : (Annotation[]) MetadataHelper.invokeMethod("inverseJoinColumns", joinTable)) {
                m_inverseJoinColumns.add(new JoinColumnMetadata(inverseJoinColumn, accessibleObject));
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getCatalogContext() {
        return MetadataLogger.JOIN_TABLE_CATALOG;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<JoinColumnMetadata> getInverseJoinColumns() {
        return m_inverseJoinColumns;
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
     */
    @Override
    public String getNameContext() {
        return MetadataLogger.JOIN_TABLE_NAME;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getSchemaContext() {
        return MetadataLogger.JOIN_TABLE_SCHEMA;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setInverseJoinColumns(List<JoinColumnMetadata> inverseJoinColumns) {
        m_inverseJoinColumns = inverseJoinColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJoinColumns(List<JoinColumnMetadata> joinColumns) {
        m_joinColumns = joinColumns;
    }
}
