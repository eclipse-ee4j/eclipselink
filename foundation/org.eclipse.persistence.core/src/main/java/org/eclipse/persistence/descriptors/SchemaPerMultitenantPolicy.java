/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
//      19/12/2014 - 2.6.0 - Lukas Jungmann
//        - 455905: initial implementation
package org.eclipse.persistence.descriptors;

import org.eclipse.persistence.annotations.TenantTableDiscriminatorType;
import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * A schema per tenant multitenant policy.
 * This is a special case of TablePerMultitenantPolicy which can be applied
 * on the project. Can be useful in cases when tenant schema change happens
 * outside of EclipseLink, typically if Proxy DataSource is used.
 *
 * Use of this policy is limited to projects where following applies
 * <ul>
 * <li>{@literal @}Multitenant entities are not used
 * <li>EMF is shared across tenants
 * <li>EMF shared cache is disabled
 * <li>no {@literal @}Entity defines schema to be used
 * </ul>
 *
 * @author Lukas Jungmann
 * @since EclipseLink 2.6
 */
//TODO: refactor MTPolicies a bit
public class SchemaPerMultitenantPolicy extends TablePerMultitenantPolicy {

    private boolean useSharedCache = false;
    private boolean useSharedEMF = true;

    public SchemaPerMultitenantPolicy() {
        type = TenantTableDiscriminatorType.SCHEMA;
        contextProperty = EntityManagerProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT;
    }

    public SchemaPerMultitenantPolicy(ClassDescriptor desc) {
        this();
        descriptor = desc;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        // Add the context property to the session set.
        session.addMultitenantContextProperty(contextProperty);

        //update caching as needed
        if (shouldUseSharedEMF()) {
            for (ClassDescriptor classDescriptor : session.getProject().getDescriptors().values()) {
                if (shouldUseSharedCache()) {
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
            session.getProject().setHasIsolatedClasses(true);
        }
    }

    @Override
    public MultitenantPolicy clone(ClassDescriptor descriptor) {
        SchemaPerMultitenantPolicy clonedPolicy = null;
        try {
            clonedPolicy = (SchemaPerMultitenantPolicy) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.getMessage());
        }
        clonedPolicy.descriptor = descriptor;
        return clonedPolicy;
    }

    @Override
    public boolean isSchemaPerMultitenantPolicy() {
        return true;
    }

    //TODO: should not have to override following methods
    @Override
    protected void setTableSchemaPerTenant() {
        //do nothing, schema is set by the datasource
    }

    @Override
    public boolean isTablePerMultitenantPolicy() {
        return false;
    }

    @Override
    protected String getTableName(DatabaseTable table, String tenant) {
        return table.getName();
    }

    @Override
    public DatabaseTable getTable(DatabaseTable table) {
        return table;
    }

    @Override
    public DatabaseTable getTable(String tableName) {
        return new DatabaseTable(tableName);
    }

    @Override
    protected DatabaseTable updateTable(DatabaseTable table) {
        //there should be no way to get here but if there is then
        throw new UnsupportedOperationException();
    }

    //TODO: methods bellow should be part of parent class/iface
    public boolean shouldUseSharedEMF() {
        return useSharedEMF;
    }

    public void setShouldUseSharedEMF(boolean shouldUseSharedEMF) {
        this.useSharedEMF = shouldUseSharedEMF;
    }

    public boolean shouldUseSharedCache() {
        return useSharedCache;
    }

    public void setShouldUseSharedCache(boolean shouldUseSharedCache) {
        this.useSharedCache = shouldUseSharedCache;
    }
}
