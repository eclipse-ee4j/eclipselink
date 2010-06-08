/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.xdb;

import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.mappings.xdb.DirectToXMLTypeMapping;

public class Employee_XMLProject extends Project {
    public Employee_XMLProject() {
        super();
        initializeLogin();
        initializeProject();
        addDescriptor(new EmployeeDescriptor());
    }

    public Employee_XMLProject(DatabaseLogin databaseLogin) {
        super(databaseLogin);
        initializeLogin();
        addDescriptor(new EmployeeDescriptor());
    }

    public void initializeProject() {
    }

    private void initializeLogin() {
        DatabaseLogin login = new DatabaseLogin();
        login.setPlatform(new org.eclipse.persistence.platform.database.oracle.Oracle9Platform());
        login.setConnectionString("jdbc:oracle:oci8:@matt");
        login.setDriverClassName("oracle.jdbc.OracleDriver");
        login.setUserName("System");
        login.setPassword("qwerty");
        setLogin(login);
    }

    class EmployeeDescriptor extends RelationalDescriptor {
        public EmployeeDescriptor() {
            super();
            setJavaClass(Employee_XML.class);
            addPrimaryKeyFieldName("ID");
            addTableName("EMPLOYEE_XML");
            setSequenceNumberName("EMP_SEQ");
            setSequenceNumberFieldName("ID");

            DirectToFieldMapping idMapping = new DirectToFieldMapping();
            idMapping.setAttributeName("id");
            idMapping.setFieldName("ID");
            idMapping.setIsReadOnly(false);
            addMapping(idMapping);

            DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
            firstNameMapping.setAttributeName("firstName");
            firstNameMapping.setFieldName("FIRST_NAME");
            firstNameMapping.setIsReadOnly(false);
            addMapping(firstNameMapping);

            DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
            lastNameMapping.setAttributeName("lastName");
            lastNameMapping.setFieldName("LAST_NAME");
            lastNameMapping.setIsReadOnly(false);
            addMapping(lastNameMapping);

            DirectToFieldMapping genderMapping = new DirectToFieldMapping();
            genderMapping.setAttributeName("gender");
            genderMapping.setFieldName("GENDER");
            genderMapping.setIsReadOnly(false);
            addMapping(genderMapping);

            DirectToXMLTypeMapping resumeMapping = new DirectToXMLTypeMapping();
            resumeMapping.setAttributeName("resume");
            resumeMapping.setFieldName("RESUME_XML");
            resumeMapping.setShouldReadWholeDocument(true);
            resumeMapping.setIsReadOnly(false);
            addMapping(resumeMapping);

            DirectToXMLTypeMapping payrollMapping = new DirectToXMLTypeMapping();
            payrollMapping.setAttributeName("payroll_xml");
            payrollMapping.setFieldName("PAYROLL_XML");
            payrollMapping.setShouldReadWholeDocument(true);
            payrollMapping.setIsReadOnly(false);
            addMapping(payrollMapping);

            OneToOneMapping managerMapping = new OneToOneMapping();
            managerMapping.setAttributeName("manager");
            managerMapping.addForeignKeyFieldName("MANAGER_ID", "ID");
            managerMapping.setUsesIndirection(false);
            managerMapping.setIsReadOnly(false);
            managerMapping.setReferenceClass(Employee_XML.class);
            addMapping(managerMapping);

            OneToManyMapping managedEmployees = new OneToManyMapping();
            managedEmployees.setAttributeName("managedEmployees");
            managedEmployees.addTargetForeignKeyFieldName("MANAGER_ID", "ID");
            managedEmployees.setUsesIndirection(false);
            managedEmployees.setIsReadOnly(false);
            managedEmployees.setReferenceClass(Employee_XML.class);
            addMapping(managedEmployees);
        }
    }
}
