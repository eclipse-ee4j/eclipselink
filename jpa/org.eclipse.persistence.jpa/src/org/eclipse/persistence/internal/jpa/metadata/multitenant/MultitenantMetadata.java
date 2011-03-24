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
package org.eclipse.persistence.internal.jpa.metadata.multitenant;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumns;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.TenantDiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * Object to hold onto multi-tenant metadata.
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
public class MultitenantMetadata extends ORMetadata {
    private List<TenantDiscriminatorColumnMetadata> m_tenantDiscriminatorColumns = new ArrayList<TenantDiscriminatorColumnMetadata>();
    private String m_type;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public MultitenantMetadata() {
        super("<multitenant>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public MultitenantMetadata(MetadataAnnotation multitenant, MetadataAccessor accessor) {
        super(multitenant, accessor);
        
        m_type = (String) multitenant.getAttribute("value");

        // Look for a @TenantDiscriminators
        if (accessor.isAnnotationPresent(TenantDiscriminatorColumns.class)) {
            for (Object tenantDiscriminatorColumn : (Object[]) accessor.getAnnotation(TenantDiscriminatorColumns.class).getAttributeArray("value")) {
                m_tenantDiscriminatorColumns.add(new TenantDiscriminatorColumnMetadata((MetadataAnnotation) tenantDiscriminatorColumn, accessor));
            }
        }
        
        // Look for a @TenantDiscriminator.
        if (accessor.isAnnotationPresent(TenantDiscriminatorColumn.class)) {
            m_tenantDiscriminatorColumns.add(new TenantDiscriminatorColumnMetadata(accessor.getAnnotation(TenantDiscriminatorColumn.class), accessor));
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof MultitenantMetadata) {
            MultitenantMetadata multitenant = (MultitenantMetadata) objectToCompare;
                
            if (! valuesMatch(m_type, multitenant.getType())) {
                return false;
            }

            return valuesMatch(m_tenantDiscriminatorColumns, multitenant.getTenantDiscriminatorColumns());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<TenantDiscriminatorColumnMetadata> getTenantDiscriminatorColumns() {
        return m_tenantDiscriminatorColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getType() {
        return m_type;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize lists of objects.
        initXMLObjects(m_tenantDiscriminatorColumns, accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor)  {
        if (m_type == null || m_type.equals(MultitenantType.SINGLE_TABLE.name())) {
            // Single table multi-tenancy.
            processTenantDiscriminators(descriptor);
        } else { 
            // TODO: to be implemented at some point.
            throw new RuntimeException("Unsupported multitenant type: " + m_type);
        }
    }
    
    /**
     * INTERNAL:
     * Process the tenant discriminator metadata.
     */
    protected void processTenantDiscriminators(MetadataDescriptor descriptor) {
        // Check for tenant discriminator columns from a parent class.
        if (descriptor.isInheritanceSubclass()) {
            // If we are an inheritance subclass, our parent will have been
            // processed and we only care about discriminator columns if we are
            // part of a TABLE_PER_CLASS setting.
            EntityAccessor parentAccessor = descriptor.getInheritanceRootDescriptor().getEntityAccessor();

            if (! parentAccessor.getInheritance().usesTablePerClassStrategy()) {
                // If we are a JOINED or SINGLE_TABLE strategy, just verify the
                // user has not specified discriminator columns on the subclass. 
                if (! m_tenantDiscriminatorColumns.isEmpty()) {
                    getLogger().logWarningMessage(MetadataLogger.IGNORE_INHERITANCE_TENANT_DISCRIMINATOR_COLUMN, descriptor.getJavaClass());
                }
                
                return;
            }
        }
        
        // Look for default tenant discriminators (from entity mappings or pu 
        // defaults level if none are associated with this multitenant metadata.
        if (m_tenantDiscriminatorColumns.isEmpty()) {
            m_tenantDiscriminatorColumns = descriptor.getDefaultTenantDiscriminatorColumns();
                
            // If we still don't have a tenant discriminator, default one.
            if (m_tenantDiscriminatorColumns.isEmpty()) {
                m_tenantDiscriminatorColumns.add(new TenantDiscriminatorColumnMetadata(getProject()));
            }
        }
            
        // Process the tenant discriminators now.
        for (TenantDiscriminatorColumnMetadata tenantDiscriminator : m_tenantDiscriminatorColumns) {
            tenantDiscriminator.process(descriptor);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTenantDiscriminatorColumns(List<TenantDiscriminatorColumnMetadata> tenantDiscriminatorColumns) {
        m_tenantDiscriminatorColumns = tenantDiscriminatorColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setType(String type) {
        m_type = type;
    }
}
