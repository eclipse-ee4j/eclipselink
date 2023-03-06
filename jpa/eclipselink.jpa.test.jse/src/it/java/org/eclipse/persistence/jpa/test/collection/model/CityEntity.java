/*
 * Copyright (c) 2023 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.jpa.test.collection.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CityEntity {

    @Id
    public String name;

    public Set<Integer> areaCodes;

    public CityEntity() {}

    public CityEntity(String name, Set<Integer> areaCodes) {
        this.name = name;
        this.areaCodes = areaCodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Integer> getAreaCodes() {
        return areaCodes;
    }

    public void setAreaCodes(Set<Integer> areaCodes) {
        this.areaCodes = areaCodes;
    }
}
