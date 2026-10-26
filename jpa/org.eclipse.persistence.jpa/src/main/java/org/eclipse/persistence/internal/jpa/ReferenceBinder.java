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
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.Query;
import jakarta.persistence.Reference;

import java.util.List;

/** Applies the common argument and hint contract of named references. */
final class ReferenceBinder {
    private ReferenceBinder() {
    }

    static void bind(Query query, Reference reference) {
        if (reference.getHints() != null) {
            reference.getHints().forEach(query::setHint);
        }
        List<Object> arguments = reference.getArguments();
        if (arguments == null) {
            return;
        }
        boolean named = query.getParameters().stream().anyMatch(parameter -> parameter.getName() != null);
        List<String> names = reference.getParameterNames();
        List<Class<?>> types = reference.getParameterTypes();
        if ((named && (names == null || names.size() != arguments.size()))
                || (types != null && types.size() != arguments.size())) {
            throw new IllegalArgumentException("Reference argument metadata has inconsistent lengths");
        }
        for (int i = 0; i < arguments.size(); i++) {
            @SuppressWarnings("unchecked")
            Class<Object> type = types == null ? null : (Class<Object>) types.get(i);
            if (named) {
                if (type == null) {
                    query.setParameter(names.get(i), arguments.get(i));
                } else {
                    query.setParameter(names.get(i), arguments.get(i), type);
                }
            } else if (type == null) {
                query.setParameter(i + 1, arguments.get(i));
            } else {
                query.setParameter(i + 1, arguments.get(i), type);
            }
        }
    }
}
