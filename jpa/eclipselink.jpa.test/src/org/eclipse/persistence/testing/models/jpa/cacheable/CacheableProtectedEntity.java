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

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.CacheIsolationType;

@Entity(name="JPA_CACHEABLE_PROTECTED")
@Cacheable(false)
@Cache(isolation=CacheIsolationType.PROTECTED)
public class CacheableProtectedEntity {
    private int id;

    protected String name;

    protected CacheableForceProtectedEntity forcedProtected;


    public CacheableProtectedEntity() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="CACHEABLE_TABLE_GENERATOR")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the forcedProtected
     */
    @OneToOne(fetch=FetchType.LAZY)
    public CacheableForceProtectedEntity getForcedProtected() {
        return forcedProtected;
    }

    /**
     * @param forcedProtected the forcedProtected to set
     */
    public void setForcedProtected(CacheableForceProtectedEntity forcedProtected) {
        this.forcedProtected = forcedProtected;
    }




}
