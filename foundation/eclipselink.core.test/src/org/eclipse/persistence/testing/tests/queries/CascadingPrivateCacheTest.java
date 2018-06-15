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
 * Refresh: on
 *
 * Test that cached private object members are refreshed from the db on a read
 */
public class CascadingPrivateCacheTest extends CascadingTest {

    public CascadingPrivateCacheTest() {
    }

    protected void setTestConfiguration() {
        setConfiguration("CascadingPrivateCacheTest", true, CASCADE_PRIVATE);
    }
}
