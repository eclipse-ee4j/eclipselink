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
//     John Vandale - initial API and implementation
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.InheritancePolicy;

/**
 * Test that the class indicator type for inheritence mappings uses the proper constructor
 * for Byte, Long, Short, etc. types - Bug 298443
 *
 * @author John Vandale
 * @version 1.0
 */
public class ProjectClassGeneratorInheritanceMappingTest extends ProjectClassGeneratorResultFileTest {

    public ProjectClassGeneratorInheritanceMappingTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject());
    }

    /**
     * Setup what we want written out.
     */
    @Override
    public void setup() {
        InheritancePolicy iPolicy = project.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Project.class).getInheritancePolicy();

        // Inheritance Properties.
        iPolicy.addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class, Byte.valueOf("1"));
        iPolicy.addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.LargeProject.class, Short.valueOf("2"));
        iPolicy.addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.Project.class, Long.valueOf("3"));
        iPolicy.addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class, Double.valueOf("4.0"));
    }

    /**
     * Verify the Byte(String value) constructor is generated.
     */
    @Override
    public void verify() {
        testString = "descriptor.getInheritancePolicy().addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class, Byte.valueOf(\"1\"));";
        super.verify();
        testString = "descriptor.getInheritancePolicy().addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.LargeProject.class, Short.valueOf(\"2\"));";
        super.verify();
        testString = "descriptor.getInheritancePolicy().addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.Project.class, Long.valueOf(\"3\"));";
        super.verify();
        testString = "descriptor.getInheritancePolicy().addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class, Double.valueOf(\"4.0\"));";
        super.verify();
    }

}
