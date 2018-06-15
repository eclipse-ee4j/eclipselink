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
package org.eclipse.persistence.testing.tests.nchar;

import org.eclipse.persistence.sessions.UnitOfWork;

public class UpdateNullNcharTest extends BaseNcharTest {

    public UpdateNullNcharTest() {
        setDescription("Tests updating by null NCHAR, NVARCHAR2, NCLOB in Oracle database.");
    }

    protected void test() {
        char ch = 'a';
        char nCh = '\u0410';
        object = new CharNchar(ch, nCh, 5, 15, 3 * getOracle9Platform().getLobValueLimits());

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(object);
        uow.commit();

        controlObject = new CharNchar();

        uow = getSession().acquireUnitOfWork();
        CharNchar objectClone = (CharNchar)uow.registerObject(object);
        objectClone.copyAllButId(controlObject);
        uow.commit();

        object = (CharNchar)getSession().refreshObject(object);
    }
}
