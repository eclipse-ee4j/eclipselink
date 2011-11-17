/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.parser.AggregateFunction;
import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;

public final class MaxFunctionTest extends FunctionTest {

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