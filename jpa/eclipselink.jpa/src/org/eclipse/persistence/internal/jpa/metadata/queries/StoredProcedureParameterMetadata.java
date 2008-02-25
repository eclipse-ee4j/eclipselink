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

/**
 * INTERNAL:
 * Object to hold onto a stored procedure parameter metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class StoredProcedureParameterMetadata {
    private Class m_type;
    private int m_jdbcType;
    private String m_name;
    private String m_direction;
    private String m_jdbcTypeName;
    private String m_queryParameter;
    
    /**
     * INTERNAL:
     */
    public StoredProcedureParameterMetadata(Object storedProcedureParameter) {
        Object direction = MetadataHelper.invokeMethod("procedureParameterDirection", storedProcedureParameter, (Object[])null);
        m_direction = (String)MetadataHelper.invokeMethod("name", direction, (Object[])null);
        m_name = (String)MetadataHelper.invokeMethod("name", storedProcedureParameter, (Object[])null);
        m_queryParameter = (String)MetadataHelper.invokeMethod("queryParameter", storedProcedureParameter, (Object[])null); 
        m_type = (Class)MetadataHelper.invokeMethod("type", storedProcedureParameter, (Object[])null);
        m_jdbcType = (Integer)MetadataHelper.invokeMethod("jdbcType", storedProcedureParameter, (Object[])null);
        m_jdbcTypeName = (String)MetadataHelper.invokeMethod("jdbcTypeName", storedProcedureParameter, (Object[])null);
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
