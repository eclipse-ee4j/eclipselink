/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//      //     30/05/2012-2.4 Guy Pelletier
//       - 354678: Temp classloader is still being used during metadata processing
//     09/27/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.internal.jpa.metadata.queries;

import static java.sql.Types.STRUCT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_IN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_INOUT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_OUT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PARAMETER_REF_CURSOR;

import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;

/**
 * INTERNAL:
 * Object to hold onto a stored procedure parameter metadata.
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
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class StoredProcedureParameterMetadata extends ORMetadata {
    private Boolean m_optional;
    private Integer m_jdbcType;

    private MetadataClass m_type;

    private String m_direction;
    private String m_mode;
    private String m_jdbcTypeName;
    private String m_name;
    private String m_queryParameter;
    private String m_typeName;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public StoredProcedureParameterMetadata() {
        super("<stored-procedure-parameter>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public StoredProcedureParameterMetadata(MetadataAnnotation storedProcedureParameter, MetadataAccessor accessor) {
        super(storedProcedureParameter, accessor);

        m_direction = storedProcedureParameter.getAttributeString("direction");
        m_mode = storedProcedureParameter.getAttributeString("mode");
        m_name = storedProcedureParameter.getAttributeString("name");
        m_queryParameter = storedProcedureParameter.getAttributeString("queryParameter");
        m_type = getMetadataClass(storedProcedureParameter.getAttributeClass("type", Void.class));
        m_jdbcType = storedProcedureParameter.getAttributeInteger("jdbcType");
        m_jdbcTypeName = storedProcedureParameter.getAttributeString("jdbcTypeName");
        m_optional = storedProcedureParameter.getAttributeBooleanDefaultFalse("optional");
    }

    /**
     * Builds an ObjectRelationalDatabaseField based on a given OracleArrayTypeMetadata
     * instance.
     *
     * @param aType OracleArrayTypeMetadata instance to be used to construct the field
     * @return an ObjectRelationalDatabaseField instance
     */
    protected ObjectRelationalDatabaseField buildNestedField(OracleArrayTypeMetadata aType) {
        ObjectRelationalDatabaseField dbFld = new ObjectRelationalDatabaseField("");
        String nestedType = aType.getNestedType();
        dbFld.setSqlTypeName(nestedType);
        for (OracleObjectTypeMetadata oType : getEntityMappings().getOracleObjectTypes()) {
            if (oType.getName().equals(nestedType)) {
                dbFld.setSqlType(STRUCT);
                dbFld.setTypeName(oType.getJavaType());
            }
        }
        return dbFld;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof StoredProcedureParameterMetadata) {
            StoredProcedureParameterMetadata parameter = (StoredProcedureParameterMetadata) objectToCompare;

            if (! valuesMatch(m_type, parameter.getType())) {
                return false;
            }

            if (! valuesMatch(m_direction, parameter.getDirection())) {
                return false;
            }

            if (! valuesMatch(m_mode, parameter.getMode())) {
                return false;
            }

            if (! valuesMatch(m_jdbcType, parameter.getJdbcType())) {
                return false;
            }

            if (! valuesMatch(m_jdbcTypeName, parameter.getJdbcTypeName())) {
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
        result = 31 * result + (m_jdbcType != null ? m_jdbcType.hashCode() : 0);
        result = 31 * result + (m_type != null ? m_type.hashCode() : 0);
        result = 31 * result + (m_direction != null ? m_direction.hashCode() : 0);
        result = 31 * result + (m_mode != null ? m_mode.hashCode() : 0);
        result = 31 * result + (m_jdbcTypeName != null ? m_jdbcTypeName.hashCode() : 0);
        result = 31 * result + (m_name != null ? m_name.hashCode() : 0);
        result = 31 * result + (m_queryParameter != null ? m_queryParameter.hashCode() : 0);
        return result;
    }

    /**
     * Returns the OracleArrayTypeMetadata instance for a given class name, or null
     * if none exists.
     *
     * @param javaClassName  class name used to look up the OracleArrayTypeMetadata instance
     * @return  the OracleArrayTypeMetadata instance with javaType matching javaClassName,
     * or null if none exists.
     */
    protected OracleArrayTypeMetadata getArrayTypeMetadata(String javaClassName) {
        for (OracleArrayTypeMetadata aType : getEntityMappings().getOracleArrayTypes()) {
            if (aType.getJavaType().equals(javaClassName)) {
                return aType;
            }
        }
        return null;
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
    public Integer getJdbcType() {
        return m_jdbcType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getJdbcTypeName() {
        return m_jdbcTypeName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getMode() {
        return m_mode;
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
    public String getQueryParameter() {
        return m_queryParameter;
    }

    /**
     * INTERNAL:
     */
    public MetadataClass getType() {
        return m_type;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getTypeName() {
        return m_typeName;
    }

    /**
     * INTERNAL:
     */
    protected boolean hasJdbcType() {
        return m_jdbcType != null && ! m_jdbcType.equals(-1);
    }

    /**
     * INTERNAL:
     */
    protected boolean hasJdbcTypeName() {
        return m_jdbcTypeName != null && ! m_jdbcTypeName.equals("");
    }

    /**
     * INTERNAL:
     */
    protected boolean hasType() {
        return !m_type.isVoid();
    }

    /**
     * INTERNAL:
     */
    protected boolean hasTypeName() {
        return m_typeName != null && ! m_typeName.equals("");
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        m_type = initXMLClassName(m_typeName);
    }

    /**
     * INTERNAL:
     */
    public boolean isOutParameter() {
        String parameterMode = (m_direction == null) ? m_mode : m_direction;
        return parameterMode != null && ( parameterMode.equals(JPA_PARAMETER_OUT)
                || parameterMode.equals(JPA_PARAMETER_INOUT)
                || parameterMode.equals(JPA_PARAMETER_REF_CURSOR)
                || parameterMode.equals(Direction.OUT_CURSOR.name()));
    }

    /**
     * INTERNAL:
     */
    private boolean process(StoredProcedureCall call, int index) {
        boolean shouldCallByIndex = false;

        // Process the procedure parameter name, defaults to the argument field name.
        if (m_name == null || m_name.equals("")) {
            if (m_queryParameter == null || m_queryParameter.equals("")) {
                // JPA 2.1 make the query parameter positional
                shouldCallByIndex = true;
                m_queryParameter = String.valueOf(index);
            } else {
                // EclipseLink support, a query parameter is required to be specified.
                // TODO: Log a message when defaulting.
                m_name = m_queryParameter;
            }
        }

        // There is no such thing as queryParameter in JPA's version.
        if (m_queryParameter == null || m_queryParameter.equals("")) {
            m_queryParameter = m_name;
        }

        if ((m_optional != null) && m_optional) {
            call.addOptionalArgument(m_queryParameter);
        }

        if (m_mode == null) {
            if (m_direction == null) {
                // TODO: Log a defaulting message if parameterMode is null.
                m_mode = JPA_PARAMETER_IN;
            } else {
                m_mode = m_direction;
            }
        }

        return shouldCallByIndex;
    }

    /**
     * INTERNAL:
     * #Bug 533272 - JPA NamedStoredProcedure call getOutputParameterValue with parameter name cause exception
     */
    public void processArgument(StoredProcedureCall call, boolean callByIndex, int index) {
         boolean shouldCallByIndex = process(call, index);

         if (! callByIndex) {
             callByIndex = shouldCallByIndex;
         }

        if (m_mode.equals(JPA_PARAMETER_IN)) {
            if (hasType()) {
                if (callByIndex) {
                    call.addUnamedArgument(m_queryParameter, getJavaClass(m_type));
                } else {
                    if (hasJdbcType() && hasJdbcTypeName()) {
                        OracleArrayTypeMetadata aType = null;
                        if (hasTypeName() && (aType = getArrayTypeMetadata(m_typeName)) != null) {
                            call.addNamedArgument(m_name, m_queryParameter, m_jdbcType, m_jdbcTypeName, getJavaClass(m_type), buildNestedField(aType));
                        } else {
                            call.addNamedArgument(m_name, m_queryParameter, m_jdbcType, m_jdbcTypeName, getJavaClass(m_type));
                        }
                    } else {
                        call.addNamedArgument(m_name, m_queryParameter, getJavaClass(m_type));
                    }
                }
            } else if (hasJdbcType() && hasJdbcTypeName()) {
                if (callByIndex) {
                    call.addUnamedArgument(m_queryParameter, m_jdbcType, m_jdbcTypeName);
                } else {
                    call.addNamedArgument(m_name, m_queryParameter, m_jdbcType, m_jdbcTypeName);
                }
            } else if (hasJdbcType()) {
                if (callByIndex) {
                    call.addUnamedArgument(m_queryParameter, m_jdbcType);
                } else {
                    call.addNamedArgument(m_name, m_queryParameter, m_jdbcType);
                }
            } else {
                if (callByIndex) {
                    call.addUnamedArgument(m_queryParameter);
                } else {
                    call.addNamedArgument(m_name, m_queryParameter);
                }
            }
        } else if (m_mode.equals(JPA_PARAMETER_OUT)) {
            if (hasType()) {
                if (callByIndex) {
                    call.addUnamedOutputArgument(m_queryParameter, getJavaClass(m_type));
                } else {
                    if (hasJdbcType() && hasJdbcTypeName()) {
                        OracleArrayTypeMetadata aType = null;
                        if (hasTypeName() && (aType = getArrayTypeMetadata(m_typeName)) != null) {
                            call.addNamedOutputArgument(m_name, m_queryParameter, m_jdbcType, m_jdbcTypeName, getJavaClass(m_type), buildNestedField(aType));
                        } else {
                            call.addNamedOutputArgument(m_name, m_queryParameter, m_jdbcType, m_jdbcTypeName, getJavaClass(m_type));
                        }
                    } else {
                        call.addNamedOutputArgument(m_name, m_queryParameter, getJavaClass(m_type));
                    }
                }
            } else if (hasJdbcType() && hasJdbcTypeName()) {
                if (callByIndex) {
                    call.addUnamedOutputArgument(m_queryParameter, m_jdbcType, m_jdbcTypeName);
                } else {
                    call.addNamedOutputArgument(m_name, m_queryParameter, m_jdbcType, m_jdbcTypeName);
                }
            } else if (hasJdbcType()) {
                if (callByIndex) {
                    call.addUnamedOutputArgument(m_queryParameter, m_jdbcType);
                } else {
                    call.addNamedOutputArgument(m_name, m_queryParameter, m_jdbcType);
                }
            } else {
                if (callByIndex) {
                    call.addUnamedOutputArgument(m_queryParameter);
                } else {
                    call.addNamedOutputArgument(m_name, m_queryParameter);
                }
            }

            //set the project level settings on the argument's database fields
            setDatabaseFieldSettings((DatabaseField)call.getParameters().get(call.getParameters().size() - 1));
        } else if (m_mode.equals(Direction.IN_OUT.name()) || m_mode.equals(JPA_PARAMETER_INOUT)) {
            if (hasType()) {
                if (callByIndex) {
                    call.addUnamedInOutputArgument(m_queryParameter, m_queryParameter, getJavaClass(m_type));
                } else {
                    if (hasJdbcType() && hasJdbcTypeName()) {
                        OracleArrayTypeMetadata aType = null;
                        if (hasTypeName() && (aType = getArrayTypeMetadata(m_typeName)) != null) {
                            call.addNamedInOutputArgument(m_name, m_queryParameter, m_queryParameter, m_jdbcType, m_jdbcTypeName, getJavaClass(m_type), buildNestedField(aType));
                        } else {
                            call.addNamedInOutputArgument(m_name, m_queryParameter, m_queryParameter, m_jdbcType, m_jdbcTypeName, getJavaClass(m_type));
                        }
                    } else {
                        call.addNamedInOutputArgument(m_name, m_queryParameter, m_queryParameter, getJavaClass(m_type));
                    }
                }
            } else if (hasJdbcType() && hasJdbcTypeName()) {
                if (callByIndex) {
                    call.addUnamedInOutputArgument(m_queryParameter, m_queryParameter, m_jdbcType, m_jdbcTypeName);
                } else {
                    call.addNamedInOutputArgument(m_name, m_queryParameter, m_queryParameter, m_jdbcType, m_jdbcTypeName);
                }
            } else if (hasJdbcType()) {
                if (callByIndex) {
                    call.addUnamedInOutputArgument(m_queryParameter, m_queryParameter, m_jdbcType);
                } else {
                    call.addNamedInOutputArgument(m_name, m_queryParameter, m_queryParameter, m_jdbcType);
                }
            } else {
                if (callByIndex) {
                    call.addUnamedInOutputArgument(m_queryParameter);
                } else {
                    call.addNamedInOutputArgument(m_name, m_queryParameter);
                }
            }

            //set the project level settings on the argument's out database field
            Object[] array = (Object[])call.getParameters().get(call.getParameters().size() - 1);
            if (array[0] == array[1]){
                array[1] = ((DatabaseField)array[1]).clone();
            }

            setDatabaseFieldSettings((DatabaseField) array[1]);
        } else if (m_mode.equals(Direction.OUT_CURSOR.name()) || m_mode.equals(JPA_PARAMETER_REF_CURSOR)) {
            if (callByIndex) {
                call.useUnnamedCursorOutputAsResultSet(index);
            } else {
                call.useNamedCursorOutputAsResultSet(m_queryParameter);
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void processResult(StoredFunctionCall call, int index) {
        process(call, index);

        // Process the function parameter
        if (hasType()) {
            if (hasJdbcType() && hasJdbcTypeName()) {
                OracleArrayTypeMetadata aType = null;
                if (hasTypeName() && (aType = getArrayTypeMetadata(m_typeName)) != null) {
                    call.setResult(m_jdbcType, m_jdbcTypeName, getJavaClass(m_type), buildNestedField(aType));
                } else {
                    call.setResult(m_jdbcType, m_jdbcTypeName, getJavaClass(m_type));
                }
            } else {
                call.setResult(m_name, getJavaClass(m_type));
            }
        } else if (hasJdbcType() && hasJdbcTypeName()) {
            call.setResult(m_name, m_jdbcType, m_jdbcTypeName);
        } else if (hasJdbcType()) {
            call.setResult(m_name, m_jdbcType);
        } else {
            call.setResult(m_name);
        }
    }

    /**
     * INTERNAL:
     * set the project level settings on the database fields
     */
    protected void setDatabaseFieldSettings(DatabaseField field) {
        if (getProject().useDelimitedIdentifier()) {
            field.setUseDelimiters(true);
        } else if (getProject().getShouldForceFieldNamesToUpperCase() && !field.shouldUseDelimiters()) {
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
    public void setJdbcType(Integer jdbcType) {
        m_jdbcType = jdbcType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJdbcTypeName(String jdbcTypeName) {
        m_jdbcTypeName = jdbcTypeName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMode(String mode) {
        m_mode = mode;
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
    public void setQueryParameter(String queryParameter) {
        m_queryParameter = queryParameter;
    }

    /**
     * INTERNAL:
     */
    public void setType(MetadataClass type) {
        m_type = type;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTypeName(String typeName) {
        m_typeName = typeName;
    }
}
