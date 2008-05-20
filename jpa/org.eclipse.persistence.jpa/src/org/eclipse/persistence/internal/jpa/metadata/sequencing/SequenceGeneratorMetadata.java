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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.sequencing;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

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
    public SequenceGeneratorMetadata(Annotation sequenceGenerator, MetadataAccessibleObject accessibleObject) {
        super(sequenceGenerator, accessibleObject);
        
        m_allocationSize = (Integer) MetadataHelper.invokeMethod("allocationSize", sequenceGenerator);
        m_initialValue = (Integer) MetadataHelper.invokeMethod("initialValue", sequenceGenerator); 
        m_name = (String) MetadataHelper.invokeMethod("name", sequenceGenerator); 
        m_sequenceName = (String) MetadataHelper.invokeMethod("sequenceName", sequenceGenerator); 
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
    public String getSequenceName() {
        return m_sequenceName;
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
