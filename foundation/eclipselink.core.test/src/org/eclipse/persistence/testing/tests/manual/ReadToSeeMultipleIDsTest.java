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
package org.eclipse.persistence.testing.tests.manual;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.inheritance.Car;

public class ReadToSeeMultipleIDsTest extends ManualVerifyTestCase {
    public ReadToSeeMultipleIDsTest() {
        setDescription("Check select SQL and make sure that ID's are not printed multiple times.");
    }

    public void test() {
        getSession().readAllObjects(Car.class);
    }
}
