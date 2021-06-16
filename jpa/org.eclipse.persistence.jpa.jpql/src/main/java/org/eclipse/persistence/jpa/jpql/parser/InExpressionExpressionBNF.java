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
 * The query BNF describes the expression being tested by the <code>IN</code> expression.
 * <p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>in_expression ::= state_field_path_expression [NOT] IN(in_item {, in_item}* | subquery)</code><p></p></div>
 * JPA 2.0
 * <div><b>BNF:</b> <code>in_expression ::= {state_field_path_expression | type_discriminator} [NOT] IN { ( in_item {, in_item}* ) | (subquery) | collection_valued_input_parameter }</code><p></p></div>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InExpressionExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this <code>InExpressionExpressionBNF</code>.
     */
    public static final String ID = "in_expression_expression";

    /**
     * Creates a new <code>InExpressionExpressionBNF</code>.
     */
    public InExpressionExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        registerChild(StateFieldPathExpressionBNF.ID);
    }
}
