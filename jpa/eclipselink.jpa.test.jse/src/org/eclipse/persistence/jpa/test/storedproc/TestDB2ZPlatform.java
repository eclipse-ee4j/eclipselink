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
//     01/31/2019 - Will Dazey
//       - 543846 : Add DB2z support for named parameters with stored procedures
package org.eclipse.persistence.jpa.test.storedproc;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestDB2ZPlatform {

    @Emf(name = "db2zemf", createTables = DDLGen.DROP_CREATE,
            properties = { @Property(name = "eclipselink.jdbc.property.enableNamedParameterMarkers", value = "true") })
    private EntityManagerFactory db2zEmf;

    /**
     * Tests stored procedure with named parameters on DB2/z
     * If this test fails, check to see if 'GET_SYSTEM_INFO' stored procedure exists
     */
    @Test
    public void testStoredProcedureNamedParameter() {
        //Test for DB2/z only
        Platform pl = db2zEmf.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
        if(!(pl.isDB2Z())) {
            return;
        }

        EntityManager em = db2zEmf.createEntityManager();
        try {
            //This should be a stored procedure that already exists by default
            StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("GET_SYSTEM_INFO ");
            storedProcedure.registerStoredProcedureParameter("major_version", Integer.class, ParameterMode.INOUT);
            storedProcedure.registerStoredProcedureParameter("minor_version", Integer.class, ParameterMode.INOUT);
            storedProcedure.registerStoredProcedureParameter("requested_locale", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("xml_input", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("xml_filter", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("xml_output", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("xml_message", String.class, ParameterMode.OUT);

            storedProcedure.setParameter("major_version", null);
            storedProcedure.setParameter("minor_version", null);
            storedProcedure.setParameter("requested_locale", null);
            storedProcedure.setParameter("xml_input", null);
            storedProcedure.setParameter("xml_filter", null);

            //This will throw an exception if anything is wrong
            storedProcedure.execute();

            //Some driver versions seem to return the wrong type and will throw a conversion exception here
            String xml_output =  (String) storedProcedure.getOutputParameterValue("xml_output");
            String xml_message =  (String) storedProcedure.getOutputParameterValue("xml_message");
            Assert.assertNotNull(xml_output);
            Assert.assertNotNull(xml_message);
            System.out.println("xml_output: " + xml_output);
            System.out.println("xml_message: " + xml_message);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }
}
