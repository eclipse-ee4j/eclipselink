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

import java.lang.annotation.Annotation;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * INTERNAL:
 * Object to hold onto join column metadata in a TopLink database fields.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class PrimaryKeyJoinColumnMetadata extends ORMetadata {
    private String m_name;
    private String m_columnDefinition;
    private String m_referencedColumnName;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public PrimaryKeyJoinColumnMetadata() {
        super("<primary-key-join-column>");
    }
    
    /**
     * INTERNAL:
     */
    public PrimaryKeyJoinColumnMetadata(Annotation primaryKeyJoinColumn, MetadataAccessibleObject accessibleObject) {
        super(primaryKeyJoinColumn, accessibleObject);
        
        if (primaryKeyJoinColumn != null) {
            m_name = ((String) MetadataHelper.invokeMethod("name", primaryKeyJoinColumn));
            m_columnDefinition = ((String) MetadataHelper.invokeMethod("columnDefinition", primaryKeyJoinColumn));
            m_referencedColumnName = ((String) MetadataHelper.invokeMethod("referencedColumnName", primaryKeyJoinColumn));
        }
    }

    /**
     * INTERNAL:
     */
    protected PrimaryKeyJoinColumnMetadata(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof PrimaryKeyJoinColumnMetadata) {
            PrimaryKeyJoinColumnMetadata joinColumn = (PrimaryKeyJoinColumnMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, joinColumn.getName())) {
                return false;
            }
            
            if (! valuesMatch(m_columnDefinition, joinColumn.getColumnDefinition())) {
                return false;
            }

            return valuesMatch(m_referencedColumnName, joinColumn.getReferencedColumnName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getColumnDefinition() {
        return m_columnDefinition;
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getForeignKeyField() {
        DatabaseField fkField = new DatabaseField();
        
        fkField.setName(m_name == null ? "" : m_name);
        fkField.setColumnDefinition(m_columnDefinition == null ? "" : m_columnDefinition);
        
        return fkField;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;    
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getPrimaryKeyField() {
        DatabaseField pkField = new DatabaseField();
        
        pkField.setName(m_referencedColumnName == null ? "" : m_referencedColumnName);
        
        return pkField;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getReferencedColumnName() {
        return m_referencedColumnName;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isForeignKeyFieldNotSpecified() {
        return m_name == null || m_name.equals("");
    }
    
    /**
     * INTERNAL:
     */
    public boolean isPrimaryKeyFieldNotSpecified() {
        return m_referencedColumnName == null || m_referencedColumnName.equals("");
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumnDefinition(String columnDefinition) {
        m_columnDefinition = columnDefinition;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;    
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReferencedColumnName(String referencedColumnName) {
        m_referencedColumnName = referencedColumnName;
    }
}
