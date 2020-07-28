/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite runs {@link EclipseLinkJPQLParserTests2_5} using JPQL grammars written for JPA
 * 2.1 and for EclipseLink 2.5.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuiteClasses({

    EclipseLinkJPQLParserTests2_5.class,

    // Extended support
    EclipseLinkInExpressionTest.class,

    // New support
    AsOfClauseTest.class,
    ConnectByClauseTest.class,
    HierarchicalQueryClauseTest.class,
    OrderSiblingsByClauseTest.class,
    StartWithClauseTest.class
})
@RunWith(JPQLTestRunner.class)
public final class AllEclipseLinkJPQLParserTests2_5 {

    private AllEclipseLinkJPQLParserTests2_5() {
        super();
    }

    @JPQLGrammarTestHelper
    static JPQLGrammar[] buildJPQLGrammars() {
        return JPQLGrammarTools.allEclipseLinkJPQLGrammars(EclipseLinkVersion.VERSION_2_5);
    }
}
