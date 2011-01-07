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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.sessioncache;

import org.eclipse.persistence.testing.framework.*;

public class SessionCacheTestSuite extends TestSuite {
    public SessionCacheTestSuite() {
        setDescription("This suite tests that we only merge into the session cache when necessary.");
    }

    public void addTests() {
        addTest(new ReadObjectAlreadyInSessionCacheTest());
        addTest(new ReadObjectNotInSessionCacheTest());
        addTest(new ReadReferencedObjectNotInSessionCacheTest());
        addTest(new WriteNewObjectTest());
        addTest(new WriteNewPessimisticLockedObjectTest());
    }
}
