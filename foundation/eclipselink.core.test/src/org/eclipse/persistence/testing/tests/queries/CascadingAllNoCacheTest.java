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
package org.eclipse.persistence.testing.tests.queries;

/**
 * Test the following circumstance:
 *
 * Settings:
 * Maintain Cache: off
 *
 * Test that public parts of an object are refreshed with data from the db (not from the cache)
 * when a read is performed.
 */
public class CascadingAllNoCacheTest extends CascadingTest {

    public CascadingAllNoCacheTest() {
    }

    protected void setTestConfiguration() {
        setConfiguration("CascadingAllNoCacheTest", false, CASCADE_ALL);
    }
}
