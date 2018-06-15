/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The second and third arguments of the <b>SUBSTRING</b> function denote the starting position and
 * length of the substring to be returned. These arguments are integers. The first position of a
 * string is denoted by 1. The <b>SUBSTRING</b> function returns a string.
 * <p>
 * JPA 1.0:
 * <div><b>BNF</b> ::= <code>SUBSTRING(string_primary, simple_arithmetic_expression, simple_arithmetic_expression)</code></div>
 * <p>
 * JPA 2.0:
 * <div><b>BNF</b> ::= <code>SUBSTRING(string_primary, simple_arithmetic_expression [, simple_arithmetic_expression])</code></div>
 * <p>
 * JPA 2.1:
 * <div><b>BNF</b> ::= <code>SUBSTRING(string_expression, arithmetic_expression [, arithmetic_expression])</code></div>
 *
 * <div>Example: <b>UPDATE</b> Employee e <b>SET</b> e.firstName = <b>SUBSTRING</b>('TopLink Workbench', 1, 8)<p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class SubstringExpression extends AbstractTripleEncapsulatedExpression {

    /**
     * Creates a new <code>SubstringExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public SubstringExpression(AbstractExpression parent) {
        super(parent, SUBSTRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterQueryBNFId(int index) {
        switch (index) {
            case 0:  return InternalSubstringStringExpressionBNF.ID;
            default: return InternalSubstringPositionExpressionBNF.ID;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningStringsBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isThirdExpressionOptional() {
        return true;
    }
}
