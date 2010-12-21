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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Bug 3241138
 * Ensure Rediectors are properly called when used through the Query Manager on
 * operations such as session.readObject()
 * Note: This test uses static variables for it's test condition and will not run very
 * well on multithreaded test systems.
 */
public class RedirectorOnDescriptorTest extends TestCase {

    protected static boolean redirectedReadObject = false;
    protected static boolean redirectedReadAll = false;
    protected static boolean redirectedDeleteObject = false;
    protected static boolean redirectedUpdate = false;
    protected static boolean redirectedInsert = false;

    protected ReadObjectQuery readObjectQuery = null;
    protected ReadAllQuery readAllQuery = null;
    protected DeleteObjectQuery deleteObjectQuery = null;
    protected InsertObjectQuery insertQuery = null;
    protected UpdateObjectQuery updateQuery = null;

    protected ClassDescriptor descriptor = null;

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getAbstractSession().beginTransaction();

        descriptor = getSession().getProject().getDescriptors().get(Employee.class);

        // Read Object
        readObjectQuery = descriptor.getQueryManager().getReadObjectQuery();
        QueryRedirector redirector = new MethodBaseQueryRedirector(RedirectorOnDescriptorTest.class, "readObject");
        ReadObjectQuery roq = new ReadObjectQuery(descriptor.getJavaClass());
        roq.setRedirector(redirector);
        descriptor.getQueryManager().setReadObjectQuery(roq);

        // Read All 
        readAllQuery = descriptor.getQueryManager().getReadAllQuery();
        redirector = new MethodBaseQueryRedirector(RedirectorOnDescriptorTest.class, "readAll");
        ReadAllQuery raq = new ReadAllQuery(descriptor.getJavaClass());
        raq.setRedirector(redirector);
        descriptor.getQueryManager().setReadAllQuery(raq);

        // Delete Object
        deleteObjectQuery = descriptor.getQueryManager().getDeleteQuery();
        redirector = new MethodBaseQueryRedirector(RedirectorOnDescriptorTest.class, "deleteObject");
        DeleteObjectQuery doq = new DeleteObjectQuery();
        doq.setRedirector(redirector);
        descriptor.getQueryManager().setDeleteQuery(doq);

        // Insert Object
        insertQuery = descriptor.getQueryManager().getInsertQuery();
        redirector = new MethodBaseQueryRedirector(RedirectorOnDescriptorTest.class, "insertObject");
        InsertObjectQuery ioq = new InsertObjectQuery();
        ioq.setRedirector(redirector);
        descriptor.getQueryManager().setInsertQuery(ioq);

        // Update Object
        updateQuery = descriptor.getQueryManager().getUpdateQuery();
        redirector = new MethodBaseQueryRedirector(RedirectorOnDescriptorTest.class, "updateObject");
        UpdateObjectQuery uoq = new UpdateObjectQuery();
        uoq.setRedirector(redirector);
        descriptor.getQueryManager().setUpdateQuery(uoq);
    }

    public void test() {
        // test readAll
        getSession().readAllObjects(Employee.class);
        getSession().getIdentityMapAccessor().initializeIdentityMap(Employee.class);

        // test readObject
        Employee employee = 
            (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("id").equal(99));

        // test delete with an employee read from the database
        employee = (Employee)getSession().readObject(Employee.class);
        try {
            getAbstractSession().deleteObject(employee);
        } catch (DatabaseException exc) {
            // if we get an integrity exception here, the query went to the DB and was not redirected
            redirectedDeleteObject = false;
        }

        UnitOfWork uow = getSession().acquireUnitOfWork();
        // test update with an employee read from the database
        employee = (Employee)uow.readObject(Employee.class);
        employee.setFirstName(employee.getFirstName() + "-changed");

        // insert an employee to test the insert behavior
        employee = new Employee();
        employee.setFirstName("Paul");
        employee.setLastName("Sheldon");
        uow.registerObject(employee);

        uow.commit();

    }

    public void verify() {
        if (!redirectedReadObject) {
            throw new TestErrorException("ReadObjectQuery was not properly redirected when redirection was set on the Descriptor.");
        }
        if (!redirectedReadAll) {
            throw new TestErrorException("ReadAllQuery was not properly redirected when redirection was set on the Descriptor.");
        }
        if (!redirectedDeleteObject) {
            throw new TestErrorException("DeleteQuery was not properly redirected when redirection was set on the Descriptor.");
        }
        if (!redirectedUpdate) {
            throw new TestErrorException("UpdateObjectQuery was not properly redirected when redirection was set on the Descriptor.");
        }
        if (!redirectedInsert) {
            throw new TestErrorException("InsertObjectQuery was not properly redirected when redirection was set on the Descriptor.");
        }
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        descriptor.getQueryManager().setReadObjectQuery(readObjectQuery);
        descriptor.getQueryManager().setReadAllQuery(readAllQuery);
        descriptor.getQueryManager().setDeleteQuery(deleteObjectQuery);
        descriptor.getQueryManager().setInsertQuery(insertQuery);
        descriptor.getQueryManager().setUpdateQuery(updateQuery);
        boolean redirectedReadObject = false;
        boolean redirectedReadAll = false;
        boolean redirectedDeleteObject = false;
        boolean redirectedInsert = false;
        boolean redirectedUpdate = false;
    }

    /**
     * Below are the methods called by the redirectors for various toplink queries
     */
    public static

    Object deleteObject(DatabaseQuery query, Record row, org.eclipse.persistence.sessions.Session session) {
        redirectedDeleteObject = true;
        return null;
    }

    public static Object insertObject(DatabaseQuery query, Record row, org.eclipse.persistence.sessions.Session session) {
        redirectedInsert = true;
        return null;
    }

    public static Object readAll(DatabaseQuery query, Record row, org.eclipse.persistence.sessions.Session session) {
        redirectedReadAll = true;
        return null;
    }

    public static Object readObject(DatabaseQuery query, Record row, org.eclipse.persistence.sessions.Session session) {
        redirectedReadObject = true;
        return null;
    }

    public static Object updateObject(DatabaseQuery query, Record row, org.eclipse.persistence.sessions.Session session) {
        redirectedUpdate = true;
        return null;
    }
}
