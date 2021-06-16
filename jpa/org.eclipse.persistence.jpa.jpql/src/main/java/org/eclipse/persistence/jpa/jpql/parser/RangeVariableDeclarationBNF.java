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
 * The query BNF for a range variable declaration expression.
 * <p></p>
 * JPA:
 * <div><b>BNF:</b> <code>range_variable_declaration ::= entity_name [AS] identification_variable</code></div>
 * <p></p>
 * EclipseLink 2.4:
 * <div><b>BNF:</b> <code>range_variable_declaration ::= { range_variable_declaration | (subquery) } [AS] identification_variable</code></div>
 * <p></p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class RangeVariableDeclarationBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "range_variable_declaration";

    /**
     * Creates a new <code>SubselectIdentificationVariableDeclarationBNF</code>.
     */
    public RangeVariableDeclarationBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(RangeVariableDeclarationFactory.ID);
    }
}
