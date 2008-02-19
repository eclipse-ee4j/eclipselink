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

import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;

/**
 * INTERNAL:
 * Object to hold onto a named stored procedure query.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class NamedStoredProcedureQueryMetadata extends NamedNativeQueryMetadata  {
	private boolean m_returnsResultSet;
	private List<StoredProcedureParameterMetadata> m_procedureParameters;
	private String m_procedureName;
	
    /**
     * INTERNAL:
     */
    public NamedStoredProcedureQueryMetadata() {
    	this.setLoadedFromXML();
    	// Will need an XML context ignore string when XML support is added.
    }
    
    /**
     * INTERNAL:
     */
    public NamedStoredProcedureQueryMetadata(NamedStoredProcedureQuery namedStoredProcedureQuery, Class javaClass) {
    	m_procedureName = namedStoredProcedureQuery.procedureName();
    	
    	setLoadedFromAnnotation();
    	setLocation(javaClass.getName());
        setName(namedStoredProcedureQuery.name());
        setHints(namedStoredProcedureQuery.hints());
        setResultClass(namedStoredProcedureQuery.resultClass());
        setResultSetMapping(namedStoredProcedureQuery.resultSetMapping());
        setReturnsResultSet(namedStoredProcedureQuery.returnsResultSet());        
        setProcedureParameters(namedStoredProcedureQuery.procedureParameters());
    }
   
    /**
     * INTERNAL:
     */
    public String getProcedureName() {
        return m_procedureName;
    }
    
    /**
     * INTERNAL:
     */
    public List<StoredProcedureParameterMetadata> getProcedureParameters() {
        return m_procedureParameters;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasProcedureParameters() {
        return ! m_procedureParameters.isEmpty();
    }
    
    /**
     * INTERNAL:
     */
    public boolean returnsResultSet() {
        return m_returnsResultSet;
    }
    
    /**
     * INTERNAL:
     */
    public void setProcedureName(String procedureName) {
        m_procedureName = procedureName;
    }
    
    /**
     * INTERNAL:
     */
    public void setProcedureParameters(StoredProcedureParameter[] procedureParameters) {
    	m_procedureParameters = new ArrayList<StoredProcedureParameterMetadata>();
    		
   		for (StoredProcedureParameter storedProcedureParameter : procedureParameters) {
   			m_procedureParameters.add(new StoredProcedureParameterMetadata(storedProcedureParameter));
   		}
    }
    
    /**
     * INTERNAL:
     */
    public void setReturnsResultSet(boolean returnsResultSet) {
        m_returnsResultSet = returnsResultSet;
    }
}
