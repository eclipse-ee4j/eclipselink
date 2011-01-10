/*******************************************************************************
 * Copyright (c) 2011 Karsten Wutzke. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/04/2011-2.3 Guy Pelletier for Karsten Wutzke 
 *       - 330628: @PrimaryKeyJoinColumn(...) is not working equivalently to @JoinColumn(..., insertable = false, updatable = false)
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "States")
@IdClass(value = StateId.class)
public class State implements Serializable
{
    @Id
    @Column(name = "country_code")
    private String countryCode;

    @Id
    @Column(name = "iso_code", length=50)
    private String isoCode;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "country_code", referencedColumnName = "iso_code")
    private Country country = null;

    @OneToMany(targetEntity = City.class, mappedBy = "state")
    private Set<City> cities = new HashSet<City>();

    public State()
    {
    }

    public State(String countryCode, String isoCode)
    {
        this(countryCode, isoCode, null);
    }

    public State(String countryCode, String isoCode, String name)
    {
        this.countryCode = countryCode;
        this.isoCode = isoCode;
        this.name = name;

        if ( countryCode != null )
        {
            this.country = new Country(countryCode);
        }
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public String getIsoCode()
    {
        return isoCode;
    }

    public void setIsoCode(String isoCode)
    {
        this.isoCode = isoCode;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Country getCountry()
    {
        return country;
    }

    public void setCountry(Country country)
    {
        this.country = country;
        this.countryCode = country.getIsoCode();
    }

    public Set<City> getCities()
    {
        return cities;
    }

    public void setCities(Set<City> cities)
    {
        this.cities = cities;
    }
}
