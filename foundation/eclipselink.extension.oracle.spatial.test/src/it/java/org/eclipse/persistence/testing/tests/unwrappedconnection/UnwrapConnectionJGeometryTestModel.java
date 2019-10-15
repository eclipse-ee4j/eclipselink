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
//     James Sutherland - Testing unwrapping
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import org.eclipse.persistence.sessions.factories.SessionManager;

public class UnwrapConnectionJGeometryTestModel extends UnwrapConnectionBaseTestModel {

    public UnwrapConnectionJGeometryTestModel() {
        setDescription("This model tests JGeometry type etc. using unwrapped connection.");
    }

    public void addTests() {
        addTest(new org.eclipse.persistence.testing.tests.spatial.jgeometry.SimpleJGeometryTestModel());
    }

    public void setup() {
        super.setup();

        // Must clear SessionManager as tests are JUnit tests.
        SessionManager.getManager().destroyAllSessions();
    }
}
