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

import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;

/**
 * Metadata object to hold generated value information.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class GeneratedValueMetadata {
	private GenerationType m_strategy;
	private String m_generator;
    
    /**
     * INTERNAL:
     */
    public GeneratedValueMetadata() {}
    
    /**
     * INTERNAL:
     */
    public GeneratedValueMetadata(GeneratedValue generatedValue) {
    	m_generator = generatedValue.generator();
    	m_strategy = generatedValue.strategy();
    }
    
    /**
     * INTERNAL:
     */
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
    public GenerationType getStrategy() {
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
    public void setStrategy(GenerationType strategy) {
    	m_strategy = strategy;
    }
}
