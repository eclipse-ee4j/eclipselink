/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpressionBNF;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * One of the aggregate functions. The arguments must correspond to orderable state-field types
 * (i.e., numeric types, string types, character types, or date types). The return type of this
 * function is based on the state-field's type.
 *
 * <div><p><b>BNF:</b> <code>expression ::= MAX([DISTINCT] state_field_path_expression)</code></p></div>
 *
 * @see MaxFunction
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class MaxFunctionStateObject extends AggregateFunctionStateObject {

    /**
     * Creates a new <code>MaxFunctionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MaxFunctionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>MaxFunctionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
     * have distinct values; <code>false</code> if it is not required
     * @param stateObject The {@link StateObject} representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MaxFunctionStateObject(StateObject parent, boolean distinct, StateObject stateObject) {
        super(parent, distinct, stateObject);
    }

    /**
     * Creates a new <code>MaxFunctionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
     * have distinct values; <code>false</code> if it is not required
     * @param path Either the identification variable or the state field path expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MaxFunctionStateObject(StateObject parent, boolean distinct, String path) {
        super(parent, distinct, path);
    }

    /**
     * Creates a new <code>MaxFunctionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param stateObject The {@link StateObject} representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MaxFunctionStateObject(StateObject parent, StateObject stateObject) {
        super(parent, stateObject);
    }

    /**
     * Creates a new <code>MaxFunctionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param path Either the identification variable or the state field path expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MaxFunctionStateObject(StateObject parent, String path) {
        super(parent, path);
    }

    @Override
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public MaxFunction getExpression() {
        return (MaxFunction) super.getExpression();
    }

    @Override
    public String getIdentifier() {
        return MAX;
    }

    @Override
    protected String getQueryBNFId() {
        return StateFieldPathExpressionBNF.ID;
    }

    /**
     * Keeps a reference of the {@link MaxFunction parsed object} object, which should only be done
     * when this object is instantiated during the conversion of a parsed JPQL query into {@link
     * StateObject StateObjects}.
     *
     * @param expression The {@link MaxFunction parsed object} representing a <code><b>MAX</b></code>
     * expression
     */
    public void setExpression(MaxFunction expression) {
        super.setExpression(expression);
    }
}
