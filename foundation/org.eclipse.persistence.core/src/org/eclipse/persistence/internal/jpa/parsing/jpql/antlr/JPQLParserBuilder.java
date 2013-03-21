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
package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

import org.eclipse.persistence.internal.libraries.antlr.runtime.*;
//import org.eclipse.persistence.internal.libraries.antlr.TokenBuffer;

//eclipselink imports
import org.eclipse.persistence.internal.jpa.parsing.jpql.CaseInsensitiveJPQLLexer;
import org.eclipse.persistence.internal.jpa.parsing.jpql.CaseInsensitiveANTLRStringStream;
import org.eclipse.persistence.internal.jpa.parsing.jpql.antlr.JPQLParser;

/*****************************************************************
 * EJBQLParserBuilder constructs the parser for EJBQL.
 * This is specific to ANTLR version 2.7.3
 *****************************************************************
 */
public class JPQLParserBuilder {

    /**
     * INTERNAL
     * Build a parser for the passed ejbql string, for ANTLR version 2.7.3
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
