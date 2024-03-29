/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.tests.wdf.jpa1.relation;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Cache;
import jakarta.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Bicycle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CostCenter;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CubiclePrimaryKeyClass;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.MotorVehicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.TravelProfile;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Vehicle;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

/**
 * Testing relations in combination with object cache. Test cases generated by all-pairs testing tool: <ul><li>ToManyFK, eager,
 * owning side right, cacheable both: testCostCenterEmployee</li> <li>ToOneFK, lazy, owning side left, cacheable right:
 * testMotorVehicleEmployee</li> <li>ToOneFK, eager, owning side right, cacheable both: testCubicleEmployee</li> <li>
 * ToManyJoinTable, eager, owning side left, cacheable right: testBicycleEmployee</li> <li>ToManyJoinTable, lazy, owning side
 * left, cacheable both: testEmployeeProject</li> <li>ToManyFK, lazy, owning side right, cacheable right: n.a.</li> <li>
 * ToManyJoinTable, lazy, owning side right, cacheable right: testTravelProfileVehicle</li>
 *
 *
 *
 * <li>ToManyJoinTable, lazy, owning side right, cacheable both: testProjectEmployee</li> <li>ToManyJoinTable, lazy, owning side
 * left, cacheable left: testEmployeePatent</li> <li>ToOneFK, eager, owning side left, cacheable left: testEmployeeTravelProfile
 * </li> <li>ToManyFK, eager, owning side right, cacheable right</li> </ul>
 */
public class TestRelationshipsWithCache extends JPA1Base {

    /**
     * Test case: CostCenter.employees
     * <p>
     * Relationship mapping: ToManyFK, FetchType: eager, owning side: right, cacheable: both
     */
    @Test
    @Bugzilla(bugid=309681)
    public void testCostCenterEmployee() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final Cache cache = em.getEntityManagerFactory().getCache();

        int costCenterId = 1;
        CostCenter costCenter = new CostCenter(1, "lotsOfMoney");
        int employeeId1 = 1;
        int employeeId2 = 2;
        Employee employee1 = new Employee(employeeId1, "very", "expensive", null);
        Employee employee2 = new Employee(employeeId2, "even", "moreExpensive", null);
        costCenter.addEmployee(employee1);
        costCenter.addEmployee(employee2);
        employee1.setCostCenter(costCenter);
        employee2.setCostCenter(costCenter);
        try {
            env.beginTransaction(em);
            em.persist(costCenter);
            em.persist(employee1);
            em.persist(employee2);
            env.commitTransactionAndClear(em);

            em.find(CostCenter.class, costCenterId); // eager loading of Employees
            assertTrue(cache.contains(CostCenter.class, costCenterId));
            assertTrue(cache.contains(Employee.class, employeeId1));
            assertTrue(cache.contains(Employee.class, employeeId2));

            env.beginTransaction(em);
            costCenter = em.find(CostCenter.class, costCenterId);
            employee1 = em.find(Employee.class, employeeId1);
            employee2 = em.find(Employee.class, employeeId2);
            List<Employee> foundEmployees = costCenter.getEmployees();
            assertTrue(foundEmployees != null);
            assertTrue("wrong number of riders: " + foundEmployees.size(), foundEmployees.size() == 2);
            if (foundEmployees.get(0).getId() == employeeId1) {
                assertTrue("wrong rider: " + foundEmployees.get(1).getId(), foundEmployees.get(1).getId() == employeeId2);
            } else {
                assertTrue("wrong rider: " + foundEmployees.get(0).getId(), foundEmployees.get(0).getId() == employeeId2);
                assertTrue("wrong rider: " + foundEmployees.get(1).getId(), foundEmployees.get(1).getId() == employeeId1);
            }
            CostCenter costCenter1 = employee1.getCostCenter();
            CostCenter costCenter2 = employee2.getCostCenter();
            assertTrue(costCenter1 != null);
            assertTrue(costCenter1.getId() == costCenterId);
            assertTrue(costCenter2 != null);
            assertTrue(costCenter2.getId() == costCenterId);
            costCenter.setEmployees(null);
            employee1.setCostCenter(null);
            employee2.setCostCenter(null);
            env.commitTransactionAndClear(em);

            assertTrue(!cache.contains(CostCenter.class, costCenterId));
            assertTrue(!cache.contains(Employee.class, employeeId1));
            assertTrue(!cache.contains(Employee.class, employeeId2));
            costCenter = em.find(CostCenter.class, costCenterId);
            employee1 = em.find(Employee.class, employeeId1);
            employee2 = em.find(Employee.class, employeeId2);
            assertTrue(costCenter.getEmployees().size() == 0);
            assertTrue(employee1.getCostCenter() == null);
            assertTrue(employee2.getCostCenter() == null);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test case: MotorVehicle.driver
     * <p>
     * Relationship mapping: ToOneFK, FetchType: lazy, owning side: left, cacheable: right
     */
    @Test
    @Bugzilla(bugid=309681)
    public void testMotorVehicleEmployee() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final Cache cache = em.getEntityManagerFactory().getCache();
        final boolean isLazyToOneEnabled = false;

        MotorVehicle motorVehicle = new MotorVehicle();
        int employeeId = 11;
        Employee employee = new Employee(employeeId, "first", "driver", null);
        motorVehicle.setDriver(employee);
        employee.setMotorVehicle(motorVehicle);
        try {
            env.beginTransaction(em);
            em.persist(motorVehicle);
            em.persist(employee);
            env.commitTransactionAndClear(em);
            Short motorVehicleId = motorVehicle.getId();

            em.find(MotorVehicle.class, motorVehicleId);
            if (isLazyToOneEnabled) {
                assertTrue(!cache.contains(Employee.class, employeeId));
                employee = em.find(Employee.class, employeeId);
                // assertTrue(employee instanceof LazilyLoadable); FIXME
                // assertTrue( ((LazilyLoadable) employee)._isPending() ); FIXME
            } else {
                assertTrue(cache.contains(Employee.class, employeeId));
            }

            env.beginTransaction(em);
            motorVehicle = em.find(MotorVehicle.class, motorVehicleId);
            employee = em.find(Employee.class, employeeId);
            assertTrue(employeeId == motorVehicle.getDriver().getId());
            assertTrue(motorVehicleId.equals(employee.getMotorVehicle().getId()));
            assertTrue(employee == motorVehicle.getDriver());
            motorVehicle.setDriver(null);
            employee.setMotorVehicle(null);
            env.commitTransactionAndClear(em);

            assertTrue(!cache.contains(Employee.class, employeeId));
            motorVehicle = em.find(MotorVehicle.class, motorVehicleId);
            employee = em.find(Employee.class, employeeId);
            assertTrue(motorVehicle.getDriver() == null);
            assertTrue(employee.getMotorVehicle() == null);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test case: Employee.cubicle
     * <p>
     * Relationship mapping: ToOneFK, FetchType: eager, owning side: right, cacheable: both
     */
    @Test
    @Bugzilla(bugid=309681)
    public void testCubicleEmployee() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final Cache cache = em.getEntityManagerFactory().getCache();

        int employeeId = 21;
        Employee employee = new Employee(employeeId, "first", "last", null);
        CubiclePrimaryKeyClass cubicleId = new CubiclePrimaryKeyClass(1, 2);
        Cubicle cubicle = new Cubicle(cubicleId, "red", employee);
        employee.setCubicle(cubicle);
        try {
            env.beginTransaction(em);
            em.persist(employee);
            em.persist(cubicle);
            env.commitTransactionAndClear(em);

            em.find(Cubicle.class, cubicleId);
            assertTrue(cache.contains(Cubicle.class, cubicleId));
            assertTrue(cache.contains(Employee.class, employeeId));

            env.beginTransaction(em);
            cubicle = em.find(Cubicle.class, cubicleId);
            employee = em.find(Employee.class, employeeId);
            assertTrue("cubicle has wrong employee: " + cubicle.getEmployee().getId(), employeeId == cubicle.getEmployee()
                    .getId());
            assertTrue("employee has wrong cubicle: " + employee.getCubicle().getId(), cubicleId.equals(employee.getCubicle()
                    .getId()));
            cubicle.setEmployee(null);
            employee.setCubicle(null);
            env.commitTransactionAndClear(em);

            assertTrue(!cache.contains(Cubicle.class, cubicleId));
            assertTrue(!cache.contains(Employee.class, employeeId));
            cubicle = em.find(Cubicle.class, cubicleId);
            employee = em.find(Employee.class, employeeId);
            assertTrue(cubicle.getEmployee() == null);
            assertTrue(employee.getCubicle() == null);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test case: Bicycle.employees
     * <p>
     * Relationship mapping: ToManyJoinTable, FetchType: eager, owning side: left, cacheable: right
     */
    @Test
    @Bugzilla(bugid=309681)
    public void testBicycleEmployee() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final Cache cache = em.getEntityManagerFactory().getCache();

        Bicycle bicycle = new Bicycle();
        int employeeId1 = 31;
        int employeeId2 = 32;
        Employee employee1 = new Employee(employeeId1, "good", "rider1", null);
        Employee employee2 = new Employee(employeeId2, "bad", "rider2", null);
        employee1.addBicycle(bicycle); // maintains also bicycle.riders relationship
        employee2.addBicycle(bicycle);
        try {
            env.beginTransaction(em);
            em.persist(bicycle);
            em.persist(employee1);
            em.persist(employee2);
            env.commitTransactionAndClear(em);
            Short bicycleId = bicycle.getId();

            em.find(Bicycle.class, bicycleId); // eager loading of Employees
            assertTrue(cache.contains(Employee.class, employeeId1));
            assertTrue(cache.contains(Employee.class, employeeId2));

            env.beginTransaction(em);
            bicycle = em.find(Bicycle.class, bicycleId);
            employee1 = em.find(Employee.class, employeeId1);
            employee2 = em.find(Employee.class, employeeId2);
            Collection<Employee> foundRiders = bicycle.getRiders();
            assertTrue(foundRiders != null);
            assertTrue("wrong number of riders: " + foundRiders.size(), foundRiders.size() == 2);
            Employee[] ridersArray = foundRiders.toArray(new Employee[2]);
            if (ridersArray[0].getId() == employeeId1) {
                assertTrue("wrong rider: " + ridersArray[1].getId(), ridersArray[1].getId() == employeeId2);
            } else {
                assertTrue("wrong rider: " + ridersArray[0].getId(), ridersArray[0].getId() == employeeId2);
                assertTrue("wrong rider: " + ridersArray[1].getId(), ridersArray[1].getId() == employeeId1);
            }
            Set<Bicycle> bicycles1 = employee1.getBicycles();
            Set<Bicycle> bicycles2 = employee2.getBicycles();
            assertTrue(bicycles1 != null);
            assertTrue(bicycles2 != null);
            assertTrue(bicycles1.size() == 1);
            assertTrue(bicycles2.size() == 1);
            assertTrue(bicycleId.equals(bicycles1.toArray(new Bicycle[1])[0].getId()));
            assertTrue(bicycleId.equals(bicycles2.toArray(new Bicycle[1])[0].getId()));
            bicycle.setRiders(null);
            employee1.setBicycles(null);
            employee2.setBicycles(null);
            env.commitTransactionAndClear(em);

            assertTrue(!cache.contains(Employee.class, employeeId1));
            assertTrue(!cache.contains(Employee.class, employeeId2));
            bicycle = em.find(Bicycle.class, bicycleId);
            employee1 = em.find(Employee.class, employeeId1);
            employee2 = em.find(Employee.class, employeeId2);
            assertTrue(bicycle.getRiders().size() == 0);
            assertTrue(employee1.getBicycles().size() == 0);
            assertTrue(employee2.getBicycles().size() == 0);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test case: Employee.projects
     * <p>
     * Relationship mapping: ToManyJoinTable, FetchType: lazy, owning side: left, cacheable: both
     */
    @SuppressWarnings("unchecked")
    @Test
    @Bugzilla(bugid=309681)
    public void testEmployeeProject() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final Cache cache = em.getEntityManagerFactory().getCache();

        int employeeId = 41;
        Employee employee = new Employee(employeeId, "aaa", "member", null);
        Project project1 = new Project("evaluation");
        Project project2 = new Project("demotivation");
        project1.addEmployee(employee); // maintains also employee.projects relationship
        project2.addEmployee(employee);
        try {
            env.beginTransaction(em);
            em.persist(employee);
            em.persist(project1);
            em.persist(project2);
            env.commitTransactionAndClear(em);
            Integer projectId1 = project1.getId();
            Integer projectId2 = project2.getId();

            env.beginTransaction(em);
            employee = em.find(Employee.class, employeeId);
            assertTrue(cache.contains(Employee.class, employeeId));
            assertTrue(!cache.contains(Project.class, projectId1)); // lazy loading of projects
            assertTrue(!cache.contains(Project.class, projectId2));
            employee.getProjects().size();
            assertTrue(cache.contains(Project.class, projectId1));
            assertTrue(cache.contains(Project.class, projectId2));
            project1 = em.find(Project.class, projectId1);
            project2 = em.find(Project.class, projectId2);
            Set<Project> foundProjects = employee.getProjects();
            assertTrue(foundProjects != null);
            assertTrue("wrong number of projects: " + foundProjects.size(), foundProjects.size() == 2);
            Project[] projArray = foundProjects.toArray(new Project[2]);
            if (projArray[0].getId().equals(projectId1)) {
                assertTrue("wrong project: " + projArray[1].getId(), projArray[1].getId().equals(projectId2));
            } else {
                assertTrue("wrong project: " + projArray[0].getId(), projArray[0].getId().equals(projectId2));
                assertTrue("wrong project: " + projArray[1].getId(), projArray[1].getId().equals(projectId1));
            }
            Set<Employee> employees1 = project1.getEmployees();
            Set<Employee> employees2 = project2.getEmployees();
            assertTrue(employees1 != null);
            assertTrue(employees2 != null);
            assertTrue(employees1.size() == 1);
            assertTrue(employees2.size() == 1);
            assertTrue(employees1.toArray(new Employee[1])[0].getId() == employeeId);
            assertTrue(employees2.toArray(new Employee[1])[0].getId() == employeeId);
            employee.setProjects(null);
            project1.setEmployees(null);
            project2.setEmployees(null);
            env.commitTransactionAndClear(em);

            assertTrue(!cache.contains(Employee.class, employeeId));
            assertTrue(!cache.contains(Project.class, projectId1));
            assertTrue(!cache.contains(Project.class, projectId2));
            employee = em.find(Employee.class, employeeId);
            project1 = em.find(Project.class, projectId1);
            project2 = em.find(Project.class, projectId2);
            assertTrue(employee.getProjects().size() == 0);
            assertTrue(project1.getEmployees().size() == 0);
            assertTrue(project2.getEmployees().size() == 0);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test case: TravelProfile.vehicles
     * <p>
     * Relationship mapping: ToManyJoinTable, FetchType: lazy, owning side: right, cacheable: right
     */
    @SuppressWarnings("unchecked")
    @Test
    @ToBeInvestigated
    public void testTravelProfileVehicle() { // TODO remove
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final Cache cache = em.getEntityManagerFactory().getCache();

        byte[] travelProfileId = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        TravelProfile travelProfile = new TravelProfile(travelProfileId, true, "neverComeBack");
        Vehicle vehicle1 = new Vehicle();
        Vehicle vehicle2 = new Vehicle();
        vehicle1.addProfile(travelProfile); // maintains also travelProfile.vehicles relationship
        vehicle2.addProfile(travelProfile);
        try {
            env.beginTransaction(em);
            em.persist(travelProfile);
            em.persist(vehicle1);
            em.persist(vehicle2);
            env.commitTransactionAndClear(em);
            Short vehicleId1 = vehicle1.getId();
            Short vehicleId2 = vehicle2.getId();

            env.beginTransaction(em);
            travelProfile = em.find(TravelProfile.class, travelProfileId);
            assertTrue(!cache.contains(Vehicle.class, vehicleId1)); // lazy loading of vehicles
            assertTrue(!cache.contains(Vehicle.class, vehicleId2));
            travelProfile.getVehicles().size();
            assertTrue(cache.contains(Vehicle.class, vehicleId1));
            assertTrue(cache.contains(Vehicle.class, vehicleId2));
            vehicle1 = em.find(Vehicle.class, vehicleId1);
            vehicle2 = em.find(Vehicle.class, vehicleId2);
            Set<Vehicle> foundVehicles = travelProfile.getVehicles();
            assertTrue(foundVehicles != null);
            assertTrue("wrong number of vehicles: " + foundVehicles.size(), foundVehicles.size() == 2);
            Vehicle[] vehicleArray = foundVehicles.toArray(new Vehicle[2]);
            if (vehicleArray[0].getId().equals(vehicleId1)) {
                assertTrue("wrong vehicle: " + vehicleArray[1].getId(), vehicleArray[1].getId().equals(vehicleId2));
            } else {
                assertTrue("wrong vehicle: " + vehicleArray[0].getId(), vehicleArray[0].getId().equals(vehicleId2));
                assertTrue("wrong vehicle: " + vehicleArray[1].getId(), vehicleArray[1].getId().equals(vehicleId1));
            }
            Set<TravelProfile> travelProfiles1 = vehicle1.getProfiles();
            Set<TravelProfile> travelProfiles2 = vehicle2.getProfiles();
            assertTrue(travelProfiles1 != null);
            assertTrue(travelProfiles2 != null);
            assertTrue(travelProfiles1.size() == 1);
            assertTrue(travelProfiles2.size() == 1);
            assertTrue(Arrays.equals(travelProfiles1.toArray(new TravelProfile[1])[0].getGuid(), travelProfileId));
            assertTrue(Arrays.equals(travelProfiles2.toArray(new TravelProfile[1])[0].getGuid(), travelProfileId));
            travelProfile.setVehicles(null);
            vehicle1.setProfiles(null);
            vehicle2.setProfiles(null);
            env.commitTransactionAndClear(em);

            assertTrue(!cache.contains(Vehicle.class, vehicleId1));
            assertTrue(!cache.contains(Vehicle.class, vehicleId2));
            travelProfile = em.find(TravelProfile.class, travelProfileId);
            vehicle1 = em.find(Vehicle.class, vehicleId1);
            vehicle2 = em.find(Vehicle.class, vehicleId2);
            assertTrue(travelProfile.getVehicles().size() == 0);
            assertTrue(vehicle1.getProfiles().size() == 0);
            assertTrue(vehicle2.getProfiles().size() == 0);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    /**
     * just for the case that all other methods are skipped
     */
    public void dummyTestMethod() {
        return;
    }

}
