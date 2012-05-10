/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.Expression;

@SuppressWarnings("nls")
public final class OperatorExpressionTest extends AbstractFunctionExpressionTest {

	@Override
	protected String functionName(int index) {
		switch (index) {
			case 0:  return "'NVC'";
			case 1:  return "'invalid";
			case 2:  return "''";
			default: return "'sql'";
		}
	}

	@Override
	protected String identifier(int index) {
		return Expression.OPERATOR;
	}
}