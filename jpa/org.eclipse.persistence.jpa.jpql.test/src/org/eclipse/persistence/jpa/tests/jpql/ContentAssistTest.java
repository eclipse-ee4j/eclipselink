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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.ContentAssistProposals;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionRegistry;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaQuery;
import org.eclipse.persistence.jpa.jpql.util.CollectionTools;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryBNFAccessor;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.junit.Assert.*;

/**
 * The abstract unit-test providing helper methods required for testing content assist.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class ContentAssistTest extends JPQLCoreTest {

	protected JPQLQueryBNFAccessor bnfAccessor;
	@JPQLQueryHelperTestHelper
	protected AbstractJPQLQueryHelper queryHelper;
	protected JavaQuery virtualQuery;

	protected final List<String> addAll(List<String> items1, Iterable<String> items2) {
		for (String item2 : items2) {
			items1.add(item2);
		}
		return items1;
	}

	protected void addClauseIdentifiers(String afterIdentifier,
	                                    String beforeIdentifier,
	                                    List<String> proposals) {

		if (afterIdentifier == SELECT) {
			proposals.add(FROM);
		}
		else if (afterIdentifier == FROM) {
			proposals.add(WHERE);

			if (beforeIdentifier != GROUP_BY) {
				proposals.add(GROUP_BY);

				if (beforeIdentifier != HAVING) {
					proposals.add(HAVING);

					if (beforeIdentifier != ORDER_BY) {
						proposals.add(ORDER_BY);
					}
				}
			}
		}
		else if (afterIdentifier == WHERE) {
			proposals.add(GROUP_BY);
			proposals.add(HAVING);
			proposals.add(ORDER_BY);
		}
		else if (afterIdentifier == GROUP_BY) {
			proposals.add(HAVING);
			proposals.add(ORDER_BY);
		}
		else if (afterIdentifier == HAVING) {
			proposals.add(ORDER_BY);
		}
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

	protected final ContentAssistProposals buildContentAssistProposals(String actualQuery,
	                                                                   int position) {

		queryHelper.setQuery(buildQuery(actualQuery));
		return queryHelper.buildContentAssistProposals(position);
	}

	protected final IQuery buildQuery(String jpqlQuery) {
		virtualQuery.setExpression(jpqlQuery);
		return virtualQuery;
	}

	/**
	 * 0 : proposals that were not found by content assist but that are expected.<br/>
	 * 1 : proposals that were found by content assist but that are not expected.<br/>
	 */
	@SuppressWarnings("unchecked")
	protected List<String>[] buildResults(String jpqlQuery,
	                                      int position,
	                                      Iterable<String> expectedProposals) {

		ContentAssistProposals contentAssistProposals = buildContentAssistProposals(jpqlQuery, position);
		List<String> unexpectedProposals = new ArrayList<String>();
		Iterator<String> iter = expectedProposals.iterator();
		boolean removed = true;

		// Identification variables
		for (String identificationVariable : contentAssistProposals.identificationVariables()) {

			// Iterate through the expected proposals and see if the identification variable is expected
			while (iter.hasNext()) {
				String proposal = iter.next();

				// The identification variable is an expected proposal
				if (proposal.equalsIgnoreCase(identificationVariable)) {
					// Remove it from the list of expected proposals
					iter.remove();
					// Indicate it was removed, if not, we'll add the identification
					// variable to the list of unexpected proposals
					removed = true;
					break;
				}
			}

			// The identification variable found by content assist is not expected
			if (!removed) {
				unexpectedProposals.add(identificationVariable);
			}
		}

		// JPQL identifiers
		for (String identifier : contentAssistProposals.identifiers()) {

			// Iterate through the expected proposals and see if the identifier is expected
			while (iter.hasNext()) {
				String proposal = iter.next();

				// The identifier is an expected proposal
				if (proposal.equalsIgnoreCase(identifier)) {
					// Remove it from the list of expected proposals
					iter.remove();
					// Indicate it was removed, if not, we'll add the identifier
					// to the list of unexpected proposals
					removed = true;
					break;
				}
			}

			// The JPQL identifier found by content assist is not expected
			if (!removed) {
				unexpectedProposals.add(identifier);
			}
		}

		// Entities
		for (IEntity entity : contentAssistProposals.abstractSchemaTypes()) {

			String entityName = entity.getName();

			// Iterate through the expected proposals and see if the entity name is expected
			while (iter.hasNext()) {
				String proposal = iter.next();

				// The entity name is an expected proposal
				if (proposal.equalsIgnoreCase(entityName)) {
					// Remove it from the list of expected proposals
					iter.remove();
					// Indicate it was removed, if not, we'll add the entity name
					// to the list of unexpected proposals
					removed = true;
					break;
				}
			}

			// The entity found by content assist is not expected
			if (!removed) {
				unexpectedProposals.add(entityName);
			}
		}

		// Mappings
		for (IMapping mapping : contentAssistProposals.mappings()) {

			String mappingName = mapping.getName();

			// Iterate through the expected proposals and see if the mapping name is expected
			while (iter.hasNext()) {
				String proposal = iter.next();

				// The mapping name is an expected proposal
				if (proposal.equalsIgnoreCase(mappingName)) {
					// Remove it from the list of expected proposals
					iter.remove();
					// Indicate it was removed, if not, we'll add the mapping name
					// to the list of unexpected proposals
					removed = true;
					break;
				}
			}

			// The mapping found by the content assist is not expected
			if (!removed) {
				unexpectedProposals.add(mappingName);
			}
		}

		// The remaining proposals were not part of any proposal list
		List<String> proposalsNotRemoved = new ArrayList<String>();

		while (iter.hasNext()) {
			proposalsNotRemoved.add(iter.next());
		}

		return new List[] { proposalsNotRemoved, unexpectedProposals };
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

		for (String proposal : proposals) {
			if (ExpressionTools.startWithIgnoreCase(proposal, startsWith)) {
				results.add(proposal);
			}
		}

		return results;
	}

	protected final JPQLGrammar getGrammar() {
		return queryHelper.getGrammar();
	}

	protected final JPQLQueryBNFAccessor getQueryBNFAccessor() {
		return bnfAccessor;
	}

	protected final AbstractJPQLQueryHelper getQueryHelper() {
		return queryHelper;
	}

	protected final JavaQuery getVirtualQuery() {
		return virtualQuery;
	}

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

	protected final JPQLGrammar jpqlGrammar() {
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

	protected final void testDoesNotHaveTheseProposals(String jpqlQuery,
	                                                   int position,
	                                                   Enum<?>... proposals) {

		testDoesNotHaveTheseProposals(jpqlQuery, position, toString(proposals));
	}

	protected final void testDoesNotHaveTheseProposals(String jpqlQuery,
	                                                   int position,
	                                                   Iterable<String> proposals) {

		List<String>[] results = buildResults(jpqlQuery, position, Collections.<String>emptyList());
		List<String> unexpectedProposals = results[1];

		for (String proposal : proposals) {
			unexpectedProposals.remove(proposal);
		}

		assertTrue(unexpectedProposals + " should not be proposals.", unexpectedProposals.isEmpty());
	}

	protected final void testDoesNotHaveTheseProposals(String jpqlQuery,
	                                                   int position,
	                                                   String... proposals) {

		testDoesNotHaveTheseProposals(jpqlQuery, position, CollectionTools.list(proposals));
	}

	protected final void testHasNoProposals(String jpqlQuery, int position) {

		List<String>[] results = buildResults(jpqlQuery, position, Collections.<String>emptyList());
		List<String> unexpectedProposals = results[1];
		assertTrue(unexpectedProposals + " should not be proposals.", unexpectedProposals.isEmpty());
	}

	protected final void testHasOnlyTheseProposals(String jpqlQuery,
	                                               int position,
	                                               Enum<?>... proposals) {

		testHasOnlyTheseProposals(jpqlQuery, position, toString(proposals));
	}

	protected final void testHasOnlyTheseProposals(String jpqlQuery, int position, Iterable<String> proposals) {

		List<String>[] results = buildResults(jpqlQuery, position, proposals);
		List<String> proposalsNotRemoved = results[0];
		List<String> unexpectedProposals = results[1];

		// Inconsistent list of proposals
		if (!unexpectedProposals.isEmpty() && !proposalsNotRemoved.isEmpty()) {
			if (proposalsNotRemoved.size() == 1) {
				fail(proposalsNotRemoved + " should be a proposal and " + unexpectedProposals + " should not be a proposal.");
			}
			else {
				fail(proposalsNotRemoved + " should be proposals and " + unexpectedProposals + " should not be proposals.");
			}
		}
		// Added more proposals than it should
		else if (!unexpectedProposals.isEmpty() && proposalsNotRemoved.isEmpty()) {
			if (proposalsNotRemoved.size() == 1) {
				fail(unexpectedProposals + " should not be a proposal.");
			}
			else {
				fail(unexpectedProposals + " should not be proposals.");
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

	protected final void testHasOnlyTheseProposals(String jpqlQuery,
	                                               int position,
	                                               String... proposals) {

		testHasOnlyTheseProposals(jpqlQuery, position, CollectionTools.list(proposals));
	}

	protected final void testHasTheseProposals(String jpqlQuery,
	                                           int position,
	                                           Enum<?>... enums) {

		testHasTheseProposals(jpqlQuery, position, toString(enums));
	}

	protected final void testHasTheseProposals(String jpqlQuery,
	                                           int position,
	                                           Iterable<String> proposals) {

		List<String>[] results = buildResults(jpqlQuery, position, proposals);
		List<String> proposalsNotRemoved = results[0];
		assertTrue(proposalsNotRemoved + " should be proposals.", proposalsNotRemoved.isEmpty());
	}

	protected final void testHasTheseProposals(String jpqlQuery,
	                                           int position,
	                                           String... proposals) {

		testHasTheseProposals(jpqlQuery, position, CollectionTools.list(proposals));
	}

	protected final String[] toString(Enum<?>[] enums) {

		String[] names = new String[enums.length];

		for (int index = enums.length; --index >= 0; ) {
			names[index] = enums[index].name();
		}

		return names;
	}
}
