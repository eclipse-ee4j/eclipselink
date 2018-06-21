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

import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class BuildConstructorPorjectsDefaultReadOnlyClassesIsNotEmptyTest extends ProjectClassGeneratorResultFileTest {
    public BuildConstructorPorjectsDefaultReadOnlyClassesIsNotEmptyTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(),
              "setDefaultReadOnlyClasses(buildDefaultReadOnlyClasses());");
        setDescription("Test buildConstructor() -> project.getDefaultReadOnlyClasses() is not Empty");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        project.addDefaultReadOnlyClass(Employee.class);
    }
}
