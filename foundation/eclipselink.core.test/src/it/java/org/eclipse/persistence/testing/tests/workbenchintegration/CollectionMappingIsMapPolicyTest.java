/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;


/** This class has been modified as per instructions from Tom Ware and Development
 *  the test(), verify() are all inherited from the parent class
 *  We pass in the TopLink project and the string we are looking for and the superclass does the verification and testing
 */
public class CollectionMappingIsMapPolicyTest extends ProjectClassGeneratorResultFileTest {
    ClassDescriptor descriptorToModify;
    DatabaseMapping mappingToModify;
    MapContainerPolicy policy;

    public CollectionMappingIsMapPolicyTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(),
              ".useMapClass(java.util.Vector.class,");
        setDescription("Test addForeignReferenceMappingLines method ->   isMapPolicy is true");
    }

    @Override
    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        descriptorToModify = project.getDescriptors().get(Employee.class);
        policy = new MapContainerPolicy();
        for (DatabaseMapping databaseMapping : descriptorToModify.getMappings()) {
            mappingToModify = databaseMapping;

            if (mappingToModify.isForeignReferenceMapping()) {
                if (mappingToModify.isCollectionMapping()) {
                    CollectionMapping collectionMapping =
                            (CollectionMapping) mappingToModify;
                    collectionMapping.setContainerPolicy(policy);
                    policy.setKeyName("testMethod");
                    collectionMapping.getContainerPolicy().setContainerClass(Vector.class);
                }
            }
        }
    }
}
