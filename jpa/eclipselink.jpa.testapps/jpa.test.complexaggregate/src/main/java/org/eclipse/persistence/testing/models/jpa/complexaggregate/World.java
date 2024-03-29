/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.TABLE;

@Entity
@Table(name="CMP3_WORLD")
public class World implements Serializable {
    private int id;
    private Collection<CitySlicker> citySlickers;
    private Collection<CountryDweller> countryDwellers;

    public World () {
        citySlickers = new Vector<>();
        countryDwellers = new Vector<>();
    }

    public void addCitySlicker(CitySlicker citySlicker) {
        citySlickers.add(citySlicker);
        citySlicker.setWorld(this);
    }

    public void addCountryDweller(CountryDweller countryDweller) {
        countryDwellers.add(countryDweller);
        countryDweller.setWorld(this);
    }

    @OneToMany(fetch=EAGER, mappedBy="world")
    @OrderBy // will default to the embedded id fields.
    public Collection<CitySlicker> getCitySlickers() {
        return citySlickers;
    }

    @OneToMany(fetch=EAGER, mappedBy="world")
    @OrderBy("age, getGender DESC")
    public Collection<CountryDweller> getCountryDwellers() {
        return countryDwellers;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="WORLD_TABLE_GENERATOR")
    @TableGenerator(
        name="WORLD_TABLE_GENERATOR",
        table="CMP3_WORLD_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="WORLD_SEQ"
    )
    public int getId() {
        return id;
    }

    public void setCitySlickers(Collection<CitySlicker> citySlickers) {
        this.citySlickers = citySlickers;
    }

    public void setCountryDwellers(Collection<CountryDweller> countryDwellers) {
        this.countryDwellers = countryDwellers;
    }

    public void setId(int id) {
        this.id = id;
    }
}
