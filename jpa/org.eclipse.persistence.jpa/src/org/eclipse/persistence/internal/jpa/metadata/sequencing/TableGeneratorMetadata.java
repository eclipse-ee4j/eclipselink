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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.sequencing;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;

/**
 * A wrapper class to a table generator metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class TableGeneratorMetadata extends TableMetadata {    
    private Integer m_allocationSize;
    private Integer m_initialValue;
    
    private String m_generatorName;
    private String m_pkColumnValue;
    private String m_pkColumnName;
    private String m_valueColumnName;
    
    /**
     * INTERNAL:
     */
    protected TableGeneratorMetadata() {
        super("<table-generator>");
    }
    
    /**
     * INTERNAL:
     */
    public TableGeneratorMetadata(MetadataAnnotation tableGenerator, MetadataAccessibleObject accessibleObject) {
        super(tableGenerator, accessibleObject);
        
        // Table will process 'name', but 'name' here is the generator name and 
        // the table name is 'table'. Set it correctly.
        m_allocationSize = (Integer) tableGenerator.getAttribute("allocationSize");
        m_initialValue = (Integer) tableGenerator.getAttribute("initialValue");
        m_generatorName = (String) tableGenerator.getAttributeString("name"); 
        m_pkColumnName = (String) tableGenerator.getAttributeString("pkColumnName"); 
        m_pkColumnValue = (String) tableGenerator.getAttributeString("pkColumnValue");
        m_valueColumnName = (String) tableGenerator.getAttributeString("valueColumnName");
        
        setName((String) tableGenerator.getAttribute("table"));
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof TableGeneratorMetadata) {
            TableGeneratorMetadata generator = (TableGeneratorMetadata) objectToCompare;
            
            if (! valuesMatch(m_allocationSize, generator.getAllocationSize())) {
                return false;
            }
            
            if (! valuesMatch(m_initialValue, generator.getInitialValue())) {
                return false;
            }
            
            if (! valuesMatch(m_generatorName, generator.getGeneratorName())) {
                return false;
            }
            
            if (! valuesMatch(m_pkColumnValue, generator.getPkColumnValue())) {
                return false;
            }
            
            if (! valuesMatch(m_pkColumnName, generator.getPkColumnName())) {
                return false;
            }
            
            return valuesMatch(m_valueColumnName, generator.getValueColumnName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getAllocationSize() {
        return m_allocationSize;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getCatalogContext() {
        return MetadataLogger.TABLE_GENERATOR_CATALOG;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getGeneratorName() {
        return m_generatorName;
    }
    
    /**
     * INTERNAL:
     * To satisfy the abstract getIdentifier() method from ORMetadata.
     */
    @Override
    public String getIdentifier() {
        return m_generatorName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getInitialValue() {
        return m_initialValue;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getNameContext() {
        return MetadataLogger.TABLE_GENERATOR_NAME;    
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPkColumnName() {
        return m_pkColumnName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPkColumnValue() {
        return m_pkColumnValue;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String getSchemaContext() {
        return MetadataLogger.TABLE_GENERATOR_SCHEMA;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getValueColumnName() {
        return m_valueColumnName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAllocationSize(Integer allocationSize) {
        m_allocationSize = allocationSize;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setGeneratorName(String generatorName) {
        m_generatorName = generatorName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setInitialValue(Integer initialValue) {
        m_initialValue = initialValue;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPkColumnName(String pkColumnName) {
        m_pkColumnName = pkColumnName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPkColumnValue(String pkColumnValue) {
        m_pkColumnValue = pkColumnValue;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValueColumnName(String valueColumnName) {
        m_valueColumnName = valueColumnName;
    }
}
