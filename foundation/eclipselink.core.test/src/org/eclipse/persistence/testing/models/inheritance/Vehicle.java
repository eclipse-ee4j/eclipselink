/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.inheritance;

import java.io.*;
import java.util.Vector;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class Vehicle implements Serializable {
    public Number id;
    public ValueHolderInterface owner;
    public Integer passengerCapacity;
    public Vector partNumbers;

    public Vehicle() {
        owner = new ValueHolder();
        partNumbers = new Vector();
    }

    public void addPartNumber(String aPartNumber) {
        partNumbers.addElement(aPartNumber);
    }

    public void change() {
        return;
    }

    public ValueHolderInterface getOwner() {
        return owner;
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

    public void setOwner(Company ownerCompany) {
        owner.setValue(ownerCompany);
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

    /**
     * Return the view for MySQL.  It is supported as of MySQL 5.0.1
     */
    public static ViewDefinition mySQLView() {
        ViewDefinition definition = new ViewDefinition();

        definition.setName("AllVehicles");
        definition.setSelectClause("Select V.*, F.FUEL_CAP, F.FUEL_TYP, B.DESCRIP, B.DRIVER_ID, C.CDESCRIP" + " from VEHICLE V left join FUEL_VEH F on V.ID = F.ID" + " left join BUS B on V.ID = B.ID left join CAR C on V.ID = C.ID");

        return definition;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + "(" + id + ")";
    }
}
