/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredFunctionCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;

/**
 * INTERNAL:
 * Object to hold onto a PLSQL parameter meta-data.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public class PLSQLParameterMetadata extends ORMetadata {
    private String m_direction;
    private Boolean m_optional;
    private String m_databaseType;

    private String m_name;
    private String m_queryParameter;
    private Integer m_length;
    private Integer m_precision;
    private Integer m_scale;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public PLSQLParameterMetadata() {
        super("<plsql-parameter>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public PLSQLParameterMetadata(MetadataAnnotation storedProcedureParameter, MetadataAccessor accessor) {
        super(storedProcedureParameter, accessor);
        
        m_direction = (String) storedProcedureParameter.getAttribute("direction");
        m_name = (String) storedProcedureParameter.getAttribute("name");
        m_queryParameter = (String) storedProcedureParameter.getAttribute("queryParameter"); 
        m_databaseType = (String) storedProcedureParameter.getAttribute("databaseType");
        m_optional = (Boolean) storedProcedureParameter.getAttribute("optional");
        m_length = (Integer) storedProcedureParameter.getAttribute("length");
        m_precision = (Integer) storedProcedureParameter.getAttribute("precision");
        m_scale = (Integer) storedProcedureParameter.getAttribute("scale");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof PLSQLParameterMetadata) {
            PLSQLParameterMetadata parameter = (PLSQLParameterMetadata) objectToCompare;
            
            if (! valuesMatch(m_databaseType, parameter.getDatabaseType())) {
                return false;
            }
            
            if (! valuesMatch(m_direction, parameter.getDirection())) {
                return false;
            }
            
            if (! valuesMatch(m_length, parameter.getLength())) {
                return false;
            }

            if (! valuesMatch(m_precision, parameter.getPrecision())) {
                return false;
            }
            
            if (! valuesMatch(m_name, parameter.getName())) {
                return false;
            }
            
            if (! valuesMatch(m_optional, parameter.getOptional())) {
                return false;
            }
            
            return valuesMatch(m_queryParameter, parameter.getQueryParameter());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDirection() {
        return m_direction;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getQueryParameter() {
        return m_queryParameter;
    }
    
    /**
     * INTERNAL:
     */
    public void process(PLSQLStoredProcedureCall call, MetadataProject project, boolean functionReturn) {
                    
        // Process the procedure parameter name, defaults to the 
        // argument field name.
        // TODO: Log a message when defaulting.
        String procedureParameterName = m_name;
        if (m_name == null || m_name.equals("")) {
            procedureParameterName = m_queryParameter;
        }

        if ((m_optional != null) && m_optional) {
            call.addOptionalArgument(m_queryParameter);
        }
        DatabaseType type = getDatabaseTypeEnum();
        // Process the parameter direction
        if (functionReturn) {
            if (getLength() != null) {
                ((PLSQLStoredFunctionCall)call).setResult(type, getLength());
            } else if (getPrecision() != null) {
                ((PLSQLStoredFunctionCall)call).setResult(type, getPrecision(), getScale());
            } else {
                ((PLSQLStoredFunctionCall)call).setResult(type);
            }
        } else if (m_direction == null || m_direction.equals(Direction.IN.name())) {
            // TODO: Log a defaulting message if m_direction is null.
            if (getLength() != null) {
                call.addNamedArgument(procedureParameterName, type, getLength());
            } else if (getPrecision() != null) {
                call.addNamedArgument(procedureParameterName, type, getPrecision(), getScale());
            } else {
                call.addNamedArgument(procedureParameterName, type);
            }
        } else if (m_direction.equals(Direction.OUT.name())) {
            if (getLength() != null) {
                call.addNamedOutputArgument(procedureParameterName, type, getLength());
            } else if (getPrecision() != null) {
                call.addNamedOutputArgument(procedureParameterName, type, getPrecision(), getScale());
            } else {
                call.addNamedOutputArgument(procedureParameterName, type);
            }
        } else if (m_direction.equals(Direction.IN_OUT.name())) {
            if (getLength() != null) {
                call.addNamedInOutputArgument(procedureParameterName, type, getLength());
            } else if (getPrecision() != null) {
                call.addNamedInOutputArgument(procedureParameterName, type, getPrecision(), getScale());
            } else {
                call.addNamedInOutputArgument(procedureParameterName, type);
            }
        } else if (m_direction.equals(Direction.OUT_CURSOR.name())) {
            boolean multipleCursors = false;
            if (call.getParameterTypes().contains(call.OUT_CURSOR)) {
                multipleCursors = true;
            }
            call.useNamedCursorOutputAsResultSet(procedureParameterName);
            // There are multiple cursor output parameters, then do not use the cursor as the result set.
            if (multipleCursors) {
                call.setIsCursorOutputProcedure(false);
            }
        } 
    }

    /**
     * INTERNAL:
     * set the project level settings on the database fields
     */
    protected void setDatabaseFieldSettings(DatabaseField field, MetadataProject project){
        if (project.useDelimitedIdentifier()) {
            field.setUseDelimiters(true);
        } else if (project.getShouldForceFieldNamesToUpperCase() && !field.shouldUseDelimiters()) {
            field.useUpperCaseForComparisons(true);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDirection(String direction) {
        m_direction = direction;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setQueryParameter(String queryParameter) {
        m_queryParameter = queryParameter;
    }
    
    public Boolean getOptional() {
        return m_optional;
    }

    public void setOptional(Boolean optional) {
        m_optional = optional;
    }
    
    /**
     * Return the DataType enum constant for the String type name.
     * If not a type defined by the enums, then return a record type.
     */
    public DatabaseType getDatabaseTypeEnum() {
        if (getDatabaseType() == null) {
            return JDBCTypes.VARCHAR_TYPE;
        }
        try {
            return JDBCTypes.valueOf(getDatabaseType());
        } catch (Exception invalid) {
            try {
                return OraclePLSQLTypes.valueOf(getDatabaseType());
            } catch (Exception alsoInvalid) {
                PLSQLrecord record = new PLSQLrecord();
                record.setTypeName(getDatabaseType());
                return record;
            }
        }
    }
    
    public String getDatabaseType() {
        return m_databaseType;
    }

    public void setDatabaseType(String databaseType) {
        m_databaseType = databaseType;
    }

    public Integer getPrecision() {
        return m_precision;
    }

    public void setPrecision(Integer precision) {
        m_precision = precision;
    }

    public Integer getScale() {
        return m_scale;
    }

    public void setScale(Integer scale) {
        m_scale = scale;
    }

    public Integer getLength() {
        return m_length;
    }

    public void setLength(Integer length) {
        m_length = length;
    }
}
