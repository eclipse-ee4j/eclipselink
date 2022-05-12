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
import java.util.function.Supplier;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The {@code DATE | TIME | DATETIME} argument of {@code LOCAL} local_datetime_type expression.
 * <br>
 * Jakarta Persistence 3.1:
 * <div><b>BNF:</b><code>
 * local_datetime_type ::= DATE |   ..... matches Java java.time.LocalDate
 *                         TIME |   ..... matches Java java.time.LocalTime
 *                         DATETIME ..... matches Java java.time.LocalDateTime
 * </code></div>
 */
public class LocalDateTime extends AbstractExpression {

    /**
     * Local date/time type identifier.
     */
    private enum Identifier {
        /** "DATE" type identifier. */
        DATE("date"),
        /** "DATETIME" type identifier. */
        DATETIME("datetime"),
        /** "TIME" type identifier. */
        TIME("time");

        private final String name;

        Identifier(final String name) {
            this.name = name;
        }

        /**
         * Convert local date/time text identifier to {@link Identifier}.
         * Conversion is case insensitive.
         *
         * @param name local date/time text identifier
         * @return {@link Identifier} matching local date/time text identifier
         *         or {@code null} when identifier is unknown.
         */
        private static final Identifier getIdentifier(final String name) {
            switch(name.toUpperCase()) {
                case Expression.DATE:
                    return DATE;
                case Expression.TIME:
                    return TIME;
                case Expression.DATETIME:
                    return DATETIME;
                default:
                    return null;
            }
        }

        /**
         * Parse identifier (DATE/DATETIME/TIME) at current parser position.
         * Determines proper identifier with minimal cost. Input always contains DATE | DATETIME | TIME
         * so no exact matching is required
         *
         * @param wordParser source JPQL parser
         * @return local date/time type identifier
         */
        private static Identifier parse(final WordParser wordParser) {
            final int position = wordParser.position();
            // Check 1st identifier chatacter:
            // - D|ATE[TIME]
            // - T|IME
            switch(wordParser.character(position))  {
                case 'd':
                case 'D':
                    //  Prefix is "D" and possible options are DATE | DATETIME
                    switch (wordParser.character(position + 4)) {
                        // Prefix is "DATET" which points to DATETIME
                        case 't':
                        case 'T':
                            return DATETIME;
                        // Anything else points to LOCAL_DATE
                        default:
                            return DATE;
                    }
                // Anything else points to TIME
                default:
                    return TIME;
            }
        }

    }

    /**
     * The actual <b>local_datetime_type</b> identifier found in the string representation of the JPQL query.
     */
    private Identifier identifier;

    /**
     * Creates a new <code>LocalExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public LocalDateTime(AbstractExpression parent) {
        super(parent);
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected void addChildrenTo(Collection<Expression> children) {
    }

    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
    }

    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {
    }

    @Override
    protected void parse(final WordParser wordParser, boolean tolerant) {
        identifier = Identifier.parse(wordParser);
        setText(identifier.name);
        wordParser.moveForward(identifier.name);
    }

    /**
     * Execute action depending on local date/time text identifier in {@link LocalDateTime} expression.
     *
     * @param dateAction function executed for {@code LOCAL DATE}
     * @param timeAction function executed for {@code LOCAL TIME}
     * @param dateTimeAction function executed for {@code LOCAL DATETIME}
     */
    public void runByType(Runnable dateAction, Runnable timeAction, Runnable dateTimeAction) {
        // Make sure there is some value available.
        switch(this.identifier != null ? this.identifier : Identifier.getIdentifier(getText())) {
            case DATE:
                dateAction.run();
                return;
            case TIME:
                timeAction.run();
                return;
            case DATETIME:
                dateTimeAction.run();
                return;
            default:
                throw new IllegalStateException("Unknown value of " + getText() + " LocalDateTime expression");
        }
    }

    /**
     * Execute supplier depending on local date/time text identifier in {@link LocalDateTime} expression.
     *
     * @param dateAction function executed for {@code LOCAL DATE}
     * @param timeAction function executed for {@code LOCAL TIME}
     * @param dateTimeAction function executed for {@code LOCAL DATETIME}
     */
    public <R> R getValueByType(Supplier<R> dateAction, Supplier<R> timeAction, Supplier<R> dateTimeAction) {
        // Make sure there is some value available.
        switch(this.identifier != null ? this.identifier : Identifier.getIdentifier(getText())) {
            case DATE:
                return dateAction.get();
            case TIME:
                return timeAction.get();
            case DATETIME:
                return dateTimeAction.get();
            default:
                throw new IllegalStateException("Unknown value of " + getText() + " LocalDateTime expression");
        }
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(LocalDateTypeBNF.ID);
    }

    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {
        writer.append(actual ? identifier.name : getText());
    }

    @Override
    public String toActualText() {
        return getText();
    }

}
