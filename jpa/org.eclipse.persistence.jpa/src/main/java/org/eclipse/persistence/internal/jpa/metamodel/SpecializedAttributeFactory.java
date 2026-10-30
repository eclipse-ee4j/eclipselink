/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.metamodel;

import java.io.Serial;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.metamodel.BooleanAttribute;
import jakarta.persistence.metamodel.ComparableAttribute;
import jakarta.persistence.metamodel.NumericAttribute;
import jakarta.persistence.metamodel.TemporalAttribute;
import jakarta.persistence.metamodel.TextAttribute;

import org.eclipse.persistence.internal.helper.GenericTypes;
import org.eclipse.persistence.mappings.DatabaseMapping;

/** Creates the specialized metamodel types for single-valued basic attributes. */
final class SpecializedAttributeFactory {

    enum Kind { SINGULAR, BOOLEAN, TEXT, NUMERIC, TEMPORAL, COMPARABLE }

    private static final Map<Class<?>, Class<?>> WRAPPERS = Map.of(
            boolean.class, Boolean.class, byte.class, Byte.class,
            short.class, Short.class, int.class, Integer.class,
            long.class, Long.class, float.class, Float.class,
            double.class, Double.class, char.class, Character.class);

    private SpecializedAttributeFactory() {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static <X, Y> SingularAttributeImpl<X, Y> create(ManagedTypeImpl<X> managedType,
                                                   DatabaseMapping mapping) {
        Kind kind = mapping.isAbstractDirectMapping()
                ? kind(SingularAttributeImpl.basicAttributeType(managedType, mapping), managedType.getJavaType()) : Kind.SINGULAR;
        return (SingularAttributeImpl<X, Y>) switch (kind) {
            case BOOLEAN -> new BooleanAttributeImpl<>(managedType, mapping);
            case TEXT -> new TextAttributeImpl<>(managedType, mapping);
            case NUMERIC -> new NumericAttributeImpl(managedType, mapping);
            case TEMPORAL -> new TemporalAttributeImpl(managedType, mapping);
            case COMPARABLE -> new ComparableAttributeImpl(managedType, mapping);
            case SINGULAR -> new SingularAttributeImpl<>(managedType, mapping, true);
        };
    }

    static Kind kind(Class<?> domainType) {
        return kind(domainType, domainType);
    }

    static Kind kind(Type declaredType, Class<?> declaringContext) {
        Type domainType = resolve(declaredType, declaringContext);
        Class<?> type = rawClass(domainType);
        if (type == null) {
            return Kind.SINGULAR;
        }
        if (type.isPrimitive()) {
            domainType = type = WRAPPERS.getOrDefault(type, type);
        }
        if (type == Boolean.class) {
            return Kind.BOOLEAN;
        }
        if (type == String.class) {
            return Kind.TEXT;
        }
        if (!Comparable.class.isAssignableFrom(type) || unresolved(domainType)) {
            return Kind.SINGULAR;
        }
        Type comparable = asSupertype(domainType, Comparable.class);
        if (!(comparable instanceof ParameterizedType parameterized)) {
            return Kind.SINGULAR;
        }
        Type comparableType = parameterized.getActualTypeArguments()[0];
        if (Number.class.isAssignableFrom(type) && domainType.equals(comparableType)) {
            return Kind.NUMERIC;
        }
        if (!isAssignable(domainType, comparableType)) {
            return Kind.SINGULAR;
        }
        return Temporal.class.isAssignableFrom(type) ? Kind.TEMPORAL : Kind.COMPARABLE;
    }

    static Type resolve(Type type, Class<?> context) {
        return resolve(type, context, Map.of(), new HashSet<>());
    }

    /** Substitute before rebinding a domain's variables, retaining the original declaring context. */
    private static Type resolve(Type type, Class<?> context, Map<TypeVariable<?>, Type> bindings,
                                Set<TypeVariable<?>> resolving) {
        if (type instanceof TypeVariable<?> variable) {
            if (!resolving.add(variable)) {
                return variable;
            }
            try {
                Type value = bindings.get(variable);
                if (value == null && context != null
                        && variable.getGenericDeclaration() instanceof Class<?> declaring
                        && declaring.isAssignableFrom(context)) {
                    TypeVariable<?>[] variables = declaring.getTypeParameters();
                    for (int i = 0; i < variables.length; i++) {
                        if (variables[i].equals(variable)) {
                            value = GenericTypes.getTypeArgument(context, declaring, i);
                            break;
                        }
                    }
                }
                return value == null || value.equals(variable) ? variable : resolve(value, context, bindings, resolving);
            } finally {
                resolving.remove(variable);
            }
        }
        if (type instanceof ParameterizedType parameterized) {
            return new Parameterized(parameterized.getRawType(),
                    resolve(parameterized.getOwnerType(), context, bindings, resolving),
                    resolveAll(parameterized.getActualTypeArguments(), context, bindings, resolving));
        }
        if (type instanceof GenericArrayType array) {
            Type component = resolve(array.getGenericComponentType(), context, bindings, resolving);
            return component instanceof Class<?> clazz ? clazz.arrayType() : new GenericArray(component);
        }
        if (type instanceof WildcardType wildcard) {
            return new Wildcard(resolveAll(wildcard.getUpperBounds(), context, bindings, resolving),
                    resolveAll(wildcard.getLowerBounds(), context, bindings, resolving));
        }
        return type;
    }

    private static List<Type> resolveAll(Type[] types, Class<?> context, Map<TypeVariable<?>, Type> bindings,
                                         Set<TypeVariable<?>> resolving) {
        return Arrays.stream(types).map(type -> resolve(type, context, bindings, resolving)).toList();
    }

    private static void bind(Type type, Map<TypeVariable<?>, Type> bindings, boolean captureWildcards) {
        if (type instanceof ParameterizedType parameterized) {
            bind(parameterized.getOwnerType(), bindings, captureWildcards);
            TypeVariable<?>[] variables = ((Class<?>) parameterized.getRawType()).getTypeParameters();
            Type[] arguments = parameterized.getActualTypeArguments();
            for (int i = 0; i < variables.length; i++) {
                // A direct wildcard denotes an unknown capture, not the same self type.
                // A proper type such as List<?> remains a valid, concrete argument.
                if (!captureWildcards || !(arguments[i] instanceof WildcardType)) {
                    bindings.put(variables[i], arguments[i]);
                }
            }
        }
    }

    /** Follow only the hierarchy path needed to prove this particular bound. */
    private static Type asSupertype(Type source, Class<?> target) {
        Class<?> sourceClass = rawClass(source);
        if (sourceClass == null || !target.isAssignableFrom(sourceClass)) {
            return null;
        }
        if (sourceClass == target) {
            return source;
        }
        Map<TypeVariable<?>, Type> bindings = new HashMap<>();
        bind(source, bindings, target == Comparable.class);
        for (Type parent : sourceClass.getGenericInterfaces()) {
            Type resolved = resolve(parent, sourceClass, bindings, new HashSet<>());
            Type found = asSupertype(resolved, target);
            if (found != null) {
                return found;
            }
        }
        Type parent = resolve(sourceClass.getGenericSuperclass(), sourceClass, bindings, new HashSet<>());
        return parent == null ? null : asSupertype(parent, target);
    }

    /** Compare corresponding type arguments; concrete arguments are invariant, even when nested. */
    private static boolean containsArgument(Type actual, Type expected) {
        if (actual == null || expected == null) {
            return actual == expected;
        }
        if (actual instanceof TypeVariable<?> || expected instanceof TypeVariable<?>) {
            return false;
        }
        if (actual.equals(expected)) {
            return true;
        }
        if (expected instanceof WildcardType wildcard) {
            for (Type bound : wildcard.getUpperBounds()) {
                Type upper = actual instanceof WildcardType source ? source.getUpperBounds()[0] : actual;
                if (!isAssignable(upper, bound)) {
                    return false;
                }
            }
            for (Type bound : wildcard.getLowerBounds()) {
                Type lower = actual;
                if (actual instanceof WildcardType source) {
                    if (source.getLowerBounds().length == 0) {
                        return false;
                    }
                    lower = source.getLowerBounds()[0];
                }
                if (!isAssignable(bound, lower)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean isAssignable(Type actual, Type bound) {
        if (actual instanceof TypeVariable<?> || bound instanceof TypeVariable<?>
                || actual instanceof WildcardType || bound instanceof WildcardType) {
            return false;
        }
        Class<?> actualClass = rawClass(actual);
        Class<?> boundClass = rawClass(bound);
        if (actualClass == null || boundClass == null || !boundClass.isAssignableFrom(actualClass)) {
            return false;
        }
        Type boundComponent = component(bound);
        if (boundComponent != null) {
            Type actualComponent = component(actual);
            return actualComponent != null && isAssignable(actualComponent, boundComponent);
        }
        if (bound instanceof ParameterizedType expected) {
            if (!(asSupertype(actual, boundClass) instanceof ParameterizedType source)
                    || !matchesOwner(source.getOwnerType(), expected.getOwnerType())) {
                return false;
            }
            Type[] actualArguments = source.getActualTypeArguments();
            Type[] expectedArguments = expected.getActualTypeArguments();
            for (int i = 0; i < actualArguments.length; i++) {
                if (!containsArgument(actualArguments[i], expectedArguments[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean matchesOwner(Type actual, Type expected) {
        return actual == null || expected == null ? actual == expected : isAssignable(actual, expected);
    }

    private static Type component(Type type) {
        if (type instanceof GenericArrayType array) {
            return array.getGenericComponentType();
        }
        return type instanceof Class<?> clazz && clazz.isArray() ? clazz.getComponentType() : null;
    }

    private static Class<?> rawClass(Type type) {
        if (type instanceof Class<?> clazz) {
            return clazz;
        }
        if (type instanceof ParameterizedType parameterized) {
            return (Class<?>) parameterized.getRawType();
        }
        Type component = component(type);
        Class<?> componentClass = component == null ? null : rawClass(component);
        return componentClass == null ? null : componentClass.arrayType();
    }

    private static boolean unresolved(Type type) {
        if (type instanceof TypeVariable<?>) {
            return true;
        }
        if (type instanceof ParameterizedType parameterized) {
            return unresolvedOwner(parameterized.getOwnerType())
                    || Arrays.stream(parameterized.getActualTypeArguments()).anyMatch(SpecializedAttributeFactory::unresolved);
        }
        if (type instanceof WildcardType wildcard) {
            return Arrays.stream(wildcard.getUpperBounds()).anyMatch(SpecializedAttributeFactory::unresolved)
                    || Arrays.stream(wildcard.getLowerBounds()).anyMatch(SpecializedAttributeFactory::unresolved);
        }
        Type component = component(type);
        if (component != null) {
            return unresolved(component);
        }
        return type instanceof Class<?> clazz && clazz.getTypeParameters().length != 0;
    }

    private static boolean unresolvedOwner(Type owner) {
        // Static nested types have a raw declaring Class but do not capture its parameters.
        return owner instanceof ParameterizedType && unresolved(owner);
    }

    private record Parameterized(Type rawType, Type ownerType, List<Type> arguments) implements ParameterizedType {
        @Override public Type getRawType() { return rawType; }
        @Override public Type getOwnerType() { return ownerType; }
        @Override public Type[] getActualTypeArguments() { return arguments.toArray(Type[]::new); }
        @Override public boolean equals(Object other) {
            return other instanceof ParameterizedType type && rawType.equals(type.getRawType())
                    && Objects.equals(ownerType, type.getOwnerType())
                    && Arrays.equals(getActualTypeArguments(), type.getActualTypeArguments());
        }
        @Override public int hashCode() {
            return Arrays.hashCode(getActualTypeArguments()) ^ Objects.hashCode(ownerType) ^ rawType.hashCode();
        }
    }

    private record GenericArray(Type component) implements GenericArrayType {
        @Override public Type getGenericComponentType() { return component; }
        @Override public boolean equals(Object other) {
            return other instanceof GenericArrayType type && component.equals(type.getGenericComponentType());
        }
        @Override public int hashCode() { return component.hashCode(); }
    }

    private record Wildcard(List<Type> upper, List<Type> lower) implements WildcardType {
        @Override public Type[] getUpperBounds() { return upper.toArray(Type[]::new); }
        @Override public Type[] getLowerBounds() { return lower.toArray(Type[]::new); }
        @Override public boolean equals(Object other) {
            return other instanceof WildcardType type && Arrays.equals(getUpperBounds(), type.getUpperBounds())
                    && Arrays.equals(getLowerBounds(), type.getLowerBounds());
        }
        @Override public int hashCode() {
            return Arrays.hashCode(getUpperBounds()) ^ Arrays.hashCode(getLowerBounds());
        }
    }

    private static final class BooleanAttributeImpl<X> extends SingularAttributeImpl<X, Boolean>
            implements BooleanAttribute<X> {
        @Serial
        private static final long serialVersionUID = 1L;

        BooleanAttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping) {
            super(managedType, mapping, true);
        }
    }

    private static final class TextAttributeImpl<X> extends SingularAttributeImpl<X, String>
            implements TextAttribute<X> {
        @Serial
        private static final long serialVersionUID = 1L;

        TextAttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping) {
            super(managedType, mapping, true);
        }
    }

    private static final class NumericAttributeImpl<X, N extends Number & Comparable<N>>
            extends SingularAttributeImpl<X, N> implements NumericAttribute<X, N> {
        @Serial
        private static final long serialVersionUID = 1L;

        NumericAttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping) {
            super(managedType, mapping, true);
        }
    }

    private static final class TemporalAttributeImpl<X, T extends Temporal & Comparable<? super T>>
            extends SingularAttributeImpl<X, T> implements TemporalAttribute<X, T> {
        @Serial
        private static final long serialVersionUID = 1L;

        TemporalAttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping) {
            super(managedType, mapping, true);
        }
    }

    private static final class ComparableAttributeImpl<X, C extends Comparable<? super C>>
            extends SingularAttributeImpl<X, C> implements ComparableAttribute<X, C> {
        @Serial
        private static final long serialVersionUID = 1L;

        ComparableAttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping) {
            super(managedType, mapping, true);
        }
    }
}
