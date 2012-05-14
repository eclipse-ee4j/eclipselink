/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     14/05/2012-2.4 Guy Pelletier   
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.multitenant;

import org.eclipse.persistence.annotations.TenantTableDiscriminatorType;
import org.eclipse.persistence.descriptors.TablePerMultitenantPolicy;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import static org.eclipse.persistence.config.PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT;

/**
 * Object to hold onto tenant table discriminator metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject  method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.4
 */
public class TenantTableDiscriminatorMetadata extends ORMetadata {
    protected final static TenantTableDiscriminatorType TYPE_DEFAULT = TenantTableDiscriminatorType.SUFFIX;
    
    protected String m_type;
    protected String m_contextProperty;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public TenantTableDiscriminatorMetadata() {
        super("<tenant-table-discriminator>");
    }
    
    /**
     * INTERNAL:
     * Used for defaulting.
     */
    public TenantTableDiscriminatorMetadata(MetadataAccessor accessor) {
        super(null, accessor);
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public TenantTableDiscriminatorMetadata(MetadataAnnotation tenantTableDiscriminator, MetadataAccessor accessor) {
        super(tenantTableDiscriminator, accessor);

        m_type = (String) tenantTableDiscriminator.getAttribute("type");
        m_contextProperty = (String) tenantTableDiscriminator.getAttribute("contextProperty");
    }

    /**
     * INTERNAL:
     * Required ORMetadata method used for XML merging.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof TenantTableDiscriminatorMetadata) {
            TenantTableDiscriminatorMetadata tenantTableDiscriminator = (TenantTableDiscriminatorMetadata) objectToCompare;
            
            if (! valuesMatch(m_contextProperty, tenantTableDiscriminator.getContextProperty())) {
                return false;
            }
            
            return valuesMatch(m_type, tenantTableDiscriminator.getType());
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
     * Used for OX mapping.
     */
    public String getType() {
        return m_type;
    }

    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor, TablePerMultitenantPolicy policy) {
        // Set the table discriminator type
        if (m_type == null) {
            // Log a defaulting message.
            getLogger().logConfigMessage(MetadataLogger.TENANT_TABLE_DISCRIMINATOR_TYPE, descriptor.getJavaClass(), TYPE_DEFAULT.name());
            policy.setTenantTableDiscriminatorType(TYPE_DEFAULT);
        } else {
            policy.setTenantTableDiscriminatorType(TenantTableDiscriminatorType.valueOf(m_type));
        }
        
        // Set the context property name, defaulting where necessary and log a warning.
        if (m_contextProperty == null) {
            getLogger().logWarningMessage(MetadataLogger.TENANT_TABLE_DISCRIMINATOR_CONTEXT_PROPERTY, descriptor.getJavaClass(), MULTITENANT_PROPERTY_DEFAULT);
            policy.setContextProperty(MULTITENANT_PROPERTY_DEFAULT);
        } else {
            policy.setContextProperty(m_contextProperty);
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
    public void setType(String type) {
        m_type = type;
    }
}
