/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     12/17/2019 - Will Dazey
//       - 558414 : Add Oracle support for named parameters with stored procedures
package org.eclipse.persistence.jpa.test.storedproc;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.storedproc.model.StoredProcedureEntity;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestStoredProceduresCursors {

    @Emf(name = "cursorStoredProcedureEmf", createTables = DDLGen.DROP_CREATE, classes = { StoredProcedureEntity.class },
            //This property ('enableNamedParameterMarkers') is needed for DB2Z named parameters
            properties = { @Property(name = "eclipselink.jdbc.property.enableNamedParameterMarkers", value = "true") } )
    private EntityManagerFactory cursorStoredProcedureEmf;

    @Before
    public void setup() {
        //Attempt to setup the stored procedure for testing.
        Assume.assumeTrue("Platform " + getPlatform(cursorStoredProcedureEmf) + " is not supported for this test", 
                createCursorStoredProcedure(cursorStoredProcedureEmf));

        //Populate the table for the stored procedure
        EntityManager em = cursorStoredProcedureEmf.createEntityManager();
        try {
            StoredProcedureEntity[] data = new StoredProcedureEntity[] {
                    new StoredProcedureEntity("KeyOne", "StrOne", 32),
                    new StoredProcedureEntity("KeyTwo", "StrTwo", 64),
                    new StoredProcedureEntity("KeyThree", "StrThree", 128)
            };
            em.getTransaction().begin();
            for(StoredProcedureEntity d : data) {
                if(em.find(StoredProcedureEntity.class, d.getKeyString()) == null) {
                    em.persist(d);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Tests stored procedure using indexed parameters
     */
    @Test
    public void testCursorStoredProcedure_IndexParameters() {
        EntityManager em = cursorStoredProcedureEmf.createEntityManager();
        try {
            StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("simple_cursor_procedure");
            storedProcedure.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR);
            storedProcedure.setParameter(1, 64);
            storedProcedure.execute();

            List<Object[]> returnValue = (List<Object[]>) storedProcedure.getOutputParameterValue(2);

            Assert.assertEquals(1, returnValue.size());
            Object[] ret = returnValue.get(0);
            Assert.assertEquals(1, ret.length);
            Assert.assertEquals(ret[0], "StrTwo");
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Tests stored procedure using named parameters
     */
    @Test
    public void testCursorStoredProcedure_NamedParameters() {
        EntityManager em = cursorStoredProcedureEmf.createEntityManager();
        try {
            StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("simple_cursor_procedure");
            storedProcedure.registerStoredProcedureParameter("in_param_one", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("out_cursor_one", void.class, ParameterMode.REF_CURSOR);
            storedProcedure.setParameter("in_param_one", 128);
            storedProcedure.execute();

            List<Object[]> returnValue = (List<Object[]>) storedProcedure.getOutputParameterValue("out_cursor_one");

            Assert.assertEquals(1, returnValue.size());
            Object[] ret = returnValue.get(0);
            Assert.assertEquals(1, ret.length);
            Assert.assertEquals(ret[0], "StrThree");
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    private static boolean createCursorStoredProcedure(EntityManagerFactory emf) {
        //Setup a stored procedure
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureDefinition proc = new StoredProcedureDefinition();
            proc.setName("simple_cursor_procedure");

            proc.addArgument("in_param_one", Integer.class, 10);

            DatabaseSession dbs = ((EntityManagerImpl)em).getDatabaseSession();
            SchemaManager manager = new SchemaManager(dbs);
            Platform platform = dbs.getDatasourcePlatform();

            //Add more platform specific diction to support more platforms
            if(platform.isOracle()) {
                proc.addOutputArgument("out_cursor_one", "SYS_REFCURSOR");
                proc.addStatement("OPEN out_cursor_one FOR SELECT ITEM_STRING1 FROM STORED_PROCEDURE_ENTITY WHERE ITEM_INTEGER1 = in_param_one");
            } else if (platform.isDB2()) {
                proc.addOutputArgument("out_cursor_one", "CURSOR");
                proc.addStatement("SET out_cursor_one = CURSOR FOR SELECT ITEM_STRING1 FROM STORED_PROCEDURE_ENTITY WHERE ITEM_INTEGER1 = in_param_one; OPEN out_cursor_one");
            } else {
                return false;
            }

            try {
                manager.dropObject(proc);
            } catch(Exception e) {
                //Ignore any drop exceptions since the procedure may not exist yet
            }

            manager.createObject(proc);
            return true;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    private static DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}