/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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
