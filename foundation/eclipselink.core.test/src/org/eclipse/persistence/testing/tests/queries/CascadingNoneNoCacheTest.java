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
