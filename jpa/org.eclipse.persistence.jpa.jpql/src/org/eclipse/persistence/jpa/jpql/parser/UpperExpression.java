/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * The <b>UPPER</b> function converts a string to upper case and it returns a string.
 * <p>
 * JPA 1.0, 2.0:
 * <div><b>BNF:</b> <code>expression ::= UPPER(string_primary)</code></div>
 * <p>
 * JPA 2.1:
 * <div><b>BNF:</b> <code>expression ::= UPPER(string_expression)</code></div>
 * <p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class UpperExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * Creates a new <code>UpperExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public UpperExpression(AbstractExpression parent) {
        super(parent, UPPER);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncapsulatedExpressionQueryBNFId() {
        return InternalUpperExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningStringsBNF.ID);
    }
}
