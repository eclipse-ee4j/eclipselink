/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package eclipselink.jpa.bugtest.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "TEST_TAB_DETAIL")
public class TestEntityDetail {
    @Id
    private long id;

    private String name;

    @ManyToOne()
    @JoinColumn(name = "MASTER_ID_FK")
    private TestEntityMaster master;

    public TestEntityDetail() {
    }

    public TestEntityDetail(long id) {
        this.id = id;
    }

    public TestEntityDetail(long id, String name) {
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

    public TestEntityMaster getMaster() {
        return master;
    }

    public void setMaster(TestEntityMaster master) {
        this.master = master;
    }
}
