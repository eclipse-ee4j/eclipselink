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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.diff.model.Address;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.Car;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.Dependent;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.Employee;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.MultiClassEmployee;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.PhoneNumber;
import org.eclipse.persistence.tools.workbench.test.utility.diff.model.State;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.diff.CompositeDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ContainerDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.IdentityDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.OrderedContainerDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveFieldDiff;


public class MultiClassReflectiveDiffTests extends AbstractReflectiveDiffTests {


	public static Test suite() {
		return new TestSuite(MultiClassReflectiveDiffTests.class);
	}
	
	public MultiClassReflectiveDiffTests(String name) {
		super(name);
	}

	protected Differentiator buildDifferentiator() {
		DiffEngine diffEngine = new DiffEngine();

		ReflectiveDifferentiator rd;
		rd = diffEngine.addReflectiveDifferentiator(MultiClassEmployee.class);
		rd.addKeyFieldNamed("id");
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

	protected Employee buildEmployee() {
		Employee result = super.buildEmployee();

		result.setAddress(new Address("201 Cobblestone Way", "Bedrock", State.CO, "99242"));

		result.addDependent("Wilma", "wife");
		result.addDependent("Pebbles", "daughter");
		result.addDependent("Dino", "pet");

		result.addCar("Babe", "Lexus");
		result.addCar("Buck", "Dodge");

		result.addPhoneNumber("home", "212", "555", "1212");
		result.addPhoneNumber("work", "212", "555", "1234");
		result.addPhoneNumber("mobile", "212", "555", "4321");

		result.addUnderling(this.buildEmployee(111, "David Lee Roth"));
		result.addUnderling(this.buildEmployee(222, "Orson Wells"));
		result.addUnderling(this.buildEmployee(333, "Charlie Brown"));

		result.addVacationBackup(this.buildEmployee(123, "Madonna"));
		result.addVacationBackup(this.buildEmployee(234, "Charo"));
		result.addVacationBackup(this.buildEmployee(345, "Evita"));

		result.setEatingPartner("breakfast", this.buildEmployee(987, "Pele"));
		result.setEatingPartner("lunch", this.buildEmployee(876, "Ronaldo"));
		result.setEatingPartner("dinner", this.buildEmployee(765, "Ali"));

		return result;
	}

	protected Employee buildEmployee(int id, String name) {
		return new MultiClassEmployee(id, name);
	}

	protected ReflectiveDifferentiator employeeDifferentiator() {
		return (ReflectiveDifferentiator) ((DiffEngine) this.differentiator).getUserDifferentiator(MultiClassEmployee.class);
	}

	public void testNestedMismatch() {
		this.employee2.setAddress(new Address("112 Boogie-Woogie Avenue", "Hollyrock", State.CA, "99999"));
		CompositeDiff diff = (CompositeDiff) this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(4, leafMismatches.size());

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(4, leafReflectiveFieldMismatches.size());

		// inheritance order, then alphabetical order...
		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("city", leafDiff.getField().getName());
		assertEquals("Bedrock", leafDiff.getObject1());
		assertEquals("Hollyrock", leafDiff.getObject2());

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(1);
		assertEquals("state", leafDiff.getField().getName());
		assertSame(State.CO, leafDiff.getObject1());
		assertSame(State.CA, leafDiff.getObject2());

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(2);
		assertEquals("street", leafDiff.getField().getName());
		assertEquals("201 Cobblestone Way", leafDiff.getObject1());
		assertEquals("112 Boogie-Woogie Avenue", leafDiff.getObject2());

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(3);
		assertEquals("zip", leafDiff.getField().getName());
		assertEquals("99242", leafDiff.getObject1());
		assertEquals("99999", leafDiff.getObject2());
	}

	public void testNestedOneFieldMismatch() {
		Address address2 = this.employee2.getAddress();
		address2.setStreet("112 Boogie-Woogie Avenue");
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		// inheritance order, then alphabetical order...
		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("street", leafDiff.getField().getName());
		assertEquals("201 Cobblestone Way", leafDiff.getObject1());
		assertEquals("112 Boogie-Woogie Avenue", leafDiff.getObject2());
	}

	public void testNestedTwoFieldMismatch() {
		Address address2 = this.employee2.getAddress();
		address2.setStreet("112 Boogie-Woogie Avenue");
		address2.setCity("Hollyrock");
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(2, leafReflectiveFieldMismatches.size());

		// inheritance order, then alphabetical order...
		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("city", leafDiff.getField().getName());
		assertEquals("Bedrock", leafDiff.getObject1());
		assertEquals("Hollyrock", leafDiff.getObject2());

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(1);
		assertEquals("street", leafDiff.getField().getName());
		assertEquals("201 Cobblestone Way", leafDiff.getObject1());
		assertEquals("112 Boogie-Woogie Avenue", leafDiff.getObject2());
	}

	public void testNestedAllFieldMismatch() {
		Address address2 = this.employee2.getAddress();
		address2.setStreet("112 Boogie-Woogie Avenue");
		address2.setCity("Hollyrock");
		address2.setState(State.CA);
		address2.setZip(null);
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(4, leafReflectiveFieldMismatches.size());

		// inheritance order, then alphabetical order...
		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("city", leafDiff.getField().getName());
		assertEquals("Bedrock", leafDiff.getObject1());
		assertEquals("Hollyrock", leafDiff.getObject2());

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(1);
		assertEquals("state", leafDiff.getField().getName());
		assertSame(State.CO, leafDiff.getObject1());
		assertSame(State.CA, leafDiff.getObject2());

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(2);
		assertEquals("street", leafDiff.getField().getName());
		assertEquals("201 Cobblestone Way", leafDiff.getObject1());
		assertEquals("112 Boogie-Woogie Avenue", leafDiff.getObject2());

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(3);
		assertEquals("zip", leafDiff.getField().getName());
		assertEquals("99242", leafDiff.getObject1());
		assertEquals(null, leafDiff.getObject2());
	}

	public void testClassSpecificDifferentiator() {
		Address address2 = this.employee2.getAddress();
		State bogusState = this.buildBogusState(address2.getState());
		address2.setState(bogusState);
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("state", leafDiff.getField().getName());
		assertSame(State.CO, leafDiff.getObject1());
		assertSame(bogusState, leafDiff.getObject2());
	}

	/**
	 * use reflection to create an "illegal" state,
	 * using the private constructor
	 */
	protected State buildBogusState(State state) {
		return (State) ClassTools.newInstance(State.class, new Class[] {String.class, String.class}, new String[] {state.getAbbreviation(), state.getName()});
	}

	public void testCompositeCollectionAddElements() {
		Dependent addedDependent1 = this.employee2.addDependent("Cousin Bob", "moocher");
		Dependent addedDependent2 = this.employee2.addDependent("Sister Sue", "moochere");
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("dependents", leafDiff.getField().getName());
		ContainerDiff containerDiff = (ContainerDiff) leafDiff.getDiff();
		Object[] addedElements = containerDiff.getAddedElements();
		assertEquals(2, addedElements.length);
		assertTrue(CollectionTools.contains(addedElements, addedDependent1));
		assertTrue(CollectionTools.contains(addedElements, addedDependent2));
		assertEquals(0, containerDiff.getRemovedElements().length);
	}

	public void testCompositeCollectionRemoveElements() {
		Dependent removedDependent1 = this.employee1.addDependent("Cousin Bob", "moocher");
		Dependent removedDependent2 = this.employee1.addDependent("Sister Sue", "moochere");
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("dependents", leafDiff.getField().getName());
		ContainerDiff containerDiff = (ContainerDiff) leafDiff.getDiff();
		Object[] removedElements = containerDiff.getRemovedElements();
		assertEquals(2, removedElements.length);
		assertTrue(CollectionTools.contains(removedElements, removedDependent1));
		assertTrue(CollectionTools.contains(removedElements, removedDependent2));
		assertEquals(0, containerDiff.getAddedElements().length);
	}

	public void testCompositeCollectionChangeElements() {
		Dependent changedDependent1 = this.employee2.dependentNamed("Wilma");
		String originalDescription1 = changedDependent1.getDescription();
		String modifiedDescription1 = "common law " + originalDescription1;
		changedDependent1.setDescription(modifiedDescription1);

		Dependent changedDependent2 = this.employee2.dependentNamed("Pebbles");
		String originalDescription2 = changedDependent2.getDescription();
		String modifiedDescription2 = "common law " + originalDescription2;
		changedDependent2.setDescription(modifiedDescription2);

		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(2, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("description", leafDiff.getField().getName());
		boolean changedDependent1Found = false;
		if (leafDiff.getObject1().equals(originalDescription1)) {
			assertEquals(modifiedDescription1, leafDiff.getObject2());
			changedDependent1Found = true;
		} else {
			assertEquals(modifiedDescription2, leafDiff.getObject2());
		}

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(1);
		assertEquals("description", leafDiff.getField().getName());
		if (changedDependent1Found) {
			assertEquals(modifiedDescription2, leafDiff.getObject2());
		} else {
			assertEquals(modifiedDescription1, leafDiff.getObject2());
		}
	}

	public void testCompositeListAddElements() {
		Car addedCar1 = this.employee2.addCar("Bossy", "Chrysler");
		Car addedCar2 = this.employee2.addCar("Bobby", "BMW");
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("cars", leafDiff.getField().getName());
		OrderedContainerDiff ocDiff = (OrderedContainerDiff) leafDiff.getDiff();
		Diff[] diffs = ocDiff.getDiffs();
		assertEquals(4, diffs.length);
		assertEquals(addedCar1, diffs[2].getObject2());
		assertEquals(addedCar2, diffs[3].getObject2());
	}

	public void testCompositeListRemoveElements() {
		Car removedCar1 = this.employee1.addCar("Bossy", "Chrysler");
		Car removedCar2 = this.employee1.addCar("Bobby", "BMW");
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("cars", leafDiff.getField().getName());
		OrderedContainerDiff ocDiff = (OrderedContainerDiff) leafDiff.getDiff();
		Diff[] diffs = ocDiff.getDiffs();
		assertEquals(4, diffs.length);
		assertEquals(removedCar1, diffs[2].getObject1());
		assertEquals(removedCar2, diffs[3].getObject1());
	}

	public void testCompositeListChangeElements() {
		Car changedCar1 = this.employee2.carNamed("Babe");
		String originalDescription1 = changedCar1.getDescription();
		String modifiedDescription1 = originalDescription1 + " (Toyota)";
		changedCar1.setDescription(modifiedDescription1);

		Car changedCar2 = this.employee2.carNamed("Buck");
		String originalDescription2 = changedCar2.getDescription();
		String modifiedDescription2 = originalDescription2 + " (Daimler-Chrysler)";
		changedCar2.setDescription(modifiedDescription2);

		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(2, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("description", leafDiff.getField().getName());
		boolean changedCar1Found = false;
		if (leafDiff.getObject1().equals(originalDescription1)) {
			assertEquals(modifiedDescription1, leafDiff.getObject2());
			changedCar1Found = true;
		} else {
			assertEquals(modifiedDescription2, leafDiff.getObject2());
		}

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(1);
		assertEquals("description", leafDiff.getField().getName());
		if (changedCar1Found) {
			assertEquals(modifiedDescription2, leafDiff.getObject2());
		} else {
			assertEquals(modifiedDescription1, leafDiff.getObject2());
		}
	}

	public void testCompositeMapAddElements() {
		String key1 = "beach";
		PhoneNumber addedPhone1 = this.employee2.addPhoneNumber(key1, "212", "555", "7777");
		PhoneNumber addedPhone2 = this.employee2.addPhoneNumber("country", "212", "555", "5555");
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("phoneNumbers", leafDiff.getField().getName());
		ContainerDiff containerDiff = (ContainerDiff) leafDiff.getDiff();

		Object[] addedElements = containerDiff.getAddedElements();
		assertEquals(2, addedElements.length);
		Map.Entry entry = (Map.Entry) addedElements[0];
		boolean phone1Found = false;
		if (entry.getKey().equals(key1)) {
			assertEquals(addedPhone1, entry.getValue());
			phone1Found = true;
		} else {
			assertEquals(addedPhone2, entry.getValue());
		}
		entry = (Map.Entry) addedElements[1];
		if (phone1Found) {
			assertEquals(addedPhone2, entry.getValue());
		} else {
			assertEquals(addedPhone1, entry.getValue());
		}

		assertEquals(0, containerDiff.getRemovedElements().length);
	}

	public void testCompositeMapRemoveElements() {
		String key1 = "beach";
		PhoneNumber addedPhone1 = this.employee1.addPhoneNumber(key1, "212", "555", "7777");
		PhoneNumber addedPhone2 = this.employee1.addPhoneNumber("country", "212", "555", "5555");
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("phoneNumbers", leafDiff.getField().getName());
		ContainerDiff containerDiff = (ContainerDiff) leafDiff.getDiff();

		Object[] removedElements = containerDiff.getRemovedElements();
		assertEquals(2, removedElements.length);
		Map.Entry entry = (Map.Entry) removedElements[0];
		boolean phone1Found = false;
		if (entry.getKey().equals(key1)) {
			assertEquals(addedPhone1, entry.getValue());
			phone1Found = true;
		} else {
			assertEquals(addedPhone2, entry.getValue());
		}
		entry = (Map.Entry) removedElements[1];
		if (phone1Found) {
			assertEquals(addedPhone2, entry.getValue());
		} else {
			assertEquals(addedPhone1, entry.getValue());
		}

		assertEquals(0, containerDiff.getAddedElements().length);
	}

	public void testCompositeMapChangeElements() {
		Iterator stream = this.employee2.phoneNumbers();

		Map.Entry entry1 = (Map.Entry) stream.next();
		String comment1 = "blah blah blah";
		((PhoneNumber) entry1.getValue()).setComment(comment1);

		Map.Entry entry2 = (Map.Entry) stream.next();
		String comment2 = "yadda yadda yadda";
		((PhoneNumber) entry2.getValue()).setComment(comment2);

		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(2, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff1 = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("comment", leafDiff1.getField().getName());
		ReflectiveFieldDiff leafDiff2 = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(1);
		assertEquals("comment", leafDiff2.getField().getName());
		assertTrue((leafDiff1.getObject2().equals(comment1) && leafDiff2.getObject2().equals(comment2)) ||
				(leafDiff1.getObject2().equals(comment2) && leafDiff2.getObject2().equals(comment1)));
	}

	public void testReferenceCollectionAddElements() {
		Employee addedUnderling1 = this.buildEmployee(555, "Charlie Chaplin");
		this.employee2.addUnderling(addedUnderling1);
		Employee addedUnderling2 = this.buildEmployee(666, "Mephistopheles");
		this.employee2.addUnderling(addedUnderling2);
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("underlings", leafDiff.getField().getName());
		ContainerDiff containerDiff = (ContainerDiff) leafDiff.getDiff();
		Object[] addedElements = containerDiff.getAddedElements();
		assertEquals(2, addedElements.length);
		assertTrue(CollectionTools.contains(addedElements, addedUnderling1));
		assertTrue(CollectionTools.contains(addedElements, addedUnderling2));
		assertEquals(0, containerDiff.getRemovedElements().length);
	}

	public void testReferenceCollectionRemoveElements() {
		Employee removedUnderling1 = this.buildEmployee(555, "Charlie Chaplin");
		this.employee1.addUnderling(removedUnderling1);
		Employee removedUnderling2 = this.buildEmployee(666, "Mephistopheles");
		this.employee1.addUnderling(removedUnderling2);
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("underlings", leafDiff.getField().getName());
		ContainerDiff containerDiff = (ContainerDiff) leafDiff.getDiff();
		Object[] removedElements = containerDiff.getRemovedElements();
		assertEquals(2, removedElements.length);
		assertTrue(CollectionTools.contains(removedElements, removedUnderling1));
		assertTrue(CollectionTools.contains(removedElements, removedUnderling2));
		assertEquals(0, containerDiff.getAddedElements().length);
	}

	public void testReferenceCollectionChangeElements() {
		Employee changedUnderling1 = this.employee2.underlingNamed("David Lee Roth");
		changedUnderling1.setPosition("vocals");
		Employee changedUnderling2 = this.employee2.underlingNamed("Charlie Brown");
		changedUnderling2.setPosition("loser");

		Diff diff = this.differentiator.diff(this.employee1.underlingNamed("David Lee Roth"), changedUnderling1);
		this.verifyDiffMismatch(diff, this.employee1.underlingNamed("David Lee Roth"), changedUnderling1);

		diff = this.differentiator.diff(this.employee1.underlingNamed("Charlie Brown"), changedUnderling2);
		this.verifyDiffMismatch(diff, this.employee1.underlingNamed("Charlie Brown"), changedUnderling2);

		diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);
	}

	public void testReferenceListAddElements() {
		Employee addedBackup1 = this.buildEmployee(567, "Zsa Zsa");
		this.employee2.addVacationBackup(addedBackup1);
		Employee addedBackup2 = this.buildEmployee(678, "Peppa");
		this.employee2.addVacationBackup(addedBackup2);
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("vacationBackups", leafDiff.getField().getName());
		OrderedContainerDiff ocDiff = (OrderedContainerDiff) leafDiff.getDiff();
		Diff[] diffs = ocDiff.getDiffs();
		assertEquals(5, diffs.length);
		assertEquals(addedBackup1, diffs[3].getObject2());
		assertEquals(addedBackup2, diffs[4].getObject2());
	}

	public void testReferenceListRemoveElements() {
		Employee removedBackup1 = this.buildEmployee(567, "Zsa Zsa");
		this.employee1.addVacationBackup(removedBackup1);
		Employee removedBackup2 = this.buildEmployee(678, "Peppa");
		this.employee1.addVacationBackup(removedBackup2);
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("vacationBackups", leafDiff.getField().getName());
		OrderedContainerDiff ocDiff = (OrderedContainerDiff) leafDiff.getDiff();
		Diff[] diffs = ocDiff.getDiffs();
		assertEquals(5, diffs.length);
		assertEquals(removedBackup1, diffs[3].getObject1());
		assertEquals(removedBackup2, diffs[4].getObject1());
	}

	public void testReferenceListChangeElements() {
		Employee changedBackup1 = this.employee2.vacationBackupNamed("Madonna");
		changedBackup1.setPosition("shape-shifter");
		Employee changedBackup2 = this.employee2.vacationBackupNamed("Evita");
		changedBackup2.setPosition("Saint");

		Diff diff = this.differentiator.diff(this.employee1.vacationBackupNamed("Madonna"), changedBackup1);
		this.verifyDiffMismatch(diff, this.employee1.vacationBackupNamed("Madonna"), changedBackup1);

		diff = this.differentiator.diff(this.employee1.vacationBackupNamed("Evita"), changedBackup2);
		this.verifyDiffMismatch(diff, this.employee1.vacationBackupNamed("Evita"), changedBackup2);

		diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);
	}

	public void testReferenceMapAddElements() {
		String key1 = "supper";
		Employee addedPartner1 = this.buildEmployee(654, "Magic");
		this.employee2.setEatingPartner(key1, addedPartner1);
		String key2 = "late night snack";
		Employee addedPartner2 = this.buildEmployee(543, "Michael");
		this.employee2.setEatingPartner(key2, addedPartner2);
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("eatingPartners", leafDiff.getField().getName());
		ContainerDiff containerDiff = (ContainerDiff) leafDiff.getDiff();

		Object[] addedElements = containerDiff.getAddedElements();
		assertEquals(2, addedElements.length);
		Map.Entry entry = (Map.Entry) addedElements[0];
		boolean partner1Found = false;
		if (entry.getKey().equals(key1)) {
			assertEquals(addedPartner1, entry.getValue());
			partner1Found = true;
		} else {
			assertEquals(addedPartner2, entry.getValue());
		}
		entry = (Map.Entry) addedElements[1];
		if (partner1Found) {
			assertEquals(addedPartner2, entry.getValue());
		} else {
			assertEquals(addedPartner1, entry.getValue());
		}

		assertEquals(0, containerDiff.getRemovedElements().length);
	}

	public void testReferenceMapRemoveElements() {
		String key1 = "supper";
		Employee removedPartner1 = this.buildEmployee(654, "Magic");
		this.employee1.setEatingPartner(key1, removedPartner1);
		String key2 = "late night snack";
		Employee removedPartner2 = this.buildEmployee(543, "Michael");
		this.employee1.setEatingPartner(key2, removedPartner2);
		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff;

		leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("eatingPartners", leafDiff.getField().getName());
		ContainerDiff containerDiff = (ContainerDiff) leafDiff.getDiff();

		Object[] removedElements = containerDiff.getRemovedElements();
		assertEquals(2, removedElements.length);
		Map.Entry entry = (Map.Entry) removedElements[0];
		boolean partner1Found = false;
		if (entry.getKey().equals(key1)) {
			assertEquals(removedPartner1, entry.getValue());
			partner1Found = true;
		} else {
			assertEquals(removedPartner2, entry.getValue());
		}
		entry = (Map.Entry) removedElements[1];
		if (partner1Found) {
			assertEquals(removedPartner2, entry.getValue());
		} else {
			assertEquals(removedPartner1, entry.getValue());
		}

		assertEquals(0, containerDiff.getAddedElements().length);
	}

	public void testReferenceMapChangeElements() {
		Employee changedPartner1 = this.employee2.getEatingPartner("breakfast");
		changedPartner1.setSalary(405000.00f);

		Employee changedPartner2 = this.employee2.getEatingPartner("lunch");
		changedPartner2.setSalary(666000.00f);

		Diff diff = this.differentiator.diff(this.employee1.getEatingPartner("breakfast"), changedPartner1);
		this.verifyDiffMismatch(diff, this.employee1.getEatingPartner("breakfast"), changedPartner1);

		diff = this.differentiator.diff(this.employee1.getEatingPartner("lunch"), changedPartner2);
		this.verifyDiffMismatch(diff, this.employee1.getEatingPartner("lunch"), changedPartner2);

		diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMatch(diff, this.employee1, this.employee2);
	}

	public void testReferenceMapReplaceElements() {
		Employee oldPartner1 = this.employee1.getEatingPartner("lunch");
		Employee newPartner1 = this.buildEmployee(555, "Ted");
		this.employee2.setEatingPartner("lunch", newPartner1);

		Diff diff = this.differentiator.diff(this.employee1, this.employee2);
		this.verifyDiffMismatch(diff, this.employee1, this.employee2);

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("eatingPartners", leafDiff.getField().getName());
		ContainerDiff containerDiff = (ContainerDiff) leafDiff.getDiff();

		Object[] removedElements = containerDiff.getRemovedElements();
		assertEquals(1, removedElements.length);
		Map.Entry entry = (Map.Entry) removedElements[0];
		assertEquals("lunch", entry.getKey());
		assertEquals(oldPartner1, entry.getValue());

		Object[] addedElements = containerDiff.getAddedElements();
		assertEquals(1, addedElements.length);
		entry = (Map.Entry) addedElements[0];
		assertEquals("lunch", entry.getKey());
		assertEquals(newPartner1, entry.getValue());
	}

}
