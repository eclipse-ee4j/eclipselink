/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite runs {@link EclipseLinkJPQLParserTests2_4} using JPQL grammars written for JPA
 * 2.0 and for EclipseLink 2.1.
 *
 * @version 2.5.1
 * @since 2.5.1
 * @author Pascal Filion
 */
@SuiteClasses({
    EclipseLinkJPQLParserTests2_1.class
})
@RunWith(JPQLTestRunner.class)
public final class AllEclipseLinkJPQLParserTests2_1 {

    private AllEclipseLinkJPQLParserTests2_1() {
        super();
    }

    @JPQLGrammarTestHelper
    static JPQLGrammar[] buildJPQLGrammars() {
        return JPQLGrammarTools.allEclipseLinkJPQLGrammars(EclipseLinkVersion.VERSION_2_1);
    }
}
