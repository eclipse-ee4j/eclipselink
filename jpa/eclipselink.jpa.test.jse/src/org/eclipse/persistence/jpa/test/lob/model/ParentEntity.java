/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/14/2018-2.7 Will Dazey
//       - 529602: Added support for CLOBs in DELETE statements for Oracle
package org.eclipse.persistence.jpa.test.lob.model;

import java.util.Collections;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

@Entity
public class ParentEntity {

    @Id private int id;

    @ElementCollection
    @CollectionTable(
        joinColumns = {
            @JoinColumn(name = "parent_id", referencedColumnName = "id")
        })
    private Set<CollectedEntity> subs;

    public ParentEntity() {}

    public ParentEntity(final int id, final Set<CollectedEntity> subs) {
        this.id = id;
        this.subs = Collections.unmodifiableSet(subs);
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public Set<CollectedEntity> getSubs() {
        return subs;
    }

    public void setSubs(final Set<CollectedEntity> subs) {
        this.subs = subs;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof ParentEntity) {
            final ParentEntity that = (ParentEntity) object;
            return (this.id == that.id);
        }
        return false;
    }
}
