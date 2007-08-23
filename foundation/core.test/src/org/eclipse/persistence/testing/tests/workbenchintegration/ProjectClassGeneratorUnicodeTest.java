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

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;


//Check if ProjectClassGenerator generates unicode escaped characters for non-ASCII
//characters properly by not passing in a boolean to generate().
public class ProjectClassGeneratorUnicodeTest extends AutoVerifyTestCase {

    public static String PROJECT_FILE = "ProjectClassGeneratorUnicodeProject";
    DatabaseMapping unicodeMap;
    Project unicodeProject;

    public ProjectClassGeneratorUnicodeTest() {
        setDescription("Test if ProjectClassGenerator generates unicode escaped characters for non-ASCII characters properly");
    }

    protected void setup() throws Exception {
        org.eclipse.persistence.sessions.Project initialProject = 
            new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
        initialProject.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getMappingForAttributeName("firstName").setAttributeName("\u5E08\u592B");
        ProjectClassGenerator generator = 
            new ProjectClassGenerator(initialProject, PROJECT_FILE, PROJECT_FILE + ".java");
        generator.generate();

        try {
            String[] source = { PROJECT_FILE + ".java" };
            int result = new com.sun.tools.javac.Main().compile(source);
            if (result != 0) {
                throw new TestErrorException("Project class generation compile failed.");
            }
            Class projectClass = Class.forName(PROJECT_FILE);
            unicodeProject = (org.eclipse.persistence.sessions.Project)projectClass.newInstance();
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
    }

    public void test() {
        unicodeMap = 
                unicodeProject.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getMappingForAttributeName("\u5E08\u592B");
    }

    protected void verify() {
        if (unicodeMap == null) {
            throw new TestErrorException("Mapping for unicode does not exist after written out and read in from project class");
        }
    }
}
