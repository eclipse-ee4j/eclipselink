/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa.test.cachedeadlock.model;

import javax.persistence.*;

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
