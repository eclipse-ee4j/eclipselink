/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/22/2009 - Guy Pelletier/Prakash Selvaraj - added tests for DDL generation of table per class
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

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
