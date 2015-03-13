/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/13/2015 - Jody Grassel  
 *       - 462103 : SQL for Stored Procedure named parameter with DB2 generated with incorrect marker
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.jpa.test.basic.model.Dog;
import org.eclipse.persistence.jpa.test.basic.model.Employee;
import org.eclipse.persistence.jpa.test.basic.model.Person;
import org.eclipse.persistence.jpa.test.basic.model.XmlFish;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.sessions.Session;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestStoredProcedure {
    private static final String DB2_STOREDPROC_CREATE_SQL = 
            "CREATE OR REPLACE PROCEDURE TSPONE " + 
            "(IN dogid INTEGER) " + 
            "DYNAMIC RESULT SETS 1 " + 
            "LANGUAGE SQL " + 
            "BEGIN " + 
               "DECLARE result_set_1 CURSOR WITH RETURN FOR " + 
                   "SELECT name FROM Dog " + 
                   "WHERE id=dogid; " + 
               "OPEN result_set_1; " + 
               "END";

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { Dog.class, XmlFish.class, Person.class, Employee.class },
            properties = { @Property(name = "eclipselink.cache.shared.default", value = "false") },
            mappingFiles = { "META-INF/fish-orm.xml" })
    private EntityManagerFactory emf;

    @Test
    public void testStoredProcedure() throws Exception {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        try {
            if (!testSupportsDB(em)) {
                return; // Unsupported database, skip test execution.
            }
            setupStoredProc(em);

            List<Dog> dogList = createDogs(emf);

            StoredProcedureQuery spq = em.createStoredProcedureQuery("TSPONE");
            spq.registerStoredProcedureParameter("dogid", Integer.class, ParameterMode.IN);
            spq.setParameter("dogid", dogList.get(0).getId());

            List res = spq.getResultList();
            assertNotNull(res);
            assertEquals(1, res.size());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }
    }

    private boolean testSupportsDB(EntityManager em) {
        Session s = em.unwrap(Session.class);
        Platform platform = s.getDatasourcePlatform();

        boolean supportDB = false;
        supportDB |= platform.isDB2();
        return supportDB;
    }

    private void setupStoredProc(EntityManager em) throws Exception {
        String stmt = DB2_STOREDPROC_CREATE_SQL;

        em.getTransaction().begin();
        Connection conn = em.unwrap(Connection.class);
        CallableStatement cs = conn.prepareCall(stmt);
        cs.execute();
        em.getTransaction().commit();
    }

    private List<Dog> createDogs(EntityManagerFactory emf) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            ArrayList<Dog> dogList = new ArrayList<Dog>();
            em.getTransaction().begin();

            for (int i = 10; i > 0; i--) {
                Dog aDog = new Dog();
                aDog.setName("Dog " + i);
                em.persist(aDog);
                dogList.add(aDog);
            }

            em.getTransaction().commit();

            return dogList;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
