/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.transparentindirection;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.indirection.IndirectSet;

/**
 * configure Order descriptor to use IndirectSets
 * this should only be used in jdk1.2+
 */
public class IndirectSetProject extends IndirectContainerProject {
    public IndirectSetProject() {
        super();
    }

    protected void configureContactContainer(org.eclipse.persistence.mappings.DirectCollectionMapping directcollectionmapping) {
        directcollectionmapping.useCollectionClass(java.util.Vector.class);
    }

    protected void configureLineContainer(org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping) {
        onetomanymapping.useCollectionClass(java.util.Vector.class);
    }

    protected void configureSalesRepContainer(org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping) {
        manytomanymapping.useCollectionClass(java.util.Vector.class);
    }

    protected void modifyOrderDescriptor(RelationalDescriptor descriptor) {
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("salesReps")).useTransparentCollection();
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("salesReps")).useCollectionClass(IndirectSet.class);

        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("contacts")).useTransparentCollection();
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("contacts")).useCollectionClass(IndirectSet.class);

        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("lines")).useTransparentCollection();
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("lines")).useCollectionClass(IndirectSet.class);
    }

    protected Class orderClass() {
        return org.eclipse.persistence.testing.models.transparentindirection.SetOrder.class;
    }

    public Class orderLineClass() {
        return org.eclipse.persistence.testing.models.transparentindirection.SetOrderLine.class;
    }

    protected Class salesRepClass() {
        return org.eclipse.persistence.testing.models.transparentindirection.SetSalesRep.class;
    }
}
