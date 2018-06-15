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
package org.eclipse.persistence.testing.tests.spatial.jgeometry;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.sessions.Session;

public class SimpleJGeometryTestModel extends TestModel {

    public static Session session = null;

    public SimpleJGeometryTestModel () {
        setDescription("Test CRUD operations on objects containing attributes of type JGeometry");
    }

    public void addTests() {
        if (getSession().getDatasourceLogin().getPlatform().isOracle()){
            SimpleSpatialTestCase.setIsJunit(false);
            session = getSession();
            addTest(SimpleJGeometryTestSuite.suite());
        }
    }

    public static Session getConfigSession(){
        return session;
    }

}
