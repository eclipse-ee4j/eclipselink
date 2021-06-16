/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.inheritance;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.indirection.*;

public class Company implements Serializable {
    public Number id;
    public String name;
    public ValueHolderInterface<Vector<? extends Vehicle>> vehicles;
    public ValueHolderInterface<Vector<CompanyWorker>> workers;

    public Company() {
        vehicles = new ValueHolder<>();
        workers = new ValueHolder<>();
    }

    public static Company example1() {
        Company company = new Company();
        Vector<Vehicle> vehicle = new Vector<>();

        vehicle.addElement(Bus.example2(company));
        vehicle.addElement(Bicycle.example1(company));
        vehicle.addElement(Bus.example3(company));
        vehicle.addElement(Bus.example4(company));
        vehicle.addElement(NonFueledVehicle.example4(company));

        company.setName("TOP");
        company.getVehicles().setValue(vehicle);

        Vector<CompanyWorker> worker = new Vector<>();
        worker.addElement(CompanyWorker.example1(company));
        worker.addElement(CompanyWorker.example4(company));
        worker.addElement(CompanyWorker.example5(company));
        company.getWorkers().setValue(worker);

        return company;
    }

    public static Company example2() {
        Company company = new Company();
        Vector<Vehicle> vehicle = new Vector<>();

        vehicle.addElement(Boat.example1(company));
        vehicle.addElement(Bicycle.example2(company));
        vehicle.addElement(Bus.example3(company));
        vehicle.addElement(FueledVehicle.example1(company));
        vehicle.addElement(NonFueledVehicle.example4(company));

        company.setName("ABC");
        company.getVehicles().setValue(vehicle);

        Vector<CompanyWorker> worker = new Vector<CompanyWorker>();
        worker.addElement(CompanyWorker.example2(company));
        company.getWorkers().setValue(worker);

        return company;
    }

    public static Company example3() {
        Company company = new Company();
        Vector<NonFueledVehicle> vehicle = new Vector<>();

        vehicle.addElement(Boat.example1(company));
        vehicle.addElement(Bicycle.example3(company));
        vehicle.addElement(Boat.example2(company));
        vehicle.addElement(Boat.example3(company));
        vehicle.addElement(NonFueledVehicle.example4(company));

        company.setName("XYZ");
        company.getVehicles().setValue(vehicle);

        Vector<CompanyWorker> worker = new Vector<>();
        worker.addElement(CompanyWorker.example3(company));
        company.getWorkers().setValue(worker);

        return company;
    }

    public ValueHolderInterface<Vector<CompanyWorker>> getWorkers() {
        return workers;
    }

    public ValueHolderInterface<Vector<? extends Vehicle>> getVehicles() {
        return vehicles;
    }

    public void setName(String aName) {
        name = aName;
    }
}
