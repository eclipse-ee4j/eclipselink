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
package org.eclipse.persistence.jpa.test.query.methodnative.discovery;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.query.NativeQuery;

/** Shared only by embeddables, never an entity superclass or explicitly listed PU class. */
@MappedSuperclass
public class MethodNativeDiscoveryBase {
    @NativeQuery("SELECT DETAIL_TEXT FROM JPA_METHOD_NATIVE_DISCOVERY WHERE ID = ?1")
    public String inheritedText(Integer id) {
        throw new AssertionError("Query declarations must not be invoked");
    }
}
