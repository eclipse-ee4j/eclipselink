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
package org.eclipse.persistence.tools.workbench.test.ant;

import java.io.File;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.ant.ProjectValidator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;

public class ProjectValidatorTests extends RelationalProjectRunnerTests {

    public static Test suite() {
        return new TestSuite( ProjectValidatorTests.class);
    }

    public ProjectValidatorTests( String name) {
        super( name);
    }
    /**
     * Verifies the Employee MWP project and displays the existing problems.
     */
    public void testProjectValidator() throws Exception {

        ProjectValidator validator = new ProjectValidator( this.log);

        File reportfile = new File( this.tempDir, "problem-report.html");
        String reportformat = "html";
        // Ignoring: 2 problem(s) ( 0233)
        // 0233 - LargeProject - Number of primary keys does not match the number of primary keys on the parent.
        // 0233 - SmallProject - Number of primary keys does not match the number of primary keys on the parent.
        Vector ignoreErrorCodes = new Vector();
        ignoreErrorCodes.add( "0233");

        int status = validator.execute(
                                        this.projectFileName,
                                        reportfile.getAbsolutePath(),
                                        reportformat,
                                        ignoreErrorCodes);
        assertEquals( status, 0);
    }

    protected MWProject buildProject() throws Exception {

        return new EmployeeProject().getProject();
    }
}
