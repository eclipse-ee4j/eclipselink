/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.DefaultRefactoringTool;
import org.eclipse.persistence.jpa.jpql.RefactoringTool;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * The abstract definition of a unit-test that tests {@link org.eclipse.persistence.jpa.jpql.
 * RefactoringTool RefactoringTool} when the JPA version is 1.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class RefactoringToolTest1_0 extends AbstractRefactoringToolTest {

	private RefactoringTool buildRefactoringTool(String jpqlQuery) throws Exception {
		return new DefaultRefactoringTool(getPersistenceUnit(), getJPQLQueryBuilder(), jpqlQuery);
	}

	@Test
	public void test_RenameClassName_1() throws Exception {

		String jpqlQuery = "SELECT NEW java.util.Vector(a.employees) FROM Address A";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameClassName("java.util.Vector", "java.util.ArrayList");

		String expected = "SELECT NEW java.util.ArrayList(a.employees) FROM Address A";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameClassName_2() throws Exception {

		String jpqlQuery = "SELECT NEW org.eclipse.persistence.Collection.Vector(a.employees) FROM Address A";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameClassName("org.eclipse.persistence.Collection", "org.eclipse.persistence.Type");

		String expected = "SELECT NEW org.eclipse.persistence.Type.Vector(a.employees) FROM Address A";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameClassName_3() throws Exception {

		String jpqlQuery = "SELECT NEW org.eclipse.persistence.Vector(a.employees) FROM Address A";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameClassName("org.eclipse.persistence.AbstractSession", "org.eclipse.persistence.session.Session");

		assertEquals(jpqlQuery, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameClassName_4() throws Exception {

		String jpqlQuery = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.FIRST_NAME";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameClassName("jpql.query.EnumType", "org.eclipse.persistence.Type");

		String expected = "SELECT p FROM Product p WHERE p.enumType = org.eclipse.persistence.Type.FIRST_NAME";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameClassName_5() throws Exception {

		String jpqlQuery = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameClassName("jpql.query.EnumType", "org.eclipse.persistence.Type");

		String expected = "SELECT p FROM Product p WHERE p.enumType = org.eclipse.persistence.Type.";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameClassName_6() throws Exception {

		String jpqlQuery = "SELECT p FROM Product p WHERE p.enumType = jpql.query.Employee.EnumType.FIRST_NAME";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameClassName("jpql.query.Employee", "org.eclipse.persistence.Type");

		String expected = "SELECT p FROM Product p WHERE p.enumType = org.eclipse.persistence.Type.EnumType.FIRST_NAME";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameClassName_7() throws Exception {

		String jpqlQuery = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.FIRST_NAME";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameClassName("jpql.query.Employee", "org.eclipse.persistence.Type");

		assertEquals(jpqlQuery, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameEntityName_1() throws Exception {

		String jpqlQuery = "SELECT a FROM Address A";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameEntityName("Address", "Employee");

		String expected = "SELECT a FROM Employee A";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameEntityName_2() throws Exception {

		String jpqlQuery = "SELECT a FROM Address A WHERE EXISTS(SELECT e FROM Employee e)";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameEntityName("Employee", "Manager");

		String expected = "SELECT a FROM Address A WHERE EXISTS(SELECT e FROM Manager e)";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameEnumConstant_1() throws Exception {

		String jpqlQuery = "UPDATE Employee SET name = javax.persistence.AccessType.FIELD";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameEnumConstant("javax.persistence.AccessType.FIELD", "javax.persistence.AccessType.PROPERTY");

		String expected = "UPDATE Employee SET name = javax.persistence.AccessType.PROPERTY";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameEnumConstant_2() throws Exception {

		String jpqlQuery = "UPDATE Employee e SET e.name = e.lName";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameEnumConstant("javax.persistence.AccessType.FIELD", "javax.persistence.AccessType.PROPERTY");

		assertEquals(jpqlQuery, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameFieldName_1() throws Exception {

		String jpqlQuery = "SELECT e.address.zip FROM Employee e";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameField("jpql.query.Employee", "address", "addr");

		String expected = "SELECT e.addr.zip FROM Employee e";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameFieldName_2() throws Exception {

		String jpqlQuery = "SELECT e.address.zipcode FROM Employee e";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameField("jpql.query.Address", "zip", "zipcode");

		assertEquals(jpqlQuery, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameVariable_1() throws Exception {

		String jpqlQuery = "SELECT a FROM Address A";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameVariable("a", "addr");

		String expected = "SELECT addr FROM Address addr";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameVariable_2() throws Exception {

		String jpqlQuery = "SELECT a FROM Address a JOIN a.employees ad";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameVariable("a", "addr");

		String expected = "SELECT addr FROM Address addr JOIN addr.employees ad";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameVariable_3() throws Exception {

		String jpqlQuery = "SELECT a FROM Address a WHERE EXISTS(SELECT e FROM a.employee)";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameVariable("a", "addr");

		String expected = "SELECT addr FROM Address addr WHERE EXISTS(SELECT e FROM addr.employee)";
		assertEquals(expected, refactoringTool.toActualText());
	}

	@Test
	public void test_RenameVariable_4() throws Exception {

		String jpqlQuery = "SELECT FROM Address";
		RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
		refactoringTool.renameVariable("a", "addr");

		assertEquals(jpqlQuery, refactoringTool.toActualText());
	}
}