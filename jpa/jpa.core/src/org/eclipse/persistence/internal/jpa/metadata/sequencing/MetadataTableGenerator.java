/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.sequencing;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

/**
 * A wrapper class to the MetadataTableGenerator that holds onto a 
 * @TableGenerator for its metadata values.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataTableGenerator extends MetadataGenerator {
    protected TableGenerator m_tableGenerator;
    protected List<String[]> m_uniqueConstraints;
    
    /**
     * INTERNAL:
     */
    protected MetadataTableGenerator(String entityClassName) {
        super(entityClassName);
    }
    
    /**
     * INTERNAL:
     */
    public MetadataTableGenerator(TableGenerator tableGenerator, String entityClassName) {
        super(entityClassName);
        m_tableGenerator = tableGenerator;
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof MetadataTableGenerator) {
            MetadataTableGenerator generator = (MetadataTableGenerator) objectToCompare;
            
            if (!generator.getName().equals(getName())) { 
                return false;
            }
            
            if (generator.getInitialValue() != getInitialValue()) {
                return false;
            }
            
            if (generator.getAllocationSize() != getAllocationSize()) {
                return false;
            }
            
            if (!generator.getPkColumnName().equals(getPkColumnName())) {
                return false;
            }
            
            if (!generator.getValueColumnName().equals(getValueColumnName())) {
                return false;
            }
            
            if (!generator.getPkColumnValue().equals(getPkColumnValue())) {
                return false;
            }
            
            if (!generator.getTable().equals(getTable())) {
                return false;
            }
                
            if (!generator.getSchema().equals(getSchema())) {
                return false;
            }
                
            return generator.getCatalog().equals(getCatalog());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public int getAllocationSize() {
        return m_tableGenerator.allocationSize();
    }
    
    /**
     * INTERNAL:
     * WIP - need to take into consideration the global catalog from XML.
     */
    public String getCatalog() {
        return m_tableGenerator.catalog();
    }
    
    /**
     * INTERNAL:
     */
    public int getInitialValue() {
        return m_tableGenerator.initialValue();
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_tableGenerator.name();
    }
    
    /**
     * INTERNAL:
     */
    public String getPkColumnName() {
        return m_tableGenerator.pkColumnName();
    }
    
    /**
     * INTERNAL:
     */
    public String getPkColumnValue() {
        return m_tableGenerator.pkColumnValue();
    }
    
    /**
     * INTERNAL:
     * WIP - need to take into consideration the global schema from XML.
     */
    public String getSchema() {
        return m_tableGenerator.schema();
    }
    
    /**
     * INTERNAL:
     */
    public String getTable() {
        return m_tableGenerator.table();
    }
 
    /**
     * INTERNAL:
     */
    public List<String[]> getUniqueConstraints() {
        if (m_uniqueConstraints == null) {
            m_uniqueConstraints = new ArrayList<String[]>();
            
            for (UniqueConstraint uniqueConstraint : m_tableGenerator.uniqueConstraints()) {
                m_uniqueConstraints.add(uniqueConstraint.columnNames());
            }
        }
        
        return m_uniqueConstraints;
    }
    
    /**
     * INTERNAL:
     */
    public String getValueColumnName() {
        return m_tableGenerator.valueColumnName();
    }
}
