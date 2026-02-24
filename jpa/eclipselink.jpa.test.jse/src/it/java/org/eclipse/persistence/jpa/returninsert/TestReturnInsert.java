/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.returninsert.model.ReturnInsertDetail;
import org.eclipse.persistence.jpa.returninsert.model.ReturnInsertDetailEmbedded;
import org.eclipse.persistence.jpa.returninsert.model.ReturnInsertDetailEmbeddedEmbedded;
import org.eclipse.persistence.jpa.returninsert.model.ReturnInsertDetailJoined;
import org.eclipse.persistence.jpa.returninsert.model.ReturnInsertDetailPK;
import org.eclipse.persistence.jpa.returninsert.model.ReturnInsertMaster;
import org.eclipse.persistence.jpa.returninsert.model.ReturnInsertMasterPK;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.fail;

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
        emf = Persistence.createEntityManagerFactory("returninsert-pu");
        try {
            if (!emf.unwrap(JpaEntityManagerFactory.class).getServerSession().getPlatform().isOracle()) {
                System.out.println("Non supported platform. This test can be executed on OraclePlatform only!");
                return;
            }
        } catch (PersistenceException pe) {
            System.out.println("Non supported platform. This test can be executed on OraclePlatform only!");
            return;
        }
        setup();
        testCreate();
        testFindUpdate();
        testQuery();
        testCreateJoined();
        testUpdateMasterDetailJoined();
        testUpdateDetailJoined();
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
                session.executeNonSelectingSQL("DROP TABLE JPA22_RETURNINSERT_DETAIL_JOINED");
            } catch (Exception ignore) {
            }
            try {
                session.executeNonSelectingSQL("DROP TABLE JPA22_RETURNINSERT_MASTER_JOINED");
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
                        "    COL5_VIRTUAL VARCHAR (100) AS ( COL5 || '_col5' ) VIRTUAL," +
                        "    COL6     VARCHAR (15) NOT NULL," +
                        "    COL6_VIRTUAL VARCHAR (100) AS ( COL6 || '_col6' ) VIRTUAL)");
                session.executeNonSelectingSQL("ALTER TABLE JPA22_RETURNINSERT_DETAIL ADD CONSTRAINT PKJPA22_RETURNINSERT_DETAIL PRIMARY KEY ( ID_VIRTUAL, ID, COL1, COL2 )");
                session.executeNonSelectingSQL("ALTER TABLE JPA22_RETURNINSERT_DETAIL ADD CONSTRAINT FKJPA22_RETURNINSERT_MASTER_DETAIL FOREIGN KEY ( ID_VIRTUAL, ID, COL1 ) REFERENCES JPA22_RETURNINSERT_MASTER ( ID_VIRTUAL, ID, COL1 ) NOT DEFERRABLE");
                session.executeNonSelectingSQL("CREATE TABLE JPA22_RETURNINSERT_MASTER_JOINED  (" +
                        "    ID     NUMBER(15) PRIMARY KEY ," +
                        "    TYPE   VARCHAR2(50) NOT NULL)");
                session.executeNonSelectingSQL("CREATE TABLE JPA22_RETURNINSERT_DETAIL_JOINED  (" +
                        "    ID          NUMBER(15) PRIMARY KEY ," +
                        "    DETAIL_NR           NUMBER(15)," +
                        "    DETAIL_NR_VIRTUAL   NUMBER(15) AS ( DETAIL_NR * 10 ) VIRTUAL)");
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
        } catch (Exception e) {
            fail(e.getMessage());
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

    private void testCreateJoined() {
        ReturnInsertDetailJoined returnInsertDetailJoined = null;

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            returnInsertDetailJoined = new ReturnInsertDetailJoined(1L, 1L, "TYPE_A");
            returnInsertDetailJoined = em.merge(returnInsertDetailJoined);

            em.getTransaction().commit();
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
        assertEquals(Long.valueOf(1L), returnInsertDetailJoined.getId());
        assertEquals("TYPE_A", returnInsertDetailJoined.getType());
        assertEquals(Long.valueOf(10L), returnInsertDetailJoined.getDetailNumberVirtual());
    }

    private void testUpdateMasterDetailJoined() {
        ReturnInsertDetailJoined returnInsertDetailJoined = null;

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            returnInsertDetailJoined = em.find(ReturnInsertDetailJoined.class, 1L);
            returnInsertDetailJoined.setType("TYPE_B");
            returnInsertDetailJoined.setDetailNumber(22L);
            returnInsertDetailJoined = em.merge(returnInsertDetailJoined);

            em.getTransaction().commit();
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
        assertEquals(Long.valueOf(1L), returnInsertDetailJoined.getId());
        assertEquals("TYPE_B", returnInsertDetailJoined.getType());
        assertEquals(Long.valueOf(220L), returnInsertDetailJoined.getDetailNumberVirtual());
    }

    private void testUpdateDetailJoined() {
        ReturnInsertDetailJoined returnInsertDetailJoined = null;

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            returnInsertDetailJoined = em.find(ReturnInsertDetailJoined.class, 1L);
            returnInsertDetailJoined.setDetailNumber(33L);
            returnInsertDetailJoined = em.merge(returnInsertDetailJoined);

            em.getTransaction().commit();
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
        assertEquals(Long.valueOf(1L), returnInsertDetailJoined.getId());
        assertEquals("TYPE_B", returnInsertDetailJoined.getType());
        assertEquals(Long.valueOf(330L), returnInsertDetailJoined.getDetailNumberVirtual());
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
            returnInsertDetailFindResult.setCol6("rst");
            returnInsertDetailMerge = em.merge(returnInsertDetailFindResult);

            em.getTransaction().commit();
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
        assertEquals("ijk_col3", returnInsertDetailMerge.getReturnInsertDetailEmbedded().getCol3Virtual());
        assertEquals("rst_col6", returnInsertDetailMerge.getCol6Virtual());
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
        } catch (Exception e) {
            fail(e.getMessage());
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
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            Date date = dateFormat.parse("1970-01-01 00:00:00.0");
            ReturnInsertMasterPK.setId(date);
        } catch (Exception e) {
            fail(e.getMessage());
        }
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

        returnInsertDetail.setCol6("rst");

        em.persist(returnInsertDetail);
        return returnInsertDetail;
    }

    //Prepare primary key for ReturnInsertDetail
    private ReturnInsertDetailPK createReturnInsertDetailPK() {
        ReturnInsertDetailPK returnInsertDetailPK = new ReturnInsertDetailPK();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            Date date = dateFormat.parse("1970-01-01 00:00:00.0");
            returnInsertDetailPK.setId(date);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        returnInsertDetailPK.setCol1(1L);
        returnInsertDetailPK.setCol2("abc");
        return returnInsertDetailPK;
    }
}
