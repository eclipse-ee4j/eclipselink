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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.xml;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;


public class MWXmlDescriptorTests extends ModelProblemsTestCase {

    public static Test suite() {
        return new TestSuite(MWXmlDescriptorTests.class);
    }

    public MWXmlDescriptorTests(String name) {
        super(name);
    }

    public void testSchemaContextSpecifiedProblem() {
        String errorName = ProblemConstants.DESCRIPTOR_NO_SCHEMA_CONTEXT_SPECIFIED;
        this.checkEisDescriptorsForFalseFailures(errorName);

        MWSchemaContextComponent schemaContext = getEmployeeEisDescriptor().getSchemaContext();

        this.getEmployeeEisDescriptor().setSchemaContext(null);
        this.assertTrue("null schema context -- should have problem", this.hasProblem(errorName, this.getEmployeeEisDescriptor()));

        this.getEmployeeEisDescriptor().setSchemaContext(schemaContext);
        this.assertTrue("schema context set -- should not have problem", ! this.hasProblem(errorName, this.getEmployeeEisDescriptor()));
    }

    public void testDefaultRootElementSpecifiedProblem() {
        String errorName = ProblemConstants.DESCRIPTOR_NO_DEFAULT_ROOT_ELEMENT_SPECIFIED;
        this.checkEisDescriptorsForFalseFailures(errorName);

        MWElementDeclaration defaultRootElement = getEmployeeEisDescriptor().getDefaultRootElement();

        this.getEmployeeEisDescriptor().setDefaultRootElement(null);
        this.assertTrue("default root element is null -- should have problem", this.hasProblem(errorName, this.getEmployeeEisDescriptor()));

        try {
            this.getEmployeeEisDescriptor().asCompositeEisDescriptor();
        } catch (InterfaceDescriptorCreationException e) {
            throw new RuntimeException(e);
        }

        this.assertTrue("descriptor is no longer root - should have no problem", ! this.hasProblem(errorName, this.getEmployeeEisDescriptor()));

        this.getEmployeeEisDescriptor().asRootEisDescriptor();
        this.getEmployeeEisDescriptor().setDefaultRootElement(defaultRootElement);
        this.assertTrue("default root element not null -- should have no problem", ! this.hasProblem(errorName, this.getEmployeeEisDescriptor()));
    }
}
