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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

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
