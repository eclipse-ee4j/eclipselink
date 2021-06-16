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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.readonly.DefaultReadOnlyTestClass;

/**
 * This test ensures that default read-only classes are available on the client of a remote session.
 * @author Tom Ware
 */
public class DefaultReadOnlyClassTest extends TestCase {
    public DefaultReadOnlyClassTest() {
        setDescription("Tests whether default read only classes are available on the client of a remote session.");
    }

    public void verify() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        boolean readOnly = uow.isClassReadOnly(DefaultReadOnlyTestClass.class);
        uow.release();
        if (!readOnly) {
            throw new TestErrorException("A class was set as read-only on the server but was not read-only on the client.");
        }
    }
}
