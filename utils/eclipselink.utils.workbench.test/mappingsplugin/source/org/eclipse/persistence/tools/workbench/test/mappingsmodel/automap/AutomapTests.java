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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.automap;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;

public class AutomapTests extends TestCase
{
    public AutomapTests(String name)
    {
        super(name);
    }

    private void startTest(MWProject mwProject,
                                  AutomapVerifier verifier) throws Exception
    {
        new AutomapProject(mwProject).startTest(verifier);
    }

    public void testEmployeeProject() throws Exception
    {
        startTest(new EmployeeProject().getProject(),
                     new EmployeeProjectVerifier());
    }
}
