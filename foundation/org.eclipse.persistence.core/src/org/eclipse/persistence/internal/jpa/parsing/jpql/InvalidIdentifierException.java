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

import org.eclipse.persistence.internal.libraries.antlr.runtime.RecognitionException;
import org.eclipse.persistence.internal.libraries.antlr.runtime.Token;

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
