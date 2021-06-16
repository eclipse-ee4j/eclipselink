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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.io.File;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;


public class GettersAndSettersForProjectClassGeneratorTest extends AutoVerifyTestCase {
    ProjectClassGenerator generator;
    Project project;
    String path = "testPath";
    String className = "EclipseLink";
    boolean foundException;

    public GettersAndSettersForProjectClassGeneratorTest() {
        super();
        setDescription("The setters and getters for ProjectClassGenerator ");
    }

    public void reset() {
        File file = new File(generator.getOutputFileName());
        file.delete();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        project = null;
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        project = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
        generator = new ProjectClassGenerator();
    }

    public void test() {
        try {
            generator.setProject(project);
            generator.setOutputPath(path);
            generator.setClassName(className);
            generator.generate();
        } catch (Exception e) {
            foundException = true;
        }
    }

    protected void verify() {
        Project projectTest = generator.getProject();
        String classTest = generator.getClassName();
        String pathTest = generator.getOutputPath();
        if (!(projectTest.getName().equals(project.getName()))) {
            throw new TestErrorException("Project name not set");
        }

        if (!(classTest.equals(className))) {
            throw new TestErrorException("ClassName not set");
        }

        if (!(pathTest.equals(path))) {
            throw new TestErrorException("Path not set");
        }

        if (foundException) {
            throw new TestErrorException("Exception has been thrown in IsTypeConversionMappingTest");
        }
    }
}
