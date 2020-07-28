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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link Expression} wraps the name of an entity type.
 *
 * @version 2.5.2
 * @since 2.3
 * @author Pascal Filion
 */
public final class EntityTypeLiteral extends AbstractExpression {

    /**
     * Creates a new <code>EntityTypeLiteral</code>.
     *
     * @param parent The parent of this expression
     * @param entityTypeName The name of the entity
     */
    public EntityTypeLiteral(AbstractExpression parent, String entityTypeName) {
        super(parent, entityTypeName);
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
     * Returns the name of the entity type.
     *
     * @return The name of the entity that was parsed
     */
    public String getEntityTypeName() {
        return getText();
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(EntityTypeLiteralBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {
        wordParser.moveForward(getText());
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
