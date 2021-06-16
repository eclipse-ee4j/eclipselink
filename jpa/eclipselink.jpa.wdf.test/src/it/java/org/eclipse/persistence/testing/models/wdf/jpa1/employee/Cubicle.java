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

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Cacheable(true)
@Entity
@Table(name = "TMP_CUBICLE")
@IdClass(CubiclePrimaryKeyClass.class)
public class Cubicle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    protected Integer floor;
    @Id
    protected Integer place;
    @Basic
    protected String color;
    // <attribute name="employee">
    // <one-to-one target-entity="com.sap.jpa.example.Employee" mapped-by="cubicle"/>
    // </attribute>
    @OneToOne(mappedBy = "cubicle")
    protected Employee employee;

    public Cubicle() {
    }

    public Cubicle(CubiclePrimaryKeyClass key, String color, Employee emp) {
        this(new Integer(key.getFloor().intValue()), new Integer(key.getPlace().intValue()), color, emp);
    }

    public Cubicle(Integer aFloor, Integer aPlace, String aColor, Employee aEmployee) {
        floor = aFloor;
        place = aPlace;
        color = aColor;
        employee = aEmployee;
    }

    public CubiclePrimaryKeyClass getId() {
        return new CubiclePrimaryKeyClass(floor, place);
    }

    public String getColor() {
        return color;
    }

    public Integer getFloor() {
        return floor;
    }

    public Integer getPlace() {
        return place;
    }

    public void setColor(String string) {
        color = string;
    }

    public void setFloor(Integer integer) {
        floor = integer;
    }

    public void setPlace(Integer integer) {
        place = integer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee aEmployee) {
        employee = aEmployee;
    }

}
