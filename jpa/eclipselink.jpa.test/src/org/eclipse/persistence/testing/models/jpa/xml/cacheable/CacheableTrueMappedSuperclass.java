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
//     06/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
package org.eclipse.persistence.testing.models.jpa.xml.cacheable;

public class CacheableTrueMappedSuperclass {
    private int id;

    public CacheableTrueMappedSuperclass() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
