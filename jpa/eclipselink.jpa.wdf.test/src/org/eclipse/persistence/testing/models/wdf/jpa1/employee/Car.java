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

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Cacheable(true)
@Entity
@DiscriminatorValue("71")
public class Car extends MotorVehicle {

    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "TOP_SPEED")
    protected short topSpeed;

    public void setTopSpeed(short topSpeed) {
        this.topSpeed = topSpeed;
    }

    public short getTopSpeed() {
        return topSpeed;
    }

}
