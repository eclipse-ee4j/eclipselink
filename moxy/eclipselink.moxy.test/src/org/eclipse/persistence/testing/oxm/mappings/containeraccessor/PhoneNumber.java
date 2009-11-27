package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

public class PhoneNumber {
	public String number;
	
	public Employee owningEmployee;
	
	public Employee getOwningEmployee() {
		return owningEmployee;
	}
	
	public void setOwningEmployee(Employee employee) {
		this.owningEmployee = employee;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof PhoneNumber)) {
			return false;
		}
		
		PhoneNumber obj = (PhoneNumber)o;
		return number.equals(obj.number) && owningEmployee.id == obj.owningEmployee.id;
		
	}
}