/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.AggregateFunction;
import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * Unit-tests for {@link org.eclipse.persistence.jpa.jpql.parser.MaxFunction MaxFunction}.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class MaxFunctionTest extends AggregateFunctionTest {

    @Override
    protected AggregateFunctionTester aggregateFunctionTester(ExpressionTester expression) {
        return max(expression);
    }

    @Override
    protected Class<? extends AggregateFunction> functionClass() {
        return MaxFunction.class;
    }

    @Override
    protected String identifier() {
        return MaxFunction.MAX;
    }
}
