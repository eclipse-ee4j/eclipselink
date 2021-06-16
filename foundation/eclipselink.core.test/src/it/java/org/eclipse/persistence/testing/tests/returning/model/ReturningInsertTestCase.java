/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
