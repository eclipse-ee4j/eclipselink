/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.transparentindirection;

import java.util.*;

import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * configure Order descriptor to use IndirectLists
 */
public class CustomIndirectContainerProject extends IndirectListProject {

    public CustomIndirectContainerProject() {
        super();
        buildDogDescriptor();
    }

    protected void buildDogDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Dog.class);
        Vector vector = new Vector();
        vector.addElement("DOG");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("DOG.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("dog");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("DOG.ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("name");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("DOG.NAME");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: ONETOONEMAPPING
        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping.setAttributeName("owner");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.useContainerIndirection(SalesRepContainer.class);
        onetoonemapping.setReferenceClass(SalesRep.class);
        onetoonemapping.setIsPrivateOwned(false);
        onetoonemapping.addForeignKeyFieldName("DOG.SREP_ID", "SALEREP.ID");
        descriptor.addMapping(onetoonemapping);
        addDescriptor(descriptor);
    }
}
