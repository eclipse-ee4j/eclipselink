/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.jpql;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
