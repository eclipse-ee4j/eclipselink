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

import javax.persistence.EntityManager;
import org.junit.Test;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CubiclePrimaryKeyClass;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Office;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.UniqueColorOffice;

import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;

public class TestMap extends JPA1Base {

    @Test
    @Bugzilla(bugid=300485)
    public void testEmptyOffice() {
        final Integer officeId = Integer.valueOf(1);
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            final Office office = new Office();
            office.setId(officeId.intValue());
            office.setCapacity(10);
            em.persist(office);
            getEnvironment().commitTransactionAndClear(em);
            getEnvironment().beginTransaction(em);
            final Office storedOffice = em.find(Office.class, officeId);
            verify(storedOffice != null, "office not stored");
            verify(storedOffice.getCapacity() == 10L, "wrong capacity: " + storedOffice.getCapacity());
            verify(storedOffice.isCapacityValid(), "invalid capacity");
            getEnvironment().commitTransactionAndClear(em);
            for (final Employee employee : storedOffice.getOccupants()) {
                verify(false, "Strangers in cubicles: " + employee.getLastName());
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=300485)
    public void testOffice() {
        final Integer officeId = Integer.valueOf(2);
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            final Office office = new Office();
            office.setId(officeId.intValue());
            office.setCapacity(10);
            em.persist(office);
            final Department department = new Department(1, "R&D");
            em.persist(department);
            for (int floor = 0; floor < 3; floor++) {
                for (int place = 10; place < 13; place++) {
                    final Employee employee = new Employee(floor * 3 + place, "first", "last", department);
                    em.persist(employee);
                    final Cubicle cubicle = new Cubicle(Integer.valueOf(floor), Integer.valueOf(place), "green", employee);
                    employee.setCubicle(cubicle);
                    em.persist(cubicle);
                    office.addCubicle(cubicle);
                }
            }
            getEnvironment().commitTransactionAndClear(em);
            getEnvironment().beginTransaction(em);
            final Office storedOffice = em.find(Office.class, officeId);
            verify(storedOffice.isCapacityValid(), "invalid capacity");
            verify(storedOffice.getCubicles().size() == 9, "Wrong number of cubicles: " + storedOffice.getCubicles().size()
                    + " (expected: " + office.getCubicles().size() + ").");
            for (final Employee employee : storedOffice.getOccupants()) {
                verify(10 <= employee.getId() && employee.getId() < 20, "Strangers in cubicles");
            }
            for (int place = 10; place < 13; place++) {
                final CubiclePrimaryKeyClass testKey = new CubiclePrimaryKeyClass(Integer.valueOf(2), Integer.valueOf(place));
                final Cubicle cubicle = storedOffice.getCubicles().get(testKey);
                final Employee employee = cubicle.getEmployee();
                verify(employee.getId() == place + 6, "Wrong occupant of cubicle with id (floor: " + cubicle.getFloor()
                        + ", place: " + cubicle.getPlace() + ").");
            }
            getEnvironment().commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testColorOffice() {
        final String[] colors = new String[] { "red", "green", "blue" };
        final Integer officeId = Integer.valueOf(3);
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            final UniqueColorOffice office = new UniqueColorOffice();
            office.setId(officeId.intValue());
            office.setCapacity(5);
            em.persist(office);
            final Department department = new Department(2, "HR");
            em.persist(department);
            for (int i = 0; i < colors.length; i++) {
                final int floor = 10 + i;
                final int place = i;
                final int emplyoeeId = 100 + i;
                final Employee employee = new Employee(emplyoeeId, "first", "last", department);
                em.persist(employee);
                final Cubicle cubicle = new Cubicle(Integer.valueOf(floor), Integer.valueOf(place), colors[i], employee);
                employee.setCubicle(cubicle);
                em.persist(cubicle);
                office.addCubicle(cubicle);
            }
            getEnvironment().commitTransactionAndClear(em);
            getEnvironment().beginTransaction(em);
            final UniqueColorOffice storedOffice = em.find(UniqueColorOffice.class, officeId);
            verify(storedOffice.getCubicles().size() == colors.length, "Wrong number of cubicles: "
                    + storedOffice.getCubicles().size() + " (expected: " + office.getCubicles().size() + ").");
            for (final Employee employee : storedOffice.getOccupants()) {
                verify(100 <= employee.getId() && employee.getId() < 100 + colors.length, "Strangers in cubicles");
            }
            for (final String color : colors) {
                final Cubicle cubicle = storedOffice.getCubicles().get(color);
                verify(cubicle != null, getEnvironment() + ": no cubicle with color " + color + " found.");
                final Employee employee = cubicle.getEmployee();
                verify(color.equals(colors[employee.getId() - 100]), "Wrong occupant of cubicle with color " + color + ".");
            }
            storedOffice.getCubicles().remove(colors[0]);
            getEnvironment().commitTransactionAndClear(em);
            getEnvironment().beginTransaction(em);
            final UniqueColorOffice officeWithoutRedCubicle = em.find(UniqueColorOffice.class, officeId);
            verify(!officeWithoutRedCubicle.getCubicles().containsKey(colors[0]), "didnt remove " + colors[0] + " cubicle");
            for (int i = 1; i < colors.length; i++) {
                final String color = colors[i];
                final Cubicle cubicle = officeWithoutRedCubicle.getCubicles().get(color);
                verify(cubicle != null, getEnvironment() + ": no cubicle with color " + color + " found.");
                final Employee employee = cubicle.getEmployee();
                verify(color.equals(colors[employee.getId() - 100]), "Wrong occupant of cubicle with color " + color + ".");
            }
            getEnvironment().commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }
}
