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

import org.eclipse.persistence.jpa.jpql.parser.StringExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.StringTermBNF;

/**
 * This expression represents an arithmetic expression, which means the first and second expressions
 * are aggregated with an arithmetic sign.
 *
 * <div><p><b>BNF:</b> <code>arithmetic_expression ::= arithmetic_expression &lt;identifier&gt; arithmetic_term</code></p></div>
 *
 * @see ConcatPipesExpressionStateObject
 *
 * @see StringExpression
 *
 * @version 4.1
 * @since 4.1
 * @author Radek Felcman
 */
public abstract class StringExpressionStateObject extends CompoundExpressionStateObject {

    /**
     * Creates a new <code>StringExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    protected StringExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>StringExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param leftStateObject The {@link StateObject} representing the left expression
     * @param rightStateObject The {@link StateObject} representing the right expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    protected StringExpressionStateObject(StateObject parent,
                                          StateObject leftStateObject,
                                          StateObject rightStateObject) {

        super(parent, leftStateObject, rightStateObject);
    }

    /**
     * Creates a new <code>StringExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param leftJpqlFragment The string representation of the left expression to parse and to
     * convert into a {@link StateObject}
     * @param rightJpqlFragment The string representation of the right expression to parse and to
     * convert into a {@link StateObject}
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    protected StringExpressionStateObject(StateObject parent,
                                          String leftJpqlFragment,
                                          String rightJpqlFragment) {

        super(parent, leftJpqlFragment, rightJpqlFragment);
    }

    @Override
    public StringExpression getExpression() {
        return (StringExpression) super.getExpression();
    }

    @Override
    protected String getLeftQueryBNFId() {
        return StringExpressionBNF.ID;
    }

    @Override
    protected String getRightQueryBNFId() {
        return StringTermBNF.ID;
    }
}
