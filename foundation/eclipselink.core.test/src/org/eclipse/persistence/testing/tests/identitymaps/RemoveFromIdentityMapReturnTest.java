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
package org.eclipse.persistence.testing.tests.identitymaps;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Bug 3310420
 * Ensure the returned object is returned from IdentityMapAccessor.removeFromIdentiytMap.
 */
public class RemoveFromIdentityMapReturnTest extends TestCase {
    protected Employee employee = null;
    protected Employee returnedEmployee = null;

    public RemoveFromIdentityMapReturnTest() {
        setDescription("Ensure the returned object is returned from IdentityMapAccessor.removeFromIdentiytMap.");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        employee = (Employee)getSession().readObject(Employee.class);
    }

    public void test() {
        returnedEmployee = (Employee)getSession().getIdentityMapAccessor().removeFromIdentityMap(employee);
    }

    public void verify() {
        if (returnedEmployee != employee) {
            throw new TestErrorException("The incorrect employee was returned from removeFromIdentityMap.");
        }
        if (getSession().getIdentityMapAccessor().getFromIdentityMap(employee) != null) {
            throw new TestErrorException("Employee was not removed from the database in removeFromIdentityMap.");
        }
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
