/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa.test.cachedeadlock.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cachedeadlock_detail")
@Cacheable(true)
@NamedQuery(name="DetailEntity.findById", query="SELECT t FROM CacheDeadLockDetectionDetail t WHERE t.id = :id")
public class CacheDeadLockDetectionDetail {
    @Id
    private long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "id_fk")
    private CacheDeadLockDetectionMaster master;

    public CacheDeadLockDetectionDetail() {
    }

    public CacheDeadLockDetectionDetail(long id) {
        this.id = id;
    }

    public CacheDeadLockDetectionDetail(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CacheDeadLockDetectionMaster getMaster() {
        return master;
    }

    public void setMaster(CacheDeadLockDetectionMaster master) {
        this.master = master;
    }

    @Override
    public String toString() {
        return "DetailEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", master=" + master +
                '}';
    }
}
