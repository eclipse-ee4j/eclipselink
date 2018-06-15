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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.ReturningPolicy;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;

public class ReturningPolicyWorkbenchIntegrationTest extends AutoVerifyTestCase {

    public static ReturningPolicyWorkbenchIntegrationTest projectXML() {
        return new ReturningPolicyWorkbenchIntegrationTest(PROJECT_XML);
    }

    public static ReturningPolicyWorkbenchIntegrationTest projectClassGenerated() {
        return new ReturningPolicyWorkbenchIntegrationTest(PROJECT_CLASS_GENERATED);
    }

    static int PROJECT_XML = 1;
    static int PROJECT_CLASS_GENERATED = 2;
    static String fileName = "ReturningPolicyMWIntegrationEmployeeProject";

    int mode;
    Project originalProject;
    Project project;

    ReturningPolicyWorkbenchIntegrationTest(int mode) {
        this.mode = mode;
        String strMode = null;
        if (mode == PROJECT_XML) {
            strMode = " XMLProj";
        } else if (mode == PROJECT_CLASS_GENERATED) {
            strMode = " ProjClassGen";
        }
        setName(getName() + strMode);
    }

    static class EmployeeProjectAmended extends EmployeeProject {
        public ClassDescriptor buildEmployeeDescriptor() {
            ClassDescriptor desc = super.buildEmployeeDescriptor();

            ReturningPolicy policy = new ReturningPolicy();
            desc.setReturningPolicy(policy);

            desc.setSequenceNumberFieldName(null);
            desc.setSequenceNumberName(null);
            policy.addFieldForInsertReturnOnly("EMPLOYEE.EMP_ID");

            policy.addFieldForInsert("EMPLOYEE.START_DATE");

            policy.addFieldForInsert("EMPLOYEE.END_DATE");
            policy.addFieldForUpdate("EMPLOYEE.END_DATE");

            policy.addFieldForInsert("EMPLOYEE.START_TIME", java.sql.Time.class);

            policy.addFieldForUpdate("EMPLOYEE.END_TIME", java.sql.Time.class);

            policy.addFieldForInsert("SALARY.SALARY");
            policy.addFieldForUpdate("SALARY.SALARY");

            return desc;
        }
    }

    protected void setup() throws Exception {
        originalProject = new EmployeeProjectAmended();
        if (mode == PROJECT_XML) {
            project = WorkbenchIntegrationSystemHelper.buildProjectXML(originalProject, fileName);
        } else if (mode == PROJECT_CLASS_GENERATED) {
            project = WorkbenchIntegrationSystemHelper.buildProjectClass(originalProject, fileName);
        } else {
            throw new TestWarningException("Invalid mode");
        }
    }

    protected void verify() {
        ReturningPolicy originalReturningPolicy =
            originalProject.getDescriptor(Employee.class).getReturningPolicy();
        ReturningPolicy returningPolicy = project.getDescriptor(Employee.class).getReturningPolicy();
        if (!originalReturningPolicy.hasEqualFieldInfos(returningPolicy)) {
            throw new TestErrorException("Returning policy changed");
        }
    }
}
