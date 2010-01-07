/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.utility.diff;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.diff.model.Address;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.Car;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.Dependent;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.Employee;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.InheritanceEmployee;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.PhoneNumber;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.SimpleEmployee;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.State;
import org.eclipse.persistence.tools.workbench.utility.diff.CompositeDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.IdentityDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDifferentiator;


public class InheritanceReflectiveDiffTests extends MultiClassReflectiveDiffTests {

	public static Test suite() {
		return new TestSuite(InheritanceReflectiveDiffTests.class);
	}
	
	public InheritanceReflectiveDiffTests(String name) {
		super(name);
	}

	protected Differentiator buildDifferentiator() {
		DiffEngine diffEngine = new DiffEngine();

		ReflectiveDifferentiator rd;
		rd = diffEngine.addReflectiveDifferentiator(SimpleEmployee.class);
		rd.addKeyFieldNamed("id");

		rd = diffEngine.addReflectiveDifferentiator(InheritanceEmployee.class);
		rd.addCollectionFieldNamed("dependents");
		rd.addListFieldNamed("cars");
		rd.addMapFieldNamed("phoneNumbers");
		rd.addReferenceCollectionFieldNamed("underlings");
		rd.addReferenceListFieldNamed("vacationBackups");
		rd.addReferenceMapFieldNamed("eatingPartners");

		diffEngine.addReflectiveDifferentiator(Address.class);

		rd = diffEngine.addReflectiveDifferentiator(Dependent.class);
		rd.addKeyFieldNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(Car.class);
		rd.addKeyFieldNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(PhoneNumber.class);
		rd.addKeyFieldNamed("areaCode");
		rd.addKeyFieldNamed("exchange");
		rd.addKeyFieldNamed("number");
		rd.addKeyFieldNamed("extension");

		diffEngine.setUserDifferentiator(State.class, IdentityDifferentiator.instance());

		return diffEngine;
	}

	protected Employee buildEmployee(int id, String name) {
		return new InheritanceEmployee(id, name);
	}

	protected ReflectiveDifferentiator employeeDifferentiator() {
		return (ReflectiveDifferentiator) ((DiffEngine) this.differentiator).getUserDifferentiator(SimpleEmployee.class);
	}

	public void testInheritanceMismatch() {
		this.employee2.setId(77);
		this.employee2.setName("Barney Rubble");
		Address address2 = this.employee2.getAddress();
		address2.setStreet("112 Boogie-Woogie Avenue");
		address2.setCity("Hollyrock");
		CompositeDiff diff = (CompositeDiff) this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(4, leafMismatches.size());

		// inheritance order, then alphabetical order...
		Diff leafDiff;

		leafDiff = (Diff) leafMismatches.get(0);		// id
		assertEquals(new Integer(1), leafDiff.getObject1());
		assertEquals(new Integer(77), leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(1);		// name
		assertEquals("Fred Flintstone", leafDiff.getObject1());
		assertEquals("Barney Rubble", leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(2);		// city
		assertEquals("Bedrock", leafDiff.getObject1());
		assertEquals("Hollyrock", leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(3);		// street
		assertEquals("201 Cobblestone Way", leafDiff.getObject1());
		assertEquals("112 Boogie-Woogie Avenue", leafDiff.getObject2());
	}

}
