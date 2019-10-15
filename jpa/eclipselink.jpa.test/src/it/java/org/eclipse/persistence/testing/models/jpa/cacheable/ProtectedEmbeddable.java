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
//     Gordon Yorke - initial development
package org.eclipse.persistence.testing.models.jpa.cacheable;

import static javax.persistence.GenerationType.TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.OneToOne;

@Embeddable
public class ProtectedEmbeddable {
    protected CacheableFalseEntity cacheableFalseEntity;
    protected String sub_name;

    public ProtectedEmbeddable() {}

    /**
     * @return the name
     */
    @Column(name="EMB_NAME")
    public String getName() {
        return sub_name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.sub_name = name;
    }

    /**
     * @return the protectedEntity
     */
    @OneToOne
    @JoinColumn(name="PROTECTED_FK")
    public CacheableFalseEntity getCacheableFalseEntity() {
        return cacheableFalseEntity;
    }

    /**
     * @param protectedEntity the protectedEntity to set
     */
    public void setCacheableFalseEntity(CacheableFalseEntity protectedEntity) {
        this.cacheableFalseEntity = protectedEntity;
    }
}
