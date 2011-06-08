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

package org.eclipse.persistence.testing.tests.wdf.jpa1.relation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.BrokerageAccount;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Car;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CostCenter;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.MotorVehicle;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestEagerLoading extends JPA1Base {

    // chain of eagerly loaded relations with foreign key mapping
    // CostCenter (1 : n, FK) Employee (1 : 1, FK) MotorVehicle
    private CostCenter[] costCenters;
    private Employee[] employees;
    private MotorVehicle[] vehicles;
    private BrokerageAccount[] accounts;

    public TestEagerLoading() {

        accounts = new BrokerageAccount[] { new BrokerageAccount(11, "BA-11"), new BrokerageAccount(12, "BA-12"),
                new BrokerageAccount(13, "BA-13"), new BrokerageAccount(14, "BA-14"), };

        costCenters = new CostCenter[] { new CostCenter(1, "Keine Kohle"), new CostCenter(2, "M\u00e4chtig Moos") };
        employees = new Employee[] { createEmployee(1, "Donald", "Duck", costCenters[0]),
                createEmployee(2, "Daisy", "Duck", costCenters[0]), createEmployee(3, "Gustav", "Gans", costCenters[0]),
                createEmployee(4, "Dagobert", "Duck", costCenters[1]), createEmployee(5, "Klaas", "Klever", costCenters[1]) };
        vehicles = new MotorVehicle[] { createCar("Fiat", employees[0]),
                createCar("Rolls Royce", employees[3]), createCar("Ferrari", employees[4]) };
        costCenters[0].addEmployee(employees[0]);
        costCenters[0].addEmployee(employees[1]);
        costCenters[0].addEmployee(employees[2]);
        costCenters[1].addEmployee(employees[3]);
        costCenters[1].addEmployee(employees[4]);
        employees[0].setMotorVehicle(vehicles[0]);
        employees[3].setMotorVehicle(vehicles[1]);
        employees[4].setMotorVehicle(vehicles[2]);

        employees[0].setBrokerageAccount(accounts[0]);
        employees[1].setBrokerageAccount(accounts[1]);
        employees[2].setBrokerageAccount(accounts[2]);
        employees[3].setBrokerageAccount(accounts[3]);
    }

    private void seedDataModel() throws SQLException {
        /*
         * 5 Projects:
         */
        clearAllTables(); // clear all tables;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            for (CostCenter costCenter : costCenters) {
                em.persist(costCenter);
            }
            for (Employee employee : employees) {
                em.persist(employee);
            }
            for (MotorVehicle vehicle : vehicles) {
                em.persist(vehicle);
            }
            for (BrokerageAccount account : accounts) {
                em.persist(account);
            }

            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSelectInverseSideFKRelationship() throws SQLException {
        seedDataModel(); // clear all tables;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query = em.createQuery("SELECT c FROM CostCenter c ORDER BY c.id");
            List result = query.getResultList();
            int numberOfCostCenters = result.size();
            verify(numberOfCostCenters == costCenters.length, "wrong number of cost centers: " + numberOfCostCenters);
            for (int i = 0; i < numberOfCostCenters; i++) {
                CostCenter resultCostCenter = (CostCenter) result.get(i);
                checkSingleCostCenter(resultCostCenter, costCenters[i]);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMultipleToOneToSubclass() throws SQLException {
        seedDataModel(); // clear all tables;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query = em.createQuery("SELECT e FROM Employee e");
            List<Employee> result = query.getResultList();
            int numbersOfEmployees = result.size();
            verify(numbersOfEmployees == employees.length, "wrong number of employees: " + numbersOfEmployees);
            for (Employee employee : result) {
                if (employee.getId() != 5) {
                    assertNotNull(employee.getBrokerageAccount());
                } else {
                    assertNull(employee.getBrokerageAccount());
                }

            }
        } finally {
            closeEntityManager(em);
        }
    }

    private void checkSingleCostCenter(CostCenter resultCostCenter, CostCenter expectedCostCenter) {
        verify(resultCostCenter.getId() == expectedCostCenter.getId(), "wrong task id: " + resultCostCenter.getId()
                + " , expected: " + expectedCostCenter.getId());
        Comparator<Employee> comparator = new EmployeeComparator();
        List<Employee> resultEmployees = new ArrayList<Employee>(resultCostCenter.getEmployees());
        Collections.sort(resultEmployees, comparator);
        List<Employee> expectedEmployees = new ArrayList<Employee>(expectedCostCenter.getEmployees());
        Collections.sort(expectedEmployees, comparator);
        for (int i = 0; i < resultEmployees.size(); i++) {
            checkSingleEmployee(resultEmployees.get(i), expectedEmployees.get(i));
        }
    }

    private void checkSingleEmployee(Employee resultEmployee, Employee expectedEmployee) {
        verify(resultEmployee.getId() == expectedEmployee.getId(), "wrong employee: " + resultEmployee.getId() + ", expected: "
                + expectedEmployee.getId());
        MotorVehicle resultVehicle = resultEmployee.getMotorVehicle();
        MotorVehicle expectedVehicle = expectedEmployee.getMotorVehicle();
        if (expectedVehicle != null) {
            verify(expectedVehicle.getId().equals(expectedVehicle.getId()), "wrong vehicle: " + resultVehicle.getId()
                    + ", expected: " + expectedVehicle.getId());
        } else {
            String idAsString = "null";
            if (resultVehicle != null) {
                idAsString = "" + resultVehicle.getId();
            }
            verify(resultVehicle == null, "wrong vehicle: " + idAsString + ", expected: null");
        }
    }

    private static class EmployeeComparator implements Comparator<Employee> {
        public int compare(Employee e1, Employee e2) {
            if (e1.getId() < e2.getId()) {
                return -1;
            } else if (e1.getId() > e2.getId()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static Employee createEmployee(int id, String firstName, String lastName, CostCenter costCenter) {
        Employee emp = new Employee(id, firstName, lastName, null);
        emp.setCostCenter(costCenter);
        return emp;
    }

    private static Car createCar(String brand, Employee driver) {
        Car car = new Car();
        car.setBrand(brand);
        car.setDriver(driver);
        return car;
    }

}
