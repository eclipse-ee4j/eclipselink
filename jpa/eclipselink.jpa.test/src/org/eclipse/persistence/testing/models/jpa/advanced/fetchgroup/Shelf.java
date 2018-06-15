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
//     01/15/2016-Mythily Parthasarathy
//       - 485984: initial API and implementation
package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.CacheIsolationType;

@Entity
@Table(name="JPA_SHELF")
@Cache(isolation = CacheIsolationType.PROTECTED)
public class Shelf {
    @Id
    private Long id;

    private String name;

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

}
