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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.models.aggregate.Worker;

/**
 * Tests merge of an Aggregate that has a Transformation Mapping.
 * Worker has an Aggregate Mapping to Job, and Job has a Transformation Mapping.
 */
public class TransformationMappingTest extends AutoVerifyTestCase {
    Worker myWorker;

    public TransformationMappingTest() {
    }

    public void reset() {
        Worker worker = (Worker)getSession().readObject(Worker.class);
        getDatabaseSession().deleteObject(worker);
    }

    public void setup() {
        myWorker = Worker.example1();
        getDatabaseSession().writeObject(myWorker);//don't use unit of work here
    }

    public void test() {
        Worker worker;
        UnitOfWork uow = getSession().acquireUnitOfWork();
        try {
            worker = (Worker)uow.readObject(myWorker);
        } catch (NullPointerException npe) {
            throw new TestErrorException("Null pointer exception thrown during uow register object.  Test Failed.");
        }
        java.sql.Time[] normalHours = new java.sql.Time[2];
        normalHours[0] = Helper.timeFromHourMinuteSecond(7, 0, 0);
        normalHours[1] = Helper.timeFromHourMinuteSecond(17, 0, 0);
        worker.getJob().setNormalHours(normalHours);

        try {
            uow.commit();
        } catch (NullPointerException npe) {
            throw new TestErrorException("Null pointer exception thrown during uow commit.  Test Failed.");
        }

        uow = getSession().acquireUnitOfWork();
        try {
            worker = (Worker)uow.readObject(Worker.class);
        } catch (NullPointerException npe) {
            throw new TestErrorException("Null pointer exception thrown during uow register object.  Test Failed.");
        }
        java.sql.Time[] normalHours2 = new java.sql.Time[2];
        normalHours2[0] = Helper.timeFromHourMinuteSecond(6, 30, 0);
        normalHours2[1] = Helper.timeFromHourMinuteSecond(20, 30, 0);
        worker.getJob().setNormalHours(normalHours2);

        try {
            uow.commit();
        } catch (NullPointerException npe) {
            throw new TestErrorException("Null pointer exception thrown during uow commit.  Test Failed.");
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 11:39:54 AM)
     */
    public void verify() {
    }
}
