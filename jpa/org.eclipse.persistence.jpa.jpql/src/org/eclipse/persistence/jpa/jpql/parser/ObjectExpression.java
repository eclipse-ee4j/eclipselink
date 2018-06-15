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
 * Stand-alone identification variables in the <b>SELECT</b> clause may optionally be qualified by
 * the <b>OBJECT</b> operator. The <b>SELECT</b> clause must not use the <b>OBJECT</b> operator to
 * qualify path expressions.
 *
 * <div><b>BNF:</b> <code>expression ::= OBJECT(identification_variable)</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class ObjectExpression extends EncapsulatedIdentificationVariableExpression {

    /**
     * Creates a new <code>ObjectExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public ObjectExpression(AbstractExpression parent) {
        super(parent, OBJECT);
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
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(ObjectExpressionBNF.ID);
    }
}
