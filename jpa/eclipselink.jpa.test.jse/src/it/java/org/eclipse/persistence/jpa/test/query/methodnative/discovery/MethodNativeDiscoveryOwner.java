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
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** The sole explicitly listed class; its embeddables and their superclass are discovered implicitly. */
@Entity
@Table(name = "JPA_METHOD_NATIVE_DISCOVERY")
public class MethodNativeDiscoveryOwner {
    @Id
    @Column(name = "ID")
    public Integer id;

    @Embedded
    public MethodNativeDiscoveryDetail detail;

    @Embedded
    public MethodNativeDiscoveryOtherDetail otherDetail;

    public MethodNativeDiscoveryOwner() {
    }

    public MethodNativeDiscoveryOwner(Integer id, String detail, String otherDetail) {
        this.id = id;
        this.detail = new MethodNativeDiscoveryDetail(detail);
        this.otherDetail = new MethodNativeDiscoveryOtherDetail(otherDetail);
    }
}
