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

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Returns an expression that allows to treat its base as if it were a subclass of the class
 * returned by the base.
 * <p>
 * New to EclipseLink 2.1.
 *
 * <div><b>BNF:</b> <code>join_treat ::= TREAT(collection_valued_path_expression AS entity_type_literal)</code></div>
 * <p>
 * Example: <code>SELECT e FROM Employee e JOIN TREAT(e.projects AS LargeProject) lp WHERE lp.budget = value</code>
 *
 * @version 2.5.1
 * @since 2.3
 * @author Pascal Filion
 */
public final class TreatExpression extends AbstractEncapsulatedExpression {

    /**
     * The actual <b>AS</b> identifier found in the string representation of the JPQL query.
     */
    private String asIdentifier;

    /**
     * The {@link Expression} that represents the collection-valued path expression.
     */
    private AbstractExpression collectionValuedPathExpression;

    /**
     * The entity type used to downcast the type of the elements in the collection.
     */
    private AbstractExpression entityType;

    /**
     * Determines whether a whitespace was parsed after <b>AS</b>.
     */
    private boolean hasSpaceAfterAs;

    /**
     * Determines whether a whitespace was parsed after the collection-valued path expression.
     */
    private boolean hasSpaceAfterCollectionValuedPathExpression;

    /**
     * Determines which child expression is been currently parsed.
     */
    private int parameterIndex;

    /**
     * Creates a new <code>TreatExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public TreatExpression(AbstractExpression parent) {
        super(parent, TREAT);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(ExpressionVisitor visitor) {
        acceptUnknownVisitor(visitor);
    }

    /**
     * {@inheritDoc}
     */
    public void acceptChildren(ExpressionVisitor visitor) {
        getCollectionValuedPathExpression().accept(visitor);
        getEntityType().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getCollectionValuedPathExpression());
        children.add(getEntityType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedEncapsulatedExpressionTo(List<Expression> children) {

        // Collection-valued path expression
        if (collectionValuedPathExpression != null) {
            children.add(collectionValuedPathExpression);
        }

        if (hasSpaceAfterCollectionValuedPathExpression) {
            children.add(buildStringExpression(SPACE));
        }

        // AS
        if (asIdentifier != null) {
            children.add(buildStringExpression(AS));
        }

        if (hasSpaceAfterAs) {
            children.add(buildStringExpression(SPACE));
        }

        // Entity type
        if (entityType != null) {
            children.add(entityType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final JPQLQueryBNF findQueryBNF(Expression expression) {

        if ((collectionValuedPathExpression != null) && collectionValuedPathExpression.isAncestor(expression)) {
            return getQueryBNF(CollectionValuedPathExpressionBNF.ID);
        }

        if ((entityType != null) && entityType.isAncestor(expression)) {
            return getQueryBNF(EntityTypeLiteralBNF.ID);
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the actual <b>AS</b> identifier found in the string representation of the JPQL
     * query, which has the actual case that was used.
     *
     * @return The <b>AS</b> identifier that was actually parsed, or an empty string if it was not
     * parsed
     */
    public String getActualAsIdentifier() {
        return (asIdentifier != null) ? asIdentifier : ExpressionTools.EMPTY_STRING;
    }

    /**
     * Returns the {@link Expression} that represents the collection-valued path expression.
     *
     * @return The expression that represents the collection-valued path expression
     */
    public Expression getCollectionValuedPathExpression() {
        if (collectionValuedPathExpression == null) {
            collectionValuedPathExpression = buildNullExpression();
        }
        return collectionValuedPathExpression;
    }

    /**
     * Returns the {@link Expression} that represents the entity type that will be used to downcast
     * the type of the elements in the collection.
     *
     * @return The expression representing the entity type
     */
    public Expression getEntityType() {
        if (entityType == null) {
            entityType = buildNullExpression();
        }
        return entityType;
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(TreatExpressionBNF.ID);
    }

    /**
     * Determines whether the identifier <b>AS</b> was parsed.
     *
     * @return <code>true</code> if the identifier <b>AS</b> was parsed; <code>false</code> otherwise
     */
    public boolean hasAs() {
        return asIdentifier != null;
    }

    /**
     * Determines whether the collection-valued path expression of the query was parsed.
     *
     * @return <code>true</code> if the collection-valued path expression was parsed;
     * <code>false</code> if nothing was parsed
     */
    public boolean hasCollectionValuedPathExpression() {
        return collectionValuedPathExpression != null &&
              !collectionValuedPathExpression.isNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEncapsulatedExpression() {
        return hasCollectionValuedPathExpression() || hasAs() || hasEntityType();
    }

    /**
     * Determines whether the entity type was parsed.
     *
     * @return <code>true</code> if the entity type was parsed; <code>false</code> if nothing was
     * parsed
     */
    public boolean hasEntityType() {
        return entityType != null &&
              !entityType.isNull();
    }

    /**
     * Determines whether a whitespace was parsed after <b>AS</b>.
     *
     * @return <code>true</code> if there was a whitespace after <b>AS</b>; <code>false</code>
     * otherwise
     */
    public boolean hasSpaceAfterAs() {
        return hasSpaceAfterAs;
    }

    /**
     * Determines whether a whitespace was parsed after the collection-valued path expression.
     *
     * @return <code>true</code> if a whitespace was parsed after the collection-valued path
     * expression; <code>false</code> otherwise
     */
    public boolean hasSpaceAfterCollectionValuedPathExpression() {
        return hasSpaceAfterCollectionValuedPathExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        char character = wordParser.character();

        // When parsing an invalid JPQL query (eg: LOCATE + ABS(e.name)) then "+ ABS(e.name)"
        // should not be parsed as an invalid first expression
        if ((parameterIndex == 0) &&
            ((character == '+') || (character == '-')) &&
            !hasLeftParenthesis()) {

            parameterIndex = -1;
            return true;
        }

        return super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parseEncapsulatedExpression(WordParser wordParser,
                                               int whitespaceCount,
                                               boolean tolerant) {

        parameterIndex = 0;

        // Collection-valued path expression
        collectionValuedPathExpression = parse(
            wordParser,
            CollectionValuedPathExpressionBNF.ID,
            tolerant
        );

        // See comment in isParsingComplete()
        if (parameterIndex == -1) {
            return;
        }

        hasSpaceAfterCollectionValuedPathExpression = wordParser.skipLeadingWhitespace() > 0;

        // Parse 'AS'
        if (wordParser.startsWithIdentifier(AS)) {
            asIdentifier = wordParser.moveForward(AS);
            hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
        }

        parameterIndex = 1;

        // Entity type
        if (tolerant) {
            entityType = parse(wordParser, EntityTypeLiteralBNF.ID, tolerant);
        }
        else {
            entityType = new EntityTypeLiteral(this, wordParser.word());
            entityType.parse(wordParser, tolerant);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void removeEncapsulatedExpression() {
        entityType = null;
        asIdentifier = null;
        hasSpaceAfterAs = false;
        collectionValuedPathExpression = null;
        hasSpaceAfterCollectionValuedPathExpression = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

        // Collection-valued path expression
        if (collectionValuedPathExpression != null) {
            writer.append(collectionValuedPathExpression);
        }

        if (hasSpaceAfterCollectionValuedPathExpression) {
            writer.append(SPACE);
        }

        // AS
        if (asIdentifier != null) {
            writer.append(actual ? asIdentifier : AS);
        }

        if (hasSpaceAfterAs) {
            writer.append(SPACE);
        }

        // Entity type
        if (entityType != null) {
            writer.append(entityType);
        }
    }
}
