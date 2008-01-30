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
	private String m_name;
	
    /**
     * INTERNAL:
     */
    public String getName() {
    	return m_name;
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
    
    /**
     * INTERNAL:
	 * Every converter needs to be able to process themselves.
     */
    public abstract void process(DatabaseMapping mapping, DirectAccessor accessor);
    
    /**
     * INTERNAL:
     */
    public void setName(String name) {
    	m_name = name;
    }
}
