package org.eclipse.persistence.testing.jaxb.xmlmarshaller;
import java.io.Serializable;
import java.util.List;
public class UnmarshallerObject {
	    public String firstName;
	    public List<String> accountNumber;

	    public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public UnmarshallerObject() {
	    }

	    public List<String> getAccountNumber() {
	        return this.accountNumber;
	    }

	    public void setAccountNumber(List<String> accountNumber) {
	        this.accountNumber = accountNumber;
	    }

	  

}
