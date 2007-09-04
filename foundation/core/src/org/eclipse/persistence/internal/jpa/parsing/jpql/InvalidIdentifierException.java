package org.eclipse.persistence.internal.jpa.parsing.jpql;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

/**
 * This is a custom Exception class that is thrown from ANTLR JPQL code when we 
 * validate JPQL identifiers.
 * 
 * It indicates that the identifier is not valid in JPQL.
 * 
 * @author tware
 *
 */
public class InvalidIdentifierException extends RecognitionException {

	public InvalidIdentifierException(Token token){
		super();
		this.token = token;
	}
	
	public Token getToken(){
		return token;
	}
	
}
