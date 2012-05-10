/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.BasicRefactoringTool;
import org.eclipse.persistence.jpa.jpql.DefaultBasicRefactoringTool;
import org.junit.Test;

/**
 * The abstract definition of a unit-test that tests {@link org.eclipse.persistence.jpa.jpql.
 * RefactoringTool RefactoringTool} when the JPA version is 1.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class BasicRefactoringToolTest1_0 extends AbstractBasicRefactoringToolTest {

	private BasicRefactoringTool buildRefactoringTool(String jpqlQuery) throws Exception {
		return new DefaultBasicRefactoringTool(jpqlQuery, getGrammar(), getPersistenceUnit());
	}

	@Test
	public void test_RenameClassName_1() throws Exception {

		String jpqlQuery = "SELECT NEW java.util.Vector(a.employees) FROM Address A";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT NEW ".length();
		String oldValue = "java.util.Vector";
		String newValue = "java.util.ArrayList";
		refactoringTool.renameClassName(oldValue, newValue);

		String expected = "SELECT NEW java.util.ArrayList(a.employees) FROM Address A";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameClassName_2() throws Exception {

		String jpqlQuery = "SELECT NEW org.eclipse.persistence.Collection.Vector(a.employees) FROM Address A";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT NEW ".length();
		String oldValue = "org.eclipse.persistence.Collection";
		String newValue = "org.eclipse.persistence.Type";
		refactoringTool.renameClassName(oldValue, newValue);

		String expected = "SELECT NEW org.eclipse.persistence.Type.Vector(a.employees) FROM Address A";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameClassName_3() throws Exception {

		String jpqlQuery = "SELECT NEW org.eclipse.persistence.Vector(a.employees) FROM Address A";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		String oldValue = "org.eclipse.persistence.AbstractSession";
		String newValue = "org.eclipse.persistence.session.Session";
		refactoringTool.renameClassName(oldValue, newValue);

		testHasNoChanges(refactoringTool);
	}

	@Test
	public void test_RenameClassName_4() throws Exception {

		String jpqlQuery = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.FIRST_NAME";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT p FROM Product p WHERE p.enumType = ".length();
		String oldValue = "jpql.query.EnumType";
		String newValue = "org.eclipse.persistence.Type";
		refactoringTool.renameClassName(oldValue, newValue);

		String expected = "SELECT p FROM Product p WHERE p.enumType = org.eclipse.persistence.Type.FIRST_NAME";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameClassName_5() throws Exception {

		String jpqlQuery = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT p FROM Product p WHERE p.enumType = ".length();
		String oldValue = "jpql.query.EnumType";
		String newValue = "org.eclipse.persistence.Type";
		refactoringTool.renameClassName(oldValue, newValue);

		String expected = "SELECT p FROM Product p WHERE p.enumType = org.eclipse.persistence.Type.";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameClassName_6() throws Exception {

		String jpqlQuery = "SELECT p FROM Product p WHERE p.enumType = jpql.query.Employee.EnumType.FIRST_NAME";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT p FROM Product p WHERE p.enumType = ".length();
		String oldValue = "jpql.query.Employee";
		String newValue = "org.eclipse.persistence.Type";
		refactoringTool.renameClassName(oldValue, newValue);

		String expected = "SELECT p FROM Product p WHERE p.enumType = org.eclipse.persistence.Type.EnumType.FIRST_NAME";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameClassName_7() throws Exception {

		String jpqlQuery = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.FIRST_NAME";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameClassName("jpql.query.Employee", "org.eclipse.persistence.Type");

		testHasNoChanges(refactoringTool);
	}

	@Test
	public void test_RenameEntityName_1() throws Exception {

		String jpqlQuery = "SELECT a FROM Address A";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT a FROM ".length();
		String oldValue = "Address";
		String newValue = "Employee";
		refactoringTool.renameEntityName(oldValue, newValue);

		String expected = "SELECT a FROM Employee A";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameEntityName_2() throws Exception {

		String jpqlQuery = "SELECT a FROM Address A WHERE EXISTS(SELECT e FROM Employee e)";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT a FROM Address A WHERE EXISTS(SELECT e FROM ".length();
		String oldValue = "Employee";
		String newValue = "Manager";
		refactoringTool.renameEntityName(oldValue, newValue);

		String expected = "SELECT a FROM Address A WHERE EXISTS(SELECT e FROM Manager e)";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameEnumConstant_1() throws Exception {

		String jpqlQuery = "UPDATE Employee SET name = javax.persistence.AccessType.FIELD";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "UPDATE Employee SET name = ".length();
		String oldValue = "javax.persistence.AccessType.FIELD";
		String newValue = "javax.persistence.AccessType.PROPERTY";
		refactoringTool.renameEnumConstant(oldValue, newValue);

		String expected = "UPDATE Employee SET name = javax.persistence.AccessType.PROPERTY";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameEnumConstant_2() throws Exception {

		String jpqlQuery = "UPDATE Employee e SET e.name = e.lName";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		String oldValue = "javax.persistence.AccessType.FIELD";
		String newValue = "javax.persistence.AccessType.PROPERTY";
		refactoringTool.renameEnumConstant(oldValue, newValue);

		testHasNoChanges(refactoringTool);
	}

	@Test
	public void test_RenameFieldName_1() throws Exception {

		String jpqlQuery = "SELECT e.address.zip FROM Employee e";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset = "SELECT e.".length();
		String typeName = "jpql.query.Employee";
		String oldValue = "address";
		String newValue = "addr";
		refactoringTool.renameField(typeName, oldValue, newValue);

		String expected = "SELECT e.addr.zip FROM Employee e";
		testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
	}

	@Test
	public void test_RenameFieldName_2() throws Exception {

		String jpqlQuery = "SELECT e.address.zipcode FROM Employee e";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		String typeName = "jpql.query.Address";
		String oldValue = "zip";
		String newValue = "zipcode";
		refactoringTool.renameField(typeName, oldValue, newValue);

		testHasNoChanges(refactoringTool);
	}

	@Test
	public void test_RenameVariable_1() throws Exception {

		String jpqlQuery = "SELECT a FROM Address A";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset1 = "SELECT ".length();
		int offset2 = "SELECT a FROM Address ".length();
		String oldValue = "a";
		String newValue = "addr";
		refactoringTool.renameVariable(oldValue, newValue);

		String expected = "SELECT addr FROM Address addr";
		testChanges(refactoringTool, jpqlQuery, expected, oldValue, newValue, offset2, offset1);
	}

	@Test
	public void test_RenameVariable_2() throws Exception {

		String jpqlQuery = "SELECT a FROM Address a JOIN a.employees ad";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset1 = "SELECT ".length();
		int offset2 = "SELECT a FROM Address ".length();
		int offset3 = "SELECT a FROM Address a JOIN ".length();
		String oldValue = "a";
		String newValue = "addr";
		refactoringTool.renameVariable("a", "addr");

		String expected = "SELECT addr FROM Address addr JOIN addr.employees ad";
		testChanges(refactoringTool, jpqlQuery, expected, oldValue, newValue, offset3, offset2, offset1);
	}

	@Test
	public void test_RenameVariable_3() throws Exception {

		String jpqlQuery = "SELECT a FROM Address a WHERE EXISTS(SELECT e FROM a.employee)";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

		int offset1 = "SELECT ".length();
		int offset2 = "SELECT a FROM Address ".length();
		int offset3 = "SELECT a FROM Address a WHERE EXISTS(SELECT e FROM ".length();
		String oldValue = "a";
		String newValue = "addr";
		refactoringTool.renameVariable("a", "addr");

		String expected = "SELECT addr FROM Address addr WHERE EXISTS(SELECT e FROM addr.employee)";
		testChanges(refactoringTool, jpqlQuery, expected, oldValue, newValue, offset3, offset2, offset1);
	}

	@Test
	public void test_RenameVariable_4() throws Exception {

		String jpqlQuery = "SELECT FROM Address";
		BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameVariable("a", "addr");

		testHasNoChanges(refactoringTool);
	}
}