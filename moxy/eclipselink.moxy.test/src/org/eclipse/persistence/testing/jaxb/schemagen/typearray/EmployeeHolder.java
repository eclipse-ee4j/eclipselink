package org.eclipse.persistence.testing.jaxb.schemagen.typearray;

import java.util.List;

import org.eclipse.persistence.testing.jaxb.schemagen.deploymentxml.Employee;

public class EmployeeHolder {
    public static final String EMPLOYEES_FIELD_NAME = "employees";
    List<Employee> employees;
}
