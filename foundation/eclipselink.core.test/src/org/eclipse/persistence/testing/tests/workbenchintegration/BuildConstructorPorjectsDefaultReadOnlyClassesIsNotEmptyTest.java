/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
