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

import static javax.persistence.GenerationType.TABLE;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.TableGenerator;

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
