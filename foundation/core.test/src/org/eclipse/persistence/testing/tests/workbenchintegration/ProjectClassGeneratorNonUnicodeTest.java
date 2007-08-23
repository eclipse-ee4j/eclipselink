/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;


//Check if ProjectClassGenerator does not generate unicode escaped characters for non-ASCII
//characters when false is passed in to generate().  Compile on the generated project file 
//should fail.
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
