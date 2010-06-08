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
package org.eclipse.persistence.internal.jpa.metadata.sequencing;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * A wrapper class to the MetadataSequenceGenerator that holds onto a 
 * @SequenceGenerator for its metadata values.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class SequenceGeneratorMetadata extends ORMetadata {
    private Integer m_allocationSize;
    private Integer m_initialValue;
    
    private String m_name;
    private String m_schema;
    private String m_catalog;
    private String m_sequenceName;
    
    /**
     * INTERNAL:
     */
    public SequenceGeneratorMetadata() {
        super("<sequence-generator>");
    }
    
    /**
     * INTERNAL:
     */
    public SequenceGeneratorMetadata(MetadataAnnotation sequenceGenerator, MetadataAccessibleObject accessibleObject) {
        super(sequenceGenerator, accessibleObject);
        
        m_allocationSize = (Integer) sequenceGenerator.getAttribute("allocationSize");
        m_initialValue = (Integer) sequenceGenerator.getAttribute("initialValue"); 
        m_name = (String) sequenceGenerator.getAttributeString("name"); 
        m_schema = (String) sequenceGenerator.getAttribute("schema"); 
        m_catalog = (String) sequenceGenerator.getAttribute("catalog");
        m_sequenceName = (String) sequenceGenerator.getAttributeString("sequenceName"); 
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof SequenceGeneratorMetadata) {
            SequenceGeneratorMetadata generator = (SequenceGeneratorMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, generator.getName())) { 
                return false;
            }
            
            if (! valuesMatch(m_initialValue, generator.getInitialValue())) {
                return false;
            }
            
            if (! valuesMatch(m_allocationSize, generator.getAllocationSize())) {
                return false;
            }
            
            return valuesMatch(m_sequenceName, generator.getSequenceName());
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
     * Used for OX mapping.
     */
    public String getCatalog() {
        return m_catalog;
    }
    
    /**
     * INTERNAL:
     * Used for processing.
     */
    public String getQualifier() {
        if(m_catalog.length() == 0) {
            if(m_schema.length() == 0) {
                return "";
            } else {
                return  m_schema;
            }
        } else {
            return m_catalog + '.' + m_schema;
        }
    }
    
    /**
     * INTERNAL:
     */
    public String getCatalogContext() {
        return MetadataLogger.SEQUENCE_GENERATOR_CATALOG;
    }
    
    /**
     * INTERNAL:
     * To satisfy the abstract getIdentifier() method from ORMetadata.
     */
    @Override
    public String getIdentifier() {
        return getName();
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
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getSchema() {
        return m_schema;
    }
    
    /**
     * INTERNAL:
     */
    public String getSchemaContext() {
        return MetadataLogger.SEQUENCE_GENERATOR_SCHEMA;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getSequenceName() {
        return m_sequenceName;
    }  
    
    /**
     * INTERNAL: 
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        if (m_sequenceName == null) {
            m_sequenceName = "";
        }
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
    public void setCatalog(String catalog) {
        m_catalog = catalog;
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
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSchema(String schema) {
        m_schema = schema;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSequenceName(String sequenceName) {
        m_sequenceName = sequenceName;
    }
}
