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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Countries")
public class Country implements Serializable
{
    @Id
    @Column(name = "iso_code", length=50)
    private String isoCode;

    @Column(name = "name")
    private String name;

    @OneToMany(targetEntity = State.class, mappedBy = "country")
    private Set<State> states = new HashSet<State>();

    @OneToMany(targetEntity = Zip.class, mappedBy = "country")
    private Set<Zip> zips = new HashSet<Zip>();

    public Country()
    {
    }

    public Country(String isoCode)
    {
        this(isoCode, null);
    }

    public Country(String isoCode, String name)
    {
        this.isoCode = isoCode;
        this.name = name;
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

    public Set<State> getStates()
    {
        return states;
    }

    public void setStates(Set<State> states)
    {
        this.states = states;
    }

    public Set<Zip> getZips()
    {
        return zips;
    }

    public void setZips(Set<Zip> zips)
    {
        this.zips = zips;
    }
}
