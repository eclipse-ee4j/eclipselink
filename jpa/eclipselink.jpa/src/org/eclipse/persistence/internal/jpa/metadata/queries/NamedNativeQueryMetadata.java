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

import javax.persistence.NamedNativeQuery;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;

/**
 * INTERNAL:
 * Object to hold onto named native query metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class NamedNativeQueryMetadata extends QueryMetadata {
    private Class m_resultClass;
    private String m_resultClassName;
    private String m_resultSetMapping;
    
    /**
     * INTERNAL:
     */
    public NamedNativeQueryMetadata() {
    	setLoadedFromXML();
    }
    
    /**
     * INTERNAL:
     */
    public NamedNativeQueryMetadata(NamedNativeQuery namedNativeQuery, String javaClassName) {
    	m_resultClass = namedNativeQuery.resultClass();
        m_resultSetMapping = namedNativeQuery.resultSetMapping();
        
    	setLoadedFromAnnotation();
        setLocation(javaClassName);
        setName(namedNativeQuery.name());
        setQuery(namedNativeQuery.query());
        setHints(namedNativeQuery.hints());
    }
    
    /**
     * INTERNAL:
     */
    public Class getResultClass() {
        return m_resultClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getResultClassName() {
        return m_resultClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getResultSetMapping() {
        return m_resultSetMapping;
    }
    
    /**
     * INTERNAL:
     */
    public void setResultClass(Class resultClass) {
        m_resultClass = resultClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setResultClassName(String resultClassName) {
        m_resultClassName = resultClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected void setResultSetMapping(String resultSetMapping) {
        m_resultSetMapping = resultSetMapping;
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
    	if (objectToCompare instanceof NamedNativeQueryMetadata) {
    		NamedNativeQueryMetadata namedNativeQuery = (NamedNativeQueryMetadata) objectToCompare;
    		
    		if (! MetadataHelper.valuesMatch(getName(), namedNativeQuery.getName())) {
    			return false;
    		}
    		
    		if (! MetadataHelper.valuesMatch(getQuery(), namedNativeQuery.getQuery())) {
    			return false;
    		}
    		
    		if (! MetadataHelper.valuesMatch(getResultClass(), namedNativeQuery.getResultClass())) {
    			return false;
    		}
    		
    		if (! MetadataHelper.valuesMatch(getResultSetMapping(), namedNativeQuery.getResultSetMapping())) {
    			return false;
    		}
    		
    		if (getHints().size() != namedNativeQuery.getHints().size()) {
    			return false;
        	} else {
    			for (QueryHintMetadata hint : getHints()) {
        			if (! namedNativeQuery.hasHint(hint)) {
        				return false;
        			}
    			}
        	}
    		
    		
    		return true;
    	}
    	
    	return false;
    }
}
