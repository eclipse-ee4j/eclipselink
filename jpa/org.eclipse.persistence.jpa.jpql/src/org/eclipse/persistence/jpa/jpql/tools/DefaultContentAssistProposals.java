/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.BETWEEN;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.DELETE_FROM;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.IN;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.INNER_JOIN;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.INNER_JOIN_FETCH;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.IS_EMPTY;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.IS_NOT_EMPTY;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.IS_NOT_NULL;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.IS_NULL;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.JOIN;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.JOIN_FETCH;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.LEFT_JOIN;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.LEFT_JOIN_FETCH;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.LEFT_OUTER_JOIN;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.LEFT_OUTER_JOIN_FETCH;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.MEMBER;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.MEMBER_OF;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.NOT_BETWEEN;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.NOT_IN;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.NOT_MEMBER;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.NOT_MEMBER_OF;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.SELECT;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.UPDATE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.parser.IdentifierRole;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.utility.XmlEscapeCharacterConverter;
import org.eclipse.persistence.jpa.jpql.tools.utility.iterable.SnapshotCloneIterable;

/**
 * The default implementation of {@link ContentAssistProposals} which stores the valid proposals.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultContentAssistProposals implements ContentAssistProposals {

    /**
     * The prefix that is used to filter the list of class names.
     *
     * @since 2.5
     */
    private String classNamePrefix;

    /**
     * The class type helps to filter the various types of classes, i.e. anonymous, member, interface
     * are never allowed.
     *
     * @since 2.5
     */
    private ClassType classType;

    /**
     * TODO
     *
     * @since 2.5
     */
    private String columnNamePrefix;

    /**
     * The set of possible abstract schema types.
     */
    private Set<IEntity> entities;

    /**
     * TODO
     *
     * @since 2.5
     */
    private Map<IType, DefaultEnumProposals> enumProposals;

    /**
     * This extension can be used to provide additional support to JPQL content assist that is
     * outside the scope of providing proposals related to JPA metadata. It adds support for
     * providing suggestions related to class names, enum constants, table names, column names.
     *
     * @since 2.5
     */
    private ContentAssistExtension extension;

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
     * TODO
     *
     * @since 2.5
     */
    private String tableName;

    /**
     * TODO
     *
     * @since 2.5
     */
    private String tableNamePrefix;

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
     * @param extension This extension can be used to provide additional support to JPQL content
     * assist that is outside the scope of providing proposals related to JPA metadata. It adds
     * support for providing suggestions related to class names, enum constants, table names, column
     * names
     */
    public DefaultContentAssistProposals(JPQLGrammar jpqlGrammar, ContentAssistExtension extension) {
        super();
        initialize(jpqlGrammar, extension);
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
    @Override
    public Iterable<IEntity> abstractSchemaTypes() {
        return new SnapshotCloneIterable<IEntity>(entities);
    }

    /**
     * Adds the given {@link IEntity} as a possible abstract schema type.
     *
     * @param abstractSchemaType The abstract schema type that is a valid proposal
     */
    public void addEntity(IEntity abstractSchemaType) {
        entities.add(abstractSchemaType);
    }

    /**
     * Adds the constants of the given enum constant as a valid proposal.
     *
     * @param enumType The {@link IType} of the enum type
     * @param enumConstant The enum constant to be added as a valid proposal
     *
     * @since 2.5
     */
    public void addEnumConstant(IType enumType, String enumConstant) {

        DefaultEnumProposals proposal = enumProposals.get(enumType);

        if (proposal == null) {
            proposal = new DefaultEnumProposals(enumType);
            enumProposals.put(enumType, proposal);
        }

        proposal.constants.add(enumConstant);
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
     * @param entity The abstract type name that identifies the type of the variable
     */
    public void addRangeIdentificationVariable(String identificationVariable, IEntity entity) {
        rangeIdentificationVariables.put(identificationVariable, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    public int[] buildPositions(WordParser wordParser, String proposal, boolean insert) {

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
            index = startPosition(wordParser, wordsToReplace);

            // Now check with the other possibilities
            if ((index == -1) && ORDERED_IDENTIFIERS.containsKey(wordsToReplace)) {
                for (String identifier : ORDERED_IDENTIFIERS.get(wordsToReplace)) {
                    wordsToReplace = identifier;
                    index = startPosition(wordParser, wordsToReplace);
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
            int dotIndex = -1;

            if (isMappingName(proposal)  ||
                isEnumConstant(proposal) ||
                isColumnName(proposal)) {

                dotIndex = partialWord.lastIndexOf('.');
            }

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
    @Override
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
    @Override
    public ResultQuery buildXmlQuery(String jpqlQuery, String proposal, int position, boolean insert) {

        // Nothing to replace
        if (ExpressionTools.stringIsEmpty(proposal)) {
            return new Result(jpqlQuery, position);
        }

        int[] positions = { position };

        // First convert the escape characters into their unicode characters
        String query = XmlEscapeCharacterConverter.unescape(jpqlQuery, positions);

        // Calculate the start and end positions
        WordParser wordParser = new WordParser(query);
        wordParser.setPosition(positions[0]);
        int[] proposalPositions = buildPositions(wordParser, proposal, insert);

        // Escape the proposal
        proposal = XmlEscapeCharacterConverter.escape(proposal, new int[1]);

        // Adjust the positions so it's in the original JPQL query, which may contain escaped characters
        XmlEscapeCharacterConverter.reposition(jpqlQuery, proposalPositions);

        // Create the new JPQL query
        StringBuilder sb = new StringBuilder(jpqlQuery);
        sb.replace(proposalPositions[0], proposalPositions[1], proposal);

        // Return the result
        return new Result(sb.toString(), proposalPositions[0] + proposal.length());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<String> classNames() {
        if (classNamePrefix == null) {
            return Collections.emptyList();
        }
        return extension.classNames(classNamePrefix, classType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<String> columnNames() {
        if (tableName == null) {
            return Collections.emptyList();
        }
        return extension.columnNames(tableName, columnNamePrefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<EnumProposals> enumConstant() {
        return new SnapshotCloneIterable<EnumProposals>(enumProposals.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEntity getAbstractSchemaType(String identificationVariable) {
        return rangeIdentificationVariables.get(identificationVariable);
    }

    /**
     * Returns the prefix that will be used to filter the list of possible class names.
     *
     * @return The prefix that is used to filter the list of class names or <code>null</code> if it
     * was not set for the cursor position within the JPQL query
     * @since 2.5
     */
    public String getClassNamePrefix() {
        return classNamePrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassType getClassType() {
        return classType;
    }

    /**
     * Returns the prefix that will be used by {@link ContentAssistExtension} to filter the column
     * names if the table name is not <code>null</code>.
     *
     * @return The prefix that is used to filter the list of columns names, which is <code>null</code>
     * if it has not been set along with the table name
     * @since 2.5
     */
    public String getColumnNamePrefix() {
        return columnNamePrefix;
    }

    /**
     * Returns the {@link JPQLGrammar} that defines how the JPQL query was parsed.
     *
     * @return The {@link JPQLGrammar} that was used to parse this {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}
     */
    public JPQLGrammar getGrammar() {
        return jpqlGrammar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IdentifierRole getIdentifierRole(String identifier) {
        return jpqlGrammar.getExpressionRegistry().getIdentifierRole(identifier);
    }

    /**
     * Returns the table name that will be used by {@link ContentAssistExtension} to retrieve the
     * column names.
     *
     * @return The name of the table for which its column names should be retrieve as possible proposals
     * @since 2.5
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Returns the prefix that will be used to filter the list of possible table names.
     *
     * @return The prefix that is used to filter the list of table names or <code>null</code> if it
     * was not set for the cursor position within the JPQL query
     * @since 2.5
     */
    public String getTableNamePrefix() {
        return tableNamePrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasProposals() {
        return !mappings.isEmpty()                ||
               !entities.isEmpty()                ||
               !identifiers.isEmpty()             ||
               !enumProposals.isEmpty()           ||
               !identificationVariables.isEmpty() ||
                classNames().iterator().hasNext() ||
                tableNames().iterator().hasNext() ||
               columnNames().iterator().hasNext() ||
               !rangeIdentificationVariables.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<String> identificationVariables() {
        List<String> variables = new ArrayList<String>(identificationVariables.size() + rangeIdentificationVariables.size());
        variables.addAll(identificationVariables);
        variables.addAll(rangeIdentificationVariables.keySet());
        return new SnapshotCloneIterable<String>(variables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<String> identifiers() {
        return new SnapshotCloneIterable<String>(identifiers);
    }

    protected void initialize(JPQLGrammar jpqlGrammar, ContentAssistExtension extension) {

        this.extension                    = extension;
        this.jpqlGrammar                  = jpqlGrammar;
        this.mappings                     = new HashSet<IMapping>();
        this.identifiers                  = new HashSet<String>();
        this.entities                     = new HashSet<IEntity>();
        this.identificationVariables      = new HashSet<String>();
        this.rangeIdentificationVariables = new HashMap<String, IEntity>();
        this.enumProposals                = new HashMap<IType, DefaultEnumProposals>();
    }


    /**
     * Determines whether the given proposal is a column name (which should be unqualified).
     *
     * @param proposal The proposal that is being inserted into the JPQL query
     * @return <code>true</code> if the given proposal is a column name; <code>false</code> otherwise
     * @since 2.5
     */
    public boolean isColumnName(String proposal) {

        for (String columName : columnNames()) {
            if (columName.equals(proposal)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether the given proposal is an enum constant name (which should be unqualified).
     *
     * @param proposal The proposal that is being inserted into the JPQL query
     * @return <code>true</code> if the given proposal is a unqualified enum constant name;
     * <code>false</code> otherwise
     * @since 2.5
     */
    public boolean isEnumConstant(String proposal) {

        for (EnumProposals enumProposal : enumProposals.values()) {
            for (String enumConstant : enumProposal.enumConstants()) {
                if (enumConstant.equals(proposal)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines whether the given proposal is a mapping name.
     *
     * @param proposal The proposal that is being inserted into the JPQL query
     * @return <code>true</code> if the given proposal is a mapping name; <code>false</code> otherwise
     * @since 2.5
     */
    public boolean isMappingName(String proposal) {

        for (IMapping mapping : mappings) {
            if (mapping.getName().equals(proposal)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the longest possible JPQL identifier that is related to the given proposal if the
     * proposal is a JPQL identifier and contains multiple words. For instance, the longest form of
     * <code>JOIN</code> or <code>JOIN FETCH</code> is <code>LEFT OUTER JOIN FETCH</code>.
     *
     * @param proposal The proposal to retrieve its longest form if one is associated with it
     * @return Either the given proposal if it's not a JPQL identifier or it does not have a longer
     * form or the longest version of the JPQL identifier
     */
    public String longuestIdentifier(String proposal) {
        return LONGUEST_IDENTIFIERS.containsKey(proposal) ? LONGUEST_IDENTIFIERS.get(proposal) : proposal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IMapping> mappings() {
        return new SnapshotCloneIterable<IMapping>(mappings);
    }

    /**
     * Removes the given JPQL identifier.
     *
     * @param identifier The identifier that was added but actually needs to be removed
     */
    protected void removeIdentifier(String identifier) {
        identifiers.remove(identifier);
    }

    /**
     * Adds the given prefix that will be used to filter the list of possible class names.
     *
     * @param prefix The prefix that is used to filter the list of class names
     * @param classType Determines how to filter the various types of classes
     * @since 2.5
     */
    public void setClassNamePrefix(String prefix, ClassType classType) {
        this.classNamePrefix = prefix;
        this.classType = classType;
    }

    /**
     * Sets the table name and a prefix that will be used to filter the names of the table's columns.
     *
     * @param tableName The name of the table for which its column names should be retrieve as
     * possible proposals
     * @param prefix The prefix that is used to filter the list of columns names, which is never
     * <code>null</code> but can be an empty string
     * @since 2.5
     */
    public void setTableName(String tableName, String prefix) {
        this.tableName = tableName;
        this.columnNamePrefix = prefix;
    }

    /**
     * Adds the given prefix that will be used to filter the list of possible columns names.
     *
     * @param tableNamePrefix The prefix that is used to filter the list of columns names
     * @since 2.5
     */
    public void setTableNamePrefix(String tableNamePrefix) {
        this.tableNamePrefix = tableNamePrefix;
    }

    public int startPosition(WordParser wordParser, String proposal) {

        int index = wordParser.position();
        int maxMoveLength = proposal.length();

        while (index >= 0 && maxMoveLength > 0) {
            if (wordParser.startsWithIgnoreCase(proposal, index)) {
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
    public Iterable<String> tableNames() {
        if (tableNamePrefix == null) {
            return Collections.emptyList();
        }
        return extension.tableNames(tableNamePrefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        // JPQL identifiers
        if (!identifiers.isEmpty()) {
            sb.append(identifiers);
        }

        // Entity names
        if (!entities.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(entities);
        }

        // Identification variables
        if (!identificationVariables.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(identificationVariables);
        }

        // Range identification variables
        if (!rangeIdentificationVariables.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(rangeIdentificationVariables);
        }

        // Mappings
        if (!mappings.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(mappings);
        }

        // Class names
        for (String className : classNames()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(className);
        }

        // Table names
        for (String tableName : tableNames()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(tableName);
        }

        // Column names
        for (String columnName : columnNames()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(columnName);
        }

        // Enum constant names
        for (EnumProposals enumProposals : enumConstant()) {
            for (String enumConstant : enumProposals.enumConstants()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(enumConstant);
            }
        }

        // No proposals
        if (sb.length() == 0) {
            sb.append("<No Default Proposals>");
        }

        return sb.toString();
    }

    private static final class DefaultEnumProposals implements EnumProposals {

        private Set<String> constants;
        private IType enumType;

        DefaultEnumProposals(IType enumType) {
            super();
            this.enumType  = enumType;
            this.constants = new HashSet<String>();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<String> enumConstants() {
            return new SnapshotCloneIterable<String>(constants);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IType enumType() {
            return enumType;
        }
    }

    /**
     * This contains the result of inserting a proposal into a JPQL query at a given position.
     *
     * @see ContentAssistProposals#buildEscapedQuery(String, String, int, boolean)
     * @see ContentAssistProposals#buildQuery(String, String, int, boolean)
     */
    private static final class Result implements ResultQuery {

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
        @Override
        public int getPosition() {
            return position;
        }

        /**
         * {@inheritDoc}
         */
        @Override
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
