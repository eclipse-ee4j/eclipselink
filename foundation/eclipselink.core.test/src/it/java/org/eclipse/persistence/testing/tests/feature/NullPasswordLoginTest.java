/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.testing.framework.*;

/**
 * Insert the type's description here.
 * Creation date: (6/5/00 2:38:36 PM)
 * @author: Administrator
 */
public class NullPasswordLoginTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {

    /**
     * Insert the method's description here.
     * Creation date: (6/5/00 2:41:20 PM)
     */
    public NullPasswordLoginTest() {
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/5/00 2:39:56 PM)
     */
    public void reset() {
        if (!getSession().isConnected()) {
            ((DatabaseSession)getSession()).login();
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/5/00 2:39:56 PM)
     */
    public void setup() {
        if (!getSession().getDatasourceLogin().getPlatform().isAccess()) {//Need MSACCESS for this test (null password)
            throw new TestWarningException("This test requires MSAccess or another database that will accept a null password");
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/5/00 2:39:56 PM)
     */
    public void test() {
        ((DatabaseSession)getSession()).logout();
        Login new_login = getSession().getDatasourceLogin().clone();
        new_login.setPassword(null);
        ((DatabaseSession)getSession()).login(new_login);
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/5/00 2:39:56 PM)
     */
    public void verify() {
    }
}
