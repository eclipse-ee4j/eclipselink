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
package org.eclipse.persistence.jpa.entityagent.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AgentCompositeKey implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "TENANT_ID")
    public Integer tenant;
    @Column(name = "ITEM_CODE")
    public String code;

    public AgentCompositeKey() {
    }

    public AgentCompositeKey(Integer tenant, String code) {
        this.tenant = tenant;
        this.code = code;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AgentCompositeKey key
                && Objects.equals(tenant, key.tenant) && Objects.equals(code, key.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenant, code);
    }
}
