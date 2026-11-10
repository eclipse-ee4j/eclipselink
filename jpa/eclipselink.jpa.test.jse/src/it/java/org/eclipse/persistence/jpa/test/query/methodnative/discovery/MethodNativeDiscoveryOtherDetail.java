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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.query.NativeQuery;

/** A second implicit embeddable exercises duplicate discovery of the shared superclass. */
@Embeddable
public class MethodNativeDiscoveryOtherDetail extends MethodNativeDiscoveryBase {
    @Column(name = "OTHER_TEXT")
    public String text;

    public MethodNativeDiscoveryOtherDetail() {
    }

    public MethodNativeDiscoveryOtherDetail(String text) {
        this.text = text;
    }

    @NativeQuery("SELECT OTHER_TEXT FROM JPA_METHOD_NATIVE_DISCOVERY WHERE ID = ?1")
    public String otherText(Integer id) {
        throw new AssertionError("Query declarations must not be invoked");
    }
}
