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

import java.sql.SQLException;
import java.util.Arrays;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.TravelProfile;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Before;
import org.junit.Test;

public class TestUnidirectionalOneToOne extends JPA1Base {
    private static final int HANS_ID_VALUE = 1;
    private static final int FRED_ID_VALUE = 2;
    private static final Integer HANS_ID = new Integer(HANS_ID_VALUE);
    private static final Integer FRED_ID = new Integer(FRED_ID_VALUE);
    private static final byte[] SMOKER_GUID = new byte[16];
    private static final byte[] NON_SMOKER_GUID = new byte[16];
    static {
        for (int i = 0; i < 16; i++) {
            SMOKER_GUID[i] = (byte) i;
            NON_SMOKER_GUID[i] = (byte) (16 - i);
        }
    }

    @Before
    public void seedDataModel() throws SQLException {
        clearAllTables(); // clear all tables;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = new Department(17, "diverses");
            Employee hans = new Employee(HANS_ID_VALUE, "Hans", "Wurst", dep);
            Employee fred = new Employee(FRED_ID_VALUE, "Fred", "von Jupiter", dep);
            TravelProfile smoker = new TravelProfile(SMOKER_GUID, true, "Aeroflot");
            hans.setTravelProfile(smoker);
            TravelProfile nonSmoker = new TravelProfile(NON_SMOKER_GUID, false, "Lufthansa");
            em.persist(dep);
            em.persist(hans);
            em.persist(fred);
            em.persist(smoker);
            em.persist(nonSmoker);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testUnchanged() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // 1. do nothing
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, HANS_ID);
            // do nothing
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
            // 2. touch the cubicle
            env.beginTransaction(em);
            emp = em.find(Employee.class, HANS_ID);
            emp.getTravelProfile().getSmoker();
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
            // 3. trivial update
            env.beginTransaction(em);
            emp = em.find(Employee.class, HANS_ID);
            TravelProfile travelProfile = emp.getTravelProfile();
            emp.setTravelProfile(travelProfile);
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMoveTravelProfile() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee hans = em.find(Employee.class, HANS_ID);
            Employee fred = em.find(Employee.class, FRED_ID);
            verify(hans != null, "employee not found");
            verify(fred != null, "employee not found");
            TravelProfile fredsProfile = fred.getTravelProfile();
            TravelProfile hansProfile = hans.getTravelProfile();
            fred.setTravelProfile(hansProfile);
            hans.setTravelProfile(null);
            verify(fredsProfile == null, "Freds travel profile is not null");
            env.commitTransactionAndClear(em);
            hans = em.find(Employee.class, HANS_ID);
            verify(hans != null, "Hans is null");
            verify(hans.getTravelProfile() == null, "Hans' travel profile is not null");
            fred = em.find(Employee.class, FRED_ID);
            verify(fred != null, "Fred is null");
            fredsProfile = fred.getTravelProfile();
            verify(fredsProfile != null, "Freds travel profile is null");
            verify(Arrays.equals(fredsProfile.getGuid(), SMOKER_GUID), "Freds travel profile has wrong guid");
            TravelProfile profile = em.find(TravelProfile.class, NON_SMOKER_GUID);
            verify(profile != null, "blue cubicle not found");
        } finally {
            closeEntityManager(em);
        }
    }
}
