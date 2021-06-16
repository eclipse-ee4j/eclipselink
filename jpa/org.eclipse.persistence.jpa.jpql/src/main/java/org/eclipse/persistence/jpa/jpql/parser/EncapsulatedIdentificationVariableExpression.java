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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link Expression} represents an identification variable that maps a {@link java.util.Map Map}
 * property, either the key, the value or a {@link java.util.Map.Entry Map.Entry}).
 * <p>
 * This is part of JPA 2.0.
 * </p>
 * <div><b>BNF:</b> <code>&lt;identifier&gt;(identification_variable)</code><p></p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class EncapsulatedIdentificationVariableExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * Creates a new <code>MapEntryIdentificationVariableExpression</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The JPQL identifier that starts this expression
     */
    protected EncapsulatedIdentificationVariableExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getEncapsulatedExpressionQueryBNFId() {
        return IdentificationVariableBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(GeneralIdentificationVariableBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractExpression parse(WordParser wordParser, String queryBNFId, boolean tolerant) {

        if (tolerant) {
            return super.parse(wordParser, LiteralBNF.ID, tolerant);
        }

        IdentificationVariable expression = new IdentificationVariable(this, wordParser.word());
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
