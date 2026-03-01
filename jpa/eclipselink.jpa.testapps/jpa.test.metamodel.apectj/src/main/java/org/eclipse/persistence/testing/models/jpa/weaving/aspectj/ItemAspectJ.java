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

// Contributors:
//     Oracle - initial implementation
package org.eclipse.persistence.testing.models.jpa.weaving.aspectj;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="ASPECTJ_ITEM")
@EntityListeners({EntityListenerAspectJ.class})
public class ItemAspectJ {
    @Id
    private long id;
    private String name;

    public ItemAspectJ() {
    }

    public ItemAspectJ(long id, String aaaa) {
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

    @Override
    public String toString() {
        return "ItemAspectJ{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
