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

/**
 * The query BNF for the <b>COUNT</b> expression's encapsulated expressions.
 * <p></p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>expression ::= CONCAT(string_primary, string_primary)</code></div>
 * <p></p>
 * JPA 2.0:
 * <div><b>BNF:</b> <code>expression ::= CONCAT(string_primary, string_primary {, string_primary}*)</code></div>
 * <p></p>
 * JPA 2.1:
 * <div><b>BNF:</b> <code>expression ::= CONCAT(string_expression, string_expression {, string_expression}*)</code></div>
 * <p></p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InternalConcatExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this <code>InternalConcatExpressionBNF</code>.
     */
    public final static String ID = "internal_concat";

    /**
     * Creates a new <code>InternalConcatExpressionBNF</code>.
     */
    public InternalConcatExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setHandleAggregate(true); // To support invalid queries
        setHandleCollection(true);
        setFallbackBNFId(LiteralExpressionFactory.ID);
        registerChild(StringPrimaryBNF.ID);
    }
}
