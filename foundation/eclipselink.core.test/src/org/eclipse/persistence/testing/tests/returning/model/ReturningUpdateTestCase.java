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
package org.eclipse.persistence.testing.tests.returning.model;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.*;

public class ReturningUpdateTestCase extends TestCase {

    Class1 originalObject;
    Class1 changedObject;
    Class1 workingObject;
    Class1 objectBeforeChange;
    boolean useUOW;
    ReturnObjectControl control;

    public ReturningUpdateTestCase(Class1 originalObject, Class1 changedObject, boolean useUOW, ReturnObjectControl control) {
        this.originalObject = originalObject;
        this.changedObject = changedObject;
        this.useUOW = useUOW;
        setName(getName() + " " + originalObject);
        setName(getName() + " changed=" + changedObject);
        if (useUOW) {
            setName(getName() + " useUOW");
        }
        this.control = control;
    }

    protected void setup() {
        workingObject = (Class1)originalObject.clone();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(workingObject);
        uow.commit();
        objectBeforeChange = (Class1)workingObject.clone();
    }

    protected void test() {
        if (useUOW) {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            Class1 cloneWorkingObject = (Class1)uow.registerObject(workingObject);
            cloneWorkingObject.updateWith(changedObject);
            uow.commit();
        } else {
            workingObject.updateWith(changedObject);
            getAbstractSession().writeObject(workingObject);
        }
    }

    protected void verify() {
        Class1 controlObject = (Class1)control.getObjectForUpdate(getSession(), objectBeforeChange, changedObject, useUOW);
        if (!workingObject.isValid()) {
            throw new TestErrorException("Object is invalid");
        }
        if (!controlObject.compareWithoutId(workingObject)) {
            throw new TestErrorException("Object is wrong");
        }
    }
}
