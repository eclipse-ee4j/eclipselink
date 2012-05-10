/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.returning;

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.descriptors.ReturningPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.testing.framework.*;

public class DescriptorExceptionTest extends AutoVerifyTestCase {

    int errorCode;
    String more;
    DatabaseSession dbSession;
    DescriptorException exception;

    public DescriptorExceptionTest(int errorCode) {
        this.errorCode = errorCode;
        setName(getName() + " errorCode=" + Integer.toString(errorCode));
    }

    public DescriptorExceptionTest(int errorCode, String more) {
        this(errorCode);
        this.more = more;
        if (more != null) {
            setName(getName() + " " + more);
        }
    }

    protected void setup() {
        exception = null;
        Project project = getProject();
        project.setLogin((DatabaseLogin)(getSession().getLogin().clone()));
        dbSession = project.createDatabaseSession();
        dbSession.setSessionLog(getSession().getSessionLog());
        dbSession.setLogLevel(getSession().getLogLevel());
        dbSession.getIntegrityChecker().setShouldCatchExceptions(false);
    }

    protected Project getProject() {
        Project project = new EmployeeProject();
        ClassDescriptor desc;
        switch (errorCode) {
        case DescriptorException.RETURNING_POLICY_FIELD_TYPE_CONFLICT:
            desc = getDescriptor(project, Employee.class);
            desc.getReturningPolicy().addFieldForInsert("EMPLOYEE.L_NAME", String.class);
            desc.getReturningPolicy().addFieldForUpdate("EMPLOYEE.L_NAME", java.math.BigDecimal.class);
            break;
        case DescriptorException.RETURNING_POLICY_FIELD_INSERT_CONFLICT:
            desc = getDescriptor(project, Employee.class);
            desc.getReturningPolicy().addFieldForInsert("EMPLOYEE.L_NAME");
            desc.getReturningPolicy().addFieldForInsertReturnOnly("EMPLOYEE.L_NAME");
            break;
        case DescriptorException.RETURNING_POLICY_AND_DESCRIPTOR_FIELD_TYPE_CONFLICT:
            desc = getDescriptor(project, Employee.class);
            desc.getReturningPolicy().addFieldForInsert("EMPLOYEE.L_NAME", java.math.BigDecimal.class);
            break;
        case DescriptorException.RETURNING_POLICY_UNMAPPED_FIELD_TYPE_NOT_SET:
            desc = getDescriptor(project, Employee.class);
            // remove optimistic locking - or a different exception would be thrown
            desc.setOptimisticLockingPolicy(null);
            desc.getReturningPolicy().addFieldForInsert("EMPLOYEE.VERSION");
            break;
        case DescriptorException.RETURNING_POLICY_MAPPING_NOT_SUPPORTED:
            desc = getDescriptor(project, Employee.class);
            desc.getReturningPolicy().addFieldForInsert("EMPLOYEE.ADDR_ID");
            break;
        case DescriptorException.RETURNING_POLICY_FIELD_NOT_SUPPORTED:
            if (more == null || more.equals("sequence")) {
                desc = getDescriptor(project, Employee.class);
                desc.getReturningPolicy().addFieldForInsert("EMPLOYEE.EMP_ID");
            } else if (more.equals("locking")) {
                desc = getDescriptor(project, Employee.class);
                desc.getReturningPolicy().addFieldForInsert("EMPLOYEE.VERSION");
            } else if (more.equals("class")) {
                // the full name for the class provided to avoid picking up org.eclipse.persistence.sessions.Project
                desc = getDescriptor(project, org.eclipse.persistence.testing.models.employee.domain.Project.class);
                desc.getReturningPolicy().addFieldForInsert("PROJECT.PROJ_TYPE");
            } else {
                throw new TestProblemException("Unknown case specified for RETURNING_POLICY_FIELD_NOT_SUPPORTED");
            }
            break;
        case DescriptorException.CUSTOM_QUERY_AND_RETURNING_POLICY_CONFLICT:
            desc = getDescriptor(project, Employee.class);
            desc.getReturningPolicy().addFieldForInsert("EMPLOYEE.L_NAME");
            desc.getReturningPolicy().addFieldForInsert("SALARY.SALARY");

            InsertObjectQuery insertQuery = new InsertObjectQuery();
            StoredProcedureCall call = new StoredProcedureCall();
            call.setProcedureName("Insert_Employee");
            call.addNamedArgument("P_EMP_ID", "EMP_ID");
            call.addNamedArgument("P_SALARY", "SALARY");
            call.addNamedOutputArgument("L_NAME", "L_NAME", String.class);
            insertQuery.setCall(call);
            desc.getQueryManager().setInsertQuery(insertQuery);
            break;
        case DescriptorException.NO_CUSTOM_QUERY_FOR_RETURNING_POLICY:
            if (getSession().getPlatform().isOracle()) {
                throw new TestWarningException("Test for NO_CUSTOM_QUERY_FOR_RETURNING_POLICY doesn't work on Oracle");
            }
            desc = getDescriptor(project, Employee.class);
            desc.getReturningPolicy().addFieldForInsert("EMPLOYEE.L_NAME");
            break;
        }
        return project;
    }

    protected static ClassDescriptor getDescriptor(Project project, Class type) {
        ClassDescriptor desc = project.getDescriptor(type);
        if (!desc.hasReturningPolicy()) {
            desc.setReturningPolicy(new ReturningPolicy());
        }
        return desc;
    }

    protected void test() {
        try {
            dbSession.login();
            dbSession.logout();
        } catch (DescriptorException ex) {
            exception = ex;
        }
    }

    protected void verify() {
        if (exception == null) {
            throw new TestErrorException("No DescriptorException thrown");
        } else if (exception.getErrorCode() != errorCode) {
            throw new TestErrorException("Wrong DescriptorException thrown: " + exception.getErrorCode() + "; expected: " + errorCode);
        }
    }

}
