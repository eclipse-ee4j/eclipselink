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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Bicycle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.DirtyCar;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Vehicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.island.Island;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Ignore;
import org.junit.Test;

public class SimpleInheritanceTest extends JPA1Base {

    private void verifyExistenceOnDatabase(Short vehicleId) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("select ID from TMP_VEHICLE where ID = ?");
            try {
                stmt.setShort(1, vehicleId.shortValue());
                ResultSet rs = stmt.executeQuery();
                try {
                    verify(rs.next(), "no department with id " + vehicleId + " found using JDBC.");
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            conn.close();
        }
    }

    private void verifyDiscrminiatorOnDatabase(Short vehicleId, int discriminator) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("select ID, DTYPE from TMP_VEHICLE where ID = ?");
            try {
                stmt.setShort(1, vehicleId.shortValue());
                ResultSet rs = stmt.executeQuery();
                try {
                    verify(rs.next(), "no department with id " + vehicleId + " found using JDBC.");
                    int actual = rs.getInt(2);
                    verify(discriminator == actual, "wrong discrminiator value: " + actual);
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            conn.close();
        }
    }

    @Test
    public void testInsertVehicle() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Vehicle vehicle = new Vehicle();
            vehicle.setBrand("Deutz");
            vehicle.setColor("green");
            em.persist(vehicle);
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(vehicle.getId());
            verifyDiscrminiatorOnDatabase(vehicle.getId(), -1);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testInsertBicycle() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Bicycle bicycle = new Bicycle();
            bicycle.setBrand("Peugeot");
            bicycle.setColor("red");
            bicycle.setNumberOfGears((short) 12);
            em.persist(bicycle);
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(bicycle.getId());
            verifyDiscrminiatorOnDatabase(bicycle.getId(), 4);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFindVehicle() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Vehicle vehicle = new Vehicle();
            vehicle.setBrand("Deutz");
            vehicle.setColor("green");
            em.persist(vehicle);
            Short deutzId = vehicle.getId();
            Bicycle bicycle = new Bicycle();
            bicycle.setBrand("Peugeot");
            bicycle.setColor("red");
            bicycle.setNumberOfGears((short) 12);
            em.persist(bicycle);
            env.commitTransactionAndClear(em);
            Short peugeotId = bicycle.getId();
            vehicle = em.find(Vehicle.class, deutzId);
            verify(vehicle.getClass() == Vehicle.class, "wrong class: " + vehicle.getClass());
            vehicle = em.find(Vehicle.class, peugeotId);
            verify(vehicle.getClass() == Bicycle.class, "wrong class: " + vehicle.getClass());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testInsertDirtyCar() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            DirtyCar dirtyCar = new DirtyCar();
            dirtyCar.setBrand("VW");
            dirtyCar.setModel("Passat");
            dirtyCar.setColor("yellow");
            dirtyCar.setLicensePlateNumber("DIT-TY 5117");
            em.persist(dirtyCar);
            env.commitTransactionAndClear(em);
            verifyExistenceOnDatabase(dirtyCar.getId());
            verifyDiscrminiatorOnDatabase(dirtyCar.getId(), 71);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSelectAll() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Vehicle vehicle = new Vehicle();
            vehicle.setBrand("Deutz");
            vehicle.setColor("green");
            em.persist(vehicle);
            Short deutzId = vehicle.getId();
            Bicycle bicycle = new Bicycle();
            bicycle.setBrand("Peugeot");
            bicycle.setColor("red");
            bicycle.setNumberOfGears((short) 12);
            em.persist(bicycle);
            env.commitTransactionAndClear(em);
            Short peugeotId = bicycle.getId();
            Query query = em.createQuery("select v from Vehicle v");
            List<?> resultList = query.getResultList();
            verify(resultList.size() == 2, "result list has wrong size");
            for (Object object : resultList) {
                if (object instanceof Vehicle) {
                    Vehicle anyVehicle = (Vehicle) object;
                    if (deutzId.equals(anyVehicle.getId())) {
                        verify(anyVehicle.getClass() == Vehicle.class, "vehicle has unexpected class: "
                                + anyVehicle.getClass().getName());
                    } else if (peugeotId.equals(anyVehicle.getId())) {
                        verify(anyVehicle.getClass() == Bicycle.class, "bicycle has unexpected class: "
                                + anyVehicle.getClass().getName());
                    } else {
                        flop("vehicle has unexpected id: " + anyVehicle.getId());
                    }
                } else {
                    flop("object has unexpected type: " + object.getClass().getName());
                }
            }
            vehicle = em.find(Vehicle.class, deutzId);
            verify(vehicle.getClass() == Vehicle.class, "wrong class: " + vehicle.getClass());
            vehicle = em.find(Vehicle.class, peugeotId);
            verify(vehicle.getClass() == Bicycle.class, "wrong class: " + vehicle.getClass());
        } finally {
            closeEntityManager(em);
        }
    }

    @Ignore
    // TODO fix model
    @Test
    public void testPseudoInheritance() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        Connection con = getEnvironment().getDataSource().getConnection();
        try {
            env.beginTransaction(em);
            Island island = new Island("Neufuenfland");
            em.persist(island);
            env.commitTransactionAndClear(em);
            int id = island.getId();
            Statement stmt = con.createStatement();
            try {
                ResultSet rs = stmt.executeQuery("select DTYPE from TMP_ISLAND where ID = " + id);
                try {
                    rs.next();
                    String discriminator = rs.getString(1);
                    verify(discriminator != null, "no discriminator");
                    if (discriminator != null) {
                        verify(discriminator.equals("Island"), "wrong discriminator " + discriminator);
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            closeEntityManager(em);
            con.close();
        }
    }
}
