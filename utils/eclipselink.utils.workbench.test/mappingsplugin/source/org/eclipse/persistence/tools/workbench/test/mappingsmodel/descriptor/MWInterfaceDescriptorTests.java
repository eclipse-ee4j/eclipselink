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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.SimpleContactProject;



public class MWInterfaceDescriptorTests extends ModelProblemsTestCase {
	
	public static Test suite() {
		return new TestSuite(MWInterfaceDescriptorTests.class);
	}
	
	public MWInterfaceDescriptorTests(String name) {
		super(name);
	}
	
	public void testAddImplementor() {
		MWRelationalProject contactProject = new SimpleContactProject().getProject();
		MWClass contactInterface = contactProject.typeFor(org.eclipse.persistence.tools.workbench.test.models.contact.Contact.class);
		MWClass personImplClass = contactProject.typeFor(org.eclipse.persistence.tools.workbench.test.models.contact.PersonImpl.class);
		
		MWInterfaceDescriptor interfaceDescriptor = (MWInterfaceDescriptor) contactProject.descriptorForType(contactInterface);
		MWTableDescriptor desc = (MWTableDescriptor) contactProject.descriptorForType(personImplClass);
		
		int numImplementorsBefore = interfaceDescriptor.implementorsSize();
		interfaceDescriptor.addImplementor(desc);
	
		assertTrue("implementor added",numImplementorsBefore < interfaceDescriptor.implementorsSize());
	
		numImplementorsBefore = interfaceDescriptor.implementorsSize();
		boolean exCaught = false;
		try {
			interfaceDescriptor.addImplementor(desc);
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().equals(desc.toString())) {
				exCaught = true;
			}
		}
		assertTrue("implementor added without thrown exception", exCaught);
		assertTrue("implementor added twice", numImplementorsBefore == interfaceDescriptor.implementorsSize());
	}
	
	public void testRemoveImplementor() {
		MWRelationalProject contactProject = new SimpleContactProject().getProject();
		MWClass contactInterface = contactProject.typeFor(org.eclipse.persistence.tools.workbench.test.models.contact.Contact.class);
		MWClass emailAddressClass = contactProject.typeFor(org.eclipse.persistence.tools.workbench.test.models.contact.EmailAddress.class);
		
		MWInterfaceDescriptor interfaceDescriptor = (MWInterfaceDescriptor) contactProject.descriptorForType(contactInterface);
		MWTableDescriptor desc = (MWTableDescriptor) contactProject.descriptorForType(emailAddressClass);
		int numImplementorsBefore = interfaceDescriptor.implementorsSize();
		interfaceDescriptor.removeImplementor(desc);
	
		assertTrue("implementor not removed",numImplementorsBefore > interfaceDescriptor.implementorsSize());
	}

	public void testImplementorDoesNotImplementInterfaceTest() {
		String problem = ProblemConstants.INTERFACE_DESCRIPTOR_IMPLEMENTOR_DOES_NOT_IMPLEMENT_INTERFACE;
		SimpleContactProject contactProject = new SimpleContactProject();
		MWInterfaceDescriptor desc = contactProject.getContactDescriptor();
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		
		MWDescriptor emailAddressDescriptor = contactProject.getEmailAddressDescriptor();
		emailAddressDescriptor.getMWClass().removeInterfaces(emailAddressDescriptor.getMWClass().interfaces());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));
	}

}
