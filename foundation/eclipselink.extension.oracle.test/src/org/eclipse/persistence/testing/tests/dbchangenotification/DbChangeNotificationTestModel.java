/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.dbchangenotification;

import org.eclipse.persistence.testing.framework.TestModel;

/**
 * Test Database change notification using JMS on top of Oracle AQ.
 */
public class DbChangeNotificationTestModel extends TestModel {
    public DbChangeNotificationTestModel() {
        super();
        addTest(new DbChangeNotificationInternalTestModel(false));
        addTest(new DbChangeNotificationInternalTestModel(true));
    }
}
