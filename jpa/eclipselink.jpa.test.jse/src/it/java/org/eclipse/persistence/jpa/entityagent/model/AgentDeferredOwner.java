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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EA_DEFER_OWNER")
public class AgentDeferredOwner {
    @Id public long id;
    public String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAVORITE_ID")
    public AgentDeferredTarget favorite;
    @OneToOne(mappedBy = "owner", fetch = FetchType.LAZY)
    public AgentDeferredTarget inverse;
    public void setFavorite(AgentDeferredTarget favorite) { this.favorite = favorite; }
    public AgentDeferredTarget getFavorite() { return favorite; }
    public AgentDeferredTarget getInverse() { return inverse; }
}
