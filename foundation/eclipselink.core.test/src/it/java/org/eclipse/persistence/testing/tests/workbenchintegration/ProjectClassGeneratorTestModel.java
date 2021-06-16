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

import org.eclipse.persistence.testing.framework.TestSuite;

/**
 *  This model tests mapping workbench integration with the foundation library
 *  by writing, compiling and instantiating project classes and then running
 *  some operations on them.
 *  @author Edwin Tang
 */
public class ProjectClassGeneratorTestModel extends XMLProjectWriterTestModel {

    /**
     *  The constructor provides the test description.
     */
    public ProjectClassGeneratorTestModel() {
        setDescription("This model tests mapping workbench integration with the foundation library by writing, compiling and instantiating project classes and then running some operations on them.");
    }

    public void setup() {
    }

    public void reset() {
    }

    /**
     *  Add the Mapping Workbench Integration test system.
     */
    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeWorkbenchIntegrationSubSystem());
        addRequiredSystem(new InheritanceWorkbenchIntegrationSubSystem());
        addRequiredSystem(new AggregateWorkbenchIntegrationSubSystem());
        addRequiredSystem(new InterfaceWorkbenchIntegrationSubSystem());
        addRequiredSystem(new DirectMapMappingMWIntergrationSubSystem());
        addRequiredSystem(new CMWorkbenchIntegrationSubSystem());
        addRequiredSystem(new ProxyIndirectionMWIntegrationSubSystem());
        if(this.getSession().getPlatform().isOracle9()) {
            addRequiredSystem(new InsuranceORWorkbenchIntegrationSystem()); // Note: not a sub system
        }
        addRequiredSystem(new MappingModelWorkbenchIntegrationSubSystem());
        addRequiredSystem(new MultipleTableModelWorkbenchIntegrationSubSystem());
    }

    public void addTests() {
        super.addTests();

        TestSuite unicodeSuite = new TestSuite();
        unicodeSuite.setName("UnicodeSuite");
        unicodeSuite.setDescription("Tests to ensure unicode/non-unicode are generated as required.");
        unicodeSuite.addTest(new ProjectClassGeneratorUnicodeTest());
        addTest(unicodeSuite);

        addTest(new ProjectClassGeneratorOrderByQueryKeysTest());
        addTest(new ProjectClassGeneratorInheritanceMappingTest()); // Bug 298443
    }
}
