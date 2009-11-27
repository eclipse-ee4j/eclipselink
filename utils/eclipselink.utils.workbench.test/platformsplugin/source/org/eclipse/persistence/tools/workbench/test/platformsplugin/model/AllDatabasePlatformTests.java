package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import junit.extensions.ActiveTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * Note these tests are not included in the main suite as they are platform specific.  
 * To be run they must be run separately.
 * @author lddavis
 *
 */
public class AllDatabasePlatformTests {

	public static Test suite() {
		TestSuite suite = new ActiveTestSuite(ClassTools.packageNameFor(AllModelTests.class));

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
}
