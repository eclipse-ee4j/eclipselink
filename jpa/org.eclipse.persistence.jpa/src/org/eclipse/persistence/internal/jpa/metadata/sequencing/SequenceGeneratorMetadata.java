/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 *     07/23/2010-2.2 Guy Pelletier 
 *       - 237902: DDL GEN doesn't qualify SEQUENCE table with persistence unit schema
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.sequencing;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.sequencing.NativeSequence;

/**
 * A wrapper class to the MetadataSequenceGenerator that holds onto a 
 * @SequenceGenerator for its metadata values.
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
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class SequenceGeneratorMetadata extends ORMetadata {
    private boolean m_useIdentityIfPlatformSupports = false;
    
    private Integer m_allocationSize;
    private Integer m_initialValue;
    
    private String m_name;
    private String m_schema;
    private String m_catalog;
    private String m_sequenceName;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public SequenceGeneratorMetadata() {
        super("<sequence-generator>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public SequenceGeneratorMetadata(MetadataAnnotation sequenceGenerator, MetadataAccessor accessor) {
        super(sequenceGenerator, accessor);
        
        m_allocationSize = (Integer) sequenceGenerator.getAttribute("allocationSize");
        m_initialValue = (Integer) sequenceGenerator.getAttribute("initialValue"); 
        m_name = (String) sequenceGenerator.getAttributeString("name"); 
        m_schema = (String) sequenceGenerator.getAttributeString("schema"); 
        m_catalog = (String) sequenceGenerator.getAttributeString("catalog");
        m_sequenceName = (String) sequenceGenerator.getAttributeString("sequenceName"); 
    }
    
    /**
     * INTERNAL
     * This constructor is used to create a default sequence generator.
     * @see MetadataProject processSequencingAccesssors.
     */
    public SequenceGeneratorMetadata(String sequenceName, Integer allocationSize, String catalog, String schema, boolean useIdentityIfPlatformSupports) {
        m_sequenceName = sequenceName;
        m_allocationSize = allocationSize;
        m_useIdentityIfPlatformSupports = useIdentityIfPlatformSupports;
        
        setSchema(schema);
        setCatalog(catalog);
    }
    
    /**
     * INTERNAL
     * This constructor is used to create a default sequence generator.
     * @see MetadataProject processSequencingAccesssors.
     */
    public SequenceGeneratorMetadata(String sequenceName, String catalog, String schema) {
        m_sequenceName = sequenceName;
        
        setSchema(schema);
        setCatalog(catalog);
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
            
            if (! valuesMatch(m_schema, generator.getSchema())) {
                return false;
            }
            
            if (! valuesMatch(m_catalog, generator.getCatalog())) {
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
        
        // We must set our sequence name now since we have dependencies on it
        // before process.
        if (m_sequenceName == null) {
            m_sequenceName = "";
        }
    }
    
    /**
     * INTERNAL:
     */
    public NativeSequence process(MetadataLogger logger) {
        NativeSequence sequence = new NativeSequence();
        
        // Process the sequence name.
        if (m_sequenceName.equals("")) {
            logger.logConfigMessage(logger.SEQUENCE_GENERATOR_SEQUENCE_NAME, m_name, getAccessibleObject(), getLocation());
            sequence.setName(m_name);
        } else {
            sequence.setName(m_sequenceName);
        }
        
        // Set the should use identity flag.
        sequence.setShouldUseIdentityIfPlatformSupports(m_useIdentityIfPlatformSupports);
        
        // Process the allocation size
        sequence.setPreallocationSize(m_allocationSize == null ? Integer.valueOf(50) : m_allocationSize);
        
        // Process the initial value
        sequence.setInitialValue(m_initialValue == null ? Integer.valueOf(1) :  m_initialValue);
        
        // Process the schema and catalog qualifier
        sequence.setQualifier(processQualifier());
        
        return sequence;
    }
    
    /**
     * INTERNAL:
     * Used for processing.
     */
    public String processQualifier() {
        String qualifier = "";
        
        if (m_schema != null && ! m_schema.equals("")) {
            qualifier = m_schema;
        }
    
        if (m_catalog != null && ! m_catalog.equals("")) {
            // We didn't append a schema, so don't add a dot.
            if (qualifier.equals("")) {
                qualifier = m_catalog;
            } else {
                qualifier = m_catalog + "." + qualifier;
            }
        }
        
        return qualifier;
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
    
    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return "SequenceGenerator[" + m_name + "]";
    }
}
