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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWCompositeEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlPrimaryKeyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.eis.employee.Address;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeEisProject;

public class MWEisDescriptorTests extends ModelProblemsTestCase {

	public static Test suite() {
		return new TestSuite(MWEisDescriptorTests.class);
	}
	
	public MWEisDescriptorTests(String name) {
		super(name);
	}

	
	//TODO need to add inheritance to our eis project.  Then need to finish this test
	public void testDescriptorTypeInheritanceMismatchProblem() {
//		String problem = ProblemConstants.DESCRIPTOR_EIS_INHERITANCE_DESCRIPTOR_TYPES_DONT_MATCH;
//		EmployeeEisProject project = new EmployeeEisProject();
//		MWMappingDescriptor desc = project.getVehicleDescriptor();
//		
//		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
//		
//		project.getBicycleDescriptor().asMWAggregateDescriptor();
//		
//		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));
	}


	public void testPrimaryKeysSpecifiedProblem() {	
		String errorName = ProblemConstants.EIS_DESCRIPTOR_NO_PRIMARY_KEYS_SPECIFIED;
		
		checkEisDescriptorsForFalseFailures(errorName);
		
		MWRootEisDescriptor employeeDescriptor = (MWRootEisDescriptor) this.getEmployeeEisDescriptor();
		MWXmlPrimaryKeyPolicy pkPolicy = ((MWEisTransactionalPolicy) employeeDescriptor.getTransactionalPolicy()).getPrimaryKeyPolicy();
		Collection primaryKeyXpaths = new ArrayList();
		for (Iterator stream = pkPolicy.primaryKeyXpaths(); stream.hasNext(); ) {
			primaryKeyXpaths.add(stream.next());
		}
		
		pkPolicy.clearPrimaryKeys();
		
		assertTrue("no primary keys -- should have problem", this.hasProblem(errorName, employeeDescriptor));
		
		for (Iterator stream = primaryKeyXpaths.iterator(); stream.hasNext(); ) {
			pkPolicy.addPrimaryKey((String) stream.next());
		}
		
		assertTrue("primary keys added -- should have no problem", ! this.hasProblem(errorName, employeeDescriptor));
	}
	
	public void testIsRoot() throws Exception {
		MWEisProject project = (MWEisProject) new EmployeeEisProject().getProject();
		
		MWEisDescriptor addressDescriptor = (MWCompositeEisDescriptor) project.descriptorNamed(Address.class.getName());
		
		assertFalse("Address descriptor should not be a root", addressDescriptor.isRootDescriptor());
		
		addressDescriptor = addressDescriptor.asRootEisDescriptor();
		assertTrue("Address descriptor should be a root", addressDescriptor.isRootDescriptor());
		assertTrue("TransactionalPolicy should be an instance of MWEisTransactionalPolicy", 
				addressDescriptor.getTransactionalPolicy() instanceof MWEisTransactionalPolicy);

		try {
			addressDescriptor = addressDescriptor.asCompositeEisDescriptor();
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException(e);
		}

		assertFalse("Address descriptor should not be a root", addressDescriptor.isRootDescriptor());
		assertTrue("TransactionalPolicy should be an instance of MWNullTransactionalPolicy", 
				addressDescriptor.getTransactionalPolicy().getClass() == Class.forName("org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWNullTransactionalPolicy"));
	}

	public void testDescendantDescriptorTypeMismatchProblem() {	
		String errorName = ProblemConstants.DESCRIPTOR_EIS_INHERITANCE_DESCRIPTOR_TYPES_DONT_MATCH;
		
		MWEisDescriptor projectDescriptor = getProjectEisDescriptor();
		MWEisDescriptor lProjectDescriptor = getLargeProjectEisDescriptor();
		
		checkEisDescriptorsForFalseFailures(errorName);
		
		try {
			lProjectDescriptor = lProjectDescriptor.asCompositeEisDescriptor();
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException(e);
		}

		
		assertTrue("Large Projet is not root -- should have problem", this.hasProblem(errorName, projectDescriptor));
		
		lProjectDescriptor = lProjectDescriptor.asRootEisDescriptor();
		
		assertTrue("Large Project is root -- should have no problem", ! this.hasProblem(errorName, projectDescriptor));
	}

}
