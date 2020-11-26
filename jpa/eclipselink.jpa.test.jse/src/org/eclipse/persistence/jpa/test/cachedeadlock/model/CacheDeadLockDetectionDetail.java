/*******************************************************************************
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
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
