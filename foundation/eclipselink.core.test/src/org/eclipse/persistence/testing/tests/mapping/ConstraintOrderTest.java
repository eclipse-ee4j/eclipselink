/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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