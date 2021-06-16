/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - initial development
package org.eclipse.persistence.testing.models.jpa.cacheable;

import static jakarta.persistence.GenerationType.TABLE;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.OneToOne;

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
