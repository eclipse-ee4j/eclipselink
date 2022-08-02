/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

import java.io.Serializable;

@Entity
@NamedQueries({
  @NamedQuery(name = "SUV.findAll", query = "select o from SUV o")
})
@Inheritance
@DiscriminatorValue("SUV")
public class SUV extends Car implements Serializable
{

   private Float groundClearence;

   private Float wheelBase;


    public SUV() {
    }

    public void setGroundClearence(Float groundClearence) {
        this.groundClearence = groundClearence;
    }

    public Float getGroundClearence() {
        return groundClearence;
    }

    public void setWheelBase(Float wheelBase) {
        this.wheelBase = wheelBase;
    }

    public Float getWheelBase() {
        return wheelBase;
    }
}
