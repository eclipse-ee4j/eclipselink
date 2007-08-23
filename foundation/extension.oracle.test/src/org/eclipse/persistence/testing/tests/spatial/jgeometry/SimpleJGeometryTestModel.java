/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
