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
 * The <b>LOCATE</b> function returns the position of a given string within a string, starting the
 * search at a specified position. It returns the first position at which the string was found as an
 * integer. The first argument is the string to be located; the second argument is the string to be
 * searched; the optional third argument is an integer that represents the string position at which
 * the search is started (by default, the beginning of the string to be searched). The first
 * position in a string is denoted by 1. If the string is not found, 0 is returned. The <b>LOCATE</b>
 * function returns the length of the string in characters as an integer.
 * <p>
 * JPA 1.0, 2.0:
 * <div><b>BNF:</b> <code>expression ::= LOCATE(string_primary, string_primary [, simple_arithmetic_expression])</code></div>
 * <p>
 * JPA 2.1:
 * <div><b>BNF:</b> <code>expression ::= LOCATE(string_expression, string_expression [, arithmetic_expression])</code></div>
 * <p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class LocateExpression extends AbstractTripleEncapsulatedExpression {

    /**
     * Creates a new <code>LocateExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public LocateExpression(AbstractExpression parent) {
        super(parent, LOCATE);
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
            case 2:  return InternalLocateThirdExpressionBNF.ID;
            default: return InternalLocateStringExpressionBNF.ID;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningNumericsBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isThirdExpressionOptional() {
        return true;
    }
}
