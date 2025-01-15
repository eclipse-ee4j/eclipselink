/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2011, 2025 Karsten Wutzke. All rights reserved.
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
//     01/04/2011-2.3 Guy Pelletier for Karsten Wutzke
//       - 330628: @PrimaryKeyJoinColumn(...) is not working equivalently to @JoinColumn(..., insertable = false, updatable = false)
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
    private Set<ZipArea> zipAreas = new HashSet<>();

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
