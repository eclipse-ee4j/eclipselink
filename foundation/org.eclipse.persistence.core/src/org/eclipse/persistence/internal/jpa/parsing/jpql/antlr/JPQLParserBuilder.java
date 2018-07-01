/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

import org.eclipse.persistence.internal.jpa.parsing.jpql.CaseInsensitiveANTLRStringStream;
import org.eclipse.persistence.internal.jpa.parsing.jpql.CaseInsensitiveJPQLLexer;
import org.eclipse.persistence.internal.libraries.antlr.runtime.CommonTokenStream;

/*****************************************************************
 * EJBQLParserBuilder constructs the parser for EJBQL.
 * This is specific to ANTLR version 3.5.2
 *****************************************************************
 */
public class JPQLParserBuilder {

    /**
     * INTERNAL
     * Build a parser for the passed ejbql string, for ANTLR version 3.5.2
     */
    public static JPQLParser buildParser(String queryText) {

        JPQLLexer lexer = new CaseInsensitiveJPQLLexer();
        lexer.setCharStream(new CaseInsensitiveANTLRStringStream(queryText));
        CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(lexer);
        JPQLParser parser = new JPQLParser(tokens);
        return parser;
    }

}
