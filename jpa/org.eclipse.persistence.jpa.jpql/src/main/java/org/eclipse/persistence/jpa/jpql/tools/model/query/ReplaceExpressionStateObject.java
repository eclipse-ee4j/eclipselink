/*
 * Copyright (c) 2011, 2023 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.StringExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.ReplaceExpression;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.REPLACE;

/**
 * The second and third arguments of the <code><b>REPLACE</b></code> function denote the pattern to replace
 * and replacement string. These arguments are strings. The <code><b>REPLACE</b></code> function returns a string.
 * <p>
 * Jakarta Persistence 3.2:
 * <div><b>BNF</b> ::= REPLACE(string_primary, string_expression , string_expression)</div>
 *
 * @see ReplaceExpression
 *
 * @version 4.1
 * @since 4.1
 * @author Radek Felcman
 */
public class ReplaceExpressionStateObject extends AbstractTripleEncapsulatedExpressionStateObject {

    /**
     * Creates a new <code>ReplaceExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ReplaceExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>ReplaceExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param firstStateObject The {@link StateObject} of the first expression
     * @param secondStateObject The {@link StateObject} of the second expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ReplaceExpressionStateObject(StateObject parent,
                                        StateObject firstStateObject,
                                        StateObject secondStateObject) {

        super(parent, firstStateObject, secondStateObject, null);
    }

    /**
     * Creates a new <code>ReplaceExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param firstStateObject The {@link StateObject} of the first expression
     * @param secondStateObject The {@link StateObject} of the second expression
     * @param thirdStateObject The {@link StateObject} of the third encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ReplaceExpressionStateObject(StateObject parent,
                                        StateObject firstStateObject,
                                        StateObject secondStateObject,
                                        StateObject thirdStateObject) {

        super(parent, firstStateObject, secondStateObject, thirdStateObject);
    }

    /**
     * Creates a new <code>ReplaceExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param firstJpqlFragment The string representation of the first encapsulated expression to
     * parse and to convert into a {@link StateObject} representation
     * @param secondJpqlFragment The string representation of the second encapsulated expression to
     * parse and to convert into a {@link StateObject} representation
     * @param thirdJpqlFragment The string representation of the third encapsulated expression to
     * parse and to convert into a {@link StateObject} representation
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ReplaceExpressionStateObject(StateObject parent,
                                        String firstJpqlFragment,
                                        String secondJpqlFragment,
                                        String thirdJpqlFragment) {

        super(parent, firstJpqlFragment, secondJpqlFragment, thirdJpqlFragment);
    }

    @Override
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ReplaceExpression getExpression() {
        return (ReplaceExpression) super.getExpression();
    }

    @Override
    protected String getFirstQueryBNFId() {
        return StringPrimaryBNF.ID;
    }

    @Override
    public String getIdentifier() {
        return REPLACE;
    }

    @Override
    protected String getSecondQueryBNFId() {
        return StringExpressionBNF.ID;
    }

    @Override
    protected String getThirdQueryBNFId() {
        return StringExpressionBNF.ID;
    }

    /**
     * Keeps a reference of the {@link ReplaceExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link ReplaceExpression parsed object} representing a <code><b>REPLACE</b></code>
     * expression
     */
    public void setExpression(ReplaceExpression expression) {
        super.setExpression(expression);
    }
}
