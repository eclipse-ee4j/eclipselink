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

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;


public class ProjectClassGeneratorWithVariablesTest extends AutoVerifyTestCase {
    ProjectClassGenerator generator;
    Project project;
    boolean foundException;
    Writer testWriter = new StringWriter();
    String TestWithThreeVar = "TestWIthThreeVar";

    public ProjectClassGeneratorWithVariablesTest() {
        super();
        setDescription("Test if ProjectTestGenerator generates correct file when we use the overloaded constructor with 3 variables");
    }

    public void reset() {
        File file = new File(generator.getOutputFileName());
        file.delete();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        project = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
    }

    public void test() {
        try {
            generator = new ProjectClassGenerator(project, TestWithThreeVar, testWriter);
            generator.generate();
        } catch (Exception e) {
            foundException = true;
        }
    }

    protected void verify() {
        if (!(generator.getClassName()).equals(TestWithThreeVar)) {
            throw new TestErrorException("Project class on " + generator.getOutputFileName() +
                                         " has not been set");
        }
        if (foundException) {
            throw new TestErrorException("Exception has been thrown in ProjectClassGeneratorTest");
        }
    }
}
