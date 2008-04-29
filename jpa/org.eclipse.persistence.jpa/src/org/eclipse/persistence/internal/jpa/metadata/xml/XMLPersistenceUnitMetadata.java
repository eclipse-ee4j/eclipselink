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
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

/**
 * INTERNAL:
 * Object to hold onto the XML persistence unit metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLPersistenceUnitMetadata {
	private boolean m_xmlMappingMetadataComplete;
	private String m_conflict;
	private XMLPersistenceUnitDefaults m_persistenceUnitDefaults;
	
	/**
	 * INTERNAL:
	 */
	public XMLPersistenceUnitMetadata() {}

	/**
     * INTERNAL:
     * If equals returns false, call getConflict() for a finer grain reason why.
     */
    public boolean equals(Object objectToCompare) {
    	if (objectToCompare instanceof XMLPersistenceUnitMetadata) {
    		XMLPersistenceUnitMetadata persistenceUnitMetadata = (XMLPersistenceUnitMetadata) objectToCompare;
            
    		// Check xmlMappingMetadataComplete.
    		if (persistenceUnitMetadata.isXMLMappingMetadataComplete() != isXMLMappingMetadataComplete()) {
    			m_conflict = "xml-mapping-metadata-complete";
    			return false;
    		} 

    		// Check the persistence unit defaults.
    		XMLPersistenceUnitDefaults persistenceUnitDefaults = persistenceUnitMetadata.getPersistenceUnitDefaults();
    		
    		if (m_persistenceUnitDefaults != null && persistenceUnitDefaults != null) {
        		if (! m_persistenceUnitDefaults.equals(persistenceUnitDefaults)) {
        			m_conflict = m_persistenceUnitDefaults.getConflict();
        			return false;
        		}
    		} else {
    			m_conflict = "persistence-unit-defaults";
    			return false;
    		}
        
    		m_conflict = "";
    		return true;
    	}
    
    	m_conflict = "Object not an instance of XMLPersistenceUnitMetadata";
    	return false;
    }
    
    /**
	 * INTERNAL:
	 */
	public String getCatalog() {
		return (m_persistenceUnitDefaults == null) ? "" : m_persistenceUnitDefaults.getCatalog();
	}
    
    /**
     * INTERNAL:
     * Calling this method after an equals call that returns false will give
     * you the conflicting meta data.
     */
    public String getConflict() {
       return m_conflict;
    }
    
	/**
	 * INTERNAL:
	 */
	public List<EntityListenerMetadata> getDefaultListeners() {
		return (m_persistenceUnitDefaults == null) ? new ArrayList<EntityListenerMetadata>(): m_persistenceUnitDefaults.getEntityListeners(); 
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public XMLPersistenceUnitDefaults getPersistenceUnitDefaults() {
		return m_persistenceUnitDefaults;
	}
	
	/**
	 * INTERNAL:
	 */
	public String getSchema() {
		return (m_persistenceUnitDefaults == null) ? "" : m_persistenceUnitDefaults.getSchema();
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getXMLMappingMetadataComplete() {
		return null;
	}

	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public boolean isXMLMappingMetadataComplete() {
		return m_xmlMappingMetadataComplete;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setPersistenceUnitDefaults(XMLPersistenceUnitDefaults persistenceUnitDefaults) {
		m_persistenceUnitDefaults = persistenceUnitDefaults;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setXMLMappingMetadataComplete(String ignore) {
		m_xmlMappingMetadataComplete = true;
	}	
}
