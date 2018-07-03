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
package org.eclipse.persistence.tools.workbench.test.mappingsplugin;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.ant.EisProjectExporterTests;
import org.eclipse.persistence.tools.workbench.test.ant.EisProjectValidatorTests;
import org.eclipse.persistence.tools.workbench.test.ant.OXProjectExporterTests;
import org.eclipse.persistence.tools.workbench.test.ant.OXProjectValidatorTests;
import org.eclipse.persistence.tools.workbench.test.ant.ProjectExporterTests;
import org.eclipse.persistence.tools.workbench.test.ant.ProjectValidatorTests;
import org.eclipse.persistence.tools.workbench.test.mappings.ExportRuntimeProjectJavaSourceCommandLineTests;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


public class AllMappingsPluginTests {

    public static Test suite() {
        return suite(true);
    }

    public static Test suite(boolean all) {
        TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllMappingsPluginTests.class));

        suite.addTest(CodeDefinitionTests.suite());
        suite.addTest(ProblemsBundleTests.suite());
        suite.addTest(ExportRuntimeProjectJavaSourceCommandLineTests.suite());
        suite.addTest(ExportModelJavaSourceTests.suite());
        suite.addTest(DescriptorGenerationTests.suite());
        suite.addTest(EisProjectValidatorTests.suite());
        suite.addTest(EisProjectExporterTests.suite());
        suite.addTest(OXProjectValidatorTests.suite());
        suite.addTest(OXProjectExporterTests.suite());
        suite.addTest(ProjectValidatorTests.suite());
        suite.addTest(ProjectExporterTests.suite());

        return suite;
    }

    private AllMappingsPluginTests() {
        super();
        throw new UnsupportedOperationException();
    }

}
