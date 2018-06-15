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

/**
 * The query BNF for a join association path  expression.
 *
 * <div><b>BNF:</b> <code>join_association_path_expression ::= join_collection_valued_path_expression |
 * join_single_valued_path_expression</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JoinAssociationPathExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "join_association_path_expression*";

    /**
     * Creates a new <code>JoinAssociationPathExpressionBNF</code>.
     */
    public JoinAssociationPathExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(CollectionValuedPathExpressionFactory.ID);
        registerChild(CollectionValuedPathExpressionBNF.ID);
    }
}
