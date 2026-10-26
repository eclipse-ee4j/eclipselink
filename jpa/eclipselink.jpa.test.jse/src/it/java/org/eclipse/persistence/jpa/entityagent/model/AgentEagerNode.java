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

/** Mapped only by the runtime fixture so the static weaver leaves this class unchanged. */
public class AgentEagerNode {
    public long id;
    public String name;
    public String detail;
    public AgentEagerNode next;
    private int loads;

    public void loaded() { loads++; }
    public AgentEagerNode getNext() { return next; }
    public String getDetail() { return detail; }
    public int getLoads() { return loads; }
}
