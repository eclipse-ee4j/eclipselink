/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
//     09/02/2019-2.7 Alexandre Jacob
//        - 527415: Fix code when locale is tr, az or lt
package org.eclipse.persistence.jpa.tests.jpql.parser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.junit.Assert;
import org.junit.Test;

public class JPQLExpressionTurkishLocaleTest {

    @Test
    public void testJPQLExpressionWithTurkishLocale() {
        Locale current = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));

            JPQLGrammar grammar = DefaultEclipseLinkJPQLGrammar.instance();
            JPQLExpression expression = new JPQLExpression(
                "select u from sec$User u where u.loginLowerCase like :login and u.deleteDate is null", 
                DefaultEclipseLinkJPQLGrammar.instance(),
                true
            );

            Collection<JPQLQueryProblem> problems = new LinkedList<JPQLQueryProblem>();
            EclipseLinkGrammarValidator grammarValidator = new EclipseLinkGrammarValidator(grammar);
            grammarValidator.setProblems(problems);
            expression.accept(grammarValidator);

            Assert.assertTrue(problems.isEmpty());
        } finally {
            Locale.setDefault(current);
        }
    }
}
