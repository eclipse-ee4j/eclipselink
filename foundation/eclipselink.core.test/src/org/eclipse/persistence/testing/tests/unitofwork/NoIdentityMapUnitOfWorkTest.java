/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.UnitOfWork;


public class NoIdentityMapUnitOfWorkTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public UnitOfWork uow;
    public Object originalObject;
    public Object objectToBeWritten;
    protected String originalString;

    public NoIdentityMapUnitOfWorkTest() {
        super();
        setDescription("Tests an update of an object with no identity map in place.");
    }

    public void setup() {
        uow = getSession().acquireUnitOfWork();

        if (originalObject == null) {
            //just read the first one out of the database
            objectToBeWritten = getSession().readObject(Weather.class);
        } else {
            objectToBeWritten = getSession().readObject(originalObject);
        }
        objectToBeWritten = uow.registerObject(objectToBeWritten);

        originalString = ((Weather)objectToBeWritten).getStormPattern();
        ((Weather)objectToBeWritten).setStormPattern(originalString + " and windy");
    }

    public void test() {
        uow.commit();
    }

    public void reset() {
        uow = getSession().acquireUnitOfWork();
        objectToBeWritten = uow.readObject(objectToBeWritten);
        ((Weather)objectToBeWritten).setStormPattern(originalString);
        uow.commit();
    }
}
