/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import java.io.Serializable;
import java.util.Vector;

public class Company implements Serializable {
    public Number id;
    public String name;
    public ValueHolderInterface<Vector<Vehicle>> vehicles;
    public ValueHolderInterface<Vector<CompanyWorker>> workers;

    public Company() {
        vehicles = new ValueHolder<>();
        workers = new ValueHolder<>();
    }

    public static Company example1() {
        Company company = new Company();
        Vector<Vehicle> vehicle = new Vector<>();

        vehicle.add(Bus.example2(company));
        vehicle.add(Bicycle.example1(company));
        vehicle.add(Bus.example3(company));
        vehicle.add(Bus.example4(company));
        vehicle.add(NonFueledVehicle.example4(company));

        company.setName("TOP");
        company.getVehicles().setValue(vehicle);

        Vector<CompanyWorker> worker = new Vector<>();
        worker.add(CompanyWorker.example1(company));
        worker.add(CompanyWorker.example4(company));
        worker.add(CompanyWorker.example5(company));
        company.getWorkers().setValue(worker);

        return company;
    }

    public static Company example2() {
        Company company = new Company();
        Vector<Vehicle> vehicle = new Vector<>();

        vehicle.add(Boat.example1(company));
        vehicle.add(Bicycle.example2(company));
        vehicle.add(Bus.example3(company));
        vehicle.add(FueledVehicle.example1(company));
        vehicle.add(NonFueledVehicle.example4(company));

        company.setName("ABC");
        company.getVehicles().setValue(vehicle);

        Vector<CompanyWorker> worker = new Vector<>();
        worker.add(CompanyWorker.example2(company));
        company.getWorkers().setValue(worker);

        return company;
    }

    public static Company example3() {
        Company company = new Company();
        Vector<Vehicle> vehicle = new Vector<>();

        vehicle.add(Boat.example1(company));
        vehicle.add(Bicycle.example3(company));
        vehicle.add(Boat.example2(company));
        vehicle.add(Boat.example3(company));
        vehicle.add(NonFueledVehicle.example4(company));

        company.setName("XYZ");
        company.getVehicles().setValue(vehicle);

        Vector<CompanyWorker> worker = new Vector<>();
        worker.add(CompanyWorker.example3(company));
        company.getWorkers().setValue(worker);

        return company;
    }

    public ValueHolderInterface<Vector<CompanyWorker>> getWorkers() {
        return workers;
    }

    public ValueHolderInterface<Vector<Vehicle>> getVehicles() {
        return vehicles;
    }

    public void setName(String aName) {
        name = aName;
    }
}
