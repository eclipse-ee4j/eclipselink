/*
 * Copyright (c) 2022 IBM Corporation, Oracle, and/or affiliates. All rights reserved.
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
//     IBM - Bug 579409: Add support for accurate IDENTITY generation when the database contains separate TRIGGER objects
package org.eclipse.persistence.jpa.test.sequence.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

@Entity
public class TeaShop {

    @Id
    private long id;
    private String name;

    @ManyToMany
    @JoinTable(name = "TEASHOP_TEA", 
            joinColumns = { @JoinColumn(name = "TEASHOP_ID") }, 
            inverseJoinColumns = { @JoinColumn(name = "TEA_ID") })
    private Set<Tea> teas;

    public TeaShop() {
    }

    public TeaShop(long id, String name) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public Set<Tea> getTeas() {
        return this.teas;
    }

    public void setTeas(Set<Tea> teas) {
        this.teas = teas;
    }

}