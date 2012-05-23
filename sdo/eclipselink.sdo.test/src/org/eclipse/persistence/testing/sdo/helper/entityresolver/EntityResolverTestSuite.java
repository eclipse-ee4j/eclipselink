package org.eclipse.persistence.testing.sdo.helper.entityresolver;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test use of org.eclipse.persistence.testing.sdo.helper.SchemaResolver implementation as an entity resolver.
 */
public class EntityResolverTestSuite extends TestCase {
    public EntityResolverTestSuite(String name) {
        super(name);
    }

    /**
     * Inherited suite method for generating all test cases.
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Entity Resolver Test Suite");
        suite.addTestSuite(EntityResolverTestCases.class);
        return suite;
    }
}
