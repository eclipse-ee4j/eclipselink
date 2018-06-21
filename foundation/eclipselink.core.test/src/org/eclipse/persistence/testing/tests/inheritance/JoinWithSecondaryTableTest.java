/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.inheritance.Insect;

public class JoinWithSecondaryTableTest extends org.eclipse.persistence.testing.framework.TestCase {

    public JoinWithSecondaryTableTest() {
        setDescription("Performs a query on a joined inheritance superclass which has a joined attribute.  Bug6111278");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        try{
            getSession().readAllObjects(Insect.class);
        }catch (Exception exception){
            throw new TestErrorException("Query on joined inheritance class with join failed");
        }
    }

    public void verify() {
    }
}
