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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.sequencing;

import javax.persistence.TableGenerator;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;

/**
 * A wrapper class to a table generator metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class TableGeneratorMetadata extends TableMetadata {	
    private boolean m_loadedFromXML;
    
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
		m_loadedFromXML = true;
	}
    
    /**
     * INTERNAL:
     */
    public TableGeneratorMetadata(Object tableGenerator, String entityClassName) {
    	super(entityClassName);
    	
    	m_loadedFromXML = false;
        
        m_allocationSize = (Integer)org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataHelper.invokeMethod("allocationSize", tableGenerator, (Object[])null);
        m_initialValue = (Integer)org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataHelper.invokeMethod("initialValue", tableGenerator, (Object[])null);
        m_generatorName = (String)org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataHelper.invokeMethod("name", tableGenerator, (Object[])null); 
        m_pkColumnName = (String)org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataHelper.invokeMethod("pkColumnName", tableGenerator, (Object[])null); 
        m_pkColumnValue = (String)org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataHelper.invokeMethod("pkColumnValue", tableGenerator, (Object[])null);
        m_valueColumnName = (String)org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataHelper.invokeMethod("valueColumnName", tableGenerator, (Object[])null);
        
        setCatalog((String)org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataHelper.invokeMethod("catalog", tableGenerator, (Object[])null));
        setSchema((String)org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataHelper.invokeMethod("schema", tableGenerator, (Object[])null));
        setName((String)org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataHelper.invokeMethod("table", tableGenerator, (Object[])null));
        setUniqueConstraints((Object[])org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataHelper.invokeMethod("uniqueConstraints", tableGenerator, (Object[])null));
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof TableGeneratorMetadata) {
        	TableGeneratorMetadata generator = (TableGeneratorMetadata) objectToCompare;
            
            if (! MetadataHelper.valuesMatch(generator.getGeneratorName(), getGeneratorName())) {
                return false;
            }
            
            if (! MetadataHelper.valuesMatch(generator.getName(), getName())) { 
                return false;
            }
            
            if (! MetadataHelper.valuesMatch(generator.getInitialValue(), getInitialValue())) {
                return false;
            }
            
            if (! MetadataHelper.valuesMatch(generator.getAllocationSize(), getAllocationSize())) {
                return false;
            }
            
            if (! MetadataHelper.valuesMatch(generator.getPkColumnName(), getPkColumnName())) {
                return false;
            }
            
            if (! MetadataHelper.valuesMatch(generator.getValueColumnName(), getValueColumnName())) {
                return false;
            }
            
            if (! MetadataHelper.valuesMatch(generator.getPkColumnValue(), getPkColumnValue())) {
                return false;
            }
            
            if (! MetadataHelper.valuesMatch(generator.getName(), getName())) { 
                return false;
            }
                
            if (! MetadataHelper.valuesMatch(generator.getSchema(), getSchema())) {
                return false;
            }
                
            return MetadataHelper.valuesMatch(generator.getCatalog(), getCatalog());
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
     * INTERNAL: (Override from MetadataTable)
     */
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
     * Used for OX mapping.
     */
    public Integer getInitialValue() {
        return m_initialValue;
    }
    
    /**
     * INTERNAL: (Override from MetadataTable)
     */
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
     * INTERNAL: (Override from MetadataTable)
     */
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
    public boolean loadedFromAnnotations() {
    	return ! loadedFromXML();
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
    	return m_loadedFromXML;
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
