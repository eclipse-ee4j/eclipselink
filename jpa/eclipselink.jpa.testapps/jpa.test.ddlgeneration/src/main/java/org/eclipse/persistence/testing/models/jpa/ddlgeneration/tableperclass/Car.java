/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     10/22/2009 - Guy Pelletier/Prakash Selvaraj - added tests for DDL generation of table per class
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Version;

import java.io.Serializable;

@Entity(name="DDL_TPC_CAR")
@NamedQueries({
  @NamedQuery(name = "Car.findAll", query = "select o from DDL_TPC_CAR o")
})
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Car implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "keygen")
    private Integer regNumber;
    @Version
    private Integer version;

    private Float engineCapacity;

    private Float brakeHorsePower;

    private String manufacturer;

    private String colour;

    private String modelName;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(name="CAR_PIC")
    private  byte[] pic;

    public Car() {
    }



    public Integer getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(Integer RegNumber) {
        this.regNumber = RegNumber;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setEngineCapacity(Float engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public Float getEngineCapacity() {
        return engineCapacity;
    }

    public void setBrakeHorsePower(Float brakeHorsePower) {
        this.brakeHorsePower = brakeHorsePower;
    }

    public Float getBrakeHorsePower() {
        return brakeHorsePower;
    }

    public void setManufacturer(String Manufacturer) {
        this.manufacturer = Manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    public byte[] getPic() {
        return pic;
    }
}
