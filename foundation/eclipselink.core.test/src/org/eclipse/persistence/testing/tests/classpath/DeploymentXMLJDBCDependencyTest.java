/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.classpath;

import org.eclipse.persistence.testing.models.employee.relational.*;
import org.eclipse.persistence.sessions.factories.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;

public class DeploymentXMLJDBCDependencyTest extends AutoVerifyTestCase {
    public DeploymentXMLJDBCDependencyTest() {
		setDescription("Tests that deployment XML does not require jdbc jar for loading basic projects.");
    }

	protected void setup() throws Exception {
		boolean jarsOnPath = true;
		try {
			Class.forName("oracle.sql.TIMESTAMP");
		} catch (Exception exception) {
			jarsOnPath = false;
		}
		if (jarsOnPath) {
			throw new TestProblemException("jdbc jar must not be on the classpath for this test to run.");
		}
	}

	public void test() {
        EmployeeProject project = new EmployeeProject();
        ((DatasourceLogin)project.getDatasourceLogin()).usePlatform(OracleDBPlatformHelper.getInstance().getOracle9Platform());
		XMLProjectWriter.write("employee_project.xml", project);
		XMLProjectReader.read("employee_project.xml", getClass().getClassLoader());
    }
}
