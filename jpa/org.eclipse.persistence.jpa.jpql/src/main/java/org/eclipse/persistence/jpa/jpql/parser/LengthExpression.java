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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The <b>LENGTH</b> function returns the length of the string in characters as an integer.
 * <p>
 * JPA 1.0, 2.0:
 * <div><b>BNF:</b> <code>expression ::= LENGTH(string_primary)</code></div>
 * <p>
 * JPA 2.1:
 * <div><b>BNF:</b> <code>expression ::= LENGTH(string_expression)</code></div>
 * <p></p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class LengthExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * Creates a new <code>LengthExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public LengthExpression(AbstractExpression parent) {
        super(parent, LENGTH);
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
    public String getEncapsulatedExpressionQueryBNFId() {
        return InternalLengthExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningNumericsBNF.ID);
    }
}
