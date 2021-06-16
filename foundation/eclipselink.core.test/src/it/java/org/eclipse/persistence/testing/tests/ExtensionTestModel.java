/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
