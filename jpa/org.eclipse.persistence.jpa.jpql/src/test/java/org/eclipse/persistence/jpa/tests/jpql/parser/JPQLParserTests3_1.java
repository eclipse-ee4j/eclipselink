package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.runners.Suite;

/**
 * This test suite contains a series of unit-tests that test parsing JPQL queries that follows the
 * JPQL grammar defined in JPA 3.1.
 */
@Suite.SuiteClasses({
        // Test the parser with JPQL queries
        JPQLQueriesTest3_1.class,

})
public class JPQLParserTests3_1 {

    private JPQLParserTests3_1() {
        super();
    }
}
