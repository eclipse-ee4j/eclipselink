/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA21_CONTINENT")
public class Continent {

    @Id
    private String id;

    @OneToMany
    List<Country> countries;

    public Continent() {
        countries = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String continentId) {
        this.id = continentId;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countriesList) {
        this.countries = countriesList;
    }
}
