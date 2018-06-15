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
 * Maintain Cache: on
 *
 * Test that all parts of a cached object are refreshed with data from the db
 * when a read is performed.
 */
public class CascadingAllCacheTest extends CascadingTest {

    public CascadingAllCacheTest() {
    }

    protected void setTestConfiguration() {
        setConfiguration("CascadingAllCacheTest", true, CASCADE_ALL);
    }
}
