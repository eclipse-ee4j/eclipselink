/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - Testing unwrapping
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import org.eclipse.persistence.sessions.factories.SessionManager;

public class UnwrapConnectionJGeometryWrappedTestModel extends UnwrapConnectionBaseTestModel {
    
    public UnwrapConnectionJGeometryWrappedTestModel() {
        setDescription("This model tests JGeometry type etc. using unwrapped connection.");
    }
    
    public void addTests() {
        addTest(new org.eclipse.persistence.testing.tests.spatial.jgeometry.wrapped.WrappedJGeometryTestModel());
    }

    public void setup() {
        super.setup();
        
        // Must clear SessionManager as tests are JUnit tests.
        SessionManager.getManager().destroyAllSessions();
    }
}
