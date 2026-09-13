/*
 * Copyright (c) 2024, 2026 Contributors to the Eclipse Foundation.
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public final class GenericTypes {

    private GenericTypes() {
    }

    public static Type getTypeArgument(Object object, Class<?> targetType, int argumentIndex) {

        Type result = findTypeArgument(object.getClass(), targetType, argumentIndex, Map.of());

        if (result == null) {
            throw new IllegalArgumentException(object.getClass().getName() + " does not implement/extend " + targetType.getName());
        }

        return result;
    }

    private static Type findTypeArgument(Type currentType, Class<?> targetType, int argumentIndex, Map<TypeVariable<?>, Type> inheritedMappings) {
        Class<?> currentClass;
        Map<TypeVariable<?>, Type> mappings = new HashMap<>(inheritedMappings);

        if (currentType instanceof ParameterizedType parameterizedType) {
            currentClass = (Class<?>) parameterizedType.getRawType();

            TypeVariable<?>[] variables = currentClass.getTypeParameters();
            Type[] arguments = parameterizedType.getActualTypeArguments();

            for (int i = 0; i < variables.length; i++) {
                mappings.put(variables[i], resolve(arguments[i], inheritedMappings));
            }
        } else if (currentType instanceof Class<?> clazz) {
            currentClass = clazz;
        } else {
            return null;
        }

        if (currentClass == targetType) {
            TypeVariable<?>[] parameters = currentClass.getTypeParameters();

            if (argumentIndex < 0 || argumentIndex >= parameters.length) {
                throw new IndexOutOfBoundsException(argumentIndex);
            }

            return resolve(parameters[argumentIndex], mappings);
        }

        for (Type genericInterface : currentClass.getGenericInterfaces()) {
            Type result = findTypeArgument(genericInterface, targetType, argumentIndex, mappings);

            if (result != null) {
                return result;
            }
        }

        Type genericSuperclass = currentClass.getGenericSuperclass();

        if (genericSuperclass != null) {
            return findTypeArgument(genericSuperclass, targetType, argumentIndex, mappings);
        }

        return null;
    }

    private static Type resolve(Type type, Map<TypeVariable<?>, Type> mappings) {
        while (type instanceof TypeVariable<?> variable) {
            Type resolved = mappings.get(variable);

            if (resolved == null || resolved == type) {
                break;
            }

            type = resolved;
        }

        return type;
    }
}