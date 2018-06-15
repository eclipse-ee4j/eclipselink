/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.tools.spi.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import org.eclipse.persistence.jpa.jpql.tools.spi.IConstructor;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeRepository;

/**
 * The concrete implementation of {@link IConstructor} that is wrapping a Java constructor.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class JavaConstructor implements IConstructor {

    /**
     * The actual Java constructor.
     */
    private Constructor<?> constructor;

    /**
     * The cached {@link ITypeDeclaration parameter types}.
     */
    private ITypeDeclaration[] parameterTypes;

    /**
     * The declaring type of this constructor.
     */
    private JavaType type;

    /**
     * Creates a new <code>JavaConstructor</code>.
     *
     * @param type The declaring type of this constructor
     * @param constructor The actual Java constructor
     */
    public JavaConstructor(JavaType type, Constructor<?> constructor) {
        super();
        this.type = type;
        this.constructor = constructor;
    }

    protected ITypeDeclaration[] buildParameterTypes() {

        Class<?>[] types = constructor.getParameterTypes();
        Type[] genericTypes = constructor.getGenericParameterTypes();
        ITypeDeclaration[] typeDeclarations = new ITypeDeclaration[types.length];

        for (int index = 0, count = types.length; index < count; index++) {
            typeDeclarations[index] = buildTypeDeclaration(types[index], genericTypes[index]);
        }

        return typeDeclarations;
    }

    protected ITypeDeclaration buildTypeDeclaration(Class<?> javaType, Type genericType) {
        ITypeRepository typeRepository = getTypeRepository();
        IType type = typeRepository.getType(javaType);
        return new JavaTypeDeclaration(typeRepository, type, genericType, javaType.isArray());
    }

    /**
     * {@inheritDoc}
     */
    public ITypeDeclaration[] getParameterTypes() {
        if (parameterTypes == null) {
            parameterTypes = buildParameterTypes();
        }
        return parameterTypes;
    }

    protected ITypeRepository getTypeRepository() {
        return type.getTypeRepository();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return constructor.toGenericString();
    }
}
