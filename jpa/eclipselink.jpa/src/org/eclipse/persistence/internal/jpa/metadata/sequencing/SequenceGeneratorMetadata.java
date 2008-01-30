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

import javax.persistence.SequenceGenerator;

/**
 * A wrapper class to the MetadataSequenceGenerator that holds onto a 
 * @SequenceGenerator for its metadata values.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class SequenceGeneratorMetadata {
    private boolean m_loadedFromXML;
    
	private Integer m_allocationSize;
	private Integer m_initialValue;
	
	private String m_location;
	private String m_name;
	private String m_sequenceName;
	
    /**
     * INTERNAL:
     */
    public SequenceGeneratorMetadata() {
    	m_loadedFromXML = true;
    }
    
    /**
     * INTERNAL:
     */
    public SequenceGeneratorMetadata(SequenceGenerator sequenceGenerator, String entityClassName) {
        m_loadedFromXML = false;
        m_location = entityClassName;
        
        m_allocationSize = sequenceGenerator.allocationSize();
        m_initialValue = sequenceGenerator.initialValue();
        m_name = sequenceGenerator.name();
        m_sequenceName = sequenceGenerator.sequenceName();            
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof SequenceGeneratorMetadata) {
        	SequenceGeneratorMetadata generator = (SequenceGeneratorMetadata) objectToCompare;
            
            if (! generator.getName().equals(getName())) { 
                return false;
            }
            
            if (! generator.getInitialValue().equals(getInitialValue())) {
                return false;
            }
            
            if (! generator.getAllocationSize().equals(getAllocationSize())) {
                return false;
            }
            
            return generator.getSequenceName().equals(getSequenceName());
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
    public Integer getInitialValue() {
        return m_initialValue;
    }
    
    /**
     * INTERNAL:
     */
    public String getLocation() {
        return m_location;
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
    public String getSequenceName() {
        return m_sequenceName;
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
    public void setInitialValue(Integer initialValue) {
    	m_initialValue = initialValue;
    }
    
    /**
     * INTERNAL:
     */
    public void setLocation(String location) {
    	m_location = location;
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
    public void setSequenceName(String sequenceName) {
    	m_sequenceName = sequenceName;
    }
}
