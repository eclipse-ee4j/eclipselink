package org.eclipse.persistence.internal.libraries.antlr.runtime.tree;

import org.eclipse.persistence.internal.libraries.antlr.runtime.CommonToken;
import org.eclipse.persistence.internal.libraries.antlr.runtime.Token;

/** A TreeAdaptor that works with any Tree implementation.  It provides
 *  really just factory methods; all the work is done by BaseTreeAdaptor.
 *  If you would like to have different tokens created than ClassicToken
 *  objects, you need to override this and then set the parser tree adaptor to
 *  use your subclass.
 *
 *  To get your parser to build nodes of a different type, override
 *  create(Token).
 */
public class CommonTreeAdaptor extends BaseTreeAdaptor {
	/** Duplicate a node.  This is part of the factory;
	 *	override if you want another kind of node to be built.
	 *
	 *  I could use reflection to prevent having to override this
	 *  but reflection is slow.
	 */
	public Object dupNode(Object treeNode) {
		return ((Tree)treeNode).dupNode();
	}

	public Object create(Token payload) {
		return new CommonTree(payload);
	}

	/** Tell me how to create a token for use with imaginary token nodes.
	 *  For example, there is probably no input symbol associated with imaginary
	 *  token DECL, but you need to create it as a payload or whatever for
	 *  the DECL node as in ^(DECL type ID).
	 *
	 *  If you care what the token payload objects' type is, you should
	 *  override this method and any other createToken variant.
	 */
	public Token createToken(int tokenType, String text) {
		return new CommonToken(tokenType, text);
	}

	/** Tell me how to create a token for use with imaginary token nodes.
	 *  For example, there is probably no input symbol associated with imaginary
	 *  token DECL, but you need to create it as a payload or whatever for
	 *  the DECL node as in ^(DECL type ID).
	 *
	 *  This is a variant of createToken where the new token is derived from
	 *  an actual real input token.  Typically this is for converting '{'
	 *  tokens to BLOCK etc...  You'll see
	 *
	 *    r : lc='{' ID+ '}' -> ^(BLOCK[$lc] ID+) ;
	 *
	 *  If you care what the token payload objects' type is, you should
	 *  override this method and any other createToken variant.
	 */
	public Token createToken(Token fromToken) {
		return new CommonToken(fromToken);
	}

	/** Track start/stop token for subtree root created for a rule.
	 *  Only works with Tree nodes.  For rules that match nothing,
	 *  seems like this will yield start=i and stop=i-1 in a nil node.
	 *  Might be useful info so I'll not force to be i..i.
	 */
	public void setTokenBoundaries(Object t, Token startToken, Token stopToken) {
		if ( t==null ) {
			return;
		}
		int start = 0;
		int stop = 0;
		if ( startToken!=null ) {
			start = startToken.getTokenIndex();
		}
		if ( stopToken!=null ) {
			stop = stopToken.getTokenIndex();
		}
		((Tree)t).setTokenStartIndex(start);
		((Tree)t).setTokenStopIndex(stop);
	}

	public int getTokenStartIndex(Object t) {
		return ((Tree)t).getTokenStartIndex();
	}

	public int getTokenStopIndex(Object t) {
		return ((Tree)t).getTokenStopIndex();
	}

	public String getText(Object t) {
		return ((Tree)t).getText();
	}

    public int getType(Object t) {
		if ( t==null ) {
			return Token.INVALID_TOKEN_TYPE;
		}
		return ((Tree)t).getType();
	}

	/** What is the Token associated with this node?  If
	 *  you are not using CommonTree, then you must
	 *  override this in your own adaptor.
	 */
	public Token getToken(Object t) {
		if ( t instanceof CommonTree ) {
			return ((CommonTree)t).getToken();
		}
		return null; // no idea what to do
	}

	public Object getChild(Object t, int i) {
        return ((Tree)t).getChild(i);
    }

    public int getChildCount(Object t) {
        return ((Tree)t).getChildCount();
    }

}
