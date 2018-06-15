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

import org.eclipse.persistence.jpa.jpql.parser.InternalEntityTypeExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.TypeExpression;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * An entity type expression can be used to restrict query polymorphism. The <code><b>TYPE</b></code>
 * operator returns the exact type of the argument.
 * <p>
 * Part of JPA 2.0.
 *
 * <div><b>BNF:</b> <code>type_discriminator ::= TYPE(identification_variable |
 *                                                           single_valued_object_path_expression |
 *                                                           input_parameter)</code><p></div>
 *
 * @see TypeExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class TypeExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

    /**
     * Creates a new <code>TypeExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public TypeExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>TypeExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param stateObject The {@link StateObject} representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public TypeExpressionStateObject(StateObject parent, StateObject stateObject) {
        super(parent, stateObject);
    }

    /**
     * Creates a new <code>TypeExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param path Either the identification variable, the singled-valued object path expression or
     * the input parameter
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public TypeExpressionStateObject(StateObject parent, String path) {
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
    public TypeExpression getExpression() {
        return (TypeExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getQueryBNFId() {
        return InternalEntityTypeExpressionBNF.ID;
    }

    /**
     * Keeps a reference of the {@link TypeExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link TypeExpression parsed object} representing a <code><b>TYPE</b></code>
     * expression
     */
    public void setExpression(TypeExpression expression) {
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
