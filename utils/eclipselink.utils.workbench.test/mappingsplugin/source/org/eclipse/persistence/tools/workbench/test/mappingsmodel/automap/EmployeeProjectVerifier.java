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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.automap;

import java.util.Hashtable;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;

public class EmployeeProjectVerifier extends AbstractAutomapVerifier
{
	private DescriptorInfo org_eclipse_persistence_tools_workbench_test_models_employee_Address()
	{
		Hashtable table = new Hashtable();

		table.put("city",       new DirectMappingInfo("CITY"));
		table.put("country",    new DirectMappingInfo("COUNTRY"));
		table.put("id",         new DirectMappingInfo("ADDRESS_ID"));
//		table.put("postalCode", new DirectMappingInfo("P_CODE"));
		table.put("postalCode", new NullMappingInfo());
		table.put("province",   new DirectMappingInfo("PROVINCE"));
		table.put("street",     new DirectMappingInfo("STREET"));

		return new TableDescriptorInfo(table, "ADDRESS");
	}

	private DescriptorInfo org_eclipse_persistence_tools_workbench_test_models_employee_Employee()
	{
		Hashtable table = new Hashtable();

		table.put("firstName",            new DirectMappingInfo("F_NAME"));
		table.put("gender",               new DirectMappingInfo("GENDER"));
//		table.put("id",                   new DirectMappingInfo("EMP_ID"));
		table.put("id",                   new NullMappingInfo());
		table.put("lastName",             new DirectMappingInfo("L_NAME"));
		table.put("normalHours",          new DirectMappingInfo(null));
		table.put("salary",               new DirectMappingInfo("SALARY", "SALARY"));
		table.put("address",              new OneToOneMappingInfo("org.eclipse.persistence.tools.workbench.test.models.employee.Address", "EMPLOYEE_ADDRESS"));
		table.put("manager",              new OneToOneMappingInfo("org.eclipse.persistence.tools.workbench.test.models.employee.Employee", "EMPLOYEE_EMPLOYEE"));
//		table.put("managedEmployees",     new OneToManyMappingInfo("org.eclipse.persistence.tools.workbench.test.models.employee.Employee", "EMPLOYEE_EMPLOYEE"));
		table.put("managedEmployees",     new DirectCollectionMappingInfo(null, null));
		table.put("phoneNumbers",         new OneToManyMappingInfo("org.eclipse.persistence.tools.workbench.test.models.employee.PhoneNumber", "PHONE_EMPLOYEE"));
		table.put("projects",             new ManyToManyMappingInfo("org.eclipse.persistence.tools.workbench.test.models.employee.Project", "PROJ_EMP", "PROJ_EMP_EMPLOYEE", "PROJ_EMP_PROJECT"));
		table.put("responsibilitiesList", new NullMappingInfo()); // TODO
		table.put("period",               new AggregateMappingInfo());
		table.put("emailAddressMap",      new NullMappingInfo());

		return new TableDescriptorInfo(table, new String[] { "EMPLOYEE", "SALARY" });
	}

	private DescriptorInfo org_eclipse_persistence_tools_workbench_test_models_employee_Project()
	{
		Hashtable table = new Hashtable();

		table.put("description", new DirectMappingInfo("DESCRIP"));
//		table.put("id",          new DirectMappingInfo("PROJ_ID"));
		table.put("id",          new NullMappingInfo());
//		table.put("name",        new DirectMappingInfo("PROJ_NAME"));
		table.put("name",        new NullMappingInfo());
		table.put("version",     new DirectMappingInfo("VERSION"));
		table.put("teamLeader",  new OneToOneMappingInfo("org.eclipse.persistence.tools.workbench.test.models.employee.Employee", "PROJECT_EMPLOYEE"));

		return new TableDescriptorInfo(table, "PROJECT");
	}

	private DescriptorInfo org_eclipse_persistence_tools_workbench_test_models_employee_LargeProject()
	{
		Hashtable table = new Hashtable();

		table.put("budget",           new DirectMappingInfo("BUDGET"));
//		table.put("budget",           new NullMappingInfo());
//		table.put("milestoneVersion", new DirectMappingInfo("MILESTONE"));
		table.put("milestoneVersion", new NullMappingInfo());

		return new TableDescriptorInfo(table, "LPROJECT");
//		return new TableDescriptorInfo(table, "RESPONS");	// LPROJECT matches SmallProject better
	}

	private DescriptorInfo org_eclipse_persistence_tools_workbench_test_models_employee_PhoneNumber()
	{
		Hashtable table = new Hashtable();

		table.put("areaCode", new DirectMappingInfo("AREA_CODE"));
		table.put("number",   new DirectMappingInfo("P_NUMBER"));
		table.put("type",     new DirectMappingInfo("TYPE"));
		table.put("owner",     new OneToOneMappingInfo("org.eclipse.persistence.tools.workbench.test.models.employee.Employee", "PHONE_EMPLOYEE"));

		return new TableDescriptorInfo(table, "PHONE");
	}

	private DescriptorInfo org_eclipse_persistence_tools_workbench_test_models_employee_EmploymentPeriod()
	{
		Hashtable table = new Hashtable();

//		table.put("endDate",   new DirectMappingInfo("CITY"));
		table.put("endDate",   new NullMappingInfo());
//		table.put("startDate", new DirectMappingInfo("COUNTRY"));
		table.put("startDate", new NullMappingInfo());

		return new AggregateDescriptorInfo(table);
	}

	/**
	 * Asks this <code>AutomapVerifier</code> to verify the execution of the
	 * automap on the state objects.
	 *
	 * @param project The root of the state object hierarchy
	 */
	public void verify(MWProject project)
	{
		MWDescriptor descriptor;
		Hashtable descriptors = new Hashtable();

		// test.oracle.models.employee.Address
		descriptor = project.descriptorNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Address");
		descriptors.put(descriptor, org_eclipse_persistence_tools_workbench_test_models_employee_Address());

		// test.oracle.models.employee.Employee
		descriptor = project.descriptorNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Employee");
		descriptors.put(descriptor, org_eclipse_persistence_tools_workbench_test_models_employee_Employee());

		// test.oracle.models.employee.EmploymentPeriod
		descriptor = project.descriptorNamed("org.eclipse.persistence.tools.workbench.test.models.employee.EmploymentPeriod");
		descriptors.put(descriptor, org_eclipse_persistence_tools_workbench_test_models_employee_EmploymentPeriod());

		// test.oracle.models.employee.LargeProject
		descriptor = project.descriptorNamed("org.eclipse.persistence.tools.workbench.test.models.employee.LargeProject");
		descriptors.put(descriptor, org_eclipse_persistence_tools_workbench_test_models_employee_LargeProject());

		// test.oracle.models.employee.PhoneNumber
		descriptor = project.descriptorNamed("org.eclipse.persistence.tools.workbench.test.models.employee.PhoneNumber");
		descriptors.put(descriptor, org_eclipse_persistence_tools_workbench_test_models_employee_PhoneNumber());

		// test.oracle.models.employee.Project
		descriptor = project.descriptorNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Project");
		descriptors.put(descriptor, org_eclipse_persistence_tools_workbench_test_models_employee_Project());

		testDescriptors(descriptors);
	}
}
