/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.tests.distributedservers.rcm.jgroups.JGroupsDistributedServersModel;

public class ExtensionTestModel extends TestModel {

    public ExtensionTestModel() {
        setDescription("This model contains tests against org.eclipse.persistence.extension module.");
        addTest(new JGroupsDistributedServersModel());
    }

    /**
     * Return the JUnit suite to allow JUnit runner to find it. Unfortunately
     * JUnit only allows suite methods to be static, so it is not possible to
     * generically do this.
     */
    public static junit.framework.TestSuite suite() {
        return new ExtensionTestModel();
    }
}
