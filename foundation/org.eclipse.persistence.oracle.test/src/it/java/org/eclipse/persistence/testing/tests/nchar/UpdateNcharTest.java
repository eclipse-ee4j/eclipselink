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
