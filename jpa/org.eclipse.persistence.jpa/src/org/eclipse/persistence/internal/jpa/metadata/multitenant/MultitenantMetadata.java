/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     04/01/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 2)
//     04/05/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 3)
//     06/30/2011-2.3.1 Guy Pelletier
//       - 341940: Add disable/enable allowing native queries
//     08/18/2011-2.3.1 Guy Pelletier
//       - 355093: Add new 'includeCriteria' flag to Multitenant metadata
//     09/09/2011-2.3.1 Guy Pelletier
//       - 356197: Add new VPD type to MultitenantType
//     09/20/2011-2.3.1 Guy Pelletier
//       - 357476: Change caching default to ISOLATED for multitenant's using a shared EMF.
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.multitenant;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumns;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;
import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.MultitenantPolicy;
import org.eclipse.persistence.descriptors.SingleTableMultitenantPolicy;
import org.eclipse.persistence.descriptors.TablePerMultitenantPolicy;
import org.eclipse.persistence.descriptors.VPDMultitenantPolicy;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.TenantDiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * Object to hold onto multi-tenant metadata.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.3
 */
public class MultitenantMetadata extends ORMetadata {
    private Boolean m_includeCriteria;
    private List<TenantDiscriminatorColumnMetadata> m_tenantDiscriminatorColumns = new ArrayList<TenantDiscriminatorColumnMetadata>();
    private String m_type;
    private TenantTableDiscriminatorMetadata m_tenantTableDiscriminator;

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

        m_type = multitenant.getAttributeString("value");
        m_includeCriteria = multitenant.getAttributeBooleanDefaultTrue("includeCriteria");

        // Look for a @TenantDiscriminators
        if (accessor.isAnnotationPresent(TenantDiscriminatorColumns.class)) {
            for (Object tenantDiscriminatorColumn : accessor.getAnnotation(TenantDiscriminatorColumns.class).getAttributeArray("value")) {
                m_tenantDiscriminatorColumns.add(new TenantDiscriminatorColumnMetadata((MetadataAnnotation) tenantDiscriminatorColumn, accessor));
            }
        }

        // Look for a @TenantDiscriminator.
        if (accessor.isAnnotationPresent(TenantDiscriminatorColumn.class)) {
            m_tenantDiscriminatorColumns.add(new TenantDiscriminatorColumnMetadata(accessor.getAnnotation(TenantDiscriminatorColumn.class), accessor));
        }

        // Look for a @TenantTableDiscriminator.
        if (accessor.isAnnotationPresent(TenantTableDiscriminator.class)) {
            m_tenantTableDiscriminator = new TenantTableDiscriminatorMetadata(accessor.getAnnotation(TenantTableDiscriminator.class), accessor);
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

            if (! valuesMatch(m_includeCriteria, multitenant.getIncludeCriteria())) {
                return false;
            }

            if (! valuesMatch(m_tenantTableDiscriminator, multitenant.getTenantTableDiscriminator())) {
                return false;
            }

            return valuesMatch(m_tenantDiscriminatorColumns, multitenant.getTenantDiscriminatorColumns());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_includeCriteria != null ? m_includeCriteria.hashCode() : 0;
        result = 31 * result + (m_tenantDiscriminatorColumns != null ? m_tenantDiscriminatorColumns.hashCode() : 0);
        result = 31 * result + (m_type != null ? m_type.hashCode() : 0);
        result = 31 * result + (m_tenantTableDiscriminator != null ? m_tenantTableDiscriminator.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getIncludeCriteria() {
        return m_includeCriteria;
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
    public TenantTableDiscriminatorMetadata getTenantTableDiscriminator() {
        return m_tenantTableDiscriminator;
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
    public boolean includeCriteria() {
        if (m_type != null && m_type.equals(MultitenantType.VPD.name())) {
            return false ;
        } else {
            return m_includeCriteria == null || m_includeCriteria.booleanValue();
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize single objects.
        initXMLObject(m_tenantTableDiscriminator, accessibleObject);

        // Initialize lists of objects.
        initXMLObjects(m_tenantDiscriminatorColumns, accessibleObject);
    }

    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor)  {
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();

        MultitenantPolicy policy;

        if (m_type == null || m_type.equals(MultitenantType.SINGLE_TABLE.name()) || m_type.equals(MultitenantType.VPD.name())) {
            if (m_type == null || m_type.equals(MultitenantType.SINGLE_TABLE.name())) {
                policy = new SingleTableMultitenantPolicy(classDescriptor);

                // As soon as we find one entity that is multitenant, turn off
                // native SQL queries Users can set the property on their
                // persistence unit if they want it back on. Or per query.
                getProject().setAllowNativeSQLQueries(false);
            } else {
                policy = new VPDMultitenantPolicy(classDescriptor);

                // Within VPD, we must ensure we are using an Always exclusive mode.
                ((ServerSession) getProject().getSession()).getDefaultConnectionPolicy().setExclusiveMode(ConnectionPolicy.ExclusiveMode.Always);
            }

            // Set the include criteria flag on the query manager (in VPD this will be false).
            ((SingleTableMultitenantPolicy) policy).setIncludeTenantCriteria(includeCriteria());

            // Single table multi-tenancy (perhaps using VPD).
            processTenantDiscriminators(descriptor, (SingleTableMultitenantPolicy) policy);
        } else {
            // Initialize the policy.
            policy = new TablePerMultitenantPolicy(classDescriptor);

            // Process the tenant table discriminator.
            processTenantTableDiscriminator(descriptor, (TablePerMultitenantPolicy) policy);
        }

        // Set the policy on the descriptor.
        classDescriptor.setMultitenantPolicy(policy);

        // If the intention of the user is to use a shared emf, we must
        // set the cache isolation type based on the multitenant shared
        // cache property. If we are using a shared cache then clearly
        // we are sharing an EMF.
        if (getProject().usesMultitenantSharedEmf()) {
            if (getProject().usesMultitenantSharedCache()) {
                // Even though it is a shared cache we don't want to
                // override an explicit ISOLATED setting from the user.
                // Caching details are processed before multitenant metadata.
                if (classDescriptor.isSharedIsolation()) {
                    classDescriptor.setCacheIsolation(CacheIsolationType.PROTECTED);
                }
            } else {
                classDescriptor.setCacheIsolation(CacheIsolationType.ISOLATED);
            }
        }
    }

    /**
     * INTERNAL:
     * Process the tenant discriminator metadata.
     */
    protected void processTenantDiscriminators(MetadataDescriptor descriptor, SingleTableMultitenantPolicy policy) {
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
                m_tenantDiscriminatorColumns.add(new TenantDiscriminatorColumnMetadata(descriptor.getClassAccessor()));
            } else {
                // For PU defaulted columns we must initialize them with our
                // context.
                for (TenantDiscriminatorColumnMetadata tenantDiscriminator : m_tenantDiscriminatorColumns) {
                    tenantDiscriminator.setAccessibleObject(getAccessibleObject());
                    tenantDiscriminator.setProject(getProject());
                }
            }
        }

        // Process the tenant discriminators now.
        for (TenantDiscriminatorColumnMetadata tenantDiscriminator : m_tenantDiscriminatorColumns) {
            tenantDiscriminator.process(descriptor, policy);
        }
    }

    /**
     * INTERNAL:
     * Process the tenant table discriminator metadata.
     */
    protected void processTenantTableDiscriminator(MetadataDescriptor descriptor, TablePerMultitenantPolicy policy) {
        // Check for tenant discriminator columns from a parent class.
        if (descriptor.isInheritanceSubclass()) {
            // If we are an inheritance subclass, our parent will have been
            // processed and we only care about a tenant table discriminator
            // if we are part of a TABLE_PER_CLASS setting.
            EntityAccessor parentAccessor = descriptor.getInheritanceRootDescriptor().getEntityAccessor();

            if (! parentAccessor.getInheritance().usesTablePerClassStrategy()) {
                // If we are a JOINED or SINGLE_TABLE strategy, just verify the
                // user has not specified a table discriminator on the subclass.
                if (m_tenantTableDiscriminator != null) {
                    getLogger().logWarningMessage(MetadataLogger.IGNORE_INHERITANCE_TENANT_TABLE_DISCRIMINATOR, descriptor.getJavaClass());
                }

                return;
            }
        }

        // Process the tenant table discriminator or default one.
        // Future: add a default tenant table discrimintor (pu defaults or
        // entity mappings level. See how implemented for tenant discriminator columns)
        if (m_tenantTableDiscriminator != null) {
            m_tenantTableDiscriminator.process(descriptor, (TablePerMultitenantPolicy) policy);
        } else {
            new TenantTableDiscriminatorMetadata(descriptor.getClassAccessor()).process(descriptor, (TablePerMultitenantPolicy) policy);
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setIncludeCriteria(Boolean includeCriteria) {
        m_includeCriteria = includeCriteria;
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
    public void setTenantTableDiscriminator(TenantTableDiscriminatorMetadata tenantTableDiscriminator) {
        m_tenantTableDiscriminator = tenantTableDiscriminator;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setType(String type) {
        m_type = type;
    }
}
