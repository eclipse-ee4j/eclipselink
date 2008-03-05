package foo;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;

public class DirectAccessingEndToEndClient {
	private Project project;
	private DatabaseSession session;
	private String errorMessage;
	
	/**
	 * if there are any problems the error message will have a value
	 */
	public static void main(String[] args) {
		DirectAccessingEndToEndClient client = new DirectAccessingEndToEndClient();
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

	DirectAccessingEndToEndClient() {
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
		try { // test out creating objects
			Employee employee = new Employee();
			PhoneNumber homeNumber = new PhoneNumber();
			PhoneNumber workNumber = new PhoneNumber();
		} catch (Exception ex) {
			this.errorMessage = "Problems instantiating objects: " + ex;
			throw ex;
		}
	}	

	private void logout() throws Exception {	
		try {
			this.session.logout();
		} catch (Exception ex) {
			this.errorMessage = "Problems logging out of database: " + ex;
			throw ex;
		}
	}

}
