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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Persistence;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.eclipse.persistence.indirection.IndirectContainer;

@Entity
@Table(name = "EA_CB_FETCH_OWNER")
public class AgentCallbackFetchOwner {
    @Id
    public int id;
    public String ownerLabel;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "EA_CB_FETCH_TAG", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "TAG_VALUE")
    public List<String> tags = new ArrayList<>();
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "EA_CB_FETCH_NOTE", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "NOTE_VALUE")
    public List<String> notes = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "EA_CB_FETCH_LINK", joinColumns = @JoinColumn(name = "OWNER_ID"),
            inverseJoinColumns = @JoinColumn(name = "TARGET_ID"))
    public List<AgentCallbackFetchTarget> targets = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAVORITE_ID")
    public AgentCallbackFetchTarget favorite;
    @Transient
    public boolean favoriteDeferredBeforeCallback;
    @Transient
    public String callbackFavorite;
    public static final ThreadLocal<Boolean> PARTIAL_PREPARATION = new ThreadLocal<>();
    public static final ThreadLocal<Consumer<AgentCallbackFetchOwner>> AFTER_LOAD = new ThreadLocal<>();
    @Transient
    public boolean tagsDeferredBeforeCallback;
    @Transient
    public boolean targetsDeferredBeforeCallback;
    @Transient
    public int callbackCount;
    @Transient
    public int callbackRows;

    public AgentCallbackFetchOwner() {
    }

    public AgentCallbackFetchTarget getFavorite() {
        return favorite;
    }

    @PostLoad
    public void loaded() {
        if (Boolean.TRUE.equals(PARTIAL_PREPARATION.get())
                && !Persistence.getPersistenceUtil().isLoaded(this, "ownerLabel")) return;
        callbackCount++;
        tagsDeferredBeforeCallback = tags instanceof IndirectContainer container && !container.isInstantiated();
        targetsDeferredBeforeCallback = targets instanceof IndirectContainer container && !container.isInstantiated();
        favoriteDeferredBeforeCallback = !Persistence.getPersistenceUtil().isLoaded(this, "favorite")
                || !Persistence.getPersistenceUtil().isLoaded(getFavorite(), "label");
        callbackRows = tags.size();
        callbackFavorite = getFavorite().getLabel();
        callbackRows += targets.size();
        Consumer<AgentCallbackFetchOwner> callback = AFTER_LOAD.get();
        if (callback != null) {
            callback.accept(this);
        }
    }
}
