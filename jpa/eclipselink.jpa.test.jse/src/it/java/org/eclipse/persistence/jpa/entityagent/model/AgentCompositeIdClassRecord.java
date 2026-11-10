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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity(name = "AgentCompositeIdClassRecord")
@Table(name = "EA_IDCLASS")
@IdClass(AgentCompositeKey.class)
public class AgentCompositeIdClassRecord {
    @Id
    @Column(name = "TENANT_ID")
    public Integer tenant;
    @Id
    @Column(name = "ITEM_CODE")
    public String code;
    @Column(name = "LABEL")
    public String label;
    @Version
    public Integer version;

    public AgentCompositeIdClassRecord() {
    }

    public AgentCompositeIdClassRecord(Integer tenant, String code, String label) {
        this.tenant = tenant;
        this.code = code;
        this.label = label;
    }
}
