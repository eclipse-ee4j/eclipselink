/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
