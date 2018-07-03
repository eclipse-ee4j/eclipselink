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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.xml.employee.Address;
import org.eclipse.persistence.tools.workbench.test.models.xml.employee.Dependent;

public class MWXmlDirectMappingTests
    extends ModelProblemsTestCase
{

    public static Test suite() {
        return new TestSuite(MWXmlDirectMappingTests.class);
    }

    public MWXmlDirectMappingTests(String name) {
        super(name);
    }

    public void testXmlFieldNotSpecifiedProblem() {
        String problem = ProblemConstants.XPATH_NOT_SPECIFIED;

        checkMappingsForFalseFailures(problem, MWXmlDirectMapping.class, getEmployeeOXProject());

        MWXmlDirectMapping mapping = (MWXmlDirectMapping) getEmployeeOXProject().descriptorNamed(Address.class.getName()).mappingNamed("city");
        String xpath = mapping.getXmlField().getXpath();
        mapping.getXmlField().setXpath("");

        assertTrue("should have problem: " + problem, hasProblem(problem, mapping));

        mapping.getXmlField().setXpath(xpath);
        checkMappingsForFalseFailures(problem, MWXmlDirectMapping.class, getEmployeeOXProject());
    }

    public void testXmlFieldNotDirectProblem() {
        String problem = ProblemConstants.XPATH_NOT_DIRECT;

        checkMappingsForFalseFailures(problem, MWXmlDirectMapping.class, getEmployeeOXProject());

        MWXmlDirectMapping mapping = (MWXmlDirectMapping) getEmployeeOXProject().descriptorNamed(Dependent.class.getName()).mappingNamed("firstName");
        String xpath = mapping.getXmlField().getXpath();
        mapping.getXmlField().setXpath("address");

        assertTrue("should have problem: " + problem, hasProblem(problem, mapping));

        mapping.getXmlField().setXpath(xpath);
        checkMappingsForFalseFailures(problem, MWXmlDirectMapping.class, getEmployeeOXProject());
    }

    public void testXmlFieldNotSingularProblem() {
        String problem = ProblemConstants.XPATH_NOT_SINGULAR;

        checkMappingsForFalseFailures(problem, MWXmlDirectMapping.class, getEmployeeOXProject());

        MWXmlDirectMapping mapping = (MWXmlDirectMapping) getEmployeeOXProject().descriptorNamed(Address.class.getName()).mappingNamed("street1");
        String xpath = mapping.getXmlField().getXpath();
        mapping.getXmlField().setXpath("street/text()");

        assertTrue("should have problem: " + problem, hasProblem(problem, mapping));

        mapping.getXmlField().setXpath(xpath);
        checkMappingsForFalseFailures(problem, MWXmlDirectMapping.class, getEmployeeOXProject());
    }
}
