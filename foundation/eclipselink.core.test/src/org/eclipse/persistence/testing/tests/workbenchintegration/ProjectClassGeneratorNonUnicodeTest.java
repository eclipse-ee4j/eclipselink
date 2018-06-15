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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;


//Check if ProjectClassGenerator does not generate unicode escaped characters for non-ASCII
//characters when false is passed in to generate().  Compile on the generated project file
//should fail.

// This test has been removed from the test suite because the IBM VM on the eclipse build server
// does not write the unicode characters in the expected manner.  Instead of ??Mapping, we get the
// actual unicode characters.  This may be because the VM is running on a 64 bit linux server
public class ProjectClassGeneratorNonUnicodeTest extends ProjectClassGeneratorResultFileTest {

    public ProjectClassGeneratorNonUnicodeTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(), "??Mapping");
        setDescription("Test if ProjectClassGenerator does not generate unicode escaped characters for non-ASCII characters");
    }

    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        project.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getMappingForAttributeName("firstName").setAttributeName("\u5E08\u592B");
    }

    public void test() {
        try {
            ProjectClassGenerator generator = new ProjectClassGenerator(project);
            generator.generate(false);
            fileName = generator.getOutputFileName();
        } catch (Exception exception) {
            generationException = exception;
        }
    }

}
