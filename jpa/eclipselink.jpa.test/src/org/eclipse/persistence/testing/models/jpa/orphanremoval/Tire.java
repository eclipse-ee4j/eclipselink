/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/1/2009-2.0 Guy Pelletier/David Minsky
 *       - 249033: JPA 2.0 Orphan removal
 *     8/12/2015-2.6 Mythily Parthasarathy
 *       - 474752: Added TireDetails
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.orphanremoval;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Tire {
    protected String type;
    protected String manufacturer;
    @OneToMany(fetch=FetchType.EAGER, orphanRemoval=true)
    @JoinColumn(name="TIRE_ID")
    private Set<TireDetail> tireDetails; 

    public Tire() {
        super();
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getType() {
        return type;
    }
    
    public Set<TireDetail> getTireDetails() {
        return this.tireDetails;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setTireDetails(Set<TireDetail> details) {
        this.tireDetails = details;
    }

}
