/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.types;

import java.io.*;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.*;

// Bug#4364359  This test is added to test the fix to overcome TIMESTAMPTZ not serializable 
// as of jdbc 9.2.0.5 and 10.1.0.2.  It has been fixed in the next version for both streams

public class SerializationOfValueHolderWithTIMESTAMPTZTest extends TestCase {

    Exception ex;
    
    public SerializationOfValueHolderWithTIMESTAMPTZTest() {
    }

    public void setup() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(TIMESTAMPTZOwner.example1());
        uow.commit();
        
        uow = getSession().acquireUnitOfWork();
        uow.registerObject(TIMESTAMPTZOwner.example2());
        uow.commit();
                        
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("This test cannot be run through the remote.");
        }
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    public void test() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(byteStream);
    
            TIMESTAMPTZOwner tstz = (TIMESTAMPTZOwner) getSession().readObject(TIMESTAMPTZOwner.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("id").equal(2));	
            
            //serialize object by writing to a stream
            stream.writeObject(tstz);
            stream.flush();
            byte arr[] = byteStream.toByteArray();
            ByteArrayInputStream inByteStream = new ByteArrayInputStream(arr);
            ObjectInputStream inObjStream = new ObjectInputStream(inByteStream);
            
            //deserialize the object
            try {
                TIMESTAMPTZOwner ts = (TIMESTAMPTZOwner) inObjStream.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println("Could not deserialize object " + e.toString());
            }
        } catch (Exception e) {
            ex = e;
        }
    }

     public void verify() {
        if (ex != null) {
            if (ex.toString().startsWith("java.io.NotSerializableException: oracle.sql.TIMESTAMPTZ")) {
                throw new TestErrorException("Serialization of value holder with TIMESTAMPTZ failed");            
            } else {
                throw new TestErrorException("This test throws an exception", ex); 
            }
        }
     }
}
