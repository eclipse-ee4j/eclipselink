/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     tware - test for 345478
package org.eclipse.persistence.testing.models.jpa.cacheable;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity(name="JPA_CACHEABLE_FALSE_DETAIL_BP")
@Cacheable(false)
public class CacheableFalseDetailWithBackPointer {

    private int id;

    private CacheableFalseEntity entity = null;

    private String description = null;

    @Id
    @GeneratedValue(strategy=TABLE, generator="CACHEABLE_TABLE_GENERATOR")
    public int getId() {
        return id;
    }

    @ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    public CacheableFalseEntity getEntity() {
        return entity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEntity(CacheableFalseEntity entity) {
        this.entity = entity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
