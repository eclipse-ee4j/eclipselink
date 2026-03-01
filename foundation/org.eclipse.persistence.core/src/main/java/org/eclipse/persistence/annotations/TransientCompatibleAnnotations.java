/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     tware - added in fix for bug 277550
package org.eclipse.persistence.annotations;

import java.util.List;

/**
 * PUBLIC:
 * This class is used by our JPA annotation processing to discover which annotations may coexist with a
 * {@linkplain jakarta.persistence.Transient} annotation. If {@linkplain jakarta.persistence.Transient} appears on
 * a field annotation in the {@code jakarta.persistence} or {@code org.eclipse.persistence} package that
 * is not in the list returned by {@link #getTransientCompatibleAnnotations()} an exception will be thrown.
 *
 * @author tware
 */
public class TransientCompatibleAnnotations {

    private static final List<String> transientCompatibleAnnotations = List.of(
            "jakarta.persistence.Access",
            "jakarta.persistence.PersistenceUnit",
            "jakarta.persistence.PersistenceUnits",
            "jakarta.persistence.PersistenceContext",
            "jakarta.persistence.PersistenceContexts",
            "jakarta.persistence.Transient"
    );

    /**
     * PUBLIC:
     * Return a list of classnames of annotations that are compatible with
     * the {@linkplain jakarta.persistence.Transient} annotation.
     */
    public static List<String> getTransientCompatibleAnnotations(){
        return transientCompatibleAnnotations;
    }

    private TransientCompatibleAnnotations() {
        // no instance please
    }
}
