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
//
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import org.eclipse.persistence.jpa.jpql.parser.RightExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.RIGHT;

/**
 * The second integer argument of the <code><b>RIGHT</b></code> function specifies length of the rightmost substring
 * of the first string argument. The <code><b>RIGHT</b></code> function returns a string.
 * <p>
 * Jakarta Persistence 3.2:
 * <div><b>BNF</b> ::= <code>RIGHT(string_expression, simple_arithmetic_expression)</code></div>
 *
 * @see RightExpression
 *
 * @since 4.1
 * @author Radek Felcman
 */
public class RightExpressionStateObject extends AbstractDoubleEncapsulatedExpressionStateObject {

    /**
     * Creates a new <code>RightExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public RightExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>RightExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param firstStateObject The {@link StateObject} of the first expression
     * @param secondStateObject The {@link StateObject} of the second expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public RightExpressionStateObject(StateObject parent,
                                      StateObject firstStateObject,
                                      StateObject secondStateObject) {

        super(parent, firstStateObject, secondStateObject);
    }

    /**
     * Creates a new <code>RightExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param firstJpqlFragment The string representation of the first encapsulated expression to
     * parse and to convert into a {@link StateObject} representation
     * @param secondJpqlFragment The string representation of the second encapsulated expression to
     * parse and to convert into a {@link StateObject} representation
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public RightExpressionStateObject(StateObject parent,
                                      String firstJpqlFragment,
                                      String secondJpqlFragment) {

        super(parent, firstJpqlFragment, secondJpqlFragment);
    }

    @Override
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RightExpression getExpression() {
        return (RightExpression) super.getExpression();
    }

    @Override
    protected String getFirstQueryBNFId() {
        return StringPrimaryBNF.ID;
    }

    @Override
    public String getIdentifier() {
        return RIGHT;
    }

    @Override
    protected String getSecondQueryBNFId() {
        return StringExpressionBNF.ID;
    }

    /**
     * Keeps a reference of the {@link RightExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link RightExpression parsed object} representing a <code><b>RIGHT</b></code>
     * expression
     */
    public void setExpression(RightExpression expression) {
        super.setExpression(expression);
    }
}
