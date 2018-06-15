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

import org.eclipse.persistence.mappings.CollectionMapping;


/**
 * Test that the order by query keys are properly persisted to the deployment
 * java.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date March 11, 2005
 */
public class ProjectClassGeneratorOrderByQueryKeysTest extends ProjectClassGeneratorResultFileTest {
    public static final String ATTRIBUTE_NAME = "phoneNumbers";

    public ProjectClassGeneratorOrderByQueryKeysTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject());
    }

    /**
     * Setup what we want written out.
     */
    public void setup() {
        CollectionMapping mapping =
            (CollectionMapping)project.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getMappingForAttributeName(ATTRIBUTE_NAME);

        // Add the new order by query keys to the mapping
        mapping.addAscendingOrdering("ascending1");
        mapping.addDescendingOrdering("descending1");
        mapping.addAscendingOrdering("ascending2");
        mapping.addDescendingOrdering("descending2");
    }

    /**
     * We have 4 strings to verify
     */
    public void verify() {
        testString = "addAscendingOrdering(\"ascending1\")";
        super.verify();
        testString = "addDescendingOrdering(\"descending1\")";
        super.verify();
        testString = "addAscendingOrdering(\"ascending2\")";
        super.verify();
        testString = "addDescendingOrdering(\"descending2\")";
        super.verify();
    }
}
