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
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.inheritance.GrassHopper;

public class TranslatedKeyInheritanceTestCase extends org.eclipse.persistence.testing.framework.TestCase {

    /**
     * This method was created in VisualAge.
     */
    public void reset() {
        getAbstractSession().rollbackTransaction();
    }

    /**
     * This method was created in VisualAge.
     */
    protected void setup() {
        getAbstractSession().beginTransaction();

        // CREATE A GRASSHOPPER
        GrassHopper grassHopper = new GrassHopper();
        grassHopper.setIn_numberOfLegs(new Integer(6));
        grassHopper.setGh_maximumJump(new Integer(100));

        // ADD THE GRASSHOPPER TO THE DATABASE
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(grassHopper);
        uow.commit();
    }

    /**
     * This method was created in VisualAge.
     */
    public void test() {
        // READ A GRASSHOPPER
        GrassHopper grassHopper = (GrassHopper)getSession().readObject(GrassHopper.class);

        // MODIFY THE GRASSHOPPER
        UnitOfWork uow = getSession().acquireUnitOfWork();
        GrassHopper tempGrassHopper = (GrassHopper)uow.registerObject(grassHopper);
        tempGrassHopper.setGh_maximumJump(new Integer(150));
        uow.commit();

    }

    /**
     * This method was created in VisualAge.
     */
    public void verify() {
    }
}
