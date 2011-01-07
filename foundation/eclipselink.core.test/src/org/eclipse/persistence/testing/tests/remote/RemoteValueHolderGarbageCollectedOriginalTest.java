/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

import org.eclipse.persistence.sessions.*;

public class RemoteValueHolderGarbageCollectedOriginalTest extends TestCase {
    protected Employee originalEmp;
    protected Session originalSession;

    public RemoteValueHolderGarbageCollectedOriginalTest(Session originalSession) {
        this.originalSession = originalSession;
        setDescription("Tests committing changes in the UnitOfWork when the originals have been Garbage Collected from the Server");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
        originalEmp = (Employee)getSession().readObject(Employee.class);
        //Trigger the indirection on the CLient
        originalEmp.getAddress();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employeeClone = (Employee)uow.registerObject(originalEmp);
        this.originalSession.getIdentityMapAccessor().initializeAllIdentityMaps();
        employeeClone.setLastName("Something other than what it was");
        try {
            uow.commit();
            employeeClone = (Employee)getSession().readObject(originalEmp);
            employeeClone.getAddress();
        } catch (Exception exception) {
            throw new TestErrorException("Test failed.  The RemoteValueHolder did not merge correctly when Original had been garbage collected.  Exception:" + 
                                         exception.toString());
        }
    }
}
