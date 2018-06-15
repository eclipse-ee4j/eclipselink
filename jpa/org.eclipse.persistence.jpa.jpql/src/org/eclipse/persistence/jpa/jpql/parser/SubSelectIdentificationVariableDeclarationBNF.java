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
 * The query BNF for an identification variable declaration expression defined
 * in a sub-select expression.
 *
 * <div><b>BNF:</b> <code>subselect_identification_variable_declaration ::= identification_variable_declaration |
 * derived_path_expression [AS] identification_variable {join}* |
 * derived_collection_member_declaration</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class SubSelectIdentificationVariableDeclarationBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "subselect_identification_variable_declaration";

    /**
     * Creates a new <code>SubSelectIdentificationVariableDeclarationBNF</code>.
     */
    public SubSelectIdentificationVariableDeclarationBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(IdentificationVariableDeclarationFactory.ID);
        registerChild(IdentificationVariableDeclarationBNF.ID);
        registerChild(DerivedCollectionMemberDeclarationBNF.ID);
    }
}
