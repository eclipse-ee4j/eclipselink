/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JoinCollectionValuedPathExpressionFactory extends AbstractLiteralExpressionFactory {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "join_association_path_expression*";

    /**
     * Creates a new <code>JoinCollectionValuedPathExpressionFactory</code>.
     */
    public JoinCollectionValuedPathExpressionFactory() {
        super(ID);
    }

    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        expression = new AbstractSchemaName(parent, word);
        expression.parse(wordParser, tolerant);
        return expression;
    }

    @Override
    protected boolean isCollection() {
        return true;
    }
}
