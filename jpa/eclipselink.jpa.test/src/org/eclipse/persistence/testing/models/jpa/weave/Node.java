/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.jpa.weave;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="JPA21_NODE")
public class Node {

    @Id
    protected String id;

    protected int availableBufferCapacity = 10;

    @Version
    protected Integer version;

    public Node(String id) {
        this.id = id;
    }

    protected Node() {
    }

    public String getId() {
        return id;
    }

    public int getAvailableBufferCapacity() {
        return availableBufferCapacity;
    }

    public void reserveBufferCapacity() {
        availableBufferCapacity--;
    }

    public Integer getVersion() {
        return version;
    }

/*
    public void setVersion(Integer version) {
        this.version = version;
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return availableBufferCapacity == node.availableBufferCapacity && Objects.equals(id, node.id) && Objects.equals(version, node.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, availableBufferCapacity, version);
    }
}