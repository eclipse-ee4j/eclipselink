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

/**
 * Common metadata object for the sequence and table generators. Holds onto
 * the location we found them. Either a class name or a xml document name.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataGenerator {
    private String m_location;

    /**
     * INTERNAL:
     */
    protected MetadataGenerator(String location) {
        m_location = location;
    }
    
    /**
     * INTERNAL:
     */
    public String getLocation() {
        return m_location;
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromAnnotations() {
       return true; 
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
       return false; 
    }
}
