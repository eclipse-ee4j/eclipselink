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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.automap;

// JDK
import java.util.Hashtable;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;

final class ComplexProjectUnmappedVerifier extends AbstractAutomapVerifier
{
	private DescriptorInfo test_automap_Address()
	{
		Hashtable table = new Hashtable();
		table.put("city",                new DirectMappingInfo("CITY"));
		table.put("id",                  new DirectMappingInfo("ID"));
		table.put("state",               new DirectMappingInfo("STATE"));
		table.put("street",              new DirectMappingInfo("STREET"));
		table.put("zip",                 new DirectMappingInfo("ZIP"));
		table.put("customerCollection",  new OneToManyMappingInfo("test.automap.Customer", "CUSTOMER_FK21031184324046"));
		table.put("employeeCollection",  new OneToManyMappingInfo("test.automap.Employee", "EMPLOYEE_FK31047585434435"));

		return new TableDescriptorInfo(table, "AJAIN.ADDRESS");
	}

	private DescriptorInfo test_automap_Cabin()
	{
		Hashtable table = new Hashtable();
		table.put("bedCount",  new DirectMappingInfo("BED_COUNT"));
		table.put("deckLevel", new DirectMappingInfo("DECK_LEVEL"));
		table.put("id",        new DirectMappingInfo("ID"));
		table.put("name",      new DirectMappingInfo("NAME"));
		table.put("shipId",    new DirectMappingInfo("SHIP_ID"));

		return new TableDescriptorInfo(table, "AJAIN.CABIN");
	}

	private DescriptorInfo test_automap_CreditCard()
	{
		Hashtable table = new Hashtable();
		table.put("expirationDate", new DirectMappingInfo("EXP_DATE"));
		table.put("id",             new DirectMappingInfo("ID"));
		table.put("number",         new DirectMappingInfo("CARD_NUMBER"));
		table.put("organization",   new DirectMappingInfo("ORGANIZATION"));
		table.put("ownerName",      new DirectMappingInfo("NAME"));
		table.put("owner",          new OneToOneMappingInfo("test.automap.Customer", "CREDIT_CARD_FK21031184406624"));

		return new TableDescriptorInfo(table, "AJAIN.CREDIT_CARD");
	}

	private DescriptorInfo test_automap_Cruise()
	{
		Hashtable table = new Hashtable();
		table.put("id",                    new DirectMappingInfo("ID"));
		table.put("name",                  new DirectMappingInfo("NAME"));
		table.put("ship",                  new OneToOneMappingInfo ("test.automap.Ship",        "CRUISE_FK21031184617483"));
		table.put("reservationCollection", new OneToManyMappingInfo("test.automap.Reservation", "RESERVATION_FK21031184732827"));

		return new TableDescriptorInfo(table, "AJAIN.CRUISE");
	}

	private DescriptorInfo test_automap_Customer()
	{
		Hashtable table = new Hashtable();
		table.put("firstName",       new DirectMappingInfo("FIRST_NAME"));
		table.put("hasGoodCredit",   new DirectMappingInfo("HAS_GOOD_CREDIT"));
		table.put("id",              new DirectMappingInfo("ID"));
		table.put("lastName",        new DirectMappingInfo("LAST_NAME"));
		table.put("address",         new OneToOneMappingInfo  ("test.automap.Address",     "CUSTOMER_FK21031184324046"));
		table.put("phoneCollection", new OneToManyMappingInfo ("test.automap.Phone",       "PHONE_FK21031184531249"));
		table.put("reservations",    new ManyToManyMappingInfo("test.automap.Reservation", "AJAIN.RESERVATION_CUSTOMER_LINK", "RESERVATION_C_FK21031185109999", "RESERVATION_C_FK11031185032733"));

		return new TableDescriptorInfo(table, "AJAIN.CUSTOMER");
	}

	private DescriptorInfo test_automap_Detail()
	{
		Hashtable table = new Hashtable();
		table.put("id",   new DirectMappingInfo("ID"));
		table.put("mId",  new DirectMappingInfo("M_ID"));
		table.put("name", new DirectMappingInfo("NAME"));

		return new TableDescriptorInfo(table, "AJAIN.DETAIL");
	}

	private DescriptorInfo test_automap_Employee()
	{
		Hashtable table = new Hashtable();
		table.put("eDate",              new DirectMappingInfo("E_DATE"));
		table.put("empId",              new DirectMappingInfo("EMP_ID"));
		table.put("fName",              new DirectMappingInfo("F_NAME"));
		table.put("gender",             new DirectMappingInfo("GENDER"));
		table.put("lName",              new DirectMappingInfo("L_NAME"));
		table.put("sDate",              new DirectMappingInfo("S_DATE"));
		table.put("version",            new DirectMappingInfo("VERSION"));
		table.put("addr",               new OneToOneMappingInfo ("test.automap.Address",  "EMPLOYEE_FK31047585434435"));
		table.put("emp",                new OneToOneMappingInfo ("test.automap.Salary",   "EMP_SAL"));
		table.put("employeeCollection", new OneToManyMappingInfo("test.automap.Employee", "EMP_EMP"));
		table.put("manager",            new OneToOneMappingInfo ("test.automap.Employee", "EMP_EMP"));
		table.put("phoneCollection",    new OneToManyMappingInfo("test.automap.Phone",    "EMP_PHONE"));
		table.put("projectCollection",  new OneToManyMappingInfo("test.automap.Project",  "PROJECT_FK51047585645879"));
		table.put("projEmpCollection",  new OneToManyMappingInfo("test.automap.Project",  "PROJECT_FK51047585645879"));
		table.put("responsCollection",  new OneToManyMappingInfo("test.automap.Respons",  "RESPONS_EMPLOYEE"));

		return new TableDescriptorInfo(table, "AJAIN.EMPLOYEE");
	}

	private DescriptorInfo test_automap_Master()
	{
		Hashtable table = new Hashtable();
		table.put("detailId", new DirectMappingInfo("DETAIL_ID"));
		table.put("id",       new DirectMappingInfo("ID"));
		table.put("name",     new DirectMappingInfo("NAME"));

		return new TableDescriptorInfo(table, "AJAIN.MASTER");
	}

	private DescriptorInfo test_automap_Payment()
	{
		Hashtable table = new Hashtable();
		table.put("amount",        new DirectMappingInfo("AMOUNT"));
		table.put("checkBarCode",  new DirectMappingInfo("CHECK_BAR_CODE"));
		table.put("checkNumber",   new DirectMappingInfo("CHECK_NUMBER"));
		table.put("creditExpDate", new DirectMappingInfo("CREDIT_EXP_DATE"));
		table.put("creditNumber",  new DirectMappingInfo("CREDIT_NUMBER"));
		table.put("customerId",    new DirectMappingInfo("CUSTOMER_ID"));
		table.put("type",          new DirectMappingInfo("TYPE"));

		return new TableDescriptorInfo(table, "AJAIN.PAYMENT");
	}

	private DescriptorInfo test_automap_Phone()
	{
		Hashtable table = new Hashtable();
		table.put("id",          new DirectMappingInfo("ID"));
		table.put("phoneNumber", new DirectMappingInfo("PHONE_NUMBER"));
		table.put("type",        new DirectMappingInfo("TYPE"));
		table.put("customer",    new OneToOneMappingInfo("test.automap.Customer", "PHONE_FK21031184531249"));
		table.put("employee",    new OneToOneMappingInfo("test.automap.Employee", "EMP_PHONE"));

		return new TableDescriptorInfo(table, "AJAIN.PHONE");
	}

	private DescriptorInfo test_automap_Project()
	{
		Hashtable table = new Hashtable();
		table.put("descrip",           new DirectMappingInfo("DESCRIP"));
		table.put("projId",            new DirectMappingInfo("PROJ_ID"));
		table.put("projName",          new DirectMappingInfo("PROJ_NAME"));
		table.put("projType",          new DirectMappingInfo("PROJ_TYPE"));
		table.put("version",           new DirectMappingInfo("VERSION"));
		table.put("leader",            new OneToOneMappingInfo ("test.automap.Employee",        "PROJECT_FK51047585645879"));
		table.put("projEmpCollection", new OneToManyMappingInfo("test.automap.Project",          null)); // Will be wrong until the string matching uses words instead of a single string
//		table.put("projEmpCollection", new OneToManyMappingInfo("test.automap.ProjectEmployee", "FK_PROJ_EMP_PROJ_ID"));

		return new TableDescriptorInfo(table, "AJAIN.PROJECT");
	}

	private DescriptorInfo test_automap_ProjectEmployee()
	{
		Hashtable table = new Hashtable();
		table.put("employeeId", new DirectMappingInfo("EMP_ID"));
		table.put("projectId",  new DirectMappingInfo("PROJ_ID"));
		table.put("employee",   new OneToOneMappingInfo("test.automap.Employee", "FK_PROJ_EMP_EMP_ID"));
		table.put("project",    new OneToOneMappingInfo("test.automap.Project",  "FK_PROJ_EMP_PROJ_ID"));

		return new TableDescriptorInfo(table, "AJAIN.PROJ_EMP");
	}

	private DescriptorInfo test_automap_Reservation()
	{
		Hashtable table = new Hashtable();
		table.put("amountPaid",   new DirectMappingInfo("AMOUNT_PAID"));
		table.put("dateReserved", new DirectMappingInfo("DATE_RESERVED"));
		table.put("id",           new DirectMappingInfo("ID"));
		table.put("cruise",       new OneToOneMappingInfo  ("test.automap.Cruise",   "RESERVATION_FK21031184732827"));
		table.put("customers",    new ManyToManyMappingInfo("test.automap.Customer", "AJAIN.RESERVATION_CUSTOMER_LINK", "RESERVATION_C_FK11031185032733", "RESERVATION_C_FK21031185109999"));

		return new TableDescriptorInfo(table, "AJAIN.RESERVATION");
	}

	private DescriptorInfo test_automap_Respons()
	{
		Hashtable table = new Hashtable();
		table.put("description", new DirectMappingInfo("DESCRIPT"));
		table.put("employeeId",  new DirectMappingInfo("EMP_ID"));
		table.put("employee",    new OneToOneMappingInfo("test.automap.Employee", "RESPONS_EMPLOYEE"));

		return new TableDescriptorInfo(table, "AJAIN.RESPONS");
	}

	private DescriptorInfo test_automap_Salary()
	{
		Hashtable table = new Hashtable();
		table.put("empId",              new DirectMappingInfo("EMP_ID"));
		table.put("salary",             new DirectMappingInfo("SALARY"));
		table.put("employee",           new VariableOneToOneMappingInfo("test.automap.IEmployee", "EMP_ID"));
		table.put("employeeCollection", new OneToManyMappingInfo       ("test.automap.Employee",  "EMP_SAL"));

		return new TableDescriptorInfo(table, "AJAIN.SALARY");
	}

	private DescriptorInfo test_automap_Ship()
	{
		Hashtable table = new Hashtable();
		table.put("capacity",         new DirectMappingInfo("CAPACITY"));
		table.put("id",               new DirectMappingInfo("ID"));
		table.put("name",             new DirectMappingInfo("NAME"));
		table.put("tonnage",          new DirectMappingInfo("TONNAGE"));
		table.put("cruiseCollection", new OneToManyMappingInfo("test.automap.Cruise", "CRUISE_FK21031184617483"));

		return new TableDescriptorInfo(table, "AJAIN.SHIP");
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

		// test.automap.Cabin
		descriptor = project.descriptorNamed("test.automap.Cabin");
		descriptors.put(descriptor, test_automap_Cabin());

		// test.automap.CreditCard
		descriptor = project.descriptorNamed("test.automap.CreditCard");
		descriptors.put(descriptor, test_automap_CreditCard());

		// test.automap.Cruise
		descriptor = project.descriptorNamed("test.automap.Cruise");
		descriptors.put(descriptor, test_automap_Cruise());

		// test.automap.Customer
		descriptor = project.descriptorNamed("test.automap.Customer");
		descriptors.put(descriptor, test_automap_Customer());

		// test.automap.Detail
		descriptor = project.descriptorNamed("test.automap.Detail");
		descriptors.put(descriptor, test_automap_Detail());

		// test.automap.Employee
		descriptor = project.descriptorNamed("test.automap.Employee");
		descriptors.put(descriptor, test_automap_Employee());

		// test.automap.Master
		descriptor = project.descriptorNamed("test.automap.Master");
		descriptors.put(descriptor, test_automap_Master());

		// test.automap.Payment
		descriptor = project.descriptorNamed("test.automap.Payment");
		descriptors.put(descriptor, test_automap_Payment());

		// test.automap.Phone
		descriptor = project.descriptorNamed("test.automap.Phone");
		descriptors.put(descriptor, test_automap_Phone());

		// test.automap.Project
		descriptor = project.descriptorNamed("test.automap.Project");
		descriptors.put(descriptor, test_automap_Project());

		// test.automap.ProjectEmployee
		descriptor = project.descriptorNamed("test.automap.ProjectEmployee");
		descriptors.put(descriptor, test_automap_ProjectEmployee());

		// test.automap.Reservation
		descriptor = project.descriptorNamed("test.automap.Reservation");
		descriptors.put(descriptor, test_automap_Reservation());

		// test.automap.Respons
		descriptor = project.descriptorNamed("test.automap.Respons");
		descriptors.put(descriptor, test_automap_Respons());

		// test.automap.Salary
		descriptor = project.descriptorNamed("test.automap.Salary");
		descriptors.put(descriptor, test_automap_Salary());

		// test.automap.Ship
		descriptor = project.descriptorNamed("test.automap.Ship");
		descriptors.put(descriptor, test_automap_Ship());

		testDescriptors(descriptors);
	}
}
