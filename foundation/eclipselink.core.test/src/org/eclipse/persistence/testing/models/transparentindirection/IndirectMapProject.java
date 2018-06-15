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

/**
 * configure Order descriptor to use IndirectMaps
 */
public class IndirectMapProject extends IndirectContainerProject {
    public IndirectMapProject() {
        super();
    }

    protected void configureContactContainer(org.eclipse.persistence.mappings.DirectCollectionMapping directcollectionmapping) {
        directcollectionmapping.useCollectionClass(java.util.Vector.class);
    }

    protected void configureLineContainer(org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping) {
        onetomanymapping.useMapClass(java.util.Hashtable.class, "getKey");
    }

    protected void configureSalesRepContainer(org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping) {
        manytomanymapping.useMapClass(java.util.Hashtable.class, "getKey");
    }

    protected void modifyOrderDescriptor(RelationalDescriptor descriptor) {
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("salesReps")).useTransparentMap("getKey");
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("contacts")).useTransparentCollection();
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptor.getMappingForAttributeName("lines")).useTransparentMap("getKey");
    }

    public Class orderClass() {
        return org.eclipse.persistence.testing.models.transparentindirection.MappedOrder.class;
    }

    public Class orderLineClass() {
        return org.eclipse.persistence.testing.models.transparentindirection.MappedOrderLine.class;
    }

    protected Class salesRepClass() {
        return org.eclipse.persistence.testing.models.transparentindirection.MappedSalesRep.class;
    }
}
