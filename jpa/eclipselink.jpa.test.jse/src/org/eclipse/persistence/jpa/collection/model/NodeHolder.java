/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jpa.collection.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

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
            nodes = new HashSet<Integer>();
        }
        nodes.add(id);
    }

    public void removeNodes(int id) {
        nodes.remove(new Integer(id));
    }

    public int getId() {
        return id;
    }
}
