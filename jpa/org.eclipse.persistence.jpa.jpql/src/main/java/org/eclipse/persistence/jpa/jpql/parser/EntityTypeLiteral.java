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
    @Override
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
