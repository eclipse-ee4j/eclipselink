/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     tware - fix for bug 352533
package org.eclipse.persistence.testing.models.jpa.cacheable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CacheableTrueDerivedIDPK {

    protected String description;

    @Column(name = "CF_ID", insertable = false, updatable = false)
    protected int cacheableFalseID;

    public CacheableTrueDerivedIDPK(){}

    public CacheableTrueDerivedIDPK(String description, int cacheableFalseID){
        this.description = description;
        this.cacheableFalseID = cacheableFalseID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCacheableFalseID() {
        return cacheableFalseID;
    }

    public void setCacheableFalseID(int cacheableFalseID) {
        this.cacheableFalseID = cacheableFalseID;
    }
}
