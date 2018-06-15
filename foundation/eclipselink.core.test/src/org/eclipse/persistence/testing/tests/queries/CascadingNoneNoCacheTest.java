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
 * Check that the object itself is updated correctly from the db
 */
public class CascadingNoneNoCacheTest extends CascadingTest {

    public CascadingNoneNoCacheTest() {
    }

    protected void setTestConfiguration() {
        setConfiguration("CascadingNoneNoCacheTest", false, NO_CASCADE);
    }
}
