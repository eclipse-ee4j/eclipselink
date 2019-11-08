/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.models.jpa.inherited;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class NodeImpl implements Node, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    protected Long id;
    protected String name;

    public NodeImpl() {
    }

    public synchronized boolean collect(Node node, Map<Node, Set<Node>> variables) {
        if (this == node) {
            return true;
        }
        Node variable;
        Node match;
        if (isVariable() && node.isVariable()) {
            return false;
        } else if (isVariable()) {
            variable = this;
            match = node;
        } else if (node.isVariable()) {
            variable = node;
            match = this;
        } else {
            return false;
        }
        Set<Node> matches = variables.get(variable);
        if (match == null) {
            matches = new HashSet<Node>();
            variables.put(variable, matches);
        }
        if (matches.contains(match)) {
            return true;
        }
        matches.add(match);
        return true;
    }

    public boolean isVariable() {
        return true;
    }

}
