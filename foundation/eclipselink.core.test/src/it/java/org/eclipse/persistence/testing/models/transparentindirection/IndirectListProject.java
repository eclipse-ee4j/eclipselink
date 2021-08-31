/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.transparentindirection;

import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * configure Order descriptor to use IndirectLists
 */
public class IndirectListProject extends IndirectContainerProject {
    public IndirectListProject() {
        super();
    }

    @Override
    protected void configureContactContainer(org.eclipse.persistence.mappings.DirectCollectionMapping directcollectionmapping) {
        directcollectionmapping.useCollectionClass(java.util.Vector.class);
    }

    @Override
    protected void configureLineContainer(org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping) {
        onetomanymapping.useCollectionClass(java.util.Vector.class);
    }

    @Override
    protected void configureSalesRepContainer(org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping) {
        manytomanymapping.useCollectionClass(java.util.Vector.class);
    }

    @Override
    protected void modifyOrderDescriptor(RelationalDescriptor descriptor) {
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("salesReps")).useTransparentCollection();
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("contacts")).useTransparentCollection();
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("lines")).useTransparentCollection();
    }

    @Override
    protected Class orderClass() {
        return org.eclipse.persistence.testing.models.transparentindirection.Order.class;
    }

    @Override
    protected Class orderLineClass() {
        return org.eclipse.persistence.testing.models.transparentindirection.OrderLine.class;
    }

    @Override
    protected Class salesRepClass() {
        return org.eclipse.persistence.testing.models.transparentindirection.SalesRep.class;
    }
}
