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

/**
 * Object to hold onto a named query metadata's hints.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class MetadataQuery  {
    private String m_name;
    private String m_query;
    private Object m_location; // Where it was found, i.e. java class or xml document.
    private List<MetadataQueryHint> m_hints;
    
    /**
     * INTERNAL:
     */
    protected MetadataQuery() {
        m_hints = new ArrayList<MetadataQueryHint>();
    }
    
    /**
     * INTERNAL:
     */
    protected void addHint(MetadataQueryHint hint) {
        m_hints.add(hint);
    }
    
    /**
     * INTERNAL:
     */
    public String getEJBQLString() {
        return m_query;
    }
    
    /**
     * INTERNAL:
     */
    public List<MetadataQueryHint> getHints() {
        return m_hints; 
    }
    
    /**
     * INTERNAL:
     */
    public abstract String getIgnoreLogMessageContext();
    
    /**
     * INTERNAL:
     */
    public Object getLocation() {
        return m_location;
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL: (Overriden in XMLNamedNativeQuery and XMLNamedQuery)
     */
    public boolean loadedFromAnnotations() {
        return true;
    }
    
    /**
     * INTERNAL: (Overriden in XMLNamedNativeQuery and XMLNamedQuery)
     */
    public boolean loadedFromXML() {
        return false;
    }  
    
    /**
     * INTERNAL:
     */
    protected void setEJBQLString(String query) {
        m_query = query;
    }
    
    /**
     * INTERNAL:
     */
    protected void setLocation(Object location) {
        m_location = location;
    }
    
    /**
     * INTERNAL:
     */
    protected void setName(String name) {
        m_name = name;
    }
}
