/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite runs {@link EclipseLinkJPQLParserTests2_4} using JPQL grammars written for JPA
 * 2.1 and for EclipseLink 2.4.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({

    EclipseLinkJPQLParserTests2_4.class,

    CastExpressionTest.class,
    ExtractExpressionTest.class,
    ColumnExpressionTest.class,
    FunctionExpressionTest.class,
    OperatorExpressionTest.class,
    OrderByItemTest.class,
    RegexpExpressionTest.class,
    SQLExpressionTest.class,
    TableVariableDeclarationTest.class,
    UnionClauseTest.class
})
@RunWith(JPQLTestRunner.class)
public final class AllEclipseLinkJPQLParserTests2_4 {

    private AllEclipseLinkJPQLParserTests2_4() {
        super();
    }

    @JPQLGrammarTestHelper
    static JPQLGrammar[] buildJPQLGrammars() {
        return JPQLGrammarTools.allEclipseLinkJPQLGrammars(EclipseLinkVersion.VERSION_2_4);
    }
}
