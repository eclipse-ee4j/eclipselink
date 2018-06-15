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

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Cacheable(true)
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("91")
public class MotorVehicle extends Vehicle {

    @Basic
    @Column(name = "LICENSE_PLATE")
    protected String licensePlateNumber;

    @Basic
    protected String model;

    @OneToOne(cascade = { CascadeType.PERSIST }, fetch = LAZY)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected Employee driver;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSMISSION_TYPE")
    protected TransmissionType transmissionType;

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setDriver(Employee driver) {
        this.driver = driver;
    }

    public Employee getDriver() {
        return driver;
    }

    public void setTransmissionType(TransmissionType transmissionType) {
        this.transmissionType = transmissionType;
    }

    public TransmissionType getTransmissionType() {
        return transmissionType;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

}
