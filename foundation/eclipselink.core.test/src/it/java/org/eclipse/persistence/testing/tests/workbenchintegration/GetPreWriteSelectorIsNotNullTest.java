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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/** This class has been modified as per instructions from Tom Ware and Development
 *  the test(), verify() are all inherited from the parent class
 *  We pass in the TopLink project and the string we are looking for and the superclass does the verification and testing
 */
public class GetPreWriteSelectorIsNotNullTest extends ProjectClassGeneratorResultFileTest {
    ClassDescriptor descriptorToModify;

    public GetPreWriteSelectorIsNotNullTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(),
              "descriptor.getEventManager().setPreWriteSelector(\"TestPreWrite\");");
        setDescription("Test addEventManagerPropertyLines method ->  getPreWriteSelector() != null");
    }

    @Override
    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        descriptorToModify = project.getDescriptors().get(Employee.class);
        descriptorToModify.getEventManager().setPreWriteSelector("TestPreWrite");
    }
}
