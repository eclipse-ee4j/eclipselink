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
 * This {@link IdentificationVariableFactory} creates a new {@link IdentificationVariable}.
 *
 * @see IdentificationVariable
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class IdentificationVariableFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link IdentificationVariableFactory}.
     */
    public static final String ID = "identification-variable";

    /**
     * Creates a new <code>IdentificationVariableFactory</code>.
     */
    public IdentificationVariableFactory() {
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

        if (word.length() > 0) {
            expression = new IdentificationVariable(parent, word);
            expression.parse(wordParser, tolerant);
            return expression;
        }

        return null;
    }
}
