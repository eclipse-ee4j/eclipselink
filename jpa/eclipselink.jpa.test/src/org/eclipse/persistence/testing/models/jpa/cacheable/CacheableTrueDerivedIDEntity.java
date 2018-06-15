/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - fix for bug 352533
package org.eclipse.persistence.testing.models.jpa.cacheable;

import javax.persistence.Cacheable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="JPA_CACHEABLE_TRUE_DER")
@Cacheable(true)
public class CacheableTrueDerivedIDEntity {

    @EmbeddedId
    protected CacheableTrueDerivedIDPK pk;

    @OneToOne
    @JoinColumn(name="CF_ID")
    protected CacheableFalseEntity cacheableFalse;

    public CacheableTrueDerivedIDEntity(){};

    public CacheableTrueDerivedIDEntity(String description, CacheableFalseEntity cacheableFalse){
        this.pk = new CacheableTrueDerivedIDPK(description, cacheableFalse.getId());
        this.cacheableFalse = cacheableFalse;
    }

    public CacheableFalseEntity getCacheableFalse() {
        return cacheableFalse;
    }

    public void setCacheableFalse(CacheableFalseEntity cacheableFalse) {
        this.cacheableFalse = cacheableFalse;
    }

    public CacheableTrueDerivedIDPK getPk() {
        return pk;
    }

    public void setPk(CacheableTrueDerivedIDPK pk) {
        this.pk = pk;
    }

}

