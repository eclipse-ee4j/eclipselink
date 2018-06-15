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
//     Gordon Yorke - Initial Contribution
package org.eclipse.persistence.testing.models.jpa.xml.cacheable;

public class CacheableForceProtectedEntity {
    private int id;
    protected CacheableFalseEntity cacheableFalse;

    public CacheableForceProtectedEntity() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the cacheableFalse
     */
    public CacheableFalseEntity getCacheableFalse() {
        return cacheableFalse;
    }

    /**
     * @param cacheableFalse the cacheableFalse to set
     */
    public void setCacheableFalse(CacheableFalseEntity cacheableFalse) {
        this.cacheableFalse = cacheableFalse;
    }

}
