/*******************************************************************************
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The <b>INDEX</b> function returns an integer value corresponding to the position of its argument
 * in an ordered list. The <b>INDEX</b> function can only be applied to identification variables
 * denoting types for which an order column has been specified.
 *
 * <div><b>BNF:</b> <code>expression ::= INDEX(identification_variable)</code><p></div>
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
