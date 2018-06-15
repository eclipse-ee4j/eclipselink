/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCursor;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredFunctionCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;

/**
 * INTERNAL:
 * Object to hold onto a PLSQL parameter meta-data.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - all metadata mapped from XML should be initialized in the initXMLObject
 *   method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public class PLSQLParameterMetadata extends ORMetadata {
    private Boolean m_optional;
    private String m_direction;
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

        m_direction = storedProcedureParameter.getAttributeString("direction");
        m_name = storedProcedureParameter.getAttributeString("name");
        m_queryParameter = storedProcedureParameter.getAttributeString("queryParameter");
        m_databaseType = storedProcedureParameter.getAttributeString("databaseType");
        m_optional = storedProcedureParameter.getAttributeBooleanDefaultFalse("optional");
        m_length = storedProcedureParameter.getAttributeInteger("length");
        m_precision = storedProcedureParameter.getAttributeInteger("precision");
        m_scale = storedProcedureParameter.getAttributeInteger("scale");
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

    @Override
    public int hashCode() {
        int result = m_optional != null ? m_optional.hashCode() : 0;
        result = 31 * result + (m_direction != null ? m_direction.hashCode() : 0);
        result = 31 * result + (m_databaseType != null ? m_databaseType.hashCode() : 0);
        result = 31 * result + (m_name != null ? m_name.hashCode() : 0);
        result = 31 * result + (m_queryParameter != null ? m_queryParameter.hashCode() : 0);
        result = 31 * result + (m_length != null ? m_length.hashCode() : 0);
        result = 31 * result + (m_precision != null ? m_precision.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDatabaseType() {
        return m_databaseType;
    }

    /**
     * Return the DataType enum constant for the String type name.  If type is
     * not a JDBCType, OraclePLSQLType, PLSQLCursor or a ComplexMetadataType,
     * the type will be wrapped in a PLSQLrecord.
     */
    @Override
    protected DatabaseType getDatabaseTypeEnum(String type) {
        // handle cursors
        if (Direction.OUT_CURSOR.name().equals(m_direction)) {
            return new PLSQLCursor(type);
        }
        return super.getDatabaseTypeEnum(type);
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
    public Integer getLength() {
        return m_length;
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
    public Boolean getOptional() {
        return m_optional;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getPrecision() {
        return m_precision;
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
     * Used for OX mapping.
     */
    public Integer getScale() {
        return m_scale;
    }

    /**
     * INTERNAL:
     */
    public void process(PLSQLStoredProcedureCall call, boolean functionReturn) {

        // Process the procedure parameter name, defaults to the
        // argument field name.
        // TODO: Log a message when defaulting.
        String procedureParameterName = m_name;
        if (m_name == null || m_name.equals("")) {
            procedureParameterName = m_queryParameter;
        }

        if ((m_optional != null) && m_optional) {
            call.addOptionalArgument(procedureParameterName);
        }

        DatabaseType type = getDatabaseTypeEnum(getDatabaseType());

        // Process the parameter direction
        if (functionReturn) {
            // check for cursor return type
            if (Direction.OUT_CURSOR.name().equals(m_direction)) {
                // the constructor by default adds a RETURN argument, so remove it
                ((PLSQLStoredFunctionCall)call).getArguments().remove(0);
                ((PLSQLStoredFunctionCall)call).useNamedCursorOutputAsResultSet(Direction.OUT_CURSOR.name(), type);
            } else {
                if (getLength() != null) {
                    ((PLSQLStoredFunctionCall)call).setResult(type, getLength());
                } else if (getPrecision() != null) {
                    ((PLSQLStoredFunctionCall)call).setResult(type, getPrecision(), getScale());
                } else {
                    ((PLSQLStoredFunctionCall)call).setResult(type);
                }
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
            call.useNamedCursorOutputAsResultSet(procedureParameterName, type);
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
    public void setDatabaseType(String databaseType) {
        m_databaseType = databaseType;
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
    public void setLength(Integer length) {
        m_length = length;
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
    public void setOptional(Boolean optional) {
        m_optional = optional;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPrecision(Integer precision) {
        m_precision = precision;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setQueryParameter(String queryParameter) {
        m_queryParameter = queryParameter;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setScale(Integer scale) {
        m_scale = scale;
    }
}
