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

// Contributors:
//     02/14/2018-2.7 Will Dazey
//       - 529602: Added support for CLOBs in DELETE statements for Oracle
package org.eclipse.persistence.jpa.test.lob.model;

import java.util.Collections;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

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
