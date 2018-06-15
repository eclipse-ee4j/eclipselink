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
package org.eclipse.persistence.jpa.tests.jpql;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractSemanticValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkSemanticValidatorExtension;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkSemanticValidator;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryStringFormatter;
import org.eclipse.persistence.jpa.tests.jpql.tools.AbstractSemanticValidatorTest;
import org.junit.Test;

/**
 * The unit-test class used for testing a JPQL query semantically when the JPA version is 1.0 and 2.0
 * and EclipseLink is the persistence provider. The EclipseLink version supported is 2.0, 2.1, 2.2
 * and 2.3.
 *
 * @version 2.5.1
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkSemanticValidatorTest extends AbstractSemanticValidatorTest {

    private JPQLQueryStringFormatter buildFormatter_01(final String jpqlQuery) {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return jpqlQuery;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLQueryContext buildQueryContext() {
        return new EclipseLinkJPQLQueryContext(jpqlGrammar);
    }

    protected EclipseLinkSemanticValidatorExtension buildSemanticExtension() {
        return EclipseLinkSemanticValidatorExtension.NULL_EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractSemanticValidator buildValidator() {
        return new EclipseLinkSemanticValidator(
            buildSemanticValidatorHelper(),
            buildSemanticExtension()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isComparisonTypeChecked() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isPathExpressionToCollectionMappingAllowed() {
        return true;
    }

    @Test
    public final void test_IdentificationVariable_Valid_2() throws Exception {

        if (EclipseLinkVersionTools.isOlderThan2_1(getQueryContext().getGrammar())) {
            return;
        }

        String jpqlQuery = "SELECT FUNC('MONTH', e.salary) mois, " +
                           "       FUNC('YEAR', e.working) annee, " +
                           "       e.address.city categ, " +
                           "       SUM(e.salary) " +
                           "FROM Employee e " +
                           "GROUP BY annee, mois, categ " +
                           "ORDER BY annee ASC, mois ASC, categ ASC";

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_OrderByItem_Valid_1() throws Exception {

        String jpqlQuery = "SELECT order2 AS order FROM Order order2 ORDER BY order";
        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_01(jpqlQuery));
        testHasNoProblems(problems);
    }
}
