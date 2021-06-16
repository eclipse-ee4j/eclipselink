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
//     John Vandale - initial API and implementation
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.InheritancePolicy;

/**
 * Test that the class indicator type for inheritence mappings uses the proper constructor
 * for Byte, Long, Short, etc. types - Bug 298443
 *
 * @author John Vandale
 * @version 1.0
 * @date Jan 20, 2010
 */
public class ProjectClassGeneratorInheritanceMappingTest extends ProjectClassGeneratorResultFileTest {

    public ProjectClassGeneratorInheritanceMappingTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject());
    }

    /**
     * Setup what we want written out.
     */
    public void setup() {
        InheritancePolicy iPolicy = project.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Project.class).getInheritancePolicy();

        // Inheritance Properties.
        iPolicy.addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class, new Byte("1"));
        iPolicy.addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.LargeProject.class, new Short("2"));
        iPolicy.addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.Project.class, new Long("3"));
        iPolicy.addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class, new Double("4.0"));
    }

    /**
     * Verify the Byte(String value) constructor is generated.
     */
    public void verify() {
        testString = "descriptor.getInheritancePolicy().addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class, new java.lang.Byte(\"1\"));";
        super.verify();
        testString = "descriptor.getInheritancePolicy().addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.LargeProject.class, new java.lang.Short(\"2\"));";
        super.verify();
        testString = "descriptor.getInheritancePolicy().addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.Project.class, new java.lang.Long(\"3\"));";
        super.verify();
        testString = "descriptor.getInheritancePolicy().addClassIndicator(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class, new java.lang.Double(\"4.0\"));";
        super.verify();
    }

}
