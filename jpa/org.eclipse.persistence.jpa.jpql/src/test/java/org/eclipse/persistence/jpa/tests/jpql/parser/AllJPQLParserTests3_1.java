package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This test suite runs {@link JPQLParserTests3_1} using JPQL grammars written for JPA 3.1.

 */
@Suite.SuiteClasses({
        JPQLParserTests3_1.class
})
@RunWith(JPQLTestRunner.class)
public class AllJPQLParserTests3_1 {
    private AllJPQLParserTests3_1() {
        super();
    }

    @JPQLGrammarTestHelper
    static JPQLGrammar[] buildJPQLGrammars() {
        return JPQLGrammarTools.allJPQLGrammars(JPAVersion.VERSION_3_1);
    }

}
