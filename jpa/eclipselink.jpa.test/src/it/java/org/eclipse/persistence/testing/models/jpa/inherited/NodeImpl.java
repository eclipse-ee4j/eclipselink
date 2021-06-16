/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.eclipse.persistence.testing.models.jpa.inherited;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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
