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
//     09/06/2019 - Will Dazey
//       - 55054 : Add Oracle support for named parameters with stored procedures
package org.eclipse.persistence.jpa.test.storedproc;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
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
public class TestStoredProcedures {

    @Emf(name = "storedProcedureEmf", createTables = DDLGen.DROP_CREATE,
            //This property ('enableNamedParameterMarkers') is needed for DB2Z named parameters
            properties = { @Property(name = "eclipselink.jdbc.property.enableNamedParameterMarkers", value = "true") } )
    private EntityManagerFactory storedProcedureEmf;

    @Before
    public void setup() {
        //Attempt to setup the stored procedure for testing.
        Assume.assumeTrue("Platform " + getPlatform(storedProcedureEmf) + " is not supported for this test", 
                createSimpleStoredProcedure(storedProcedureEmf));
    }

    /**
     * Tests stored procedure using indexed parameters
     */
    @Test
    public void testStoredProcedure_SetOrdered_IndexParameters() {
        EntityManager em = storedProcedureEmf.createEntityManager();
        try {
            StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("simple_order_procedure");
            storedProcedure.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(4, String.class, ParameterMode.OUT);

            storedProcedure.setParameter(1, "One");
            storedProcedure.setParameter(2, "Two");
            storedProcedure.setParameter(3, "Three");

            storedProcedure.execute();

            String returnValue =  (String) storedProcedure.getOutputParameterValue(4);
            Assert.assertEquals("One: One Two: Two Three: Three", returnValue);
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
     * Tests stored procedure using indexed parameters, but alter the order to make sure the result doesn't change.
     */
    @Test
    public void testStoredProcedure_SetUnordered_IndexParameters() {
        EntityManager em = storedProcedureEmf.createEntityManager();
        try {
            StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("simple_order_procedure");
            storedProcedure.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(4, String.class, ParameterMode.OUT);

            storedProcedure.setParameter(2, "Two");
            storedProcedure.setParameter(1, "One");
            storedProcedure.setParameter(3, "Three");

            storedProcedure.execute();

            String returnValue =  (String) storedProcedure.getOutputParameterValue(4);
            Assert.assertEquals("One: One Two: Two Three: Three", returnValue);
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
    public void testStoredProcedure_SetOrdered_NamedParameters() {
        EntityManager em = storedProcedureEmf.createEntityManager();
        try {
            StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("simple_order_procedure");
            storedProcedure.registerStoredProcedureParameter("in_param_one", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("in_param_two", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("in_param_three", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("out_param_one", String.class, ParameterMode.OUT);

            storedProcedure.setParameter("in_param_one", "One");
            storedProcedure.setParameter("in_param_two", "Two");
            storedProcedure.setParameter("in_param_three", "Three");

            storedProcedure.execute();

            String returnValue =  (String) storedProcedure.getOutputParameterValue("out_param_one");
            Assert.assertEquals("One: One Two: Two Three: Three", returnValue);
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
     * Tests stored procedure using named parameters, but alter the order to make sure the result doesn't change.
     */
    @Test
    public void testStoredProcedure_SetUnordered_NamedParameters() {
        EntityManager em = storedProcedureEmf.createEntityManager();
        try {
            StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("simple_order_procedure");
            storedProcedure.registerStoredProcedureParameter("in_param_one", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("in_param_two", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("in_param_three", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("out_param_one", String.class, ParameterMode.OUT);

            storedProcedure.setParameter("in_param_three", "Three");
            storedProcedure.setParameter("in_param_two", "Two");
            storedProcedure.setParameter("in_param_one", "One");

            storedProcedure.execute();

            String returnValue =  (String) storedProcedure.getOutputParameterValue("out_param_one");
            Assert.assertEquals("One: One Two: Two Three: Three", returnValue);
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
     * Creates a simple stored procedure for the given EntityManagerFactory
     * @return boolean indicating if storedProcedure was created
     */
    private static boolean createSimpleStoredProcedure(EntityManagerFactory emf) {
        //Setup a stored procedure
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureDefinition proc = new StoredProcedureDefinition();
            proc.setName("simple_order_procedure");

            proc.addArgument("in_param_one", String.class, 10);
            proc.addArgument("in_param_two", String.class, 10);
            proc.addArgument("in_param_three", String.class, 10);
            proc.addOutputArgument("out_param_one", String.class, 30);

            DatabaseSession dbs = ((EntityManagerImpl)em).getDatabaseSession();
            SchemaManager manager = new SchemaManager(dbs);
            Platform platform = dbs.getDatasourcePlatform();

            //Add more platform specific diction to support more platforms
            if(platform.isMySQL()) {
                proc.addStatement("SET out_param_one = CONCAT('One: ',in_param_one,' Two: ',in_param_two,' Three: ',in_param_three)");
            } else if(platform.isOracle()) {
                proc.addStatement("out_param_one := 'One: ' || in_param_one || ' Two: ' || in_param_two || ' Three: ' || in_param_three");
            } else if (platform.isDB2() || platform.isDB2Z()) {
                proc.addStatement("SET out_param_one = 'One: ' || in_param_one || ' Two: ' || in_param_two || ' Three: ' || in_param_three");
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