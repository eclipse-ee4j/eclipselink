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
package org.eclipse.persistence.tools.workbench.test.utility.diff;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.Employee;
import org.eclipse.persistence.tools.workbench.utility.diff.CompositeDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.EqualityDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.IdentityDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.NullDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDifferentiator;


public abstract class AbstractReflectiveDiffTests extends TestCase {
	protected Differentiator differentiator;
	protected Employee employee1;
	protected Employee employee2;


	public AbstractReflectiveDiffTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.differentiator = this.buildDifferentiator();
		this.employee1 = this.buildEmployee();
		this.employee2 = this.buildEmployee();
	}

	protected abstract Differentiator buildDifferentiator();

	protected Employee buildEmployee() {
		Employee result = this.buildEmployee(1, "Fred Flintstone");
		result.setSalary(20000.20f);
		result.setPosition("long-neck operator");
		result.addComment("animated sitcom character");
		result.addComment("member of Moose Lodge");
		result.addComment("not too bright");
		return result;
	}

	protected abstract Employee buildEmployee(int id, String name);

	protected abstract ReflectiveDifferentiator employeeDifferentiator();

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testNoDifference() {
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);

		diff = this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);
	}

	public void testOneNull() {
		Diff diff = this.differentiator.diff(null, this.employee2);
		this.verifyDiffMismatch(diff, null, this.employee2);

		diff = this.differentiator.keyDiff(null, this.employee2);
		this.verifyDiffMismatch(diff, null, this.employee2);

		diff = this.differentiator.diff(this.employee1, null);
		this.verifyDiffMismatch(diff, this.employee1, null);

		diff = this.differentiator.keyDiff(this.employee1, null);
		this.verifyDiffMismatch(diff, this.employee1, null);
	}

	public void testClassMismatch() {
		Object object2 = new Integer(42);
		Diff diff = this.differentiator.diff(this.employee1, object2);
		this.verifyDiffMismatch(diff, this.employee1, object2);

		diff = this.differentiator.keyDiff(this.employee1, object2);
		this.verifyDiffMismatch(diff, this.employee1, object2);

		diff = this.differentiator.diff(object2, this.employee2);
		this.verifyDiffMismatch(diff, object2, this.employee2);

		diff = this.differentiator.keyDiff(object2, this.employee2);
		this.verifyDiffMismatch(diff, object2, this.employee2);
	}

	public void testOneFieldMismatch() {
		this.employee2.setName("Barney Rubble");
		CompositeDiff diff = (CompositeDiff) this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);
		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());

		Diff leafDiff = (Diff) leafMismatches.get(0);		// name
		assertEquals("Fred Flintstone", leafDiff.getObject1());
		assertEquals("Barney Rubble", leafDiff.getObject2());

		diff = (CompositeDiff) this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);
	}

	public void testTwoFieldMismatch() {
		this.employee2.setId(77);
		this.employee2.setName("Barney Rubble");
		CompositeDiff diff = (CompositeDiff) this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);
		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(2, leafMismatches.size());

		// alphabetical order...
		Diff leafDiff;

		leafDiff = (Diff) leafMismatches.get(0);		// id
		assertEquals(new Integer(1), leafDiff.getObject1());
		assertEquals(new Integer(77), leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(1);		// name
		assertEquals("Fred Flintstone", leafDiff.getObject1());
		assertEquals("Barney Rubble", leafDiff.getObject2());

		diff = (CompositeDiff) this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);
	}

	public void testCollectionFieldMismatch() {
		this.employee2.addComment("bogus comment");
		CompositeDiff diff = (CompositeDiff) this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);
		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());
		Diff leafDiff = (Diff) leafMismatches.get(0);		// comments
		assertTrue(((Collection) leafDiff.getObject2()).contains("bogus comment"));

		diff = (CompositeDiff) this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);

		for (Iterator stream = this.employee2.comments(); stream.hasNext(); ) {
			String comment = (String) stream.next();
			if (comment.equals("bogus comment")) {
				stream.remove();
			}
		}
		diff = (CompositeDiff) this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);

		diff = (CompositeDiff) this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);

		String removedComment = null;
		for (Iterator stream = this.employee2.comments(); stream.hasNext(); ) {
			String comment = (String) stream.next();
			if (comment.startsWith("animated")) {
				stream.remove();
				removedComment = comment;
			}
		}
		diff = (CompositeDiff) this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);
		leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());
		leafDiff = (Diff) leafMismatches.get(0);		// comments
		assertTrue(((Collection) leafDiff.getObject1()).contains(removedComment));
		assertFalse(((Collection) leafDiff.getObject2()).contains(removedComment));

		diff = (CompositeDiff) this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);
	}

	public void testAllFieldMismatch() {
		this.employee2.setId(77);
		this.employee2.setName("Barney Rubble");
		this.employee2.setSalary(200000.01f);
		this.employee2.setPosition("troublemaker");
		String addedComment = "Fred's sidekick";
		this.employee2.addComment(addedComment);
		CompositeDiff diff = (CompositeDiff) this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);
		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(5, leafMismatches.size());

		// alphabetical order...
		Diff leafDiff;

		leafDiff = (Diff) leafMismatches.get(0);		// comments
		assertFalse(((Collection) leafDiff.getObject1()).contains(addedComment));
		assertTrue(((Collection) leafDiff.getObject2()).contains(addedComment));

		leafDiff = (Diff) leafMismatches.get(1);		// id
		assertEquals(new Integer(1), leafDiff.getObject1());
		assertEquals(new Integer(77), leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(2);		// name
		assertEquals("Fred Flintstone", leafDiff.getObject1());
		assertEquals("Barney Rubble", leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(3);		// position
		assertEquals("long-neck operator", leafDiff.getObject1());
		assertEquals("troublemaker", leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(4);		// salary
		assertEquals(new Float(20000.20f), leafDiff.getObject1());
		assertEquals(new Float(200000.01f), leafDiff.getObject2());

		diff = (CompositeDiff) this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);
		leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());
		leafDiff = (Diff) leafMismatches.get(0);		// id
		assertEquals(new Integer(1), leafDiff.getObject1());
		assertEquals(new Integer(77), leafDiff.getObject2());
	}

	public void testNullFieldMismatch() {
		this.employee2.setPosition(null);
		CompositeDiff diff = (CompositeDiff) this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);
		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());
		Diff leafDiff = (Diff) leafMismatches.get(0);		// position
		assertEquals("long-neck operator", leafDiff.getObject1());
		assertNull(leafDiff.getObject2());

		diff = (CompositeDiff) this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);
	}

	public void testIgnoreField() {
		this.employee2.setPosition("troublemaker");
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		ReflectiveDifferentiator rd = this.employeeDifferentiator();
		rd.ignoreFieldNamed("position");
		diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);

		diff = this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);

		boolean exCaught = false;
		try {
			rd.ignoreFieldNamed("positionXXX");
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("positionXXX") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	public void testFieldDifferentiator() {
		this.employee2.setSalary(this.employee1.getSalary() + 0.02f);
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		diff = this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);

		ReflectiveDifferentiator rd = this.employeeDifferentiator();
		rd.setFieldDifferentiator("salary", this.buildCustomSalaryDifferentiator());
		diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);

		diff = this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);

		this.employee2.setSalary(this.employee1.getSalary() + 1.02f);
		diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		diff = this.differentiator.keyDiff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);

		boolean exCaught = false;
		try {
			rd.setFieldDifferentiator("salaryXXX", IdentityDifferentiator.instance());
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("salaryXXX") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			rd.setFieldDifferentiator("salary", IdentityDifferentiator.instance());
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("salary") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	protected Differentiator buildCustomSalaryDifferentiator() {
		return new Differentiator() {
			public Diff diff(Object object1, Object object2) {
				float salary1 = ((Float) object1).floatValue();
				float salary2 = ((Float) object2).floatValue();
				if (Math.abs(salary2 - salary1) < 1) {
					return NullDifferentiator.instance().diff(object1, object2);
				}
				return EqualityDifferentiator.instance().diff(object1, object2);
			}
			public Diff keyDiff(Object object1, Object object2) {
				return this.diff(object1, object2);
			}
			public boolean comparesValueObjects() {
				return true;
			}
		};
	}

	public void testInterface() {
		Class bogusClass = Collection.class;
		boolean exCaught = false;
		try {
			this.differentiator = new ReflectiveDifferentiator(bogusClass);
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf(bogusClass.getName()) != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	protected void verifyDiffMatch(Diff diff, Object object1, Object object2) {
		assertEquals(object1, diff.getObject1());
		assertEquals(object2, diff.getObject2());
		assertTrue(diff.identical());
		assertFalse(diff.different());
		assertEquals(0, diff.getDescription().length());
	}

	protected void verifyDiffMismatch(Diff diff, Object object1, Object object2) {
		assertEquals(object1, diff.getObject1());
		assertEquals(object2, diff.getObject2());
		assertFalse(diff.identical());
		assertTrue(diff.different());
		assertTrue(diff.getDescription().length() > 0);
	}

}
