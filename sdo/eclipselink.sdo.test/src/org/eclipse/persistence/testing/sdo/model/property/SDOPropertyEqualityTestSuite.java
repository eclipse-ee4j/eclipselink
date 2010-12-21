package org.eclipse.persistence.testing.sdo.model.property;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SDOPropertyEqualityTestSuite extends TestCase {
    public SDOPropertyEqualityTestSuite(String name) {
        super(name);
    }

    /**
     * Inherited suite method for generating all test cases.
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("SDOProperty Equality Test Suite");
        suite.addTestSuite(SDOPropertyEqualityTests.class);
        return suite;
    }    
}