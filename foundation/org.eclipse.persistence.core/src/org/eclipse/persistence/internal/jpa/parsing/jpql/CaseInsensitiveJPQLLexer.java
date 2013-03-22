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

import org.eclipse.persistence.internal.libraries.antlr.runtime.*;
import org.eclipse.persistence.internal.jpa.parsing.jpql.antlr.JPQLLexer;

/*
 * This Lexer matches in a case insensitive manner.
 * This is required because starting in ANTLR v3, case insensitivity is not provided
 * as an option in ANTLR and JPQL requires case insensitivity
 * @author tware
 */
public class CaseInsensitiveJPQLLexer extends JPQLLexer {
		
	public void match(String s) throws MismatchedTokenException {
        int i = 0;
        while ( i<s.length() ) {
        	int currentChar = Character.toLowerCase(input.LA(1));
        	int stringChar = Character.toLowerCase(s.charAt(i));
            if ( currentChar != stringChar ) {
				if ( state.backtracking>0 ) {
					state.failed = true;
					return;
				}
				MismatchedTokenException mte =
					new MismatchedTokenException(s.charAt(i), input);
				recover(mte);
				throw mte;
            }
            i++;
            input.consume();
            state.failed = false;
        }
    }

    public void match(int c) throws MismatchedTokenException {
    	int currentChar = Character.toLowerCase(input.LA(1));
    	int stringChar = Character.toLowerCase(c);
        if ( currentChar != stringChar ) {
			if ( state.backtracking>0 ) {
			    state.failed = true;
				return;
			}
			MismatchedTokenException mte =
				new MismatchedTokenException(c, input);
			recover(mte);
			throw mte;
        }
        input.consume();
        state.failed = false;
    }

    public void matchRange(int a, int b)
		throws MismatchedRangeException
	{
    	int currentChar = Character.toLowerCase(input.LA(1));
    	int aChar = Character.toLowerCase(a);
    	int bChar = Character.toLowerCase(b);
        if ( currentChar<aChar || currentChar>bChar ) {
			if ( state.backtracking>0 ) {
				state.failed = true;
				return;
			}
            MismatchedRangeException mre =
				new MismatchedRangeException(a,b,input);
			recover(mre);
			throw mre;
        }
        input.consume();
		state.failed = false;
    }
}
