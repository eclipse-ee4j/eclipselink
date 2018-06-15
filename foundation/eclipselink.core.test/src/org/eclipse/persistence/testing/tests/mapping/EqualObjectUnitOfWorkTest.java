/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.mapping.Monitor;

public class EqualObjectUnitOfWorkTest extends AutoVerifyTestCase {
    public UnitOfWork unitOfWork;
    public Monitor monitor1;
    public Monitor monitor2;

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        this.monitor1 = new Monitor();
        this.monitor2 = new Monitor();
        this.monitor1.setBrand("Sony");
        this.monitor1.setSerialNumber("1");

        this.monitor2.setBrand("Sony");
        this.monitor2.setSerialNumber("2");

        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWork.registerObject(this.monitor1);
        this.unitOfWork.registerObject(this.monitor2);
        beginTransaction();
    }

    protected void test() {
        this.unitOfWork.commit();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Monitor monitor = (Monitor)getSession().readObject(monitor1);
        if (!monitor.getSerialNumber().equals("1")) {
            throw new TestErrorException("Equal objects were not inserted properly");
        }
        monitor = (Monitor)getSession().readObject(monitor2);
        if (!monitor.getSerialNumber().equals("2")) {
            throw new TestErrorException("Equal objects were not inserted properly");
        }
    }
}
