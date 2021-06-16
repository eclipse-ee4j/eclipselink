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
 * The <b>INDEX</b> function returns an integer value corresponding to the position of its argument
 * in an ordered list. The <b>INDEX</b> function can only be applied to identification variables
 * denoting types for which an order column has been specified.
 *
 * <div><b>BNF:</b> <code>expression ::= INDEX(identification_variable)</code><p></p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class IndexExpression extends EncapsulatedIdentificationVariableExpression {

    /**
     * Creates a new <code>IndexExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public IndexExpression(AbstractExpression parent) {
        super(parent, INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningNumericsBNF.ID);
    }
}
