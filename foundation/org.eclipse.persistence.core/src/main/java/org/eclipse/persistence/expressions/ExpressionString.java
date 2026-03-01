/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.expressions;

/**
 * <p>
 * <b>Purpose</b>: This class contains String operators.</p>
 * <p>Example:
 * <blockquote><pre>
 *  ExpressionBuilder builder = new ExpressionBuilder();
 *  Expression stringResult = ExpressionString.concatPipes("abcd", "xyz");
 *  session.readAllObjects(Company.class, stringResult);
 * </pre></blockquote>
 */
public final class ExpressionString {

    private ExpressionString() {
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression concatPipes(Expression right, Object left) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.ConcatPipes);
        return anOperator.expressionFor(right, left);
    }
}
