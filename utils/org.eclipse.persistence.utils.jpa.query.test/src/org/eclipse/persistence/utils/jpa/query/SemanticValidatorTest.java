/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.util.List;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractValidator;
import org.eclipse.persistence.utils.jpa.query.spi.IEntity;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.junit.Test;

@SuppressWarnings("nls")
public abstract class SemanticValidatorTest extends AbstractValidatorTest
{
	@Override
	IQuery buildExternalQuery(String query) throws Exception
	{
		return entity().getNamedQuery(query);
	}

	@Override
	AbstractValidator buildValidationVisitor(IQuery query)
	{
		return new SemanticValidator(query);
	}

	private IEntity entity() throws Exception
	{
		return (IEntity) queryTestHelper().getPersistenceUnit().getManagedType("SemanticEntity");
	}

	@Test
	public void test_AbsExpression_InvalidNumericExpression_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE ABS(e.name)
		int startPosition = "SELECT e FROM Employee e WHERE ABS(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE ABS(e.name".length();

		List<QueryProblem> problems = validate("AbsExpression_InvalidExpression_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.AbsExpression_InvalidNumericExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbsExpression_InvalidNumericExpression_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE ABS(e.empId)
		List<QueryProblem> problems = validate("AbsExpression_InvalidExpression_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.AbsExpression_InvalidNumericExpression
		);
	}

	@Test
	public void test_AvgFunction_InvalidNumericExpression_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE ABS(e.name)
		int startPosition = "SELECT e FROM Employee e WHERE AVG(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE AVG(e.name".length();

		List<QueryProblem> problems = validate("AvgFunction_InvalidExpression_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.AvgFunction_InvalidNumericExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AvgFunction_InvalidNumericExpression_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE ABS(e.empId)
		List<QueryProblem> problems = validate("AvgFunction_InvalidExpression_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.AvgFunction_InvalidNumericExpression
		);
	}

	@Test
	public void test_BetweenExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.name BETWEEN 'A' AND 2
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.name BETWEEN 'A' AND 2".length();

		List<QueryProblem> problems = validate("BetweenExpression_WrongType_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.BetweenExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_BetweenExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.empId BETWEEN 'A' AND 2
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.empId BETWEEN 'A' AND 2".length();

		List<QueryProblem> problems = validate("BetweenExpression_WrongType_2");

		testHasProblem
		(
			problems,
			QueryProblemMessages.BetweenExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_BetweenExpression_WrongType_3() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.empId BETWEEN 'A' AND 'Z'
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.empId BETWEEN 'A' AND 'Z'".length();

		List<QueryProblem> problems = validate("BetweenExpression_WrongType_3");

		testHasProblem
		(
			problems,
			QueryProblemMessages.BetweenExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_BetweenExpression_WrongType_4() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.name BETWEEN 'A' AND :name
		List<QueryProblem> problems = validate("BetweenExpression_WrongType_4");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.BetweenExpression_WrongType
		);
	}

	@Test
	public void test_CollectionMemberDeclaration_InvalidCollectionExpression_1() throws Exception
	{
		// SELECT a FROM Address a, IN(a.zip) c
		int startPosition = "SELECT a FROM Address a, IN(".length();
		int endPosition   = "SELECT a FROM Address a, IN(a.zip".length();

		List<QueryProblem> problems = validate("CollectionMemberDeclaration_InvalidCollectionExpression_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.CollectionMemberDeclaration_InvalidCollectionExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CollectionMemberDeclaration_InvalidCollectionExpression_2() throws Exception
	{
		// SELECT a FROM Address a IN(a.customerList) c
		List<QueryProblem> problems = validate("CollectionMemberDeclaration_InvalidCollectionExpression_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.CollectionMemberDeclaration_InvalidCollectionExpression
		);
	}

	@Test
	public void test_CollectionMemberExpression_Embeddable_1() throws Exception
	{
		// SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.customerList
		List<QueryProblem> problems = validate("CollectionMemberExpression_Embeddable_1");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.CollectionMemberExpression_Embeddable
		);
	}

	@Test
	public void test_CollectionMemberExpression_Embeddable_2() throws Exception
	{
		// SELECT a FROM Address a, Customer c WHERE a.zip MEMBER OF a.customerList
		int startPosition = "SELECT a FROM Address a, Customer c WHERE ".length();
		int endPosition   = "SELECT a FROM Address a, Customer c WHERE a.zip".length();

		List<QueryProblem> problems = validate("CollectionMemberExpression_Embeddable_2");

		testHasProblem
		(
			problems,
			QueryProblemMessages.CollectionMemberExpression_Embeddable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CollectionMemberExpression_InvalidCollectionExpression_1() throws Exception
	{
		// SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.customerList
		List<QueryProblem> problems = validate("CollectionMemberExpression_InvalidCollectionExpression_1");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.CollectionMemberExpression_InvalidCollectionExpression
		);
	}

	@Test
	public void test_CollectionMemberExpression_InvalidCollectionExpression_2() throws Exception
	{
		// SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.state
		int startPosition = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF ".length();
		int endPosition   = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.state".length();

		List<QueryProblem> problems = validate("CollectionMemberExpression_InvalidCollectionExpression_2");

		testHasProblem
		(
			problems,
			QueryProblemMessages.CollectionMemberExpression_InvalidCollectionExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ComparisonExpression_WrongComparisonType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE NULLIF(e.working, TRUE) < LENGTH(e.name) + 2
		List<QueryProblem> problems = validate("ComparisonExpression_WrongComparisonType_1");

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE NULLIF(e.working, TRUE) < LENGTH(e.name) + 2".length();

		testHasProblem
		(
			problems,
			QueryProblemMessages.ComparisonExpression_WrongComparisonType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ComparisonExpression_WrongComparisonType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE COALESCE(e.working, :name) < LENGTH(e.name)
		List<QueryProblem> problems = validate("ComparisonExpression_WrongComparisonType_2");

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE COALESCE(e.working, :name) < LENGTH(e.name)".length();

		testHasProblem
		(
			problems,
			QueryProblemMessages.ComparisonExpression_WrongComparisonType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ComparisonExpression_WrongComparisonType_3() throws Exception
	{
		// SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < e.salary
		List<QueryProblem> problems = validate("ComparisonExpression_WrongComparisonType_3");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ComparisonExpression_WrongComparisonType
		);
	}

	@Test
	public void test_ComparisonExpression_WrongComparisonType_4() throws Exception
	{
		// SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < e.name
		List<QueryProblem> problems = validate("ComparisonExpression_WrongComparisonType_4");

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < e.name".length();

		testHasProblem
		(
			problems,
			QueryProblemMessages.ComparisonExpression_WrongComparisonType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ComparisonExpression_WrongComparisonType_5() throws Exception
	{
		// SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < (e.name)
		List<QueryProblem> problems = validate("ComparisonExpression_WrongComparisonType_5");

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < (e.name)".length();

		testHasProblem
		(
			problems,
			QueryProblemMessages.ComparisonExpression_WrongComparisonType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ComparisonExpression_WrongComparisonType_6() throws Exception
	{
		// SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < (e.salary)
		List<QueryProblem> problems = validate("ComparisonExpression_WrongComparisonType_6");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ComparisonExpression_WrongComparisonType
		);
	}

	@Test
	public void test_ComparisonExpression_WrongComparisonType_7() throws Exception
	{
		// SELECT e FROM Employee e WHERE ALL < e.salary
		List<QueryProblem> problems = validate("ComparisonExpression_WrongComparisonType_7");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ComparisonExpression_WrongComparisonType
		);
	}

	@Test
	public void test_ConcatExpression_FirstExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE CONCAT('JPQL', 'query') = 'JPQL query'
		List<QueryProblem> problems = validate("ConcatExpression_FirstExpression_WrongType_1");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ConcatExpression_FirstExpression_WrongType
		);
	}

	@Test
	public void test_ConcatExpression_FirstExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE CONCAT(e.name, 'query') = 'JPQL query'
		List<QueryProblem> problems = validate("ConcatExpression_FirstExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ConcatExpression_FirstExpression_WrongType
		);
	}

	@Test
	public void test_ConcatExpression_FirstExpression_WrongType_3() throws Exception
	{
		// SELECT e FROM Employee e WHERE CONCAT(e.empId, 'query') = 'JPQL query'
		int startPosition = "SELECT e FROM Employee e WHERE CONCAT(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE CONCAT(e.empId".length();

		List<QueryProblem> problems = validate("ConcatExpression_FirstExpression_WrongType_3");

		testHasProblem
		(
			problems,
			QueryProblemMessages.ConcatExpression_FirstExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConcatExpression_SecondExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE CONCAT('JPQL', 'query') = 'JPQL query'
		List<QueryProblem> problems = validate("ConcatExpression_SecondExpression_WrongType_1");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ConcatExpression_SecondExpression_WrongType
		);
	}

	@Test
	public void test_ConcatExpression_SecondExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE CONCAT('JPQL', e.name) = 'JPQL query'
		List<QueryProblem> problems = validate("ConcatExpression_SecondExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ConcatExpression_SecondExpression_WrongType
		);
	}

	@Test
	public void test_ConcatExpression_SecondExpression_WrongType_3() throws Exception
	{
		// SELECT e FROM Employee e WHERE CONCAT('JPQL', e.empId) = 'JPQL query'
		int startPosition = "SELECT e FROM Employee e WHERE CONCAT('JPQL', ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE CONCAT('JPQL', e.empId".length();

		List<QueryProblem> problems = validate("ConcatExpression_SecondExpression_WrongType_3");

		testHasProblem
		(
			problems,
			QueryProblemMessages.ConcatExpression_SecondExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConstructorExpression_UnknownType_1() throws Exception
	{
		// SELECT NEW Address(e.name) FROM Employee e
		int startPosition = "SELECT NEW ".length();
		int endPosition   = "SELECT NEW Address".length();

		List<QueryProblem> problems = validate("ConstructorExpression_UnknownType_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.ConstructorExpression_UnknownType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConstructorExpression_UnknownType_2() throws Exception
	{
		// SELECT NEW String(e.name) FROM Employee e
		int startPosition = "SELECT NEW ".length();
		int endPosition   = "SELECT NEW String".length();

		List<QueryProblem> problems = validate("ConstructorExpression_UnknownType_2");

		testHasProblem
		(
			problems,
			QueryProblemMessages.ConstructorExpression_UnknownType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConstructorExpression_UnknownType_3() throws Exception
	{
		// SELECT NEW jpql.query.Employee(e.name) FROM Employee e
		List<QueryProblem> problems = validate("ConstructorExpression_UnknownType_3");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ConstructorExpression_UnknownType
		);
	}

	@Test
	public void test_CountFunction_DistinctEmbeddable() throws Exception
	{
		// SELECT COUNT(DISTINCT a.zip) FROM Address a
		int startPosition = "SELECT COUNT(".length();
		int endPosition   = "SELECT COUNT(DISTINCT a.zip".length();

		List<QueryProblem> problems = validate("CountFunction_DistinctEmbeddable");

		testHasProblem
		(
			problems,
			QueryProblemMessages.CountFunction_DistinctEmbeddable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_InvalidCollectionExpression_1() throws Exception
	{
		// SELECT a FROM Address a WHERE a.state IS NOT EMPTY
		int startPosition = "SELECT a FROM Address a WHERE ".length();
		int endPosition   = "SELECT a FROM Address a WHERE a.state".length();

		List<QueryProblem> problems = validate("EmptyCollectionComparisonExpression_InvalidCollectionExpression_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.EmptyCollectionComparisonExpression_InvalidCollectionExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_InvalidCollectionExpression_2() throws Exception
	{
		// SELECT a FROM Address a WHERE a.customerList IS NOT EMPTY
		List<QueryProblem> problems = validate("EmptyCollectionComparisonExpression_InvalidCollectionExpression_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.EmptyCollectionComparisonExpression_InvalidCollectionExpression
		);
	}

	@Test
	public void test_EncapsulatedIdentificationVariableExpression_NotMapValued_1() throws Exception
	{
		// SELECT ENTRY(cust) FROM CodeAssist c JOIN c.customerMap cust
		List<QueryProblem> problems = validate("EncapsulatedIdentificationVariableExpression_NotMapValued_1");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.EncapsulatedIdentificationVariableExpression_NotMapValued
		);
	}

	@Test
	public void test_EncapsulatedIdentificationVariableExpression_NotMapValued_2() throws Exception
	{
		// SELECT ENTRY(c) FROM Address a JOIN a.customerList c
		List<QueryProblem> problems = validate("EncapsulatedIdentificationVariableExpression_NotMapValued_2");

		int startPosition = "SELECT ENTRY(".length();
		int endPosition   = "SELECT ENTRY(c".length();

		testHasProblem
		(
			problems,
			QueryProblemMessages.EncapsulatedIdentificationVariableExpression_NotMapValued,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IdentificationVariable_EntityName() throws Exception
	{
		// SELECT Employee FROM Address Employee
		int startPosition1 = "SELECT ".length();
		int endPosition1   = "SELECT Employee".length();
		int startPosition2 = "SELECT Employee FROM Address ".length();
		int endPosition2   = "SELECT Employee FROM Address Employee".length();

		List<QueryProblem> problems = validate("IdentificationVariable_EntityName");

		testHasProblems
		(
			problems,
			new String[] { QueryProblemMessages.SqrtExpression_WrongType,
			               QueryProblemMessages.SqrtExpression_WrongType },
			new int[] { startPosition1, startPosition2 },
			new int[] { endPosition1,   endPosition2 }
		);
	}

	@Test
	public void test_InExpression_InItem_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.name IN(e, ‘JPQL’)
		List<QueryProblem> problems = validate("InExpression_InItem_WrongType_1");

		int startPosition = "SELECT e FROM Employee e WHERE e.name IN(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.name IN(e, ‘JPQL’".length();

		testHasProblem
		(
			problems,
			QueryProblemMessages.InExpression_InItem_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InExpression_InItem_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.name IN(e.name, :name, ‘JPQL’)
		List<QueryProblem> problems = validate("InExpression_InItem_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.InExpression_InItem_WrongType
		);
	}

	@Test
	public void test_InExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.name IN(e.empId)
		List<QueryProblem> problems = validate("InExpression_WrongType_1");

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.name IN(e.empId)".length();

		testHasProblem
		(
			problems,
			QueryProblemMessages.InExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.name IN('JPQL', 'Java')
		List<QueryProblem> problems = validate("InExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.InExpression_WrongType
		);
	}

	@Test
	public void test_InExpression_WrongType_3() throws Exception
	{
		//SELECT e FROM Employee e WHERE e.name IN(?1, ?2, ?3)
		List<QueryProblem> problems = validate("InExpression_WrongType_3");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.InExpression_WrongType
		);
	}

	@Test
	public void test_LengthExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE LENGTH(e.empId) = 2
		int startPosition = "SELECT e FROM Employee e WHERE LENGTH(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LENGTH(e.empId".length();

		List<QueryProblem> problems = validate("LengthExpression_WrongType_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.LengthExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LengthExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE LENGTH(e.name) = 2
		List<QueryProblem> problems = validate("LengthExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.LengthExpression_WrongType
		);
	}

	@Test
	public void test_LowerExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE LOWER('JPQL') = 'jpql'
		List<QueryProblem> problems = validate("LowerExpression_WrongType_1");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.LowerExpression_WrongType
		);
	}

	@Test
	public void test_LowerExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE LOWER(e.name) = 'jpql'
		List<QueryProblem> problems = validate("LowerExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.LowerExpression_WrongType
		);
	}

	@Test
	public void test_LowerExpression_WrongType_3() throws Exception
	{
		// SELECT e FROM Employee e WHERE LOWER(e.empId) = 'jpql'
		int startPosition = "SELECT e FROM Employee e WHERE LOWER(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LOWER(e.empId".length();

		List<QueryProblem> problems = validate("LowerExpression_WrongType_3");

		testHasProblem
		(
			problems,
			QueryProblemMessages.LowerExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_FirstExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE MOD(e.name, 3) = 1
		int startPosition = "SELECT e FROM Employee e WHERE MOD(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE MOD(e.name".length();

		List<QueryProblem> problems = validate("ModExpression_FirstExpression_WrongType_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.ModExpression_FirstExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_FirstExpression_WrongType_2() throws Exception
	{
		// SELECT d FROM Dept d WHERE MOD(d.floorNumber, 3) = 1
		List<QueryProblem> problems = validate("ModExpression_FirstExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ModExpression_FirstExpression_WrongType
		);
	}

	@Test
	public void test_ModExpression_FirstExpression_WrongType_3() throws Exception
	{
		// SELECT d FROM Dept d, Product p WHERE MOD(p.quantity + d.floorNumber, 3) = 1
		List<QueryProblem> problems = validate("ModExpression_FirstExpression_WrongType_3");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ModExpression_FirstExpression_WrongType
		);
	}

	@Test
	public void test_ModExpression_SecondExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE MOD(3, e.name) = 1
		int startPosition = "SELECT e FROM Employee e WHERE MOD(3, ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE MOD(3, e.name".length();

		List<QueryProblem> problems = validate("ModExpression_SecondExpression_WrongType_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.ModExpression_SecondExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_SecondExpression_WrongType_2() throws Exception
	{
		// SELECT d FROM Dept d WHERE MOD(3, d.floorNumber) = 1
		List<QueryProblem> problems = validate("ModExpression_SecondExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ModExpression_SecondExpression_WrongType
		);
	}

	@Test
	public void test_ModExpression_SecondExpression_WrongType_3() throws Exception
	{
		// SELECT d FROM Dept d, Product p WHERE MOD(3, p.quantity + d.floorNumber) = 1
		List<QueryProblem> problems = validate("ModExpression_SecondExpression_WrongType_3");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.ModExpression_SecondExpression_WrongType
		);
	}

	@Test
	public void test_NullComparisonExpression_InvalidCollectionExpression_1() throws Exception
	{
		// SELECT a FROM Address a WHERE a.zip IS NOT NULL
		int startPosition = "SELECT a FROM Address a WHERE ".length();
		int endPosition   = "SELECT a FROM Address a WHERE a.zip".length();

		List<QueryProblem> problems = validate("NullComparisonExpression_InvalidCollectionExpression_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.NullComparisonExpression_InvalidCollectionExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullComparisonExpression_InvalidCollectionExpression_2() throws Exception
	{
		// SELECT a FROM Address a WHERE a.customerList IS NOT NULL
		List<QueryProblem> problems = validate("NullComparisonExpression_InvalidCollectionExpression_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.NullComparisonExpression_InvalidCollectionExpression
		);
	}

	@Test
	public void test_SelectStatement_SelectClauseHasNonAggregateFunctions_1() throws Exception
	{
		// SELECT e FROM Employee e HAVING COUNT(e) >= 5
		int startPosition = "SELECT ".length();
		int endPosition   = "SELECT e".length();

		List<QueryProblem> problems = validate("SelectStatement_SelectClauseHasNonAggregateFunctions_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.SelectStatement_SelectClauseHasNonAggregateFunctions,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SelectStatement_SelectClauseHasNonAggregateFunctions_2() throws Exception
	{
		// SELECT AVG(e.age), e FROM Employee e HAVING COUNT(e) >= 5
		int startPosition = "SELECT ".length();
		int endPosition   = "SELECT AVG(e.age), e".length();

		List<QueryProblem> problems = validate("SelectStatement_SelectClauseHasNonAggregateFunctions_2");

		testHasProblem
		(
			problems,
			QueryProblemMessages.SelectStatement_SelectClauseHasNonAggregateFunctions,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SelectStatement_SelectClauseHasNonAggregateFunctions_3() throws Exception
	{
		// SELECT AVG(e.age), COUNT(e) FROM Employee e HAVING COUNT(e) >= 5
		List<QueryProblem> problems = validate("SelectStatement_SelectClauseHasNonAggregateFunctions_3");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.SelectStatement_SelectClauseHasNonAggregateFunctions
		);
	}

	@Test
	public void test_SizeExpression_InvalidCollectionExpression_1() throws Exception
	{
		// SELECT a FROM Address a WHERE SIZE(a.customerList) = 2
		List<QueryProblem> problems = validate("SizeExpression_InvalidCollectionExpression_1");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.SizeExpression_InvalidCollectionExpression
		);
	}

	@Test
	public void test_SizeExpression_InvalidCollectionExpression_2() throws Exception
	{
		// SELECT a FROM Address a WHERE SIZE(a.zip) = 2
		int startPosition = "SELECT a FROM Address a WHERE SIZE(".length();
		int endPosition   = "SELECT a FROM Address a WHERE SIZE(a.zip".length();

		List<QueryProblem> problems = validate("SizeExpression_InvalidCollectionExpression_2");

		testHasProblem
		(
			problems,
			QueryProblemMessages.SizeExpression_InvalidCollectionExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SqrtExpression_WrongType_1() throws Exception
	{
		// SELECT a FROM Address a WHERE SQRT(a.zip) = 2
		int startPosition = "SELECT a FROM Address a WHERE SQRT(".length();
		int endPosition   = "SELECT a FROM Address a WHERE SQRT(a.zip".length();

		List<QueryProblem> problems = validate("SqrtExpression_WrongType_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.SqrtExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SqrtExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE SQRT(e.salary) = 2
		List<QueryProblem> problems = validate("SqrtExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.SqrtExpression_WrongType
		);
	}

	@Test
	public void test_SqrtExpression_WrongType_3() throws Exception
	{
		// SELECT e FROM Employee e WHERE SQRT(NULLIF(e.name, :name)) = 2
		int startPosition = "SELECT e FROM Employee e WHERE SQRT(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE SQRT(NULLIF(e.name, :name)".length();

		List<QueryProblem> problems = validate("SqrtExpression_WrongType_3");

		testHasProblem
		(
			problems,
			QueryProblemMessages.SqrtExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SqrtExpression_WrongType_4() throws Exception
	{
		// SELECT e FROM Employee e WHERE SQRT(NULLIF(e.salary, :salary)) = 2
		List<QueryProblem> problems = validate("SqrtExpression_WrongType_4");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.SqrtExpression_WrongType
		);
	}

	@Test
	public void test_SubstringExpression_FirstExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE SUBSTRING(e.empId, 1, 2) = 'P'
		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE SUBSTRING(e.empId".length();

		List<QueryProblem> problems = validate("SubstringExpression_FirstExpression_WrongType_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.SubstringExpression_FirstExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_FirstExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE SUBSTRING(e.name, 1, 2) = 'P'
		List<QueryProblem> problems = validate("SubstringExpression_FirstExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.SubstringExpression_FirstExpression_WrongType
		);
	}

	@Test
	public void test_SubstringExpression_SecondExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE SUBSTRING(e.name, 2 + e.name, 2) = 'P'
		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 2 + e.name".length();

		List<QueryProblem> problems = validate("SubstringExpression_SecondExpression_WrongType_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.SubstringExpression_SecondExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_SecondExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE SUBSTRING(e.name, 1, 2) = 'P'
		List<QueryProblem> problems = validate("SubstringExpression_SecondExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.SubstringExpression_SecondExpression_WrongType
		);
	}

	@Test
	public void test_SubstringExpression_ThirdExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE SUBSTRING(e.name, 2, 2 + e.name) = 'P'
		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 2, ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 2, 2 + e.name".length();

		List<QueryProblem> problems = validate("SubstringExpression_ThirdExpression_WrongType_1");

		testHasProblem
		(
			problems,
			QueryProblemMessages.SubstringExpression_ThirdExpression_WrongType,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_ThirdExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE SUBSTRING(e.name, 1, 2) = 'P'
		List<QueryProblem> problems = validate("SubstringExpression_ThirdExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.SubstringExpression_ThirdExpression_WrongType
		);
	}

	@Test
	public void test_UpperExpression_WrongType_1() throws Exception
	{
		// SELECT e FROM Employee e WHERE UPPER('jpql') = 'JPQL'
		List<QueryProblem> problems = validate("UpperExpression_WrongType_1");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.UpperExpression_WrongType
		);
	}

	@Test
	public void test_UpperExpression_WrongType_2() throws Exception
	{
		// SELECT e FROM Employee e WHERE UPPER(e.name) = 'JPQL'
		List<QueryProblem> problems = validate("UpperExpression_WrongType_2");

		testDoesNotHaveProblem
		(
			problems,
			QueryProblemMessages.UpperExpression_WrongType
		);
	}

	@Test
	public void test_UpperExpression_WrongType_3() throws Exception
	{
		// SELECT e FROM Employee e WHERE UPPER(e.empId) = 'JPQL'
		int startPosition = "SELECT e FROM Employee e WHERE UPPER(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE UPPER(e.empId".length();

		List<QueryProblem> problems = validate("UpperExpression_WrongType_3");

		testHasProblem
		(
			problems,
			QueryProblemMessages.UpperExpression_WrongType,
			startPosition,
			endPosition
		);
	}
}