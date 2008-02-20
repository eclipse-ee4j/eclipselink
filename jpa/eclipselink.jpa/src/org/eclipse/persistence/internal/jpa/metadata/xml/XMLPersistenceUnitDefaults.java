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

import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

/**
 * Object to hold onto the XML persistence unit defaults.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLPersistenceUnitDefaults {
	private List<EntityListenerMetadata> m_entityListeners;
	private boolean m_cascadePersist;
	private String m_access;
	private String m_catalog;
	private String m_conflict;
	private String m_schema;

	/**
	 * INTERNAL:
	 */
	public XMLPersistenceUnitDefaults() {}
	
	/**
     * INTERNAL:
     * If equals returns false, call getConflict() for a finer grain reason why.
     */
    public boolean equals(Object objectToCompare) {
    	if (objectToCompare instanceof XMLPersistenceUnitDefaults) {
    		XMLPersistenceUnitDefaults persistenceUnitDefaults = (XMLPersistenceUnitDefaults) objectToCompare; 
    		
    		// Check the access.
    		if (! MetadataHelper.valuesMatch(persistenceUnitDefaults.getAccess(), getAccess())) {
    			m_conflict = "access";
    			return false;
    		}

    		// Check the catalog.
    		if (! MetadataHelper.valuesMatch(persistenceUnitDefaults.getCatalog(), getCatalog())) {
    			m_conflict = "catalog";
    			return false;
    		}
                
    		// Check the schema.
    		if (! MetadataHelper.valuesMatch(persistenceUnitDefaults.getSchema(), getSchema())) {
    			m_conflict = "schema";
    			return false;
    		}
                
    		// Check the cascade persist.
    		if (! MetadataHelper.valuesMatch(persistenceUnitDefaults.isCascadePersist(), isCascadePersist())) {
    			m_conflict = "cascade-persist";
    			return false;
    		} 
        
    		m_conflict = "";
    		return true;
    	}
    	
    	m_conflict = "Object not an instance of XMLPersistenceUnitDefaults";
    	return false;
    }
    
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getAccess() {
		return m_access;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getCascadePersist() {
		return null;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getCatalog() {
		return m_catalog;
	}
	
	/**
	 * INTERNAL:
	 */
	public String getConflict() {
		return m_conflict;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public List<EntityListenerMetadata> getEntityListeners() {
		return m_entityListeners;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getSchema() {
		return m_schema;
	}
	
	/**
	 * INTERNAL:
	 */
	public boolean hasEntityListeners() {
		return m_entityListeners != null && ! m_entityListeners.isEmpty();
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public boolean isCascadePersist() {
		return m_cascadePersist;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setAccess(String access) {
		m_access = access;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setCatalog(String catalog) {
		m_catalog = catalog;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setEntityListeners(List<EntityListenerMetadata> entityListeners) {
		m_entityListeners = entityListeners;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setCascadePersist(String ignore) {
		m_cascadePersist = true;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setSchema(String schema) {
		m_schema = schema;
	}
}
