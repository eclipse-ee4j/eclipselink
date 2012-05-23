/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 *     04/05/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 3)
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType 
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.descriptors.SingleTableMultitenantPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MultitenantIdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import static org.eclipse.persistence.config.PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT;

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
    public TenantDiscriminatorColumnMetadata(MetadataAccessor accessor) {
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
    public void process(MetadataDescriptor descriptor, SingleTableMultitenantPolicy policy) {
        DatabaseField tenantDiscriminatorField = getDatabaseField();
            
        // Set the field name. This will take care of any any delimited 
        // identifiers and casing defaults etc.
        setFieldName(tenantDiscriminatorField, NAME_DEFAULT, MetadataLogger.TENANT_DISCRIMINATOR_COLUMN);
    
        // Set a default table if one isn't specified.
        if (! tenantDiscriminatorField.hasTableName()) {
            tenantDiscriminatorField.setTable(descriptor.getPrimaryTable());
        }
            
        // Set the property name, defaulting where necessary and log a warning.
        if (m_contextProperty == null) {
            getLogger().logWarningMessage(MetadataLogger.TENANT_DISCRIMINATOR_CONTEXT_PROPERTY, getAccessibleObject(), tenantDiscriminatorField, MULTITENANT_PROPERTY_DEFAULT);
            m_contextProperty = MULTITENANT_PROPERTY_DEFAULT;
        }

        policy.addTenantDiscriminatorField(m_contextProperty, tenantDiscriminatorField);
        
        // Add a multitenant id accessor to the descriptor (to be processed 
        // later) other mappings may need to look it up hence need to create a 
        // new mapping accessor.
        if (tenantDiscriminatorField.isPrimaryKey()) {
            MultitenantIdAccessor idAccessor = new MultitenantIdAccessor(m_contextProperty, tenantDiscriminatorField, getAccessibleObject(), descriptor.getClassAccessor());
            descriptor.addMappingAccessor(idAccessor);
        }
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
