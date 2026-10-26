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

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "EA_GRAPH_LAZY")
public class AgentLazyNode {
    @Id
    public long id;
    public String name;
    @Basic(fetch = FetchType.LAZY)
    public String detail;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEXT_ID")
    public AgentLazyNode next;
    @Transient
    private int loads;

    @PostLoad
    public void loaded() { loads++; }
    public AgentLazyNode getNext() { return next; }
    public String getDetail() { return detail; }
    public int getLoads() { return loads; }
}
