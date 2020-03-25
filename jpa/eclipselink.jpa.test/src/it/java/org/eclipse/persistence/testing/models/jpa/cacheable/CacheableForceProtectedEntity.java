/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - Initial Contribution
package org.eclipse.persistence.testing.models.jpa.cacheable;

import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Cacheable;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.QueryHint;
import jakarta.persistence.TableGenerator;

import org.eclipse.persistence.config.QueryHints;

@Entity(name="JPA_CACHEABLE_FORCE_PROTECTED")
@Inheritance(strategy=SINGLE_TABLE)
@DiscriminatorColumn(name="DTYPE")
@DiscriminatorValue("CFPE")
@Cacheable // defaults to true

public class CacheableForceProtectedEntity {
    protected int id;
    protected String name;
    protected CacheableFalseEntity cacheableFalse;
    protected List<CacheableProtectedEntity> cacheableProtecteds;

    public CacheableForceProtectedEntity() {
        cacheableProtecteds = new ArrayList<CacheableProtectedEntity>();
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="CACHEABLE_TABLE_GENERATOR")
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the cacheableFalse
     */
    @OneToOne
    @JoinColumn(name="FALSE_FK")
    public CacheableFalseEntity getCacheableFalse() {
        return cacheableFalse;
    }

    /**
     * @param cacheableFalse the cacheableFalse to set
     */
    public void setCacheableFalse(CacheableFalseEntity cacheableFalse) {
        this.cacheableFalse = cacheableFalse;
    }

    public String toString() {
        return "CacheableForceProtectedEntity: [" + name + "]";
    }

    /**
     * @return the cacheableProtecteds
     */
    @OneToMany(mappedBy="forcedProtected")
    public List<CacheableProtectedEntity> getCacheableProtecteds() {
        return cacheableProtecteds;
    }

    /**
     * @param cacheableProtecteds the cacheableProtecteds to set
     */
    public void setCacheableProtecteds(List<CacheableProtectedEntity> cacheableProtecteds) {
        this.cacheableProtecteds = cacheableProtecteds;
    }


}
