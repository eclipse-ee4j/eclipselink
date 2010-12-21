package foo;

import java.math.BigDecimal;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;

public class MethodAccessingEndToEndClient {
	private Project project;
	private DatabaseSession session;
	private String errorMessage;
	
	/**
	 * if there are any problems the error message will have a value
	 */
	public static void main(String[] args) {
		MethodAccessingEndToEndClient client = new MethodAccessingEndToEndClient();
		try {
			client.login();
			client.testCode();
			client.logout();
		} catch (Exception ex) {
			// any exceptions should be recorded in the error message...
		}
		if (client.errorMessage == null) {
			System.exit(0);
		} else {
			System.err.println(client.errorMessage);
			System.exit(1);
		}
	}
	
	MethodAccessingEndToEndClient() {
		super();
	}
	
	private void login() throws Exception {
		try {
			this.project = new EndToEndProject();
			this.session = this.project.createDatabaseSession();
			this.session.dontLogMessages();
//			this.session.logMessages();
			this.session.login();
		} catch (Exception ex) {
			this.errorMessage = "Problems logging in to database: " + ex;
			throw ex;
		}
	}
	
	private void testCode() throws Exception {	
		Employee employee;
		PhoneNumber homePhone;
		PhoneNumber workPhone;
		
		try {
			// create objects
			employee = new Employee();
			homePhone = new PhoneNumber();
			workPhone = new PhoneNumber();
		} catch (Exception ex) {
			this.errorMessage = "Problems instantiating objects: " + ex;
			throw ex;
		}
		
		UnitOfWork uow = this.session.acquireUnitOfWork();
		uow.registerNewObject(employee);
		uow.registerNewObject(homePhone);
		uow.registerNewObject(workPhone);
		
		
		// Test direct field accessors
		
		employee.setId(new BigDecimal(10));
		homePhone.setId(new BigDecimal(20));
		workPhone.setId(new BigDecimal(30));
		
		String employeeName = "George \"Dubya\" Bush";
		employee.setName(employeeName);
		
		String homeNumber = "1-800-555-HOME";
		homePhone.setNum(homeNumber);
		
		String workNumber = "1-800-555-WORK";
		workPhone.setNum(workNumber);
		
		if (employee.getName() != employeeName
			|| homePhone.getNum() != homeNumber
			|| workPhone.getNum() != workNumber)
		{
			this.errorMessage = "Direct field accessors did not work.";
			return;
		}	
		
		// Test value holder accessors
		
		homePhone.setEmp(employee);
		workPhone.setEmp(employee);
		
		if (homePhone.getEmp() != employee
			|| workPhone.getEmp() != employee)
		{
			this.errorMessage = "Value holder accessor did not work.";
			return;
		}
		
		homePhone.setEmp(null);
		workPhone.setEmp(null);
		
		
		// Test add collection accessors
		
		employee.addToPhoneNumberCollection(homePhone);
		employee.addToPhoneNumberCollection(workPhone);
		
		if (! employee.getPhoneNumberCollection().contains(homePhone)
			|| ! employee.getPhoneNumberCollection().contains(workPhone))
		{
			this.errorMessage = "Add accessors did not work.";
			return;
		}
		
		if (homePhone.getEmp() != employee
			|| workPhone.getEmp() != employee)
		{
			this.errorMessage = "Back-pointers did not work.";
			return;
		}
		
		
		// Test writing to database
		uow.commitAndResume();
		
		if (uow.readAllObjects(Employee.class).size() == 0
			|| uow.readAllObjects(PhoneNumber.class).size() == 0)
		{
			this.errorMessage = "Did not write objects to database.";
			return;
		}
		
		
		// Test remove collection accessors
		
		employee.removeFromPhoneNumberCollection(homePhone);
		employee.removeFromPhoneNumberCollection(workPhone);
		
		if (employee.getPhoneNumberCollection().contains(homePhone)
			|| employee.getPhoneNumberCollection().contains(workPhone))
		{
			this.errorMessage = "Remove accessors did not work.";
			return;
		}
		
		if (homePhone.getEmp() != null
			|| workPhone.getEmp() != null)
		{
			this.errorMessage = "Back-pointers did not work.";
			return;
		}
	}	

	private void logout() throws Exception {	
		try {
			Vector employees = this.session.readAllObjects(Employee.class);
			Vector phoneNumbers = this.session.readAllObjects(PhoneNumber.class);
			this.session.deleteAllObjects(phoneNumbers);
			this.session.deleteAllObjects(employees);
			this.session.logout();
		} catch (Exception ex) {
			this.errorMessage = "Problems logging out of database: " + ex;
			throw ex;
		}
	}

}
