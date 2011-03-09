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
package org.eclipse.persistence.jpa.internal.jpql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.internal.jpql.parser.WordParser;
import org.eclipse.persistence.jpa.jpql.ContentAssistItems;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.ResultQuery;
import org.eclipse.persistence.jpa.jpql.spi.IMappingType;

import static org.eclipse.persistence.jpa.internal.jpql.parser.Expression.*;

/**
 * The default implementation of {@link ContentAssistItems} which stores the valid choices.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultContentAssistItems implements ContentAssistItems {

	/**
	 * The set of possible abstract schema names, which are the entity names.
	 */
	private Set<String> abstractSchemaNames;

	/**
	 * The set of possible identification variables.
	 */
	private Set<String> identificationVariables;

	/**
	 * The set of possible JPQL identifiers.
	 */
	private Set<String> identifiers;

	/**
	 * The set of possible properties, which are either state fields or collection valued fields.
	 */
	private Map<String, IMappingType> properties;

	/**
	 * The identification variables mapped to their abstract schema names.
	 */
	private Map<String, String> rangeIdentificationVariables;

	/**
	 * An JPQL identifier that is mapped to its longest counterpart.
	 */
	private static final Map<String, String> LONGUEST_IDENTIFIERS = buildLonguestIdentifiers();

	/**
	 * An JPQL identifier that is mapped to the list of counterparts used to find them in the query.
	 */
	private static final Map<String, List<String>> ORDERED_IDENTIFIERS = buildOrderedIdentifiers();

	/**
	 * Creates a new <code>DefaultContentAssistItems</code>.
	 */
	DefaultContentAssistItems() {
		super();
		initialize();
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

		return identifiers;
	}

	private static Map<String, List<String>> buildOrderedIdentifiers() {

		Map<String, List<String>> identifiers = new HashMap<String, List<String>>();

		identifiers.put(IS_NOT_EMPTY,         Collections.singletonList(IS_NOT_EMPTY));
		identifiers.put(IS_NOT_NULL,          Collections.singletonList(IS_NOT_NULL));
		identifiers.put(NOT_IN,               Collections.singletonList(NOT_IN));
		identifiers.put(NOT_BETWEEN,          Collections.singletonList(NOT_BETWEEN));

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

		return identifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<String> abstractSchemaNames() {
		return new ArrayList<String>(abstractSchemaNames).iterator();
	}

	/**
	 * Adds the given abstract schema name as a choice.
	 *
	 * @param abstractSchemaName The abstract schema name that is a valid choice
	 */
	public void addAbstractSchemaName(String abstractSchemaName) {
		abstractSchemaNames.add(abstractSchemaName);
	}

	/**
	 * Adds the given identification variable as a choice.
	 *
	 * @param identificationVariable The identification variable that is a valid choice
	 */
	public void addIdentificationVariable(String identificationVariable) {
		identificationVariables.add(identificationVariable);
	}

	/**
	 * Adds the given JPQL identifier as a choice.
	 *
	 * @param identifier The JPQL identifier that is a valid choice
	 */
	public void addIdentifier(String identifier) {
		identifiers.add(identifier);
	}

	/**
	 * Adds the given property (state field, association field or collection field) as a choice.
	 *
	 * @param property The name of the state field, association field or collection field, which does
	 * not include the full path
	 * @param mappingType The type of the mapping it represents
	 */
	public void addProperty(String property, IMappingType mappingType) {
		properties.put(property, mappingType);
	}

	/**
	 * Adds the given range identification variable that is mapping the given abstract schema name.
	 *
	 * @param identificationVariable The range identification variable mapping the abstract schema name
	 * @param abstractSchemaName The abstract schema name that identifies the type of the variable
	 */
	public void addRangeIdentificationVariable(String identificationVariable,
	                                           String abstractSchemaName) {

		rangeIdentificationVariables.put(identificationVariable, abstractSchemaName);
	}

	/**
	 * Calculates the start and end position for correctly inserting the choice into the query.
	 *
	 * @param wordParser This parser can be used to retrieve words from the cursor position
	 * @param choice The choice to be inserted into the query
	 * @param insert Flag that determines if the choice is simply inserted or it should also replace
	 * the partial word following the position of the cursor
	 * @return The start and end positions
	 */
	private int[] buildPositions(WordParser wordParser, String choice, boolean insert) {

		int index;
		String wordsToReplace = null;

		// Special case for arithmetic symbol (<, <=, =, <>, >, >=, +, -, *, /)
		if (wordParser.isArithmeticSymbol(choice.charAt(0))) {
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

			// Increment the start index because the loop always decremements it by one before stopping
			// and decrement the end position because it's always incremented by one before stopping
			index = ++startIndex;
			wordsToReplace = wordParser.substring(index, --endIndex);
		}
		else {
			// Always try to find the start position using the longest JPQL identifier
			// if the choice has more than one word
			wordsToReplace = longuestIdentifier(choice);
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
			else {
				wordsToReplace = partialWord;
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
		else if (choice != wordsToReplace) {
			length = wordsToReplace.length();
		}
		else {
			index = 0;

			for (int charIndex = 0, wordLength = wordParser.length(), count = wordsToReplace.length();
			     startPosition + index < wordLength && charIndex < count;
			     index++, charIndex++) {

				// Find the end position by matching the choice with the content of the query by
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
	public ResultQuery buildQuery(String jpqlQuery, String choice, int position, boolean insert) {

		// Nothing to replace
		if (ExpressionTools.stringIsEmpty(choice)) {
			return new Result(jpqlQuery, position);
		}

		WordParser wordParser = new WordParser(jpqlQuery);
		wordParser.setPosition(position);

		// Calculate the start and end positions
		int[] positions = buildPositions(wordParser, choice, insert);

		// Create the new query
		StringBuilder sb = new StringBuilder(jpqlQuery);
		sb.replace(positions[0], positions[1], choice);

		// Now return the result
		return new Result(sb.toString(), positions[0] + choice.length());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getAbstractSchemaName(String identificationVariable) {
		return rangeIdentificationVariables.get(identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	public IMappingType getMappingType(String propertyName) {
		return properties.get(propertyName);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasItems() {
		return !properties.isEmpty()          ||
		       !identifiers.isEmpty()         ||
		       !abstractSchemaNames.isEmpty() ||
		       !identificationVariables.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<String> identificationVariables() {
		return new ArrayList<String>(identificationVariables).iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<String> identifiers() {
		return new ArrayList<String>(identifiers).iterator();
	}

	private void initialize() {
		identifiers                  = new HashSet<String>();
		abstractSchemaNames          = new HashSet<String>();
		identificationVariables      = new HashSet<String>();
		rangeIdentificationVariables = new HashMap<String, String>();
		properties                   = new HashMap<String, IMappingType>();
	}

	private String longuestIdentifier(String choice) {
		return LONGUEST_IDENTIFIERS.containsKey(choice) ? LONGUEST_IDENTIFIERS.get(choice) : choice;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<String> properties() {
		return new ArrayList<String>(properties.keySet()).iterator();
	}

	public boolean remove(String identifier) {
		return identifiers.remove(identifier)             ||
		       abstractSchemaNames.remove(identifier)     ||
		       identificationVariables.remove(identifier) ||
		       properties.remove(identifier) != null      ||
		       rangeIdentificationVariables.remove(identifier) != null;
	}

	private int startPositionImp(WordParser wordParser, String choice) {

		int index = wordParser.position();
		int maxMoveLength = choice.length();

		while (index >= 0 && maxMoveLength > 0) {
			if (wordParser.startsWith(choice, index)) {
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

		if (!abstractSchemaNames.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(abstractSchemaNames);
		}

		if (!identificationVariables.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(identificationVariables);
		}

		if (!properties.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(properties);
		}

		if (sb.length() == 0) {
			sb.append("<No Default Proposals>");
		}

		return sb.toString();
	}

	/**
	 * This contains the result of inserting a choice into a JPQL query at a given position.
	 *
	 * @see ContentAssistItems#buildQuery(String, String, int, boolean)
	 */
	private final class Result implements ResultQuery {

		/**
		 * The new JPQL query after insertion of the choice.
		 */
		private final String jpqlQuery;

		/**
		 * The position of the cursor within the new query.
		 */
		private final int position;

		/**
		 * Creates a new <code>Result</code>.
		 *
		 * @param jpqlQuery The new JPQL query after insertion of the choice
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
	}
}