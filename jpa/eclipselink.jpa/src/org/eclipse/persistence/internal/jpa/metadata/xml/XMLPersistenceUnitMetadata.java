/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

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
	 * Default constructor.
	 */
	public XMLPersistenceUnitMetadata() {
		// We can't do this ... write out would create the tags ...
		setPersistenceUnitDefaults(new XMLPersistenceUnitDefaults());
	}

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

    		if (! m_persistenceUnitDefaults.equals(persistenceUnitDefaults)) {
    			m_conflict = m_persistenceUnitDefaults.getConflict();
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
		return m_persistenceUnitDefaults.getCatalog();
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
		return m_persistenceUnitDefaults.getEntityListeners();
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
		return m_persistenceUnitDefaults.getSchema();
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
	 */
	public boolean hasDefaultListeners() {
		return m_persistenceUnitDefaults.hasEntityListeners();
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
