/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import org.eclipse.persistence.jpa.jpql.parser.ConcatPipesExpression;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.CONCAT_PIPES;

/**
 *
 * <div><p><b>BNF:</b> <code>string_expression ::= string_expression || string_term</code></p></div>
 *
 * @see ConcatPipesExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class ConcatPipesExpressionStateObject extends StringExpressionStateObject {

    /**
     * Creates a new <code>ConcatPipesExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ConcatPipesExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>ConcatPipesExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param leftStateObject The {@link StateObject} representing the left expression
     * @param rightStateObject The {@link StateObject} representing the right expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ConcatPipesExpressionStateObject(StateObject parent,
                                            StateObject leftStateObject,
                                            StateObject rightStateObject) {

        super(parent, leftStateObject, rightStateObject);
    }

    /**
     * Creates a new <code>ConcatPipesExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param leftJpqlFragment The string representation of the left expression to parse and to
     * convert into a {@link StateObject}
     * @param rightJpqlFragment The string representation of the right expression to parse and to
     * convert into a {@link StateObject}
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ConcatPipesExpressionStateObject(StateObject parent,
                                            String leftJpqlFragment,
                                            String rightJpqlFragment) {

        super(parent, leftJpqlFragment, rightJpqlFragment);
    }

    @Override
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ConcatPipesExpression getExpression() {
        return (ConcatPipesExpression) super.getExpression();
    }

    @Override
    public String getIdentifier() {
        return CONCAT_PIPES;
    }

    /**
     * Keeps a reference of the {@link ConcatPipesExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link ConcatPipesExpression parsed object} representing an addition
     * expression
     */
    public void setExpression(ConcatPipesExpression expression) {
        super.setExpression(expression);
    }
}
