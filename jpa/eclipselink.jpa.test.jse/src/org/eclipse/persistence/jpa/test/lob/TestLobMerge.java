/*******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/14/2018-2.7 Will Dazey
 *       - 529602: Added support for CLOBs in DELETE statements for Oracle
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.lob;

import java.util.Set;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.lob.model.CollectedEntity;
import org.eclipse.persistence.jpa.test.lob.model.ParentEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestLobMerge {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { CollectedEntity.class, ParentEntity.class })
    private EntityManagerFactory emf;

    /**
     * Merging ElementCollections on Oracle fails when EclipseLink generates 
     * a DELETE SQL statement with a WHERE clause containing a CLOB.
     * 
     * @throws Exception
     */
    @Test
    public void testLobMerge() throws Exception {
        //Test for Oracle only
        Platform pl = emf.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
        if(!pl.isOracle()) {
            Assert.assertTrue("Platform \""+ pl +"\". Test will run on Oracle only", true);
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            final Set<CollectedEntity> col1 = new HashSet<CollectedEntity>(
                    Arrays.asList(new CollectedEntity[] { 
                            new CollectedEntity("label1", "content1"),
                            new CollectedEntity("label2", "content2"),
                            new CollectedEntity("label3", "content3") }));

            final ParentEntity pdo = new ParentEntity(9, Collections.unmodifiableSet(col1));
            em.getTransaction().begin();
            em.persist(pdo);
            em.getTransaction().commit();

            final Set<CollectedEntity> col2 = new HashSet<CollectedEntity>(
                    Arrays.asList(new CollectedEntity[] { 
                            new CollectedEntity("label1", "content1"),
                            new CollectedEntity("label2", "content2") }));
            final ParentEntity newEntity = new ParentEntity(pdo.getId(), col2);

            em.getTransaction().begin();
            em.merge(newEntity);
            //Failure would occur on merge, if it passed merge, test passed
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

}
