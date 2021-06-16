/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Vikram Bhatia - initial API and implementation
package org.eclipse.persistence.testing.models.vehicle;

/**
 * This tests;
 * <ul>
 * <li> the query populated with wrong select list items.
 */
public class CarOwner implements java.io.Serializable {
    public Number id;
    public String name;
    public SportsCar car;
    public SportsCar lastCar;

    public CarOwner() {
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public void setCar(SportsCar car) {
        this.car = car;
    }

    public void setLastCar(SportsCar lastCar) {
        this.lastCar = lastCar;
    }

    public SportsCar getCar() {
        return car;
    }

    public SportsCar getLastCar() {
        return lastCar;
    }

    public void setName(String aName) {
        name = aName;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.name;
    }

    public static CarOwner example1() {
        CarOwner example = new CarOwner();

        example.setId(1001);
        example.setName("Raymen");
        example.setCar(SportsCar.example1());
        example.setLastCar(SportsCar.example2());
        return example;
    }
}
