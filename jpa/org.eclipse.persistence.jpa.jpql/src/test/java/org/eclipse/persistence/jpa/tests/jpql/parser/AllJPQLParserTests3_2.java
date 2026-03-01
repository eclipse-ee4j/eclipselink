/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This test suite runs {@link JPQLParserTests3_2} using JPQL grammar written for JPA 3.2.
 */
@Suite.SuiteClasses({
        JPQLParserTests3_2.class,
        CastExpressionTest.class,
        UnionClauseTest.class
})
@RunWith(JPQLTestRunner.class)
public class AllJPQLParserTests3_2 {

    private AllJPQLParserTests3_2() {
        super();
    }

    @JPQLGrammarTestHelper
    static JPQLGrammar[] buildJPQLGrammars() {
        return JPQLGrammarTools.allJPQLGrammars(JPAVersion.VERSION_3_2);
    }

}
