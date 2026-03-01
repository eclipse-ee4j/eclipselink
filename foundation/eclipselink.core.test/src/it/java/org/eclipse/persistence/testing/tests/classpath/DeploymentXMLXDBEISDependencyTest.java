/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.classpath;

import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;

public class DeploymentXMLXDBEISDependencyTest extends AutoVerifyTestCase {

    public DeploymentXMLXDBEISDependencyTest() {
        setDescription("Tests that deployment XML does not require EIS or XDB depend jars for loading basic projects.");
    }

    @Override
    protected void setup() throws Exception {
        boolean jarsOnPath = true;
        try {
            Class.forName("jakarta.resource.ResourceException");
        } catch (Exception exception) {
            jarsOnPath = false;
        }
        if (jarsOnPath) {
            throw new TestProblemException("connector.jar must not be on the classpath for this test to run.");
        }
        jarsOnPath = true;
        try {
            Class.forName("oracle.xdb.XMLType");
        } catch (Exception exception) {
            jarsOnPath = false;
        }
        if (jarsOnPath) {
            throw new TestProblemException("xdb.jar must not be on the classpath for this test to run.");
        }
    }

    @Override
    public void test() {
        XMLProjectWriter.write("employee_project.xml", new EmployeeProject());
        XMLProjectReader.read("employee_project.xml", getClass().getClassLoader());
    }
}
