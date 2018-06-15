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
//     04/02/2008-1.0M6 Guy Pelletier
//       - 224155: embeddable-attributes should be extended in the EclipseLink ORM.XML schema
//     08/27/2008-1.1 Guy Pelletier
//       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.xml.complexaggregate;

import java.util.Vector;
import java.util.Collection;
import java.io.Serializable;

public class World implements Serializable {
    private int id;
    private Collection<CitySlicker> citySlickers;
    private Collection<CountryDweller> countryDwellers;

    public World () {
        citySlickers = new Vector<CitySlicker>();
        countryDwellers = new Vector<CountryDweller>();
    }

    public void addCitySlicker(CitySlicker citySlicker) {
        citySlickers.add(citySlicker);
        citySlicker.setWorld(this);
    }

    public void addCountryDweller(CountryDweller countryDweller) {
        countryDwellers.add(countryDweller);
        countryDweller.setWorld(this);
    }

    public Collection<CitySlicker> getCitySlickers() {
        return citySlickers;
    }

    public Collection<CountryDweller> getCountryDwellers() {
        return countryDwellers;
    }

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
