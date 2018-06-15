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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Exact numeric literals support the use of Java integer literal syntax as well as SQL exact
 * numeric literal syntax. Approximate literals support the use of Java floating point literal
 * syntax as well as SQL approximate numeric literal
 * syntax.
 * <p>
 * Appropriate suffixes may be used to indicate the specific type of a numeric literal in accordance
 * with the Java Language Specification.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class NumericLiteral extends AbstractExpression {

    /**
     * Creates a new <code>NumericLiteral</code>.
     *
     * @param parent The parent of this expression
     */
    public NumericLiteral(AbstractExpression parent) {
        super(parent);
    }

    /**
     * Creates a new <code>NumericLiteral</code>.
     *
     * @param parent The parent of this expression
     * @param numeric The numeric value
     */
    public NumericLiteral(AbstractExpression parent, String numeric) {
        super(parent, numeric);
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
    public void acceptChildren(ExpressionVisitor visitor) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {
        children.add(buildStringExpression(getText()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(NumericLiteralBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return super.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        String numeric = getText();

        if (numeric == ExpressionTools.EMPTY_STRING) {
            numeric = wordParser.numericLiteral();
            setText(numeric);
        }

        wordParser.moveForward(numeric);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toActualText() {
        return getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toParsedText() {
        return getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {
        writer.append(getText());
    }
}
