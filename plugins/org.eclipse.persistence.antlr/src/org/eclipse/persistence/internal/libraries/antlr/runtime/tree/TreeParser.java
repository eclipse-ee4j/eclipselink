package org.eclipse.persistence.internal.libraries.antlr.runtime.tree;

import org.eclipse.persistence.internal.libraries.antlr.runtime.*;

/** A parser for a stream of tree nodes.  "tree grammars" result in a subclass
 *  of this.  All the error reporting and recovery is shared with Parser via
 *  the BaseRecognizer superclass.
*/
public class TreeParser extends BaseRecognizer {
	public static final int DOWN = Token.DOWN;
	public static final int UP = Token.UP;

	protected TreeNodeStream input;

	public TreeParser(TreeNodeStream input) {
		setTreeNodeStream(input);
	}

	public void reset() {
		super.reset(); // reset all recognizer state variables
		if ( input!=null ) {
			input.seek(0); // rewind the input
		}
	}

	/** Set the input stream */
	public void setTreeNodeStream(TreeNodeStream input) {
		this.input = input;
	}

	public TreeNodeStream getTreeNodeStream() {
		return input;
	}

	/** Match '.' in tree parser has special meaning.  Skip node or
	 *  entire tree if node has children.  If children, scan until
	 *  corresponding UP node.
	 */
	public void matchAny(IntStream ignore) { // ignore stream, copy of this.input
		errorRecovery = false;
		failed = false;
		Object look = input.LT(1);
		if ( input.getTreeAdaptor().getChildCount(look)==0 ) {
			input.consume(); // not subtree, consume 1 node and return
			return;
		}
		// current node is a subtree, skip to corresponding UP.
		// must count nesting level to get right UP
		int level=0;
		int tokenType = input.getTreeAdaptor().getType(look);
		while ( tokenType!=Token.EOF && !(tokenType==UP && level==0) ) {
			input.consume();
			look = input.LT(1);
			tokenType = input.getTreeAdaptor().getType(look);
			if ( tokenType == DOWN ) {
				level++;
			}
			else if ( tokenType == UP ) {
				level--;
			}
		}
		input.consume(); // consume UP
	}

	/** We have DOWN/UP nodes in the stream that have no line info; override.
	 *  plus we want to alter the exception type.
	 */
	protected void mismatch(IntStream input, int ttype, BitSet follow)
		throws RecognitionException
	{
		MismatchedTreeNodeException mte =
			new MismatchedTreeNodeException(ttype, (TreeNodeStream)input);
		recoverFromMismatchedToken(input, mte, ttype, follow);
	}

	/** Prefix error message with the grammar name because message is
	 *  always intended for the programmer because the parser built
	 *  the input tree not the user.
	 */
	public String getErrorHeader(RecognitionException e) {
		return getGrammarFileName()+": node from line "+e.line+":"+e.charPositionInLine;
	}

	/** Tree parsers parse nodes they usually have a token object as
	 *  payload. Set the exception token and do the default behavior.
	 */
	public String getErrorMessage(RecognitionException e, String[] tokenNames) {
		if ( this instanceof TreeParser ) {
			TreeAdaptor adaptor = ((TreeNodeStream)e.input).getTreeAdaptor();
			e.token = adaptor.getToken(e.node);
			if ( e.token==null ) { // could be an UP/DOWN node
				e.token = new CommonToken(adaptor.getType(e.node),
										  adaptor.getText(e.node));
			}
		}
		return super.getErrorMessage(e, tokenNames);
	}

	public void traceIn(String ruleName, int ruleIndex)  {
		super.traceIn(ruleName, ruleIndex, input.LT(1));
	}

	public void traceOut(String ruleName, int ruleIndex)  {
		super.traceOut(ruleName, ruleIndex, input.LT(1));
	}

}
