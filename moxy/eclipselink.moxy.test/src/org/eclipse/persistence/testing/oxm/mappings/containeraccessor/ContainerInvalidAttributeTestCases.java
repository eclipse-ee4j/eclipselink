package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class ContainerInvalidAttributeTestCases extends OXTestCase {
	public ContainerInvalidAttributeTestCases(String name) {
		super(name);
	}
	
	public void testCreateContextAttributeAccess() {
		try {
			getXMLContext(new EmployeeInvalidContainerAttributeProject(false));
		} catch(IntegrityException ex) {
			ex.printStackTrace();
			assertTrue("incorrect number of errors", ex.getIntegrityChecker().getCaughtExceptions().size() == 1);
			DescriptorException caughtException = (DescriptorException)ex.getIntegrityChecker().getCaughtExceptions().elementAt(0);
			assertTrue(caughtException.getErrorCode() == 59);
		}
	}
	
	public void testCreateContextMethodAccess() {
		try {
			getXMLContext(new EmployeeInvalidContainerAttributeProject(true));
		} catch(IntegrityException ex) {
			ex.printStackTrace();
			assertTrue("incorrect number of errors", ex.getIntegrityChecker().getCaughtExceptions().size() == 1);
			DescriptorException caughtException = (DescriptorException)ex.getIntegrityChecker().getCaughtExceptions().elementAt(0);
			assertTrue(caughtException.getErrorCode() == 60);
		}
	}
}
