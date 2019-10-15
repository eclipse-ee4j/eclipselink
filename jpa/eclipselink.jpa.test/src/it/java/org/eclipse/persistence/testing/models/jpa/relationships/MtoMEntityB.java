/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2015 IBM Corporation.
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
//     06/25/2014-2.5.2 Rick Curtis
//       - 438177: Test M2M map
package org.eclipse.persistence.testing.models.jpa.relationships;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MTOMENTITYB")
@Cacheable(false)
public class MtoMEntityB {
    @Id
    private int id;

    private String name;

    public MtoMEntityB() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MtoMEntityB [id=" + id + ", name=" + name + "]";
    }
}
