/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * An abstract schema name designates the abstract schema type over which the query ranges.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class AbstractSchemaName extends AbstractExpression {

    /**
     * Creates a new <code>AbstractSchemaName</code>.
     *
     * @param parent The parent of this expression
     * @param abstractSchemaName The abstract schema name
     */
    public AbstractSchemaName(AbstractExpression parent, String abstractSchemaName) {
        super(parent, abstractSchemaName);
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
    }

    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {
        children.add(buildStringExpression(getText()));
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(AbstractSchemaNameBNF.ID);
    }

    @Override
    public String getText() {
        return super.getText();
    }

    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {
        wordParser.moveForward(getText());
    }

    @Override
    public String toActualText() {
        return getText();
    }

    @Override
    public String toParsedText() {
        return getText();
    }

    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {
        writer.append(getText());
    }
}
