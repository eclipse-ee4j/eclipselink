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
//     06/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
package org.eclipse.persistence.testing.models.jpa.xml.cacheable;

public class CacheableFalseEntity {
    private int id;
    protected CacheableProtectedEntity protectedEntity;

    public CacheableFalseEntity() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the protectedEntity
     */
    public CacheableProtectedEntity getProtectedEntity() {
        return protectedEntity;
    }

    /**
     * @param protectedEntity the protectedEntity to set
     */
    public void setProtectedEntity(CacheableProtectedEntity protectedEntity) {
        this.protectedEntity = protectedEntity;
    }
}
