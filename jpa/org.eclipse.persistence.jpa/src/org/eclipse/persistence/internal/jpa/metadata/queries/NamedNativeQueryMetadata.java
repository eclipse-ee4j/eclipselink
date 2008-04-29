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
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;

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
    public NamedNativeQueryMetadata(Annotation namedNativeQuery, String javaClassName) {
    	setLoadedFromAnnotation();
        setLocation(javaClassName);
        
        setName((String) invokeMethod("name", namedNativeQuery));
        setQuery((String) invokeMethod("query", namedNativeQuery));
        setHints((Annotation[]) invokeMethod("hints", namedNativeQuery));
        
        m_resultClass = (Class) invokeMethod("resultClass", namedNativeQuery); 
        m_resultSetMapping = (String) invokeMethod("resultSetMapping", namedNativeQuery);
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
    	if (objectToCompare instanceof NamedNativeQueryMetadata) {
    		NamedNativeQueryMetadata namedNativeQuery = (NamedNativeQueryMetadata) objectToCompare;
    		
    		if (! org.eclipse.persistence.internal.jpa.metadata.MetadataHelper.valuesMatch(getName(), namedNativeQuery.getName())) {
    			return false;
    		}
    		
    		if (! org.eclipse.persistence.internal.jpa.metadata.MetadataHelper.valuesMatch(getQuery(), namedNativeQuery.getQuery())) {
    			return false;
    		}
    		
    		if (! org.eclipse.persistence.internal.jpa.metadata.MetadataHelper.valuesMatch(getResultClass(), namedNativeQuery.getResultClass())) {
    			return false;
    		}
    		
    		if (! org.eclipse.persistence.internal.jpa.metadata.MetadataHelper.valuesMatch(getResultSetMapping(), namedNativeQuery.getResultSetMapping())) {
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
    public void process(AbstractSession session, ClassLoader loader) {
        HashMap<String, String> hints = processQueryHints(session);

        if (m_resultClass == void.class) {
            if (m_resultSetMapping.equals("")) {
                // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                session.addQuery(getName(), EJBQueryImpl.buildSQLDatabaseQuery(getQuery(), hints));
            } else {
                session.addQuery(getName(), EJBQueryImpl.buildSQLDatabaseQuery(m_resultSetMapping, getQuery(), hints));
            }
        } else { 
            session.addQuery(getName(), EJBQueryImpl.buildSQLDatabaseQuery(MetadataHelper.getClassForName(m_resultClass.getName(), loader), getQuery(), hints));
        }
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
}
