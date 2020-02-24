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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.jpa.advanced.returninsert.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;


import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.Date;

/**
 * Tests for an entities that has a @ReturnInsert and @ReturnUpdate annotations.
 * exceptions on any of the CRUD operations of an employee with multiple
 *
 * @author Radek Felcman
 */
public class ReturnInsertTest extends EntityContainerTestBase  {
    protected boolean reset = false;    // reset gets called twice on error
    private DatabaseSession session = (DatabaseSession)((EntityManagerImpl)getEntityManager()).getActiveSession();

    public void setup () {
        super.setup();
        this.reset = true;

        try {
            if (!session.getPlatform().isOracle()) {
                session.executeNonSelectingSQL("DROP TABLE JPA22_RETURNINSERT_DETAIL");
                session.executeNonSelectingSQL("DROP TABLE JPA22_RETURNINSERT_MASTER");
            }
        } catch (Exception ignore) {}
        try {
            if (!session.getPlatform().isOracle()) {
                session.executeNonSelectingSQL("CREATE TABLE JPA22_RETURNINSERT_MASTER  (ID_VIRTUAL NUMBER (2) AS ( TO_NUMBER(TO_CHAR(\"ID\",'DD')) ) VIRTUAL NOT NULL , ID     DATE NOT NULL , COL1 NUMBER (15) NOT NULL, COL1_VIRTUAL NUMBER (15) AS ( COL1 * 10 ) VIRTUAL)");
                session.executeNonSelectingSQL("ALTER TABLE JPA22_RETURNINSERT_MASTER ADD CONSTRAINT PKJPA22_RETURNINSERT_MASTER PRIMARY KEY ( ID_VIRTUAL, ID, COL1 )");
                session.executeNonSelectingSQL("CREATE TABLE JPA22_RETURNINSERT_DETAIL  (ID_VIRTUAL NUMBER (2) AS ( TO_NUMBER(TO_CHAR(\"ID\",'DD')) ) VIRTUAL NOT NULL , ID     DATE NOT NULL , COL1     NUMBER (15) NOT NULL, COL1_VIRTUAL NUMBER (15) AS ( COL1 * 10 ) VIRTUAL, COL2     VARCHAR (15) NOT NULL, COL2_VIRTUAL VARCHAR (100) AS ( COL2 || '_col2' ) VIRTUAL, COL3     VARCHAR (15) NOT NULL, COL3_VIRTUAL VARCHAR (100) AS ( COL3 || '_col3' ) VIRTUAL)");
                session.executeNonSelectingSQL("ALTER TABLE JPA22_RETURNINSERT_DETAIL ADD CONSTRAINT PKJPA22_RETURNINSERT_DETAIL PRIMARY KEY ( ID_VIRTUAL, ID, COL1, COL2 )");
                session.executeNonSelectingSQL("ALTER TABLE JPA22_RETURNINSERT_DETAIL ADD CONSTRAINT FKJPA22_RETURNINSERT_MASTER_DETAIL FOREIGN KEY ( ID_VIRTUAL, ID, COL1 ) REFERENCES JPA22_RETURNINSERT_MASTER ( ID_VIRTUAL, ID, COL1 ) NOT DEFERRABLE");
            }
        } catch (Exception ignore) {}

    }

    public void reset () {
        if (reset) {
            reset = false;
        }
        super.reset();
    }

    public void test() throws Exception {
        if (!session.getPlatform().isOracle()) {
            createTest();
            findUpdateTest();
        }
    }

    private void createTest() {
        EntityManager em = getEntityManager();

        //Prepare data
        //Test Insert
        em.getTransaction().begin();
        ReturnInsertMaster returnInsertMaster = insertReturnInsertMaster(em);
        ReturnInsertDetail returnInsertDetail = insertReturnInsertDetail(em, returnInsertMaster);

        em.getTransaction().commit();

        em.close();

        assertEquals(1, returnInsertMaster.getId().getIdVirtual());
        assertEquals(10, returnInsertMaster.getCol1Virtual());
        assertEquals(1, returnInsertDetail.getId().getIdVirtual());
        assertEquals(10, returnInsertDetail.getCol1Virtual());
        assertEquals("abc_col2", returnInsertDetail.getReturnInsertDetailEmbedded().getCol2Virtual());
    }

    private void findUpdateTest() {
        //Test find and update
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        ReturnInsertDetailPK returnInsertDetailPK = createReturnInsertDetailPK();
        //Must be set, it's not populated from DB virtual column
        returnInsertDetailPK.setIdVirtual(1L);
        ReturnInsertDetail returnInsertDetailFindResult = em.find(ReturnInsertDetail.class, returnInsertDetailPK);
        assertNotNull(returnInsertDetailFindResult);
        assertEquals(1, returnInsertDetailFindResult.getId().getIdVirtual());
        assertEquals("abc_col2", returnInsertDetailFindResult.getReturnInsertDetailEmbedded().getCol2Virtual());
        //Test update
        returnInsertDetailFindResult.getReturnInsertDetailEmbedded().setCol3("ijk");
        ReturnInsertDetail returnInsertDetailMerge = em.merge(returnInsertDetailFindResult);
        em.getTransaction().commit();
        assertEquals("ijk_col3", returnInsertDetailMerge.getReturnInsertDetailEmbedded().getCol3Virtual());
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

        //Prepare embedded part
        ReturnInsertDetailEmbedded returnInsertDetailEmbedded = new ReturnInsertDetailEmbedded();
        returnInsertDetailEmbedded.setCol3("xyz");
        returnInsertDetail.setReturnInsertDetailEmbedded(returnInsertDetailEmbedded);

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
