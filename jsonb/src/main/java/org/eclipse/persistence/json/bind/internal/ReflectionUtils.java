/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.unmarshaller.CurrentItem;
import org.eclipse.persistence.json.bind.internal.unmarshaller.EmbeddedItem;

import javax.json.bind.JsonbException;
import java.lang.reflect.*;

/**
 * Utility class for resolution of generics during unmarshalling.
 *
 * @author Roman Grigoriadi
 */
public class ReflectionUtils {

    /**
     * Get raw type by type.
     * Only for ParametrizedTypes and Classes.
     * Can't handle TypeVariables and Wildcards.
     *
     * @param type Type to get class information from, not null.
     * @return Class of a type.
     */
    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getClass();
        }
        throw new JsonbException(Messages.getMessage(MessageKeys.UNSUPPORTED_TYPE, type));
    }

    /**
     * Get a raw type of any type.
     * If type is a {@link TypeVariable} recursively search {@link CurrentItem} for resolution of typevar.
     * If type is a {@link WildcardType} find most specific upper / lower bound, which can be used. If most specific
     * bound is a {@link TypeVariable}, perform typevar resolution.
     *
     * @param item item containing wrapper class of a type field, not null.
     * @param type type to resolve, typically field type or generic bound, not null.
     * @return resolved raw class
     */
    public static Class<?> resolveRawType(CurrentItem<?> item, Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            return getRawType(resolveType(item, type));
        }
    }

    /**
     * Resolve a type by item.
     * If type is a {@link TypeVariable} recursively search {@link CurrentItem} for resolution of typevar.
     * If type is a {@link WildcardType} find most specific upper / lower bound, which can be used. If most specific
     * bound is a {@link TypeVariable}, perform typevar resolution.
     *
     * @param item item containing wrapper class of a type field, not null.
     * @param type type to resolve, typically field type or generic bound, not null.
     * @return resolved type
     */
    public static Type resolveType(CurrentItem<?> item, Type type) {
        if (type instanceof WildcardType) {
            return resolveMostSpecificBound(item, (WildcardType) type);
        } else if (type instanceof TypeVariable) {
            return resolveItemVariableType(item, (TypeVariable<?>) type);
        }
        return type;
    }

    /**
     * Resolve a bounded type variable type by its wrapper types.
     * Resolution could be done only if a compile time generic information is provided, either:
     * by generic field or subclass of a generic class.
     * @param item item to search "runtime" generic type of a TypeVariable.
     * @param typeVariable type to search in item for, not null.
     * @return Type of a generic "runtime" bound, not null.
     */
    public static Type resolveItemVariableType(CurrentItem<?> item, TypeVariable<?> typeVariable) {
        if (item == null) {
            throw new JsonbException(Messages.getMessage(MessageKeys.GENERIC_BOUND_NOT_FOUND, typeVariable));
        }

        //Embedded items doesn't hold information about variable types
        if (item instanceof EmbeddedItem) {
            return resolveItemVariableType(item.getWrapper(), typeVariable);
        }

        ParameterizedType wrapperParameterizedType = findParameterizedSuperclass(item.getRuntimeType());

        VariableTypeInheritanceSearch search = new VariableTypeInheritanceSearch();
        Type foundType = search.searchParametrizedType(wrapperParameterizedType, typeVariable);
        if (foundType != null) {
            if (foundType instanceof TypeVariable) {
                return resolveItemVariableType(item.getWrapper(), (TypeVariable<?>) foundType);
            }
            return foundType;
        }

        return resolveItemVariableType(item.getWrapper(), typeVariable);
    }

    private static ParameterizedType findParameterizedSuperclass(Type type) {
        if (type == null || type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        if (!(type instanceof Class)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.RESOLVE_PARAMETRIZED_TYPE, type));
        }
        return findParameterizedSuperclass(((Class) type).getGenericSuperclass());
    }

    /**
     * Resolves a wildcard most specific upper or lower bound.
     */
    private static Type resolveMostSpecificBound(CurrentItem<?> item, WildcardType wildcardType) {
        Class<?> result = Object.class;
        for (Type upperBound : wildcardType.getUpperBounds()) {
            result = getMostSpecificBound(item, result, upperBound);
        }
        for (Type lowerBound : wildcardType.getLowerBounds()) {
            result = getMostSpecificBound(item, result, lowerBound);
        }
        return result;
    }

    private static Class<?> getMostSpecificBound(CurrentItem<?> item, Class<?> result, Type bound) {
        if (bound == Object.class) {
            return result;
        }
        //if bound is type variable search recursively for wrapper generic expansion
        Type resolvedBoundType = bound instanceof TypeVariable ? resolveType(item, bound) : bound;
        Class<?> boundRawType = getRawType(resolvedBoundType);
        //resolved class is a subclass of a result candidate
        if (result.isAssignableFrom(boundRawType)) {
            result = boundRawType;
        }
        return result;
    }

}
