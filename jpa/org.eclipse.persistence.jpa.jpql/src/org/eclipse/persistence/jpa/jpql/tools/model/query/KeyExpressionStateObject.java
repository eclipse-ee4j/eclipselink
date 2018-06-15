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

import org.eclipse.persistence.jpa.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This object represents an identification variable that maps the keys of a {@link java.util.Map}.
 * <p>
 * This is part of JPA 2.0.
 *
 * <div><b>BNF:</b> <code>KEY(identification_variable)</code><p></div>
 *
 * @see KeyExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class KeyExpressionStateObject extends EncapsulatedIdentificationVariableExpressionStateObject {

    /**
     * Creates a new <code>KeyExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public KeyExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>KeyExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param identificationVariable The identification variable
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public KeyExpressionStateObject(StateObject parent, String identificationVariable) {
        super(parent, identificationVariable);
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
    public KeyExpression getExpression() {
        return (KeyExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType resolveType() {

        ITypeDeclaration typeDeclaration = getTypeDeclaration();

        if (getTypeHelper().isMapType(typeDeclaration.getType())) {
            ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();

            if (typeParameters.length > 0) {
                return typeParameters[0].getType();
            }
        }

        return getTypeHelper().objectType();
    }

    /**
     * Keeps a reference of the {@link KeyExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link KeyExpression parsed object} representing a <code><b>KEY</b></code>
     * expression
     */
    public void setExpression(KeyExpression expression) {
        super.setExpression(expression);
    }
}
