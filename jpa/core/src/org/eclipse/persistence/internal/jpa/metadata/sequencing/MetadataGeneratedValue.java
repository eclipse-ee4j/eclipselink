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

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * Metadata object to hold generated value information.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataGeneratedValue {
    private GeneratedValue m_generatedValue;
    public static final String DEFAULT_STRATEGY = GenerationType.AUTO.name();
    
    /**
     * INTERNAL:
     */
    public MetadataGeneratedValue() {}
    
    /**
     * INTERNAL:
     */
    public MetadataGeneratedValue(GeneratedValue generatedValue) {
        m_generatedValue = generatedValue;
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof MetadataGeneratedValue) {
            MetadataGeneratedValue generatedValue = (MetadataGeneratedValue) objectToCompare;
            if (!generatedValue.getStrategy().equals(getStrategy())) {
                return false;
            }
            
            return generatedValue.getGenerator().equals(getGenerator());
        }
        
        return false;
    }
    
    /**
     * INTERNAL: (Overridden in XMLGeneratedValue)
     */
    public String getStrategy() {
        return m_generatedValue.strategy().name();
    }
    
    /**
     * INTERNAL: (Overridden in XMLGeneratedValue)
     */
    public String getGenerator() {
        return m_generatedValue.generator();
    }
}
