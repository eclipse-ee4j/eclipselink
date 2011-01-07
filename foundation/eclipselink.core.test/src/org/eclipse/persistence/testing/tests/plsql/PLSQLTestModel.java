/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James - initial impl
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.plsql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.plsql.Address;
import org.eclipse.persistence.testing.models.plsql.Employee;
import org.eclipse.persistence.testing.models.plsql.PLSQLSystem;
import org.eclipse.persistence.testing.models.plsql.Phone;

/**
 * This model tests calling PLSQL stored procedures with PLSQL types.
 */
public class PLSQLTestModel extends TestModel {
	public PLSQLTestModel() {
		setDescription("This model tests calling PLSQL stored procedures with PLSQL types.");
	}

	public void addRequiredSystems() {
		if (!getSession().getLogin().getPlatform().isOracle()) {
			warning("PLSQL is only supported on Oracle.");
		}

		addRequiredSystem(new PLSQLSystem());
	}

	public void addTests() {
		addTest(getSimpleTestSuite());
		addTest(getRecordTestSuite());
		addTest(getCollectionTestSuite());
		addTest(getErrorTestSuite());
	}


	public static TestSuite getErrorTestSuite() {
		TestSuite suite = new TestSuite();
		suite.setName("PLSQLErrorTestSuite");
		suite.setDescription("This suite tests calling PLSQL procedures with invalid arguments.");

		List args = new ArrayList();
		PLSQLTest test = new PLSQLTest("BadAddressOut", Address.class, args, QueryException.typeNameNotSet(null));
		test.setName("BadAddressOutTest");
		suite.addTest(test);

		args = new ArrayList();
		test = new PLSQLTest("MissingTypeAddressListOut", Address.class, args, QueryException.compatibleTypeNotSet(null));
		test.setName("MissingTypeAddressListOutTest");
		suite.addTest(test);

		Employee employee = new Employee();
		employee.setId(new BigDecimal(123));
		employee.setName("Bad Jones");

		args = new ArrayList();
		DatabaseRecord result = new DatabaseRecord();
		result.put("P_EMP", employee);
		result.put("P_CITY", "Nepean");
		args.add(employee);
		args.add("Nepean");
		test = new PLSQLTest("EmployeeInOutObject", Employee.class, args, result);
		test.setName("EmptyEmployeeInOutObjectTest");
		// PLSQL does not seem to allow empty collections.
		//suite.addTest(test);
		
		return suite;
	}
	
	public static TestSuite getSimpleTestSuite() {
		TestSuite suite = new TestSuite();
		suite.setName("PLSQLSimpleTestSuite");
		suite.setDescription("This suite tests calling PLSQL procedures that take simple types.");

		List args = new ArrayList();
		args.add("varchar");
		args.add(new Integer(1));
		args.add(new Integer(123));
		args.add(new BigDecimal("123.6"));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new BigDecimal("123.5"));
		PLSQLTest test = new PLSQLTest("SimpleIn", Address.class, args);
		test.setName("SimpleInTest");
		suite.addTest(test);

		args = new ArrayList();
		test = new PLSQLTest("SimpleOut", Address.class, args);
		test.setName("SimpleOutTest");
		suite.addTest(test);

		args = new ArrayList();
		args.add("varchar");
		args.add(new Integer(1));
		args.add(new Integer(123));
		args.add(new BigDecimal("123.6"));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new Integer(1));
		args.add(new BigDecimal("123.5"));
		test = new PLSQLTest("SimpleInOut", Address.class, args);
		test.setName("SimpleInOutTest");
		suite.addTest(test);

		return suite;
	}

	public static TestSuite getRecordTestSuite() {
		TestSuite suite = new TestSuite();
		suite.setName("PLSQLRecordTestSuite");
		suite.setDescription("This suite tests calling PLSQL procedures that take records.");

		Address address = new Address();
		address.setId(new BigDecimal(123));
		address.setNumber(17);
		address.setStreet("Bank");
		address.setCity("Ottawa");
		address.setState("ON");

		Address resultAddress = new Address();
		resultAddress.setId(new BigDecimal(1234));
		resultAddress.setNumber(17);
		resultAddress.setStreet("Bank");
		resultAddress.setCity("Ottawa");
		resultAddress.setState("ON");        
		
		Phone phone = new Phone();
		phone.setAreaCode("613");
		phone.setNumber("7927711");

		Employee employee = new Employee();
		employee.setId(new BigDecimal(123));
		employee.setName("Bob Jones");
		employee.setAddress(address);
		employee.getPhones().add(phone);

		List args = new ArrayList();
		args.add(address);
		DatabaseRecord result = new DatabaseRecord();
		result.put("P_ADDRESS", resultAddress);
		PLSQLTest test = new PLSQLTest("AddressInOutObject", Address.class, args, result);
		test.setName("AddressInOutObjectTest");
		suite.addTest(test);

		args = new ArrayList();
		args.add(address.getId());
		args.add(address.getNumber());
		args.add(address.getStreet());
		args.add(address.getCity());
		args.add(address.getState());
		args.add("Local");
		result = new DatabaseRecord();
		result.put("ADDRESS_ID", resultAddress.getId());
		result.put("ADDRESS_NUM", resultAddress.getNumber());
		result.put("STREET", resultAddress.getStreet());
		result.put("CITY", resultAddress.getCity());
		result.put("STATE", resultAddress.getState());
		result.put("P_LOCAL", "Nepean");
		test = new PLSQLTest("AddressInOutData", Address.class, args);
		test.setName("AddressInOutDataTest");
		suite.addTest(test);

		args = new ArrayList();
		args.add(address.getId());
		args.add(address.getNumber());
		args.add(address.getStreet());
		args.add(address.getCity());
		args.add(address.getState());
		args.add("Local");
		test = new PLSQLTest("AddressInData", Address.class, args);
		test.setName("AddressInDataTest");
		suite.addTest(test);

		args = new ArrayList();
		args.add(address);
		test = new PLSQLTest("AddressInObject", Address.class, args);
		test.setName("AddressInObjectTest");
		suite.addTest(test);

		args = new ArrayList();
		result = new DatabaseRecord();
		result.put("P_ADDRESS", resultAddress);
		test = new PLSQLTest("AddressOutObject", Address.class, args, result);
		test.setName("AddressOutTest");
		suite.addTest(test);

		args = new ArrayList();
		result = new DatabaseRecord();
		result.put("ADDRESS_ID", resultAddress.getId());
		result.put("STREET_NUM", resultAddress.getNumber());
		result.put("STREET", resultAddress.getStreet());
		result.put("CITY", resultAddress.getCity());
		result.put("STATE", resultAddress.getState());
		result.put("P_LOCAL", "Local");
		test = new PLSQLTest("AddressOutData", Address.class, args, result);
		test.setName("AddressOutDataTest");
		suite.addTest(test);

		args = new ArrayList();
		args.add(employee);
		args.add("Nepean");
		test = new PLSQLTest("EmployeeInObject", Employee.class, args);
		test.setName("EmployeeInObjectTest");
		suite.addTest(test);

		args = new ArrayList();
		result = new DatabaseRecord();
		result.put("P_EMP", new Employee());
		result.put("P_CITY", "Nepean");
		test = new PLSQLTest("EmployeeOutObject", Employee.class, args, result);
		test.setName("EmployeeOutTest");
		suite.addTest(test);

		args = new ArrayList();
		result = new DatabaseRecord();
		result.put("P_EMP", employee);
		result.put("P_CITY", "Nepean");
		args.add(employee);
		args.add("Nepean");
		test = new PLSQLTest("EmployeeInOutObject", Employee.class, args, result);
		test.setName("EmployeeInOutObjectTest");
		suite.addTest(test);

		return suite;
	}


	public static TestSuite getCollectionTestSuite() {
		TestSuite suite = new TestSuite();
		suite.setName("PLSQLCollectionTestSuite");
		suite.setDescription("This suite tests calling PLSQL procedures that take collections.");

		Address address = new Address();
		address.setId(new BigDecimal(123));
		address.setNumber(17);
		address.setStreet("Bank");
		address.setCity("Ottawa");
		address.setState("ON");
		
		Phone phone = new Phone();
		phone.setAreaCode("613");
		phone.setNumber("7927711");

		Employee employee = new Employee();
		employee.setId(new BigDecimal(123));
		employee.setName("Bob Jones");
		employee.setAddress(address);
		employee.getPhones().add(phone);

		List args = new ArrayList();
		DatabaseRecord result = new DatabaseRecord();
		List collection = new ArrayList();
		collection.add("Ottawa");
		result.put("P_CITY_LIST", collection);
		result.put("P_CITY", "Nepean");
		PLSQLTest test = new PLSQLTest("CityListOut", Address.class, args, result);
		test.setName("CityListOutTest");
		suite.addTest(test);

		args = new ArrayList();
		collection = new ArrayList(Arrays.asList(new String[]{"Ottawa", "Toronto"}));
		args.add(collection);
		args.add("Nepean");
		result = new DatabaseRecord();
		result.put("P_CITY_LIST", collection);
		result.put("P_CITY", "Nepean");
		test = new PLSQLTest("CityListInOut", Address.class, args, result);
		test.setName("CityListInOutTest");
		suite.addTest(test);

		args = new ArrayList();
		args.add(new Object[]{address, address});
		args.add("Nepean");
		test = new PLSQLTest("AddressListIn", Address.class, args);
		test.setName("AddressListInTest");
		suite.addTest(test);

		args = new ArrayList();
		result = new DatabaseRecord();
		result.put("P_ADDRESS_LIST", new ArrayList());
		result.put("P_CITY", "Nepean");
		test = new PLSQLTest("AddressListOut", Address.class, args, result);
		test.setName("AddressListOutTest");
		suite.addTest(test);

		args = new ArrayList();
		collection = new ArrayList(Arrays.asList(new Object[]{address, address}));
		args.add(collection);
		args.add("Nepean");
		result = new DatabaseRecord();
		result.put("P_ADDRESS_LIST", collection);
		result.put("P_CITY", "Nepean");
		test = new PLSQLTest("AddressListInOut", Address.class, args, result);
		test.setName("AddressListInOutTest");
		suite.addTest(test);

		args = new ArrayList();
		args.add(new Object[]{employee, employee});
		args.add("Nepean");
		test = new PLSQLTest("EmployeeListIn", Employee.class, args);
		test.setName("EmployeeListInTest");
		suite.addTest(test);

		args = new ArrayList();
		result = new DatabaseRecord();
		result.put("P_EMP_LIST", new ArrayList());
		result.put("P_CITY", "Nepean");
		test = new PLSQLTest("EmployeeListOut", Employee.class, args, result);
		test.setName("EmployeeListOutTest");
		suite.addTest(test);

		args = new ArrayList();
		collection = new ArrayList(Arrays.asList(new Object[]{employee, employee}));
		args.add(collection);
		args.add("Nepean");
		result = new DatabaseRecord();
		result.put("P_EMP_LIST", collection);
		result.put("P_CITY", "Nepean");
		test = new PLSQLTest("EmployeeListInOut", Employee.class, args, result);
		test.setName("EmployeeListInOutTest");
		suite.addTest(test);

		return suite;
	}
}
