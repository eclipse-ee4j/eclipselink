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
package org.eclipse.persistence.testing.tests.queries;

/**
 * Test the following circumstance:
 *
 * Settings:
 * Maintain Cache: off
 *
 * Test that private parts of an object are refreshed with data from the db (not from the cache)
 * when a read is performed.
 */
public class CascadingPrivateNoCacheTest extends CascadingTest {

    public CascadingPrivateNoCacheTest() {
    }

    protected void setTestConfiguration() {
        setConfiguration("CascadingPrivateNoCacheTest", false, CASCADE_PRIVATE);
    }
}
