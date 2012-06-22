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
package org.eclipse.persistence.testing.tests.distributedservers;

import java.util.Enumeration;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Test changing private parts of an object.
 */
public class ComplexUpdateTest extends WriteObjectTest {

    /** The object which is actually changed */
    public Object workingCopy;

    /** This is the object from the distributed server */
    public Object distributedCopy;

    public UnitOfWork unitOfWork;

    public ComplexUpdateTest() {
        super();
    }

    public ComplexUpdateTest(Object originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        // By default do nothing
    }

    public String getName() {
        return super.getName() + " " + "Distributed";
    }

    /**
     * This method iterates over the distributed servers until one
     * returns an object.
     * Creation date: (7/21/00 10:33:48 AM)
     * @param query org.eclipse.persistence.queries.DatabaseQuery
     */
    public Object getObjectFromDistributedSession(DatabaseQuery query) {
        Enumeration servers = DistributedServersModel.getDistributedServers().elements();
        while (servers.hasMoreElements()) {
            try {
                Object result = ((DistributedServer)servers.nextElement()).getDistributedSession().executeQuery(query);
                if (result != null) {
                    return result;
                }
            } catch (Exception exception) {
            }
        }
        return null;
    }

    public boolean isObjectValidOnDistributedServer(Object object) {
        Enumeration servers = DistributedServersModel.getDistributedServers().elements();
        while (servers.hasMoreElements()) {
            try {
                DistributedServer server = (DistributedServer)servers.nextElement();
                Object result = server.getDistributedSession().getIdentityMapAccessor().getFromIdentityMap(object);
                if (result != null) {
                    return server.isObjectValid(object);
                }
            } catch (Exception exception) {
            }
        }
        return true;
    }

    /**
     * This method returns the test's reference to the current UnitOfWork
     * @return org.eclipse.persistence.sessions.UnitOfWork
     */
    public org.eclipse.persistence.sessions.UnitOfWork getUnitOfWork() {
        return unitOfWork;
    }

    public void reset() {
        super.reset();
        Enumeration enumtr = DistributedServersModel.getDistributedServers().elements();
        while (enumtr.hasMoreElements()) {
            (((DistributedServer)enumtr.nextElement()).getDistributedSession()).getIdentityMapAccessor().initializeAllIdentityMaps();
        }
    }

    /**
     * Set the currently available UnitOfWork in this test
     * @param newUnitOfWork org.eclipse.persistence.sessions.UnitOfWork
     */
    public void setUnitOfWork(org.eclipse.persistence.sessions.UnitOfWork newUnitOfWork) {
        unitOfWork = newUnitOfWork;
    }

    protected void setup() {
        super.setup();
        //Make sure that the object has been loaded on the remote server
        this.distributedCopy = setupDistributedSessions(this.query);
        setUnitOfWork(getSession().acquireUnitOfWork());
        this.workingCopy = getUnitOfWork().registerObject(this.objectToBeWritten);
    }

    /**
     * This method iterates over the distributed servers querying an Object
     * @param query org.eclipse.persistence.queries.DatabaseQuery
     */
    public Object setupDistributedSessions(DatabaseQuery query) {
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        try {
            Object result = server.getDistributedSession().executeQuery(query);
            ((Employee)result).getManagedEmployees();
            ((Employee)result).getPhoneNumbers();
            ((Employee)result).getAddress();
            ((Employee)result).getManager();
            ((Employee)result).getProjects();
            ((Employee)result).getResponsibilitiesList();
            return result;
        } catch (Exception exception) {
            return null;
        }
    }

    protected void test() {
        changeObject();
        // Ensure that the original has not been changed.
        if (!getUnitOfWork().getParent().compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
        getUnitOfWork().commit();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        this.objectFromDatabase = getSession().executeQuery(this.query);

        if (!(((AbstractSession)getSession()).compareObjects(this.objectToBeWritten, this.objectFromDatabase))) {
            throw new TestErrorException("The object inserted into the database, '" + this.objectFromDatabase + "' does not match the original, '" + this.objectToBeWritten + ".");
        }

        this.distributedCopy = getObjectFromDistributedSession(this.query);
        if (!(((AbstractSession)getSession()).compareObjects(this.distributedCopy, this.objectFromDatabase))) {
            throw new TestErrorException("The object from the distributed cache, '" + this.objectFromDatabase + "' does not match the original, '" + this.objectToBeWritten + ".");
        }

    }
}
