/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.inherited;

import org.eclipse.persistence.testing.framework.jpa.junit.CMP3TestModel;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;

/**
 * <p><b>Purpose</b>: To collect the tests that will test specifics of our
 * EJB3.0 implementation through the use of the EntityContainer.  In order for
 * this test model to work correctly the EntityContainer must be initialized
 * thought the comandline agent.
 */
public class CMP3InheritedTestModel extends CMP3TestModel {
    @Override
    public void setup(){
        super.setup();
           new InheritedTableManager().replaceTables(getServerSession());
    }

    @Override
    public void addTests() {
        addTest(new InheritedCRUDTest());
    }
}
