/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     10/18/2010-2.2 Chris Delahunt
//       - bug:323370 - flush() doesn't flush native queries if batch writing is enabled
package org.eclipse.persistence.testing.tests.jpa.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * @author cdelahun
 *
 */
public class EMFlushBatchWritingTest extends EntityContainerTestBase {

    //reset gets called twice on error
    protected boolean reset = false;
    /**
     *
     */
    public EMFlushBatchWritingTest() {
        setDescription("Test flush multiple times in EntityManager");
    }
    private boolean usesBatchWriting;
    private boolean usesJDBCBatchWriting;
    public void setup() {
        super.setup();
        JpaEntityManager em = JpaHelper.getEntityManager(getEntityManager());
        DatabasePlatform platform = em.getServerSession().getPlatform();
        usesBatchWriting = platform.usesBatchWriting();
        usesJDBCBatchWriting = platform.usesJDBCBatchWriting();

        platform.setUsesBatchWriting(true);
        platform.setUsesJDBCBatchWriting(true);

        em.close();

        this.reset = true;
    }

    public void reset() {
        if (reset){
            JpaEntityManager em = JpaHelper.getEntityManager(getEntityManager());
            DatabasePlatform platform = em.getServerSession().getPlatform();
            platform.setUsesBatchWriting(usesBatchWriting);
            platform.setUsesJDBCBatchWriting(usesJDBCBatchWriting);

            em.close();
            reset = false;
        }
        super.reset();
    }

    public void test(){
        Exception expectedException = null;
        try {
            beginTransaction();
            EntityManager em = getEntityManager();
            //create a native query that will throw an exception on execution
            Query query = em.createNativeQuery("unexecutable native sql query");

            try {
                query.executeUpdate();
            } catch (Exception e) {
                expectedException = e;
            }
            em.flush();
        } finally {
            this.rollbackTransaction();
        }
        this.assertNotNull("Native query did not get executed when EntityManager was flushed", expectedException);
    }

}
