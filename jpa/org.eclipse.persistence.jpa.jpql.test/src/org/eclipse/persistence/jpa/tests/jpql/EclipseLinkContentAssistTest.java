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
		testHasNoProposals(jpqlQuery, position);
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
}