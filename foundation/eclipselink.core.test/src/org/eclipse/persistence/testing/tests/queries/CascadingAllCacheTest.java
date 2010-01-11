/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
