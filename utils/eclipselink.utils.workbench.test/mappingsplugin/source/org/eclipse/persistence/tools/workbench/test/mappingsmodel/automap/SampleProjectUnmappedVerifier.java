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

// JDK
import java.util.Hashtable;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;

final class SampleProjectUnmappedVerifier extends AbstractAutomapVerifier
{
	private DescriptorInfo test_automap_Address()
	{
		Hashtable table = new Hashtable();
		table.put("id",                 new DirectMappingInfo("ID"));
		table.put("street",             new DirectMappingInfo("STREET"));
		table.put("city",               new DirectMappingInfo("CITY"));
		table.put("state",              new DirectMappingInfo("STATE"));
		table.put("zip",                new DirectMappingInfo("ZIP"));
		table.put("customerCollection", new OneToManyMappingInfo("test.automap.Customer", "CUSTOMER_FK21031184324046"));
		table.put("employeeCollection", new OneToManyMappingInfo("test.automap.Employee", "EMPLOYEE_FK31047585434435"));

		return new TableDescriptorInfo(table, "AJAIN.ADDRESS");
	}

	private DescriptorInfo test_automap_Customer()
	{
		Hashtable table = new Hashtable();
		table.put("id",            new DirectMappingInfo("ID"));
		table.put("lastName",      new DirectMappingInfo("LAST_NAME"));
		table.put("firstName",     new DirectMappingInfo("FIRST_NAME"));
		table.put("addressId",     new DirectMappingInfo("ADDRESS_ID"));
		table.put("hasGoodCredit", new DirectMappingInfo("HAS_GOOD_CREDIT"));
		table.put("address",       new OneToOneMappingInfo("test.automap.Address", "CUSTOMER_FK21031184324046"));

		return new TableDescriptorInfo(table, "AJAIN.CUSTOMER");
	}

	private DescriptorInfo test_automap_Employee()
	{
		Hashtable table = new Hashtable();
		table.put("empId",              new DirectMappingInfo("EMP_ID"));
		table.put("fName",              new DirectMappingInfo("F_NAME"));
		table.put("lName",              new DirectMappingInfo("L_NAME"));
		table.put("sDate",              new DirectMappingInfo("S_DATE"));
		table.put("eDate",              new DirectMappingInfo("E_DATE"));
		table.put("gender",             new DirectMappingInfo("GENDER"));
		table.put("version",            new DirectMappingInfo("VERSION"));
		table.put("manager",            new OneToOneMappingInfo ("test.automap.Employee", "EMP_EMP"));
		table.put("addr",               new OneToOneMappingInfo ("test.automap.Address",  "EMPLOYEE_FK31047585434435"));
		table.put("employeeCollection", new OneToManyMappingInfo("test.automap.Employee", "EMP_EMP"));
		table.put("projectCollection",  new OneToManyMappingInfo("test.automap.Project",  "PROJECT_FK51047585645879"));

		return new TableDescriptorInfo(table, "AJAIN.EMPLOYEE");
	}

	private DescriptorInfo test_automap_Project()
	{
		Hashtable table = new Hashtable();
		table.put("projId",   new DirectMappingInfo("PROJ_ID"));
		table.put("projType", new DirectMappingInfo("PROJ_TYPE"));
		table.put("projName", new DirectMappingInfo("PROJ_NAME"));
		table.put("descrip",  new DirectMappingInfo("DESCRIP"));
		table.put("version",  new DirectMappingInfo("VERSION"));
		table.put("leader",   new OneToOneMappingInfo("test.automap.Employee", "PROJECT_FK51047585645879"));

		return new TableDescriptorInfo(table, "AJAIN.PROJECT");
	}

	private DescriptorInfo test2_automap_Address()
	{
		Hashtable table = new Hashtable();
		table.put("id",                 new DirectMappingInfo("ID"));
		table.put("street",             new DirectMappingInfo("STREET"));
		table.put("city",               new DirectMappingInfo("CITY"));
		table.put("state",              new DirectMappingInfo("STATE"));
		table.put("zip",                new DirectMappingInfo("ZIP"));
		table.put("customerCollection", new OneToManyMappingInfo("test2.automap.Customer", "CUSTOMER_FK21031184324046"));

		return new TableDescriptorInfo(table, "AJAIN.ADDRESS");
	}

	private DescriptorInfo test2_automap_Customer()
	{
		Hashtable table = new Hashtable();
		table.put("id",            new DirectMappingInfo("ID"));
		table.put("lastName",      new DirectMappingInfo("LAST_NAME"));
		table.put("firstName",     new DirectMappingInfo("FIRST_NAME"));
		table.put("addressId",     new DirectMappingInfo("ADDRESS_ID"));
		table.put("hasGoodCredit", new DirectMappingInfo("HAS_GOOD_CREDIT"));
		table.put("address",       new OneToOneMappingInfo("test2.automap.Address", "CUSTOMER_FK21031184324046"));

		return new TableDescriptorInfo(table, "AJAIN.CUSTOMER");
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

		// test.automap.Address
		descriptor = project.descriptorNamed("test.automap.Address");
		descriptors.put(descriptor, test_automap_Address());

		// test.automap.Customer
		descriptor = project.descriptorNamed("test.automap.Customer");
		descriptors.put(descriptor, test_automap_Customer());

		// test.automap.Employee
		descriptor = project.descriptorNamed("test.automap.Employee");
		descriptors.put(descriptor, test_automap_Employee());

		// test.automap.Project
		descriptor = project.descriptorNamed("test.automap.Project");
		descriptors.put(descriptor, test_automap_Project());

		// test2.automap.Address
		descriptor = project.descriptorNamed("test2.automap.Address");
		descriptors.put(descriptor, test2_automap_Address());

		// test2.automap.Customer
		descriptor = project.descriptorNamed("test2.automap.Customer");
		descriptors.put(descriptor, test2_automap_Customer());

		testDescriptors(descriptors);
	}
}
