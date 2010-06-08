package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
public class ContainerInstanceVariableAccessorTestCases extends XMLMappingTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/containeraccessor/containeraccessor.xml";
	
	public ContainerInstanceVariableAccessorTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setProject(new EmployeeProject(false));
	}

	@Override
	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.id = 10;
		emp.firstName = "Jane";
		emp.lastName = "Doe";
		
		emp.address = new Address();
		emp.address.street = "123 Fake Street";
		emp.address.city = "Ottawa";
		emp.address.state = "Ontario";
		emp.address.country = "Canada";
		emp.address.owningEmployee = emp;
		
		emp.phoneNumbers = new ArrayList<PhoneNumber>();
		
		PhoneNumber num1 = new PhoneNumber();
		num1.number = "123-4567";
		num1.owningEmployee = emp;
		emp.phoneNumbers.add(num1);
		
		PhoneNumber num2 = new PhoneNumber();
		num2.number = "234-5678";
		num2.owningEmployee = emp;
		emp.phoneNumbers.add(num2);
		
		return emp;
	}

}