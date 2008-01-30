/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.QueryHint;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;

/**
 * INTERNAL:
 * Object to hold onto a named query metadata's hints.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class QueryMetadata  {
	private boolean m_loadedFromXML;
	private List<QueryHintMetadata> m_hints;
	private String m_location;
	private String m_name;
    private String m_query;
    
    /**
     * INTERNAL:
     */
    public QueryMetadata() {}
    
    /**
     * INTERNAL:
     */
    public List<QueryHintMetadata> getHints() {
        return m_hints; 
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
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     */
    public String getQuery() {
        return m_query;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean hasHint(QueryHintMetadata hint) {
    	for (QueryHintMetadata myHint : getHints()) {
    		if (MetadataHelper.valuesMatch(myHint.getName(), hint.getName()) && MetadataHelper.valuesMatch(myHint.getValue(), hint.getValue())) {
    			// Once we find it return true, otherwise keep looking.
    			return true;
    		}
    	}

    	return false;
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromAnnotation() {
        return !loadedFromXML();
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return m_loadedFromXML;
    }  
    
    /**
     * INTERNAL:
     */
    public void setHints(List<QueryHintMetadata> hints) {
        m_hints = hints;
    }
    
    /**
     * INTERNAL:
     */
    protected void setHints(QueryHint[] hints) {
    	if (hints.length > 0) {
    		m_hints = new ArrayList<QueryHintMetadata>();

    		for (QueryHint hint : hints) {
    			m_hints.add(new QueryHintMetadata(hint.name(), hint.value()));
    		}
    	}
    }
    
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
    public void setLocation(String location) {
        m_location = location;
    }
    
    /**
     * INTERNAL:
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * INTERNAL:
     */
    public void setQuery(String query) {
        m_query = query;
    }
}
