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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_5;
import org.eclipse.persistence.jpa.jpql.tools.model.EclipseLinkJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.JPQLQueryBuilder2_1;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.eclipse.persistence.jpa.tests.jpql.tools.model.IJPQLQueryBuilderTestHelper;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite containing the unit-tests testing the refactoring functionality with JPA 2.1.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
    RefactoringToolTest2_1.class
})
@RunWith(JPQLTestRunner.class)
public final class AllRefactoringToolTest2_1 {

    private AllRefactoringToolTest2_1() {
        super();
    }

    @IJPQLQueryBuilderTestHelper
    static IJPQLQueryBuilder[] buildJPQLQueryBuilders() {
        return new IJPQLQueryBuilder[] {
            new JPQLQueryBuilder2_1(),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_4.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_5.instance())
        };
    }
}
