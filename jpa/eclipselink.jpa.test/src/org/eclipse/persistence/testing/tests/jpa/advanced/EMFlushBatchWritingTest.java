/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/18/2010-2.2 Chris Delahunt
 *       - bug:323370 - flush() doesn't flush native queries if batch writing is enabled 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;
import org.eclipse.persistence.testing.framework.TestWarningException;

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
                //exception not expected if the query gets added to the batch, delaying query being executed until flush.  
                //This may change in the future if native queries are not added to batches
                throw new TestWarningException("ExcuteUpdate threw an exception while using batch writing instead " +
                		"of waiting for the commit/flush call" );
            }
            try {
                //exception should be thrown here if query gets added to the batch and batching gets flushed correctly.  
                em.flush();
            } catch (Exception e) {
                 expectedException = e;
            }
            
        } finally {
            this.rollbackTransaction();
        }
        this.assertNotNull("Native query did not get executed when EntityManager was flushed", expectedException);
    }

}
