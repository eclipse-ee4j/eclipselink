/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.validation;

import java.security.Permission;

//This is an assisting class for SecurityException tests.  This is a lazy yet efficient way to 
//trigger SecurityException since triggering the real SecurityException is too much involved.
public class TestSecurityManager extends SecurityManager {
    public TestSecurityManager() {
        super();
    }

    public void checkMemberAccess(Class clazz, int which) {
        String testString = clazz.getPackage().toString();
        if (!testString.startsWith("package org.eclipse.persistence.exceptions") && !testString.startsWith("package org.eclipse.persistence.internal.helper") && !testString.startsWith("package java.lang")) {
            if (testString.startsWith("package org.eclipse.persistence.testing.tests.validation")) {
                throw new SecurityException("Dummy SecurityException test");
            }

        }
        super.checkMemberAccess(clazz, which);
    }
    
    public void checkPermission(Permission perm) {
        // don't throw an error, so reset can reset security manager.
    }
}
