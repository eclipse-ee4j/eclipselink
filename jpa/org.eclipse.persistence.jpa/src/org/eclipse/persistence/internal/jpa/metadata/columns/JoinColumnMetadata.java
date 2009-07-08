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

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * INTERNAL:
 * Object to hold onto join column metadata in an EclipseLink database field.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class JoinColumnMetadata extends PrimaryKeyJoinColumnMetadata {    
    private Boolean m_unique;
    private Boolean m_nullable;
    private Boolean m_updatable;
    private Boolean m_insertable;
    
    private String m_table;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public JoinColumnMetadata() {
        super("<join-column>");
    }
    
    /**
     * INTERNAL:
     */
    public JoinColumnMetadata(Annotation joinColumn, MetadataAccessibleObject accessibleObject) {
        super(joinColumn, accessibleObject);
        
        if (joinColumn != null) {
            m_table = ((String) MetadataHelper.invokeMethod("table", joinColumn));
            m_unique = ((Boolean) MetadataHelper.invokeMethod("unique", joinColumn));
            m_nullable = ((Boolean) MetadataHelper.invokeMethod("nullable", joinColumn));
            m_updatable = ((Boolean) MetadataHelper.invokeMethod("updatable", joinColumn));
            m_insertable = ((Boolean) MetadataHelper.invokeMethod("insertable", joinColumn));
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof JoinColumnMetadata) {
            JoinColumnMetadata joinColumn = (JoinColumnMetadata) objectToCompare;
            
            if (! valuesMatch(m_unique, joinColumn.getUnique())) {
                return false;
            }
            
            if (! valuesMatch(m_nullable, joinColumn.getNullable())) {
                return false;
            }
            
            if (! valuesMatch(m_updatable, joinColumn.getUpdatable())) {
                return false;
            }
            
            if (! valuesMatch(m_insertable, joinColumn.getInsertable())) {
                return false;
            }
            
            return valuesMatch(m_table, joinColumn.getTable());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public DatabaseField getForeignKeyField() {
        // Initialize the DatabaseField with values and defaults.
        DatabaseField fkField = super.getForeignKeyField();
        
        fkField.setTableName(m_table == null ? "" : m_table);
        fkField.setUnique(m_unique == null ? false : m_unique.booleanValue());
        fkField.setNullable(m_nullable == null ? true : m_nullable.booleanValue());
        fkField.setUpdatable(m_updatable == null ? true : m_updatable.booleanValue());
        fkField.setInsertable(m_insertable == null ? true : m_insertable.booleanValue());
        
        return fkField;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getInsertable() {
        return m_insertable;    
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getNullable() {
        return m_nullable;    
    }
    
    /**
     * INTERNAL:
     * USed for OX mapping.
     */
    public String getTable() {
        return m_table;    
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getUnique() {
        return m_unique;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getUpdatable() {
        return m_updatable;    
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setInsertable(Boolean insertable) {
        m_insertable = insertable;    
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setNullable(Boolean nullable) {
        m_nullable = nullable;    
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTable(String table) {
        m_table = table;    
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setUnique(Boolean unique) {
        m_unique = unique;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setUpdatable(Boolean updatable) {
        m_updatable = updatable;    
    }
}
