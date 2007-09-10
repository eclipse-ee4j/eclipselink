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

import org.eclipse.persistence.annotations.StoredProcedureParameter;

/**
 * Object to hold onto a stored procedure parameter metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class MetadataStoredProcedureParameter {
    private Class m_type;
    private int m_jdbcType;
    private String m_name;
    private String m_direction;
    private String m_jdbcTypeName;
    private String m_queryParameter;
    
    /**
     * INTERNAL:
     */
    public MetadataStoredProcedureParameter(StoredProcedureParameter storedProcedureParameter) {
        // Process the direction.
        setDirection(storedProcedureParameter.procedureParameterDirection().name());
        
        // Process the name.
        setName(storedProcedureParameter.name());
     
        // Process the query parameter.
        setQueryParameter(storedProcedureParameter.queryParameter());
        
        // Process the type.
        setType(storedProcedureParameter.type());
        
        // Process the JDBC type.
        setJdbcType(storedProcedureParameter.jdbcType());
        
        // Process the JDBC type name.
        setJdbcTypeName(storedProcedureParameter.jdbcTypeName());   
    }
    
    /**
     * INTERNAL:
     */
    public String getDirection() {
        return m_direction;
    }
    
    /**
     * INTERNAL:
     */
    public int getJdbcType() {
        return m_jdbcType;
    }
    
    /**
     * INTERNAL:
     */
    public String getJdbcTypeName() {
        return m_jdbcTypeName;
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
    public String getQueryParameter() {
        return m_queryParameter;
    }
    
    /**
     * INTERNAL:
     */
    public Class getType() {
        return m_type;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasJdbcType() {
        return getJdbcType() != -1;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasJdbcTypeName() {
        return ! getJdbcTypeName().equals("");
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasType() {
        return getType() != void.class;
    }
    
    /**
     * INTERNAL:
     */
    protected void setDirection(String direction) {
        m_direction = direction;
    }
    
    /**
     * INTERNAL:
     */
    protected void setJdbcType(int jdbcType) {
        m_jdbcType = jdbcType;
    }
    
    /**
     * INTERNAL:
     */
    protected void setJdbcTypeName(String jdbcTypeName) {
        m_jdbcTypeName = jdbcTypeName;
    }
    
    /**
     * INTERNAL:
     */
    protected void setName(String name) {
        m_name = name;
    }
    
    /**
     * INTERNAL:
     */
    protected void setQueryParameter(String queryParameter) {
        m_queryParameter = queryParameter;
    }
    
    /**
     * INTERNAL:
     */
    protected void setType(Class type) {
        m_type = type;
    }
}
