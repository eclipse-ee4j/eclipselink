/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.queries;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.jpa.parsing.JPQLParseTree;
import org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParser;
import org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParserFactory;
import org.eclipse.persistence.internal.queries.JPQLCallQueryMechanism;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * This class defines the default {@link JPAQueryBuilder} implementation that
 * uses an Antlr-based parser and {@link DatabaseQuery} conversion mechanism.
 * 
 * @see JPQLParser
 * @see JPQLParser
 * @see JPQLParserFactory
 * @see JPAQueryBuilder
 * 
 * @version 2.4
 * @since 2.2
 * @author John Bracken
 * @deprecated replaced with Hermes parser, which is the default.
 */
@Deprecated
public final class ANTLRQueryBuilder implements JPAQueryBuilder {

    /**
     * Creates a new <code>ANTLRQueryBuilder</code>.
     */
    public ANTLRQueryBuilder() {
        super();
    }

    /**
     * Allow the parser validation level to be set.
     * 
     * @param level
     *            The validation levels are defined in ParserValidationType
     */
    public void setValidationLevel(String level) {
        // Not supported.
    }
    
    /**
     * Builds a {@link JPQLParseTree} based on the given query and associated
     * {@link AbstractSession}.
     * 
     * @param jpqlQuery
     *            The JPQL query
     * @param session
     *            The associated session
     * @return The {@link JPQLParseTree}
     */
    private JPQLParseTree buildParseTree(CharSequence jpqlQuery, AbstractSession session) {
        JPQLParseTree parseTree = JPQLParser.buildParseTree(jpqlQuery.toString());
        parseTree.setClassLoader(session.getDatasourcePlatform().getConversionManager().getLoader());
        return parseTree;
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseQuery buildQuery(CharSequence jpqlQuery, AbstractSession session) {
        // TODO - must set class loader

        JPQLParseTree parseTree = buildParseTree(jpqlQuery, session);
        DatabaseQuery databaseQuery = parseTree.createDatabaseQuery();
        databaseQuery.setJPQLString(jpqlQuery.toString());
        ((JPQLCallQueryMechanism)databaseQuery.getQueryMechanism()).getJPQLCall().setIsParsed(true);

        populateQueryInternal(session, parseTree, databaseQuery);

        return databaseQuery;
    }

    /**
     * {@inheritDoc}
     */
    public Expression buildSelectionCriteria(String entityName, String additionalCriteria, AbstractSession session) {

        StringBuilder jpqlQuery = new StringBuilder();
        jpqlQuery.append("select this from ");
        jpqlQuery.append(entityName);
        jpqlQuery.append(" this where ");
        jpqlQuery.append(additionalCriteria.trim());
        return buildQuery(jpqlQuery, session).getSelectionCriteria();
    }

    /**
     * {@inheritDoc}
     */
    public void populateQuery(CharSequence jpqlQuery, DatabaseQuery query, AbstractSession session) {
        new JPQLParserFactory().populateQuery(jpqlQuery.toString(), (ObjectLevelReadQuery) query, session);
    }

    /**
     * Populates the given query based on the provided parse tree and session.
     * 
     * @param session
     *            The associated {@link AbstractSession}
     * @param parseTree
     *            The parse tree for the given query
     * @param databaseQuery
     *            The database query to be updated
     */
    private void populateQueryInternal(AbstractSession session, JPQLParseTree parseTree, DatabaseQuery databaseQuery) {

        // TODO - must set class loader.
        parseTree.populateQuery(databaseQuery, session);

        // Bug#4646580 Add arguments to query.
        parseTree.addParametersToQuery(databaseQuery);
    }
}