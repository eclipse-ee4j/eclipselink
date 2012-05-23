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
@Table(name = "Zips")
@IdClass(value = ZipId.class)
public class Zip implements Serializable
{
    @Id
    @Column(name = "country_code")
    private String countryCode;

    @Id
    @Column(name = "code", length=50)
    private String code;

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "country_code", referencedColumnName = "iso_code")
    private Country country = null;

    @OneToMany(targetEntity = ZipArea.class, mappedBy = "zip")
    private Set<ZipArea> zipAreas = new HashSet<ZipArea>();

    public Zip()
    {
    }

    public Zip(String countryCode, String code)
    {
        this.countryCode = countryCode;
        this.code = code;

        if ( countryCode != null )
        {
            this.country = new Country(countryCode);
        }
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
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

    public Set<ZipArea> getZipAreas()
    {
        return zipAreas;
    }

    public void setZipAreas(Set<ZipArea> zipAreas)
    {
        this.zipAreas = zipAreas;
    }
}
