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
//
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The second and third arguments of the <b>REPLACE</b> function denote the pattern to replace and
 * replacement string. These arguments are strings. The <b>REPLACE</b> function returns a string.
 * <br>
 * Jakarta Persistence 3.2:
 * <div><b>BNF</b> ::= <code>REPLACE(string_expression, string_expression , string_expression)</code></div>
 *
 * <div>Example: <b>UPDATE</b> Employee e <b>SET</b> e.firstName = <b>REPLACE</b>('TopLink Workbench', 'Top', 'Eclipse')</div>
 *
 * @since 4.1
 * @author Radek Felcman
 */
public final class ReplaceExpression extends AbstractTripleEncapsulatedExpression {

    /**
     * Creates a new <code>ReplaceExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public ReplaceExpression(AbstractExpression parent) {
        super(parent, REPLACE);
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getParameterQueryBNFId(int index) {
        switch (index) {
            case 0:  return InternalReplaceStringExpressionBNF.ID;
            default: return InternalReplacePositionExpressionBNF.ID;
        }
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningStringsBNF.ID);
    }

    @Override
    protected boolean isThirdExpressionOptional() {
        return false;
    }
}
