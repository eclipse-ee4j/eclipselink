package org.eclipse.persistence.internal.jpa.parsing.jpql;

import org.antlr.runtime.RecognitionException;

/*
 * This is a custom Exception class that is thrown from ANTLR JPQL code when we 
 * validate JPQL identifiers. 
 * 
 * It indicates that the identifier does not start with a valid character
 **/
 
public class InvalidIdentifierStartException extends RecognitionException {

	public InvalidIdentifierStartException(int c, int line, int positionInLine){
		this.c = c;
		this.line = line;
		this.charPositionInLine = positionInLine;
	}
}
