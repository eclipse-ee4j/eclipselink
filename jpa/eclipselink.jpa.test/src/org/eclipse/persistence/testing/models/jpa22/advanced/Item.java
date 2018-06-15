/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/06/2013-2.5 Guy Pelletier
//       - 382503: Use of @ConstructorResult with createNativeQuery(sqlString, resultSetMapping) results in NullPointerException
//     02/11/2013-2.5 Guy Pelletier
//       - 365931: @JoinColumn(name="FK_DEPT",insertable = false, updatable = true) causes INSERT statement to include this data value that it is associated with
package org.eclipse.persistence.testing.models.jpa22.advanced;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="JPA22_ITEM")
public class Item {
    @Id
    @GeneratedValue
    @Column(name="ID")
    private Integer itemId;

    @Basic
    private String name;

    public Item() {}

    public Integer getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public void setItemId(Integer id) {
        this.itemId = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Item [" + getName() + "]";
    }
}
