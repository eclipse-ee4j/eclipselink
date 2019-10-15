/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.testing.models.employee.relational.*;
import org.eclipse.persistence.sessions.factories.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;

public class DeploymentXMLAntlrDependencyTest extends AutoVerifyTestCase {
  public DeploymentXMLAntlrDependencyTest()  {
    setDescription("Tests that deployment XML does not require Antlr.jar for loading basic non-EJBQL containing projects.");
  }

    protected void setup() throws Exception {
        boolean jarsOnPath = true;
        try {
            Class.forName("antlr.LLkParser");
        } catch (Exception exception) {
            jarsOnPath = false;
        }
        if (jarsOnPath) {
            throw new TestProblemException("Antlr.jar must not be on the classpath for this test to run.");
        }
    }

  public void test() {
    EmployeeProject project = new EmployeeProject();
    ((DatasourceLogin)project.getDatasourceLogin()).usePlatform(OracleDBPlatformHelper.getInstance().getOracle9Platform());
    XMLProjectWriter.write("employee_project.xml", project);
    XMLProjectReader.read("employee_project.xml", getClass().getClassLoader());
  }
}
