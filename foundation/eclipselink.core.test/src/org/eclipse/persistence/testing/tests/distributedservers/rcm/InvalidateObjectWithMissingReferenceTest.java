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
package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Tests fix for bug:3604593
 * This test checks to see that when changing an object's reference that
 * removing a referenced object in the database will not cause a corruption
 * when the original changeset containing the reference change gets propogated.
 */
public class InvalidateObjectWithMissingReferenceTest extends ConfigurableCacheSyncDistributedTest {
    ReadObjectQuery query;
    Employee originalObject;
    Address originalAddress;
    Address newAddress;
    DistributedServer server;

    public InvalidateObjectWithMissingReferenceTest() {
        super();
        setName("InvalidateObjectWithMissingReferenceTest");
        cacheSyncConfigValues.put(Employee.class, new Integer(ClassDescriptor.SEND_OBJECT_CHANGES));
        cacheSyncConfigValues.put(Address.class, new Integer(ClassDescriptor.DO_NOT_SEND_CHANGES));
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        Enumeration enumtr = DistributedServersModel.getDistributedServers().elements();
        while (enumtr.hasMoreElements()) {
            (((DistributedServer)enumtr.nextElement()).getDistributedSession()).getIdentityMapAccessor().initializeAllIdentityMaps();
        }
        Enumeration keys = oldCacheSyncConfigValues.keys();
        while (keys.hasMoreElements()) {
            Class keyClass = (Class)keys.nextElement();
            ClassDescriptor descriptor = getSession().getDescriptor(keyClass);
            int newCacheSyncType = ((Integer)oldCacheSyncConfigValues.get(keyClass)).intValue();
            descriptor.setCacheSynchronizationType(newCacheSyncType);
        }
    }

    public void setup() {
        oldCacheSyncConfigValues = new Hashtable();
        Enumeration keys = cacheSyncConfigValues.keys();
        while (keys.hasMoreElements()) {
            Class keyClass = (Class)keys.nextElement();
            ClassDescriptor descriptor = getSession().getDescriptor(keyClass);

            if (descriptor != null) {
                int cacheSyncType = descriptor.getCacheSynchronizationType();
                Object newCacheSyncType = cacheSyncConfigValues.get(keyClass);
                if (newCacheSyncType != null) {
                    oldCacheSyncConfigValues.put(keyClass, new Integer(cacheSyncType));
                    descriptor.setCacheSynchronizationType(((Integer)newCacheSyncType).intValue());
                }
            }
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        this.originalObject = (Employee)getSession().readObject(Employee.class);
        this.originalAddress = this.originalObject.getAddress();
        this.query = new ReadObjectQuery();
        this.query.setSelectionObject(this.originalObject);
        this.server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        getEmployeeFromDistributedSession(this.query);
    }


    protected void test() {

        UnitOfWork uow = getSession().acquireUnitOfWork();

        Employee empClone = (Employee)uow.registerObject(this.originalObject);

        this.newAddress = new Address();
        this.newAddress.setCity("Ottawa");
        this.newAddress.setPostalCode("K5J2B5");
        this.newAddress.setProvince("ONT");
        this.newAddress.setStreet("should not exist when test completes");
        this.newAddress.setCountry("Canada");
        Address addressClone = (Address)uow.registerObject(this.newAddress);
        empClone.setAddress(addressClone);
        uow.assignSequenceNumber(addressClone);
        //uow.commit() is broken into components to allow DB change before cache synch propogates changes
        ((UnitOfWorkImpl)uow).issueSQLbeforeCompletion();
        getAbstractSession().insertObject(this.originalAddress);
        
        getAbstractSession().getSessionForClass(Employee.class).executeNonSelectingSQL("UPDATE EMPLOYEE SET ADDR_ID = " + this.originalAddress.getId() + " WHERE (EMP_ID = " + empClone.getId() + ")");

        getAbstractSession().deleteObject(addressClone);

        ((UnitOfWorkImpl)uow).mergeClonesAfterCompletion();
    }


    protected void verify() {
        Employee remoteEmployee = (Employee)getObjectFromDistributedCache(this.originalObject);
        if ((remoteEmployee != null) && (remoteEmployee.getAddress() == null)) {
            throw new TestErrorException("Employee's Address reference was corrupted during RCM synch" + " when Address was removed from the database. ");
        }
        remoteEmployee = getEmployeeFromDistributedSession(this.query);
        if (remoteEmployee.getAddress() == null) {
            throw new TestErrorException("Employee's Address reference was corrupted during RCM synch" + " when Address was removed from the database. ");
        }
        if (remoteEmployee.getAddress().getId() == newAddress.getId()) {
            throw new TestErrorException("New object Address was built/sent with the changeset in RCM " + "though its descriptor specifies DO_NOT_SEND_CHANGES");
        }

    }


    public Employee getEmployeeFromDistributedSession(DatabaseQuery query) {
        try {
            Employee result = (Employee)this.server.getDistributedSession().executeQuery(query);
            result.getAddress();
            return result;
        } catch (Exception exception) {
            return null;
        }
    }


}
