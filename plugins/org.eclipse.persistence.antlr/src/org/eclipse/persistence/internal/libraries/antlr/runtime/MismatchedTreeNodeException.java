package org.eclipse.persistence.internal.libraries.antlr.runtime;

import org.eclipse.persistence.internal.libraries.antlr.runtime.tree.TreeNodeStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.tree.Tree;

/**
 */
public class MismatchedTreeNodeException extends RecognitionException {
	public int expecting;

	public MismatchedTreeNodeException() {
	}

	public MismatchedTreeNodeException(int expecting, TreeNodeStream input) {
		super(input);
		Tree t = (Tree)input.LT(1);
		if ( input.LT(1) instanceof Tree ) {
			line = t.getLine();
			charPositionInLine = t.getCharPositionInLine();
			// TODO: if DOWN/UP, there is no line info currently
		}
		this.expecting = expecting;
	}

	public String toString() {
		return "MismatchedTreeNodeException("+getUnexpectedType()+"!="+expecting+")";
	}
}
