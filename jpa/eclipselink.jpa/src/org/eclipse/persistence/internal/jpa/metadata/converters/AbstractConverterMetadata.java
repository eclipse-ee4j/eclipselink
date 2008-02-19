/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.lang.reflect.AnnotatedElement;

import org.eclipse.persistence.mappings.DatabaseMapping;

import org.eclipse.persistence.internal.jpa.metadata.accessors.DirectAccessor;

/**
 * INTERNAL:
 * Abstract metadata converter.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public abstract class AbstractConverterMetadata  {
	private boolean m_loadedFromXML;
	
	private String m_location;
	private String m_name;
	
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
     */
    public boolean loadedFromAnnotation() {
        return !m_loadedFromXML;
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return m_loadedFromXML;
    }  
    
    /**
     * INTERNAL:
	 * Every converter needs to be able to process themselves.
     */
    public abstract void process(DatabaseMapping mapping, DirectAccessor accessor);
    
    /**
     * INTERNAL:
     */
    public void setLoadedFromAnnotation() {
        m_loadedFromXML = false;
    } 
    
    /**
     * INTERNAL:
     */
    public void setLoadedFromXML() {
        m_loadedFromXML = true;
    } 
    
    /**
     * INTERNAL:
     */
    public void setLocation(AnnotatedElement annotatedElement) {
        m_location = annotatedElement.toString();
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
}
