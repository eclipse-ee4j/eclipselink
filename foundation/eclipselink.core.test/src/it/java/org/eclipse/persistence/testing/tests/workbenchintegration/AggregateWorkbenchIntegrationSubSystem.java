/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration;

/**
 *  This test system uses the Aggregate test system to test the integration
 *  between the Mapping Workbench and the Foundation Library.  To do this, it
 *  writes our test project to a project class file and then compile and
 *  instantiate the project class and runs the tests on it.
 *  @author Edwin Tang
 */
public class AggregateWorkbenchIntegrationSubSystem extends AggregateWorkbenchIntegrationSystem {
    protected void buildProject() {
        project = WorkbenchIntegrationSystemHelper.buildProjectClass(project, PROJECT_FILE);
    }
}
