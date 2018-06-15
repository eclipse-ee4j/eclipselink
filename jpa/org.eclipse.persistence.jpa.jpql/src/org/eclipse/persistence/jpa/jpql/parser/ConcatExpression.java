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

import java.util.ArrayList;
import java.util.List;

/**
 * The <b>CONCAT</b> function returns a string that is a concatenation of its arguments.
 * <p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>expression ::= CONCAT(string_primary, string_primary)</code></div>
 * <p>
 * JPA 2.0:
 * <div><b>BNF:</b> <code>expression ::= CONCAT(string_primary, string_primary {, string_primary}*)</code></div>
 * <p>
 * JPA 2.1:
 * <div><b>BNF:</b> <code>expression ::= CONCAT(string_expression, string_expression {, string_expression}*)</code></div>
 *
 * <div>Example: <b>SELECT</b> c.firstName <b>FROM</b> Customer c <b>HAVING</b> c.firstName = <b>CONCAT</b>(:fname, :lname)</div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class ConcatExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * Creates a new <code>ConcatExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public ConcatExpression(AbstractExpression parent) {
        super(parent, CONCAT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates a new {@link CollectionExpression} that will wrap the single constructor item.
     *
     * @return The single constructor item represented by a temporary collection
     */
    public CollectionExpression buildCollectionExpression() {

        List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
        children.add((AbstractExpression) getExpression());

        List<Boolean> commas = new ArrayList<Boolean>(1);
        commas.add(Boolean.FALSE);

        List<Boolean> spaces = new ArrayList<Boolean>(1);
        spaces.add(Boolean.FALSE);

        return new CollectionExpression(this, children, commas, spaces, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncapsulatedExpressionQueryBNFId() {
        return InternalConcatExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningStringsBNF.ID);
    }
}
