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
package org.eclipse.persistence.testing.tests.nchar;

import org.eclipse.persistence.sessions.UnitOfWork;

public class UpdateNcharTest extends BaseNcharTest {

    public UpdateNcharTest() {
        setDescription("Tests updating NCHAR, NVARCHAR2, NCLOB in Oracle database.");
    }

    protected void test() {
        char ch = 'a';
        char nCh = '\u0410';
        object = new CharNchar(ch, nCh, 5, 15, 12000);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(object);
        uow.commit();

        char chUpdated = 'b';
        char nChUpdated = '\u0401';
        controlObject = new CharNchar(chUpdated, nChUpdated, 10, 10, 8000);

        uow = getSession().acquireUnitOfWork();
        CharNchar objectClone = (CharNchar)uow.registerObject(object);
        objectClone.copyAllButId(controlObject);
        uow.commit();

        object = (CharNchar)getSession().refreshObject(object);
    }
}
