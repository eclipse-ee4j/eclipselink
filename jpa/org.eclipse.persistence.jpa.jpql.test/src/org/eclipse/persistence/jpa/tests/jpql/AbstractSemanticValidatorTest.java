/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractSemanticValidator;
import org.eclipse.persistence.jpa.jpql.GenericSemanticValidatorHelper;
import org.eclipse.persistence.jpa.jpql.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.SemanticValidatorHelper;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaQuery;
import org.junit.Ignore;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The base definition of a test class used for testing a JPQL query semantically.
 *
 * @see AbstractSemanticValidator
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractSemanticValidatorTest extends AbstractValidatorTest {

	/**
	 * The instance of {@link JPQLQueryContext} that is used when running the unit-tests defined by
	 * the subclass. The instance is shared between each the unit-test.
	 */
	private JPQLQueryContext queryContext;

	/**
	 * Creates a new external form for the given JPQL query.
	 *
	 * @param jpqlQuery The JPQL query to wrap with a {@link IQuery}
	 * @return The external form for the JPQL query
	 * @throws Exception If a problem was encountered while access the application metadata
	 */
	protected IQuery buildQuery(String jpqlQuery) throws Exception {
		return new JavaQuery(getPersistenceUnit(), jpqlQuery);
	}

	/**
	 * Creates a new {@link JPQLQueryContext}.
	 *
	 * @return A instance of {@link JPQLQueryContext} that is used to cache information about the
	 * JPQL query being manipulated
	 */
	protected abstract JPQLQueryContext buildQueryContext();

	/**
	 * Creates a new {@link SemanticValidatorHelper}, which is used by {@link AbstractSemanticValidator}
	 * in order to access the JPA metadata. The unit-tests uses {@link GenericSemanticValidatorHelper},
	 * which simply accesses Hermes SPI and delegates the calls to {@link JPQLQueryContext}.
	 *
	 * @return A new instance of {@link GenericSemanticValidatorHelper}
	 */
	protected SemanticValidatorHelper buildSemanticValidatorHelper() {
		return new GenericSemanticValidatorHelper(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract AbstractSemanticValidator buildValidator();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractSemanticValidator getValidator() {
		return (AbstractSemanticValidator) super.getValidator();
	}

	protected abstract boolean isPathExpressionToCollectionMappingAllowed();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUpClass() throws Exception {
		queryContext = buildQueryContext();
		super.setUpClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		queryContext.dispose();
		super.tearDown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDownClass() throws Exception {
		queryContext = null;
		super.tearDownClass();
	}

	@Test
	public final void test_AbstractFromClause_WrongOrderOfIdentificationVariableDeclaration() throws Exception {

		String query = "SELECT e FROM Employee e JOIN a.people p, Address a";

		int startPosition = "SELECT e FROM Employee e JOIN ".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			AbstractFromClause_WrongOrderOfIdentificationVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotCollectionType_01() throws Exception {

		String query = "SELECT a FROM Address a JOIN a.customerList c";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			CollectionValuedPathExpression_NotCollectionType
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotCollectionType_02() throws Exception {

		String query = "SELECT a FROM Address a JOIN a.state c";
		int startPosition = "SELECT a FROM Address a JOIN ".length();
		int endPosition   = "SELECT a FROM Address a JOIN a.state".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			CollectionValuedPathExpression_NotCollectionType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotCollectionType_03() throws Exception {

		String query = "SELECT a FROM Address a WHERE a.state IS NOT EMPTY";
		int startPosition = "SELECT a FROM Address a WHERE ".length();
		int endPosition   = "SELECT a FROM Address a WHERE a.state".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			CollectionValuedPathExpression_NotCollectionType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotCollectionType_04() throws Exception {

		String query = "SELECT a FROM Address a WHERE a.customerList IS NOT EMPTY";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			CollectionValuedPathExpression_NotCollectionType
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotCollectionType_05() throws Exception {

		String query = "SELECT a FROM Address a, IN(a.zip) c";
		int startPosition = "SELECT a FROM Address a, IN(".length();
		int endPosition   = "SELECT a FROM Address a, IN(a.zip".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			CollectionValuedPathExpression_NotCollectionType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotCollectionType_06() throws Exception {

		String query = "SELECT a FROM Address a IN(a.customerList) c";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			CollectionValuedPathExpression_NotCollectionType
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotCollectionType_07() throws Exception {

		String query = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.customerList";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			CollectionValuedPathExpression_NotCollectionType
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotCollectionType_08() throws Exception {

		String query = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.state";
		int startPosition = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF ".length();
		int endPosition   = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.state".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			CollectionValuedPathExpression_NotCollectionType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotCollectionType_09() throws Exception {

		String query = "SELECT a FROM Address a WHERE SIZE(a.customerList) = 2";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			CollectionValuedPathExpression_NotCollectionType
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotCollectionType_10() throws Exception {

		String query = "SELECT a FROM Address a WHERE SIZE(a.zip) = 2";
		int startPosition = "SELECT a FROM Address a WHERE SIZE(".length();
		int endPosition   = "SELECT a FROM Address a WHERE SIZE(a.zip".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			CollectionValuedPathExpression_NotCollectionType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotResolvable_1() throws Exception {

		String query = "SELECT a FROM Address a JOIN a.customerList c";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			CollectionValuedPathExpression_NotResolvable
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotResolvable_2() throws Exception {

		String query = "SELECT a FROM Address a JOIN a.wrong c";
		int startPosition = "SELECT a FROM Address a JOIN ".length();
		int endPosition   = "SELECT a FROM Address a JOIN a.wrong".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			CollectionValuedPathExpression_NotResolvable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_01() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.address < 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.address".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_02() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.address > 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.address".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_03() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.address <= 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.address".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_04() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.address >= 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.address".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_05() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 < e.address";
		int startPosition = "SELECT e FROM Employee e WHERE 2 < ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE 2 < e.address".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_06() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 > e.address";
		int startPosition = "SELECT e FROM Employee e WHERE 2 > ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE 2 > e.address".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_07() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 <= e.address";
		int startPosition = "SELECT e FROM Employee e WHERE 2 <= ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE 2 <= e.address".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_08() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 >= e.address";
		int startPosition = "SELECT e FROM Employee e WHERE 2 >= ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE 2 >= e.address".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_09() throws Exception {

		String query = "SELECT e FROM Employee e, Address a WHERE a = e.address";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_10() throws Exception {

		String query = "SELECT e FROM Employee e, Address a WHERE a <> e.address";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_11() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.embeddedAddress > 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.embeddedAddress".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			ComparisonExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_AssociationField_12() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.managerEmployee > 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.managerEmployee".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			ComparisonExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_01() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.roomNumber < 2";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_02() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.roomNumber > 2";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_03() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.roomNumber <= 2";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_04() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.roomNumber >= 2";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_05() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 < e.roomNumber";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_06() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 > e.roomNumber";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_07() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 <= e.roomNumber";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_08() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 >= e.roomNumber";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_09() throws Exception {

		String query = "SELECT e FROM Employee e, Address a WHERE a = e.salary";
		int startPosition = "SELECT e FROM Employee e, Address a WHERE a = ".length();
		int endPosition   = "SELECT e FROM Employee e, Address a WHERE a = e.salary".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_BasicField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_10() throws Exception {

		String query = "SELECT e FROM Employee e, Address a WHERE a <> e.salary";
		int startPosition = "SELECT e FROM Employee e, Address a WHERE a <> ".length();
		int endPosition   = "SELECT e FROM Employee e, Address a WHERE a <> e.salary".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_BasicField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_11() throws Exception {

		String query = "SELECT e FROM Employee e, Address a WHERE e.salary = a";
		int startPosition = "SELECT e FROM Employee e, Address a WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e, Address a WHERE e.salary".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_BasicField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_BasicField_12() throws Exception {

		String query = "SELECT e FROM Employee e, Address a WHERE e.salary <> a";
		int startPosition = "SELECT e FROM Employee e, Address a WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e, Address a WHERE e.salary".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, ComparisonExpression_BasicField, startPosition, endPosition);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_01() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e > 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			ComparisonExpression_IdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_02() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e < 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			ComparisonExpression_IdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_03() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e >= 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			ComparisonExpression_IdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_04() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e <= 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			ComparisonExpression_IdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_05() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 < e";
		int startPosition = "SELECT e FROM Employee e WHERE 2 < ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE 2 < e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			ComparisonExpression_IdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_06() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 > e";
		int startPosition = "SELECT e FROM Employee e WHERE 2 > ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE 2 > e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			ComparisonExpression_IdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_07() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 <= e";
		int startPosition = "SELECT e FROM Employee e WHERE 2 <= ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE 2 <= e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			ComparisonExpression_IdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_08() throws Exception {

		String query = "SELECT e FROM Employee e WHERE 2 >= e";
		int startPosition = "SELECT e FROM Employee e WHERE 2 >= ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE 2 >= e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			ComparisonExpression_IdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_09() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e = e";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_10() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e <> e";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_11() throws Exception {

		String query = "SELECT LENGTH(e.name) AS n FROM Employee e WHERE n > 2";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_12() throws Exception {

		String query = "SELECT LENGTH(e.name) AS n FROM Employee e WHERE 2 < n";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_13() throws Exception {

		String query = "SELECT LENGTH(e.name) AS n FROM Employee e WHERE n = 2";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_ComparisonExpression_IdentificationVariable_14() throws Exception {

		String query = "SELECT LENGTH(e.name) AS n FROM Employee e WHERE 2 = n";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_IndexExpression_WrongVariable_1() throws Exception {

		String query = "SELECT c FROM Customer c JOIN c.aliases t WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			IndexExpression_WrongVariable
		);
	}

	@Test
	public final void test_IndexExpression_WrongVariable_2() throws Exception {

		String query = "SELECT c FROM Customer c, IN(c.aliases) t WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			IndexExpression_WrongVariable
		);
	}

	@Test
	public final void test_IndexExpression_WrongVariable_3() throws Exception {

		String query = "SELECT c FROM Customer c JOIN c.aliases t WHERE c.holder.name = 'John Doe' AND INDEX(c) BETWEEN 0 AND 9";
		int startPosition = "SELECT c FROM Customer c JOIN a.aliases t WHERE c.holder.name = 'John Doe' AND INDEX(".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			IndexExpression_WrongVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_IndexExpression_WrongVariable_4() throws Exception {

		String query = "SELECT c FROM Customer c, IN(c.aliases) t WHERE c.holder.name = 'John Doe' AND INDEX(c) BETWEEN 0 AND 9";
		int startPosition = "SELECT c FROM Customer c, IN(a.aliases) t WHERE c.holder.name = 'John Doe' AND INDEX(".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			IndexExpression_WrongVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_IndexExpression_WrongVariable_5() throws Exception {

		String query = "SELECT c FROM Customer c, IN(c.aliases) t WHERE EXISTS(SELECT e FROM Employee e WHERE c.holder.name = 'John Doe' AND INDEX(c) BETWEEN 0 AND 9)";
		int startPosition = "SELECT c FROM Customer c, IN(a.aliases) t WHERE EXISTS(SELECT e FROM Employee e WHERE c.holder.name = 'John Doe' AND INDEX(".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			IndexExpression_WrongVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_01() throws Exception {

		String query = "SELECT MIN(e.managerEmployee) FROM Employee e";
		int startPosition = "SELECT MIN(".length();
		int endPosition   = "SELECT MIN(e.managerEmployee".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_02() throws Exception {

		String query = "SELECT e.managerEmployee FROM Employee e";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			StateFieldPathExpression_AssociationField
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_03() throws Exception {

		String query = "SELECT e FROM Employee e WHERE UPPER(e.address) = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE UPPER(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE UPPER(e.address".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_04() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOWER(e.address) = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE LOWER(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LOWER(e.address".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_05() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH(e.address) = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE LENGTH(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LENGTH(e.address".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_06() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.address + 2";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.address".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_07() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD(e.address, 2) > 2";
		int startPosition = "SELECT e FROM Employee e WHERE MOD(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE MOD(e.address".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_08() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TRIM(e.address, 'JPQL') = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE TRIM(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE TRIM(e.address".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_09() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TRIM(BOTH '_' FROM e.address) = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE TRIM(BOTH '_' FROM ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE TRIM(BOTH '_' FROM e.address".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_10() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TRIM(e.department, 'JPQL') = 'JPQL'";

		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			StateFieldPathExpression_AssociationField
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_11() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH(e.department) = 2";

		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			StateFieldPathExpression_AssociationField
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_12() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOWER(e.department) = 2";

		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			StateFieldPathExpression_AssociationField
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_13() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOWER(UPPER(e.address)) = 2";
		int startPosition = "SELECT e FROM Employee e WHERE LOWER(UPPER(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LOWER(UPPER(e.address".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_AssociationField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_14() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name > 2";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			StateFieldPathExpression_AssociationField
		);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_19() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e <> e";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_StateFieldPathExpression_AssociationField_20() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e = e";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	@Ignore
	public final void test_StateFieldPathExpression_BasicField_01() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name IS NOT NULL";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.name".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_BasicField,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_BasicField_02() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.address IS NOT NULL";
		List<JPQLQueryProblem> problems = validate(query);
		testDoesNotHaveProblem(problems, StateFieldPathExpression_BasicField);
	}

	@Test
	public final void test_StateFieldPathExpression_BasicField_03() throws Exception {

		String query = "SELECT e.name FROM Employee e ORDER BY e.name";
		List<JPQLQueryProblem> problems = validate(query);
		testDoesNotHaveProblem(problems, StateFieldPathExpression_BasicField);
	}

	@Test
	public final void test_StateFieldPathExpression_BasicField_04() throws Exception {

		String query = "SELECT e.address FROM Employee e JOIN e.address";
		List<JPQLQueryProblem> problems = validate(query);
		testDoesNotHaveProblem(problems, StateFieldPathExpression_BasicField);
	}

	@Test
	public final void test_StateFieldPathExpression_BasicField_06() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.managerEmployee IS NOT NULL";
		List<JPQLQueryProblem> problems = validate(query);
		testDoesNotHaveProblem(problems, StateFieldPathExpression_BasicField);
	}

	@Test
	public final void test_StateFieldPathExpression_BasicField_07() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TYPE(e) IN (Exempt, Contractor)";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_StateFieldPathExpression_BasicField_08() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TYPE(e.name) IN (Exempt, Contractor)";
		int startPosition = "SELECT e FROM Employee e WHERE TYPE(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE TYPE(e.name".length();

		List<JPQLQueryProblem> problems = validate(query);
		testHasOnlyOneProblem(problems, StateFieldPathExpression_BasicField, startPosition, endPosition);
	}

	@Test
	public final void test_StateFieldPathExpression_BasicField_09() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TYPE(e.managerEmployee) IN (Exempt, Contractor)";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_StateFieldPathExpression_BasicField_10() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TYPE(e.embeddedAddress) IN (Exempt, Contractor)";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_StateFieldPathExpression_CollectionType() throws Exception {

		String query = "SELECT a.customerList FROM Address a";
		List<JPQLQueryProblem> problems = validate(query);

		if (isPathExpressionToCollectionMappingAllowed()) {
			testHasNoProblems(problems);
		}
		else {
			int startPosition = "SELECT ".length();
			int endPosition   = "SELECT a.customerList".length();

			testHasProblem(
				problems,
				StateFieldPathExpression_CollectionType,
				startPosition,
				endPosition
			);
		}
	}

	@Test
	public final void test_StateFieldPathExpression_InvalidEnumConstant_1() throws Exception {

		String query = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.FIRST_NAME";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			StateFieldPathExpression_InvalidEnumConstant
		);
	}

	@Test
	public final void test_StateFieldPathExpression_InvalidEnumConstant_2() throws Exception {

		String query = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.INVALID";
		int startPosition = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_InvalidEnumConstant,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_NoMapping_1() throws Exception {

		String query = "SELECT c.title FROM Customer c";
		int startPosition = "SELECT ".length();
		int endPosition   = "SELECT c.title".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_NoMapping,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_NoMapping_2() throws Exception {

		String query = "SELECT c.lastName FROM Customer c";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			StateFieldPathExpression_NoMapping
		);
	}

	@Test
	public final void test_StateFieldPathExpression_NotResolvable_1() throws Exception {

		String query = "SELECT e.name.wrong FROM Employee e";
		int startPosition = "SELECT ".length();
		int endPosition   = "SELECT e.name.wrong".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_NotResolvable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_StateFieldPathExpression_NotResolvable_2() throws Exception {

		String query = "UPDATE Employee WHERE ALL(SELECT e FROM managerEmployee e)";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			StateFieldPathExpression_NotResolvable
		);
	}

	@Test
	public final void test_StateFieldPathExpression_NotResolvable_3() throws Exception {

		String query = "UPDATE Employee WHERE ALL(SELECT e FROM phone e)";
		int startPosition = "UPDATE Employee WHERE ALL(SELECT e FROM ".length();
		int endPosition   = "UPDATE Employee WHERE ALL(SELECT e FROM phone".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			StateFieldPathExpression_NotResolvable,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_UpdateItem_RelationshipPathExpression_1() throws Exception {

		String query = "UPDATE Employee e SET e.name = 'JPQL'";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			UpdateItem_RelationshipPathExpression
		);
	}

	@Test
	public final void test_UpdateItem_RelationshipPathExpression_2() throws Exception {

		String query = "UPDATE Employee e SET e.managerEmployee = NULL";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			UpdateItem_RelationshipPathExpression
		);
	}

	@Test
	public final void test_UpdateItem_RelationshipPathExpression_3() throws Exception {

		String query = "UPDATE Employee e SET e.phoneNumbers.area = NULL";
		int startPosition = "UPDATE Employee e SET ".length();
		int endPosition   = "UPDATE Employee e SET e.phoneNumbers.area".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			UpdateItem_RelationshipPathExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_UpdateItem_RelationshipPathExpression_4() throws Exception {

		String query = "UPDATE Employee SET phoneNumbers.area = NULL";
		int startPosition = "UPDATE Employee SET ".length();
		int endPosition   = "UPDATE Employee SET phoneNumbers.area".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			UpdateItem_RelationshipPathExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_UpdateItem_RelationshipPathExpression_5() throws Exception {

		String query = "UPDATE Employee e SET e.department.invalid = TRUE";
		int startPosition = "UPDATE Employee e SET ".length();
		int endPosition   = "UPDATE Employee e SET e.department.invalid".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			UpdateItem_RelationshipPathExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_UpdateItem_RelationshipPathExpression_6() throws Exception {

		String query = "UPDATE Employee SET department.invalid = TRUE";
		int startPosition = "UPDATE Employee SET ".length();
		int endPosition   = "UPDATE Employee SET department.invalid".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			UpdateItem_RelationshipPathExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_UpdateItem_RelationshipPathExpression_7() throws Exception {

		String query = "UPDATE Employee e SET e.embeddedAddress.city = 'Cary'";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			UpdateItem_RelationshipPathExpression
		);
	}

	@Test
	public final void test_UpdateItem_RelationshipPathExpression_8() throws Exception {

		String query = "UPDATE Employee SET embeddedAddress.city = 'Cary'";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			UpdateItem_RelationshipPathExpression
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<JPQLQueryProblem> validate(String jpqlQuery, JPQLExpression jpqlExpression) throws Exception {

		// Update JPQLQueryContext, set JPQLExpression first since it was already parsed
		queryContext.setJPQLExpression(jpqlExpression);
		queryContext.setQuery(buildQuery(jpqlQuery));

		// Now validate semantically the JPQL query
		return super.validate(jpqlQuery, jpqlExpression);
	}
}