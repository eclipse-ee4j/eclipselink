/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - test for 345478
package org.eclipse.persistence.testing.models.jpa.cacheable;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
