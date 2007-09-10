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
public class MetadataSequenceGenerator extends MetadataGenerator {
    private SequenceGenerator m_sequenceGenerator;
    
    /**
     * INTERNAL:
     */
    protected MetadataSequenceGenerator(String entityClassName) {
        super(entityClassName);
    }
    
    /**
     * INTERNAL:
     */
    public MetadataSequenceGenerator(SequenceGenerator sequenceGenerator, String entityClassName) {
        super(entityClassName);
        m_sequenceGenerator = sequenceGenerator;    
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof MetadataSequenceGenerator) {
            MetadataSequenceGenerator generator = (MetadataSequenceGenerator) objectToCompare;
            
            if (!generator.getName().equals(getName())) { 
                return false;
            }
            
            if (generator.getInitialValue() != getInitialValue()) {
                return false;
            }
            
            if (generator.getAllocationSize() != getAllocationSize()) {
                return false;
            }
            
            return generator.getSequenceName().equals(getSequenceName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public int getAllocationSize() {
        return m_sequenceGenerator.allocationSize();
    }
    
    /**
     * INTERNAL:
     */
    public int getInitialValue() {
        return m_sequenceGenerator.initialValue();
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_sequenceGenerator.name();
    }
    
    /**
     * INTERNAL:
     */
    public String getSequenceName() {
        return m_sequenceGenerator.sequenceName();
    }
}
