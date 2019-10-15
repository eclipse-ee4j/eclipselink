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

/**
 * The query BNF for a derived collection member declaration expression.
 *
 * <div><b>BNF:</b> <code>derived_collection_member_declaration ::= IN superquery_identification_variable.{single_valued_object_field.}*collection_valued_field</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DerivedCollectionMemberDeclarationBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "derived_collection_member_declaration";

    /**
     * Creates a new <code>DerivedCollectionMemberDeclarationBNF</code>.
     */
    public DerivedCollectionMemberDeclarationBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        registerExpressionFactory(CollectionMemberDeclarationFactory.ID);
    }
}
