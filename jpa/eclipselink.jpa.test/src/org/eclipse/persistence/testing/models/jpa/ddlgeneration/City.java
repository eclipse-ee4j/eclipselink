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
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

@Entity
@Table(name = "Cities")
@IdClass(value = CityId.class)
public class City implements Serializable
{
    @Id
    @Column(name = "country_code")
    private String countryCode;

    @Id
    @Column(name = "state_code")
    private String stateCode;

    @Id
    @Column(name = "name")
    private String name;

    @ManyToOne
    @PrimaryKeyJoinColumns(value = {@PrimaryKeyJoinColumn(name = "country_code", referencedColumnName = "country_code"), @PrimaryKeyJoinColumn(name = "state_code", referencedColumnName = "iso_code")})
    private State state = null;

    @OneToMany(targetEntity = ZipArea.class, mappedBy = "city")
    private Set<ZipArea> zipAreas = new HashSet<ZipArea>();

    public City()
    {
    }

    public City(String countryCode, String stateCode, String name)
    {
        this.countryCode = countryCode;
        this.stateCode = stateCode;
        this.name = name;

        if ( countryCode != null && stateCode != null )
        {
            this.state = new State(countryCode, stateCode);
        }
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public String getStateCode()
    {
        return stateCode;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
        this.countryCode = state.getCountryCode();
        this.stateCode = state.getIsoCode();
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
