/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.junit.Ignore;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This unit-test tests the JPQL content assist at various position within the JPQL query and with
 * complete and incomplete queries and make sure the EclipseLink additional support works correctly.
 *
 * @version 2.4.1
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkContentAssistTest extends AbstractContentAssistTest {

	@Override
	protected void addClauseIdentifiers(String afterIdentifier,
	                                    String beforeIdentifier,
	                                    List<String> proposals) {

		super.addClauseIdentifiers(afterIdentifier, beforeIdentifier, proposals);

		if (afterIdentifier != SELECT &&
		    beforeIdentifier == null) {

			proposals.add(EXCEPT);
			proposals.add(INTERSECT);
			proposals.add(UNION);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isJoinFetchIdentifiable() {
		return getGrammar().getProviderVersion().equals(EclipseLinkJPQLGrammar2_4.VERSION);
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

	@Test
	public void test_ExtractExpression_01() {
		String jpqlQuery = "Select ";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_02() {
		String jpqlQuery = "Select e";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_03() {
		String jpqlQuery = "Select ex";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_04() {
		String jpqlQuery = "Select ext";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_05() {
		String jpqlQuery = "Select extr";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_06() {
		String jpqlQuery = "Select extra";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_07() {
		String jpqlQuery = "Select extrac";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_08() {
		String jpqlQuery = "Select extract";
		int position = "Select ".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_09() {
		String jpqlQuery = "Select extract(YEAR from e.hiringDate) From Employee e";
		int position = "Select e".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_10() {
		String jpqlQuery = "Select extract(YEAR from e.hiringDate) From Employee e";
		int position = "Select ex".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_11() {
		String jpqlQuery = "Select extract(YEAR from e.hiringDate) From Employee e";
		int position = "Select ext".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_12() {
		String jpqlQuery = "Select extract(YEAR from e.hiringDate) From Employee e";
		int position = "Select extr".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_13() {
		String jpqlQuery = "Select extract(YEAR from e.hiringDate) From Employee e";
		int position = "Select extra".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_14() {
		String jpqlQuery = "Select extract(YEAR from e.hiringDate) From Employee e";
		int position = "Select extrac".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_15() {
		String jpqlQuery = "Select extract(YEAR from e.hiringDate) From Employee e";
		int position = "Select extract".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_16() {
		String jpqlQuery = "Select e";
		int position = "Select e".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_17() {
		String jpqlQuery = "Select ex";
		int position = "Select ex".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_18() {
		String jpqlQuery = "Select ext";
		int position = "Select ext".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_19() {
		String jpqlQuery = "Select extr";
		int position = "Select extr".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_20() {
		String jpqlQuery = "Select extra";
		int position = "Select extra".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_21() {
		String jpqlQuery = "Select extrac";
		int position = "Select extrac".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_22() {
		String jpqlQuery = "Select extract";
		int position = "Select extract".length();
		testHasTheseProposals(jpqlQuery, position, EXTRACT);
	}

	@Test
	public void test_ExtractExpression_23() {

		String jpqlQuery = "Select extract(YEAR from e.hiringDate) from Employee e";
		int position = "Select extract(".length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public void test_ExtractExpression_24() {

		String jpqlQuery = "Select extract(YEAR from e.hiringDate) from Employee e";
		int position = "Select extract(YEAR ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_ExtractExpression_25() {

		String jpqlQuery = "Select extract(YEAR from) from Employee e";
		int position = "Select extract(YEAR ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(FROM);
		proposals.add("e");
		addAll(proposals, bnfAccessor.scalarExpressionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_ExtractExpression_26() {

		String jpqlQuery = "Select extract(YEAR from) from Employee e";
		int position = "Select extract(YEAR f".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(FROM);
		addAll(proposals, filter(bnfAccessor.scalarExpressionFunctions(), "f"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_ExtractExpression_27() {

		String jpqlQuery = "Select extract(YEAR from) from Employee e";
		int position = "Select extract(YEAR fr".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(FROM);
		addAll(proposals, filter(bnfAccessor.scalarExpressionFunctions(), "fr"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_ExtractExpression_28() {

		String jpqlQuery = "Select extract(YEAR from) from Employee e";
		int position = "Select extract(YEAR fro".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(FROM);
		addAll(proposals, filter(bnfAccessor.scalarExpressionFunctions(), "fro"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_ExtractExpression_29() {

		String jpqlQuery = "Select extract(YEAR from) from Employee e";
		int position = "Select extract(YEAR from".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(FROM);
		addAll(proposals, filter(bnfAccessor.scalarExpressionFunctions(), "from"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_ExtractExpression_30() {

		String jpqlQuery = "Select extract(YEAR from ) from Employee e";
		int position = "Select extract(YEAR from".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(FROM);
		addAll(proposals, filter(bnfAccessor.scalarExpressionFunctions(), "from"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_ExtractExpression_31() {

		String jpqlQuery = "Select extract(YEAR e.hiringDate) from Employee e";
		int position = "Select extract(YEAR ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(FROM);
		proposals.add("e");
		addAll(proposals, bnfAccessor.scalarExpressionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_ExtractExpression_32() {

		String jpqlQuery = "Select extract(YEAR e.hiringDate) from Employee e";
		int position = "Select extract(YEAR e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, filter(bnfAccessor.scalarExpressionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_ExtractExpression_33() {

		String jpqlQuery = "Select extract(from e.hiringDate) from Employee e";
		int position = "Select extract(f".length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_ExtractExpression_34() {

		String jpqlQuery = "Select extract(from e.hiringDate) from Employee e";
		int position = "Select extract(fr".length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_ExtractExpression_35() {

		String jpqlQuery = "Select extract(from e.hiringDate) from Employee e";
		int position = "Select extract(fro".length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_ExtractExpression_36() {

		String jpqlQuery = "Select extract(from e.hiringDate) from Employee e";
		int position = "Select extract(from".length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_ExtractExpression_37() {

		String jpqlQuery = "Select extract(YEAR from ) from Employee e";
		int position = "Select extract(YEAR from ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.scalarExpressionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
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

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			ASC, DESC,
			NULLS_FIRST, NULLS_LAST,
			UNION, INTERSECT, EXCEPT
		);
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

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			NULLS_FIRST, NULLS_LAST,
			UNION, INTERSECT, EXCEPT
		);
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
	public void test_RegexpExpression_02() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	public void test_RegexpExpression_03() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name R";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	public void test_RegexpExpression_04() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name RE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	public void test_RegexpExpression_05() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name REG";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	public void test_RegexpExpression_06() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name REGE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	public void test_RegexpExpression_07() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name REGEX";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	public void test_RegexpExpression_08() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name REGEXP";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	@Ignore
	public void test_RegexpExpression_09() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name R";
		int position = "SELECT e FROM Employee e WHERE e.name ".length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	@Ignore
	public void test_RegexpExpression_10() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name RE";
		int position = "SELECT e FROM Employee e WHERE e.name ".length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	@Ignore
	public void test_RegexpExpression_11() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name REG";
		int position = "SELECT e FROM Employee e WHERE e.name ".length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	@Ignore
	public void test_RegexpExpression_12() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name REGE";
		int position = "SELECT e FROM Employee e WHERE e.name ".length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	@Ignore
	public void test_RegexpExpression_13() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name REGEX";
		int position = "SELECT e FROM Employee e WHERE e.name ".length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	public void test_RegexpExpression_14() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name REGEXP";
		int position = "SELECT e FROM Employee e WHERE e.name ".length();
		testHasTheseProposals(jpqlQuery, position, REGEXP);
	}

	@Test
	public void test_RegexpExpression_15() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name REGEXP ";
		int position = "SELECT e FROM Employee e WHERE e.name REGEXP ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.patternValueFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_RegexpExpression_16() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name REGEXP e";
		int position = "SELECT e FROM Employee e WHERE e.name REGEXP e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, filter(bnfAccessor.patternValueFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_TableVariableDeclaration_01() {

		String jpqlQuery  = "SELECT e FROM Employee e, TABLE('EMP_ID')";
		int startPosition = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, startPosition);
	}

	@Test
	public void test_TableVariableDeclaration_02() {

		String jpqlQuery  = "SELECT e FROM Employee e, TABLE('EMP_ID') ";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, AS);
	}

	@Test
	public void test_TableVariableDeclaration_03() {

		String jpqlQuery  = "SELECT e FROM Employee e, TABLE('EMP_ID') AS";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, AS);
	}

	@Test
	public void test_TableVariableDeclaration_04() {

		String jpqlQuery  = "SELECT e FROM Employee e, TABLE('EMP_ID') AS";
		int startPosition = "SELECT e FROM Employee e, TABLE('EMP_ID') ".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, AS);
	}

	@Test
	public void test_TableVariableDeclaration_05() {

		String jpqlQuery  = "SELECT e FROM Employee e, TABLE('EMP_ID') AS";
		int startPosition = "SELECT e FROM Employee e, TABLE('EMP_ID') A".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, AS);
	}

	@Test
	public void test_TableVariableDeclaration_06() {

		String jpqlQuery  = "SELECT e FROM Employee e, TABLE('EMP_ID') A";
		int startPosition = "SELECT e FROM Employee e, TABLE('EMP_ID') ".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, AS);
	}

	@Test
	public void test_TableVariableDeclaration_07() {

		String jpqlQuery  = "SELECT e FROM Employee e, TABLE('EMP_ID') A";
		int startPosition = "SELECT e FROM Employee e, TABLE('EMP_ID') A".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, AS);
	}

	@Test
	public void test_TableVariableDeclaration_08() {

		String jpqlQuery  = "SELECT e FROM Employee e, TABLE('EMP_ID') E";
		int startPosition = "SELECT e FROM Employee e, TABLE('EMP_ID') ".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, AS);
	}

	@Test
	public void test_TableVariableDeclaration_09() {

		String jpqlQuery  = "SELECT e FROM Employee e, TABLE('EMP_ID') E";
		int startPosition = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, startPosition);
	}

	@Test
	public void test_TableVariableDeclaration_10() {

		String jpqlQuery  = "SELECT e FROM Employee e, TABLE('EMP_ID') Astérix";
		int startPosition = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, startPosition);
	}

	@Test
	public void test_UnionClause_001() {

		String jpqlQuery  = "SELECT e FROM Employee e ";
		int startPosition = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_002() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name ";
		int startPosition = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_003() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' ";
		int startPosition = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_004() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name ";
		int startPosition = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_005() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name ";
		int startPosition = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_006() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name ";
		int startPosition = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_007() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name HAVING e.age > 12 ";
		int startPosition = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_008() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name HAVING e.age > 12 ";
		int startPosition = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_009() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name HAVING e.age > 12 ";
		int startPosition = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_010() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name HAVING e.age > 12 ORDER BY e.name ";
		int startPosition = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_011() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name HAVING e.age > 12 ORDER BY e.name ";
		int startPosition = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_012() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name HAVING e.age > 12 ORDER BY e.name ";
		int startPosition = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name HAVING e.age > 12 ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_013() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name HAVING e.age > 12 ORDER BY e.name ";
		int startPosition = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			startPosition,
			ASC, DESC,
			NULLS_FIRST, NULLS_LAST,
			UNION, INTERSECT, EXCEPT
		);
	}

	@Test
	public void test_UnionClause_014() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION";
		int startPosition = "SELECT e FROM Employee e ".length();
		testHasTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_015() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION";
		int startPosition = "SELECT e FROM Employee e U".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_016() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION";
		int startPosition = "SELECT e FROM Employee e UN".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_017() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION";
		int startPosition = "SELECT e FROM Employee e UNI".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_018() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION";
		int startPosition = "SELECT e FROM Employee e UNIO".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_019() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION";
		int startPosition = "SELECT e FROM Employee e UNION".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_020() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL, SELECT);
	}

	@Test
	public void test_UnionClause_021() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION A";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_022() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION AL";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_023() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_024() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL ";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_025() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL";
		int startPosition = "SELECT e FROM Employee e UNION ".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_026() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION AL";
		int startPosition = "SELECT e FROM Employee e UNION A".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_027() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL";
		int startPosition = "SELECT e FROM Employee e UNION ALL".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_028() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL ";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_029() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL S";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_030() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SE";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_031() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SEL";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_032() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SELE";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_033() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SELEC";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_034() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SELECT";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_035() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e UNION ALL ".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_036() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e UNION ALL S".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_037() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e UNION ALL SE".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_038() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e UNION ALL SEL".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_039() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e UNION ALL SELE".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_040() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e UNION ALL SELEC".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_041() {

		String jpqlQuery  = "SELECT e FROM Employee e UNION ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e UNION ALL SELECT".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_042() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT";
		int startPosition = "SELECT e FROM Employee e ".length();
		testHasTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_043() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT";
		int startPosition = "SELECT e FROM Employee e I".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_044() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT";
		int startPosition = "SELECT e FROM Employee e IN".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_045() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT";
		int startPosition = "SELECT e FROM Employee e INT".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_046() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT";
		int startPosition = "SELECT e FROM Employee e INTE".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_047() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT";
		int startPosition = "SELECT e FROM Employee e INTER".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_048() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT";
		int startPosition = "SELECT e FROM Employee e INTERC".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_049() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT";
		int startPosition = "SELECT e FROM Employee e INTERCE".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_050() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT";
		int startPosition = "SELECT e FROM Employee e INTERCEP".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_051() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT";
		int startPosition = "SELECT e FROM Employee e INTERSECT".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_052() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL, SELECT);
	}

	@Test
	public void test_UnionClause_053() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT A";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_054() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT AL";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_055() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_056() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL ";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_057() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL";
		int startPosition = "SELECT e FROM Employee e INTERSECT ".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_058() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT AL";
		int startPosition = "SELECT e FROM Employee e INTERSECT A".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_059() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL";
		int startPosition = "SELECT e FROM Employee e INTERSECT ALL".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_060() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL ";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_061() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL S";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_062() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SE";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_063() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SEL";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_064() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SELE";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_065() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SELEC";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_066() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SELECT";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_067() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e INTERSECT ALL ".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_068() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e INTERSECT ALL S".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_069() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e INTERSECT ALL SE".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_070() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e INTERSECT ALL SEL".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_071() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e INTERSECT ALL SELE".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_072() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e INTERSECT ALL SELEC".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_073() {

		String jpqlQuery  = "SELECT e FROM Employee e INTERSECT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e INTERSECT ALL SELECT".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_074() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT";
		int startPosition = "SELECT e FROM Employee e ".length();
		testHasTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_075() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT";
		int startPosition = "SELECT e FROM Employee e E".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_076() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT";
		int startPosition = "SELECT e FROM Employee e EX".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_077() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT";
		int startPosition = "SELECT e FROM Employee e EXC".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_078() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT";
		int startPosition = "SELECT e FROM Employee e EXCE".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_079() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT";
		int startPosition = "SELECT e FROM Employee e EXCEP".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_080() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT";
		int startPosition = "SELECT e FROM Employee e EXCEPT".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, UNION, INTERSECT, EXCEPT);
	}

	@Test
	public void test_UnionClause_081() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL, SELECT);
	}

	@Test
	public void test_UnionClause_082() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT A";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_083() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT AL";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_084() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_085() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL ";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_086() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL";
		int startPosition = "SELECT e FROM Employee e EXCEPT ".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_087() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT AL";
		int startPosition = "SELECT e FROM Employee e EXCEPT A".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_088() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL";
		int startPosition = "SELECT e FROM Employee e EXCEPT ALL".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, ALL);
	}

	@Test
	public void test_UnionClause_089() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL ";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_090() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL S";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_091() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SE";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_092() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SEL";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_093() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SELE";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_094() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SELEC";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_095() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SELECT";
		int startPosition = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_096() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e EXCEPT ALL ".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_097() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e EXCEPT ALL S".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_098() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e EXCEPT ALL SE".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_099() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e EXCEPT ALL SEL".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_100() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e EXCEPT ALL SELE".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_101() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e EXCEPT ALL SELEC".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_102() {

		String jpqlQuery  = "SELECT e FROM Employee e EXCEPT ALL SELECT ";
		int startPosition = "SELECT e FROM Employee e EXCEPT ALL SELECT".length();
		testHasOnlyTheseProposals(jpqlQuery, startPosition, SELECT);
	}

	@Test
	public void test_UnionClause_103() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name, e";
		int startPosition = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		addAll(proposals, filter(bnfAccessor.groupByItemFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, startPosition, proposals);
	}

	@Test
	public void test_UnionClause_104() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name <> 'JPQL' GROUP BY e.name, e e";
		int startPosition = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		addAll(proposals, filter(bnfAccessor.groupByItemFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, startPosition, proposals);
	}
}