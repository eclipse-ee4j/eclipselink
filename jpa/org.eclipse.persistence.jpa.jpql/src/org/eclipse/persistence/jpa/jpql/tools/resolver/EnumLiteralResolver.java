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
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} retrieves the type for an enum constant.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public class EnumLiteralResolver extends Resolver {

    /**
     * The fully qualified name of the enum constant.
     */
    private String enumLiteral;

    /**
     * The cached {@link IType} that was already created elsewhere.
     */
    private IType type;

    /**
     * Creates a new <code>EnumLiteralResolver</code>.
     *
     * @param parent The parent of this resolver, which is never <code>null</code>
     * @param type The {@link IType} of the enum type
     * @param enumLiteral The fully qualified name of the enum constant
     * @exception NullPointerException If the parent is <code>null</code>
     */
    public EnumLiteralResolver(Resolver parent, IType type, String enumLiteral) {
        super(parent);
        this.type        = type;
        this.enumLiteral = enumLiteral;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        return type.getTypeDeclaration();
    }

    /**
     * Returns the name of the constant constant.
     *
     * @return The name of the constant without the fully qualified enum type
     */
    public String getConstantName() {
        int index = enumLiteral.lastIndexOf(AbstractExpression.DOT);
        return enumLiteral.substring(index + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return enumLiteral;
    }
}
