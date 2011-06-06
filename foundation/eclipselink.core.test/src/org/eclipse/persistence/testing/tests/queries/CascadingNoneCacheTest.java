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
package org.eclipse.persistence.testing.tests.queries;

/**
 * Test the following circumstance:
 *
 * Settings:
 * Maintain Cache: on
 * Refresh: on
 *
 * Test that local data is refreshed when a read is performed (if it isn't, you've got real problems)
 */
public class CascadingNoneCacheTest extends CascadingTest {

    public CascadingNoneCacheTest() {
    }

    protected void setTestConfiguration() {
        setConfiguration("CascadingNoneCacheTest", true, NO_CASCADE);
    }
}
