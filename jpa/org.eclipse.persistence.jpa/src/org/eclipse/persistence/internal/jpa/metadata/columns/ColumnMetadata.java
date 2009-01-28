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
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * INTERNAL:
 * Object to hold onto column metadata in a TopLink database field.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class ColumnMetadata extends ORMetadata {
    private Boolean m_unique;
    private Boolean m_nullable;
    private Boolean m_updatable;
    private Boolean m_insertable;

    private DatabaseField m_databaseField;
    
    private Integer m_scale;
    private Integer m_length;
    private Integer m_precision;

    private String m_attributeName;
    private String m_name;
    private String m_table;
    private String m_columnDefinition;    
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata() {
        super("<column>");
    }
    
    /**
     * INTERNAL:
     */
    public ColumnMetadata(Annotation column, MetadataAccessibleObject accessibleObject) {
        this(column, accessibleObject, "");
    }
    
    /**
     * INTERNAL:
     */
    public ColumnMetadata(Annotation column, MetadataAccessibleObject accessibleObject, String attributeName) {
        super(column, accessibleObject);
        
        m_attributeName = attributeName;
        
        if (column != null) {
            // Apply the values from the column annotation.
            setUnique((Boolean) MetadataHelper.invokeMethod("unique", column));
            setNullable((Boolean) MetadataHelper.invokeMethod("nullable", column));
            setUpdatable((Boolean) MetadataHelper.invokeMethod("updatable", column));
            setInsertable((Boolean) MetadataHelper.invokeMethod("insertable", column));
        
            setScale((Integer) MetadataHelper.invokeMethod("scale", column));
            setLength((Integer) MetadataHelper.invokeMethod("length", column));
            setPrecision((Integer) MetadataHelper.invokeMethod("precision", column));
        
            setName((String) MetadataHelper.invokeMethod("name", column));
            setTable((String) MetadataHelper.invokeMethod("table", column));
            setColumnDefinition((String) MetadataHelper.invokeMethod("columnDefinition", column));
        }
    }
    
    /**
     * INTERNAL:
     */
    public ColumnMetadata(MetadataAccessibleObject accessibleObject, String attributeName) {
        this(null, accessibleObject, attributeName);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ColumnMetadata) {
            ColumnMetadata column = (ColumnMetadata) objectToCompare;
            
            if (! valuesMatch(m_unique, column.getUnique())) {
                return false;
            }
            
            if (! valuesMatch(m_nullable, column.getNullable())) {
                return false;
            }
            
            if (! valuesMatch(m_updatable, column.getUpdatable())) {
                return false;
            }
            
            if (! valuesMatch(m_insertable, column.getInsertable())) {
                return false;
            }
            
            if (! valuesMatch(m_scale, column.getScale())) {
                return false;
            }
            
            if (! valuesMatch(m_length, column.getLength())) {
                return false;
            }
            
            if (! valuesMatch(m_precision, column.getPrecision())) {
                return false;
            }
            
            if (! valuesMatch(m_name, column.getName())) {
                return false;
            }
            
            if (! valuesMatch(m_table, column.getTable())) {
                return false;
            }
            
            return valuesMatch(m_columnDefinition, column.getColumnDefinition());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public String getAttributeName() {
        return m_attributeName;
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
    public DatabaseField getDatabaseField() {
        if (m_databaseField == null) {
            // Initialize the DatabaseField with values and defaults.
            m_databaseField = new DatabaseField();
            
            m_databaseField.setUnique(m_unique == null ? false : m_unique.booleanValue());
            m_databaseField.setNullable(m_nullable == null ? true : m_nullable.booleanValue());
            m_databaseField.setUpdatable(m_updatable == null ? true : m_updatable.booleanValue());
            m_databaseField.setInsertable(m_insertable == null ? true : m_insertable.booleanValue());
            
            m_databaseField.setScale(m_scale == null ? 0 : m_scale.intValue());
            m_databaseField.setLength(m_length == null ? 255 : m_length.intValue());
            m_databaseField.setPrecision(m_precision == null ? 0 : m_precision.intValue());
            
            m_databaseField.setName(m_name == null ? "" : m_name);
            m_databaseField.setTableName(m_table == null ? "" : m_table);
            m_databaseField.setColumnDefinition(m_columnDefinition == null ? "" : m_columnDefinition);
        }
        
        return m_databaseField;
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
    public Integer getLength() {
        return m_length;
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
     * Used for OX mapping.
     */
    public Boolean getNullable() {
        return m_nullable;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getPrecision() {
        return m_precision;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getScale() {
        return m_scale;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
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
     */
    public String getUpperCaseAttributeName() {
        return m_attributeName.toUpperCase();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
        
        m_attributeName = accessibleObject.getAttributeName();
    }
    
    /**
     * INTERNAL:
     * The attribute name is used when loggin messages. Users should call this 
     * method if they would like to override the attribute name for this column
     * @see AttributeOverrideMetadata initXMLObject. 
     */
    public void setAttributeName(String attributeName) {
        m_attributeName = attributeName;
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
     * This method will get called if we have an attribute override that
     * overrides another attribute override. See EmbeddedAccessor.
     */
    public void setDatabaseField(DatabaseField databaseField) {
        m_databaseField = databaseField;
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
    public void setLength(Integer length) {
        m_length = length;
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
    public void setNullable(Boolean nullable) {
        m_nullable = nullable;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPrecision(Integer precision) {
        m_precision = precision;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setScale(Integer scale) {
        m_scale = scale;
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
