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
package org.eclipse.persistence.testing.tests.classpath;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.relational.*;
import org.eclipse.persistence.sessions.factories.*;

public class DeploymentXMLXDBEISDependencyTest extends AutoVerifyTestCase {

    public DeploymentXMLXDBEISDependencyTest() {
        setDescription("Tests that deployment XML does not require EIS or XDB depend jars for loading basic projects.");
    }

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

    public void test() {
        XMLProjectWriter.write("employee_project.xml", new EmployeeProject());
        XMLProjectReader.read("employee_project.xml", getClass().getClassLoader());
    }
}
