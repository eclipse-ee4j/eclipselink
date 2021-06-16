/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     tware - fix for bug 352533
package org.eclipse.persistence.testing.models.jpa.cacheable;

import jakarta.persistence.Cacheable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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

