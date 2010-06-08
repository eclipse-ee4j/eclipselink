package org.eclipse.persistence.testing.sdo.helper.xsdhelper;

import org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.XSDHelperDefineTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.XSDHelperGenerateTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOXSDHelperTestSuite {
    public SDOXSDHelperTestSuite() {
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XSDHelper Tests");
        suite.addTest(new XSDHelperDefineTestSuite().suite());
        suite.addTest(new XSDHelperGenerateTestSuite().suite());
        suite.addTestSuite(SDOXSDHelperExceptionTestCases.class);
        return suite;
    }
}
