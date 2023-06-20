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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * String operators. A concat is a string operation representing the
 * combination of two operands together.
 *
 * <div><b>BNF:</b> <code>string_expression ::= string_expression || string_term</code></div>
 *
 * @version 4.1
 * @since 4.1
 * @author Radek Felcman
 */
public final class ConcatPipesExpression extends StringExpression {

    /**
     * Creates a new <code>ConcatExpressionPipes</code>.
     *
     * @param parent The parent of this expression
     */
    public ConcatPipesExpression(AbstractExpression parent) {
        super(parent, CONCAT_PIPES);
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
