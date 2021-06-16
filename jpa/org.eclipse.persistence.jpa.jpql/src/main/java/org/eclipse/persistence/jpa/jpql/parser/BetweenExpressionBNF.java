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
 * The query BNF for the between expression.
 *
 * <div><b>BNF:</b> <code>between_expression ::= arithmetic_expression [NOT] BETWEEN arithmetic_expression AND arithmetic_expression |
 * string_expression [NOT] BETWEEN string_expression AND string_expression |
 * datetime_expression [NOT] BETWEEN datetime_expression AND datetime_expression</code><p></p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class BetweenExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this {@link BetweenExpressionBNF}.
     */
    public static final String ID = "between_expression";

    /**
     * Creates a new <code>BetweenExpressionBNF</code>.
     */
    public BetweenExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setCompound(true);
        registerExpressionFactory(BetweenExpressionFactory.ID);
        registerChild(ArithmeticExpressionBNF.ID);
        registerChild(StringExpressionBNF.ID);
        registerChild(DatetimeExpressionBNF.ID);
    }
}
