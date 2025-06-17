/*
 * Copyright (c) 2020, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CACHEDEADLOCK_MASTER")
@Cacheable(true)
@NamedQuery(name="MasterEntity.findById", query="SELECT t FROM CacheDeadLockDetectionMaster t WHERE t.id = :id")
public class CacheDeadLockDetectionMaster {

    @Id
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    private List<CacheDeadLockDetectionDetail> details = new ArrayList<>();


    public CacheDeadLockDetectionMaster() {
    }

    public CacheDeadLockDetectionMaster(long id) {
        this.id = id;
    }
    
    public CacheDeadLockDetectionMaster(long id, String name) {
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "master", cascade = CascadeType.ALL)
    @JoinColumn(name = "CACHEDEADLOCK_MASTER_FK", referencedColumnName = "ID")
    public List<CacheDeadLockDetectionDetail> getDetails() {
        return details;
    }

    public void setDetails(List<CacheDeadLockDetectionDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "MasterEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", details=" + details +
                '}';
    }
}
