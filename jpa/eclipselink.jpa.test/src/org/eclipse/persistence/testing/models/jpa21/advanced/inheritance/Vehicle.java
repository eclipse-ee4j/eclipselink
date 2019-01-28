/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.*;
import javax.persistence.*;

@Entity
@EntityListeners(org.eclipse.persistence.testing.models.jpa21.advanced.inheritance.listeners.VehicleListener.class)
@Table(name="JPA21_VEHICLE")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="VEH_TYPE")
@DiscriminatorValue("V")
public abstract class Vehicle implements Serializable {
    private Number id;
    private Company owner;
    private Integer passengerCapacity;
    private VehicleDirectory directory;

    public Vehicle() {}

    public void change() {
        return;
    }

    public abstract String getColor();

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="VEHICLE_TABLE_GENERATOR")
    @TableGenerator(
        name="VEHICLE_TABLE_GENERATOR",
        table="JPA21_INHERITANCE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="VEHICLE_SEQ")
    @Column(name="ID")
    public Number getId() {
        return id;
    }

    @ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
    @JoinColumn(name="OWNER_ID", referencedColumnName="ID")
    public Company getOwner() {
        return owner;
    }

    @Column(name="CAPACITY")
    public Integer getPassengerCapacity() {
        return passengerCapacity;
    }

    /**
     * Return the view for Sybase.
     */
    public static ViewDefinition oracleView() {
        ViewDefinition definition = new ViewDefinition();

        definition.setName("AllVehicles");
        definition.setSelectClause("Select V.*, F.FUEL_CAP, F.FUEL_TYP, B.DESCRIP, B.DRIVER_ID, C.CDESCRIP" + " from VEHICLE V, FUEL_VEH F, BUS B, CAR C" + " where V.ID = F.ID (+) AND V.ID = B.ID (+) AND V.ID = C.ID (+)");

        return definition;
    }

    public abstract void setColor(String color);

    public void setId(Number id) {
        this.id = id;
    }

    public void setOwner(Company ownerCompany) {
        owner = ownerCompany;
    }

    public void setPassengerCapacity(Integer capacity) {
        passengerCapacity = capacity;
    }

    @ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
    @JoinColumn(name="DIRECTORY_ID", referencedColumnName="ID")
    public VehicleDirectory getDirectory() {
        return directory;
    }

    public void setDirectory(VehicleDirectory directory) {
        this.directory = directory;
    }

    /**
     * Return the view for Sybase.
     */
    public static ViewDefinition sybaseView() {
        ViewDefinition definition = new ViewDefinition();

        definition.setName("AllVehicles");
        definition.setSelectClause("Select V.*, F.FUEL_CAP, F.FUEL_TYP, B.DESCRIP, B.DRIVER_ID, C.CDESCRIP" + " from VEHICLE V, FUEL_VEH F, BUS B, CAR C" + " where V.ID *= F.ID AND V.ID *= B.ID AND V.ID *= C.ID");

        return definition;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + "(" + id + ")";
    }
}
