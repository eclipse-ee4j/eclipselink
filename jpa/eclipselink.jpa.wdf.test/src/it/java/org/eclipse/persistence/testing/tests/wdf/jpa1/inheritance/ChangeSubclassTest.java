/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.tests.wdf.jpa1.inheritance;

import java.sql.SQLException;

import jakarta.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Bicycle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Car;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Vehicle;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class ChangeSubclassTest extends JPA1Base {


    @Test
    public void testFindVehicle() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();

        try {
            env.beginTransaction(em);
            Vehicle vehicle = new Car();

            em.persist(vehicle);
            env.commitTransactionAndClear(em);
            Short id = vehicle.getId();

            env.beginTransaction(em);
            vehicle = em.find(Vehicle.class, id);
            em.remove(vehicle);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            vehicle = new Bicycle();
            vehicle.setId(id);
            em.persist(vehicle);
            env.commitTransactionAndClear(em);

            vehicle = em.find(Vehicle.class, id);
        } finally {
            closeEntityManager(em);
        }
    }
}
