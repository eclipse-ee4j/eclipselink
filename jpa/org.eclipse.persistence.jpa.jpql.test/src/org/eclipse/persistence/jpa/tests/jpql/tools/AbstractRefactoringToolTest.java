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

import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.tests.jpql.JPQLCoreTest;
import org.eclipse.persistence.jpa.tests.jpql.tools.model.IJPQLQueryBuilderTestHelper;

/**
 * The abstract definition of a unit-test that tests {@link org.eclipse.persistence.jpa.jpql.
 * RefactoringTool RefactoringTool}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractRefactoringToolTest extends JPQLCoreTest {

    @IJPQLQueryBuilderTestHelper
    private IJPQLQueryBuilder jpqlQueryBuilder;

    protected JPQLGrammar getGrammar() {
        return jpqlQueryBuilder.getGrammar();
    }

    protected IJPQLQueryBuilder getJPQLQueryBuilder() {
        return jpqlQueryBuilder;
    }
}
