package org.eclipse.persistence.internal.jpa.parsing.jpql;

import org.antlr.runtime.ANTLRStringStream;

/**
 * This Stream is used when tokenizing JPQL queries
 * It overrides the look ahead operator to return the lower case version of the string
 * This is required because starting in ANTLR v3, case insensitivity is not provided
 * as an option in ANTLR and JPQL requires case insensitivity
 * @author tware
 *
 */
public class CaseInsensitiveANTLRStringStream extends ANTLRStringStream {

	/** Copy data in string to a local char array */
	public CaseInsensitiveANTLRStringStream(String input) {
		super(input);
	}

	/** This is the preferred constructor as no data is copied */
	public CaseInsensitiveANTLRStringStream(char[] data, int numberOfActualCharsInArray) {
		super(data, numberOfActualCharsInArray);
	}
	
    public int LA(int i) {
    	int la = super.LA(i);
    	return Character.toLowerCase(la);
    }
	
}
