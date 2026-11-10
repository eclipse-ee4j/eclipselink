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
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity(name = "AgentOwnedChild")
@Table(name = "EA_OWNED_CHILD")
public class AgentOwnedChild {
    @Id
    public Long id;
    @Column(name = "BUSINESS_NAME")
    public String businessName;
    @Version
    public Integer version;
    @Column(name = "OWNED_PARENT_ID", insertable = false, updatable = false)
    public Long ownedParentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVERSE_PARENT_ID")
    public AgentOwnedParent inverseParent;

    public AgentOwnedChild() {
    }

    public AgentOwnedChild(long id, String businessName) {
        this.id = id;
        this.businessName = businessName;
    }
}
