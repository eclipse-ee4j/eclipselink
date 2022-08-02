/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2011, 2022 Karsten Wutzke. All rights reserved.
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
    private Set<State> states = new HashSet<>();

    @OneToMany(targetEntity = Zip.class, mappedBy = "country")
    private Set<Zip> zips = new HashSet<>();

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
