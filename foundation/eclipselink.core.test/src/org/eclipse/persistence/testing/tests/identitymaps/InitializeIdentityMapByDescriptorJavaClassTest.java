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
package org.eclipse.persistence.testing.tests.identitymaps;

import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Bug 3736313
 * Ensure that descriptors are accessed by their java class rather than the class they
 * are indexed by in the project in initializeIdentityMap
 */
public class InitializeIdentityMapByDescriptorJavaClassTest extends TestCase {
    protected boolean wrongMap = false;

    public InitializeIdentityMapByDescriptorJavaClassTest() {
        setDescription("Ensure that descriptors are accessed by their java class rather than the class they " + "are indexed by in the project in initializeIdentityMap.");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        // put an item in the employee identity map
        getSession().readObject(Employee.class);
        wrongMap = false;

        // index the employee descriptor by the BigDecimal class to simulate
        // what is done with EJBs
        ClassDescriptor descriptor = getSession().getClassDescriptor(Employee.class);
        getSession().getDescriptors().put(java.math.BigDecimal.class, descriptor);
    }

    public void test() {
        // initializing the identity map for the BigDecimal class should clean up
        // the employee identity map because of the way it was indexed in setup.
        getAbstractSession().getIdentityMapAccessorInstance().initializeIdentityMap(java.math.BigDecimal.class);
        IdentityMap employeeIdentityMap = getAbstractSession().getIdentityMapAccessorInstance().getIdentityMap(Employee.class);
        if (employeeIdentityMap.getSize() > 0) {
            wrongMap = true;
        }
    }

    public void verify() {
        if (wrongMap) {
            throw new TestErrorException("InitializeIdentityMap does not access identity maps by it's descriptor's java class.");
        }
    }

    public void reset() {
        getSession().getDescriptors().remove(java.math.BigDecimal.class);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
