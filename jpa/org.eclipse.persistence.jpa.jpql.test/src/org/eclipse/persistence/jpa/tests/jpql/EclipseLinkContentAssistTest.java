/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This unit-test tests the JPQL content assist at various position within the JPQL query and with
 * complete and incomplete queries and make sure the EclipseLink additional support works correctly.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkContentAssistTest extends AbstractContentAssistTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isJoinFetchIdentifiable() {
		return getGrammar().getProviderVersion().equals(EclipseLinkJPQLGrammar2_4.VERSION);
	}

	@Test
	public void test_Func_01() {
		test_AbstractSingleEncapsulatedExpression_01(FUNC);
	}

	@Test
	public void test_Func_02() {
		test_AbstractSingleEncapsulatedExpression_02(FUNC);
	}

	@Test
	public void test_Func_03() {
		test_AbstractSingleEncapsulatedExpression_03(FUNC);
	}

	@Test
	public void test_Func_04() {
		test_AbstractSingleEncapsulatedExpression_04(FUNC);
	}

	@Test
	public void test_Func_05() {
		test_AbstractSingleEncapsulatedExpression_05(FUNC);
	}

	@Test
	public void test_Func_06() {
		test_AbstractSingleEncapsulatedExpression_06(FUNC);
	}

	@Test
	public void test_Func_07() {
		test_AbstractSingleEncapsulatedExpression_07(FUNC);
	}

	@Test
	public void test_Func_08() {
		test_AbstractSingleEncapsulatedExpression_08(FUNC);
	}

	@Test
	public void test_Func_09() {
		test_AbstractSingleEncapsulatedExpression_09(FUNC);
	}

	@Test
	public void test_Func_10() {
		test_AbstractSingleEncapsulatedExpression_10(FUNC);
	}

	@Test
	public void test_OrderByItem_NullOrdering_01() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public void test_OrderByItem_NullOrdering_02() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, ASC, DESC, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_03() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, ASC, DESC);
	}

	@Test
	public void test_OrderByItem_NullOrdering_04() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_05() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC".length();
		testHasOnlyTheseProposals(jpqlQuery, position, ASC, DESC);
	}

	@Test
	public void test_OrderByItem_NullOrdering_06() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC , e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC".length();
		testHasOnlyTheseProposals(jpqlQuery, position, ASC, DESC);
	}

	@Test
	public void test_OrderByItem_NullOrdering_07() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS FIRST";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_08() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_09() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = "SELECT e FROM Employee e ORDER BY e ASC ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_10() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = "SELECT e FROM Employee e ORDER BY e ASC N".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_11() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NU".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_12() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NUL".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_13() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULL".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_14() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULLS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_15() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULLS ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_16() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULLS L".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_17() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULLS LA".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_18() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_19() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_20() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC N".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_21() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NU".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_22() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NUL".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_23() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULL".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_24() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULLS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_25() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULLS ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_26() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULLS L".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_27() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULLS LA".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_OrderByItem_NullOrdering_28() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAST, e.name";
		int position = "SELECT e FROM Employee e ORDER BY e ASC NULLS LAS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULLS_FIRST, NULLS_LAST);
	}

	@Test
	public void test_CastExpression_01() {
		String jpqlQuery = "Select ";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_02() {
		String jpqlQuery = "Select c";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_03() {
		String jpqlQuery = "Select ca";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_04() {
		String jpqlQuery = "Select cas";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_05() {
		String jpqlQuery = "Select cast";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_06() {
		String jpqlQuery = "Select cast(e.firstName as char) From Employee e";
		int position = "Select c".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_07() {
		String jpqlQuery = "Select cast(e.firstName as char) From Employee e";
		int position = "Select ca".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_08() {
		String jpqlQuery = "Select cast(e.firstName as char) From Employee e";
		int position = "Select cas".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_09() {
		String jpqlQuery = "Select cast(e.firstName as char) From Employee e";
		int position = "Select cas".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_10() {
		String jpqlQuery = "Select cast(e.firstName as char) From Employee e";
		int position = "Select cast".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_11() {
		String jpqlQuery = "Select c";
		int position = "Select c".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_12() {
		String jpqlQuery = "Select ca";
		int position = "Select ca".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_13() {
		String jpqlQuery = "Select cas";
		int position = "Select cas".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_14() {
		String jpqlQuery = "Select cast";
		int position = "Select cast".length();
		testHasTheseProposals(jpqlQuery, position, CAST);
	}

	@Test
	public void test_CastExpression_22() {

		String jpqlQuery = "Select cast(e.firstName as char(3)) from Employee e where cast(e.firstName as char(3)) = 'Bob'";
		int position = "Select cast(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.scalarExpressionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CastExpression_23() {

		String jpqlQuery = "Select cast(e.firstName as char(3)) from Employee e where cast(e.firstName as char(3)) = 'Bob'";
		int position = "Select cast(e.firstName ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(AS);
		addAll(proposals, bnfAccessor.scalarExpressionAggregates());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CastExpression_24() {

		String jpqlQuery = "Select cast(e.firstName as char(3)) from Employee e where cast(e.firstName as char(3)) = 'Bob'";
		int position = "Select cast(e.firstName a".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(AS);
		addAll(proposals, filter(bnfAccessor.scalarExpressionAggregates(), "a"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CastExpression_25() {

		String jpqlQuery = "Select cast(e.firstName as char(3)) from Employee e where cast(e.firstName as char(3)) = 'Bob'";
		int position = "Select cast(e.firstName as".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(AS);
		addAll(proposals, filter(bnfAccessor.scalarExpressionAggregates(), "as"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CastExpression_26() {

		String jpqlQuery = "Select cast(e.firstName + as char(3)) from Employee e where cast(e.firstName as char(3)) = 'Bob'";
		int position = "Select cast(e.firstName + ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}
}