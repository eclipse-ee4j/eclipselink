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

import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataQueryHint;

/**
 * Object to hold onto a named stored procedure query.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class MetadataNamedStoredProcedureQuery extends MetadataNamedNativeQuery  {
    private String m_procedureName;
    private boolean m_returnsResultSet;
    private List<MetadataStoredProcedureParameter> m_procedureParameters;
    
    /**
     * INTERNAL:
     */
    protected MetadataNamedStoredProcedureQuery() {
        m_procedureParameters = new ArrayList<MetadataStoredProcedureParameter>();
    }
    
    /**
     * INTERNAL:
     */
    public MetadataNamedStoredProcedureQuery(NamedStoredProcedureQuery namedStoredProcedureQuery, Class javaClass) {
        this();
        
        // Set the location where we found this query.
        setLocation(javaClass.getName());
        
        // Process the name.
        setName(namedStoredProcedureQuery.name());
        
        // Process the query hints.
        for (QueryHint hint : namedStoredProcedureQuery.hints()) {
            addHint(new MetadataQueryHint(hint.name(), hint.value()));
        }
        
        // Process the result class
        setResultClass(namedStoredProcedureQuery.resultClass());
        
        // Process the result set mapping.
        setResultSetMapping(namedStoredProcedureQuery.resultSetMapping());
        
        // Process the procedure name.
        setProcedureName(namedStoredProcedureQuery.procedureName());
        
        // Process the returns result set.
        setReturnsResultSet(namedStoredProcedureQuery.returnsResultSet());        
        
        // Process the stored procedure parameters.
        for (StoredProcedureParameter storedProcedureParameter : namedStoredProcedureQuery.procedureParameters()) {
            addProcedureParamater(new MetadataStoredProcedureParameter(storedProcedureParameter));
        }    
    }
    
    /**
     * INTERNAL:
     */
    public void addProcedureParamater(MetadataStoredProcedureParameter procedureParameter) {
        m_procedureParameters.add(procedureParameter);
    }
    
    /**
     * INTERNAL:
     */
    public String getIgnoreLogMessageContext() {
        return MetadataLogger.IGNORE_NAMED_STORED_PROCEDURE_QUERY_ANNOTATION;
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
    public List<MetadataStoredProcedureParameter> getProcedureParameters() {
        return m_procedureParameters;
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
    protected void setProcedureName(String procedureName) {
        m_procedureName = procedureName;
    }
    
    /**
     * INTERNAL:
     */
    protected void setReturnsResultSet(boolean returnsResultSet) {
        m_returnsResultSet = returnsResultSet;
    }
}
