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
 *     GYORKE - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * 
 */
public class DoNotRedirectDefaultRedirectorTest extends TestCase {

    protected ClassDescriptor descriptor = null;

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        descriptor = getSession().getProject().getDescriptors().get(Employee.class);

        descriptor.setDefaultReadObjectQueryRedirector(new MethodBaseQueryRedirector(DoNotRedirectDefaultRedirectorTest.class, "readObject"));
    }

    public void test() {
        // test readAll
        ReadObjectQuery roq = new ReadObjectQuery(Employee.class);
        roq.setSelectionId(99);
        roq.setDoNotRedirect(true);
        try {
            Employee employee = (Employee)getSession().executeQuery(roq);
        } catch (Exception exc) {
            throw new TestErrorException("DoNotRedirect was ignored");
        }

    }

    public void verify() {
    }

    public void reset() {
        descriptor.setDefaultReadObjectQueryRedirector(null);
    }

    /**
     * Below are the methods called by the redirectors for various toplink queries
     */

    public static Object readObject(DatabaseQuery query, Record row, org.eclipse.persistence.sessions.Session session) {
        throw new TestErrorException("Query setting doNotRedirect was ignored");
    }

}
