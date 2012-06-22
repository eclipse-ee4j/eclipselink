/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 *     07/23/2010-2.2 Guy Pelletier 
 *       - 237902: DDL GEN doesn't qualify SEQUENCE table with persistence unit schema
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.sequencing;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;
import org.eclipse.persistence.sequencing.TableSequence;

/**
 * A wrapper class to a table generator metadata.
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
public class TableGeneratorMetadata extends TableMetadata {    
    private Integer m_allocationSize;
    private Integer m_initialValue;
    
    private String m_generatorName;
    private String m_pkColumnValue;
    private String m_pkColumnName;
    private String m_valueColumnName;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected TableGeneratorMetadata() {
        super("<table-generator>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public TableGeneratorMetadata(MetadataAnnotation tableGenerator, MetadataAccessor accessor) {
        super(tableGenerator, accessor);
        
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
     * INTERNAL
     * This constructor is used to create a default table generator.
     * @see MetadataProject processSequencingAccesssors.
     */
    public TableGeneratorMetadata(String pkColumnValue) {
        m_pkColumnValue = pkColumnValue;
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
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
     */
    public TableSequence process(MetadataLogger logger) {
        TableSequence sequence = new TableSequence();

        // Process the sequence name.
        if (m_pkColumnValue == null || m_pkColumnValue.equals("")) {
            logger.logConfigMessage(logger.TABLE_GENERATOR_PK_COLUMN_VALUE, m_generatorName, getAccessibleObject(), getLocation());
            sequence.setName(m_generatorName);
        } else {
            sequence.setName(m_pkColumnValue);
        }
        
        // Process the allocation size
        sequence.setPreallocationSize(m_allocationSize == null ? Integer.valueOf(50) : m_allocationSize);
        
        // Process the initial value
        sequence.setInitialValue(m_initialValue == null ? Integer.valueOf(0) :  m_initialValue);
        
        // Get the database table from the table generator.
        sequence.setTable(getDatabaseTable());
        
        // If the table has a qualifer, make sure to set it on the sequence.
        //sequence.setQualifier(getDatabaseTable().getTableQualifier());
        
        // Process the pk column name
        if (m_pkColumnName != null && ! m_pkColumnName.equals("")) {
            sequence.setNameFieldName(m_pkColumnName);
        }
            
        // Process the pk volumn column name
        if (m_valueColumnName != null && ! m_valueColumnName.equals("")) {
            sequence.setCounterFieldName(m_valueColumnName);
        }
        
        return sequence;
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
    
    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return "TableGenerator[" + m_generatorName + "]";
    }
}
