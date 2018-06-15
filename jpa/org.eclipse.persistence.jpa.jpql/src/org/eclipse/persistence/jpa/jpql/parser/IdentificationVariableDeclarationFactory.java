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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This <code>IdentificationVariableDeclarationFactory</code> handles parsing the JPQL fragment
 * within the <code><b>FROM</b></code> clause.
 *
 * @see IdentificationVariableDeclaration
 * @see CollectionMemberDeclaration
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class IdentificationVariableDeclarationFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link IdentificationVariableDeclarationFactory}.
     */
    public static final String ID = "identification-variable-declaration";

    /**
     * Creates a new <code>IdentificationVariableDeclarationFactory</code>.
     */
    public IdentificationVariableDeclarationFactory() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        if (word.equalsIgnoreCase(Expression.IN)) {
            expression = new CollectionMemberDeclaration(parent);
        }
        else {
            expression = new IdentificationVariableDeclaration(parent);
        }

        expression.parse(wordParser, tolerant);
        return expression;
    }
}
