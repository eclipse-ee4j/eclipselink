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


/** This class has been modified as per instructions from Tom Ware and Development
 *  the test(), verify() are all inherited from the parent class
 *  We pass in the TopLink project and the string we are looking for and the superclass does the verification and testing
 */
public class GetClassExtractionMethodNameIsNotNullTest extends ProjectClassGeneratorResultFileTest {
    ClassDescriptor descriptorToModify;

    public GetClassExtractionMethodNameIsNotNullTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(),
              "descriptor.getInheritancePolicy().setClassExtractionMethodName(\"Testing getClassExractionMethodName()\");");
        setDescription("Test addInheritanceLines method ->  getClassExtractionMethodName != null");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        descriptorToModify =
                project.getDescriptors().get(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        descriptorToModify.getInheritancePolicy().setClassExtractionMethodName("Testing getClassExractionMethodName()");
    }
}
