/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     01/28/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 1)
package org.eclipse.persistence.testing.models.jpa.inherited;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

@Entity
@Table(name="JPA2_LOCATION")
public class Location {
    @Id
    @GeneratedValue(strategy=TABLE, generator="LOCATION_TABLE_GENERATOR")
    @TableGenerator(
        name="LOCATION_TABLE_GENERATOR",
        table="CMP3_LOCATION_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="LOCATION_SEQ")
    private String id;

    @Column(name="CITY")
    private String city;

    @Column(name="COUNTRY")
    private String country;

    public Location() {}

    public Location(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
