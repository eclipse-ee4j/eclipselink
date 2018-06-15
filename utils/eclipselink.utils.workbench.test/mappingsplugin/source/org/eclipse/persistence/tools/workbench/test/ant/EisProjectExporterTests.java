/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.ant;

import java.io.File;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.ant.ProjectExporter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisLoginSpec;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeEisProject;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

public class EisProjectExporterTests extends XmlProjectRunnerTests {

    public static Test suite() {
        TestTools.setUpJUnitThreadContextClassLoader();
        return new TestSuite( EisProjectExporterTests.class);
    }

    public EisProjectExporterTests( String name) {
        super( name);
    }
    /**
     * Generates of a TopLink deployment descriptor XML
     */
    public void testProjectExporter() throws Exception {

        ProjectExporter exporter = new ProjectExporter( this.log);

        File deploymentFile = new File( this.tempDir, MW + "/EmployeeEisProject.xml");

        Vector ignoreErrorCodes = new Vector();

        int status = exporter.execute(
                                    this.projectFileName,                         // mwp fileName
                                    deploymentFile.getAbsolutePath(),    // deploymentFileName
                                    "",
                                    ignoreErrorCodes,
                                    new Boolean( true),     // failOnErrorObject
                                    "JMS.URL",
                                    MWEisLoginSpec.JMS_ADAPTER_NAME,
                                    "tle", "");
        assertEquals( status, 0);
    }

    protected MWProject buildProject() throws Exception {

        return new EmployeeEisProject().getProject();
    }

}
