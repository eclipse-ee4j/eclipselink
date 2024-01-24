/*
 * Copyright (c) 2018, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jpa.collection.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "nodeholder")
public class NodeHolder {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ElementCollection(targetClass = Integer.class)
    @CollectionTable(name = "nodes", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "element")
    private Set<Integer> nodes;

    public void setNodes(Set<Integer> nodes) {
        this.nodes = nodes;
    }

    public void addNodes(int id) {
        if (nodes == null) {
            nodes = new HashSet<>();
        }
        nodes.add(id);
    }

    public void removeNodes(int id) {
        nodes.remove(id);
    }

    public int getId() {
        return id;
    }
}
