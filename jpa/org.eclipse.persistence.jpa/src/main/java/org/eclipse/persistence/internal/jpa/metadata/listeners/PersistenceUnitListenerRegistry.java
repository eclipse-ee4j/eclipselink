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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.PersistenceException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.cdi.InjectionManager;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Immutable callback bindings owned by one public factory and its live agents.
 * The public callback argument is intentionally shared; the dispatch collections
 * are private snapshots. Metadata refresh does not change these bindings.
 */
public final class PersistenceUnitListenerRegistry {
    private record Binding(Object listener, Map<Class<?>, Method> postCreate, Map<Class<?>, Method> preClose) { }

    private final List<Binding> bindings;
    private final List<InjectionManager<Object>> injectionManagers;
    private int owners = 1;
    private boolean factoryReleased;

    public PersistenceUnitListenerRegistry(List<PersistenceUnitListener> definitions, AbstractSession session) {
        List<PersistenceUnitListener.Resolved> resolved = definitions.stream()
                .map(definition -> definition.resolve(session.getLoader())).toList();
        List<InjectionManager<Object>> managers = new ArrayList<>(resolved.size());
        List<Binding> collected = new ArrayList<>(resolved.size());
        try {
            for (PersistenceUnitListener.Resolved definition : resolved) {
                // Each existing injection manager owns one creational context.
                // Keep contexts and failure cleanup independent between listeners.
                InjectionManager<Object> injectionManager = session.createInjectionManager(
                        session.getProperty(PersistenceUnitProperties.CDI_BEANMANAGER));
                managers.add(injectionManager);
                Object listener = null;
                try {
                    @SuppressWarnings("unchecked")
                    Class<Object> listenerClass = (Class<Object>) definition.listenerClass();
                    listener = injectionManager.createManagedBeanAndInjectDependencies(listenerClass);
                } catch (Exception failure) {
                    session.logThrowable(SessionLog.FINEST, SessionLog.JPA, failure);
                }
                if (listener == null) {
                    listener = PrivilegedAccessHelper.callDoPrivileged(() -> {
                        try {
                            return PrivilegedAccessHelper.newInstanceFromClass(definition.listenerClass());
                        } catch (IllegalAccessException | InstantiationException failure) {
                            throw ValidationException.errorInstantiatingClass(definition.listenerClass(), failure);
                        }
                    });
                }
                collected.add(new Binding(listener, Map.copyOf(definition.postCreate()), Map.copyOf(definition.preClose())));
            }
            bindings = List.copyOf(collected);
            injectionManagers = List.copyOf(managers);
        } catch (RuntimeException | Error failure) {
            for (InjectionManager<Object> manager : managers) {
                cleanup(failure, manager::cleanUp);
            }
            throw failure;
        }
    }

    /** M7 leaves exceptions undefined: creation fails fast and the caller cleans up. */
    public void postCreate(Class<?> targetType, Object target) {
        retainAgent();
        Throwable failure = null;
        try {
            for (Binding binding : bindings) {
                invoke(binding.postCreate().get(targetType), binding.listener(), target);
            }
        } catch (RuntimeException | Error exception) {
            failure = exception;
        }
        // A callback may close the factory. Its bean must survive the entire
        // active dispatch, including the remaining callback chain.
        failure = cleanup(failure, this::releaseAgent);
        rethrow(failure);
    }

    /** Provider policy: attempt every close callback, then let the caller finish cleanup. */
    public Throwable preClose(Class<?> targetType, Object target) {
        try {
            retainAgent();
        } catch (RuntimeException | Error failure) {
            // Ownership can end after a manager's openness check. Its caller
            // must still finish resource cleanup without invoking a destroyed bean.
            return failure;
        }
        Throwable failure = null;
        for (Binding binding : bindings) {
            failure = cleanup(failure, () -> invoke(binding.preClose().get(targetType), binding.listener(), target));
        }
        return cleanup(failure, this::releaseAgent);
    }

    public synchronized void retainAgent() {
        if (owners == 0) {
            throw new IllegalStateException("Persistence unit lifecycle listeners are closed");
        }
        owners++;
    }

    public void releaseFactory() {
        synchronized (this) {
            if (factoryReleased) {
                return;
            }
            factoryReleased = true;
        }
        releaseAgent();
    }

    public void releaseAgent() {
        boolean dispose;
        synchronized (this) {
            dispose = --owners == 0;
        }
        if (dispose) {
            Throwable failure = null;
            for (InjectionManager<Object> manager : injectionManagers) {
                failure = cleanup(failure, manager::cleanUp);
            }
            rethrow(failure);
        }
    }

    private static void invoke(Method method, Object listener, Object target) {
        if (method == null) {
            return;
        }
        PrivilegedAccessHelper.callDoPrivileged(() -> {
            try {
                PrivilegedAccessHelper.invokeMethod(method, listener, new Object[] {target});
            } catch (IllegalAccessException failure) {
                throw ValidationException.invalidCallbackMethod(listener.getClass(), method.toString());
            } catch (InvocationTargetException failure) {
                rethrow(failure.getCause());
            }
            return null;
        });
    }

    public static Throwable cleanup(Throwable primary, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException | Error failure) {
            if (primary == null) {
                return failure;
            }
            if (primary != failure) {
                primary.addSuppressed(failure);
            }
        }
        return primary;
    }

    public static void rethrow(Throwable failure) {
        if (failure instanceof RuntimeException exception) {
            throw exception;
        }
        if (failure instanceof Error error) {
            throw error;
        }
        if (failure != null) {
            throw new PersistenceException(failure);
        }
    }
}
