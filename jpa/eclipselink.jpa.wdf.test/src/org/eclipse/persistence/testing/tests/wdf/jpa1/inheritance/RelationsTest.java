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

package org.eclipse.persistence.testing.tests.wdf.jpa1.inheritance;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Bicycle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Car;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.MotorVehicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.MountainBike;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.RoadBike;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Truck;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class RelationsTest extends JPA1Base {

    @Test
    public void testEmployeeWithTruck() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Employee employee = new Employee(1234, "Mighty", "Mouse", null);
            Truck truck = new Truck();
            truck.setBrand("Renault");
            truck.setColor("yellow");
            truck.setLicensePlateNumber("MA-US 1234");
            truck.setModel("Trafic");
            truck.setMaxLoad((short) 800);
            truck.setDriver(employee);
            employee.setMotorVehicle(truck);
            env.beginTransaction(em);
            em.persist(employee);
            final Short id = truck.getId();
            env.commitTransaction(em);
            employee = null;
            em.clear();
            employee = em.find(Employee.class, Integer.valueOf(1234));
            verify(employee != null, "employee not found");
            MotorVehicle motorVehicle = employee.getMotorVehicle();
            verify(motorVehicle != null, "no motor vehicle found");
            verify(motorVehicle.getClass() == Truck.class, "wrong class: " + motorVehicle.getClass());
            verify("yellow".equals(motorVehicle.getColor()), "wrong color: " + motorVehicle.getColor());
            verify(id.equals(motorVehicle.getId()), "wrong motor vehicle id: " + motorVehicle.getId());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testEmployeeWithBicycles() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Employee employee = new Employee(17, "Kuno", "Hurtig", null);
            Set<Employee> riders = new HashSet<Employee>();
            riders.add(employee);
            Set<Bicycle> bicycles = new HashSet<Bicycle>();
            Bicycle roadBike = new RoadBike();
            roadBike.setRiders(riders);
            bicycles.add(roadBike);
            MountainBike mountainBike = new MountainBike();
            mountainBike.setRiders(riders);
            bicycles.add(mountainBike);
            employee.setBicycles(bicycles);
            env.beginTransaction(em);
            em.persist(employee);
            final Short roadBikeId = roadBike.getId();
            env.commitTransaction(em);
            employee = null;
            em.clear();
            employee = em.find(Employee.class, Integer.valueOf(17));
            verify(employee != null, "employee not found");
            bicycles = employee.getBicycles();
            verify(bicycles != null, "bicycles is null");
            verify(bicycles.size() == 2, "wrong size");
            Iterator<Bicycle> iter = bicycles.iterator();
            Bicycle bike1 = iter.next();
            Bicycle bike2 = iter.next();
            verify(bike1.getClass() == RoadBike.class && bike2.getClass() == MountainBike.class
                    || bike1.getClass() == MountainBike.class && bike2.getClass() == RoadBike.class,
                    "bicyles have unexpected types: " + bike1.getClass().getName() + ", " + bike2.getClass().getName());
            em.clear();
            env.beginTransaction(em);
            em.find(Bicycle.class, roadBikeId);
            env.commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testEnBlocLoading() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Employee e1 = new Employee(1, "Paul", "Weber", null);
            Car car = new Car();
            car.setDriver(e1);
            e1.setMotorVehicle(car);

            Employee e2 = new Employee(2, "Paul", "Breitner", null);
            Truck truck = new Truck();
            truck.setDriver(e2);
            e2.setMotorVehicle(truck);

            Employee e3 = new Employee(3, "Paulchen", "Panther", null);
            MotorVehicle mv = new MotorVehicle();
            mv.setDriver(e3);
            e3.setMotorVehicle(mv);

            env.beginTransaction(em);
            em.persist(e1);
            em.persist(e2);
            em.persist(e3);
            em.persist(car);
            em.persist(truck);
            em.persist(mv);

            env.commitTransactionAndClear(em);

            env.beginTransaction(em);

            Query query = em.createQuery("select mv from MotorVehicle mv");
            query.getResultList();

        } finally {
            closeEntityManager(em);
        }

    }
}
