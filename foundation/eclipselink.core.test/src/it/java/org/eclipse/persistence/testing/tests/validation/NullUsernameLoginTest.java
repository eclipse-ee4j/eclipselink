/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * Test that invoking DatasourceLogin.setUserName(null) removes the 'user' property from
 * the DatasourceLogin's internal properties list, and DatasourceLogin.getUserName()
 * returns null.
 *
 * EclipseLink Bug 351374
 */
public class NullUsernameLoginTest extends TestCase {

    protected DatasourceLogin login;

    public void test() {
        login = getSession().getLogin().clone();
        login.setUserName(null);
    }

    public void verify() {
        assertNull("Login should return null for 'user'", login.getUserName());
        assertFalse("Login properties should not contain 'user'", login.getProperties().contains("user"));
    }

}
