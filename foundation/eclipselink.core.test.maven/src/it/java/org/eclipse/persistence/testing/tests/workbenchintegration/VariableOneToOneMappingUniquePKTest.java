/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class VariableOneToOneMappingUniquePKTest extends ProjectClassGeneratorResultFileTest {
    ClassDescriptor descriptorToModify;
    DatabaseMapping mappingToModify;

    public VariableOneToOneMappingUniquePKTest() {
        //also tested for this String -> "testMapping.addTargetForeignQueryKeyName("testTargerFKQueryName", "testSourcePKFieldName");" Both are present!
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(),
              "testMapping.addForeignQueryKeyName(\"T_ID\", \"id\");");
        setDescription("Test addVariableOneToOneMappingLines method -> using unique PK ");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        descriptorToModify = project.getDescriptors().get(Employee.class);
        VariableOneToOneMapping typeMapping = new VariableOneToOneMapping();
        typeMapping.setAttributeName("test");
        typeMapping.setReferenceClass(Employee.class);
        typeMapping.setForeignQueryKeyName("T_ID", "id");
        typeMapping.addTargetForeignQueryKeyName("testTargerFKQueryName", "testSourcePKFieldName");
        typeMapping.dontUseIndirection();
        typeMapping.privateOwnedRelationship();
        descriptorToModify.addMapping(typeMapping);
    }
}
