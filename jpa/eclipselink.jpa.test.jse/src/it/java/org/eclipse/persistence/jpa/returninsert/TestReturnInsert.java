/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa.returninsert;

import java.time.Instant;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.returninsert.model.*;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * TestSuite to test entities, that has a @ReturnInsert and @ReturnUpdate annotations.
 *
 * @author Radek Felcman
 */
public class TestReturnInsert {

    private EntityManagerFactory emf;
    private EntityManager em;

    @Test
    public void test() {
        boolean supported = true;
        emf = Persistence.createEntityManagerFactory("returninsert-pu");
        try {
            EntityManager em = emf.createEntityManager();
        } catch (Exception e) {
            supported = false;
            System.out.println("Non supported platform. This test can be executed on OraclePlatform only!");
        }
        if (em != null) {
            em.close();
        }
        if(!supported) {
            return;
        }
        setup();
        testCreate();
        testFindUpdate();
        testQuery();
        emf.close();
    }

    public void setup() {
        EntityManager em = emf.createEntityManager();
        try {
            DatabaseSession session = ((EntityManagerImpl) em).getDatabaseSession();
            try {
                session.executeNonSelectingSQL("DROP TABLE JPA22_RETURNINSERT_DETAIL");
            } catch (Exception ignore) {
            }
            try {
                session.executeNonSelectingSQL("DROP TABLE JPA22_RETURNINSERT_MASTER");
            } catch (Exception ignore) {
            }
            try {
                session.executeNonSelectingSQL("CREATE TABLE JPA22_RETURNINSERT_MASTER  (" +
                        "    ID_VIRTUAL NUMBER (2) AS ( TO_NUMBER(TO_CHAR(\"ID\",'DD')) ) VIRTUAL NOT NULL ," +
                        "    ID     DATE NOT NULL ," +
                        "    COL1     NUMBER (15) NOT NULL," +
                        "    COL1_VIRTUAL NUMBER (15) AS ( COL1 * 10 ) VIRTUAL)");
                session.executeNonSelectingSQL("ALTER TABLE JPA22_RETURNINSERT_MASTER ADD CONSTRAINT PKJPA22_RETURNINSERT_MASTER PRIMARY KEY ( ID_VIRTUAL, ID, COL1 )");
                session.executeNonSelectingSQL("CREATE TABLE JPA22_RETURNINSERT_DETAIL  (" +
                        "    ID_VIRTUAL NUMBER (2) AS ( TO_NUMBER(TO_CHAR(\"ID\",'DD')) ) VIRTUAL NOT NULL ," +
                        "    ID     DATE NOT NULL ," +
                        "    COL1     NUMBER (15) NOT NULL," +
                        "    COL1_VIRTUAL NUMBER (15) AS ( COL1 * 10 ) VIRTUAL," +
                        "    COL2     VARCHAR (15) NOT NULL," +
                        "    COL2_VIRTUAL VARCHAR (100) AS ( COL2 || '_col2' ) VIRTUAL," +
                        "    COL3     VARCHAR (15) NOT NULL," +
                        "    COL3_VIRTUAL VARCHAR (100) AS ( COL3 || '_col3' ) VIRTUAL," +
                        "    COL4     VARCHAR (15) NOT NULL," +
                        "    COL4_VIRTUAL VARCHAR (100) AS ( COL4 || '_col4' ) VIRTUAL," +
                        "    COL5     VARCHAR (15) NOT NULL," +
                        "    COL5_VIRTUAL VARCHAR (100) AS ( COL5 || '_col5' ) VIRTUAL)");
                session.executeNonSelectingSQL("ALTER TABLE JPA22_RETURNINSERT_DETAIL ADD CONSTRAINT PKJPA22_RETURNINSERT_DETAIL PRIMARY KEY ( ID_VIRTUAL, ID, COL1, COL2 )");
                session.executeNonSelectingSQL("ALTER TABLE JPA22_RETURNINSERT_DETAIL ADD CONSTRAINT FKJPA22_RETURNINSERT_MASTER_DETAIL FOREIGN KEY ( ID_VIRTUAL, ID, COL1 ) REFERENCES JPA22_RETURNINSERT_MASTER ( ID_VIRTUAL, ID, COL1 ) NOT DEFERRABLE");
            } catch (Exception ignore) {
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private void testCreate() {
        ReturnInsertMaster returnInsertMaster = null;
        ReturnInsertDetail returnInsertDetail = null;

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            //Prepare data
            //Test Insert
            returnInsertMaster = insertReturnInsertMaster(em);
            returnInsertDetail = insertReturnInsertDetail(em, returnInsertMaster);

        em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }

        assertEquals(1, returnInsertMaster.getId().getIdVirtual());
        assertEquals(10, returnInsertMaster.getCol1Virtual());
        assertEquals(1, returnInsertDetail.getId().getIdVirtual());
        assertEquals(10, returnInsertDetail.getCol1Virtual());
        assertEquals("abc_col2", returnInsertDetail.getReturnInsertDetailEmbedded().getCol2Virtual());
        assertEquals("opq_col4", returnInsertDetail.getCol4Virtual());
        assertEquals("lmn_col5", returnInsertDetail.getReturnInsertDetailEmbedded().getReturnInsertDetailEmbeddedEmbedded().getCol5Virtual());
    }

    private void testFindUpdate() {
        //Test find and update
        ReturnInsertDetail returnInsertDetailMerge = null;

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            ReturnInsertMasterPK returnInsertMasterPK = createReturnInsertMasterPK();
            returnInsertMasterPK.setIdVirtual(1L);
            ReturnInsertDetailPK returnInsertDetailPK = createReturnInsertDetailPK();
            //Must be set, it's not populated from DB virtual column
            returnInsertDetailPK.setIdVirtual(1L);
            ReturnInsertDetail returnInsertDetailFindResult = em.find(ReturnInsertDetail.class, returnInsertDetailPK);
            assertNotNull(returnInsertDetailFindResult);
            assertEquals(1, returnInsertDetailFindResult.getId().getIdVirtual());
            assertEquals("abc_col2", returnInsertDetailFindResult.getReturnInsertDetailEmbedded().getCol2Virtual());
            //Test update
            returnInsertDetailFindResult.getReturnInsertDetailEmbedded().setCol3("ijk");
            returnInsertDetailMerge = em.merge(returnInsertDetailFindResult);

            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
        assertEquals("ijk_col3", returnInsertDetailMerge.getReturnInsertDetailEmbedded().getCol3Virtual());
    }

    private void testQuery() {
        ReturnInsertDetail returnInsertDetailQueryResult = null;

        EntityManager em = emf.createEntityManager();

        try {
            ReturnInsertDetailPK returnInsertDetailPK = createReturnInsertDetailPK();
            //Must be set, it's not populated from DB virtual column
            returnInsertDetailPK.setIdVirtual(1L);
            Query query  = em.createQuery("select t from ReturnInsertDetail t where t.id = :returnInsertDetailId");
            query.setParameter("returnInsertDetailId", returnInsertDetailPK);
            returnInsertDetailQueryResult = (ReturnInsertDetail) query.getSingleResult();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
        assertNotNull(returnInsertDetailQueryResult);
        assertEquals(1, returnInsertDetailQueryResult.getId().getIdVirtual());
        assertEquals("abc_col2", returnInsertDetailQueryResult.getReturnInsertDetailEmbedded().getCol2Virtual());
        assertEquals("opq_col4", returnInsertDetailQueryResult.getCol4Virtual());
    }

    private ReturnInsertMaster insertReturnInsertMaster(EntityManager em) {
        //Prepare primary key
        ReturnInsertMasterPK ReturnInsertMasterPK = createReturnInsertMasterPK();

        //Prepare object/row
        ReturnInsertMaster tbReciboNfce = new ReturnInsertMaster();
        tbReciboNfce.setId(ReturnInsertMasterPK);

        em.persist(tbReciboNfce);
        return tbReciboNfce;
    }

    //Prepare primary key for ReturnInsertMaster
    private ReturnInsertMasterPK createReturnInsertMasterPK() {
        ReturnInsertMasterPK ReturnInsertMasterPK = new ReturnInsertMasterPK();
        ReturnInsertMasterPK.setId(Date.from(Instant.ofEpochMilli(0)));
        ReturnInsertMasterPK.setCol1(1L);
        return ReturnInsertMasterPK;
    }

    private ReturnInsertDetail insertReturnInsertDetail(EntityManager em, ReturnInsertMaster returnInsertMaster) {
        //Prepare primary key
        ReturnInsertDetailPK returnInsertDetailPK = createReturnInsertDetailPK();

        //Prepare object/row
        ReturnInsertDetail returnInsertDetail = new ReturnInsertDetail();
        returnInsertDetail.setId(returnInsertDetailPK);
        returnInsertDetail.setReturnInsertMaster(returnInsertMaster);

        //Prepare embedded embedded part
        ReturnInsertDetailEmbeddedEmbedded returnInsertDetailEmbeddedEmbedded = new ReturnInsertDetailEmbeddedEmbedded();
        returnInsertDetailEmbeddedEmbedded.setCol5("lmn");

        //Prepare embedded part
        ReturnInsertDetailEmbedded returnInsertDetailEmbedded = new ReturnInsertDetailEmbedded();
        returnInsertDetailEmbedded.setCol3("xyz");
        returnInsertDetailEmbedded.setReturnInsertDetailEmbeddedEmbedded(returnInsertDetailEmbeddedEmbedded);
        returnInsertDetail.setReturnInsertDetailEmbedded(returnInsertDetailEmbedded);

        //Inherited field
        returnInsertDetail.setCol4("opq");

        em.persist(returnInsertDetail);
        return returnInsertDetail;
    }

    //Prepare primary key for ReturnInsertDetail
    private ReturnInsertDetailPK createReturnInsertDetailPK() {
        ReturnInsertDetailPK returnInsertDetailPK = new ReturnInsertDetailPK();
        returnInsertDetailPK.setId(Date.from(Instant.ofEpochMilli(0)));
        returnInsertDetailPK.setCol1(1L);
        returnInsertDetailPK.setCol2("abc");
        return returnInsertDetailPK;
    }

}
