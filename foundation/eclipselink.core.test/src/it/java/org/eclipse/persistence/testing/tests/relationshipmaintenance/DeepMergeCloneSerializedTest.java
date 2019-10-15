/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.relationshipmaintenance;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.relationshipmaintenance.Dept;
import org.eclipse.persistence.testing.models.relationshipmaintenance.Emp;

/**
 * This test checks on a previous problem TopLink had with merging clones and
 * serialization.  If an existing object was serialized and deserialized and another
 * reference added to that object.  Then the object was serialized and deserialized
 * and deepMergeClone'd into a unit of work and committed.
 * When the origional object was then serialized and deserialized and modified, then serialized
 * and deserialized and deepMergeClone'd with another unit of work and committed the changes are
 * not saved
 */
public class DeepMergeCloneSerializedTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Dept origional;
    public Dept mergedClone;
    public Dept deptObject;

    public DeepMergeCloneSerializedTest() {
        setDescription("This test verifies that deepMergeClone works after a serialized transaction");
    }

    public void setup() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("This test cannot be run through the remote.");
        }
        beginTransaction();
    }

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    /*
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
            Session session = getSession();
            UnitOfWork uow = session.acquireUnitOfWork();
            this.deptObject = (Dept)session.readObject(Dept.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("deptno").equal(5.0));
            Dept deptClone = (Dept)uow.registerObject(this.deptObject);

            //force instantiations of value holders before serialization
            deptClone.getEmpCollection().size();
            Emp empClone = null;
            if ((deptClone.getEmpCollection() != null) && (deptClone.getEmpCollection().size() > 1)) {
                empClone = (Emp)deptClone.getEmpCollection().iterator().next();
                deptClone.getEmpCollection().remove(empClone);
                empClone.setDeptno(null);
            }

            //serialize object by writing to a stream
            stream.writeObject(deptClone);
            stream.writeObject(empClone);
            stream.flush();
            byte[] arr = byteStream.toByteArray();
            ByteArrayInputStream inByteStream = new ByteArrayInputStream(arr);
            ObjectInputStream inObjStream = new ObjectInputStream(inByteStream);
            Dept deserialDept;
            Emp deserialEmp = null;

            //deserialize the object
            try {
                deserialDept = (Dept)inObjStream.readObject();
                deserialEmp = (Emp)inObjStream.readObject();

            } catch (ClassNotFoundException e) {
                throw new TestErrorException("Could not deserialize object " + e.toString());
            }

            //add a new manager, test 1-m's
            /*            if (deserialDept.getEmpCollection() != null && deserialDept.getEmpCollection().size() > 1) {
                            deserialEmp =  (Emp)deserialDept.getEmpCollection().iterator().next();
                            deserialDept.getEmpCollection().remove(deserialEmp);
                            deserialEmp.setDeptno(null);
                        }
              */
            deserialDept.getEmpCollection().add(deserialEmp);
            deserialEmp.setDeptno(deserialDept);
            int collectionSize = deserialDept.getEmpCollection().size();

            byteStream = new ByteArrayOutputStream();
            stream = new ObjectOutputStream(byteStream);
            //send the ammended object back through the serialization process
            stream.writeObject(deserialDept);
            stream.flush();
            arr = byteStream.toByteArray();
            inByteStream = new ByteArrayInputStream(arr);
            inObjStream = new ObjectInputStream(inByteStream);
            try {
                deserialDept = (Dept)inObjStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new TestErrorException("Could not deserialize object " + e.toString());
            }

            //merge the ammended clone with the unit of work
            deptClone = (Dept)uow.deepMergeClone(deserialDept);
            if (deptClone.getEmpCollection().size() != collectionSize) {
                throw new TestErrorException("Failed to merge the collection correctly not enough Emps");
            }
            for (Iterator iterator = deptClone.getEmpCollection().iterator(); iterator.hasNext();) {
                Emp emp = (Emp)iterator.next();
                if (emp.getDeptno() != deptClone) {
                    throw new TestErrorException("Failed to merge the back pointer");
                }
            }
            uow.commit();
        } catch (IOException e) {
            throw new TestErrorException("Error running Test " + e.toString());
        }
    }
}
