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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

public class OptomizeValueHolderTest extends AutoVerifyTestCase {
    PhoneNumber phone;

    public OptomizeValueHolderTest() {
        setDescription("Tests to see that updates don't cause ValueHolders to instatiate");
    }

    public void setup() {
        beginTransaction();
    }

    public void reset() {
        rollbackTransaction();
    }

    public void test() {
        phone = (PhoneNumber)getSession().readObject(PhoneNumber.class);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getDatabaseSession().updateObject(phone);
    }

    public void verify() {
        if (phone.owner.isInstantiated()) {
            throw (new TestWarningException("The owner was instatiated."));
        }
    }
}
