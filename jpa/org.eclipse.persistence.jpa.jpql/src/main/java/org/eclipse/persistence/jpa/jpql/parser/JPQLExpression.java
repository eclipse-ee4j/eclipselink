/*
 * Copyright (c) 2006, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;

/**
 * A <code>JPQLExpression</code> is the root of the parsed tree representation of a JPQL query. The
 * query is parsed based on what was registered in the {@link JPQLGrammar}'s {@link ExpressionRegistry}.
 * <p>
 * A JPQL statement may be either a <b>SELECT</b> statement, an <b>UPDATE</b> statement, or a
 * <b>DELETE FROM</b> statement.
 *
 * <div><b>BNF:</b> <code>QL_statement ::= {@link SelectStatement select_statement} |
 *                                                {@link UpdateStatement update_statement} |
 *                                                {@link DeleteStatement delete_statement}</code></div>
 * <p>
 * It is possible to parse a portion of a JPQL query. The ID of the {@link JPQLQueryBNF} is used to
 * parse that portion and {@link #getQueryStatement()} then returns only the parsed tree representation
 * of that JPQL fragment.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLExpression extends AbstractExpression {

    /**
     * Parser type constant which allows to generate missing Entity alias for SELECT queries like "SELECT e FROM Entity".
     * It matches org.eclipse.persistence.config.ParserValidationType#None.
     */
    static final String None = "None";

    /**
     * The JPQL grammar that defines how to parse a JPQL query.
     */
    private JPQLGrammar jpqlGrammar;

    /**
     * By default, this is {@link JPQLStatementBNF#ID} but it can be any other unique identifier of
     * a {@link JPQLQueryBNF} when a portion of a JPQL query needs to be parsed.
     */
    private String queryBNFId;

    /**
     * The tree representation of the query.
     */
    private AbstractExpression queryStatement;

    /**
     * Determines if the parsing system should be tolerant, meaning if it should try to parse invalid
     * or incomplete queries.
     */
    private boolean tolerant;

    /**
     * If the expression could not be fully parsed, meaning some unknown text is trailing the query,
     * this will contain it.
     */
    private AbstractExpression unknownEndingStatement;

    private String validationLevel;

    /**
     * Evaluated alias from SELECT part for SELECT queries like "SELECT e FROM Entity".
     */
    private String foundAlias;

    /**
     * Creates a new <code>JPQLExpression</code>, which is the root of the JPQL parsed tree.
     *
     * @param query The string representation of the JPQL query to parse
     * @param jpqlGrammar The JPQL grammar that defines how to parse a JPQL query
     */
    public JPQLExpression(CharSequence query, JPQLGrammar jpqlGrammar) {
        this(query, jpqlGrammar, false);
    }

    /**
     * Creates a new <code>JPQLExpression</code>, which is the root of the JPQL parsed tree.
     *
     * @param query The string representation of the JPQL query to parse
     * @param jpqlGrammar The JPQL grammar that defines how to parse a JPQL query
     * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
     * to parse invalid or incomplete queries
     */
    public JPQLExpression(CharSequence query, JPQLGrammar jpqlGrammar, boolean tolerant) {
        this(query, jpqlGrammar, JPQLStatementBNF.ID, tolerant);
    }

    /**
     * Creates a new <code>JPQLExpression</code> that will parse the given fragment of a JPQL query.
     * This means {@link #getQueryStatement()} will not return a query statement (select, delete or
     * update) but only the parsed tree representation of the fragment if the query BNF can pare it.
     * If the fragment of the JPQL query could not be parsed using the given {@link JPQLQueryBNF},
     * then {@link #getUnknownEndingStatement()} will contain the non-parsable fragment.
     *
     * @param jpqlFragment A fragment of a JPQL query, which is a portion of a complete JPQL query
     * @param jpqlGrammar The JPQL grammar that defines how to parse a JPQL query
     * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF JPQLQueryBNF}
     * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
     * to parse invalid or incomplete queries
     * @since 2.4
     */
    public JPQLExpression(CharSequence jpqlFragment,
                          JPQLGrammar jpqlGrammar,
                          String queryBNFId,
                          boolean tolerant) {

        this(jpqlGrammar, queryBNFId, tolerant, null);
        parse(new WordParser(jpqlFragment), tolerant);
    }

    /**
     * Creates a new <code>JPQLExpression</code> that will parse the given fragment of a JPQL query.
     * This means {@link #getQueryStatement()} will not return a query statement (select, delete or
     * update) but only the parsed tree representation of the fragment if the query BNF can pare it.
     * If the fragment of the JPQL query could not be parsed using the given {@link JPQLQueryBNF},
     * then {@link #getUnknownEndingStatement()} will contain the non-parsable fragment.
     *
     * @param jpqlFragment A fragment of a JPQL query, which is a portion of a complete JPQL query
     * @param jpqlGrammar The JPQL grammar that defines how to parse a JPQL query
     * @param queryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF JPQLQueryBNF}
     * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
     * to parse invalid or incomplete queries
     * @param validationLevel It matches some of the constants from org.eclipse.persistence.config.ParserValidationType. Should be null.
     * Used to control to generate missing Entity alias for SELECT queries like "SELECT e FROM Entity",
     * in case of org.eclipse.persistence.config.ParserValidationType#None.
     * @since 2.4
     */
    public JPQLExpression(CharSequence jpqlFragment,
                          JPQLGrammar jpqlGrammar,
                          String queryBNFId,
                          boolean tolerant,
                          String validationLevel) {

        this(jpqlGrammar, queryBNFId, tolerant, validationLevel);
        parse(new WordParser(jpqlFragment), tolerant);
    }

    /**
     * Creates a new <code>JPQLExpression</code>, which is the root of the JPQL parsed tree.
     *
     * @param jpqlGrammar The JPQL grammar that defines how to parse a JPQL query
     * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
     * @param validationLevel It matches some of the constants from org.eclipse.persistence.config.ParserValidationType. Should be null.
     * to parse invalid or incomplete queries
     */
    private JPQLExpression(JPQLGrammar jpqlGrammar, String queryBNFId, boolean tolerant, String validationLevel) {
        super(null);
        this.queryBNFId  = queryBNFId;
        this.tolerant    = tolerant;
        this.jpqlGrammar = jpqlGrammar;
        this.validationLevel = validationLevel;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
        getQueryStatement().accept(visitor);
        getUnknownEndingStatement().accept(visitor);
    }

    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getQueryStatement());
        children.add(getUnknownEndingStatement());
    }

    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {

        if (queryStatement != null) {
            children.add(queryStatement);
        }

        if (unknownEndingStatement != null) {
            children.add(unknownEndingStatement);
        }
    }

    /**
     * Creates a map of the position of the cursor within each {@link Expression} of the parsed tree.
     *
     * @param actualQuery The actual query is a string representation of the query that may contain
     * extra whitespace
     * @param position The position of the cursor in the actual query, which is used to retrieve the
     * deepest {@link Expression}. The position will be adjusted to fit into the beautified version
     * of the query
     * @return A new {@link QueryPosition}
     */
    public QueryPosition buildPosition(String actualQuery, int position) {

        // Adjust the position by not counting extra whitespace
        position = ExpressionTools.repositionCursor(actualQuery, position, toActualText());

        QueryPosition queryPosition = new QueryPosition(position);
        populatePosition(queryPosition, position);
        return queryPosition;
    }

    /**
     * Returns the deepest {@link Expression} for the given position.
     *
     * @param actualQuery The actual query is the text version of the query that may contain extra
     * whitespace and different formatting than the trim down version generated by the parsed tree
     * @param position The position in the actual query used to retrieve the {@link Expression}
     * @return The {@link Expression} located at the given position in the given query
     */
    public Expression getExpression(String actualQuery, int position) {
        QueryPosition queryPosition = buildPosition(actualQuery, position);
        return queryPosition.getExpression();
    }

    @Override
    public JPQLGrammar getGrammar() {
        return jpqlGrammar;
    }

    @Override
    public JPAVersion getJPAVersion() {
        return jpqlGrammar.getJPAVersion();
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(queryBNFId);
    }

    public String getValidationLevel() {
        return validationLevel;
    }

    public String getFoundAlias() {
        return foundAlias;
    }

    public void setFoundAlias(String foundAlias) {
        this.foundAlias = foundAlias;
    }

    /**
     * Returns the {@link Expression} representing the query, which is either a <b>SELECT</b>, a
     * <b>DELETE</b> or an <b>UPDATE</b> clause.
     *
     * @return The expression representing the Java Persistence query
     */
    public Expression getQueryStatement() {
        if (queryStatement == null) {
            queryStatement = buildNullExpression();
        }
        return queryStatement;
    }

    /**
     * Returns the {@link Expression} that may contain a portion of the query that could not be
     * parsed, this happens when the query is either incomplete or malformed.
     *
     * @return The expression used when the ending of the query is unknown or malformed
     */
    public Expression getUnknownEndingStatement() {
        if (unknownEndingStatement == null) {
            unknownEndingStatement = buildNullExpression();
        }
        return unknownEndingStatement;
    }

    /**
     * Determines whether a query was parsed. The query may be incomplete but it started with one of
     * the three clauses (<b>SELECT</b>, <b>DELETE FROM</b>, or <b>UPDATE</b>).
     *
     * @return <code>true</code> the query was parsed; <code>false</code> otherwise
     */
    public boolean hasQueryStatement() {
        return queryStatement != null &&
              !queryStatement.isNull();
    }

    /**
     * Determines whether the query that got parsed had some malformed or unknown information.
     *
     * @return <code>true</code> if the query could not be parsed correctly
     * because it is either incomplete or malformed
     */
    public boolean hasUnknownEndingStatement() {
        return unknownEndingStatement != null &&
              !unknownEndingStatement.isNull();
    }

    @Override
    protected boolean isTolerant() {
        return tolerant;
    }

    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        // Skip leading whitespace
        wordParser.skipLeadingWhitespace();

        // Parse the query, which can be invalid/incomplete or complete and valid
        // Make sure to use this statement if it's a JPQL fragment as well
        if (tolerant || (queryBNFId != JPQLStatementBNF.ID)) {

            // If the query BNF is not the "root" BNF, then we need to parse
            // it with a broader check when parsing
            if (queryBNFId == JPQLStatementBNF.ID) {
                queryStatement = parseUsingExpressionFactory(wordParser, queryBNFId, tolerant);
            }
            else {
                queryStatement = parse(wordParser, queryBNFId, tolerant);
            }

            int count = wordParser.skipLeadingWhitespace();

            // The JPQL query is invalid or incomplete, the remaining will be added
            // to the unknown ending statement
            if ((queryStatement == null) || !wordParser.isTail()) {
                wordParser.moveBackward(count);
                unknownEndingStatement = buildUnknownExpression(wordParser.substring());
            }
            // The JPQL query has some ending whitespace, keep one (used by content assist)
            else if (!wordParser.isTail() || (tolerant && (count > 0))) {
                unknownEndingStatement = buildUnknownExpression(" ");
            }
            // The JPQL query or fragment is invalid
            else if (queryStatement.isUnknown()) {
                unknownEndingStatement = buildUnknownExpression(queryStatement.toParsedText());
                queryStatement = null;
            }
        }
        // Quickly parse the valid query
        else {

            switch (wordParser.character()) {
                case 'd': case 'D': queryStatement = new DeleteStatement(this); break;
                case 'u': case 'U': queryStatement = new UpdateStatement(this); break;
                case 's': case 'S': queryStatement = new SelectStatement(this); break;
            }

            if (queryStatement != null) {
                queryStatement.parse(wordParser, tolerant);
            }
            else {
                queryStatement = parse(wordParser, queryBNFId, tolerant);
            }
        }
    }

    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        if (queryStatement != null) {
            queryStatement.toParsedText(writer, actual);
        }

        if (unknownEndingStatement != null) {
            unknownEndingStatement.toParsedText(writer, actual);
        }
    }



    /**
     * Check for missing Entity alias in JPQL SELECT query.
     *
     * @param jpqlExpression Parsed JPQL query in form of {@link Expression}
     * @return boolean true for JPQL like <code>SELECT e FROM Employee</code>.
     *  <p>Returns false for <code>SELECT e FROM Employee e</code>
     */
    private boolean isMissingSelectFromAlias(JPQLExpression jpqlExpression) {
        return jpqlExpression.getQueryStatement() instanceof SelectStatement selectStatement &&
                selectStatement.getFromClause() instanceof FromClause fromClause &&
                fromClause.getDeclaration() instanceof IdentificationVariableDeclaration identificationVariableDeclaration &&
                identificationVariableDeclaration.getRangeVariableDeclaration() instanceof RangeVariableDeclaration rangeVariableDeclaration &&
                !rangeVariableDeclaration.hasIdentificationVariable();
    }

    /**
     * Evaluate Entity alias from provided {@link Expression}. It returns first available alias.
     *
     * @param jpqlExpression Start {@link Expression} which will searched. Whole parsed JPQL. E.g. <code>SELECT OBJECT (e) FROM Employee e</code>.
     * @return Found Entity alias.
     */
    private String getMissingAliasFromSelectPart(JPQLExpression jpqlExpression) {
        SelectStatement selectStatement = (SelectStatement) jpqlExpression.getQueryStatement();
        SelectClause selectClause = (SelectClause) selectStatement.getSelectClause();
        if (selectClause.getSelectExpression() instanceof IdentificationVariable identificationVariable) {
            return identificationVariable.getText();
        }
        return findMissingAliasFromSelectPart(selectClause.getSelectExpression());
    }

    /**
     * Evaluate Entity alias from provided {@link Expression}. It returns first available alias. Searches recursively nested expressions/functions.
     *
     * @param parentExpression Start {@link Expression} which will searched. E.g. <code>SELECT OBJECT (e)</code> {@link SelectClause} part from e.g. <code>SELECT OBJECT (e) FROM Employee e</code>.
     * @return Found Entity alias.
     */
    private String findMissingAliasFromSelectPart(Expression parentExpression) {
        ListIterable<Expression> list = parentExpression.children();
        for (Expression expression : list) {
            if (expression instanceof IdentificationVariable identificationVariable) {
                return ((IdentificationVariable) expression).getText();
            } else {
                return findMissingAliasFromSelectPart(expression);
            }
        }
        return null;
    }

    /**
     * Add missing Entity alias into provided {@link Expression}. It's designed for a SELECT expressions.
     *
     * @param jpqlExpression Parsed JPQL query in form of {@link Expression} which will be updated.
     * @param aliasName Entity alias.
     */
    private void fixMissingAlias(JPQLExpression jpqlExpression, String aliasName) {
        SelectStatement selectStatement = (SelectStatement) jpqlExpression.getQueryStatement();
        FromClause fromClause = (FromClause) selectStatement.getFromClause();
        IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) fromClause.getDeclaration();
        RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) identificationVariableDeclaration.getRangeVariableDeclaration();
        rangeVariableDeclaration.setVirtualIdentificationVariable(aliasName);
    }
}
