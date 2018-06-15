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
package org.eclipse.persistence.jpa.tests.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_5;
import org.eclipse.persistence.jpa.jpql.tools.model.DefaultActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.DefaultJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.EclipseLinkActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.EclipseLinkJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.EclipseLinkJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryFormatter.IdentifierStyle;
import org.eclipse.persistence.jpa.jpql.tools.model.JPQLQueryBuilder2_0;
import org.eclipse.persistence.jpa.jpql.tools.model.JPQLQueryBuilder2_1;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite containing the unit-tests testing {@link StateObject} API when the JPA version is 2.0.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
    StateObjectTest2_0.class,
})
@RunWith(JPQLTestRunner.class)
public final class AllStateObjectTest2_0 {

    private AllStateObjectTest2_0() {
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
            new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_5.instance())
        };
    }

    @IJPQLQueryFormatterTestHelper
    static IJPQLQueryFormatter[] buildJPQLQUeryFormatters() {

        return new IJPQLQueryFormatter[] {

            new DefaultJPQLQueryFormatter(IdentifierStyle.CAPITALIZE_EACH_WORD),
            new DefaultJPQLQueryFormatter(IdentifierStyle.LOWERCASE),
            new DefaultJPQLQueryFormatter(IdentifierStyle.UPPERCASE),

            new EclipseLinkJPQLQueryFormatter(IdentifierStyle.CAPITALIZE_EACH_WORD),
            new EclipseLinkJPQLQueryFormatter(IdentifierStyle.LOWERCASE),
            new EclipseLinkJPQLQueryFormatter(IdentifierStyle.UPPERCASE),

            new DefaultActualJPQLQueryFormatter(true, IdentifierStyle.CAPITALIZE_EACH_WORD),
            new DefaultActualJPQLQueryFormatter(true, IdentifierStyle.LOWERCASE),
            new DefaultActualJPQLQueryFormatter(true, IdentifierStyle.UPPERCASE),

            new DefaultActualJPQLQueryFormatter(false, IdentifierStyle.CAPITALIZE_EACH_WORD),
            new DefaultActualJPQLQueryFormatter(false, IdentifierStyle.LOWERCASE),
            new DefaultActualJPQLQueryFormatter(false, IdentifierStyle.UPPERCASE),

            new EclipseLinkActualJPQLQueryFormatter(true, IdentifierStyle.CAPITALIZE_EACH_WORD),
            new EclipseLinkActualJPQLQueryFormatter(true, IdentifierStyle.LOWERCASE),
            new EclipseLinkActualJPQLQueryFormatter(true, IdentifierStyle.UPPERCASE),

            new EclipseLinkActualJPQLQueryFormatter(false, IdentifierStyle.CAPITALIZE_EACH_WORD),
            new EclipseLinkActualJPQLQueryFormatter(false, IdentifierStyle.LOWERCASE),
            new EclipseLinkActualJPQLQueryFormatter(false, IdentifierStyle.UPPERCASE)
        };
    }
}
