/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     06/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
package org.eclipse.persistence.testing.models.jpa.cacheable;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.TABLE;

@Entity(name="JPA_CACHEABLE_FALSE")
@Cacheable(false)
public class CacheableFalseEntity {
    protected int id;
    protected CacheableProtectedEntity protectedEntity;
    List<CacheableFalseDetail> details = new ArrayList<>();
    List<CacheableFalseDetailWithBackPointer> detailsBackPointer;

    public CacheableFalseEntity() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="CACHEABLE_TABLE_GENERATOR")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "JPA_CACHEABLE_FALSE_TO_DETAIL",
            joinColumns=@JoinColumn(name="ENTITY_ID"),
            inverseJoinColumns=@JoinColumn(name="DETAIL_ID")
    )
    @OrderColumn(name = "IND")
    public List<CacheableFalseDetail> getDetails() {
        return details;
    }

    public void setDetails(List<CacheableFalseDetail> details) {
        this.details = details;
    }

    /**
     * @return the protectedEntity
     */
    @OneToOne
    @JoinColumn(name="PROTECTED_FK")
    public CacheableProtectedEntity getProtectedEntity() {
        return protectedEntity;
    }

    /**
     * @param protectedEntity the protectedEntity to set
     */
    public void setProtectedEntity(CacheableProtectedEntity protectedEntity) {
        this.protectedEntity = protectedEntity;
    }

    @OneToMany(cascade = {CascadeType.ALL},  mappedBy="entity")
    public List<CacheableFalseDetailWithBackPointer> getDetailsBackPointer() {
        return detailsBackPointer;
    }

    public void setDetailsBackPointer(
            List<CacheableFalseDetailWithBackPointer> detailsBackPointer) {
        this.detailsBackPointer = detailsBackPointer;
    }
}
