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

public class ReturningInsertTestCase extends TestCase {

    Class1 originalObject;
    Class1 workingObject;
    boolean useUOW;
    ReturnObjectControl control;

    public ReturningInsertTestCase(Class1 originalObject, boolean useUOW, ReturnObjectControl control) {
        this.originalObject = originalObject;
        this.useUOW = useUOW;
        setName(getName() + " " + originalObject);
        if (useUOW) {
            setName(getName() + " useUOW");
        }
        this.control = control;
    }

    protected void setup() {
        workingObject = (Class1)originalObject.clone();
    }

    protected void test() {
        if (useUOW) {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.registerObject(workingObject);
            uow.commit();
        } else {
            getAbstractSession().writeObject(workingObject);
        }
    }

    protected void verify() {
        Class1 controlObject = (Class1)control.getObjectForInsert(getSession(), originalObject);
        if (!workingObject.isValid()) {
            throw new TestErrorException("Object is invalid");
        }
        if (!controlObject.compareWithoutId(workingObject)) {
            throw new TestErrorException("Object is wrong");
        }
    }
}
