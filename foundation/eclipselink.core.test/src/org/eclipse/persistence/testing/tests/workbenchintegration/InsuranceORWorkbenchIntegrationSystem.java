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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.testing.models.insurance.objectrelational.InsuranceProject;
import org.eclipse.persistence.testing.models.insurance.objectrelational.InsuranceORSystem;

/**
 *  This test system uses the Insurance test system to test the integration
 *  between the Mapping Workbench and the Foundation Library.  To do this, it
 *  writes our test project to an XML file and then reads the XML file and runs
 *  the employee tests on it.
 *  @author Tom Ware
 */
public class InsuranceORWorkbenchIntegrationSystem extends InsuranceORSystem {
    public static String PROJECT_FILE = "MWIntegrationTestInsuranceORProject";

    /**
     * Override the constructor for employee system to allow us to read and write XML
     */
    public InsuranceORWorkbenchIntegrationSystem() {
        super();
        buildProject();
    }

    protected void buildProject() {
        project = WorkbenchIntegrationSystemHelper.buildProjectXML(new InsuranceProject(), PROJECT_FILE);
    }
}
