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
 * The <b>LOWER</b> function converts a string to lower case and it returns a string.
 * <p>
 * JPA 1.0, 2.0:
 * <div><b>BNF:</b> <code>expression ::= LOWER(string_primary)</code></div>
 * <p>
 * JPA 2.1:
 * <div><b>BNF:</b> <code>expression ::= LOWER(string_expression)</code></div>
 * <p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class LowerExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * Creates a new <code>LowerExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public LowerExpression(AbstractExpression parent) {
        super(parent, LOWER);
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
    public String getEncapsulatedExpressionQueryBNFId() {
        return InternalLowerExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningStringsBNF.ID);
    }
}
