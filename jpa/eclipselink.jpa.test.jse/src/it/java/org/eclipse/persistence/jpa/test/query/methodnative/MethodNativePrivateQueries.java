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
package org.eclipse.persistence.jpa.test.query.methodnative;

import jakarta.persistence.query.NativeQuery;

public final class MethodNativePrivateQueries {
    static {
        MethodNativeTracker.initializations++;
    }

    private MethodNativePrivateQueries() {
        throw new AssertionError("Query declaration holders must not be instantiated");
    }

    @NativeQuery("SELECT TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    private static String hidden(Integer id) {
        throw new AssertionError("Query declarations must not be invoked");
    }

    @NativeQuery("SELECT TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    public static String visible(Integer id) {
        throw new AssertionError("Query declarations must not be invoked");
    }
}
