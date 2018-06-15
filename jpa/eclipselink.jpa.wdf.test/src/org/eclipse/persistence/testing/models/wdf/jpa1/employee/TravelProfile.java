/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.testing.framework.wdf.customizer.AdjustArrayTypeCustomizer;

@Entity
@Table(name = "TMP_PROFILE")
@Customizer(AdjustArrayTypeCustomizer.class)
public class TravelProfile {

    static final String BINARY_16_COLUMN = "BINARY(16)";
    static final String BINARY_16_COLUMN_NOT_NULL = "BINARY(16) NOT NULL";

    /**
     * @param guid
     *            The guid to set.
     */
    public void setGuid(byte[] guid) {
        this.guid = guid;
    }


    private byte[] guid = new byte[16];
    private boolean smoker;
    private String preferredAirline;

    public TravelProfile() {

    }

    public TravelProfile(byte[] aGuid, boolean isSmoker, String anAirline) {
        guid = aGuid;
        smoker = isSmoker;
        preferredAirline = anAirline;
    }

    /**
     * @return Returns the preferredAirline.
     */
    @Basic
    @Column(name = "AIRLINE")
    public String getPreferredAirline() {
        return preferredAirline;
    }

    /**
     * @param preferredAirline
     *            The preferredAirline to set.
     */
    public void setPreferredAirline(String preferredAirline) {
        this.preferredAirline = preferredAirline;
    }

    /**
     * @return Returns the smoker.
     */
    @Basic
    public boolean getSmoker() {
        return smoker;
    }

    /**
     * @param smoker
     *            The smoker to set.
     */
    public void setSmoker(boolean smoker) {
        this.smoker = smoker;
    }

    /**
     * @return Returns the guid.
     */
    @Id
    @Column(length = 16, columnDefinition=BINARY_16_COLUMN_NOT_NULL)
    public byte[] getGuid() {
        return guid;
    }

    private Set<Vehicle> vehicles;

    @SuppressWarnings("unchecked")
    @ManyToMany(mappedBy = "profiles", targetEntity = Vehicle.class)
    public Set getVehicles() {
        return vehicles;
    }

    public void setVehicles(Set<Vehicle> vehic) {
        vehicles = vehic;
    }

}
