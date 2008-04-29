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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.StoredProcedureCall;

/**
 * INTERNAL:
 * Object to hold onto a named stored procedure query.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class NamedStoredProcedureQueryMetadata extends NamedNativeQueryMetadata  {
	private Boolean m_returnsResultSet;
	private List<StoredProcedureParameterMetadata> m_procedureParameters;
	private String m_procedureName;
	
    /**
     * INTERNAL:
     */
    public NamedStoredProcedureQueryMetadata() {
    	setLoadedFromXML();
    }
    
    /**
     * INTERNAL:
     */
    public NamedStoredProcedureQueryMetadata(Annotation namedStoredProcedureQuery, String javaClassName) {
    	setLoadedFromAnnotation();
    	setLocation(javaClassName);
    	
        setName((String) invokeMethod("name", namedStoredProcedureQuery));
        setHints((Annotation[]) invokeMethod("hints", namedStoredProcedureQuery));
        setResultClass((Class) invokeMethod("resultClass", namedStoredProcedureQuery));
        setResultSetMapping((String) invokeMethod("resultSetMapping", namedStoredProcedureQuery));
        setProcedureParameters((Annotation[]) invokeMethod("procedureParameters", namedStoredProcedureQuery));
        
        m_procedureName = (String) invokeMethod("procedureName", namedStoredProcedureQuery);
        m_returnsResultSet = (Boolean) invokeMethod("returnsResultSet", namedStoredProcedureQuery);
    }
   
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getProcedureName() {
        return m_procedureName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<StoredProcedureParameterMetadata> getProcedureParameters() {
        return m_procedureParameters;
    }
    
    /**
     * INTERNAL:
     */
    public Boolean getReturnsResultSet() {
        return m_returnsResultSet;
    }
    
    /**
     * INTERNAL:
     */
    public void process(AbstractSession session, ClassLoader loader) {
        // Build the stored procedure call.
        StoredProcedureCall call = new StoredProcedureCall();
        
        // Process the stored procedure parameters.
        List<String> queryArguments = new ArrayList<String>();
        for (StoredProcedureParameterMetadata parameter : m_procedureParameters) {
            queryArguments.addAll(parameter.process(call));
        }
        
        // Process the procedure name.
        call.setProcedureName(m_procedureName);
        
        // Process the returns result set.
        call.setReturnsResultSet((m_returnsResultSet == null) ? false : m_returnsResultSet);
        
        // Process the query hints.
        HashMap<String, String> hints = processQueryHints(session);
        
        // Process the result class.
        if (getResultClass() == void.class) {
            if (getResultSetMapping().equals("")) {
                // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
                session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(call, queryArguments, hints));
            } else {
                session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(getResultSetMapping(), call, queryArguments, hints));
            }
        } else {
            session.addQuery(getName(), EJBQueryImpl.buildStoredProcedureQuery(MetadataHelper.getClassForName(getResultClass().getName(), loader), call, queryArguments, hints));
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setProcedureName(String procedureName) {
        m_procedureName = procedureName;
    }
    
    /**
     * INTERNAL:
     */
    public void setProcedureParameters(Annotation[] procedureParameters) {
        m_procedureParameters = new ArrayList<StoredProcedureParameterMetadata>();
        
        for (Annotation storedProcedureParameter : procedureParameters) {
           m_procedureParameters.add(new StoredProcedureParameterMetadata(storedProcedureParameter));
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setProcedureParameters(List<StoredProcedureParameterMetadata> procedureParameters) {
        m_procedureParameters = procedureParameters;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReturnsResultSet(Boolean returnsResultSet) {
        m_returnsResultSet = returnsResultSet;
    }
}
