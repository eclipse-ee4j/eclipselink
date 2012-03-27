/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.jpql.parser.IdentifierRole;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The default implementation of {@link ContentAssistProposals} which stores the valid proposals.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultContentAssistProposals implements ContentAssistProposals {

	/**
	 * The set of possible abstract schema types.
	 */
	private Set<IEntity> abstractSchemaTypes;

	/**
	 * The set of possible identification variables.
	 */
	private Set<String> identificationVariables;

	/**
	 * The set of possible JPQL identifiers.
	 */
	private Set<String> identifiers;

	/**
	 * The {@link JPQLGrammar} that defines how the JPQL query was parsed.
	 */
	private JPQLGrammar jpqlGrammar;

	/**
	 * The set of possible {@link IMapping mappings}, which can be state fields, association fields
	 * and/or collection fields.
	 */
	private Set<IMapping> mappings;

	/**
	 * The identification variables mapped to their abstract schema types.
	 */
	private Map<String, IEntity> rangeIdentificationVariables;

	/**
	 * A JPQL identifier that is mapped to its longest counterpart.
	 */
	private static final Map<String, String> LONGUEST_IDENTIFIERS = buildLonguestIdentifiers();

	/**
	 * A JPQL identifier that is mapped to the list of counterparts used to find them in the query.
	 */
	private static final Map<String, List<String>> ORDERED_IDENTIFIERS = buildOrderedIdentifiers();

	/**
	 * Creates a new <code>DefaultContentAssistProposals</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} that defines how the JPQL query was parsed
	 */
	public DefaultContentAssistProposals(JPQLGrammar jpqlGrammar) {
		super();
		initialize(jpqlGrammar);
	}

	private static Map<String, String> buildLonguestIdentifiers() {

		Map<String, String> identifiers = new HashMap<String, String>();

		identifiers.put(IS_EMPTY,         IS_NOT_EMPTY);
		identifiers.put(IS_NULL,          IS_NOT_NULL);
		identifiers.put(IN,               NOT_IN);
		identifiers.put(BETWEEN,          NOT_BETWEEN);
		identifiers.put(MEMBER,           NOT_MEMBER_OF);
		identifiers.put(MEMBER_OF,        NOT_MEMBER_OF);
		identifiers.put(NOT_MEMBER,       NOT_MEMBER_OF);
		identifiers.put(JOIN,             LEFT_OUTER_JOIN_FETCH);
		identifiers.put(JOIN_FETCH,       LEFT_OUTER_JOIN_FETCH);
		identifiers.put(LEFT_JOIN,        LEFT_OUTER_JOIN_FETCH);
		identifiers.put(LEFT_JOIN_FETCH,  LEFT_OUTER_JOIN_FETCH);
		identifiers.put(LEFT_OUTER_JOIN,  LEFT_OUTER_JOIN_FETCH);
		identifiers.put(INNER_JOIN,       LEFT_OUTER_JOIN_FETCH);
		identifiers.put(INNER_JOIN_FETCH, LEFT_OUTER_JOIN_FETCH);

		identifiers.put(SELECT, DELETE_FROM);
		identifiers.put(UPDATE, DELETE_FROM);

		return identifiers;
	}

	private static Map<String, List<String>> buildOrderedIdentifiers() {

		Map<String, List<String>> identifiers = new HashMap<String, List<String>>();

		identifiers.put(IS_NOT_EMPTY, Collections.singletonList(IS_NOT_EMPTY));
		identifiers.put(IS_NOT_NULL,  Collections.singletonList(IS_NOT_NULL));
		identifiers.put(NOT_IN,       Collections.singletonList(NOT_IN));
		identifiers.put(NOT_BETWEEN,  Collections.singletonList(NOT_BETWEEN));

		List<String> members = new ArrayList<String>();
		members.add(MEMBER_OF);
		members.add(NOT_MEMBER);
		members.add(MEMBER);
		identifiers.put(NOT_MEMBER_OF, members);

		List<String> joins = new ArrayList<String>();
		joins.add(LEFT_OUTER_JOIN);
		joins.add(LEFT_JOIN_FETCH);
		joins.add(LEFT_JOIN);
		joins.add(INNER_JOIN_FETCH);
		joins.add(INNER_JOIN);
		joins.add(JOIN_FETCH);
		joins.add(JOIN);
		identifiers.put(LEFT_OUTER_JOIN_FETCH, joins);

		List<String> clauses = new ArrayList<String>();
		clauses.add(SELECT);
		clauses.add(UPDATE);
		identifiers.put(DELETE_FROM, clauses);

		return identifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableIterator<IEntity> abstractSchemaTypes() {
		return new CloneIterator<IEntity>(abstractSchemaTypes);
	}

	/**
	 * Adds the given {@link IEntity} as a possible abstract schema type.
	 *
	 * @param abstractSchemaType The abstract schema type that is a valid proposal
	 */
	public void addAbstractSchemaType(IEntity abstractSchemaType) {
		abstractSchemaTypes.add(abstractSchemaType);
	}

	/**
	 * Adds the given identification variable as a proposal.
	 *
	 * @param identificationVariable The identification variable that is a valid proposal
	 */
	public void addIdentificationVariable(String identificationVariable) {
		identificationVariables.add(identificationVariable);
	}

	/**
	 * Adds the given JPQL identifier as a proposal.
	 *
	 * @param identifier The JPQL identifier that is a valid proposal
	 */
	public void addIdentifier(String identifier) {
		identifiers.add(identifier);
	}

	/**
	 * Adds the given {@link IMapping mapping} (state field, association field or collection field)
	 * as a valid proposal.
	 *
	 * @param mapping The {@link IMapping} of the state field, association field or collection field
	 */
	public void addMapping(IMapping mapping) {
		mappings.add(mapping);
	}

	/**
	 * Adds the given {@link IMapping mappings} (state fields, association fields or collection fields)
	 * as valid proposals.
	 *
	 * @param mappings The {@link IMapping mappings} of the state fields, association fields or
	 * collection fields
	 */
	public void addMappings(Collection<IMapping> mappings) {
		this.mappings.addAll(mappings);
	}

	/**
	 * Adds the given range identification variable that is mapping the given abstract schema type.
	 *
	 * @param identificationVariable The range identification variable mapping the abstract schema name
	 * @param abstractSchemaType The abstract type name that identifies the type of the variable
	 */
	public void addRangeIdentificationVariable(String identificationVariable,
	                                           IEntity abstractSchemaType) {

		rangeIdentificationVariables.put(identificationVariable, abstractSchemaType);
	}

	/**
	 * {@inheritDoc}
	 */
	public Result buildEscapedQuery(String jpqlQuery, String proposal, int position, boolean insert) {

		Result result = buildQuery(jpqlQuery, proposal, position, insert);

		// Escape the JPQL query and adjust the position accordingly
		int[] positions = { result.position };
		result.jpqlQuery = ExpressionTools.escape(result.jpqlQuery, positions);
		result.position  = positions[0];

		return result;
	}

	/**
	 * Calculates the start and end position for correctly inserting the proposal into the query.
	 *
	 * @param wordParser This parser can be used to retrieve words from the cursor position
	 * @param proposal The proposal to be inserted into the query
	 * @param insert Flag that determines if the proposal is simply inserted or it should also replace
	 * the partial word following the position of the cursor
	 * @return The start and end positions
	 */
	private int[] buildPositions(WordParser wordParser, String proposal, boolean insert) {

		int index;
		String wordsToReplace = null;

		// Special case for arithmetic symbol (<, <=, =, <>, >, >=, +, -, *, /)
		if (wordParser.isArithmeticSymbol(proposal.charAt(0))) {
			int startIndex = wordParser.position();
			int endIndex   = startIndex;
			char character;

			// Now look for the start index
			do {
				character = wordParser.character(--startIndex);
			}
			while (wordParser.isArithmeticSymbol(character));

			// Now look for the end index
			do {
				character = wordParser.character(endIndex++);
			}
			while (wordParser.isArithmeticSymbol(character));

			// Increment the start index because the loop always decrements it by one before stopping
			// and decrement the end position because it's always incremented by one before stopping
			index = ++startIndex;
			wordsToReplace = wordParser.substring(index, --endIndex);
		}
		else {
			// Always try to find the start position using the longest JPQL identifier
			// if the proposal has more than one word
			wordsToReplace = longuestIdentifier(proposal);
			index = startPositionImp(wordParser, wordsToReplace);

			// Now check with the other possibilities
			if ((index == -1) && ORDERED_IDENTIFIERS.containsKey(wordsToReplace)) {
				for (String identifier : ORDERED_IDENTIFIERS.get(wordsToReplace)) {
					wordsToReplace = identifier;
					index = startPositionImp(wordParser, wordsToReplace);
					if (index > -1) {
						break;
					}
				}
			}
		}

		// Now calculate the start position
		int startPosition;

		if (index > -1) {
			startPosition = index;
		}
		else {
			// Check for path expression (which uses a dot) and update the wordToReplace
			// and startPosition accordingly
			String partialWord = wordParser.partialWord();
			String entireWord = wordParser.entireWord();
			int dotIndex = partialWord.lastIndexOf(".");

			if (dotIndex > 0) {
				wordsToReplace = entireWord.substring(dotIndex + 1);
				startPosition = wordParser.position() - partialWord.length() + dotIndex + 1;
			}
			else if (insert) {
				wordsToReplace = wordParser.word();
				startPosition = wordParser.position() - partialWord.length();
			}
			else {
				wordsToReplace = entireWord;
				startPosition = wordParser.position() - partialWord.length();
			}
		}

		// Now calculate the end position
		int length;

		// Only the partial word to the left of the cursor will be replaced and
		// the partial word to the right of the cursor will stay
		if (insert) {
			String partialWord = wordParser.substring(startPosition, wordParser.position());
			length = partialWord.length();
		}
		else if (proposal != wordsToReplace) {
			length = wordsToReplace.length();
		}
		else {
			index = 0;

			for (int charIndex = 0, wordLength = wordParser.length(), count = wordsToReplace.length();
			     startPosition + index < wordLength && charIndex < count;
			     index++, charIndex++) {

				// Find the end position by matching the proposal with the content of the query by
				// scanning every character starting at the start position, the end position will be
				// the position of the first character that is different or at the end of the query
				if (wordParser.character(startPosition + charIndex) !=
					 wordsToReplace.charAt(charIndex)) {

					break;
				}
			}

			length = index;
		}

		return new int[] { startPosition, startPosition + length };
	}

	/**
	 * {@inheritDoc}
	 */
	public Result buildQuery(String jpqlQuery, String proposal, int position, boolean insert) {

		// Nothing to replace
		if (ExpressionTools.stringIsEmpty(proposal)) {
			return new Result(jpqlQuery, position);
		}

		WordParser wordParser = new WordParser(jpqlQuery);
		wordParser.setPosition(position);

		// Calculate the start and end positions
		int[] positions = buildPositions(wordParser, proposal, insert);

		// Create the new query
		StringBuilder sb = new StringBuilder(jpqlQuery);
		sb.replace(positions[0], positions[1], proposal);

		// Return the result
		return new Result(sb.toString(), positions[0] + proposal.length());
	}

	/**
	 * {@inheritDoc}
	 */
	public IEntity getAbstractSchemaType(String identificationVariable) {
		return rangeIdentificationVariables.get(identificationVariable);
	}

	/**
	 * Returns the {@link JPQLGrammar} that defines how the JPQL query was parsed.
	 *
	 * @return The {@link JPQLGrammar} that was used to parse this {@link org.eclipse.persistence.
	 * jpa.jpql.parser.Expression Expression}
	 */
	public JPQLGrammar getGrammar() {
		return jpqlGrammar;
	}

	/**
	 * {@inheritDoc}
	 */
	public IdentifierRole getIdentifierRole(String identifier) {
		return jpqlGrammar.getExpressionRegistry().getIdentifierRole(identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasProposals() {
		return !mappings.isEmpty()            ||
		       !identifiers.isEmpty()         ||
		       !abstractSchemaTypes.isEmpty() ||
		       !identificationVariables.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableIterator<String> identificationVariables() {
		List<String> variables = new ArrayList<String>(identificationVariables.size() + rangeIdentificationVariables.size());
		variables.addAll(identificationVariables);
		variables.addAll(rangeIdentificationVariables.keySet());
		return new CloneIterator<String>(variables);
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableIterator<String> identifiers() {
		return new CloneIterator<String>(identifiers);
	}

	private void initialize(JPQLGrammar jpqlGrammar) {

		this.jpqlGrammar                  = jpqlGrammar;
		this.mappings                     = new HashSet<IMapping>();
		this.identifiers                  = new HashSet<String>();
		this.abstractSchemaTypes          = new HashSet<IEntity>();
		this.identificationVariables      = new HashSet<String>();
		this.rangeIdentificationVariables = new HashMap<String, IEntity>();
	}

	private String longuestIdentifier(String proposal) {
		return LONGUEST_IDENTIFIERS.containsKey(proposal) ? LONGUEST_IDENTIFIERS.get(proposal) : proposal;
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableIterator<IMapping> mappings() {
		return new CloneIterator<IMapping>(mappings);
	}

	/**
	 * This is only used by the unit-tests, it removes the given proposal from one of the collection
	 * of possible proposals.
	 *
	 * @param proposal The proposal to remove
	 * @return <code>true</code> the given proposal was removed from one of the collections;
	 * <code>false</code> if it could not be found, thus not removed
	 */
	public boolean remove(String proposal) {

		boolean removed = identifiers.remove(proposal)             ||
		                  identificationVariables.remove(proposal) ||
		                  rangeIdentificationVariables.remove(proposal) != null;

		if (!removed) {
			for (Iterator<IMapping> iter = mappings.iterator(); iter.hasNext(); ) {
				IMapping mapping = iter.next();
				if (mapping.getName().equals(proposal)) {
					iter.remove();
					removed = true;
					break;
				}
			}
		}

		if (!removed) {
			for (Iterator<IEntity> iter = abstractSchemaTypes.iterator(); iter.hasNext(); ) {
				IEntity entity = iter.next();
				if (entity.getName().equals(proposal)) {
					iter.remove();
					removed = true;
					break;
				}
			}
		}

		return removed;
	}

	private int startPositionImp(WordParser wordParser, String proposal) {

		int index = wordParser.position();
		int maxMoveLength = proposal.length();

		while (index >= 0 && maxMoveLength > 0) {
			if (wordParser.startsWith(proposal, index)) {
				return index;
			}
			index--;
			maxMoveLength--;
		}

		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		if (!identifiers.isEmpty()) {
			sb.append(identifiers);
		}

		if (!abstractSchemaTypes.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(abstractSchemaTypes);
		}

		if (!identificationVariables.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(identificationVariables);
		}

		if (!mappings.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(mappings);
		}

		if (sb.length() == 0) {
			sb.append("<No Default Proposals>");
		}

		return sb.toString();
	}

	/**
	 * This contains the result of inserting a proposal into a JPQL query at a given position.
	 *
	 * @see ContentAssistProposals#buildEscapedQuery(String, String, int, boolean)
	 * @see ContentAssistProposals#buildQuery(String, String, int, boolean)
	 */
	private final class Result implements ResultQuery {

		/**
		 * The new JPQL query after insertion of the proposal.
		 */
		private String jpqlQuery;

		/**
		 * The position of the cursor within the new query.
		 */
		private int position;

		/**
		 * Creates a new <code>Result</code>.
		 *
		 * @param jpqlQuery The new JPQL query after insertion of the proposal
		 * @param position The position of the cursor within the new query
		 */
		public Result(String jpqlQuery, int position) {
			super();
			this.jpqlQuery = jpqlQuery;
			this.position  = position;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getPosition() {
			return position;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getQuery() {
			return jpqlQuery;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Query=[").append(jpqlQuery);
			sb.append("], position=").append(position);
			return sb.toString();
		}
	}
}