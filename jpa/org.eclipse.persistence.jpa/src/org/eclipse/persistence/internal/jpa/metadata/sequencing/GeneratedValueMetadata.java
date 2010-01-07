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

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Metadata object to hold generated value information.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class GeneratedValueMetadata {
    private String m_strategy;
    private String m_generator;
    
    /**
     * INTERNAL:
     */
    public GeneratedValueMetadata() {}
    
    /**
     * INTERNAL:
     */
    public GeneratedValueMetadata(MetadataAnnotation generatedValue) {
        m_generator = (String) generatedValue.getAttributeString("generator");
        m_strategy = (String) generatedValue.getAttribute("strategy"); 
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof GeneratedValueMetadata) {
            GeneratedValueMetadata generatedValue = (GeneratedValueMetadata) objectToCompare;
            
            if (m_generator == null && generatedValue.getGenerator() != null) {
                return false;
            } else if (! m_generator.equals(generatedValue.getGenerator())) {
                return false;
            }
                
            if (m_strategy == null && generatedValue.getStrategy() != null) {
                return false;
            } else if (! m_strategy.equals(generatedValue.getStrategy())) {
                return false;
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getGenerator() {
        return m_generator;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getStrategy() {
        return m_strategy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setGenerator(String generator) {
        m_generator = generator;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setStrategy(String strategy) {
        m_strategy = strategy;
    }
}
