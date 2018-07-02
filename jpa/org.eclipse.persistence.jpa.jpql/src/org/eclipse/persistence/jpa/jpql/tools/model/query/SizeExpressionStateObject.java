/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>SIZE</b></code> function returns an integer value, the number of elements of the
 * collection. If the collection is empty, the <code><b>SIZE</b></code> function evaluates to zero.
 *
 * <div><b>BNF:</b> <code>expression ::= SIZE(collection_valued_path_expression)</code><p></div>
 *
 * @see SizeExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class SizeExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

    /**
     * Creates a new <code>SizeExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public SizeExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>SizeExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param stateObject The {@link StateObject} representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public SizeExpressionStateObject(StateObject parent, StateObject stateObject) {
        super(parent, stateObject);
    }

    /**
     * Creates a new <code>SizeExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param path The collection-valued path expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public SizeExpressionStateObject(StateObject parent, String path) {
        super(parent, path);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SizeExpression getExpression() {
        return (SizeExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return SIZE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getQueryBNFId() {
        return CollectionValuedPathExpressionBNF.ID;
    }

    /**
     * Keeps a reference of the {@link SizeExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link SizeExpression parsed object} representing a <code><b>SIZE</b></code>
     * expression
     */
    public void setExpression(SizeExpression expression) {
        super.setExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStateObject(StateObject stateObject) {
        super.setStateObject(stateObject);
    }
}
