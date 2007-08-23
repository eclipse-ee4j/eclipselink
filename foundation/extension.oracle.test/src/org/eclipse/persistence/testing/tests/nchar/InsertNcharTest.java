/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.nchar;

import org.eclipse.persistence.sessions.UnitOfWork;

public class InsertNcharTest extends BaseNcharTest {

    public InsertNcharTest() {
        setDescription("Tests inserting NCHAR, NVARCHAR2, NCLOB into Oracle database.");
    }

    protected void test() {
        char ch = 'a';
        char nCh = '\u0410';
        object = new CharNchar(ch, nCh, 5, 10, 8000);
        controlObject = new CharNchar(object);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(object);
        uow.commit();

        object = (CharNchar)getSession().refreshObject(object);
    }
}
