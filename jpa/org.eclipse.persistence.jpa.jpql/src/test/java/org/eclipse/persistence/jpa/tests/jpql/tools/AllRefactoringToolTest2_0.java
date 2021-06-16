/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_5;
import org.eclipse.persistence.jpa.jpql.tools.model.EclipseLinkJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.JPQLQueryBuilder2_0;
import org.eclipse.persistence.jpa.jpql.tools.model.JPQLQueryBuilder2_1;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.eclipse.persistence.jpa.tests.jpql.tools.model.IJPQLQueryBuilderTestHelper;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The root test suite containing the unit-tests testing the refactoring functionality when the JPA
 * version is 2.0.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
    RefactoringToolTest2_0.class
})
@RunWith(JPQLTestRunner.class)
public final class AllRefactoringToolTest2_0 {

    private AllRefactoringToolTest2_0() {
        super();
    }

    @IJPQLQueryBuilderTestHelper
    static IJPQLQueryBuilder[] buildJPQLQueryBuilders() {
        return new IJPQLQueryBuilder[] {
            new JPQLQueryBuilder2_0(),
            new JPQLQueryBuilder2_1(),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_0.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_1.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_2.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_3.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_4.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_5.instance()),
        };
    }
}
