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

import javax.persistence.NamedQuery;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;

/**
 * INTERNAL:
 * Object to hold onto a named query metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class NamedQueryMetadata extends QueryMetadata {
    /**
     * INTERNAL:
     */
    public NamedQueryMetadata() {
    	setLoadedFromXML();
    }

    /**
     * INTERNAL:
     */
    public NamedQueryMetadata(NamedQuery namedQuery, String javaClassName) {
    	setLoadedFromAnnotation();
        setLocation(javaClassName);
        setName(namedQuery.name());
        setQuery(namedQuery.query());
        setHints(namedQuery.hints());     
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
    	if (objectToCompare instanceof NamedQueryMetadata) {
    		NamedQueryMetadata namedQuery = (NamedQueryMetadata) objectToCompare;
    		
    		if (! MetadataHelper.valuesMatch(getName(), namedQuery.getName())) {
    			return false;
    		}
    		
    		if (! MetadataHelper.valuesMatch(getQuery(), namedQuery.getQuery())) {
    			return false;
    		}
    		
    		if (getHints().size() != namedQuery.getHints().size()) {
    			return false;
        	} else {
    			for (QueryHintMetadata hint : getHints()) {
        			if (! namedQuery.hasHint(hint)) {
        				return false;
        			}
        		}
    		}
    		
    		return true;
    	}
    	
    	return false;
    }
}
