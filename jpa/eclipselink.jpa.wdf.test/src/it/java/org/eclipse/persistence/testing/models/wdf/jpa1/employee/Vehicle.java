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

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;

import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.testing.framework.wdf.customizer.AdjustArrayTypeCustomizer;

@Cacheable(value = true)
@Entity
@Table(name = "TMP_VEHICLE")
@EntityListeners( { VehicleListener.class })
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorValue("-1")
@Customizer(AdjustArrayTypeCustomizer.class)
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    protected @Id
    @TableGenerator(name = "VehicleGenerator", table = "TMP_VEHICLE_GEN", pkColumnName = "BEAN_NAME", valueColumnName = "MAX_ID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VehicleGenerator")
    Short id;

    protected @Basic
    String color;

    protected @Basic
    String brand;

    protected @Version
    int version;

    @ManyToMany
    @JoinTable(name = "TMP_VEHICLE_PROFILE", joinColumns = { @JoinColumn(name = "VEHICLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "PROFILE_ID", columnDefinition=TravelProfile.BINARY_16_COLUMN_NOT_NULL) })
    private Set<TravelProfile> profiles;


    public void setId(Short id) {
        this.id = id;
    }

    public Short getId() {
        return id;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setProfiles(Set<TravelProfile> aProfiles) {
        profiles = aProfiles;
    }

    public Set<TravelProfile> getProfiles() {
        return profiles;
    }

    @SuppressWarnings("unchecked")
    public void addProfile(TravelProfile profile) {
        if (profiles == null) {
            profiles = new HashSet<TravelProfile>();
        }
        profiles.add(profile);

        if (profile.getVehicles() == null) {
            profile.setVehicles(new HashSet<Vehicle>());
        }
        profile.getVehicles().add(this);
    }
}
