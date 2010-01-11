/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.*;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.InheritanceType.*;

@Entity
@EntityListeners(org.eclipse.persistence.testing.models.jpa.inheritance.listeners.VehicleListener.class)
@Table(name="CMP3_VEHICLE")
@Inheritance(strategy=JOINED)
@DiscriminatorColumn(name="VEH_TYPE")
@DiscriminatorValue("V")
public abstract class Vehicle implements Serializable {
    private Number id;
    private Company owner;
    private Integer passengerCapacity;

    public Vehicle() {}

    public void change() {
        return;
    }

    public abstract String getColor();

	@Id
    @GeneratedValue(strategy=TABLE, generator="VEHICLE_TABLE_GENERATOR")
	@TableGenerator(
        name="VEHICLE_TABLE_GENERATOR", 
        table="CMP3_INHERITANCE_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="VEHICLE_SEQ")
    @Column(name="ID")
    public Number getId() {
        return id;
    }

	@ManyToOne(cascade=PERSIST, fetch=LAZY)
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
