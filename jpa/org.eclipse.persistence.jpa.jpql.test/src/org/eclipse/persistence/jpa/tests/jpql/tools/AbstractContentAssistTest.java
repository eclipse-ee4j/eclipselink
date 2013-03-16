/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jpql.query.Address;
import jpql.query.Alias;
import jpql.query.CodeAssist;
import jpql.query.Customer;
import jpql.query.Employee;
import jpql.query.Product;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;
import org.junit.Assert;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This unit-test tests the JPQL content assist at various position within the JPQL query and with
 * complete and incomplete queries.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractContentAssistTest extends ContentAssistTest {

	protected List<String> clauses(String afterIdentifier, String beforeIdentifier, boolean subquery) {

		List<String> proposals = new ArrayList<String>();

		if (afterIdentifier == SELECT) {

			if (beforeIdentifier != FROM) {
				proposals.add(FROM);
			}

			if (beforeIdentifier != FROM &&
			    beforeIdentifier != WHERE) {

				proposals.add(WHERE);
			}

			if (beforeIdentifier != FROM  &&
			    beforeIdentifier != WHERE &&
			    beforeIdentifier != GROUP_BY) {

				proposals.add(GROUP_BY);
			}

			if (beforeIdentifier != FROM     &&
			    beforeIdentifier != WHERE    &&
			    beforeIdentifier != GROUP_BY &&
			    beforeIdentifier != HAVING) {

				proposals.add(HAVING);
			}

			if (!subquery) {
				if (beforeIdentifier != FROM     &&
				    beforeIdentifier != WHERE    &&
				    beforeIdentifier != GROUP_BY &&
				    beforeIdentifier != HAVING   &&
				    beforeIdentifier != ORDER_BY) {

					proposals.add(ORDER_BY);
				}
			}
		}
		else if (afterIdentifier == FROM) {

			if (beforeIdentifier != WHERE) {
				proposals.add(WHERE);
			}

			if (beforeIdentifier != WHERE &&
			    beforeIdentifier != GROUP_BY) {

				proposals.add(GROUP_BY);
			}

			if (beforeIdentifier != WHERE    &&
			    beforeIdentifier != GROUP_BY &&
			    beforeIdentifier != HAVING) {

				proposals.add(HAVING);
			}

			if (!subquery) {
				if (beforeIdentifier != WHERE    &&
				    beforeIdentifier != GROUP_BY &&
				    beforeIdentifier != HAVING   &&
				    beforeIdentifier != ORDER_BY) {

					proposals.add(ORDER_BY);
				}
			}
		}
		else if (afterIdentifier == WHERE) {

			if (beforeIdentifier != GROUP_BY) {
				proposals.add(GROUP_BY);
			}

			if (beforeIdentifier != GROUP_BY &&
			    beforeIdentifier != HAVING) {

				proposals.add(HAVING);
			}

			if (!subquery) {
				if (beforeIdentifier != GROUP_BY &&
				    beforeIdentifier != HAVING   &&
				    beforeIdentifier != ORDER_BY) {

					proposals.add(ORDER_BY);
				}
			}
		}
		else if (afterIdentifier == GROUP_BY) {

			if (beforeIdentifier != HAVING) {
				proposals.add(HAVING);
			}

			if (!subquery) {
				if (beforeIdentifier != HAVING   &&
				    beforeIdentifier != ORDER_BY) {

					proposals.add(ORDER_BY);
				}
			}
		}
		else if (afterIdentifier == HAVING) {
			if (!subquery) {
				if (beforeIdentifier != ORDER_BY) {
					proposals.add(ORDER_BY);
				}
			}
		}

		return proposals;
	}

	protected List<String> fromClauseInternalClauses(String afterIdentifier) {

		if (afterIdentifier == FROM) {
			return joinIdentifiers();
		}

		return Collections.emptyList();
	}

	private Iterable<String> functionProposals() {

		List<String> identifiers = CollectionTools.list(
			bnfAccessor.conditionalExpressionsFunctions().iterator()
		);

		Collections.sort(identifiers);
		return identifiers;
	}

	/**
	 * Determines whether a <code><b>JOIN FETCH</b></code> expression can be identified with an
	 * identification variable.
	 *
	 * @return <code>true</code> if it can be identified by an identification variable;
	 * <code>false</code> otherwise
	 */
	protected abstract boolean isJoinFetchIdentifiable();

	@Test
	public final void test_AbstractSchemaName_01() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e";
		int position = "SELECT e FROM ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public final void test_AbstractSchemaName_02() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e";
		int position = "SELECT e FROM E".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "E"));
	}

	@Test
	public final void test_AbstractSchemaName_03() throws Exception {
		String jpqlQuery = "SELECT AVG(e.roomNumber) FROM ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public final void test_AbstractSchemaName_04() throws Exception {
		String jpqlQuery = "SELECT e FROM ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public final void test_AbstractSchemaName_05() throws Exception {
		String jpqlQuery = "SELECT e FROM E";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "E"));
	}

	@Test
	public final void test_AbstractSchemaName_06() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, ";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		CollectionTools.addAll(proposals, entityNames());
		CollectionTools.addAll(proposals, classNames());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AbstractSchemaName_07() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, Address a";
		int position = "SELECT e FROM ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public final void test_AbstractSchemaName_08() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public final void test_AbstractSchemaName_09() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, Address a ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public final void test_AbstractSchemaName_10() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = "SELECT e FROM ".length();
		testHasTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public final void test_AbstractSchemaName_11() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = "SELECT e FROM E".length();
		testHasTheseProposals(jpqlQuery, position, filter(entityNames(), "f"));
	}

	@Test
	public final void test_AbstractSchemaName_12() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = "SELECT e FROM Employee".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "Employee"));
	}

	@Test
	public final void test_AbstractSchemaName_13() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, Address a ";
		int position = "SELECT e FROM Employee e, ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		CollectionTools.addAll(proposals, entityNames());
		CollectionTools.addAll(proposals, classNames());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AbstractSchemaName_14() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, Address a ";
		int position = "SELECT e FROM Employee e, A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "A"));
	}

	@Test
	public final void test_AbstractSchemaName_15() {
		String jpqlQuery = "SELECT e FROM Employee e, I";
		int position = "SELECT e FROM Employee e, I".length();
		testHasOnlyTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public final void test_AbstractSchemaName_16() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e, A";
		int position = "SELECT e FROM Employee e, A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "A"));
	}

	@Test
	public final void test_AbstractSchemaName_17() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e";
		int position = "SELECT e FROM Employee ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		CollectionTools.addAll(proposals, entityNames());

		testDoesNotHaveTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AbstractSchemaName_18() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, , Address a";
		int position = "SELECT e FROM Employee e,".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		CollectionTools.addAll(proposals, entityNames());
		CollectionTools.addAll(proposals, classNames());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AbstractSchemaName_19() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, , Address a";
		int position = "SELECT e FROM Employee e, ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		CollectionTools.addAll(proposals, entityNames());
		CollectionTools.addAll(proposals, classNames());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AbstractSchemaName_20() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e,, Address a";
		int position = "SELECT e FROM Employee e,".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(IN);
		CollectionTools.addAll(proposals, entityNames());
		CollectionTools.addAll(proposals, classNames());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AbstractSchemaName_21() {

		String jpqlQuery = "SELECT e FROM Employee e J";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, filter(fromClauseInternalClauses(FROM), "J"));
		CollectionTools.addAll(proposals, filter(clauses(FROM, null, false), "J"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AbstractSchemaName_22() {

		String jpqlQuery = "SELECT e FROM Employee e E";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, filter(fromClauseInternalClauses(FROM), "e"));
		CollectionTools.addAll(proposals, filter(clauses(FROM, null, false), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AbstractSchemaName_23() {
		String jpqlQuery = "SELECT e FROM Employee e Order b, Address e";
		int position = "SELECT e FROM Employee e Order b".length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_AbstractSchemaName_24() {
		String jpqlQuery = "SELECT e FROM Employee e Order b WHERE e.name = 'JPQL'";
		int position = "SELECT e FROM Employee e Order b".length();
		testHasNoProposals(jpqlQuery, position);
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
	public final void test_As_01() {
		String jpqlQuery = "SELECT o FROM Countries ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_As_02() {
		String jpqlQuery = "SELECT o FROM Countries o";
		int position = "SELECT o FROM Countries ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_As_03() {
		String jpqlQuery = "SELECT o FROM Countries a";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_As_04() {
		String jpqlQuery = "SELECT o FROM Countries j";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_As_05() {
		String jpqlQuery = "SELECT o FROM Countries o ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_As_06() {
		String jpqlQuery = "SELECT o FROM Countries A o";
		int position = "SELECT o FROM Countries A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_As_07() {
		String jpqlQuery = "SELECT o FROM Countries AS o";
		int position = "SELECT o FROM Countries A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_As_08() {
		String jpqlQuery = "SELECT o FROM Countries AS o";
		int position = "SELECT o FROM Countries AS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_As_09() {
		String jpqlQuery = "SELECT o FROM Countries AS o";
		int position = "SELECT o FROM Countries AS ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_As_10() {
		String jpqlQuery = "SELECT ABS(a.city)  FROM Address AS a";
		int position = "SELECT ABS(a.city) ".length();
		testHasTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_As_11() {
		String jpqlQuery = "SELECT ABS(a.city) AS FROM Address AS a";
		int position = "SELECT ABS(a.city) AS ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_As_12() {
		// To test GROUP BY is not added
		String jpqlQuery = "SELECT o FROM Countries g";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_As_13() {
		// To test HAVING is not added
		String jpqlQuery = "SELECT o FROM Countries h";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_As_14() {
		// To test ORDER BY is not added
		String jpqlQuery = "SELECT o FROM Countries o";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_As_15() {
		// To test GROUP BY/ORDER BY are not added
		String jpqlQuery = "SELECT o FROM Countries b";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_As_16() {
		// To test UNION is not added (specific to EclipseLink 2.4 or later)
		String jpqlQuery = "SELECT o FROM Countries u";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_As_17() {
		// To test START WITH is not added (specific to EclipseLink 2.5 or later)
		String jpqlQuery = "SELECT o FROM Countries s";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_As_18() {
		// To test AS OF is not added (specific to EclipseLink 2.5 or later)
		String jpqlQuery = "SELECT o FROM Countries AS o";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_AvgFunction_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AVG);
	}

	@Test
	public final void test_AvgFunction_02() {
		String jpqlQuery = "SELECT A";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AVG);
	}

	@Test
	public final void test_AvgFunction_03() {
		String jpqlQuery = "SELECT AV";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AVG);
	}

	@Test
	public final void test_AvgFunction_04() {
		String jpqlQuery = "SELECT AVG";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AVG);
	}

	@Test
	public final void test_AvgFunction_05() {

		String jpqlQuery = "SELECT AVG(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AvgFunction_06() {

		String jpqlQuery = "SELECT AVG() From Employee e";
		int position = "SELECT AVG(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AvgFunction_07() {

		String jpqlQuery = "SELECT AVG(DISTINCT ) From Employee e";
		int position = "SELECT AVG(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AvgFunction_08() {
		String jpqlQuery = "SELECT AVG(D ) From Employee e";
		int position = "SELECT AVG(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_AvgFunction_09() {
		String jpqlQuery = "SELECT AVG(DI ) From Employee e";
		int position = "SELECT AVG(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_AvgFunction_10() {
		String jpqlQuery = "SELECT AVG(DIS ) From Employee e";
		int position = "SELECT AVG(DIS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_AvgFunction_11() {
		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_AvgFunction_12() {
		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_AvgFunction_13() {
		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_AvgFunction_14() {

		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AvgFunction_15() {

		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AvgFunction_16() {
		String jpqlQuery = "SELECT AVG(DISTINCT e) From Employee emp";
		int position = "SELECT AVG(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AvgFunction_17() {

		String jpqlQuery = "SELECT AVG() From Employee emp";
		int position = "SELECT AVG(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AvgFunction_18() {

		String jpqlQuery = "SELECT AVG(e) From Employee emp";
		int position = "SELECT AVG(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AvgFunction_19() {
		String jpqlQuery = "SELECT AVG(em) From Employee emp";
		int position = "SELECT AVG(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_AvgFunction_20() {
		String jpqlQuery = "SELECT AVG(emp) From Employee emp";
		int position = "SELECT AVG(emp".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_AvgFunction_21() {

		String jpqlQuery = "SELECT AVG(emp) From Employee emp";
		int position = "SELECT AVG(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AvgFunction_22() {
		String jpqlQuery = "SELECT AVG(emp) From Employee emp";
		int position = "SELECT AVG(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_AvgFunction_23() {

		String jpqlQuery = "SELECT AVG( From Employee emp";
		int position = "SELECT AVG(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_AvgFunction_24() {

		String jpqlQuery = "SELECT AVG(e From Employee emp";
		int position = "SELECT AVG(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Between_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, BETWEEN, NOT_BETWEEN);
	}

	@Test
	public final void test_Between_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber B";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BET";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETW";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWEE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWEEN";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BETWEEN, NOT_BETWEEN);
	}

	@Test
	public final void test_Between_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber NOT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_BETWEEN);
	}

	@Test
	public final void test_Between_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber NOT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_BETWEEN);
	}

	@Test
	public final void test_Between_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber NOT B";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_BETWEEN);
	}

	@Test
	public final void test_Between_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWEEN 1 ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AND);
	}

	@Test
	public final void test_Between_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWEEN 1 A";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AND);
	}

	@Test
	public final void test_Between_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWEEN 1 AN";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AND);
	}

	@Test
	public final void test_Between_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWEEN 1 AND";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AND);
	}

	@Test
	public final void test_Between_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWEEN 1 AND ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AND);
	}

	@Test
	public final void test_Between_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber B";
		int position = "SELECT e FROM Employee e WHERE e.roomNumber ".length();
		testHasTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BE";
		int position = "SELECT e FROM Employee e WHERE e.roomNumber ".length();
		testHasTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BET";
		int position = "SELECT e FROM Employee e WHERE e.roomNumber ".length();
		testHasTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETW";
		int position = "SELECT e FROM Employee e WHERE e.roomNumber ".length();
		testHasTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWE";
		int position = "SELECT e FROM Employee e WHERE e.roomNumber ".length();
		testHasTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_32() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWEE";
		int position = "SELECT e FROM Employee e WHERE e.roomNumber ".length();
		testHasTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_Between_33() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWEEN";
		int position = "SELECT e FROM Employee e WHERE e.roomNumber ".length();
		testHasTheseProposals(jpqlQuery, position, BETWEEN);
	}

	@Test
	public final void test_CollectionMember_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public final void test_CollectionMember_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER, NOT_MEMBER);
	}

	@Test
	public final void test_CollectionMember_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e N";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_MEMBER);
	}

	@Test
	public final void test_CollectionMember_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e NO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_MEMBER);
	}

	@Test
	public final void test_CollectionMember_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_MEMBER);
	}

	@Test
	public final void test_CollectionMember_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_MEMBER);
	}

	@Test
	public final void test_CollectionMember_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT M";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public final void test_CollectionMember_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT ME";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public final void test_CollectionMember_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT MEM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public final void test_CollectionMember_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e NOT MEMB";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public final void test_CollectionMember_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e NOT MEMBE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public final void test_CollectionMember_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT MEMBER";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, MEMBER, MEMBER_OF, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public final void test_CollectionMember_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT MEMBER ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e", OF);
	}

	@Test
	public final void test_CollectionMember_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT MEMBER O";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, OF);
	}

	@Test
	public final void test_CollectionMember_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT MEMBER OF";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, MEMBER, MEMBER_OF, NOT_MEMBER_OF, NOT_MEMBER);
	}

	@Test
	public final void test_CollectionMember_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name M";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public final void test_CollectionMember_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ME";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public final void test_CollectionMember_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEM";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public final void test_CollectionMember_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMB";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public final void test_CollectionMember_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public final void test_CollectionMember_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MEMBER);
	}

	@Test
	public final void test_CollectionMember_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e", OF);
	}

	@Test
	public final void test_CollectionMember_23() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER O";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, OF);
	}

	@Test
	public final void test_CollectionMember_24() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER OF";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, MEMBER, MEMBER_OF, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public final void test_CollectionMember_25() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER OF ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e");
	}

	@Test
	public final void test_CollectionMember_26() {
		String jpqlQuery = "SELECT e FROM Employee e JOIN e.employees emp WHERE e MEMBER OF ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e", "emp");
	}

	@Test
	public final void test_CollectionMember_27() {
		String jpqlQuery = "SELECT e FROM Employee e JOIN e.employees emp WHERE e MEMBER ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e", "emp", OF);
	}

	@Test
	public final void test_CollectionMember_28() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name MEMBER OF e.employees";
		int position = jpqlQuery.length() - "EMBER OF e.employees".length();
		testHasOnlyTheseProposals(jpqlQuery, position, MEMBER, MEMBER_OF, NOT_MEMBER, NOT_MEMBER_OF);
	}

	@Test
	public final void test_CollectionMember_29() {
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
	public final void test_CollectionMemberDeclaration_01() {
		String jpqlQuery = "SELECT e FROM Employee e, I";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public final void test_CollectionMemberDeclaration_02() {
		String jpqlQuery = "SELECT e FROM ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public final void test_CollectionMemberDeclaration_03() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.names) AS f";
		int position = jpqlQuery.length() - "e, IN(e.names) AS f".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public final void test_CollectionMemberDeclaration_04() {
		String jpqlQuery = "SELECT e FROM Employee e, ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public final void test_CollectionMemberDeclaration_05() {
		String jpqlQuery = "SELECT e FROM Employee e, IN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public final void test_CollectionMemberDeclaration_06() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AS);
		testDoesNotHaveTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public final void test_CollectionMemberDeclaration_07() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) A";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_CollectionMemberDeclaration_08() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_CollectionMemberDeclaration_09() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_CollectionMemberDeclaration_10() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_CollectionMemberDeclaration_11() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, ".length();
		testHasTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public final void test_CollectionMemberDeclaration_12() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, I".length();
		testHasOnlyTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public final void test_CollectionMemberDeclaration_13() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN".length();
		testHasTheseProposals(jpqlQuery, position, IN);
	}

	@Test
	public final void test_CollectionMemberDeclaration_14() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.collectionMemberDeclarationParameters());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CollectionMemberDeclaration_16() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.collectionMemberDeclarationParameters());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CollectionMemberDeclaration_17() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.collectionMemberDeclarationParameters());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CollectionMemberDeclaration_18() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(e.name) ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_CollectionMemberDeclaration_19() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(e.name) A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_CollectionMemberDeclaration_20() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(e.name) AS ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_CollectionMemberDeclaration_23() {
		String jpqlQuery = "SELECT e FROM Employee e, IN(K";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, KEY);
	}

	@Test
	public final void test_CollectionMemberDeclaration_24() {

		String jpqlQuery = "SELECT e FROM Employee e, IN(KEY(a)) AS a";
		int position = "SELECT e FROM Employee e, IN(K".length();
		testHasOnlyTheseProposals(jpqlQuery, position, KEY);
	}

	@Test
	public final void test_Comparison_01() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, bnfAccessor.comparators());
	}

	@Test
	public final void test_Comparison_02() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, bnfAccessor.comparators());
	}

	@Test
	public final void test_Comparison_03() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber <";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, bnfAccessor.comparators());
	}

	@Test
	public final void test_Comparison_04() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber >";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, bnfAccessor.comparators());
	}

	@Test
	public final void test_Comparison_05() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber =";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, bnfAccessor.comparators());
	}

	@Test
	public final void test_Comparison_06() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber <=";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, bnfAccessor.comparators());
	}

	@Test
	public final void test_Comparison_07() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber >=";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, bnfAccessor.comparators());
	}

	@Test
	public final void test_Comparison_08() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber <>";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, bnfAccessor.comparators());
	}

	@Test
	public final void test_Comparison_09() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber IS ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, bnfAccessor.comparators());
	}

	@Test
	public final void test_Comparison_10() {

		String jpqlQuery = "delete from Address as a where current_date ";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, bnfAccessor.comparators());
		CollectionTools.addAll(proposals, bnfAccessor.arithmetics());
		CollectionTools.addAll(proposals, bnfAccessor.conditionalExpressionsCompoundFunctions());

		// These are filtered out
		proposals.remove(IS_EMPTY);
		proposals.remove(IS_NOT_EMPTY);

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CompoundFunction_01() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, bnfAccessor.conditionalExpressionsAggregates());
		CollectionTools.addAll(proposals, bnfAccessor.conditionalExpressionsCompoundFunctions());

		// These are filtered out
		proposals.remove(AND);
		proposals.remove(OR);
		proposals.remove(IS_EMPTY);
		proposals.remove(IS_NOT_EMPTY);
		removeAll(proposals, bnfAccessor.arithmetics());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Concat_34() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE CONCAT(e.name, AS e)";
		int position = "SELECT e FROM Employee e WHERE CONCAT(e.name, ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.internalConcatExpressionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Concat_35() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CONCAT(e.name, AS e)";
		int position = "SELECT e FROM Employee e WHERE CONCAT(e.name, A".length();
		Iterable<String> proposals = filter(bnfAccessor.internalConcatExpressionFunctions(), "A");
		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Concat_36() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CONCAT(e.name, AS e.name)";
		int position = "SELECT e FROM Employee e WHERE CONCAT(e.name, AS e.".length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_Concat_37() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CONCAT(e.name, S)";
		int position = "SELECT e FROM Employee e WHERE CONCAT(e.name, S".length();
		Iterable<String> proposals = filter(bnfAccessor.internalConcatExpressionFunctions(), "S");
		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Concat_38() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE CONCAT(e.name, ()";
		int position = "SELECT e FROM Employee e WHERE CONCAT(e.name, (".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.internalConcatExpressionFunctions());
		CollectionTools.addAll(proposals, bnfAccessor.internalConcatExpressionClauses());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Concat_39() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE CONCAT(e.name, (S)";
		int position = "SELECT e FROM Employee e WHERE CONCAT(e.name, (S".length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalConcatExpressionFunctions(), "S"));
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalConcatExpressionClauses(), "S"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_ConditionalExpression_01() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, nonTransientFieldNames(Employee.class));
	}

	@Test
	public final void test_Constructor_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_02() {
		String jpqlQuery = "SELECT N";
		int position = "SELECT ".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_03() {
		String jpqlQuery = "SELECT N";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_04() throws Exception {
		String jpqlQuery = "SELECT e, NEW (";
		int position = "SELECT e, ".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_05() throws Exception {
		String jpqlQuery = "SELECT e, NEW (";
		int position = "SELECT e, N".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_06() throws Exception {
		String jpqlQuery = "SELECT NEW String() From Employee e";
		int position = "SELECT NEW String(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.constructorItemFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Constructor_07() throws Exception {

		String jpqlQuery = "SELECT NEW String(e) From Employee e";
		int position = "SELECT NEW String(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, filter(bnfAccessor.constructorItemFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Constructor_08() {
		String jpqlQuery = "SELECT e, NEW java.lang.String(e.name)";
		int position = "SELECT e, N".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_09() {
		String jpqlQuery = "SELECT e, NEW java.lang.String(e.name)";
		int position = "SELECT e, NE".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_10() {
		String jpqlQuery = "SELECT e, NEW(java.lang.String)";
		int position = "SELECT e, NEW".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_11() throws Exception {
		String jpqlQuery = "SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c";
		int position = "SELECT new com.titan.domain.Name(c.".length();
		testHasTheseProposals(jpqlQuery, position, "firstName", "hasGoodCredit", "id", "lastName", "address", "home");
	}

	@Test
	public final void test_Constructor_12() {
		String jpqlQuery = "SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c";
		int position = "SELECT new com.titan.domain.Name(c.firstName, ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("c");
		CollectionTools.addAll(proposals, bnfAccessor.constructorItemFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Constructor_13() {
		String jpqlQuery = "SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c";
		int position = "SELECT new com.titan.domain.Name(c.firstName, c".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("c");
		CollectionTools.addAll(proposals, filter(bnfAccessor.constructorItemFunctions(), "c"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Constructor_15() throws Exception {
		String jpqlQuery = "SELECT NE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_16() throws Exception {
		String jpqlQuery = "SELECT e, NE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_17() throws Exception {
		String jpqlQuery = "SELECT e, NEW";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_18() {
		String jpqlQuery = "SELECT NE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_19() {
		String jpqlQuery = "SELECT NEW";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_20() {
		String jpqlQuery = "SELECT e, NEW";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_Constructor_21() {
		String jpqlQuery = "SELECT e, NEW java.lang.String(e.name)";
		int position = "SELECT e, ".length();
		testHasTheseProposals(jpqlQuery, position, NEW);
	}

	@Test
	public final void test_CountFunction_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public final void test_CountFunction_02() {
		String jpqlQuery = "SELECT C";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public final void test_CountFunction_03() {
		String jpqlQuery = "SELECT CO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public final void test_CountFunction_04() {
		String jpqlQuery = "SELECT COU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public final void test_CountFunction_05() {
		String jpqlQuery = "SELECT COUN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public final void test_CountFunction_06() {
		String jpqlQuery = "SELECT COUNT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, COUNT);
	}

	@Test
	public final void test_CountFunction_07() {

		String jpqlQuery = "SELECT COUNT(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_08() {

		String jpqlQuery = "SELECT COUNT() From Employee e";
		int position = "SELECT COUNT(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_09() {

		String jpqlQuery = "SELECT COUNT(DISTINCT ) From Employee e";
		int position = "SELECT COUNT(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_10() {
		String jpqlQuery = "SELECT COUNT(D ) From Employee e";
		int position = "SELECT COUNT(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_CountFunction_11() {
		String jpqlQuery = "SELECT COUNT(DI ) From Employee e";
		int position = "SELECT COUNT(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_CountFunction_12() {
		String jpqlQuery = "SELECT COUNT(DIS ) From Employee e";
		int position = "SELECT COUNT(DIS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_CountFunction_13() {
		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_CountFunction_14() {
		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_CountFunction_15() {
		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_CountFunction_16() {

		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_17() {

		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, filter(bnfAccessor.countFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_18() {

		String jpqlQuery = "SELECT COUNT(DISTINCT e) From Employee emp";
		int position = "SELECT COUNT(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.countFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_19() {
		String jpqlQuery = "SELECT COUNT() From Employee emp";
		int position = "SELECT COUNT(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_20() {

		String jpqlQuery = "SELECT COUNT(e) From Employee emp";
		int position = "SELECT COUNT(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.countFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_21() {

		String jpqlQuery = "SELECT COUNT(em) From Employee emp";
		int position = "SELECT COUNT(em".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.countFunctions(), "em"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_22() {

		String jpqlQuery = "SELECT COUNT(emp) From Employee emp";
		int position = "SELECT COUNT(emp".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.countFunctions(), "emp"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_23() {

		String jpqlQuery = "SELECT COUNT(emp) From Employee emp";
		int position = "SELECT COUNT(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.countFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_24() {

		String jpqlQuery = "SELECT COUNT(emp) From Employee emp";
		int position = "SELECT COUNT(em".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.countFunctions(), "em"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_25() {
		String jpqlQuery = "SELECT COUNT( From Employee emp";
		int position = "SELECT COUNT(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.countFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_CountFunction_26() {

		String jpqlQuery = "SELECT COUNT(e From Employee emp";
		int position = "SELECT COUNT(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.countFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_DateTime_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hiredTime < ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE C";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CUR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURREN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_D";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_DATE);
	}

	@Test
	public final void test_DateTime_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_DA";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_DATE);
	}

	@Test
	public final void test_DateTime_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_DAT";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_DATE);
	}

	@Test
	public final void test_DateTime_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_DATE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_T";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TI";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIME";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIMES";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIMEST";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIMESTA";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIMESTAM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_DateTime_23() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE CURRENT_TIMESTAMP";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP);
	}

	@Test
	public final void test_Delete_01() {
		String jpqlQuery = "D";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_02() {
		String jpqlQuery = "DE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_03() {
		String jpqlQuery = "DEL";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_04() {
		String jpqlQuery = "DELE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_05() {
		String jpqlQuery = "DELET";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_06() {
		String jpqlQuery = "DELETE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_07() {
		String jpqlQuery = "DELETE ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_08() {
		String jpqlQuery = "DELETE F";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_09() {
		String jpqlQuery = "DELETE FR";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_10() {
		String jpqlQuery = "DELETE FRO";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_11() {
		String jpqlQuery = "DELETE FROM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_12() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_13() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_14() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DEL".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_15() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_16() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELET".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_17() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_18() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_19() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE F".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_20() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE FR".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_21() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE FRO".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_22() {
		String jpqlQuery = "DELETE FROM Employee";
		int position = "DELETE FROM".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_23() {
		String jpqlQuery = "DELETE FROM Employee e WHERE";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_24() {
		String jpqlQuery = "DELETE FROM WHERE";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, DELETE_FROM);
	}

	@Test
	public final void test_Delete_25() throws Exception {
		String jpqlQuery = "DELETE FROM ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public final void test_Delete_26() throws Exception {
		String jpqlQuery = "DELETE FROM P";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "P"));
	}

	@Test
	public final void test_Delete_27() {
		String jpqlQuery = "DELETE FROM Employee WHERE n";
		int position = "DELETE FROM Employee ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_Delete_28() {
		String jpqlQuery = "DELETE FROM Employee A WHERE";
		int position = jpqlQuery.length() - " WHERE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_Delete_29() {
		String jpqlQuery = "DELETE FROM Employee A ";
		int position = jpqlQuery.length() - 1;
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_Delete_30() {
		String jpqlQuery = "DELETE FROM Employee AS e ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Delete_31() {
		String jpqlQuery = "DELETE FROM Employee AS e W";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Delete_32() {
		String jpqlQuery = "DELETE FROM Employee AS e WH";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Delete_33() {
		String jpqlQuery = "DELETE FROM Employee AS e WHE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Delete_34() {
		String jpqlQuery = "DELETE FROM Employee AS e WHER";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Delete_35() {
		String jpqlQuery = "DELETE FROM Employee AS e WHERE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_EmptyCollectionComparison_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers I";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS N";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS NO";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_NULL, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS NOT";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY, IS_NOT_NULL);
	}

	@Test
	public final void test_EmptyCollectionComparison_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS NOT ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY, IS_NOT_NULL);
	}

	@Test
	public final void test_EmptyCollectionComparison_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS NOT E";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS NOT EM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS NOT EMP";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS NOT EMPT";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS NOT EMPTY";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS E";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS EM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS EMP";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS EMPT";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.phoneNumbers IS EMPTY";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name I";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_23() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS N";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_24() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NO";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_25() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_26() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_27() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT E";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_28() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT EM";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_29() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT EMP";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_30() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT EMPT";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_31() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT EMPTY";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_32() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS E";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_33() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS EM";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_34() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS EMP";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_35() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS EMPT";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_EMPTY);
	}

	@Test
	public final void test_EmptyCollectionComparison_36() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS EMPTY";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_EMPTY, IS_NOT_EMPTY);
	}

	@Test
	public final void test_From_01() throws Exception {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_02() throws Exception {
		String jpqlQuery = "SELECT F";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_03() throws Exception {
		String jpqlQuery = "SELECT AVG(e.roomNumber)";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_04() throws Exception {
		String jpqlQuery = "SELECT AVG(e.roomNumber) ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_05() throws Exception {
		String jpqlQuery = "SELECT AVG(e.roomNumber) F";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_06() throws Exception {
		String jpqlQuery = "SELECT f ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_07() throws Exception {
		String jpqlQuery = "SELECT a, ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_08() throws Exception {
		String jpqlQuery = "SELECT AVG( ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_09() throws Exception {
		String jpqlQuery = "SELECT AVG(a ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_10() throws Exception {
		String jpqlQuery = "SELECT AVG(e.roomNumber ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_11() throws Exception {
		String jpqlQuery = "SELECT F F";
		int position = "SELECT F".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_12() throws Exception {

		String jpqlQuery = "SELECT e FROM Address a," +
		               "              Employee emp JOIN emp.customers emp_c, " +
		               "              Address ea " +
		               "WHERE ALL(SELECT a e";

		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_From_13() throws Exception {
		String jpqlQuery = "SELECT e F";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_14() throws Exception {
		String jpqlQuery = "SELECT e F, E";
		int position = "SELECT e F".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_From_15() throws Exception {

		String jpqlQuery = "SELECT e FROM Address a," +
		               "              Employee emp JOIN emp.customers emp_c, " +
		               "              Address ea " +
		               "WHERE ALL(SELECT a w";

		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_FromAs_01() {
		String jpqlQuery = "SELECT e FROM Employee ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_FromAs_02() {
		String jpqlQuery = "SELECT e FROM Employee A";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_FromAs_03() {
		String jpqlQuery = "SELECT e FROM Employee AS";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_FromAs_04() {
		String jpqlQuery = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee ".length();
		testHasTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_FromAs_05() {
		String jpqlQuery = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_FromAs_06() {
		String jpqlQuery = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee AS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	protected final void test_Function(String fragment,
	                                   String ending,
	                                   boolean subqueryAllowed) throws Exception {

		test_Function(fragment, ending, subqueryAllowed, new TweakFunctionIdentifiers());
	}

	protected final void test_Function(String fragment,
	                                   String ending,
	                                   boolean subqueryAllowed,
	                                   TweakFunctionIdentifiers tweaker) throws Exception {

		// Test all functions (example: MAX)
		for (String identifier : functionProposals()) {

			// Test by cutting the identifier from all the characters making the identifier up to cutting none
			for (int index = 0; index <= identifier.length(); index++) {

				String identifierFragment = identifier.substring(0, index);
				String jpqlQuery = fragment + identifierFragment + ending;

				// Now move the cursor position from the beginning of the fragment identifier to the end of it
				for (int positionIndex = 0; positionIndex <= index; positionIndex++) {

					int position = fragment.length() + positionIndex;
					String filter = identifier.substring(0, positionIndex);

					List<String> proposals = new ArrayList<String>();
					CollectionTools.addAll(proposals, filter(functionProposals(), filter));
					CollectionTools.addAll(proposals, filter(new String[] { "e" }, filter));

					if (subqueryAllowed) {
						CollectionTools.addAll(proposals, filter(new String[] { SELECT }, filter));
					}

					tweaker.tweak(proposals, identifier, identifierFragment, positionIndex);

					// Now to the test
					try {
						testHasOnlyTheseProposals(jpqlQuery, position, proposals);
					}
					catch (AssertionError e) {
						StringBuilder sb = new StringBuilder();
						sb.append(e.getMessage());
						sb.append(" \"");
						sb.append(jpqlQuery.substring(0, position));
						sb.append("|");
						sb.append(jpqlQuery.substring(position, jpqlQuery.length()));
						sb.append("\" [");
						sb.append(position);
						sb.append("]");
						Assert.fail(sb.toString());
					}
					finally {
						tearDown();
					}
				}
			}
		}
	}

	@Test
	public final void test_Function_01() throws Exception {
		String fragment = "select e from Employee e where ";
		test_Function(fragment, ExpressionTools.EMPTY_STRING, false);
	}

	@Test
	public final void test_Function_02() throws Exception {
		String fragment = "select e from Employee e where ";
		test_Function(fragment, "(", false);
	}

	@Test
	public final void test_Function_03() throws Exception {
		String fragment = "select e from Employee e where (";
		test_Function(fragment, ExpressionTools.EMPTY_STRING, true);
	}

	@Test
	public final void test_Function_04() throws Exception {
		String fragment = "select e from Employee e where (";
		test_Function(fragment, "(", true);
	}

	@Test
	public final void test_Function_05() throws Exception {
		String fragment = "select e from Employee e where (";
		test_Function(fragment, ")", true);
	}

	@Test
	public final void test_Function_06() throws Exception {
		String fragment = "select e from Employee e where ";
		test_Function(fragment, " + ABS(e.roomNumber)", false);
	}

	@Test
	public final void test_GroupBy_01() {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_02() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name";
		int position = "SELECT e FROM Employee e ".length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_03() {
		String jpqlQuery = "SELECT e FROM Employee e G";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_04() {
		String jpqlQuery = "SELECT e FROM Employee e GR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_05() {
		String jpqlQuery = "SELECT e FROM Employee e GRO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_06() {
		String jpqlQuery = "SELECT e FROM Employee e GROU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_07() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_08() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_09() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP B";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_10() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') G";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_14() {
		String jpqlQuery = "SELECT e FROM Employee e, GROUP";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_GroupBy_15() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP, Address a";
		int position = "SELECT e FROM Employee e GROUP".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_Having_01() {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_02() {
		String jpqlQuery = "SELECT e FROM Employee e HAVING COUNT(e) >= 5";
		int position = "SELECT e FROM Employee e ".length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_03() {
		String jpqlQuery = "SELECT e FROM Employee e H";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_04() {
		String jpqlQuery = "SELECT e FROM Employee e HA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_05() {
		String jpqlQuery = "SELECT e FROM Employee e HAV";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_06() {
		String jpqlQuery = "SELECT e FROM Employee e HAVI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_07() {
		String jpqlQuery = "SELECT e FROM Employee e HAVIN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_08() {
		String jpqlQuery = "SELECT e FROM Employee e HAVING";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') H";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_11() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_12() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name H";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name H";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_Having_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, HAVING);
	}

	@Test
	public final void test_IncompleteCollectionExpressionVisitor_01() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP B";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_IncompleteCollectionExpressionVisitor_02() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER B";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_IncompleteCollectionExpressionVisitor_03() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER B HAVING e.name = 'JPQL'";
		int position = "SELECT e FROM Employee e ORDER B".length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_IncompleteCollectionExpressionVisitor_04() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP B HAVING e.name = 'JPQL'";
		int position = "SELECT e FROM Employee e GROUP B".length();
		testHasOnlyTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_IncompleteCollectionExpressionVisitor_05() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP B HAVING e.name = 'JPQL'";
		int position = "SELECT e FROM Employee e GROUP B".length();
		testHasOnlyTheseProposals(jpqlQuery, position, GROUP_BY);
	}

	@Test
	public final void test_IncompleteCollectionExpressionVisitor_06() {
		String jpqlQuery = "SELECT e FROM Employee e HAVING e.name = 'JPQL' ORDER B";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_IncompleteCollectionExpressionVisitor_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ALL(SELECT a FROM Address a HAVING e.name = 'JPQL' ORDER B";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_IncompleteCollectionExpressionVisitor_08() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP B ORT b a";
		int position = "SELECT e FROM Employee e GROUP B".length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_Invalid_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS e.name";
		int position = "SELECT e FROM Employee e WHERE e.name IS e.".length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_Invalid_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS CONCAT(";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_Is_01() throws Exception {
		String jpqlQuery = "select e from Employee e where e.managerEmployee is not ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public final void test_Is_02() throws Exception {
		String jpqlQuery = "select e from Employee e where (select a from e.employees) is ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public final void test_Is_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS e.name";
		int position = "SELECT e FROM Employee e WHERE e.name IS ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public final void test_Is_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS WHERE";
		int position = "SELECT e FROM Employee e WHERE e.name IS ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public final void test_Join_01() {

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
	public final void test_Join_02() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub L".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_03() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_04() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEF".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_05() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_06() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_07() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT J".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_08() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT JO".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_09() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT JOI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_10() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_11() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_12() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub J".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_13() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub JO".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_14() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub JOI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_15() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_16() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_17() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub L".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_18() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_19() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEF".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_20() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_21() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_22() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT O".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_23() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OU".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_24() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUT".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_25() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_26() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_27() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_28() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER J".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_29() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER JO".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_30() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER JOI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_31() {
		String jpqlQuery = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER JOIN".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_32() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag";
		int position = "SELECT pub FROM Publisher pub JOIN pub.magazines ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_Join_33() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag";
		int position = "SELECT pub FROM Publisher pub JOIN pub.magazines A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_Join_34() {
		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag";
		int position = "SELECT pub FROM Publisher pub JOIN pub.magazines AS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_Join_35() {
		String jpqlQuery = "SELECT e FROM Employee e INNER JOIN e.magazines mags";
		int position = "SELECT e FROM Employee e ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_36() {

		String jpqlQuery = "SELECT e FROM Employee e INNER JOIN e.magazines mags ";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, fromClauseInternalClauses(FROM));
		CollectionTools.addAll(proposals, clauses(FROM, null, false));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Join_37() {

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
	public final void test_Join_38() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList e LEFT ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(joinIdentifiers(), "LEFT "));
	}

	@Test
	public final void test_Join_39() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList e LEFT OUTER JOIN FETCH  ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "o", "e");
	}

	@Test
	public final void test_Join_40() {
		String jpqlQuery = "select o.city from Address o ,";
		int position = "select o.city from Address o ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, joinIdentifiers());
	}

	@Test
	public final void test_Join_41() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_Join_42() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList A";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_Join_43() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList J";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_Join_44() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList J LEFT JOIN ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "o", "J");
	}

	@Test
	public final void test_Join_45() {
		String jpqlQuery = "SELECT o from Countries o JOIN o.locationsList J LEFT JOIN L";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, "o");
	}

	@Test
	public final void test_Keyword_01() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = ".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public final void test_Keyword_02() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = T".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public final void test_Keyword_03() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = TR".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public final void test_Keyword_04() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = TRU".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public final void test_Keyword_05() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, TRUE, FALSE, NULL);
	}

	@Test
	public final void test_Keyword_06() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = ".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public final void test_Keyword_07() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = F".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public final void test_Keyword_08() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = FA".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public final void test_Keyword_09() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = FAL".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public final void test_Keyword_10() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = FALS".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public final void test_Keyword_11() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, TRUE, FALSE, NULL);
	}

	@Test
	public final void test_Keyword_12() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = ".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public final void test_Keyword_13() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = N".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public final void test_Keyword_14() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = NU".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public final void test_Keyword_15() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = NUL".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public final void test_Keyword_16() {
		String jpqlQuery = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULL, TRUE, FALSE, NULLIF);
	}

	@Test
	public final void test_Keyword_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = ".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public final void test_Keyword_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = T".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public final void test_Keyword_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = TR".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public final void test_Keyword_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = TRU".length();
		testHasTheseProposals(jpqlQuery, position, TRUE);
	}

	@Test
	public final void test_Keyword_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, TRUE, FALSE, NULL);
	}

	@Test
	public final void test_Keyword_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = ".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public final void test_Keyword_23() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = F".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public final void test_Keyword_24() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = FA".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public final void test_Keyword_25() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = FAL".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public final void test_Keyword_26() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = FALS".length();
		testHasTheseProposals(jpqlQuery, position, FALSE);
	}

	@Test
	public final void test_Keyword_27() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, TRUE, FALSE, NULL);
	}

	@Test
	public final void test_Keyword_28() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = ".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public final void test_Keyword_29() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = N".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public final void test_Keyword_30() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = NU".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public final void test_Keyword_31() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = NUL".length();
		testHasTheseProposals(jpqlQuery, position, NULL);
	}

	@Test
	public final void test_Keyword_32() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, NULL, TRUE, FALSE, NULLIF);
	}

	@Test
	public final void test_Keyword_33() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NULL, TRUE, FALSE);
	}

	@Test
	public final void test_Keyword_34() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired =";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NULL, TRUE, FALSE);
	}

	@Test
	public final void test_Keyword_35() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NULL, TRUE, FALSE);
	}

	@Test
	public final void test_Keyword_36() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.hired = NULL ";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(AND);
		proposals.add(OR);
		CollectionTools.addAll(proposals, clauses(WHERE, null, false));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Like_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, LIKE, NOT_LIKE);
	}

	@Test
	public final void test_Like_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LIKE, NOT_LIKE);
	}

	@Test
	public final void test_Like_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name L";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, LIKE);
	}

	@Test
	public final void test_Like_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name LI";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, LIKE);
	}

	@Test
	public final void test_Like_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name LIKE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, LIKE, NOT_LIKE);
	}

	@Test
	public final void test_Like_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name N";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public final void test_Like_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public final void test_Like_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public final void test_Like_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name ".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public final void test_Like_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name N".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public final void test_Like_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NO".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public final void test_Like_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT ".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public final void test_Like_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT L".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public final void test_Like_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT LI".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public final void test_Like_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT LIK".length();
		testHasTheseProposals(jpqlQuery, position, NOT_LIKE);
	}

	@Test
	public final void test_Like_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, LIKE, NOT_LIKE);
	}

	@Test
	public final void test_Locate_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE L";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOC";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOCA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOCAT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOCATE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE LOCATE(";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (L";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LOC";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LOCA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LOCAT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LOCATE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (LOCATE)";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Locate_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ()";
		int position = jpqlQuery.length() - 1;
		testHasTheseProposals(jpqlQuery, position, LOCATE);
	}

	@Test
	public final void test_Logical_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber BETWEEN 1 AND 3 ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, AND, OR);
	}

	@Test
	public final void test_MaxFunction_05() {

		String jpqlQuery = "SELECT MAX(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MaxFunction_06() {

		String jpqlQuery = "SELECT MAX() From Employee e";
		int position = "SELECT MAX(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MaxFunction_07() {

		String jpqlQuery = "SELECT MAX(DISTINCT ) From Employee e";
		int position = "SELECT MAX(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MaxFunction_08() {
		String jpqlQuery = "SELECT MAX(D ) From Employee e";
		int position = "SELECT MAX(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MaxFunction_09() {
		String jpqlQuery = "SELECT MAX(DI ) From Employee e";
		int position = "SELECT MAX(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MaxFunction_10() {
		String jpqlQuery = "SELECT MAX(DIS ) From Employee e";
		int position = "SELECT MAX(DIS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MaxFunction_11() {
		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MaxFunction_12() {
		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MaxFunction_13() {
		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MaxFunction_14() {

		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MaxFunction_15() {

		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MaxFunction_16() {

		String jpqlQuery = "SELECT MAX(DISTINCT e) From Employee emp";
		int position = "SELECT MAX(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MaxFunction_17() {

		String jpqlQuery = "SELECT MAX() From Employee emp";
		int position = "SELECT MAX(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MaxFunction_18() {

		String jpqlQuery = "SELECT MAX(e) From Employee emp";
		int position = "SELECT MAX(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MaxFunction_19() {
		String jpqlQuery = "SELECT MAX(em) From Employee emp";
		int position = "SELECT MAX(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_MaxFunction_20() {
		String jpqlQuery = "SELECT MAX(emp) From Employee emp";
		int position = "SELECT MAX(emp".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_MaxFunction_21() {

		String jpqlQuery = "SELECT MAX(emp) From Employee emp";
		int position = "SELECT MAX(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MaxFunction_22() {
		String jpqlQuery = "SELECT MAX(emp) From Employee emp";
		int position = "SELECT MAX(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_MaxFunction_23() {

		String jpqlQuery = "SELECT MAX( From Employee emp";
		int position = "SELECT MAX(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MaxFunction_24() {

		String jpqlQuery = "SELECT MAX(e From Employee emp";
		int position = "SELECT MAX(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MIN);
	}

	@Test
	public final void test_MinFunction_02() {
		String jpqlQuery = "SELECT M";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MIN);
	}

	@Test
	public final void test_MinFunction_03() {
		String jpqlQuery = "SELECT MI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MIN);
	}

	@Test
	public final void test_MinFunction_04() {
		String jpqlQuery = "SELECT MIN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, MIN);
	}

	@Test
	public final void test_MinFunction_05() {

		String jpqlQuery = "SELECT MIN(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_06() {

		String jpqlQuery = "SELECT MIN() From Employee e";
		int position = "SELECT MIN(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_07() {

		String jpqlQuery = "SELECT MIN(DISTINCT ) From Employee e";
		int position = "SELECT MIN(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_08() {
		String jpqlQuery = "SELECT MIN(D ) From Employee e";
		int position = "SELECT MIN(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MinFunction_09() {
		String jpqlQuery = "SELECT MIN(DI ) From Employee e";
		int position = "SELECT MIN(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MinFunction_10() {
		String jpqlQuery = "SELECT MIN(DIS ) From Employee e";
		int position = "SELECT MIN(DIS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MinFunction_11() {
		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MinFunction_12() {
		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MinFunction_13() {
		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_MinFunction_14() {

		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_15() {

		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_16() {

		String jpqlQuery = "SELECT MIN(DISTINCT e) From Employee emp";
		int position = "SELECT MIN(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_17() {

		String jpqlQuery = "SELECT MIN() From Employee emp";
		int position = "SELECT MIN(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_18() {

		String jpqlQuery = "SELECT MIN(e) From Employee emp";
		int position = "SELECT MIN(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_19() {
		String jpqlQuery = "SELECT MIN(em) From Employee emp";
		int position = "SELECT MIN(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_MinFunction_20() {
		String jpqlQuery = "SELECT MIN(emp) From Employee emp";
		int position = "SELECT MIN(emp".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_MinFunction_21() {

		String jpqlQuery = "SELECT MIN(emp) From Employee emp";
		int position = "SELECT MIN(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_22() {
		String jpqlQuery = "SELECT MIN(emp) From Employee emp";
		int position = "SELECT MIN(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_MinFunction_23() {

		String jpqlQuery = "SELECT MIN( From Employee emp";
		int position = "SELECT MIN(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_MinFunction_24() {

		String jpqlQuery = "SELECT MIN(e From Employee emp";
		int position = "SELECT MIN(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_NullComparison_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name I";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS N";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NO";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT N";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT NU";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT NUL";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NOT_NULL);
	}

	@Test
	public final void test_NullComparison_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT NULL";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, IS_NULL, IS_NOT_NULL);
	}

	@Test
	public final void test_Object_01() {
		String jpqlQuery = "SELECT O FROM Employee e";
		int position = "SELECT O".length() - 1;
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_02() {
		String jpqlQuery = "SELECT OB FROM Employee e";
		int position = "SELECT O".length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_03() {
		String jpqlQuery = "SELECT OBJ FROM Employee e";
		int position = "SELECT O".length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_04() {
		String jpqlQuery = "SELECT OBJE FROM Employee e";
		int position = "SELECT O".length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_05() {
		String jpqlQuery = "SELECT OBJEC FROM Employee e";
		int position = "SELECT O".length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_12() {
		String jpqlQuery = "SELECT OBJECT";
		testHasTheseProposals(jpqlQuery, jpqlQuery.length(), OBJECT);
	}

	@Test
	public final void test_Object_13() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_14() {
		String jpqlQuery = "SELECT O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_15() {
		String jpqlQuery = "SELECT OB";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_16() {
		String jpqlQuery = "SELECT OBJ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_17() {
		String jpqlQuery = "SELECT OBJE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_18() {
		String jpqlQuery = "SELECT OBJEC";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_19() {
		String jpqlQuery = "SELECT OBJECT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, OBJECT);
	}

	@Test
	public final void test_Object_20() {
		String jpqlQuery = "SELECT DISTINCT OBJECT(a) FROM Address a";
		int position = "SELECT DISTINCT OBJECT(a".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "a");
	}

	@Test
	public final void test_OptionalClauses_01() {

		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, fromClauseInternalClauses(FROM));
		CollectionTools.addAll(proposals, clauses(FROM, null, false));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_OptionalClauses_02() {

		String jpqlQuery = "SELECT e FROM Employee e HAVING e.name = 'Oracle'";
		int position = "SELECT e FROM Employee e ".length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, fromClauseInternalClauses(FROM));
		CollectionTools.addAll(proposals, clauses(FROM, ORDER_BY, false));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_OptionalClauses_03() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name";
		int position = "SELECT e FROM Employee e ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(ORDER_BY);
		CollectionTools.addAll(proposals, fromClauseInternalClauses(FROM));
		CollectionTools.addAll(proposals, clauses(FROM, ORDER_BY, false));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_OptionalClauses_04() {

		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name";
		int position = "SELECT e FROM Employee e ".length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, fromClauseInternalClauses(FROM));
		CollectionTools.addAll(proposals, clauses(FROM, HAVING, false));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_OptionalClauses_05() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name = 'Oracle' ";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(AND);
		proposals.add(OR);
		CollectionTools.addAll(proposals, clauses(WHERE, null, false));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_OptionalClauses_06() {

		String jpqlQuery = "SELECT e FROM Employee e A";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, filter(fromClauseInternalClauses(FROM), "A"));
		CollectionTools.addAll(proposals, filter(clauses(FROM, null, false), "A"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_OptionalClauses_07() {
		String jpqlQuery = "SELECT e FROM Employee e";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_OrderBy_01() {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_02() {
		String jpqlQuery = "SELECT e FROM Employee e O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_03() {
		String jpqlQuery = "SELECT e FROM Employee e OR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_04() {
		String jpqlQuery = "SELECT e FROM Employee e ORD";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_05() {
		String jpqlQuery = "SELECT e FROM Employee e ORDE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_06() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_07() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_08() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER B";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_09() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_12() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_13() {
		String jpqlQuery = "SELECT e FROM Employee e GROUP BY e.name O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_14() {
		String jpqlQuery = "SELECT e FROM Employee e HAVING COUNT(e) >= 5 ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_15() {
		String jpqlQuery = "SELECT e FROM Employee e HAVING COUNT(e) >= 5 O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name HAVING COUNT(e) >= 5 ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderBy_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name HAVING COUNT(e) >= 5 O";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_OrderByItem_01() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, ASC);
	}

	@Test
	public final void test_OrderByItem_02() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ASC);
	}

	@Test
	public final void test_OrderByItem_03() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name A";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ASC);
	}

	@Test
	public final void test_OrderByItem_04() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name AS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, ASC);
	}

	@Test
	public final void test_OrderByItem_05() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name ASC";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, ASC, DESC);
	}

	@Test
	public final void test_OrderByItem_06() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name D";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, DESC);
	}

	@Test
	public final void test_OrderByItem_07() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name DE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, DESC);
	}

	@Test
	public final void test_OrderByItem_08() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name DE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, DESC);
	}

	@Test
	public final void test_OrderByItem_09() {
		String jpqlQuery = "SELECT e FROM Employee e ORDER BY e.name DESC";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, ASC, DESC);
	}

	@Test
	public final void test_Query_01() throws Exception {

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
	public final void test_Query_02() throws Exception {

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
	public final void test_Query_03() throws Exception {

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
	public final void test_Query_04() throws Exception {

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
	public final void test_Restriction_01() throws Exception {

		String jpqlQuery = "SELECT AVG(e.name) FROM Employee e";
		int position = "SELECT AVG(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Restriction_02() throws Exception {
		String jpqlQuery = "SELECT AVG(e.name) FROM Employee e";
		int position = "SELECT AVG(e".length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		List<String> selectProposals = new ArrayList<String>();
		CollectionTools.addAll(selectProposals, filter(bnfAccessor.selectItemIdentifiers(), "e"));
		selectProposals.removeAll(proposals);

		testDoesNotHaveTheseProposals(jpqlQuery, position, selectProposals);
	}

	@Test
	public final void test_Restriction_03() throws Exception {
		String jpqlQuery = "SELECT AVG(e.name) FROM Employee e";
		int position = "SELECT AVG(e.".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, bnfAccessor.selectItemIdentifiers());
	}

	@Test
	public final void test_Restriction_04() {
		String jpqlQuery = "SELECT o FROM Countries AS o";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, ORDER_BY);
	}

	@Test
	public final void test_Select_01() throws Exception {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.selectItemFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Select_02() throws Exception {

		String jpqlQuery = "SELECT e";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		CollectionTools.addAll(proposals, filter(bnfAccessor.selectItemFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Select_03() throws Exception {
		String jpqlQuery = "SELECT  FROM Employee e";
		int position = "SELECT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.selectItemFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Select_04() throws Exception {
		String jpqlQuery = "SELECT AV FROM Employee e";
		int position = "SELECT AV".length();
		testHasOnlyTheseProposals(jpqlQuery, position, AVG);
	}

	@Test
	public final void test_Select_05() throws Exception {
		String jpqlQuery = "SELECT e,";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, bnfAccessor.selectItemFunctions());
	}

	@Test
	public final void test_Select_06() throws Exception {
		String jpqlQuery = "SELECT e, ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, bnfAccessor.selectItemFunctions());
	}

	@Test
	public final void test_Select_07() throws Exception {

		String jpqlQuery = "SELECT AVG(e.age)  FROM Employee e";
		int position = "SELECT AVG(e.age) ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(AS);
		proposals.add(FROM);
		CollectionTools.addAll(proposals, bnfAccessor.selectItemAggregates());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Select_08() throws Exception {

		String jpqlQuery = SELECT;

		for (int position = 1, count = jpqlQuery.length(); position < count; position++) {
			testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
		}
	}

	@Test
	public final void test_SelectItem_01() {
		String jpqlQuery = "SELECT o,  FROM Address o";
		int position = "SELECT o, ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("o");
		CollectionTools.addAll(proposals, bnfAccessor.selectItemFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SelectItem_02() {
		String jpqlQuery = "SELECT O, CASE WHEN c.firstName = 'Pascal' THEN 'P' ELSE 'JPQL' END FROM Customer c";
		int position = "SELECT O".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(bnfAccessor.selectItemIdentifiers(), "O"));
	}

	@Test
	public final void test_StateFieldPath_01() throws Exception {
		String jpqlQuery = "SELECT c. FROM CodeAssist c";
		int position = "SELECT c.".length();
		testHasOnlyTheseProposals(jpqlQuery, position, singledValuedObjectFieldNames(CodeAssist.class));
	}

	@Test
	public final void test_StateFieldPath_02() throws Exception {
		String jpqlQuery = "SELECT c.name FROM CodeAssist c";
		int position = "SELECT c.n".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(singledValuedObjectFieldNames(CodeAssist.class), "n"));
	}

	@Test
	public final void test_StateFieldPath_03() throws Exception {
		String jpqlQuery = "SELECT c.name FROM CodeAssist c";
		int position = "SELECT c.name".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(singledValuedObjectFieldNames(CodeAssist.class), "name"));
	}

	@Test
	public final void test_StateFieldPath_04() throws Exception {
		String jpqlQuery = "SELECT c.manager.name FROM CodeAssist c";
		int position = "SELECT c.m".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(singledValuedObjectFieldNames(CodeAssist.class), "m"));
	}

	@Test
	public final void test_StateFieldPath_05() throws Exception {
		String jpqlQuery = "SELECT c.manager.name FROM CodeAssist c";
		int position = "SELECT c.manager".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(singledValuedObjectFieldNames(CodeAssist.class), "manager"));
	}

	@Test
	public final void test_StateFieldPath_06() throws Exception {
		String jpqlQuery = "SELECT c.manager.name FROM CodeAssist c";
		int position = "SELECT c.manager.".length();
		testHasOnlyTheseProposals(jpqlQuery, position, singledValuedObjectFieldNames(Employee.class));
	}

	@Test
	public final void test_StateFieldPath_07() throws Exception {
		String jpqlQuery = "SELECT c.manager.name FROM CodeAssist c";
		int position = "SELECT c.manager.name".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(singledValuedObjectFieldNames(Employee.class), "name"));
	}

	@Test
	public final void test_StateFieldPath_08() throws Exception {
		String jpqlQuery = "SELECT c.employees. FROM CodeAssist c";
		int position = "SELECT c.employees.".length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_StateFieldPath_09() throws Exception {
		String jpqlQuery = "SELECT e. FROM CodeAssist c JOIN c.employees e";
		int position = "SELECT e.".length();
		testHasOnlyTheseProposals(jpqlQuery, position, singledValuedObjectFieldNames(Employee.class));
	}

	@Test
	public final void test_StateFieldPath_10() throws Exception {
		String jpqlQuery = "SELECT e. FROM CodeAssist c, IN (c.employees) e";
		int position = "SELECT e.".length();
		testHasOnlyTheseProposals(jpqlQuery, position, singledValuedObjectFieldNames(Employee.class));
	}

	@Test
	public final void test_StateFieldPath_11() throws Exception {
		String jpqlQuery = "SELECT a.alias FROM CodeAssist c, IN (c.customerMap) cust, IN(KEY(cust).aliases) a";
		int position = "SELECT a.".length();
		testHasOnlyTheseProposals(jpqlQuery, position, singledValuedObjectFieldNames(Alias.class));
	}

	@Test
	public final void test_StateFieldPath_12() throws Exception {
		String jpqlQuery = "SELECT a.alias FROM CodeAssist c, IN (c.customerMap) cust, IN(KEY(cust).aliases) a";
		int position = "SELECT a.alias".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "alias");
	}

	@Test
	public final void test_StateFieldPath_13() throws Exception {
		String jpqlQuery = "SELECT a.alias FROM CodeAssist c, IN (c.customerMap) cust, IN(KEY(cust).aliases) a";
		int position = "SELECT a.alias FROM CodeAssist c, IN (c.".length();
		testHasOnlyTheseProposals(jpqlQuery, position, relationshipAndCollectionFieldNames(CodeAssist.class));
	}

	@Test
	public final void test_StateFieldPath_14() throws Exception {
		String jpqlQuery = "SELECT a.alias FROM CodeAssist c, IN (c.customerMap) cust, IN(KEY(cust).aliases) a";
		int position = "SELECT a.alias FROM CodeAssist c, IN (c.customerMap) cust, IN(KEY(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "cust");
	}

	@Test
	public final void test_StateFieldPath_15() throws Exception {
		String jpqlQuery = "SELECT a.alias FROM CodeAssist c, IN (c.customerMap) cust, IN(KEY(cust).aliases) a";
		int position = "SELECT a.alias FROM CodeAssist c, IN (c.customerMap) cust, IN(KEY(cust).".length();
		testHasOnlyTheseProposals(jpqlQuery, position, relationshipAndCollectionFieldNames(Customer.class));
	}

	@Test
	public final void test_StateFieldPath_16() throws Exception {
		String jpqlQuery = "SELECT c FROM CodeAssist c WHERE ALL(SELECT e FROM c.";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, relationshipAndCollectionFieldNames(CodeAssist.class));
	}

	@Test
	public final void test_StateFieldPath_17() throws Exception {
		String jpqlQuery = "SELECT c FROM CodeAssist c WHERE ALL(SELECT e FROM c.employees e";
		int position = "SELECT c FROM CodeAssist c WHERE ALL(SELECT e FROM c.employees".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(relationshipAndCollectionFieldNames(CodeAssist.class), "employees"));
	}

	@Test
	public final void test_StateFieldPath_18() throws Exception {
		String jpqlQuery = "update Employee e set e.name = 'JPQL' where (select a from address a where a.";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, nonTransientFieldNames(Address.class));
	}

	@Test
	public final void test_StateFieldPath_19() throws Exception {
		String jpqlQuery = "select O from Product O where O.";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, singledValuedObjectFieldNames(Product.class));
	}

	@Test
	public final void test_Subquery_01() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.comparisonExpressionFunctions());
		CollectionTools.addAll(proposals, bnfAccessor.comparisonExpressionClauses());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Subquery_02() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (S".length();
		testHasTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public final void test_Subquery_03() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public final void test_Subquery_04() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SEL".length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public final void test_Subquery_05() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELE".length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public final void test_Subquery_06() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELEC".length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public final void test_Subquery_07() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT".length();
		testHasTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public final void test_Subquery_08() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT ";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT".length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public final void test_Subquery_09() throws Exception {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT A";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, ABS, AVG);
	}

	@Test
	public final void test_Subquery_10() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(FROM);
		CollectionTools.addAll(proposals, bnfAccessor.selectItemAggregates());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Subquery_11() throws Exception {

		String jpqlQuery = "select e from Employee e where (sel";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, SELECT);
	}

	@Test
	public final void test_Subquery_12() throws Exception {

		String jpqlQuery = "select s from Employee s where (s";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("s");
		proposals.add(SELECT);
		CollectionTools.addAll(proposals, filter(bnfAccessor.conditionalExpressionsFunctions(), "s"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Subquery_13() throws Exception {

		String jpqlQuery = "select e from Employee e where (";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(SELECT);
		CollectionTools.addAll(proposals, bnfAccessor.conditionalExpressionsFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Subquery_StateFieldPath_01() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.".length();

		testHasOnlyTheseProposals(
			jpqlQuery,
			position,
			singledValuedObjectFieldNames(Employee.class, acceptableType(AVG))
		);
	}

	@Test
	public final void test_Substring_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_Substring_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE S";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_Substring_03() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_Substring_04() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUB";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_Substring_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBS";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_Substring_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBST";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_Substring_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_Substring_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_Substring_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_Substring_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRIN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_Substring_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRING";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUBSTRING);
	}

	@Test
	public final void test_SumFunction_01() {
		String jpqlQuery = "SELECT ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUM);
	}

	@Test
	public final void test_SumFunction_02() {
		String jpqlQuery = "SELECT S";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUM);
	}

	@Test
	public final void test_SumFunction_03() {
		String jpqlQuery = "SELECT SU";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUM);
	}

	@Test
	public final void test_SumFunction_04() {
		String jpqlQuery = "SELECT SUM";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SUM);
	}

	@Test
	public final void test_SumFunction_05() {

		String jpqlQuery = "SELECT SUM(";
		int position = jpqlQuery.length();

		List<String> proposals = new ArrayList<String>();
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SumFunction_06() {

		String jpqlQuery = "SELECT SUM() From Employee e";
		int position = "SELECT SUM(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SumFunction_07() {

		String jpqlQuery = "SELECT SUM(DISTINCT ) From Employee e";
		int position = "SELECT SUM(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SumFunction_08() {
		String jpqlQuery = "SELECT SUM(D ) From Employee e";
		int position = "SELECT SUM(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_SumFunction_09() {
		String jpqlQuery = "SELECT SUM(DI ) From Employee e";
		int position = "SELECT SUM(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_SumFunction_10() {
		String jpqlQuery = "SELECT SUM(DIS ) From Employee e";
		int position = "SELECT SUM(DIS".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_SumFunction_11() {
		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_SumFunction_12() {
		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(D".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_SumFunction_13() {
		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(DI".length();
		testHasOnlyTheseProposals(jpqlQuery, position, DISTINCT);
	}

	@Test
	public final void test_SumFunction_14() {

		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(DISTINCT ".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SumFunction_15() {

		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("e");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SumFunction_16() {

		String jpqlQuery = "SELECT SUM(DISTINCT e) From Employee emp";
		int position = "SELECT SUM(DISTINCT e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SumFunction_17() {

		String jpqlQuery = "SELECT SUM() From Employee emp";
		int position = "SELECT SUM(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SumFunction_18() {

		String jpqlQuery = "SELECT SUM(e) From Employee emp";
		int position = "SELECT SUM(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SumFunction_19() {
		String jpqlQuery = "SELECT SUM(em) From Employee emp";
		int position = "SELECT SUM(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_SumFunction_20() {
		String jpqlQuery = "SELECT SUM(emp) From Employee emp";
		int position = "SELECT SUM(emp".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_SumFunction_21() {

		String jpqlQuery = "SELECT SUM(emp) From Employee emp";
		int position = "SELECT SUM(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SumFunction_22() {
		String jpqlQuery = "SELECT SUM(emp) From Employee emp";
		int position = "SELECT SUM(em".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "emp");
	}

	@Test
	public final void test_SumFunction_23() {

		String jpqlQuery = "SELECT SUM( From Employee emp";
		int position = "SELECT SUM(".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		proposals.add(DISTINCT);
		CollectionTools.addAll(proposals, bnfAccessor.internalAggregateFunctionFunctions());

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_SumFunction_24() {

		String jpqlQuery = "SELECT SUM(e From Employee emp";
		int position = "SELECT SUM(e".length();

		List<String> proposals = new ArrayList<String>();
		proposals.add("emp");
		CollectionTools.addAll(proposals, filter(bnfAccessor.internalAggregateFunctionFunctions(), "e"));

		testHasOnlyTheseProposals(jpqlQuery, position, proposals);
	}

	@Test
	public final void test_Trim_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public final void test_Trim_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(B";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, BOTH);
	}

	@Test
	public final void test_Trim_08() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(BO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, BOTH);
	}

	@Test
	public final void test_Trim_09() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(BOT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, BOTH);
	}

	@Test
	public final void test_Trim_10() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(BOTH";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, BOTH);
	}

	@Test
	public final void test_Trim_11() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(L";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public final void test_Trim_12() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public final void test_Trim_13() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LEA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public final void test_Trim_14() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LEAD";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public final void test_Trim_15() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LEADI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public final void test_Trim_16() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LEADIN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public final void test_Trim_17() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(LEADING";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, LEADING);
	}

	@Test
	public final void test_Trim_18() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(T";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public final void test_Trim_19() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public final void test_Trim_20() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public final void test_Trim_21() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public final void test_Trim_22() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAIL";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public final void test_Trim_23() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILI";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public final void test_Trim_24() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILIN";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public final void test_Trim_25() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, TRAILING);
	}

	@Test
	public final void test_Trim_26() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_Trim_27() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' F";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_Trim_28() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FR";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_Trim_29() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FRO";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_Trim_30() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FROM";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_Trim_31() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FROM ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, "e");
	}

	@Test
	public final void test_Trim_32() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FROM";
		int position = jpqlQuery.length() - 1;
		testHasOnlyTheseProposals(jpqlQuery, position, FROM);
	}

	@Test
	public final void test_Update_01() {
		String jpqlQuery = "U";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_02() {
		String jpqlQuery = "UP";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_03() {
		String jpqlQuery = "UPD";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_04() {
		String jpqlQuery = "UPDA";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_05() {
		String jpqlQuery = "UPDAT";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_06() {
		String jpqlQuery = UPDATE;
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_07() {
		String jpqlQuery = "UPDATE Employee";
		int position = "U".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_08() {
		String jpqlQuery = "UPDATE Employee";
		int position = "UP".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_09() {
		String jpqlQuery = "UPDATE Employee";
		int position = "UPD".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_10() {
		String jpqlQuery = "UPDATE Employee";
		int position = "UPDA".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_11() {
		String jpqlQuery = "UPDATE Employee";
		int position = "UPDAT".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_12() {
		String jpqlQuery = "UPDATE Employee";
		int position = "UPDATE".length();
		testHasTheseProposals(jpqlQuery, position, UPDATE);
	}

	@Test
	public final void test_Update_13() {
		String jpqlQuery = "UPDATE Employee e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public final void test_Update_14() {
		String jpqlQuery = "UPDATE Employee e S";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public final void test_Update_15() {
		String jpqlQuery = "UPDATE Employee e SE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public final void test_Update_16() {
		String jpqlQuery = "UPDATE Employee e SET";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public final void test_Update_17() {
		String jpqlQuery = "UPDATE SET";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_Update_18() throws Exception {
		String jpqlQuery = "UPDATE S";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "S"));
	}

	@Test
	public final void test_Update_19() {
		String jpqlQuery = "UPDATE Employee S";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public final void test_Update_20() {
		String jpqlQuery = "UPDATE Employee S SET";
		int position = "UPDATE Employee S".length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_Update_21() {
		String jpqlQuery = "UPDATE Z";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_Update_22() throws Exception {
		String jpqlQuery = "UPDATE A";
		int position = "UPDATE A".length();
		testHasOnlyTheseProposals(jpqlQuery, position, filter(entityNames(), "A"));
	}

	@Test
	public final void test_Update_23() {
		String jpqlQuery = "UPDATE Employee e SET ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, "e");
	}

	@Test
	public final void test_Update_24() {
		String jpqlQuery = "UPDATE Employee SET e";
		int position = "UPDATE Employee SET ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "employee");
	}

	@Test
	public final void test_Update_25() throws Exception {
		String jpqlQuery = "UPDATE ";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public final void test_Update_26() throws Exception {
		String jpqlQuery = "UPDATE Alias a";
		int position = "UPDATE ".length();
		testHasOnlyTheseProposals(jpqlQuery, position, entityNames());
	}

	@Test
	public final void test_Update_27() throws Exception {
		String jpqlQuery = "UPDATE Alias a";
		int position = "UPDATE Al".length();
		testHasOnlyTheseProposals(jpqlQuery, position, "Alias");
	}

	@Test
	public final void test_Update_28() {
		String jpqlQuery = "UPDATE Employee A SET";
		int position = "UPDATE Employee A ".length();
		testHasTheseProposals(jpqlQuery, position, SET);
	}

	@Test
	public final void test_Update_30() {
		String jpqlQuery = "UPDATE Employee A ";
		int position = jpqlQuery.length() - 1;
		testHasOnlyTheseProposals(jpqlQuery, position, AS);
	}

	@Test
	public final void test_Update_31() {
		String jpqlQuery = "UPDATE Employee AS ";
		int position = jpqlQuery.length();
		testHasNoProposals(jpqlQuery, position);
	}

	@Test
	public final void test_Update_32() throws Exception {
		String jpqlQuery = "UPDATE Employee AS e SET e.";
		int position = "UPDATE Employee AS e SET e.".length();
		testHasTheseProposals(jpqlQuery, position, "department", "empId", "manager", "name", "salary", "dept");
	}

	@Test
	public final void test_Where_01() {
		String jpqlQuery = "SELECT e FROM Employee e ";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE COUNT(e) >= 5";
		int position = "SELECT e FROM Employee e ".length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_03() {
		String jpqlQuery = "SELECT e FROM Employee e W";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_04() {
		String jpqlQuery = "SELECT e FROM Employee e WH";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_05() {
		String jpqlQuery = "SELECT e FROM Employee e WHE";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_06() {
		String jpqlQuery = "SELECT e FROM Employee e WHER";
		int position = jpqlQuery.length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_07() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE";
		int position = jpqlQuery.length();
		testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_08() {
		String jpqlQuery = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM E".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_09() {
		String jpqlQuery = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_10() {
		String jpqlQuery = "SELECT w FROM Employee AS w";
		int position = "SELECT w FROM Employee ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_11() {
		String jpqlQuery = "SELECT w FROM Employee AS w";
		int position = "SELECT w FROM Employee A".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_12() {
		String jpqlQuery = "SELECT w FROM Employee AS w";
		int position = "SELECT w FROM Employee AS".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_13() {
		String jpqlQuery = "SELECT w FROM Employee AS w";
		int position = "SELECT w FROM Employee AS ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_14() {
		String jpqlQuery = "SELECT w FROM Employee AS w";
		int position = "SELECT w FROM Employee AS ".length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_15() {
		String jpqlQuery = "SELECT w FROM Employee AS w ";
		int position = "SELECT w FROM Employee AS w ".length();
		testHasTheseProposals(jpqlQuery, position, WHERE);
	}

	@Test
	public final void test_Where_16() {
		String jpqlQuery = "SELECT e FROM Employee e J";
		int position = jpqlQuery.length();
		testDoesNotHaveTheseProposals(jpqlQuery, position, WHERE);
	}

	protected class TweakFunctionIdentifiers {

		protected void tweak(List<String> proposals,
		                     String identifier,
		                     String identifierFragment,
		                     int positionIndex) {

			// Special case for ALL/SOME/ANY
			if (identifier.equals(ALL) && identifier.equals(identifierFragment)) {
				if (!proposals.contains(SOME)) {
					proposals.add(SOME);
				}
				if (!proposals.contains(ANY)) {
					proposals.add(ANY);
				}
			}
			else if (identifier.equals(ANY) && identifier.equals(identifierFragment)) {
				if (!proposals.contains(ALL)) {
					proposals.add(ALL);
				}
				if (!proposals.contains(SOME)) {
					proposals.add(SOME);
				}
			}
			else if (identifier.equals(SOME) && identifier.equals(identifierFragment)) {
				if (!proposals.contains(ALL)) {
					proposals.add(ALL);
				}
				if (!proposals.contains(ANY)) {
					proposals.add(ANY);
				}
			}

			// Special case for CURRENT_DATE/CURRENT_TIME/CURRENT_TIMESTAMP
			else if (identifier.equals(CURRENT_DATE) && identifier.equals(identifierFragment)) {
				if (!proposals.contains(CURRENT_TIME)) {
					proposals.add(CURRENT_TIME);
				}
				if (!proposals.contains(CURRENT_TIMESTAMP)) {
					proposals.add(CURRENT_TIMESTAMP);
				}
			}
			else if (identifier.equals(CURRENT_TIME) && identifier.equals(identifierFragment)) {
				if (!proposals.contains(CURRENT_DATE)) {
					proposals.add(CURRENT_DATE);
				}
				if (!proposals.contains(CURRENT_TIMESTAMP)) {
					proposals.add(CURRENT_TIMESTAMP);
				}
			}
			else if (identifier.equals(CURRENT_TIMESTAMP) && identifier.equals(identifierFragment)) {
				if (!proposals.contains(CURRENT_DATE)) {
					proposals.add(CURRENT_DATE);
				}
				if (!proposals.contains(CURRENT_TIME)) {
					proposals.add(CURRENT_TIME);
				}
			}
			else if (identifier.equals(CURRENT_TIME) && identifierFragment.equals(CURRENT_TIMESTAMP) ||
			         identifier.equals(CURRENT_TIMESTAMP) && identifierFragment.equals(CURRENT_TIME)) {

				if (!proposals.contains(CURRENT_DATE)) {
					proposals.add(CURRENT_DATE);
				}
			}

			// Special case for EXISTS/NOT EXISTS
			else if (identifier.equals(EXISTS) && identifier.equals(identifierFragment) && positionIndex > 0) {
				if (!proposals.contains(NOT_EXISTS)) {
					proposals.add(NOT_EXISTS);
				}
			}

			// Special case for TRUE/FALSE/NULL
			else if (identifier.equals(TRUE) && identifier.equals(identifierFragment)) {
				if (!proposals.contains(FALSE)) {
					proposals.add(FALSE);
				}
				if (!proposals.contains(NULL)) {
					proposals.add(NULL);
				}
			}
			else if (identifier.equals(FALSE) && identifier.equals(identifierFragment)) {
				if (!proposals.contains(TRUE)) {
					proposals.add(TRUE);
				}
				if (!proposals.contains(NULL)) {
					proposals.add(NULL);
				}
			}
			else if (identifier.equals(NULL) && identifier.equals(identifierFragment)) {
				if (!proposals.contains(FALSE)) {
					proposals.add(FALSE);
				}
				if (!proposals.contains(TRUE)) {
					proposals.add(TRUE);
				}
			}
			else if (identifier.equals(NULLIF) && identifierFragment.equals(NULL)) {
				if (!proposals.contains(FALSE)) {
					proposals.add(FALSE);
				}
				if (!proposals.contains(TRUE)) {
					proposals.add(TRUE);
				}
			}
		}
	}
}