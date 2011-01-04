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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

@Entity
@Table(name = "ZipAreas")
@IdClass(value = ZipAreaId.class)
public class ZipArea implements Serializable
{
    @Id
    @Column(name = "country_code")
    private String countryCode;

    @Id
    @Column(name = "zip_code")
    private String zipCode;

    @Id
    @Column(name = "state_code")
    private String stateCode;

    @Id
    @Column(name = "city_name")
    private String cityName;

    @ManyToOne
    @PrimaryKeyJoinColumns(value = {@PrimaryKeyJoinColumn(name = "country_code", referencedColumnName = "country_code"), @PrimaryKeyJoinColumn(name = "zip_code", referencedColumnName = "code")})
    private Zip zip = null;

    @ManyToOne
    @PrimaryKeyJoinColumns(value = {@PrimaryKeyJoinColumn(name = "country_code", referencedColumnName = "country_code"), @PrimaryKeyJoinColumn(name = "state_code", referencedColumnName = "state_code"), @PrimaryKeyJoinColumn(name = "city_name", referencedColumnName = "name")})
    private City city = null;

    public ZipArea()
    {
    }

    public ZipArea(String countryCode, String zipCode, String stateCode, String cityName)
    {
        this.countryCode = countryCode;
        this.zipCode = zipCode;
        this.stateCode = stateCode;
        this.cityName = cityName;

        if ( countryCode != null && zipCode != null )
        {
            this.zip = new Zip(countryCode, zipCode);
        }

        if ( countryCode != null && stateCode != null && cityName != null )
        {
            this.city = new City(countryCode, stateCode, cityName);
        }
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public String getStateCode()
    {
        return stateCode;
    }

    public String getCityName()
    {
        return cityName;
    }

    public Zip getZip()
    {
        return zip;
    }

    public void setZip(Zip zip)
    {
        this.zip = zip;
        this.countryCode = zip.getCountryCode();
        this.zipCode = zip.getCode();
    }

    public City getCity()
    {
        return city;
    }

    public void setCity(City city)
    {
        this.city = city;
        this.countryCode = city.getCountryCode();
        this.stateCode = city.getStateCode();
        this.cityName = city.getName();
    }
}
