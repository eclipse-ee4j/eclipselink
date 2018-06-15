/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import org.eclipse.persistence.jpa.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.jpql.parser.SimpleArithmeticExpressionBNF;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The modulo operation finds the remainder of division of one number by another.
 * <p>
 * It takes two integer arguments and returns an integer.
 *
 * <div><b>BNF:</b> <code>expression ::= MOD(simple_arithmetic_expression, simple_arithmetic_expression)</code><p></div>
 *
 * @see ModExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class ModExpressionStateObject extends AbstractDoubleEncapsulatedExpressionStateObject {

    /**
     * Creates a new <code>ModExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ModExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>ModExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param firstStateObject The {@link StateObject} representing the first expression
     * @param secondStateObject The {@link StateObject} representing the second expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ModExpressionStateObject(StateObject parent,
                                    StateObject firstStateObject,
                                    StateObject secondStateObject) {

        super(parent, firstStateObject, secondStateObject);
    }

    /**
     * Creates a new <code>ModExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param firstJpqlFragment The string representation of the first encapsulated expression to
     * parse and to convert into a {@link StateObject} representation
     * @param secondJpqlFragment The string representation of the second encapsulated expression to
     * parse and to convert into a {@link StateObject} representation
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ModExpressionStateObject(StateObject parent,
                                    String firstJpqlFragment,
                                    String secondJpqlFragment) {

        super(parent, firstJpqlFragment, secondJpqlFragment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModExpression getExpression() {
        return (ModExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getFirstQueryBNFId() {
        return SimpleArithmeticExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return MOD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSecondQueryBNFId() {
        return SimpleArithmeticExpressionBNF.ID;
    }

    /**
     * Keeps a reference of the {@link ModExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link ModExpression parsed object} representing a <code><b>MOD</b></code>
     * expression
     */
    public void setExpression(ModExpression expression) {
        super.setExpression(expression);
    }
}
