/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.queries;

import org.eclipse.persistence.internal.jpa.parsing.JPQLParseTree;
import org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParser;
import org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParserFactory;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;


/**
 * This class defines the default {@link JPAQueryBuilder} implementation 
 * that uses an Antlr-based parser and {@link DatabaseQuery} conversion 
 * mechanism.
 * 
 * @see JPQLParser
 * @see JPQLParser
 * @see JPQLParserFactory
 * @see JPAQueryBuilder
 * @see JPAQueryBuilderManager
 *
 * @version 2.2
 * @since 2.2
 * @author John Bracken
 */
public final class ANTLRQueryBuilder implements JPAQueryBuilder {
   
    /**
     * Constructs a new instance of 
     * {@link ANTLRQueryBuilder}.
     */
    public ANTLRQueryBuilder() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseQuery buildQuery(String jpqlQuery, AbstractSession session) {
        // TODO - must set class loader
        
        JPQLParseTree parseTree = buildParseTree(jpqlQuery, session);
        DatabaseQuery databaseQuery = parseTree.createDatabaseQuery();
        databaseQuery.setJPQLString(jpqlQuery);

        populateQueryInternal(jpqlQuery, session, parseTree, databaseQuery);
        
        return databaseQuery;
    }

    /**
     * {@inheritDoc}
     */
    public void populateQuery(String jpqlQuery, DatabaseQuery query, AbstractSession session) {
        new JPQLParserFactory().populateQuery(jpqlQuery, (ObjectLevelReadQuery)query, session);
    }

    
    /**
     * Builds a {@link JPQLParseTree} based on the given query and associated
     * {@link AbstractSession}.
     * 
     * @param jpqlQuery The jpql query.
     * @param session The associated session.
     * @return The {@link JPQLParseTree}.
     */
    private JPQLParseTree buildParseTree(String jpqlQuery, AbstractSession session) {
        JPQLParseTree parseTree = JPQLParser.buildParseTree(jpqlQuery);
        parseTree.setClassLoader(session.getDatasourcePlatform().getConversionManager().getLoader());
        return parseTree;
    }

    /**
     * Populates the given query based on the provided parse tree, jpql, and session.
     * 
     * @param jpqlQuery the jpql query.
     * @param session The associated {@link AbstractSession}.
     * @param parseTree The parse tree for the given query.
     * @param databaseQuery The database query to be updated.
     */
    private void populateQueryInternal(String jpqlQuery, AbstractSession session, JPQLParseTree parseTree, DatabaseQuery databaseQuery) {
        parseTree.populateQuery(databaseQuery, session);
        // TODO - must set class loader.

        // Bug#4646580 Add arguments to query.
        parseTree.addParametersToQuery(databaseQuery);
    }
}
