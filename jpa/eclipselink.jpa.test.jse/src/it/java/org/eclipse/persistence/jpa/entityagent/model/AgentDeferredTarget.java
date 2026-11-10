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

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "EA_DEFER_TARGET")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AgentDeferredTarget {
    @Id public long id;
    public String label;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    public AgentDeferredOwner owner;
    @Transient private int loads;
    @PostLoad
    public void loaded() {
        if (label == null) throw new IllegalStateException("PostLoad preceded target state");
        loads++;
    }
    public String getLabel() { return label; }
    public int getLoads() { return loads; }
}
