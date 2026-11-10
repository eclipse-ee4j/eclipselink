/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.graphfetchoptions.model;

import java.util.function.Consumer;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_GFO_CHILD")
public class GraphFetchChild {
    private static final ThreadLocal<Consumer<GraphFetchChild>> POST_LOAD = new ThreadLocal<>();

    @Id
    private int id;
    private String label;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DETAIL_ID")
    private GraphFetchDetail detail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "OTHER_DETAIL_ID")
    private GraphFetchDetail otherDetail;

    public GraphFetchChild() {
    }

    public GraphFetchChild(int id, GraphFetchDetail detail, GraphFetchDetail otherDetail) {
        this.id = id;
        label = "child-" + id;
        this.detail = detail;
        this.otherDetail = otherDetail;
    }

    public static void setPostLoad(Consumer<GraphFetchChild> callback) {
        if (callback == null) {
            POST_LOAD.remove();
        } else {
            POST_LOAD.set(callback);
        }
    }

    @PostLoad
    private void postLoad() {
        Consumer<GraphFetchChild> callback = POST_LOAD.get();
        if (callback != null) {
            callback.accept(this);
        }
    }

    public int getId() { return id; }
    public String getLabel() { return label; }
    public GraphFetchDetail getDetail() { return detail; }
    public GraphFetchDetail getOtherDetail() { return otherDetail; }
}
