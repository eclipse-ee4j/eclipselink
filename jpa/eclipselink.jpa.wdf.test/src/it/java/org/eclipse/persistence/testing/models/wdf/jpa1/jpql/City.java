/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.jpql;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP_CITY")
public class City implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    protected long id;

    @Enumerated(EnumType.STRING)
    protected Scale type;

    @Enumerated(EnumType.STRING)
    @Column(name = "CITY_ENUM")
    protected CityEnum cityEnum;

    protected String name;

    protected boolean cool;

    @OneToMany
    protected Collection<Cop> cops;

    @OneToMany
    protected Set<Criminal> criminals;

    @Embedded
    @AttributeOverrides( { @AttributeOverride(name = "integer", column = @Column(name = "CITY_TESLA_INT")),
            @AttributeOverride(name = "sound", column = @Column(name = "CITY_TESLA_BLOB")) })
    protected Tesla tesla;

    public Collection<Cop> getCops() {
        return cops;
    }

    public void setCops(Collection<Cop> cops) {
        this.cops = cops;
    }

    public Set<Criminal> getCriminals() {
        return criminals;
    }

    public void setCriminals(Set<Criminal> criminals) {
        this.criminals = criminals;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Tesla getTesla() {
        return tesla;
    }

    public void setTesla(Tesla tesla) {
        this.tesla = tesla;
    }

    public Scale getType() {
        return type;
    }

    public void setType(Scale type) {
        this.type = type;
    }

    public boolean isCool() {
        return cool;
    }

    public void setCool(boolean cool) {
        this.cool = cool;
    }

    public static enum CityEnum {
        BLI, BLA, BLUB
    }
}
