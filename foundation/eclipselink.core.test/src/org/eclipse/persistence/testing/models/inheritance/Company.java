/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.inheritance;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.indirection.*;

public class Company implements Serializable {
    public Number id;
    public String name;
    public ValueHolderInterface vehicles;
    public ValueHolderInterface workers;

    public Company() {
        vehicles = new ValueHolder();
        workers = new ValueHolder();
    }

    public static Company example1() {
        Company company = new Company();
        Vector vehicle = new Vector();

        vehicle.addElement(Bus.example2(company));
        vehicle.addElement(Bicycle.example1(company));
        vehicle.addElement(Bus.example3(company));
        vehicle.addElement(Bus.example4(company));
        vehicle.addElement(NonFueledVehicle.example4(company));

        company.setName("TOP");
        company.getVehicles().setValue(vehicle);

        Vector worker = new Vector();
        worker.addElement(CompanyWorker.example1(company));
        worker.addElement(CompanyWorker.example4(company));
        worker.addElement(CompanyWorker.example5(company));
        company.getWorkers().setValue(worker);

        return company;
    }

    public static Company example2() {
        Company company = new Company();
        Vector vehicle = new Vector();

        vehicle.addElement(Boat.example1(company));
        vehicle.addElement(Bicycle.example2(company));
        vehicle.addElement(Bus.example3(company));
        vehicle.addElement(FueledVehicle.example1(company));
        vehicle.addElement(NonFueledVehicle.example4(company));

        company.setName("ABC");
        company.getVehicles().setValue(vehicle);

        Vector worker = new Vector();
        worker.addElement(CompanyWorker.example2(company));
        company.getWorkers().setValue(worker);

        return company;
    }

    public static Company example3() {
        Company company = new Company();
        Vector vehicle = new Vector();

        vehicle.addElement(Boat.example1(company));
        vehicle.addElement(Bicycle.example3(company));
        vehicle.addElement(Boat.example2(company));
        vehicle.addElement(Boat.example3(company));
        vehicle.addElement(NonFueledVehicle.example4(company));

        company.setName("XYZ");
        company.getVehicles().setValue(vehicle);

        Vector worker = new Vector();
        worker.addElement(CompanyWorker.example3(company));
        company.getWorkers().setValue(worker);

        return company;
    }

    public ValueHolderInterface getWorkers() {
        return workers;
    }

    public ValueHolderInterface getVehicles() {
        return vehicles;
    }

    public void setName(String aName) {
        name = aName;
    }
}
