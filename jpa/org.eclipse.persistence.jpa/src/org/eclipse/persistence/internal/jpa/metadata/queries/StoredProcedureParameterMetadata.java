/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;

/**
 * INTERNAL:
 * Object to hold onto a stored procedure parameter metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class StoredProcedureParameterMetadata extends ORMetadata {
    private MetadataClass m_type;
    private String m_direction;
    private Boolean m_optional;
    private Integer m_jdbcType;
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
        
        m_direction = (String) storedProcedureParameter.getAttribute("direction");
        m_name = (String) storedProcedureParameter.getAttribute("name");
        m_queryParameter = (String) storedProcedureParameter.getAttribute("queryParameter"); 
        m_type = getMetadataClass((String) storedProcedureParameter.getAttributeClass("type"));
        m_jdbcType = (Integer) storedProcedureParameter.getAttribute("jdbcType");
        m_jdbcTypeName = (String) storedProcedureParameter.getAttribute("jdbcTypeName");
        m_optional = (Boolean) storedProcedureParameter.getAttribute("optional");
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
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
        return m_jdbcType != null && m_jdbcType.equals(-1);
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
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
    
        m_type = initXMLClassName(m_typeName);
    }
    
    /**
     * INTERNAL:
     */
    public void process(StoredProcedureCall call, MetadataProject project, boolean callByIndex, boolean functionReturn) {                    
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
        // Process the parameter direction
        if (functionReturn) {
            if (hasType()) {
                ((StoredFunctionCall)call).setResult(procedureParameterName, getJavaClass(m_type));
            } else if (hasJdbcType() && hasJdbcTypeName()) {
                ((StoredFunctionCall)call).setResult(procedureParameterName, m_jdbcType, m_jdbcTypeName);
            } else if (hasJdbcType()) {
                ((StoredFunctionCall)call).setResult(procedureParameterName, m_jdbcType);
            } else {
                ((StoredFunctionCall)call).setResult(procedureParameterName);                
            }
        } else if (m_direction == null || m_direction.equals(Direction.IN.name())) {
            // TODO: Log a defaulting message if m_direction is null.
            if (hasType()) {
                if (!callByIndex) {
                    call.addNamedArgument(procedureParameterName, m_queryParameter, getJavaClass(m_type));
                } else {
                    call.addUnamedArgument(m_queryParameter, getJavaClass(m_type));                    
                }
            } else if (hasJdbcType() && hasJdbcTypeName()) {
                if (!callByIndex) {
                    call.addNamedArgument(procedureParameterName, m_queryParameter, m_jdbcType, m_jdbcTypeName);
                } else {
                    call.addUnamedArgument(m_queryParameter, m_jdbcType, m_jdbcTypeName);
                }
            } else if (hasJdbcType()) {
                if (!callByIndex) {
                    call.addNamedArgument(procedureParameterName, m_queryParameter, m_jdbcType);
                } else {
                    call.addUnamedArgument(m_queryParameter, m_jdbcType);
                }
            } else {
                if (!callByIndex) {
                    call.addNamedArgument(procedureParameterName, m_queryParameter);
                } else {
                    call.addUnamedArgument(m_queryParameter);
                }
            }
        } else if (m_direction.equals(Direction.OUT.name())) {
            if (hasType()) {
                if (!callByIndex) {
                    call.addNamedOutputArgument(procedureParameterName, m_queryParameter, getJavaClass(m_type));
                } else {
                    call.addUnamedOutputArgument(m_queryParameter, getJavaClass(m_type));
                }
            } else if (hasJdbcType() && hasJdbcTypeName()) {
                if (!callByIndex) {
                    call.addNamedOutputArgument(procedureParameterName, m_queryParameter, m_jdbcType, m_jdbcTypeName);
                } else {
                    call.addUnamedOutputArgument(m_queryParameter, m_jdbcType, m_jdbcTypeName);
                }
            } else if (hasJdbcType()) {
                if (callByIndex) {
                    call.addNamedOutputArgument(procedureParameterName, m_queryParameter, m_jdbcType);
                } else {
                    call.addUnamedOutputArgument(m_queryParameter, m_jdbcType);
                }
            } else {
                if (!callByIndex) {
                    call.addNamedOutputArgument(procedureParameterName, m_queryParameter);
                } else {
                    call.addUnamedOutputArgument(m_queryParameter);
                }
            }
            //set the project level settings on the argument's database fields
            setDatabaseFieldSettings((DatabaseField)call.getParameters().get(call.getParameters().size() - 1), project);
        } else if (m_direction.equals(Direction.IN_OUT.name())) {
            if (hasType()) {
                if (!callByIndex) {
                    call.addNamedInOutputArgument(procedureParameterName, m_queryParameter, m_queryParameter, getJavaClass(m_type));
                } else {
                    call.addUnamedInOutputArgument(m_queryParameter, m_queryParameter, getJavaClass(m_type));
                }
            } else if (hasJdbcType() && hasJdbcTypeName()) {
                if (!callByIndex) {
                    call.addNamedInOutputArgument(procedureParameterName, m_queryParameter, m_queryParameter, m_jdbcType, m_jdbcTypeName);
                } else {
                    call.addUnamedInOutputArgument(m_queryParameter, m_queryParameter, m_jdbcType, m_jdbcTypeName);
                }
            } else if (hasJdbcType()) {
                if (!callByIndex) {
                    call.addNamedInOutputArgument(procedureParameterName, m_queryParameter, m_queryParameter, m_jdbcType);
                } else {
                    call.addUnamedInOutputArgument(m_queryParameter, m_queryParameter, m_jdbcType);
                }
            } else {
                if (!callByIndex) {
                    call.addNamedInOutputArgument(procedureParameterName, m_queryParameter);
                } else {
                    call.addUnamedInOutputArgument(m_queryParameter);
                }
            }
            //set the project level settings on the argument's out database field
            Object[] array = (Object[])call.getParameters().get(call.getParameters().size() - 1);
            if (array[0] == array[1]){
                array[1] = ((DatabaseField)array[1]).clone();
            }
            setDatabaseFieldSettings((DatabaseField)array[1], project);
        } else if (m_direction.equals(Direction.OUT_CURSOR.name())) {
            boolean multipleCursors = false;
            if (call.getParameterTypes().contains(call.OUT_CURSOR)) {
                multipleCursors = true;
            }
            if (!callByIndex) {
                call.useNamedCursorOutputAsResultSet(m_queryParameter);
            } else {
                call.useUnnamedCursorOutputAsResultSet();
            }
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
    
    public Boolean getOptional() {
        return m_optional;
    }

    public void setOptional(Boolean optional) {
        m_optional = optional;
    }
}
