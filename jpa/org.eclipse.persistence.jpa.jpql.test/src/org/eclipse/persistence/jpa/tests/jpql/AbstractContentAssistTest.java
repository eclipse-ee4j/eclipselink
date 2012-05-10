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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.DefaultContentAssistProposals;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionRegistry;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaQuery;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryBNFAccessor;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.junit.Assert.*;

/**
 * This unit-test tests the JPQL content assist at various position within the JPQL query and with
 * complete and incomplete queries.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"})
public abstract class AbstractContentAssistTest extends JPQLCoreTest {

	protected JPQLQueryBNFAccessor bnfAccessor;
	@JPQLQueryHelperTestHelper
	private AbstractJPQLQueryHelper queryHelper;
	private JavaQuery virtualQuery;

	protected final List<String> addAll(List<String> items1, Iterable<String> items2) {
		for (String item2 : items2) {
			items1.add(item2);
		}
		return items1;
	}

	protected final void addIdentifiers(List<String> identifiers, String... expressionFactoryIds) {

		ExpressionRegistry registry = getGrammar().getExpressionRegistry();

		for (String id : expressionFactoryIds) {
			ExpressionFactory factory = registry.getExpressionFactory(id);

			for (String identifier : factory.identifiers()) {
				identifiers.add(identifier);
			}
		}
	}

	protected final DefaultContentAssistProposals buildContentAssistProposals(String actualQuery,
	                                                                          int position) {

		queryHelper.setQuery(buildQuery(actualQuery));
		return (DefaultContentAssistProposals) queryHelper.buildContentAssistProposals(position);
	}

	protected final IQuery buildQuery(String jpqlQuery) {
		virtualQuery.setExpression(jpqlQuery);
		return virtualQuery;
	}

	protected final Iterable<IEntity> entities() throws Exception {
		return getPersistenceUnit().entities();
	}

	protected final Iterable<String> entityNames() throws Exception {
		List<String> names = new ArrayList<String>();
		for (IEntity entity : entities()) {
			names.add(entity.getName());
		}
		return names;
	}

	protected final Iterable<String> filter(Iterable<String> proposals, String startsWith) {

		List<String> results = new ArrayList<String>();
		startsWith = startsWith.toUpperCase();

		for (String proposal : proposals) {
			if (proposal.toUpperCase().startsWith(startsWith)) {
				results.add(proposal);
			}
		}

		return results;
	}

	protected JPQLGrammar getGrammar() {
		return queryHelper.getGrammar();
	}

	protected JPQLQueryBNFAccessor getQueryBNFAccessor() {
		return bnfAccessor;
	}

	protected AbstractJPQLQueryHelper getQueryHelper() {
		return queryHelper;
	}

	protected JavaQuery getVirtualQuery() {
		return virtualQuery;
	}

	protected final boolean isEclipseLink2_0() {
		return jpqlGrammar().getProviderVersion() == EclipseLinkJPQLGrammar2_0.VERSION;
	}

	protected final boolean isEclipseLink2_1() {
		return jpqlGrammar().getProviderVersion() == EclipseLinkJPQLGrammar2_1.VERSION;
	}

	protected boolean isEclipseLink2_1OrLater() {
		return isEclipseLink2_1() ||
		       isEclipseLink2_2() ||
		       isEclipseLink2_3() ||
		       isEclipseLink2_4();
	}

	protected final boolean isEclipseLink2_2() {
		return jpqlGrammar().getProviderVersion() == EclipseLinkJPQLGrammar2_2.VERSION;
	}

	protected final boolean isEclipseLink2_3() {
		return jpqlGrammar().getProviderVersion() == EclipseLinkJPQLGrammar2_3.VERSION;
	}

	protected final boolean isEclipseLink2_4() {
		return jpqlGrammar().getProviderVersion() == EclipseLinkJPQLGrammar2_4.VERSION;
	}

	protected boolean isEclipseLink2_4OrLater() {
		return isEclipseLink2_4();
	}

	/**
	 * Determines whether a <code><b>JOIN FETCH</b></code> expression can be identified with an
	 * identification variable.
	 *
	 * @return <code>true</code> if it can be identified by an identification variable; <code>false</code>
	 * otherwise
	 */
	abstract boolean isJoinFetchIdentifiable();

	protected final boolean isJPA1_0() {
		return jpqlGrammar().getJPAVersion() == JPAVersion.VERSION_1_0;
	}

	protected final boolean isJPA2_0() {
		return jpqlGrammar().getJPAVersion() == JPAVersion.VERSION_2_0;
	}

	protected final boolean isJPA2_1() {
		return jpqlGrammar().getJPAVersion() == JPAVersion.VERSION_2_1;
	}

	protected final Iterable<String> joinIdentifiers() {
		List<String> proposals = new ArrayList<String>();
		proposals.add(INNER_JOIN);
		proposals.add(INNER_JOIN_FETCH);
		proposals.add(JOIN);
		proposals.add(JOIN_FETCH);
		proposals.add(LEFT_JOIN);
		proposals.add(LEFT_JOIN_FETCH);
		proposals.add(LEFT_OUTER_JOIN);
		proposals.add(LEFT_OUTER_JOIN_FETCH);
		return proposals;
	}

	protected final Iterable<String> joinOnlyIdentifiers() {
		List<String> proposals = new ArrayList<String>();
		proposals.add(INNER_JOIN);
		proposals.add(JOIN);
		proposals.add(LEFT_JOIN);
		proposals.add(LEFT_OUTER_JOIN);
		return proposals;
	}

	protected JPQLGrammar jpqlGrammar() {
		return queryHelper.getGrammar();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUpClass() throws Exception {
		super.setUpClass();
		virtualQuery = new JavaQuery(getPersistenceUnit(), null);
		bnfAccessor  = new JPQLQueryBNFAccessor(getGrammar().getExpressionRegistry());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		queryHelper.dispose();
		virtualQuery.setExpression(null);
		super.tearDown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDownClass() throws Exception {
		bnfAccessor  = null;
		queryHelper  = null;
		virtualQuery = null;
		super.tearDownClass();
	}

	@Test
	public void test_Abs_01() {
		test_AbstractSingleEncapsulatedExpression_01(ABS);
	}

	@Test
	public void test_Abs_02() {
		test_AbstractSingleEncapsulatedExpression_02(ABS);
	}

	@Test
	public void test_Abs_03() {
		test_AbstractSingleEncapsulatedExpression_03(ABS);
	}

	@Test
	public void test_Abs_04() {
		test_AbstractSingleEncapsulatedExpression_04(ABS);
	}

	@Test
	public void test_Abs_05() {
		test_AbstractSingleEncapsulatedExpression_05(ABS);
	}

	@Test
	public void test_Abs_06() {
		test_AbstractSingleEncapsulatedExpression_06(ABS);
	}

	@Test
	public void test_Abs_07() {
		test_AbstractSingleEncapsulatedExpression_07(ABS);
	}

	@Test
	public void test_Abs_08() {
		test_AbstractSingleEncapsulatedExpression_08(ABS);
	}

	@Test
	public void test_Abs_09() {
		test_AbstractSingleEncapsulatedExpression_09(ABS);
	}

	@Test
	public void test_Abs_10() {
		test_AbstractSingleEncapsulatedExpression_10(ABS);
	}

	@Test
	public void test_AbstractSchemaName_01() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e";
		int position = "SELECT e FROM ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public void test_AbstractSchemaName_02() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e";
		int position = "SELECT e FROM E".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "E"));
	}

	@Test
	public void test_AbstractSchemaName_03() throws Exception {
		String jpqlQuery = "SELECT AVG(e.age) FROM ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public void test_AbstractSchemaName_04() throws Exception {
		String jpqlQuery = "SELECT e FROM ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public void test_AbstractSchemaName_05() throws Exception {
		String jpqlQuery = "SELECT e FROM E";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "E"));
	}

	@Test
	public void test_AbstractSchemaName_06() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, ";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		addAll(proposals, entityNames());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AbstractSchemaName_07() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, Address a";
		int position = "SELECT e FROM ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public void test_AbstractSchemaName_08() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public void test_AbstractSchemaName_09() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, Address a ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public void test_AbstractSchemaName_10() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = "SELECT e FROM ".length();
		testHasTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public void test_AbstractSchemaName_11() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = "SELECT e FROM E".length();
		testHasTheseProposals(jpqlQuery, position, filter(entityNames(), "f"));
	}

	@Test
	public void test_AbstractSchemaName_12() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = "SELECT e FROM Employee".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "Employee"));
	}

	@Test
	public void test_AbstractSchemaName_13() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, Address a ";
		int position = "SELECT e FROM Employee e, ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		addAll(proposals, entityNames());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AbstractSchemaName_14() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, Address a ";
		int position = "SELECT e FROM Employee e, A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "A"));
	}

	@Test
	public void test_AbstractSchemaName_15() {
		String jpqlQuery = "SELECT e FROM Employee e, I";
		int position = "SELECT e FROM Employee e, I".length();
		testHasOnlyTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public void test_AbstractSchemaName_16() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, A";
		int position = "SELECT e FROM Employee e, A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "A"));
	}

	@Test
	public void test_AbstractSchemaName_17() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e";
		int position = "SELECT e FROM Employee ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		addAll(proposals, entityNames());

		testDoesNotHaveTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AbstractSchemaName_18() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, , Address a";
		int position = "SELECT e FROM Employee e,".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		addAll(proposals, entityNames());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AbstractSchemaName_19() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, , Address a";
		int position = "SELECT e FROM Employee e, ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		addAll(proposals, entityNames());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AbstractSchemaName_20() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e,, Address a";
		int position = "SELECT e FROM Employee e,".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		addAll(proposals, entityNames());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	protected final void test_AbstractSingleEncapsulatedExpression_01(String identifier) {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, identifier);
	}

	protected final void test_AbstractSingleEncapsulatedExpression_02(String identifier) {
		String jpqlQuery = "SELECT e FROM Employee e WHERE " + identifier.charAt(0);
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, identifier);
	}

	protected final void test_AbstractSingleEncapsulatedExpression_03(String identifier) {
		String jpqlQuery = "SELECT e FROM Employee e WHERE " + identifier.charAt(0);
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, identifier);
	}

	protected final void test_AbstractSingleEncapsulatedExpression_04(String identifier) {
		String jpqlQuery = "SELECT e FROM Employee e WHERE " + identifier.substring(0, 2);
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, identifier);
	}

	protected final void test_AbstractSingleEncapsulatedExpression_05(String identifier) {
		String jpqlQuery = "SELECT e FROM Employee e WHERE " + identifier;
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, identifier);
	}

	protected final void test_AbstractSingleEncapsulatedExpression_06(String identifier) {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (" + identifier.charAt(0);
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, identifier);
	}

	protected final void test_AbstractSingleEncapsulatedExpression_07(String identifier) {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (" + identifier.charAt(0);
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, identifier);
	}

	protected final void test_AbstractSingleEncapsulatedExpression_08(String identifier) {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (" + identifier.charAt(0) + ")";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, identifier);
	}

	protected final void test_AbstractSingleEncapsulatedExpression_09(String identifier) {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (" + identifier.substring(0, identifier.length() - 1) + ")";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, identifier);
	}

	protected final void test_AbstractSingleEncapsulatedExpression_10(String identifier) {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (" + identifier + ")";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, identifier);
	}

	@Test
	public void test_All_01() {
		test_AbstractSingleEncapsulatedExpression_01(ALL);
	}

	@Test
	public void test_All_02() {
		test_AbstractSingleEncapsulatedExpression_02(ALL);
	}

	@Test
	public void test_All_03() {
		test_AbstractSingleEncapsulatedExpression_03(ALL);
	}

	@Test
	public void test_All_04() {
		test_AbstractSingleEncapsulatedExpression_04(ALL);
	}

	@Test
	public void test_All_05() {
		test_AbstractSingleEncapsulatedExpression_05(ALL);
	}

	@Test
	public void test_All_06() {
		test_AbstractSingleEncapsulatedExpression_06(ALL);
	}

	@Test
	public void test_All_07() {
		test_AbstractSingleEncapsulatedExpression_07(ALL);
	}

	@Test
	public void test_All_08() {
		test_AbstractSingleEncapsulatedExpression_08(ALL);
	}

	@Test
	public void test_All_09() {
		test_AbstractSingleEncapsulatedExpression_09(ALL);
	}

	@Test
	public void test_All_10() {
		test_AbstractSingleEncapsulatedExpression_10(ALL);
	}

	@Test
	public void test_AllOrAny_All_1() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE AL";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ALL);
	}

	@Test
	public void test_AllOrAny_All_2() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ALL";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ALL);
	}

	@Test
	public void test_AllOrAny_All_3() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (AL)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, ALL);
	}

	@Test
	public void test_AllOrAny_All_4() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (ALL)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, ALL);
	}

	@Test
	public void test_AllOrAny_Any_1() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE AN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ANY);
	}

	@Test
	public void test_AllOrAny_Any_2() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ANY";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ANY);
	}

	@Test
	public void test_AllOrAny_Any_3() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (AN)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, ANY);
	}

	@Test
	public void test_AllOrAny_Any_4() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (ANY)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, ANY);
	}

	@Test
	public void test_AllOrAny_AnyOrAny_1() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ANY);
	}

	@Test
	public void test_AllOrAny_AnyOrAny_2() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE A";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ANY);
	}

	@Test
	public void test_AllOrAny_AnyOrAny_3() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ANY);
	}

	@Test
	public void test_AllOrAny_AnyOrAny_4() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (A";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, ANY);
	}

	@Test
	public void test_AllOrAny_AnyOrAny_5() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (A)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, ANY);
	}

	@Test
	public void test_AllOrAny_Some_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE S";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE S";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SOM";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SOME";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (S";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (SO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (SOM";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (SOME";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (S)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (SO)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (SOM)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_AllOrAny_Some_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (SOME)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, SOME);
	}

	@Test
	public void test_Any_01() {
		test_AbstractSingleEncapsulatedExpression_01(ANY);
	}

	@Test
	public void test_Any_02() {
		test_AbstractSingleEncapsulatedExpression_02(ANY);
	}

	@Test
	public void test_Any_03() {
		test_AbstractSingleEncapsulatedExpression_03(ANY);
	}

	@Test
	public void test_Any_04() {
		test_AbstractSingleEncapsulatedExpression_04(ANY);
	}

	@Test
	public void test_Any_05() {
		test_AbstractSingleEncapsulatedExpression_05(ANY);
	}

	@Test
	public void test_Any_06() {
		test_AbstractSingleEncapsulatedExpression_06(ANY);
	}

	@Test
	public void test_Any_07() {
		test_AbstractSingleEncapsulatedExpression_07(ANY);
	}

	@Test
	public void test_Any_08() {
		test_AbstractSingleEncapsulatedExpression_08(ANY);
	}

	@Test
	public void test_Any_09() {
		test_AbstractSingleEncapsulatedExpression_09(ANY);
	}

	@Test
	public void test_Any_10() {
		test_AbstractSingleEncapsulatedExpression_10(ANY);
	}

	@Test
	public void test_As_01() {
		String jpqlQuery = "SELECT o FROM Countries ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_As_02() {
		String jpqlQuery = "SELECT o FROM Countries o";
		int position = "SELECT o FROM Countries ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_As_03() {
		String jpqlQuery = "SELECT o FROM Countries a";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_As_04() {
		String jpqlQuery = "SELECT o FROM Countries o ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_As_05() {
		String jpqlQuery = "SELECT o FROM Countries A o";
		int position = "SELECT o FROM Countries A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_As_06() {
		String jpqlQuery = "SELECT o FROM Countries AS o";
		int position = "SELECT o FROM Countries A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_As_07() {
		String jpqlQuery = "SELECT o FROM Countries AS o";
		int position = "SELECT o FROM Countries AS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_As_08() {
		String jpqlQuery = "SELECT o FROM Countries AS o";
		int position = "SELECT o FROM Countries AS ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_As_09() {
		String jpqlQuery = "SELECT ABS(a.city)  FROM Address AS a";
		int position = "SELECT ABS(a.city) ".length();
		testHasTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_As_10() {
		String jpqlQuery = "SELECT ABS(a.city) AS FROM Address AS a";
		int position = "SELECT ABS(a.city) AS ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_AvgFunction_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AVG);
	}

	@Test
	public void test_AvgFunction_02() {
		String jpqlQuery = "SELECT A";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AVG);
	}

	@Test
	public void test_AvgFunction_03() {
		String jpqlQuery = "SELECT AV";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AVG);
	}

	@Test
	public void test_AvgFunction_04() {
		String jpqlQuery = "SELECT AVG";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AVG);
	}

	@Test
	public void test_AvgFunction_05() {

		String jpqlQuery = "SELECT AVG(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AvgFunction_06() {

		String jpqlQuery = "SELECT AVG() From Employee e";
		int position = "SELECT AVG(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AvgFunction_07() {

		String jpqlQuery = "SELECT AVG(DISTINCT ) From Employee e";
		int position = "SELECT AVG(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AvgFunction_08() {
		String jpqlQuery = "SELECT AVG(D ) From Employee e";
		int position = "SELECT AVG(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_AvgFunction_09() {
		String jpqlQuery = "SELECT AVG(DI ) From Employee e";
		int position = "SELECT AVG(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_AvgFunction_10() {
		String jpqlQuery = "SELECT AVG(DIS ) From Employee e";
		int position = "SELECT AVG(DIS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_AvgFunction_11() {
		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_AvgFunction_12() {
		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_AvgFunction_13() {
		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_AvgFunction_14() {

		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AvgFunction_15() {

		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AvgFunction_16() {
		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee emp";
		int position = "SELECT AVG(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AvgFunction_17() {

		String jpqlQuery = "SELECT AVG() From Employee emp";
		int position = "SELECT AVG(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AvgFunction_18() {

		String jpqlQuery = "SELECT AVG(e) From Employee emp";
		int position = "SELECT AVG(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AvgFunction_19() {
		String jpqlQuery = "SELECT AVG(em) From Employee emp";
		int position = "SELECT AVG(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_AvgFunction_20() {
		String jpqlQuery = "SELECT AVG(emp) From Employee emp";
		int position = "SELECT AVG(emp".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_AvgFunction_21() {

		String jpqlQuery = "SELECT AVG(emp) From Employee emp";
		int position = "SELECT AVG(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AvgFunction_22() {
		String jpqlQuery = "SELECT AVG(emp) From Employee emp";
		int position = "SELECT AVG(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_AvgFunction_23() {

		String jpqlQuery = "SELECT AVG( From Employee emp";
		int position = "SELECT AVG(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_AvgFunction_24() {

		String jpqlQuery = "SELECT AVG(e From Employee emp";
		int position = "SELECT AVG(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Between_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public void test_Between_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, BETWEEN, NOT_BETWEEN);
	}

	@Test
	public void test_Between_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age B";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public void test_Between_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public void test_Between_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BET";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public void test_Between_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETW";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public void test_Between_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public void test_Between_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public void test_Between_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN, NOT_BETWEEN);
	}

	@Test
	public void test_Between_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age NOT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_BETWEEN);
	}

	@Test
	public void test_Between_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age NOT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_BETWEEN);
	}

	@Test
	public void test_Between_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age NOT B";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_BETWEEN);
	}

	@Test
	public void test_Between_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AND);
	}

	@Test
	public void test_Between_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 A";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AND);
	}

	@Test
	public void test_Between_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 AN";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AND);
	}

	@Test
	public void test_Between_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 AND";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AND);
	}

	@Test
	public void test_Between_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 AND ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AND);
	}

	@Test
	public void test_Coalesce_01() {
		test_AbstractSingleEncapsulatedExpression_01(COALESCE);
	}

	@Test
	public void test_Coalesce_02() {
		test_AbstractSingleEncapsulatedExpression_02(COALESCE);
	}

	@Test
	public void test_Coalesce_03() {
		test_AbstractSingleEncapsulatedExpression_03(COALESCE);
	}

	@Test
	public void test_Coalesce_04() {
		test_AbstractSingleEncapsulatedExpression_04(COALESCE);
	}

	@Test
	public void test_Coalesce_05() {
		test_AbstractSingleEncapsulatedExpression_05(COALESCE);
	}

	@Test
	public void test_Coalesce_06() {
		test_AbstractSingleEncapsulatedExpression_06(COALESCE);
	}

	@Test
	public void test_Coalesce_07() {
		test_AbstractSingleEncapsulatedExpression_07(COALESCE);
	}

	@Test
	public void test_Coalesce_08() {
		test_AbstractSingleEncapsulatedExpression_08(COALESCE);
	}

	@Test
	public void test_Coalesce_09() {
		test_AbstractSingleEncapsulatedExpression_09(COALESCE);
	}

	@Test
	public void test_Coalesce_10() {
		test_AbstractSingleEncapsulatedExpression_10(COALESCE);
	}

	@Test
	public void test_CollectionMember_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public void test_CollectionMember_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e ";
		int position = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			position,
			MEMBER,
			NOT_MEMBER
		);
	}

	@Test
	public void test_CollectionMember_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e N";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_MEMBER);
	}

	@Test
	public void test_CollectionMember_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e NO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_MEMBER);
	}

	@Test
	public void test_CollectionMember_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_MEMBER);
	}

	@Test
	public void test_CollectionMember_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_MEMBER);
	}

	@Test
	public void test_CollectionMember_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT M";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public void test_CollectionMember_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT ME";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public void test_CollectionMember_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT MEM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public void test_CollectionMember_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e NOT MEMB";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public void test_CollectionMember_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e NOT MEMBE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public void test_CollectionMember_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT MEMBER";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, MEMBER, MEMBER_OF, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public void test_CollectionMember_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT MEMBER ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e", OF);
	}

	@Test
	public void test_CollectionMember_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT MEMBER O";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, OF);
	}

	@Test
	public void test_CollectionMember_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT MEMBER OF";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, MEMBER, MEMBER_OF, NOT_MEMBER_OF, NOT_MEMBER);
	}

	@Test
	public void test_CollectionMember_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name M";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public void test_CollectionMember_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ME";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public void test_CollectionMember_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEM";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public void test_CollectionMember_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMB";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public void test_CollectionMember_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public void test_CollectionMember_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public void test_CollectionMember_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e", OF);
	}

	@Test
	public void test_CollectionMember_23() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER O";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, OF);
	}

	@Test
	public void test_CollectionMember_24() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER OF";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, MEMBER, MEMBER_OF, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public void test_CollectionMember_25() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER OF ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e");
	}

	@Test
	public void test_CollectionMember_26() {
		String jpqlQuery = "SELECT e FROM Employee e JOIN e.employees emp WHERE e MEMBER OF ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e", "emp");
	}

	@Test
	public void test_CollectionMember_27() {
		String jpqlQuery = "SELECT e FROM Employee e JOIN e.employees emp WHERE e MEMBER ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e", "emp", OF);
	}

	@Test
	public void test_CollectionMember_28() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER OF e.employees";
		int position = jpqlQuery.length() - "EMBER OF e.employees".length();
		testHasOnlyTheseProposals(jpqlQuery, position, MEMBER, MEMBER_OF, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public void test_CollectionMember_29() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			position,
			MEMBER,
			NOT_MEMBER
		);
	}

	@Test
	public void test_CollectionMemberDeclaration_01() {
		String jpqlQuery = "SELECT e FROM Employee e, I";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_02() {
		String jpqlQuery = "SELECT e FROM ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_03() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.names) AS f";
		int position = jpqlQuery.length() - "e, IN(e.names) AS f".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_04() {
		String jpqlQuery = "SELECT e FROM Employee e, ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_05() {
		String jpqlQuery = "SELECT e FROM Employee e, IN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_06() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AS);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_07() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) A";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_08() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_09() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_10() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_11() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, ".length();
		testHasTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_12() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, I".length();
		testHasOnlyTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_13() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN".length();
		testHasTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_14() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.collectionMemberDeclarationParameters());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CollectionMemberDeclaration_16() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.collectionMemberDeclarationParameters());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CollectionMemberDeclaration_17() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.collectionMemberDeclarationParameters());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CollectionMemberDeclaration_18() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(e.name) ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_19() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(e.name) A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_20() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(e.name) AS ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_23() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(K";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, KEY);
	}

	@Test
	public void test_CollectionMemberDeclaration_24() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(KEY(a)) AS a";
		int position = jpqlQuery.length() - "EY(a)) AS a".length();
		testHasOnlyTheseProposals(jpqlQuery, position, KEY);
	}

	@Test
	public void test_Comparison_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();

		testDoesNotHaveTheseProposals(
			jpqlQuery,
			position,
			DIFFERENT,
			GREATER_THAN,
			GREATER_THAN_OR_EQUAL,
			LOWER_THAN,
			LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age ";
		int position = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			position,
			DIFFERENT,
			GREATER_THAN,
			GREATER_THAN_OR_EQUAL,
			LOWER_THAN,
			LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age <";
		int position = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			position,
			DIFFERENT,
			EQUAL,
			LOWER_THAN_OR_EQUAL,
			LOWER_THAN,
			GREATER_THAN,
			GREATER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age >";
		int position = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			position,
			DIFFERENT,
			EQUAL,
			LOWER_THAN_OR_EQUAL,
			LOWER_THAN,
			GREATER_THAN,
			GREATER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age =";
		int position = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			position,
			DIFFERENT,
			EQUAL,
			LOWER_THAN_OR_EQUAL,
			LOWER_THAN,
			GREATER_THAN,
			GREATER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age <=";
		int position = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			position,
			DIFFERENT,
			EQUAL,
			LOWER_THAN_OR_EQUAL,
			LOWER_THAN,
			GREATER_THAN,
			GREATER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age >=";
		int position = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			position,
			DIFFERENT,
			EQUAL,
			LOWER_THAN_OR_EQUAL,
			LOWER_THAN,
			GREATER_THAN,
			GREATER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age <>";
		int position = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			position,
			DIFFERENT,
			EQUAL,
			LOWER_THAN_OR_EQUAL,
			LOWER_THAN,
			GREATER_THAN,
			GREATER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_CompoundFunction_01() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length();

		List<String> identifiers = new ArrayList<String>();
		addAll(identifiers, bnfAccessor.conditionalExpressionsAggregates());
		addAll(identifiers, bnfAccessor.conditionalExpressionsCompoundFunctions());

		// TODO: Add better check since AND and OR are not valid proposals
		testHasOnlyTheseProposals(jpqlQuery, position, identifiers);
	}

	@Test
	public void test_Concat_001() {
		test_AbstractSingleEncapsulatedExpression_01(CONCAT);
	}

	@Test
	public void test_Concat_002() {
		test_AbstractSingleEncapsulatedExpression_02(CONCAT);
	}

	@Test
	public void test_Concat_003() {
		test_AbstractSingleEncapsulatedExpression_03(CONCAT);
	}

	@Test
	public void test_Concat_004() {
		test_AbstractSingleEncapsulatedExpression_04(CONCAT);
	}

	@Test
	public void test_Concat_005() {
		test_AbstractSingleEncapsulatedExpression_05(CONCAT);
	}

	@Test
	public void test_Concat_006() {
		test_AbstractSingleEncapsulatedExpression_06(CONCAT);
	}

	@Test
	public void test_Concat_007() {
		test_AbstractSingleEncapsulatedExpression_07(CONCAT);
	}

	@Test
	public void test_Concat_008() {
		test_AbstractSingleEncapsulatedExpression_08(CONCAT);
	}

	@Test
	public void test_Concat_009() {
		test_AbstractSingleEncapsulatedExpression_09(CONCAT);
	}

	@Test
	public void test_Concat_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_010() {
		test_AbstractSingleEncapsulatedExpression_10(CONCAT);
	}

	@Test
	public void test_Concat_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE C";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE C";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CON";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CONC";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CONCA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CONCAT";
		testHasTheseProposals(jpqlQuery, jpqlQuery.length(), CONCAT);
	}

	@Test
	public void test_Concat_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (C";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (CO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (CON";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (CONC";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (CONCA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (CONCAT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ()";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (C)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (CO)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (CON)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (CONC)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (CONCA)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (CONCAT)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Concat_23() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length() - 1;
		testDoesNotHaveTheseProposals(jpqlQuery, position, CONCAT);
	}

	@Test
	public void test_Constructor_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_02() {
		String jpqlQuery = "SELECT N";
		int position = "SELECT ".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_03() {
		String jpqlQuery = "SELECT N";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_04() throws Exception {
		String jpqlQuery = "SELECT e, NEW (";
		int position = "SELECT e, ".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_05() throws Exception {
		String jpqlQuery = "SELECT e, NEW (";
		int position = "SELECT e, N".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_06() throws Exception {
		String jpqlQuery = "SELECT NEW String() From Employee e";
		int position = "SELECT NEW String(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.constructorItemFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Constructor_07() throws Exception {

		String jpqlQuery = "SELECT NEW String(e) From Employee e";
		int position = "SELECT NEW String(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, filter(bnfAccessor.constructorItemFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Constructor_08() {
		String jpqlQuery = "SELECT e, NEW java.lang.String(e.name)";
		int position = "SELECT e, N".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_09() {
		String jpqlQuery = "SELECT e, NEW java.lang.String(e.name)";
		int position = "SELECT e, NE".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_10() {
		String jpqlQuery = "SELECT e, NEW(java.lang.String)";
		int position = "SELECT e, NEW".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_11() throws Exception {
		String jpqlQuery = "SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c";
		int position = "SELECT new com.titan.domain.Name(c.".length();
		testHasTheseProposals(jpqlQuery, position, "firstName", "hasGoodCredit", "id", "lastName", "address", "home");
	}

	@Test
	public void test_Constructor_12() {
		String jpqlQuery = "SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c";
		int position = "SELECT new com.titan.domain.Name(c.firstName, ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("c");
		addAll(proposals, bnfAccessor.constructorItemFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Constructor_13() {
		String jpqlQuery = "SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c";
		int position = "SELECT new com.titan.domain.Name(c.firstName, c".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("c");
		addAll(proposals, filter(bnfAccessor.constructorItemFunctions(), "c"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Constructor_15() throws Exception {
		String jpqlQuery = "SELECT NE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_16() throws Exception {
		String jpqlQuery = "SELECT e, NE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_17() throws Exception {
		String jpqlQuery = "SELECT e, NEW";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_18() {
		String jpqlQuery = "SELECT NE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_19() {
		String jpqlQuery = "SELECT NEW";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_20() {
		String jpqlQuery = "SELECT e, NEW";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_Constructor_21() {
		String jpqlQuery = "SELECT e, NEW java.lang.String(e.name)";
		int position = "SELECT e, ".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public void test_CountFunction_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public void test_CountFunction_02() {
		String jpqlQuery = "SELECT C";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public void test_CountFunction_03() {
		String jpqlQuery = "SELECT CO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public void test_CountFunction_04() {
		String jpqlQuery = "SELECT COU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public void test_CountFunction_05() {
		String jpqlQuery = "SELECT COUN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public void test_CountFunction_06() {
		String jpqlQuery = "SELECT COUNT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public void test_CountFunction_07() {

		String jpqlQuery = "SELECT COUNT(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_08() {

		String jpqlQuery = "SELECT COUNT() From Employee e";
		int position = "SELECT COUNT(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_09() {

		String jpqlQuery = "SELECT COUNT(DISTINCT ) From Employee e";
		int position = "SELECT COUNT(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_10() {
		String jpqlQuery = "SELECT COUNT(D ) From Employee e";
		int position = "SELECT COUNT(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_CountFunction_11() {
		String jpqlQuery = "SELECT COUNT(DI ) From Employee e";
		int position = "SELECT COUNT(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_CountFunction_12() {
		String jpqlQuery = "SELECT COUNT(DIS ) From Employee e";
		int position = "SELECT COUNT(DIS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_CountFunction_13() {
		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_CountFunction_14() {
		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_CountFunction_15() {
		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_CountFunction_16() {

		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_17() {

		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, filter(bnfAccessor.countFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_18() {

		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee emp";
		int position = "SELECT COUNT(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.countFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_19() {
		String jpqlQuery = "SELECT COUNT() From Employee emp";
		int position = "SELECT COUNT(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_20() {

		String jpqlQuery = "SELECT COUNT(e) From Employee emp";
		int position = "SELECT COUNT(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.countFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_21() {

		String jpqlQuery = "SELECT COUNT(em) From Employee emp";
		int position = "SELECT COUNT(em".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.countFunctions(), "em"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_22() {

		String jpqlQuery = "SELECT COUNT(emp) From Employee emp";
		int position = "SELECT COUNT(emp".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.countFunctions(), "emp"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_23() {

		String jpqlQuery = "SELECT COUNT(emp) From Employee emp";
		int position = "SELECT COUNT(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.countFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_24() {

		String jpqlQuery = "SELECT COUNT(emp) From Employee emp";
		int position = "SELECT COUNT(em".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.countFunctions(), "em"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_25() {
		String jpqlQuery = "SELECT COUNT( From Employee emp";
		int position = "SELECT COUNT(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_CountFunction_26() {

		String jpqlQuery = "SELECT COUNT(e From Employee emp";
		int position = "SELECT COUNT(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.countFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_DateTime_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hiredTime < ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE C";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CUR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURREN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_D";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE);
		testDoesNotHaveTheseProposals(jpqlQuery, position, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_DA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE);
		testDoesNotHaveTheseProposals(jpqlQuery, position, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_DAT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE);
		testDoesNotHaveTheseProposals(jpqlQuery, position, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_DATE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_T";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_TIME, CURRENT_TIMESTAMP);
		testDoesNotHaveTheseProposals(jpqlQuery, position, CURRENT_DATE);
	}

	@Test
	public void test_DateTime_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_TIME, CURRENT_TIMESTAMP);
		testDoesNotHaveTheseProposals(jpqlQuery, position, CURRENT_DATE);
	}

	@Test
	public void test_DateTime_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIM";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_TIME, CURRENT_TIMESTAMP);
		testDoesNotHaveTheseProposals(jpqlQuery, position, CURRENT_DATE);
	}

	@Test
	public void test_DateTime_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIME";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_TIMESTAMP);
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_DateTime_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIMES";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_TIMESTAMP);
		testDoesNotHaveTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME);
	}

	@Test
	public void test_DateTime_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIMEST";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_TIMESTAMP);
		testDoesNotHaveTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME);
	}

	@Test
	public void test_DateTime_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIMESTA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_TIMESTAMP);
		testDoesNotHaveTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME);
	}

	@Test
	public void test_DateTime_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIMESTAM";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_TIMESTAMP);
		testDoesNotHaveTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME);
	}

	@Test
	public void test_DateTime_23() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIMESTAMP";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public void test_Delete_01() {
		String jpqlQuery = "D";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_02() {
		String jpqlQuery = "DE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_03() {
		String jpqlQuery = "DEL";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_04() {
		String jpqlQuery = "DELE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_05() {
		String jpqlQuery = "DELET";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_06() {
		String jpqlQuery = "DELETE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_07() {
		String jpqlQuery = "DELETE ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_08() {
		String jpqlQuery = "DELETE F";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_09() {
		String jpqlQuery = "DELETE FR";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_10() {
		String jpqlQuery = "DELETE FRO";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_11() {
		String jpqlQuery = "DELETE FROM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_12() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_13() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_14() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DEL".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_15() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_16() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELET".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_17() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_18() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_19() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE F".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_20() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE FR".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_21() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE FRO".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_22() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE FROM".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_23() {
		String jpqlQuery = "DELETE FROM Employee e WHERE";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_24() {
		String jpqlQuery = "DELETE FROM WHERE";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public void test_Delete_25() throws Exception {
		String jpqlQuery = "DELETE FROM ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public void test_Delete_26() throws Exception {
		String jpqlQuery = "DELETE FROM P";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "P"));
	}

	@Test
	public void test_Delete_27() {
		String jpqlQuery = "DELETE FROM Employee WHERE n";
		int position = jpqlQuery.length() - "WHERE n".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Delete_28() {
		String jpqlQuery = "DELETE FROM Employee A WHERE";
		int position = jpqlQuery.length() - " WHERE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_Delete_29() {
		String jpqlQuery = "DELETE FROM Employee A ";
		int position = jpqlQuery.length() - 1;
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_Delete_30() {
		String jpqlQuery = "DELETE FROM Employee AS e ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Delete_31() {
		String jpqlQuery = "DELETE FROM Employee AS e W";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Delete_32() {
		String jpqlQuery = "DELETE FROM Employee AS e WH";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Delete_33() {
		String jpqlQuery = "DELETE FROM Employee AS e WHE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Delete_34() {
		String jpqlQuery = "DELETE FROM Employee AS e WHER";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Delete_35() {
		String jpqlQuery = "DELETE FROM Employee AS e WHERE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_EmptyCollectionComparison_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name I";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS N";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY, NOT, IS_NOT_NULL);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NULL);
	}

	@Test
	public void test_EmptyCollectionComparison_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY, IS_NOT_NULL);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NULL);
	}

	@Test
	public void test_EmptyCollectionComparison_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT E";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT EM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT EMP";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT EMPT";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT EMPTY";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS E";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY);
		testDoesNotHaveTheseProposals(jpqlQuery, position, EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS EM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NOT_EMPTY, EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS EMP";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY);
		testDoesNotHaveTheseProposals(jpqlQuery, position, EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS EMPT";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY);
		testDoesNotHaveTheseProposals(jpqlQuery, position, EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public void test_EmptyCollectionComparison_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS EMPTY";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public void test_Exists_001() {
		test_AbstractSingleEncapsulatedExpression_01(EXISTS);
	}

	@Test
	public void test_Exists_002() {
		test_AbstractSingleEncapsulatedExpression_02(EXISTS);
	}

	@Test
	public void test_Exists_003() {
		test_AbstractSingleEncapsulatedExpression_03(EXISTS);
	}

	@Test
	public void test_Exists_004() {
		test_AbstractSingleEncapsulatedExpression_04(EXISTS);
	}

	@Test
	public void test_Exists_005() {
		test_AbstractSingleEncapsulatedExpression_05(EXISTS);
	}

	@Test
	public void test_Exists_006() {
		test_AbstractSingleEncapsulatedExpression_06(EXISTS);
	}

	@Test
	public void test_Exists_007() {
		test_AbstractSingleEncapsulatedExpression_07(EXISTS);
	}

	@Test
	public void test_Exists_008() {
		test_AbstractSingleEncapsulatedExpression_08(EXISTS);
	}

	@Test
	public void test_Exists_009() {
		test_AbstractSingleEncapsulatedExpression_09(EXISTS);
	}

	@Test
	public void test_Exists_010() {
		test_AbstractSingleEncapsulatedExpression_10(EXISTS);
	}

	@Test
	public void test_Exists_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE E";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE EX";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE EXI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE EXIS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE EXIST";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE EXISTS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (E";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (EX";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (EXI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (EXIS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (EXIST";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (EXISTS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (E)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (EX)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (EXI)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (EXIS)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (EXIST)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_Exists_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (EXISTS)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, EXISTS);
	}

	@Test
	public void test_From_01() throws Exception {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_02() throws Exception {
		String jpqlQuery = "SELECT F";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_03() throws Exception {
		String jpqlQuery = "SELECT AVG(e.age)";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_04() throws Exception {
		String jpqlQuery = "SELECT AVG(e.age) ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_05() throws Exception {
		String jpqlQuery = "SELECT AVG(e.age) F";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_06() throws Exception {
		String jpqlQuery = "SELECT f ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_07() throws Exception {
		String jpqlQuery = "SELECT a, ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_08() throws Exception {
		String jpqlQuery = "SELECT AVG( ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_09() throws Exception {
		String jpqlQuery = "SELECT AVG(a ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_10() throws Exception {
		String jpqlQuery = "SELECT AVG(e.age ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_11() throws Exception {
		String jpqlQuery = "SELECT F F";
		int position = "SELECT F".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_From_12() throws Exception {
		String jpqlQuery = "SELECT e FROM Address a," +
		               "              Employee emp JOIN emp.customers emp_c, " +
		               "              Address ea " +
		               "WHERE ALL(SELECT a e";

		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public void test_From_13() throws Exception {
		String jpqlQuery = "SELECT e F";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_FromAs_01() {
		String jpqlQuery = "SELECT e FROM Employee ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_FromAs_02() {
		String jpqlQuery = "SELECT e FROM Employee A";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_FromAs_03() {
		String jpqlQuery = "SELECT e FROM Employee AS";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_FromAs_04() {
		String jpqlQuery = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee ".length();
		testHasTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_FromAs_05() {
		String jpqlQuery = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_FromAs_06() {
		String jpqlQuery = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee AS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_GroupBy_01() {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_02() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name";
		int position = "SELECT e FROM Employee e ".length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_03() {
		String jpqlQuery = "SELECT e FROM Employee e G";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_04() {
		String jpqlQuery = "SELECT e FROM Employee e GR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_05() {
		String jpqlQuery = "SELECT e FROM Employee e GRO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_06() {
		String jpqlQuery = "SELECT e FROM Employee e GROU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_07() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_08() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_09() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP B";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_10() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') G";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_GroupBy_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public void test_Having_01() {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_02() {
		String jpqlQuery = "SELECT e FROM Employee e HAVING COUNT(e) >= 5";
		int position = "SELECT e FROM Employee e ".length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_03() {
		String jpqlQuery = "SELECT e FROM Employee e H";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_04() {
		String jpqlQuery = "SELECT e FROM Employee e HA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_05() {
		String jpqlQuery = "SELECT e FROM Employee e HAV";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_06() {
		String jpqlQuery = "SELECT e FROM Employee e HAVI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_07() {
		String jpqlQuery = "SELECT e FROM Employee e HAVIN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_08() {
		String jpqlQuery = "SELECT e FROM Employee e HAVING";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') H";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_11() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_12() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name H";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name H";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Having_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public void test_Index_01() {
		test_AbstractSingleEncapsulatedExpression_01(INDEX);
	}

	@Test
	public void test_Index_02() {
		test_AbstractSingleEncapsulatedExpression_02(INDEX);
	}

	@Test
	public void test_Index_03() {
		test_AbstractSingleEncapsulatedExpression_03(INDEX);
	}

	@Test
	public void test_Index_04() {
		test_AbstractSingleEncapsulatedExpression_04(INDEX);
	}

	@Test
	public void test_Index_05() {
		test_AbstractSingleEncapsulatedExpression_05(INDEX);
	}

	@Test
	public void test_Index_06() {
		test_AbstractSingleEncapsulatedExpression_06(INDEX);
	}

	@Test
	public void test_Index_07() {
		test_AbstractSingleEncapsulatedExpression_07(INDEX);
	}

	@Test
	public void test_Index_08() {
		test_AbstractSingleEncapsulatedExpression_08(INDEX);
	}

	@Test
	public void test_Index_09() {
		test_AbstractSingleEncapsulatedExpression_09(INDEX);
	}

	@Test
	public void test_Index_10() {
		test_AbstractSingleEncapsulatedExpression_10(INDEX);
	}

	@Test
	public void test_IsNotNull() throws Exception {
		String jpqlQuery = "select e from Employee e where e.managerEmployee is not ";
		int position = jpqlQuery.length();
//		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY, IS_NULL);
	}

	@Test
	public void test_Join_01() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub ".length();

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			INNER_JOIN,
			INNER_JOIN_FETCH,
			JOIN,
			JOIN_FETCH,
			LEFT_JOIN,
			LEFT_JOIN_FETCH,
			LEFT_OUTER_JOIN,
			LEFT_OUTER_JOIN_FETCH
		);
	}

	@Test
	public void test_Join_02() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub L".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_03() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_04() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEF".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_05() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_06() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_07() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT J".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_08() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT JO".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_09() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT JOI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_10() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_11() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_12() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub J".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_13() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub JO".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_14() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub JOI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_15() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_16() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_17() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub L".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_18() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_19() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEF".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_20() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_21() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_22() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT O".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_23() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OU".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_24() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUT".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_25() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_26() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_27() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_28() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER J".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_29() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER JO".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_30() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER JOI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_31() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER JOIN".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_32() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag";
		int position = "SELECT pub FROM Publisher pub JOIN pub.magazines ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_Join_33() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag";
		int position = "SELECT pub FROM Publisher pub JOIN pub.magazines A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_Join_34() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag";
		int position = "SELECT pub FROM Publisher pub JOIN pub.magazines AS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_Join_35() {
		String jpqlQuery = "SELECT e FROM Employee e INNER JOIN e.magazines mags";
		int position = "SELECT e FROM Employee e ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_36() {
		String jpqlQuery = "SELECT e FROM Employee e INNER JOIN e.magazines mags ";
		int position = "SELECT e FROM Employee e INNER JOIN e.magazines mags ".length();

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			INNER_JOIN,
			INNER_JOIN_FETCH,
			JOIN,
			JOIN_FETCH,
			LEFT_JOIN,
			LEFT_JOIN_FETCH,
			LEFT_OUTER_JOIN,
			LEFT_OUTER_JOIN_FETCH,
			WHERE,
			HAVING,
			ORDER_BY,
			GROUP_BY
		);
	}

	@Test
	public void test_Join_37() {

		String jpqlQuery = "SELECT e FROM Employee e INNER JOIN e.mags mags";
		int position = "SELECT e FROM Employee e INNER".length();

		if (isJoinFetchIdentifiable()) {
			testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
		}
		else {
			testHasOnlyTheseProposals(jpqlQuery, position, joinOnlyIdentifiers());
		}
	}

	@Test
	public void test_Join_38() {

		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList e LEFT ";
		int position = jpqlQuery.length();

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			LEFT_JOIN,
			LEFT_JOIN_FETCH,
			LEFT_OUTER_JOIN,
			LEFT_OUTER_JOIN_FETCH
		);
	}

	@Test
	public void test_Join_39() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList e LEFT OUTER JOIN FETCH  ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "o", "e");
	}

	@Test
	public void test_Join_40() {
		String jpqlQuery = "select o.city from Address o ,";
		int position = "select o.city from Address o ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public void test_Join_41() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_Join_42() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList A";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_Join_43() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList J";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_Join_44() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList J LEFT JOIN ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "o", "J");
	}

	@Test
	public void test_Join_45() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList J LEFT JOIN L";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, "o");
	}

	@Test
	public void test_Key_01() {
		test_AbstractSingleEncapsulatedExpression_01(KEY);
	}

	@Test
	public void test_Key_02() {
		test_AbstractSingleEncapsulatedExpression_02(KEY);
	}

	@Test
	public void test_Key_03() {
		test_AbstractSingleEncapsulatedExpression_03(KEY);
	}

	@Test
	public void test_Key_04() {
		test_AbstractSingleEncapsulatedExpression_04(KEY);
	}

	@Test
	public void test_Key_05() {
		test_AbstractSingleEncapsulatedExpression_05(KEY);
	}

	@Test
	public void test_Key_06() {
		test_AbstractSingleEncapsulatedExpression_06(KEY);
	}

	@Test
	public void test_Key_07() {
		test_AbstractSingleEncapsulatedExpression_07(KEY);
	}

	@Test
	public void test_Key_08() {
		test_AbstractSingleEncapsulatedExpression_08(KEY);
	}

	@Test
	public void test_Key_09() {
		test_AbstractSingleEncapsulatedExpression_09(KEY);
	}

	@Test
	public void test_Key_10() {
		test_AbstractSingleEncapsulatedExpression_10(KEY);
	}

	@Test
	public void test_Keyword_01() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = ".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public void test_Keyword_02() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = T".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public void test_Keyword_03() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = TR".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public void test_Keyword_04() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = TRU".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public void test_Keyword_05() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, TRUE, FALSE, NULL);
	}

	@Test
	public void test_Keyword_06() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = ".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public void test_Keyword_07() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = F".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public void test_Keyword_08() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = FA".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public void test_Keyword_09() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = FAL".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public void test_Keyword_10() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = FALS".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public void test_Keyword_11() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, TRUE, FALSE, NULL);
	}

	@Test
	public void test_Keyword_12() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = ".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public void test_Keyword_13() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = N".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public void test_Keyword_14() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = NU".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public void test_Keyword_15() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = NUL".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public void test_Keyword_16() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULL, TRUE, FALSE, NULLIF);
	}

	@Test
	public void test_Keyword_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = ".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public void test_Keyword_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = T".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public void test_Keyword_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = TR".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public void test_Keyword_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = TRU".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public void test_Keyword_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, TRUE, FALSE, NULL);
	}

	@Test
	public void test_Keyword_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = ".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public void test_Keyword_23() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = F".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public void test_Keyword_24() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = FA".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public void test_Keyword_25() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = FAL".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public void test_Keyword_26() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = FALS".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public void test_Keyword_27() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, TRUE, FALSE, NULL);
	}

	@Test
	public void test_Keyword_28() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = ".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public void test_Keyword_29() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = N".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public void test_Keyword_30() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = NU".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public void test_Keyword_31() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = NUL".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public void test_Keyword_32() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULL, TRUE, FALSE, NULLIF);
	}

	@Test
	public void test_Keyword_33() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NULL, TRUE, FALSE);
	}

	@Test
	public void test_Keyword_34() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired =";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NULL, TRUE, FALSE);
	}

	@Test
	public void test_Keyword_35() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NULL, TRUE, FALSE);
	}

	@Test
	public void test_Length_001() {
		test_AbstractSingleEncapsulatedExpression_01(LENGTH);
	}

	@Test
	public void test_Length_002() {
		test_AbstractSingleEncapsulatedExpression_02(LENGTH);
	}

	@Test
	public void test_Length_003() {
		test_AbstractSingleEncapsulatedExpression_03(LENGTH);
	}

	@Test
	public void test_Length_004() {
		test_AbstractSingleEncapsulatedExpression_04(LENGTH);
	}

	@Test
	public void test_Length_005() {
		test_AbstractSingleEncapsulatedExpression_05(LENGTH);
	}

	@Test
	public void test_Length_006() {
		test_AbstractSingleEncapsulatedExpression_06(LENGTH);
	}

	@Test
	public void test_Length_007() {
		test_AbstractSingleEncapsulatedExpression_07(LENGTH);
	}

	@Test
	public void test_Length_008() {
		test_AbstractSingleEncapsulatedExpression_08(LENGTH);
	}

	@Test
	public void test_Length_009() {
		test_AbstractSingleEncapsulatedExpression_09(LENGTH);
	}

	@Test
	public void test_Length_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_010() {
		test_AbstractSingleEncapsulatedExpression_10(LENGTH);
	}

	@Test
	public void test_Length_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE L";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LEN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LENG";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LENGT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LENGTH";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (L";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LEN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LENG";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LENGT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LENGTH";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ()";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (L)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LE)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LEN)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LENG)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LENGT)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Length_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LENGTH)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LENGTH);
	}

	@Test
	public void test_Like_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, LIKE, NOT_LIKE);
	}

	@Test
	public void test_Like_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LIKE, NOT_LIKE);
	}

	@Test
	public void test_Like_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name L";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, LIKE);
	}

	@Test
	public void test_Like_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name LI";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, LIKE);
	}

	@Test
	public void test_Like_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name LIKE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, LIKE, NOT_LIKE);
	}

	@Test
	public void test_Like_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name N";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public void test_Like_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public void test_Like_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public void test_Like_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name ".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public void test_Like_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name N".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public void test_Like_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NO".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public void test_Like_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT ".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public void test_Like_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT L".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public void test_Like_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT LI".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public void test_Like_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT LIK".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public void test_Like_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, LIKE, NOT_LIKE);
	}

	@Test
	public void test_Locate_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE L";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOC";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOCA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOCAT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOCATE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOCATE(";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (L";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LOC";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LOCA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LOCAT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LOCATE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LOCATE)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Locate_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ()";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public void test_Logical_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 AND 3 ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AND, OR);
	}

	@Test
	public void test_Lower_01() {
		test_AbstractSingleEncapsulatedExpression_01(LOWER);
	}

	@Test
	public void test_Lower_02() {
		test_AbstractSingleEncapsulatedExpression_02(LOWER);
	}

	@Test
	public void test_Lower_03() {
		test_AbstractSingleEncapsulatedExpression_03(LOWER);
	}

	@Test
	public void test_Lower_04() {
		test_AbstractSingleEncapsulatedExpression_04(LOWER);
	}

	@Test
	public void test_Lower_05() {
		test_AbstractSingleEncapsulatedExpression_05(LOWER);
	}

	@Test
	public void test_Lower_06() {
		test_AbstractSingleEncapsulatedExpression_06(LOWER);
	}

	@Test
	public void test_Lower_07() {
		test_AbstractSingleEncapsulatedExpression_07(LOWER);
	}

	@Test
	public void test_Lower_08() {
		test_AbstractSingleEncapsulatedExpression_08(LOWER);
	}

	@Test
	public void test_Lower_09() {
		test_AbstractSingleEncapsulatedExpression_09(LOWER);
	}

	@Test
	public void test_Lower_10() {
		test_AbstractSingleEncapsulatedExpression_10(LOWER);
	}

	@Test
	public void test_Lower_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOWER";
		testHasTheseProposals(jpqlQuery, jpqlQuery.length(), LOWER);
	}

	@Test
	public void test_MaxFunction_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MAX);
	}

	@Test
	public void test_MaxFunction_02() {
		String jpqlQuery = "SELECT M";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MAX);
	}

	@Test
	public void test_MaxFunction_03() {
		String jpqlQuery = "SELECT MA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MAX);
	}

	@Test
	public void test_MaxFunction_04() {
		String jpqlQuery = "SELECT MAX";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MAX);
	}

	@Test
	public void test_MaxFunction_05() {

		String jpqlQuery = "SELECT MAX(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MaxFunction_06() {

		String jpqlQuery = "SELECT MAX() From Employee e";
		int position = "SELECT MAX(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MaxFunction_07() {

		String jpqlQuery = "SELECT MAX(DISTINCT ) From Employee e";
		int position = "SELECT MAX(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MaxFunction_08() {
		String jpqlQuery = "SELECT MAX(D ) From Employee e";
		int position = "SELECT MAX(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MaxFunction_09() {
		String jpqlQuery = "SELECT MAX(DI ) From Employee e";
		int position = "SELECT MAX(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MaxFunction_10() {
		String jpqlQuery = "SELECT MAX(DIS ) From Employee e";
		int position = "SELECT MAX(DIS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MaxFunction_11() {
		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MaxFunction_12() {
		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MaxFunction_13() {
		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MaxFunction_14() {

		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MaxFunction_15() {

		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MaxFunction_16() {

		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee emp";
		int position = "SELECT MAX(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MaxFunction_17() {

		String jpqlQuery = "SELECT MAX() From Employee emp";
		int position = "SELECT MAX(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MaxFunction_18() {

		String jpqlQuery = "SELECT MAX(e) From Employee emp";
		int position = "SELECT MAX(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MaxFunction_19() {
		String jpqlQuery = "SELECT MAX(em) From Employee emp";
		int position = "SELECT MAX(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_MaxFunction_20() {
		String jpqlQuery = "SELECT MAX(emp) From Employee emp";
		int position = "SELECT MAX(emp".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_MaxFunction_21() {

		String jpqlQuery = "SELECT MAX(emp) From Employee emp";
		int position = "SELECT MAX(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MaxFunction_22() {
		String jpqlQuery = "SELECT MAX(emp) From Employee emp";
		int position = "SELECT MAX(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_MaxFunction_23() {

		String jpqlQuery = "SELECT MAX( From Employee emp";
		int position = "SELECT MAX(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MaxFunction_24() {

		String jpqlQuery = "SELECT MAX(e From Employee emp";
		int position = "SELECT MAX(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MIN);
	}

	@Test
	public void test_MinFunction_02() {
		String jpqlQuery = "SELECT M";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MIN);
	}

	@Test
	public void test_MinFunction_03() {
		String jpqlQuery = "SELECT MI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MIN);
	}

	@Test
	public void test_MinFunction_04() {
		String jpqlQuery = "SELECT MIN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MIN);
	}

	@Test
	public void test_MinFunction_05() {

		String jpqlQuery = "SELECT MIN(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_06() {

		String jpqlQuery = "SELECT MIN() From Employee e";
		int position = "SELECT MIN(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_07() {

		String jpqlQuery = "SELECT MIN(DISTINCT ) From Employee e";
		int position = "SELECT MIN(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_08() {
		String jpqlQuery = "SELECT MIN(D ) From Employee e";
		int position = "SELECT MIN(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MinFunction_09() {
		String jpqlQuery = "SELECT MIN(DI ) From Employee e";
		int position = "SELECT MIN(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MinFunction_10() {
		String jpqlQuery = "SELECT MIN(DIS ) From Employee e";
		int position = "SELECT MIN(DIS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MinFunction_11() {
		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MinFunction_12() {
		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MinFunction_13() {
		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_MinFunction_14() {

		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_15() {

		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_16() {

		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee emp";
		int position = "SELECT MIN(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_17() {

		String jpqlQuery = "SELECT MIN() From Employee emp";
		int position = "SELECT MIN(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_18() {

		String jpqlQuery = "SELECT MIN(e) From Employee emp";
		int position = "SELECT MIN(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_19() {
		String jpqlQuery = "SELECT MIN(em) From Employee emp";
		int position = "SELECT MIN(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_MinFunction_20() {
		String jpqlQuery = "SELECT MIN(emp) From Employee emp";
		int position = "SELECT MIN(emp".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_MinFunction_21() {

		String jpqlQuery = "SELECT MIN(emp) From Employee emp";
		int position = "SELECT MIN(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_22() {
		String jpqlQuery = "SELECT MIN(emp) From Employee emp";
		int position = "SELECT MIN(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_MinFunction_23() {

		String jpqlQuery = "SELECT MIN( From Employee emp";
		int position = "SELECT MIN(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_MinFunction_24() {

		String jpqlQuery = "SELECT MIN(e From Employee emp";
		int position = "SELECT MIN(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_NullComparison_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NULL);
	}

	@Test
	public void test_NullComparison_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NULL);
	}

	@Test
	public void test_NullComparison_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name I";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NULL);
	}

	@Test
	public void test_NullComparison_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NULL);
	}

	@Test
	public void test_NullComparison_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NULL);
	}

	@Test
	public void test_NullComparison_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS N";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NULL);
	}

	@Test
	public void test_NullComparison_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public void test_NullComparison_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public void test_NullComparison_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT N";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public void test_NullComparison_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT NU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public void test_NullComparison_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT NUL";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public void test_NullComparison_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT NULL";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public void test_Object_01() {
		String jpqlQuery = "SELECT O FROM Employee e";
		int position = "SELECT O".length() - 1;
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_02() {
		String jpqlQuery = "SELECT OB FROM Employee e";
		int position = "SELECT O".length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_03() {
		String jpqlQuery = "SELECT OBJ FROM Employee e";
		int position = "SELECT O".length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_04() {
		String jpqlQuery = "SELECT OBJE FROM Employee e";
		int position = "SELECT O".length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_05() {
		String jpqlQuery = "SELECT OBJEC FROM Employee e";
		int position = "SELECT O".length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_12() {
		String jpqlQuery = "SELECT OBJECT";
		testHasTheseProposals(jpqlQuery, jpqlQuery.length(), OBJECT);
	}

	@Test
	public void test_Object_13() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_14() {
		String jpqlQuery = "SELECT O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_15() {
		String jpqlQuery = "SELECT OB";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_16() {
		String jpqlQuery = "SELECT OBJ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_17() {
		String jpqlQuery = "SELECT OBJE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_18() {
		String jpqlQuery = "SELECT OBJEC";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_19() {
		String jpqlQuery = "SELECT OBJECT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public void test_Object_20() {
		String jpqlQuery = "SELECT DISTINCT OBJECT(a) FROM Address a";
		int position = "SELECT DISTINCT OBJECT(a".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "a");
	}

	@Test
	public void test_OptionalClauses_01() {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			INNER_JOIN,
			INNER_JOIN_FETCH,
			JOIN,
			JOIN_FETCH,
			LEFT_JOIN,
			LEFT_JOIN_FETCH,
			LEFT_OUTER_JOIN,
			LEFT_OUTER_JOIN_FETCH,
			WHERE,
			GROUP_BY,
			HAVING,
			ORDER_BY
		);
	}

	@Test
	public void test_OptionalClauses_02() {
		String jpqlQuery = "SELECT e FROM Employee e HAVING e.name = 'Oracle'";
		int position = "SELECT e FROM Employee e ".length();

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			INNER_JOIN,
			INNER_JOIN_FETCH,
			JOIN,
			JOIN_FETCH,
			LEFT_JOIN,
			LEFT_JOIN_FETCH,
			LEFT_OUTER_JOIN,
			LEFT_OUTER_JOIN_FETCH,
			WHERE,
			GROUP_BY,
			HAVING
		);
	}

	@Test
	public void test_OptionalClauses_03() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name";
		int position = "SELECT e FROM Employee e ".length();

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			INNER_JOIN,
			INNER_JOIN_FETCH,
			JOIN,
			JOIN_FETCH,
			LEFT_JOIN,
			LEFT_JOIN_FETCH,
			LEFT_OUTER_JOIN,
			LEFT_OUTER_JOIN_FETCH,
			WHERE,
			GROUP_BY,
			HAVING,
			ORDER_BY
		);
	}

	@Test
	public void test_OptionalClauses_04() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name";
		int position = "SELECT e FROM Employee e ".length();

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			INNER_JOIN,
			INNER_JOIN_FETCH,
			JOIN,
			JOIN_FETCH,
			LEFT_JOIN,
			LEFT_JOIN_FETCH,
			LEFT_OUTER_JOIN,
			LEFT_OUTER_JOIN_FETCH,
			WHERE,
			GROUP_BY
		);
	}

	@Test
	public void test_OptionalClauses_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name = 'Oracle' ";
		int position = jpqlQuery.length();

		testHasTheseProposals(
			jpqlQuery,
			position,
			GROUP_BY,
			HAVING,
			ORDER_BY
		);
	}

	@Test
	public void test_OrderBy_01() {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_02() {
		String jpqlQuery = "SELECT e FROM Employee e O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_03() {
		String jpqlQuery = "SELECT e FROM Employee e OR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_04() {
		String jpqlQuery = "SELECT e FROM Employee e ORD";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_05() {
		String jpqlQuery = "SELECT e FROM Employee e ORDE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_06() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_07() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_08() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER B";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_09() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_12() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_13() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_14() {
		String jpqlQuery = "SELECT e FROM Employee e HAVING COUNT(e) >= 5 ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_15() {
		String jpqlQuery = "SELECT e FROM Employee e HAVING COUNT(e) >= 5 O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name HAVING COUNT(e) >= 5 ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderBy_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name HAVING COUNT(e) >= 5 O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_OrderByItem_01() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, ASC);
	}

	@Test
	public void test_OrderByItem_02() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ASC);
	}

	@Test
	public void test_OrderByItem_03() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name A";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ASC);
	}

	@Test
	public void test_OrderByItem_04() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name AS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ASC);
	}

	@Test
	public void test_OrderByItem_05() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name ASC";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, ASC, DESC);
	}

	@Test
	public void test_OrderByItem_06() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name D";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, DESC);
	}

	@Test
	public void test_OrderByItem_07() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name DE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, DESC);
	}

	@Test
	public void test_OrderByItem_08() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name DE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, DESC);
	}

	@Test
	public void test_OrderByItem_09() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name DESC";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, ASC, DESC);
	}

	@Test
	public void test_Query_01() throws Exception {

		String jpqlQuery = ExpressionTools.EMPTY_STRING;
		int position = 0;

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			SELECT,
			UPDATE,
			DELETE_FROM
		);
	}

	@Test
	public void test_Query_02() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name SEL";
		int position = jpqlQuery.length();

		testDoesNotHaveTheseProposals(
			jpqlQuery,
			position,
			SELECT,
			UPDATE,
			DELETE_FROM
		);
	}

	@Test
	public void test_Query_03() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name DEL";
		int position = jpqlQuery.length();

		testDoesNotHaveTheseProposals(
			jpqlQuery,
			position,
			SELECT,
			UPDATE,
			DELETE_FROM
		);
	}

	@Test
	public void test_Query_04() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name UP";
		int position = jpqlQuery.length();

		testDoesNotHaveTheseProposals(
			jpqlQuery,
			position,
			SELECT,
			UPDATE,
			DELETE_FROM
		);
	}

	@Test
	public void test_Restriction_01() throws Exception {

		String jpqlQuery = "SELECT AVG(e.name) FROM Employee e";
		int position = "SELECT AVG(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Restriction_02() throws Exception {
		String jpqlQuery = "SELECT AVG(e.name) FROM Employee e";
		int position = "SELECT AVG(e".length();

		List<String> proposals = new ArrayList<String>();
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		List<String> selectProposals = new ArrayList<String>();
		addAll(selectProposals, filter(bnfAccessor.selectItemIdentifiers(), "e"));
		selectProposals.removeAll(proposals);

		testDoesNotHaveTheseProposals(jpqlQuery, position, selectProposals);
	}

	@Test
	public void test_Restriction_03() throws Exception {
		String jpqlQuery = "SELECT AVG(e.name) FROM Employee e";
		int position = "SELECT AVG(e.".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, bnfAccessor.selectItemIdentifiers());
	}

	@Test
	public void test_Restriction_04() {
		String jpqlQuery = "SELECT o FROM Countries AS o";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public void test_Select_01() throws Exception {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.selectItemFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Select_02() throws Exception {

		String jpqlQuery = "SELECT e";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		addAll(proposals, filter(bnfAccessor.selectItemFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Select_03() throws Exception {
		String jpqlQuery = "SELECT  FROM Employee e";
		int position = "SELECT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		proposals.add("e");
		addAll(proposals, bnfAccessor.selectItemFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Select_04() throws Exception {
		String jpqlQuery = "SELECT AV FROM Employee e";
		int position = "SELECT AV".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AVG);
	}

	@Test
	public void test_Select_05() throws Exception {
		String jpqlQuery = "SELECT e,";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, bnfAccessor.selectItemFunctions());
	}

	@Test
	public void test_Select_06() throws Exception {
		String jpqlQuery = "SELECT e, ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, bnfAccessor.selectItemFunctions());
	}

	@Test
	public void test_Select_07() throws Exception {

		String jpqlQuery = SELECT;

		for (int position = 1, count = jpqlQuery.length(); position < count; position++) {
			testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
		}
	}

	@Test
	public void test_SelectItem_01() {
		String jpqlQuery = "SELECT o,  FROM Address o";
		int position = "SELECT o, ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("o");
		addAll(proposals, bnfAccessor.selectItemFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SelectItem_02() {
		String jpqlQuery = "SELECT O, CASE WHEN c.firstName = 'Pascal' THEN 'P' ELSE 'JPQL' END FROM Customer c";
		int position = "SELECT O".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(bnfAccessor.selectItemIdentifiers(), "O"));
	}

	@Test
	public void test_Size_01() {
		test_AbstractSingleEncapsulatedExpression_01(SIZE);
	}

	@Test
	public void test_Size_02() {
		test_AbstractSingleEncapsulatedExpression_02(SIZE);
	}

	@Test
	public void test_Size_03() {
		test_AbstractSingleEncapsulatedExpression_03(SIZE);
	}

	@Test
	public void test_Size_04() {
		test_AbstractSingleEncapsulatedExpression_04(SIZE);
	}

	@Test
	public void test_Size_05() {
		test_AbstractSingleEncapsulatedExpression_05(SIZE);
	}

	@Test
	public void test_Size_06() {
		test_AbstractSingleEncapsulatedExpression_06(SIZE);
	}

	@Test
	public void test_Size_07() {
		test_AbstractSingleEncapsulatedExpression_07(SIZE);
	}

	@Test
	public void test_Size_08() {
		test_AbstractSingleEncapsulatedExpression_08(SIZE);
	}

	@Test
	public void test_Size_09() {
		test_AbstractSingleEncapsulatedExpression_09(SIZE);
	}

	@Test
	public void test_Size_10() {
		test_AbstractSingleEncapsulatedExpression_10(SIZE);
	}

	@Test
	public void test_Some_01() {
		test_AbstractSingleEncapsulatedExpression_01(SOME);
	}

	@Test
	public void test_Some_02() {
		test_AbstractSingleEncapsulatedExpression_02(SOME);
	}

	@Test
	public void test_Some_03() {
		test_AbstractSingleEncapsulatedExpression_03(SOME);
	}

	@Test
	public void test_Some_04() {
		test_AbstractSingleEncapsulatedExpression_04(SOME);
	}

	@Test
	public void test_Some_05() {
		test_AbstractSingleEncapsulatedExpression_05(SOME);
	}

	@Test
	public void test_Some_06() {
		test_AbstractSingleEncapsulatedExpression_06(SOME);
	}

	@Test
	public void test_Some_07() {
		test_AbstractSingleEncapsulatedExpression_07(SOME);
	}

	@Test
	public void test_Some_08() {
		test_AbstractSingleEncapsulatedExpression_08(SOME);
	}

	@Test
	public void test_Some_09() {
		test_AbstractSingleEncapsulatedExpression_09(SOME);
	}

	@Test
	public void test_Some_10() {
		test_AbstractSingleEncapsulatedExpression_10(SOME);
	}

	@Test
	public void test_Sqrt_01() {
		test_AbstractSingleEncapsulatedExpression_01(SQRT);
	}

	@Test
	public void test_Sqrt_02() {
		test_AbstractSingleEncapsulatedExpression_02(SQRT);
	}

	@Test
	public void test_Sqrt_03() {
		test_AbstractSingleEncapsulatedExpression_03(SQRT);
	}

	@Test
	public void test_Sqrt_04() {
		test_AbstractSingleEncapsulatedExpression_04(SQRT);
	}

	@Test
	public void test_Sqrt_05() {
		test_AbstractSingleEncapsulatedExpression_05(SQRT);
	}

	@Test
	public void test_Sqrt_06() {
		test_AbstractSingleEncapsulatedExpression_06(SQRT);
	}

	@Test
	public void test_Sqrt_07() {
		test_AbstractSingleEncapsulatedExpression_07(SQRT);
	}

	@Test
	public void test_Sqrt_08() {
		test_AbstractSingleEncapsulatedExpression_08(SQRT);
	}

	@Test
	public void test_Sqrt_09() {
		test_AbstractSingleEncapsulatedExpression_09(SQRT);
	}

	@Test
	public void test_Sqrt_10() {
		test_AbstractSingleEncapsulatedExpression_10(SQRT);
	}

	@Test
	public void test_StateFieldPath_01() throws Exception {
		String jpqlQuery = "SELECT c. FROM CodeAssist c";
		int position = "SELECT c.".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "id", "name", "manager");
	}

	@Test
	public void test_StateFieldPath_02() throws Exception {
		String jpqlQuery = "SELECT c.name FROM CodeAssist c";
		int position = "SELECT c.n".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "name");
	}

	@Test
	public void test_StateFieldPath_03() throws Exception {
		String jpqlQuery = "SELECT c.name FROM CodeAssist c";
		int position = "SELECT c.name".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "name");
	}

	@Test
	public void test_StateFieldPath_04() throws Exception {
		String jpqlQuery = "SELECT c.manager.name FROM CodeAssist c";
		int position = "SELECT c.m".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "manager");
	}

	@Test
	public void test_StateFieldPath_05() throws Exception {
		String jpqlQuery = "SELECT c.manager.name FROM CodeAssist c";
		int position = "SELECT c.manager".length();
		testHasTheseProposals(jpqlQuery, position, "manager");
	}

	@Test
	public void test_StateFieldPath_06() throws Exception {
		String jpqlQuery = "SELECT c.manager.name FROM CodeAssist c";
		int position = "SELECT c.manager.".length();
		testHasTheseProposals(jpqlQuery, position, "department", "empId", "manager", "name", "salary", "dept");
	}

	@Test
	public void test_StateFieldPath_07() throws Exception {
		String jpqlQuery = "SELECT c.manager.name FROM CodeAssist c";
		int position = "SELECT c.manager.name".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "name");
	}

	@Test
	public void test_StateFieldPath_08() throws Exception {
		String jpqlQuery = "SELECT c.employees. FROM CodeAssist c";
		int position = "SELECT c.employees.".length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public void test_StateFieldPath_09() throws Exception {
		String jpqlQuery = "SELECT e. FROM CodeAssist c JOIN c.employees e";
		int position = "SELECT e.".length();
		testHasTheseProposals(jpqlQuery, position, "department", "empId", "manager", "name", "salary", "dept");
	}

	@Test
	public void test_StateFieldPath_10() throws Exception {
		String jpqlQuery = "SELECT e. FROM CodeAssist c, IN c.employees e";
		int position = "SELECT e.".length();
		testHasTheseProposals(jpqlQuery, position, "department", "empId", "manager", "name", "salary", "dept");
	}

	@Test
	public void test_StateFieldPath_11() throws Exception {
		String jpqlQuery = "SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a";
		int position = "SELECT a.".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "alias", "customer", "id");
	}

	@Test
	public void test_StateFieldPath_12() throws Exception {
		String jpqlQuery = "SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a";
		int position = "SELECT a.alias".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "alias");
	}

	@Test
	public void test_StateFieldPath_13() throws Exception {
		String jpqlQuery = "SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a";
		int position = "SELECT a.alias FROM CodeAssist c, IN c.".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "employees", "customerMapAddress", "customerMap", "manager");
	}

	@Test
	public void test_StateFieldPath_14() throws Exception {
		String jpqlQuery = "SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a";
		int position = "SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "cust");
	}

	@Test
	public void test_StateFieldPath_15() throws Exception {
		String jpqlQuery = "SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a";
		int position = "SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "address", "home", "dept", "phoneList", "aliases");
	}

	@Test
	public void test_Subquery_01() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.comparisonExpressionFunctions());
		addAll(proposals, bnfAccessor.comparisonExpressionClauses());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Subquery_02() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (S".length();
		testHasTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public void test_Subquery_03() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public void test_Subquery_04() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SEL".length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public void test_Subquery_05() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public void test_Subquery_06() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELEC".length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public void test_Subquery_07() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT".length();
		testHasTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public void test_Subquery_08() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT ";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT".length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public void test_Subquery_09() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT A";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, ABS, AVG);
	}

	@Test
	public void test_Subquery_10() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(AS);
		proposals.add(FROM);
		addAll(proposals, bnfAccessor.selectItemAggregates());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Subquery_StateFieldPath_01() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "empId", "roomNumber", "salary", "address", "dept", "managerEmployee");
	}

	@Test
	public void test_Substring_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_Substring_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE S";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_Substring_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_Substring_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUB";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_Substring_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_Substring_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBST";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_Substring_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_Substring_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_Substring_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_Substring_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRIN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_Substring_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRING";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public void test_SumFunction_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUM);
	}

	@Test
	public void test_SumFunction_02() {
		String jpqlQuery = "SELECT S";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUM);
	}

	@Test
	public void test_SumFunction_03() {
		String jpqlQuery = "SELECT SU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUM);
	}

	@Test
	public void test_SumFunction_04() {
		String jpqlQuery = "SELECT SUM";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUM);
	}

	@Test
	public void test_SumFunction_05() {

		String jpqlQuery = "SELECT SUM(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SumFunction_06() {

		String jpqlQuery = "SELECT SUM() From Employee e";
		int position = "SELECT SUM(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SumFunction_07() {

		String jpqlQuery = "SELECT SUM(DISTINCT ) From Employee e";
		int position = "SELECT SUM(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SumFunction_08() {
		String jpqlQuery = "SELECT SUM(D ) From Employee e";
		int position = "SELECT SUM(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_SumFunction_09() {
		String jpqlQuery = "SELECT SUM(DI ) From Employee e";
		int position = "SELECT SUM(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_SumFunction_10() {
		String jpqlQuery = "SELECT SUM(DIS ) From Employee e";
		int position = "SELECT SUM(DIS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_SumFunction_11() {
		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_SumFunction_12() {
		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_SumFunction_13() {
		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public void test_SumFunction_14() {

		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SumFunction_15() {

		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SumFunction_16() {

		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee emp";
		int position = "SELECT SUM(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SumFunction_17() {

		String jpqlQuery = "SELECT SUM() From Employee emp";
		int position = "SELECT SUM(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SumFunction_18() {

		String jpqlQuery = "SELECT SUM(e) From Employee emp";
		int position = "SELECT SUM(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SumFunction_19() {
		String jpqlQuery = "SELECT SUM(em) From Employee emp";
		int position = "SELECT SUM(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_SumFunction_20() {
		String jpqlQuery = "SELECT SUM(emp) From Employee emp";
		int position = "SELECT SUM(emp".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_SumFunction_21() {

		String jpqlQuery = "SELECT SUM(emp) From Employee emp";
		int position = "SELECT SUM(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SumFunction_22() {
		String jpqlQuery = "SELECT SUM(emp) From Employee emp";
		int position = "SELECT SUM(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public void test_SumFunction_23() {

		String jpqlQuery = "SELECT SUM( From Employee emp";
		int position = "SELECT SUM(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_SumFunction_24() {

		String jpqlQuery = "SELECT SUM(e From Employee emp";
		int position = "SELECT SUM(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public void test_Trim_001() {
		test_AbstractSingleEncapsulatedExpression_01(TRIM);
	}

	@Test
	public void test_Trim_002() {
		test_AbstractSingleEncapsulatedExpression_02(TRIM);
	}

	@Test
	public void test_Trim_003() {
		test_AbstractSingleEncapsulatedExpression_03(TRIM);
	}

	@Test
	public void test_Trim_004() {
		test_AbstractSingleEncapsulatedExpression_04(TRIM);
	}

	@Test
	public void test_Trim_005() {
		test_AbstractSingleEncapsulatedExpression_05(TRIM);
	}

	@Test
	public void test_Trim_006() {
		test_AbstractSingleEncapsulatedExpression_06(TRIM);
	}

	@Test
	public void test_Trim_007() {
		test_AbstractSingleEncapsulatedExpression_07(TRIM);
	}

	@Test
	public void test_Trim_008() {
		test_AbstractSingleEncapsulatedExpression_08(TRIM);
	}

	@Test
	public void test_Trim_009() {
		test_AbstractSingleEncapsulatedExpression_09(TRIM);
	}

	@Test
	public void test_Trim_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRIM);
	}

	@Test
	public void test_Trim_010() {
		test_AbstractSingleEncapsulatedExpression_10(TRIM);
	}

	@Test
	public void test_Trim_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE T";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRIM);
	}

	@Test
	public void test_Trim_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRIM);
	}

	@Test
	public void test_Trim_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRIM);
	}

	@Test
	public void test_Trim_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRIM);
	}

	@Test
	public void test_Trim_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public void test_Trim_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(B";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, BOTH);
	}

	@Test
	public void test_Trim_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(BO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, BOTH);
	}

	@Test
	public void test_Trim_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(BOT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, BOTH);
	}

	@Test
	public void test_Trim_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(BOTH";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BOTH);
	}

	@Test
	public void test_Trim_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(L";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public void test_Trim_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public void test_Trim_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LEA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public void test_Trim_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LEAD";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public void test_Trim_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LEADI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public void test_Trim_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LEADIN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public void test_Trim_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LEADING";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public void test_Trim_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(T";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public void test_Trim_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public void test_Trim_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public void test_Trim_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public void test_Trim_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAIL";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public void test_Trim_23() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public void test_Trim_24() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILIN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public void test_Trim_25() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public void test_Trim_26() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_Trim_27() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' F";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_Trim_28() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_Trim_29() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FRO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_Trim_30() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FROM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_Trim_31() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FROM ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, "e");
	}

	@Test
	public void test_Trim_32() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FROM";
		int position = jpqlQuery.length() - 1;
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public void test_Type_01() {
		test_AbstractSingleEncapsulatedExpression_01(TYPE);
	}

	@Test
	public void test_Type_02() {
		test_AbstractSingleEncapsulatedExpression_02(TYPE);
	}

	@Test
	public void test_Type_03() {
		test_AbstractSingleEncapsulatedExpression_03(TYPE);
	}

	@Test
	public void test_Type_04() {
		test_AbstractSingleEncapsulatedExpression_04(TYPE);
	}

	@Test
	public void test_Type_05() {
		test_AbstractSingleEncapsulatedExpression_05(TYPE);
	}

	@Test
	public void test_Type_06() {
		test_AbstractSingleEncapsulatedExpression_06(TYPE);
	}

	@Test
	public void test_Type_07() {
		test_AbstractSingleEncapsulatedExpression_07(TYPE);
	}

	@Test
	public void test_Type_08() {
		test_AbstractSingleEncapsulatedExpression_08(TYPE);
	}

	@Test
	public void test_Type_09() {
		test_AbstractSingleEncapsulatedExpression_09(TYPE);
	}

	@Test
	public void test_Type_10() {
		test_AbstractSingleEncapsulatedExpression_10(TYPE);
	}

	@Test
	public void test_Update_01() {
		String jpqlQuery = "U";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_02() {
		String jpqlQuery = "UP";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_03() {
		String jpqlQuery = "UPD";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_04() {
		String jpqlQuery = "UPDA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_05() {
		String jpqlQuery = "UPDAT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_06() {
		String jpqlQuery = UPDATE;
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_07() {
		String jpqlQuery = "UPDATE Employee";
		int position = "U".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_08() {
		String jpqlQuery = "UPDATE Employee";
		int position = "UP".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_09() {
		String jpqlQuery = "UPDATE Employee";
		int position = "UPD".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_10() {
		String jpqlQuery = "UPDATE Employee";
		int position = "UPDA".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_11() {
		String jpqlQuery = "UPDATE Employee";
		int position = "UPDAT".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_12() {
		String jpqlQuery = "UPDATE Employee";
		int position = "UPDATE".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public void test_Update_13() {
		String jpqlQuery = "UPDATE Employee e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public void test_Update_14() {
		String jpqlQuery = "UPDATE Employee e S";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public void test_Update_15() {
		String jpqlQuery = "UPDATE Employee e SE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public void test_Update_16() {
		String jpqlQuery = "UPDATE Employee e SET";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public void test_Update_17() {
		String jpqlQuery = "UPDATE SET";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public void test_Update_18() throws Exception {
		String jpqlQuery = "UPDATE S";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "S"));
	}

	@Test
	public void test_Update_19() {
		String jpqlQuery = "UPDATE Employee S";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public void test_Update_20() {
		String jpqlQuery = "UPDATE Employee S SET";
		int position = "UPDATE Employee S".length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public void test_Update_21() {
		String jpqlQuery = "UPDATE Z";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public void test_Update_22() throws Exception {
		String jpqlQuery = "UPDATE A";
		int position = "UPDATE A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "A"));
	}

	@Test
	public void test_Update_23() {
		String jpqlQuery = "UPDATE Employee e SET ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e");
	}

	@Test
	public void test_Update_24() {
		String jpqlQuery = "UPDATE Employee SET e";
		int position = "UPDATE Employee SET ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "employee");
	}

	@Test
	public void test_Update_25() throws Exception {
		String jpqlQuery = "UPDATE ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public void test_Update_26() throws Exception {
		String jpqlQuery = "UPDATE Alias a";
		int position = "UPDATE ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public void test_Update_27() throws Exception {
		String jpqlQuery = "UPDATE Alias a";
		int position = "UPDATE Al".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "Alias");
	}

	@Test
	public void test_Update_28() {
		String jpqlQuery = "UPDATE Employee A SET";
		int position = "UPDATE Employee A ".length();
		testHasTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public void test_Update_30() {
		String jpqlQuery = "UPDATE Employee A ";
		int position = jpqlQuery.length() - 1;
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public void test_Update_31() {
		String jpqlQuery = "UPDATE Employee AS ";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public void test_Update_32() throws Exception {
		String jpqlQuery = "UPDATE Employee AS e SET e.";
		int position = "UPDATE Employee AS e SET e.".length();
		testHasTheseProposals(jpqlQuery, position, "department", "empId", "manager", "name", "salary", "dept");
	}

	@Test
	public void test_Upper_01() {
		test_AbstractSingleEncapsulatedExpression_01(UPPER);
	}

	@Test
	public void test_Upper_02() {
		test_AbstractSingleEncapsulatedExpression_02(UPPER);
	}

	@Test
	public void test_Upper_03() {
		test_AbstractSingleEncapsulatedExpression_03(UPPER);
	}

	@Test
	public void test_Upper_04() {
		test_AbstractSingleEncapsulatedExpression_04(UPPER);
	}

	@Test
	public void test_Upper_05() {
		test_AbstractSingleEncapsulatedExpression_05(UPPER);
	}

	@Test
	public void test_Upper_06() {
		test_AbstractSingleEncapsulatedExpression_06(UPPER);
	}

	@Test
	public void test_Upper_07() {
		test_AbstractSingleEncapsulatedExpression_07(UPPER);
	}

	@Test
	public void test_Upper_08() {
		test_AbstractSingleEncapsulatedExpression_08(UPPER);
	}

	@Test
	public void test_Upper_09() {
		test_AbstractSingleEncapsulatedExpression_09(UPPER);
	}

	@Test
	public void test_Upper_10() {
		test_AbstractSingleEncapsulatedExpression_10(UPPER);
	}

	@Test
	public void test_Upper_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE UPPER";
		testHasTheseProposals(jpqlQuery, jpqlQuery.length(), UPPER);
	}

	@Test
	public void test_Value_01() {
		test_AbstractSingleEncapsulatedExpression_01(VALUE);
	}

	@Test
	public void test_Value_02() {
		test_AbstractSingleEncapsulatedExpression_02(VALUE);
	}

	@Test
	public void test_Value_03() {
		test_AbstractSingleEncapsulatedExpression_03(VALUE);
	}

	@Test
	public void test_Value_04() {
		test_AbstractSingleEncapsulatedExpression_04(VALUE);
	}

	@Test
	public void test_Value_05() {
		test_AbstractSingleEncapsulatedExpression_05(VALUE);
	}

	@Test
	public void test_Value_06() {
		test_AbstractSingleEncapsulatedExpression_06(VALUE);
	}

	@Test
	public void test_Value_07() {
		test_AbstractSingleEncapsulatedExpression_07(VALUE);
	}

	@Test
	public void test_Value_08() {
		test_AbstractSingleEncapsulatedExpression_08(VALUE);
	}

	@Test
	public void test_Value_09() {
		test_AbstractSingleEncapsulatedExpression_09(VALUE);
	}

	@Test
	public void test_Value_10() {
		test_AbstractSingleEncapsulatedExpression_10(VALUE);
	}

	@Test
	public void test_Where_01() {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE COUNT(e) >= 5";
		int position = "SELECT e FROM Employee e ".length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_03() {
		String jpqlQuery = "SELECT e FROM Employee e W";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_04() {
		String jpqlQuery = "SELECT e FROM Employee e WH";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHER";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_08() {
		String jpqlQuery = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM E".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_09() {
		String jpqlQuery = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_10() {
		String jpqlQuery = "SELECT w FROM Employee AS w";
		int position = "SELECT w FROM Employee ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_11() {
		String jpqlQuery = "SELECT w FROM Employee AS w";
		int position = "SELECT w FROM Employee A".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_12() {
		String jpqlQuery = "SELECT w FROM Employee AS w";
		int position = "SELECT w FROM Employee AS".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_13() {
		String jpqlQuery = "SELECT w FROM Employee AS w";
		int position = "SELECT w FROM Employee AS ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_14() {
		String jpqlQuery = "SELECT w FROM Employee AS w";
		int position = "SELECT w FROM Employee AS ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_15() {
		String jpqlQuery = "SELECT w FROM Employee AS w ";
		int position = "SELECT w FROM Employee AS w ".length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public void test_Where_16() {
		String jpqlQuery = "SELECT e FROM Employee e J";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	protected final void testDoesNotHaveTheseProposals(String jpqlQuery, int position, Enum<?>... proposals) {
		testDoesNotHaveTheseProposals(jpqlQuery, position, toString(proposals));
	}

	protected final void testDoesNotHaveTheseProposals(String jpqlQuery, int position, Iterable<String> proposals) {

		DefaultContentAssistProposals contentAssistProposals = buildContentAssistProposals(jpqlQuery, position);
		List<String> proposalsRemoved = new ArrayList<String>();

		for (String proposal : proposals) {
			if (contentAssistProposals.remove(proposal)) {
				proposalsRemoved.add(proposal);
			}
		}

		assertTrue(proposalsRemoved + " should not be proposals.", proposalsRemoved.isEmpty());
	}

	protected final void testDoesNotHaveTheseProposals(String jpqlQuery, int position, String... proposals) {
		testDoesNotHaveTheseProposals(jpqlQuery, position, Arrays.asList(proposals));
	}

	protected final void testHasNoProposals(String jpqlQuery, int position) {
		DefaultContentAssistProposals proposals = buildContentAssistProposals(jpqlQuery, position);
		assertFalse(proposals + " should not be proposals.", proposals.hasProposals());
	}

	protected final void testHasOnlyTheseProposals(String jpqlQuery, int position, Enum<?>... proposals) {
		testHasOnlyTheseProposals(jpqlQuery, position, toString(proposals));
	}

	protected final void testHasOnlyTheseProposals(String jpqlQuery, int position, Iterable<String> proposals) {

		DefaultContentAssistProposals contentAssistProposals = buildContentAssistProposals(jpqlQuery, position);
		List<String> proposalsNotRemoved = new ArrayList<String>();

		for (String proposal : proposals) {
			if (!contentAssistProposals.remove(proposal)) {
				proposalsNotRemoved.add(proposal);
			}
		}

		// Inconsistent list of proposals
		if (contentAssistProposals.hasProposals() && !proposalsNotRemoved.isEmpty()) {
			if (proposalsNotRemoved.size() == 1) {
				fail(proposalsNotRemoved + " should be a proposal and " + contentAssistProposals + " should not be a proposal.");
			}
			else {
				fail(proposalsNotRemoved + " should be proposals and " + contentAssistProposals + " should not be proposals.");
			}
		}
		// Added more proposals than it should
		else if (contentAssistProposals.hasProposals() && proposalsNotRemoved.isEmpty()) {
			if (proposalsNotRemoved.size() == 1) {
				fail(contentAssistProposals + " should not be a proposal.");
			}
			else {
				fail(contentAssistProposals + " should not be proposals.");
			}
		}
		// Forgot to add some proposals
		else if (!proposalsNotRemoved.isEmpty()) {
			if (proposalsNotRemoved.size() == 1) {
				fail(proposalsNotRemoved + " should be a proposal.");
			}
			else {
				fail(proposalsNotRemoved + " should be proposals.");
			}
		}
	}

	protected final void testHasOnlyTheseProposals(String jpqlQuery, int position, String... proposals) {
		testHasOnlyTheseProposals(jpqlQuery, position, Arrays.asList(proposals));
	}

	protected final void testHasTheseProposals(String jpqlQuery, int position, Enum<?>... enums) {
		testHasTheseProposals(jpqlQuery, position, toString(enums));
	}

	protected final void testHasTheseProposals(String jpqlQuery, int position, Iterable<String> proposals) {

		DefaultContentAssistProposals contentAssistProposals = buildContentAssistProposals(jpqlQuery, position);
		List<String> proposalsNotRemoved = new ArrayList<String>();

		for (String proposal : proposals) {
			if (!contentAssistProposals.remove(proposal)) {
				proposalsNotRemoved.add(proposal);
			}
		}

		assertTrue(proposalsNotRemoved + " should be proposals.", proposalsNotRemoved.isEmpty());
	}

	protected final void testHasTheseProposals(String jpqlQuery, int position, String... proposals) {
		testHasTheseProposals(jpqlQuery, position, Arrays.asList(proposals));
	}

	protected final String[] toString(Enum<?>[] enums) {

		String[] names = new String[enums.length];

		for (int index = enums.length; --index >= 0; ) {
			names[index] = enums[index].name();
		}

		return names;
	}
}