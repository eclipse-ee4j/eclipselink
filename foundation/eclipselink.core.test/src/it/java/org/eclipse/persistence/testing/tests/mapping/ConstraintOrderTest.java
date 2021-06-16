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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.mapping.Joystick;
import org.eclipse.persistence.testing.models.mapping.Keyboard;

public class ConstraintOrderTest extends TransactionalTestCase {
    protected Keyboard objectToBeWritten;

    public ConstraintOrderTest() {
        setDescription("Test constraints are maintained correctly for 1-m and user defined constraints.");
    }

    public void test() {
        this.objectToBeWritten = Keyboard.example1();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerNewObject(this.objectToBeWritten);
        uow.registerObject(Keyboard.example2());
        uow.registerObject(new Joystick("Logitech", this.objectToBeWritten));
        uow.registerObject(new Joystick("Intel", Keyboard.example2()));
        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Object objectFromDatabase = getSession().readObject(this.objectToBeWritten);

        if (!(compareObjects(this.objectToBeWritten, objectFromDatabase))) {
            throw new TestErrorException("The object inserted into the database, '" + objectFromDatabase + "' does not match the original, '" + this.objectToBeWritten + ".");
        }
    }
}
