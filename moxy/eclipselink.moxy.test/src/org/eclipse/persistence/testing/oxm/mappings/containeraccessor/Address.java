package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

public class Address {
	public String street;
	public String city;
	public String state;
	public String country;
	
	public Employee owningEmployee;
	
	public Employee getOwningEmployee() {
		return owningEmployee;
	}
	
	public void setOwningEmployee(Employee employee) {
		owningEmployee = employee;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Address)) {
			return false;
		}
		Address obj = (Address)o;
		boolean equal = true;
		
		equal = equal && street.equals(obj.street);
		equal = equal && city.equals(obj.city);
		equal = equal && state.equals(obj.state);
		equal = equal && country.equals(obj.country);
		
		equal = equal && owningEmployee.id == obj.owningEmployee.id;
		
		return equal;
	}
}