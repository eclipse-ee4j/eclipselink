/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import junit.extensions.ActiveTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllModelTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllModelTests.class.getName()});
	}

	public static Test suite() {
		// *************************************************************************
		//   NOTE - NOTE - NOTE - NOTE - NOTE
		// run these tests in separate threads since they spend lots of time waiting on the database servers
		// *************************************************************************
		TestSuite suite = new ActiveTestSuite(ClassTools.packageNameFor(AllModelTests.class));

		suite.addTest(DatabasePlatformRepositoryTests.suite());
		suite.addTest(DatabasePlatformTests.suite());
		suite.addTest(DatabaseTypeTests.suite());
		suite.addTest(JavaTypeDeclarationTests.suite());
		suite.addTest(JDBCTypeRepositoryTests.suite());

		suite.addTest(Oracle10gR2Tests.suite());
		suite.addTest(Oracle10gTests.suite());
		suite.addTest(Oracle9iTests.suite());
//		suite.addTest(Oracle8iTests.suite());

		suite.addTest(DB2_8Tests.suite());
		suite.addTest(DB2_7Tests.suite());

		if (System.getProperty("os.name").indexOf("Windows") != -1) {
			suite.addTest(MSAccessTests.suite());
			// I commented out the dBASE tests since they don't test much of anything
			// and they require an additional ODBC datasource to be defined,
			// all for something that's probably not used much by customers  ~bjv
			// suite.addTest(DbaseTests.suite());
		}

		suite.addTest(Sybase12_5Tests.suite());
//		suite.addTest(Sybase12_0Tests.suite());

		suite.addTest(MySQL5Tests.suite());
		suite.addTest(MySQL4Tests.suite());

		return suite;
	}

	private AllModelTests() {
		super();
	}

}
