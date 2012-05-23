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

package org.eclipse.persistence.testing.tests.wdf.jpa1.entitymanager;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestUpdateBatching extends JPA1Base {

   
    private void testUpdate(boolean batch) throws NamingException, SQLException {
        
        clearAllTables();
        final JPAEnvironment env = getEnvironment();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(PersistenceUnitProperties.DDL_GENERATION, "none"); 
        map.put(PersistenceUnitProperties.BATCH_WRITING, batch ? "JDBC" : "None"); 
        EntityManagerFactory emf = env.createNewEntityManagerFactory(map); 
        try {
            final EntityManager em = emf.createEntityManager();
            try {
                Department dep1 = new Department(1, "HR");
                Department dep2 = new Department(2, "FI");
                Department dep3 = new Department(3, "QA");
                env.beginTransaction(em);
                em.persist(dep1);
                em.persist(dep2);
                em.persist(dep3);
                env.commitTransactionAndClear(em);

                env.beginTransaction(em);
                dep1 = em.find(Department.class, 1);
                dep2 = em.find(Department.class, 2);
                dep3 = em.find(Department.class, 3);
                
                dep1.setName("hr");
                dep2.setName("fi");
                
                env.commitTransactionAndClear(em);

                dep1 = em.find(Department.class, 1);
                assertEquals("hr", dep1.getName());
                dep2 = em.find(Department.class, 2);
                assertEquals("fi", dep2.getName());
                dep3 = em.find(Department.class, 3);
                assertEquals("QA", dep3.getName());
            } finally {
                closeEntityManager(em);
            }
            
        } finally {
            emf.close();
        }
    }
    
    @Test
    public void testPossiblyBatchedUpdate() throws NamingException, SQLException {
        testUpdate(true);
    }
    
    @Test
    public void testUnbacthedUpdate() throws NamingException, SQLException {
        testUpdate(false);
    }

}
