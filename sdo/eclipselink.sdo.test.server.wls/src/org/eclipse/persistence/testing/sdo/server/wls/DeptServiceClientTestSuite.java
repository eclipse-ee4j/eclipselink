package org.eclipse.persistence.testing.sdo.server.wls;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class DeptServiceClientTestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("WebLogic server tests - DeptService");
        suite.addTest(new TestSuite(DeptServiceClientTestCases.class));
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.server.wls.DeptServiceClientTestSuite" };
        TestRunner.main(arguments);
    }
}
