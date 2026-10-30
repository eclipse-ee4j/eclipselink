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
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PostCreate;
import jakarta.persistence.PreClose;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * INTERNAL:
 * Immutable persistence-unit listener metadata. Class names are retained during
 * predeployment so callback classes are not loaded before transformers are ready.
 */
public final class PersistenceUnitListener {
    public static final String DEFINITIONS_PROPERTY = PersistenceUnitListener.class.getName();
    public static final String NAME_PROPERTY = DEFINITIONS_PROPERTY + ".name";

    private final String className;
    private final boolean annotations;
    private final String postCreate;
    private final String preClose;

    public PersistenceUnitListener(String className, boolean annotations, String postCreate, String preClose) {
        this.className = className;
        this.annotations = annotations;
        this.postCreate = postCreate;
        this.preClose = preClose;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PersistenceUnitListener listener
                && className.equals(listener.className) && annotations == listener.annotations
                && Objects.equals(postCreate, listener.postCreate) && Objects.equals(preClose, listener.preClose);
    }

    public String getClassName() {
        return className;
    }

    /** Detect callbacks using bytecode metadata only, including overloaded methods. */
    public static boolean hasCallbackAnnotations(MetadataClass listenerClass) {
        for (MetadataClass type = listenerClass; type != null && !type.isClass(Object.class);
                type = type.getSuperclass()) {
            for (MetadataMethod first : type.getMethods().values()) {
                for (MetadataMethod method = first; method != null; method = method.getNext()) {
                    if (method.isAnnotationPresent(PostCreate.class.getName())
                            || method.isAnnotationPresent(PreClose.class.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, annotations, postCreate, preClose);
    }

    /** Resolve and validate only after the public factory's deployment is ready. */
    public Resolved resolve(ClassLoader loader) {
        Class<?> listenerClass = PrivilegedAccessHelper.callDoPrivilegedWithException(
                () -> PrivilegedAccessHelper.getClassForName(className, true, loader),
                exception -> ValidationException.unableToLoadClass(className, exception));
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> type = listenerClass; type != null && type != Object.class; type = type.getSuperclass()) {
            hierarchy.add(type);
        }
        Collections.reverse(hierarchy);
        List<Method> methods = new ArrayList<>();
        for (Class<?> type : hierarchy) {
            for (Method method : PrivilegedAccessHelper.callDoPrivileged(
                    () -> PrivilegedAccessHelper.getDeclaredMethods(type))) {
                if (!method.isBridge() && !method.isSynthetic()
                        && !EntityListener.isMethodOverridden(method, listenerClass)) {
                    methods.add(method);
                }
            }
        }
        Map<Class<?>, Method> createMethods = new LinkedHashMap<>();
        Map<Class<?>, Method> closeMethods = new LinkedHashMap<>();
        resolveXML(listenerClass, methods, postCreate, createMethods);
        resolveXML(listenerClass, methods, preClose, closeMethods);
        if (annotations) {
            for (Method method : methods) {
                if (postCreate == null && method.isAnnotationPresent(PostCreate.class)) {
                    addMethod(listenerClass, method, createMethods);
                }
                if (preClose == null && method.isAnnotationPresent(PreClose.class)) {
                    addMethod(listenerClass, method, closeMethods);
                }
            }
        }
        return new Resolved(listenerClass, createMethods, closeMethods);
    }

    private static void addMethod(Class<?> listenerClass, Method method, Map<Class<?>, Method> methods) {
        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)
                || Modifier.isAbstract(modifiers) || method.getReturnType() != void.class) {
            throw ValidationException.invalidCallbackMethodModifier(listenerClass, method.getName());
        }
        if (!hasLifecycleParameter(method)) {
            throw ValidationException.invalidCallbackMethod(listenerClass, method.toString());
        }
        Method existing = methods.putIfAbsent(method.getParameterTypes()[0], method);
        if (existing != null && !existing.equals(method)) {
            throw ValidationException.multipleLifecycleCallbackMethodsForSameLifecycleEvent(
                    listenerClass, method, existing);
        }
    }

    private static boolean hasLifecycleParameter(Method method) {
        if (method.getParameterCount() != 1) {
            return false;
        }
        Class<?> parameter = method.getParameterTypes()[0];
        return parameter == EntityManagerFactory.class || parameter == EntityManager.class || parameter == EntityAgent.class;
    }

    private static void resolveXML(Class<?> listenerClass, List<Method> candidates, String name,
            Map<Class<?>, Method> methods) {
        if (name == null) {
            return;
        }
        // M7 leaves XML overload resolution unspecified. Select each legal exact
        // lifecycle parameter type, rather than the first method reflection returns.
        for (Method method : candidates) {
            if (method.getName().equals(name) && hasLifecycleParameter(method)) {
                addMethod(listenerClass, method, methods);
            }
        }
        if (methods.isEmpty()) {
            throw ValidationException.invalidCallbackMethod(listenerClass, name);
        }
    }

    public record Resolved(Class<?> listenerClass, Map<Class<?>, Method> postCreate, Map<Class<?>, Method> preClose) {
        public Resolved {
            postCreate = Map.copyOf(postCreate);
            preClose = Map.copyOf(preClose);
        }
    }
}
