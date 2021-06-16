/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import org.eclipse.persistence.jpa.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpressionBNF;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * One of the aggregate functions. The arguments must correspond to orderable state-field types
 * (i.e., numeric types, string types, character types, or date types). The return type of this
 * function is based on the state-field's type.
 *
 * <div><b>BNF:</b> <code>expression ::= MIN([DISTINCT] state_field_path_expression)</code><p></p></div>
 *
 * @see MinFunction
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class MinFunctionStateObject extends AggregateFunctionStateObject {

    /**
     * Creates a new <code>MinFunctionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MinFunctionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>MinFunctionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
     * have distinct values; <code>false</code> if it is not required
     * @param stateObject The {@link StateObject} representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MinFunctionStateObject(StateObject parent, boolean distinct, StateObject stateObject) {
        super(parent, distinct, stateObject);
    }

    /**
     * Creates a new <code>MinFunctionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param distinct <code>true</code> to add <code><b>DISTINCT</b></code> to the query in order to
     * have distinct values; <code>false</code> if it is not required
     * @param path Either the identification variable or the state field path expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MinFunctionStateObject(StateObject parent, boolean distinct, String path) {
        super(parent, distinct, path);
    }

    /**
     * Creates a new <code>MinFunctionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param stateObject The {@link StateObject} representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MinFunctionStateObject(StateObject parent, StateObject stateObject) {
        super(parent, stateObject);
    }

    /**
     * Creates a new <code>MinFunctionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param path Either the identification variable or the state field path expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MinFunctionStateObject(StateObject parent, String path) {
        super(parent, path);
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
    public MinFunction getExpression() {
        return (MinFunction) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return MIN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getQueryBNFId() {
        return StateFieldPathExpressionBNF.ID;
    }

    /**
     * Keeps a reference of the {@link MinFunction parsed object} object, which should only be done
     * when this object is instantiated during the conversion of a parsed JPQL query into {@link
     * StateObject StateObjects}.
     *
     * @param expression The {@link MinFunction parsed object} representing a <code><b>MIN</b></code>
     * expression
     */
    public void setExpression(MinFunction expression) {
        super.setExpression(expression);
    }
}
