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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test does exist.
 */
public class DoesExistTest extends AutoVerifyTestCase {
    public DoesExistTest() {
        setDescription("This tests does exist on the session.");
    }

    public void test() {
        Object existing = getSession().readObject(Employee.class);
        boolean exists = getSession().doesObjectExist(existing);

        if (!exists) {
            throw new TestErrorException("Object does exist.");
        }
    }
}
