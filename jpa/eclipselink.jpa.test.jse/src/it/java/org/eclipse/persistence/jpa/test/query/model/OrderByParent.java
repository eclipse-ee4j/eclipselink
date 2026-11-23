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
package org.eclipse.persistence.jpa.test.query.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.Customizer;

@Entity
@Table(name = "ORDER_BY_PARENT")
@Customizer(OrderByParentCustomizer.class)
public class OrderByParent {

    @Id
    private long id;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    @OrderBy("name DESC")
    private List<OrderByChild> children = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "CODED_PARENT_ID")
    @OrderBy("detail.code DESC")
    private List<OrderByChild> coded = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "DETAILED_PARENT_ID")
    @OrderBy("detail DESC")
    private List<OrderByChild> detailed = new ArrayList<>();

    @Embedded
    private OrderByGroup group = new OrderByGroup();

    @ElementCollection
    @OrderBy("label DESC")
    private List<OrderByTag> tags = new ArrayList<>();

    @ElementCollection
    @OrderBy("DESC")
    private List<String> labels = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ORDER_BY_NOTES")
    @Column(name = "NOTE")
    private List<String> notes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ORDER_BY_REMARKS")
    @Column(name = "REMARK")
    private List<String> remarks = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ORDER_BY_QUOTED")
    @Column(name = "\"quoted_value\"")
    @OrderBy("DESC")
    private List<String> quoted = new ArrayList<>();

    public OrderByParent() {
    }

    public OrderByParent(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public List<OrderByChild> getChildren() {
        return children;
    }

    public List<OrderByChild> getCoded() {
        return coded;
    }

    public List<OrderByChild> getDetailed() {
        return detailed;
    }

    public OrderByGroup getGroup() {
        return group;
    }

    public List<OrderByTag> getTags() {
        return tags;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<String> getNotes() {
        return notes;
    }

    public List<String> getRemarks() {
        return remarks;
    }

    public List<String> getQuoted() {
        return quoted;
    }

    public void addChild(long childId, String name) {
        children.add(new OrderByChild(childId, name, this));
    }
}
