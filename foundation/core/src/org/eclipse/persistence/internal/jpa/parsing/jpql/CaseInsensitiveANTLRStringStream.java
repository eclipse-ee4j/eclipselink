package org.eclipse.persistence.internal.jpa.parsing.jpql;

import org.antlr.runtime.ANTLRStringStream;

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
