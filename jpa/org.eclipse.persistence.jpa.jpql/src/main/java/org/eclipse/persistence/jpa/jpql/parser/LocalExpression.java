/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 317: Implement LOCAL DATE, LOCAL TIME and LOCAL DATETIME.
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The {@code LOCAL local_datetime_type} expression.
 * <br>
 * Jakarta Persistence 3.1:
 * <div><b>BNF:</b> <code>
 * functions_returning_datetime ::= CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP |
 *                                  LOCAL local_datetime_type |
 *                                  extract_datetime_par
 *
 * local_datetime_type ::= DATE |   ..... matches Java java.time.LocalDate
 *                         TIME |   ..... matches Java java.time.LocalTime
 *                         DATETIME ..... matches Java java.time.LocalDateTime
 * </code></div>
 */
public class LocalExpression extends AbstractExpression {

    /**
     * The expression being negated by this expression.
     */
    private AbstractExpression dateType;

    /**
     * The actual <b>LOCAL</b> identifier found in the string representation of the JPQL query.
     */
    private String identifier;

    /**
     * Creates a new <code>LocalExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public LocalExpression(AbstractExpression parent) {
        super(parent, LOCAL);
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
        getDateType().accept(visitor);
    }

    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getDateType());
    }

    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {
        children.add(buildStringExpression(LOCAL));
        children.add(buildStringExpression(SPACE));
        if (dateType != null) {
            children.add(dateType);
        }
    }

    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {
        identifier = wordParser.moveForward(LOCAL);
        wordParser.skipLeadingWhitespace();
        dateType = parse(wordParser, LocalDateTypeBNF.ID, tolerant);
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(LocalExpressionBNF.ID);
    }

    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {
        // LOCAL
        writer.append(actual ? identifier : getText());
        writer.append(SPACE);
        // <local_datetime_type> expression
        if (dateType != null) {
            dateType.toParsedText(writer, actual);
        }
    }

    /**
     * Returns the {@link Expression} representing the expression with date type.
     *
     * @return The expression representing the expression with date type
     */
    public Expression getDateType() {
        if (dateType == null) {
            dateType = buildNullExpression();
        }
        return dateType;
    }

    /**
     * Determines whether the expression with date type was parsed.
     *
     * @return <code>true</code> if the expression with date type was parsed;
     *         <code>false</code> if it was not parsed
     */
    public boolean hasDateType() {
        return dateType != null && !dateType.isNull();
    }

}
