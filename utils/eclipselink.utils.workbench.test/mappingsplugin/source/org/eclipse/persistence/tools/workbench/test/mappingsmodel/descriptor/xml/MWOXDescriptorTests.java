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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.xml;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;

public class MWOXDescriptorTests extends ModelProblemsTestCase {

    public static Test suite() {
        return new TestSuite(MWOXDescriptorTests.class);
    }

    public MWOXDescriptorTests(String name) {
        super(name);
    }

    public void testDefaultRootElementType() {
        String errorName = ProblemConstants.DESCRIPTOR_DEFAULT_ROOT_ELEMENT_TYPE;
        this.checkOXDescriptorsForFalseFailures(errorName);

        MWElementDeclaration defaultRootElement = getEmployeeEisDescriptor().getDefaultRootElement();
        MWXmlSchema empSchema = getEmployeeOXProject().getSchemaRepository().getSchema("employee.xsd");
        this.getEmployeeOXDescriptor().setDefaultRootElementType(empSchema.complexType("phone-type"));
        this.getEmployeeOXDescriptor().setDefaultRootElement(null);
        this.assertTrue("default root element is null -- should have problem", this.hasProblem(errorName, this.getEmployeeOXDescriptor()));

        this.getEmployeeOXDescriptor().setDefaultRootElement(defaultRootElement);
        this.assertTrue("default root element not null -- should have no problem", ! this.hasProblem(errorName, this.getEmployeeOXDescriptor()));

    }
}
