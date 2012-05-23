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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Collection;

import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.transparentindirection.Order;
import org.eclipse.persistence.testing.models.transparentindirection.OrderLine;


/**
 * This test checks on a previous problem TopLink had with merging clones and
 * serialization.  If an existing object was serialized and deserialized and another
 * reference added to that object.  Then the object was serialized and deserialized
 * and deepMergeClone'd into a unit of work and committed.
 * When the origional object was then serialized and deserialized and modified, then serialized
 * and deserialized and deepMergeClone'd with another unit of work and committed the changes are
 * not saved
 */
public class DeepMergeCloneIndirectionTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Order orderObject;

    public DeepMergeCloneIndirectionTest() {
        setDescription("This test verifies that deepMergeClone works after a serialized transaction.  When nested objects use indirection.");
    }

    public void setup() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("This test cannot be run through the remote.");
        }
        getAbstractSession().beginTransaction();
        this.orderObject = new Order();
        this.orderObject.contacts = new IndirectList();
        this.orderObject.lines = new IndirectList();
        this.orderObject.setTotal(56789);
        this.orderObject.customerName = "henry";
        //Using a unit of work here becuase this test is used in the clientSession Tests
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(this.orderObject);
        uow.commit();
    }

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    /**
     * This test creates an object and registers it with a unit of work.  It then serializes that
     * object and deserializes it.  Adds an object onto the origional then performs serialization
     * sequence again.  Then deepMergeClone is attempted and the results are compared to verify that
     * the merge worked.
     */
    public void test() {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(byteStream);

            //create the phoneNumber object
            Order orderClone;
            Session session = getSession();
            org.eclipse.persistence.sessions.UnitOfWork uow = session.acquireUnitOfWork();

            this.orderObject = (Order)session.readObject(Order.class);
            //force instantiations of value holders before serialization	
            ((Collection)this.orderObject.getLineContainer()).size();

            //serialize object by writing to a stream
            stream.writeObject(this.orderObject);
            stream.flush();
            byte[] arr = byteStream.toByteArray();
            ByteArrayInputStream inByteStream = new ByteArrayInputStream(arr);
            ObjectInputStream inObjStream = new ObjectInputStream(inByteStream);
            Order deserialOrder;

            //deserialize the object
            try {
                deserialOrder = (Order)inObjStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new TestErrorException("Could not deserialize object " + e.toString());
            }

            //add a new manager, test 1-m's
            OrderLine newLine = new OrderLine();
            ((Collection)deserialOrder.getLineContainer()).clear();
            deserialOrder.addLine(newLine);
            newLine.order = deserialOrder;

            orderClone = (Order)uow.registerObject(this.orderObject);

            orderClone = (Order)uow.deepMergeClone(deserialOrder);
            uow.commit();
        } catch (IOException e) {
            throw new TestErrorException("Error running Test " + e.toString());
        } catch (NullPointerException exception) {
            throw new TestErrorException("Test Failed.  Backup clone indirection was not triggered in mergeIntoObject.");
        }
    }

    /*
     * Checks to see that the names of the updated version and the origional are the same
     */

    public void verify() {
    }
}
