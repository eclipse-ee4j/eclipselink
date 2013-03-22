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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing.jpql;

import org.eclipse.persistence.internal.jpa.parsing.JPQLParseTree;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * Factory class used to isolate Antlr dependencies.
 */
public class JPQLParserFactory {
    public JPQLParserFactory() {
    }

    public JPQLParser buildParserFor(String ejbqlString) {
        return JPQLParser.buildParserFor(ejbqlString);
    }

    public JPQLParser parseEJBQLString(String ejbqlString) {
        JPQLParser parser = buildParserFor(ejbqlString);
        parser.parse();
        return parser;
    }

    /**
     * Populate the query using the information retrieved from parsing the JPQL.
     */
    public void populateQuery(String jpqlString, ObjectLevelReadQuery query, AbstractSession session) {     
        // PERF: Check if the JPQL has already been parsed.
        // Only allow queries with default properties to be parse cached.
        boolean isCacheable = query.isDefaultPropertiesQuery();
        DatabaseQuery cachedQuery = null;
        if (isCacheable) {
            cachedQuery = (DatabaseQuery)session.getProject().getJPQLParseCache().get(jpqlString);
        }
        if ((cachedQuery == null)
                || (!cachedQuery.isPrepared())
                || (cachedQuery.getClass() != query.getClass())) {
            JPQLParser parser = parseEJBQLString(jpqlString);
            JPQLParseTree parseTree = parser.getParseTree();
            parseTree.populateQuery(query, session);
            if (isCacheable) {
                session.getProject().getJPQLParseCache().put(jpqlString, query);
            }
        } else {
            query.prepareFromQuery(cachedQuery);
            query.setIsPrepared(true);
        }
    }
    
}
