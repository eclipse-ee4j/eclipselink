/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * An ordering clause allows the objects or values that are returned by the query to be ordered.
 *
 * <div><b>BNF:</b> <code>orderby_clause ::= <b>&lt;identifier&gt;</b> {@link OrderByItem orderby_item} {, {@link OrderByItem orderby_item}}*</code><p></div>
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public abstract class AbstractOrderByClause extends AbstractExpression {

    /**
     * Determines whether a whitespace was parsed after the JPQL identifier.
     */
    private boolean hasSpaceAfterIdentifier;

    /**
     * The actual <b></b> identifier found in the string representation of the JPQL query.
     */
    private String identifier;

    /**
     * The order by items.
     */
    private AbstractExpression orderByItems;

    /**
     * Creates a new <code>AbstractOrderByClause</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The JPQL identifier representing an ordering clause
     */
    protected AbstractOrderByClause(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
        getOrderByItems().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getOrderByItems());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {

        children.add(buildStringExpression(getText()));

        if (hasSpaceAfterIdentifier) {
            children.add(buildStringExpression(SPACE));
        }

        if (orderByItems != null) {
            children.add(orderByItems);
        }
    }

    /**
     * Creates a new {@link CollectionExpression} that will wrap the single order by item.
     *
     * @return The single order by item represented by a temporary collection
     */
    public final CollectionExpression buildCollectionExpression() {

        List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
        children.add((AbstractExpression) getOrderByItems());

        List<Boolean> commas = new ArrayList<Boolean>(1);
        commas.add(Boolean.FALSE);

        List<Boolean> spaces = new ArrayList<Boolean>(1);
        spaces.add(Boolean.FALSE);

        return new CollectionExpression(this, children, spaces, commas, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final JPQLQueryBNF findQueryBNF(Expression expression) {

        if ((orderByItems != null) && orderByItems.isAncestor(expression)) {
            return getQueryBNF(OrderByItemBNF.ID);
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the actual identifier found in the string representation of the JPQL query, which
     * has the actual case that was used.
     *
     * @return The actual identifier that was actually parsed
     */
    public final String getActualIdentifier() {
        return identifier;
    }

    /**
     * Returns the {@link Expression} representing the list of items to order.
     *
     * @return The expression representing the list of items to order
     */
    public final Expression getOrderByItems() {
        if (orderByItems == null) {
            orderByItems = buildNullExpression();
        }
        return orderByItems;
    }

    /**
     * Determines whether the list of items to order was parsed.
     *
     * @return <code>true</code> the list of items to order was parsed; <code>false</code> otherwise
     */
    public final boolean hasOrderByItems() {
        return orderByItems != null &&
              !orderByItems.isNull();
    }

    /**
     * Determines whether a whitespace was parsed after the identifier.
     *
     * @return <code>true</code> if a whitespace was parsed after the identifier; <code>false</code> otherwise
     */
    public final boolean hasSpaceAfterIdentifier() {
        return hasSpaceAfterIdentifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        // Identifier
        identifier = wordParser.moveForwardIgnoreWhitespace(getText());

        hasSpaceAfterIdentifier = wordParser.skipLeadingWhitespace() > 0;

        // Group by items
        orderByItems = parse(wordParser, OrderByItemBNF.ID, tolerant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        // Identifier
        writer.append(actual ? identifier : getText());

        if (hasSpaceAfterIdentifier) {
            writer.append(SPACE);
        }

        // Order by items
        if (orderByItems != null) {
            orderByItems.toParsedText(writer, actual);
        }
    }
}
