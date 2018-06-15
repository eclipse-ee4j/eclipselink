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
 * The query BNF for a collection-valued path expression.
 * <p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>collection_valued_path_expression ::= identification_variable.{single_valued_object_field.}*collection_valued_field</code><p></div>
 *
 * JPA 2.0:
 * <div><b>BNF:</b> <code>collection_valued_path_expression ::= general_identification_variable.{single_valued_object_field.}*collection_valued_field</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class CollectionValuedPathExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "collection_valued_path_expression";

    /**
     * Creates a new <code>CollectionValuedPathExpressionBNF</code>.
     */
    public CollectionValuedPathExpressionBNF() {
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
    }
}
