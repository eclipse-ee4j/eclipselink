/*******************************************************************************
 * Copyright (c) 2014, 2015  IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     06/25/2014-2.5.2 Rick Curtis
 *       - 438177: Test M2M map
 *******************************************************************************/
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
