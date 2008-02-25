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

/**
 * Metadata object to hold generated value information.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class GeneratedValueMetadata {
	private Enum m_strategy;
	private String m_generator;
    
    /**
     * INTERNAL:
     */
    public GeneratedValueMetadata() {}
    
    /**
     * INTERNAL:
     */
    public GeneratedValueMetadata(Object generatedValue) {
        m_generator = (String)MetadataHelper.invokeMethod("generator", generatedValue, (Object[])null);
    	m_strategy = (Enum)MetadataHelper.invokeMethod("strategy", generatedValue, (Object[])null); 
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
    public Enum getStrategy() {
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
    public void setStrategy(Enum strategy) {
    	m_strategy = strategy;
    }
}
