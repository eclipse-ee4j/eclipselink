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
package org.eclipse.persistence.jpa.tests.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_5;
import org.eclipse.persistence.jpa.jpql.tools.model.EclipseLinkJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.JPQLQueryBuilder1_0;
import org.eclipse.persistence.jpa.jpql.tools.model.JPQLQueryBuilder2_0;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The test suite containing the unit-tests testing the manual creation of the {@link StateObject}
 * representation of a JPQL query defined in JPA version 1.0.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
    ManualCreationStateObjectTest1_0.class
})
@RunWith(JPQLTestRunner.class)
public final class AllManualCreationStateObjectTest1_0 {

    private AllManualCreationStateObjectTest1_0() {
        super();
    }

    @IJPQLQueryBuilderTestHelper
    static IJPQLQueryBuilder[] buildJPQLQueryBuilders() {
        return new IJPQLQueryBuilder[] {
            new JPQLQueryBuilder1_0(),
            new JPQLQueryBuilder2_0(),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar1.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_0.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_1.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_2.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_3.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_4.instance()),
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_5.instance())
        };
    }
}
