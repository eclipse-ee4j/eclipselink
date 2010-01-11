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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.mapping.Computer;
import org.eclipse.persistence.testing.models.mapping.Monitor;

public class BidirectionalUOWInsertTest extends AutoVerifyTestCase {
    UnitOfWork unitOfWork;

    public BidirectionalUOWInsertTest() {
        setDescription("Test bidirectional insert in a unit of work.");
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        beginTransaction();
    }

    protected void test() {
        unitOfWork = getSession().acquireUnitOfWork();

        Computer computer = new Computer();
        Monitor monitor = Monitor.example1();
        computer.setDescription("IBM PS2");
        computer.notMacintosh();
        computer.setMonitor(monitor);
        computer.getMonitor().setComputer(computer);
        computer.setSerialNumber("1124345-1876212-2");

        unitOfWork.registerObject(computer);
        unitOfWork.commit();
    }

    protected void verify() {
        int size = ((UnitOfWorkImpl) unitOfWork).getCloneMapping().size();
        if (size != 2) {
            throw new TestErrorException("cloneMapping hashtable contains " + size + " elements, should contain 2.");
        }
    }
}
