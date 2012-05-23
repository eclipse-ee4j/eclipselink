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

import javax.persistence.EntityManager;

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
        final Short id = Short.valueOf((short) 99);
        
        try {
            env.beginTransaction(em);
            Vehicle vehicle = new Car();
            vehicle.setId(id);
            em.persist(vehicle);
            env.commitTransactionAndClear(em);
            
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
