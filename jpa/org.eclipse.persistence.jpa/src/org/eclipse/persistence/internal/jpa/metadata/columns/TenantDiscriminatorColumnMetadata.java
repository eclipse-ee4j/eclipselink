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
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Object to hold onto tenant discriminator metadata.
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
 * @since EclipseLink 2.3
 */
public class TenantDiscriminatorColumnMetadata extends DiscriminatorColumnMetadata {
    public static final String NAME_DEFAULT = "TENANT_ID";
    public static final String CONTEXT_PROPERTY_DEFAULT = "eclipselink.tenant-id";
    
    private Boolean m_primaryKey;
    private String m_table;
    private String m_contextProperty;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public TenantDiscriminatorColumnMetadata() {
        super("<tenant-discriminator-column>");
    }
    
    /**
     * INTERNAL:
     * Used for defaulting.
     */
    public TenantDiscriminatorColumnMetadata(MetadataProject project) {
        setProject(project);
    }
    
    /**
     * INTERNAL:
     * Used for defaulting.
     */
    protected TenantDiscriminatorColumnMetadata(MetadataAccessor accessor) {
        super(accessor);
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public TenantDiscriminatorColumnMetadata(MetadataAnnotation tenantDiscriminator, MetadataAccessor accessor) {
        super(tenantDiscriminator, accessor);
        
        m_table = (String) tenantDiscriminator.getAttribute("table");
        m_primaryKey = (Boolean) tenantDiscriminator.getAttributeBooleanDefaultFalse("primaryKey");
        m_contextProperty = (String) tenantDiscriminator.getAttribute("contextProperty");
    }
    
    /**
     * INTERNAL:
     * Required ORMetadata method used for XML merging.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof TenantDiscriminatorColumnMetadata) {
            TenantDiscriminatorColumnMetadata tenantDiscriminator = (TenantDiscriminatorColumnMetadata) objectToCompare;
            
            if (! valuesMatch(m_primaryKey, tenantDiscriminator.getPrimaryKey())) {
                return false;
            }
            
            if (! valuesMatch(m_contextProperty, tenantDiscriminator.getContextProperty())) {
                return false;
            }
            
            return valuesMatch(m_table, tenantDiscriminator.getTable());
        }
        
        return false;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getContextProperty() {
        return m_contextProperty;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public DatabaseField getDatabaseField() {
        // Initialize the DatabaseField with values and defaults.
        DatabaseField field = super.getDatabaseField();
        
        // If the table is specified use it.
        if (m_table != null) {
            field.setTableName(m_table);
        }
        
        // Set the primary key setting
        field.setPrimaryKey(m_primaryKey == null ? false : m_primaryKey.booleanValue());
        
        return field;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getPrimaryKey() {
        return m_primaryKey;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getTable() {
        return m_table;
    }

    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor) {
        DatabaseField tenantDiscriminatorField = getDatabaseField();
            
        // Set the field name. This will take care of any any delimited 
        // identifiers and casing defaults etc.
        setFieldName(tenantDiscriminatorField, NAME_DEFAULT);
    
        // Set a default table if one isn't specified.
        if (! tenantDiscriminatorField.hasTableName()) {
            tenantDiscriminatorField.setTable(descriptor.getPrimaryTable());
        }
            
        // Set the property name, defaulting where necessary.
        if (m_contextProperty == null) {
            // TODO: log a warning.
            m_contextProperty = CONTEXT_PROPERTY_DEFAULT;
        }

        // TODO: Guy update when core is implemented
        //descriptor.getClassDescriptor().addTenantDiscriminatorField(m_contextProperty, tenantDiscriminatorField);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setContextProperty(String contextProperty) {
        m_contextProperty = contextProperty;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPrimaryKey(Boolean primaryKey) {
        m_primaryKey = primaryKey;
    }    

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTable(String table) {
        m_table = table;
    }
}
