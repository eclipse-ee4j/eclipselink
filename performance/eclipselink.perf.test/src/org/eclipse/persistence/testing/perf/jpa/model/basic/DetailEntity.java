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

// Contributors:
//              Oracle - initial implementation
package org.eclipse.persistence.testing.perf.jpa.model.basic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "P2_DETAIL")
public class DetailEntity {
    @Id
    private long id;

    private String name;

    @ManyToOne()
    @JoinColumn(name = "MASTER_ID_FK")
    private MasterEntity master;

    public DetailEntity() {
    }

    public DetailEntity(long id) {
        this.id = id;
    }

    public DetailEntity(long id, String name, MasterEntity master) {
        this.id = id;
        this.name = name;
        this.master = master;
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

    public MasterEntity getMaster() {
        return master;
    }

    public void setMaster(MasterEntity master) {
        this.master = master;
    }

    @Override
    public String toString() {
        return "DetailEntity{" +
                "id=" + id +
                ", name='" + name +
                '}';
    }
}
