package org.eclipse.persistence.internal.libraries.antlr.runtime.tree;

import org.eclipse.persistence.internal.libraries.antlr.runtime.Token;

public abstract class BaseTreeAdaptor implements TreeAdaptor {
	public Object nil() {
		return create(null);
	}

	public boolean isNil(Object tree) {
		return ((Tree)tree).isNil();
	}

	public Object dupTree(Object tree) {
		return ((Tree)tree).dupTree();
	}

	/** Add a child to the tree t.  If child is a flat tree (a list), make all
	 *  in list children of t.  Warning: if t has no children, but child does
	 *  and child isNil then you can decide it is ok to move children to t via
	 *  t.children = child.children; i.e., without copying the array.  Just
	 *  make sure that this is consistent with have the user will build
	 *  ASTs.
	 */
	public void addChild(Object t, Object child) {
		if ( t!=null ) {
			((Tree)t).addChild((Tree)child);
		}
	}

	/** If oldRoot is a nil root, just copy or move the children to newRoot.
	 *  If not a nil root, make oldRoot a child of newRoot.
	 *
	 *    old=^(nil a b c), new=r yields ^(r a b c)
	 *    old=^(a b c), new=r yields ^(r ^(a b c))
	 *
	 *  If newRoot is a nil-rooted single child tree, use the single
	 *  child as the new root node.
	 *
	 *    old=^(nil a b c), new=^(nil r) yields ^(r a b c)
	 *    old=^(a b c), new=^(nil r) yields ^(r ^(a b c))
	 *
	 *  If oldRoot was null, it's ok, just return newRoot (even if isNil).
	 *
	 *    old=null, new=r yields r
	 *    old=null, new=^(nil r) yields ^(nil r)
	 *
	 *  Return newRoot.  Throw an exception if newRoot is not a
	 *  simple node or nil root with a single child node--it must be a root
	 *  node.  If newRoot is ^(nil x) return x as newRoot.
	 *
	 *  Be advised that it's ok for newRoot to point at oldRoot's
	 *  children; i.e., you don't have to copy the list.  We are
	 *  constructing these nodes so we should have this control for
	 *  efficiency.
	 */
	public Object becomeRoot(Object newRoot, Object oldRoot) {
		Tree newRootTree = (Tree)newRoot;
		Tree oldRootTree = (Tree)oldRoot;
		if ( oldRoot==null ) {
			return newRoot;
		}
		// handle ^(nil real-node)
		if ( newRootTree.isNil() ) {
			if ( newRootTree.getChildCount()>1 ) {
				// TODO: make tree run time exceptions hierarchy
				throw new RuntimeException("more than one node as root (TODO: make exception hierarchy)");
			}
			newRootTree = (Tree)newRootTree.getChild(0);
		}
		// add oldRoot to newRoot; addChild takes care of case where oldRoot
		// is a flat list (i.e., nil-rooted tree).  All children of oldRoot
		// are added to newRoot.
		newRootTree.addChild(oldRootTree);
		return newRootTree;
	}

	/** Transform ^(nil x) to x */
	public Object rulePostProcessing(Object root) {
		Tree r = (Tree)root;
		if ( r!=null && r.isNil() && r.getChildCount()==1 ) {
			r = (Tree)r.getChild(0);
		}
		return r;
	}

	public Object becomeRoot(Token newRoot, Object oldRoot) {
		return becomeRoot(create(newRoot), oldRoot);
	}

	public Object create(int tokenType, Token fromToken) {
		fromToken = createToken(fromToken);
		//((ClassicToken)fromToken).setType(tokenType);
		fromToken.setType(tokenType);
		Tree t = (Tree)create(fromToken);
		return t;
	}

	public Object create(int tokenType, Token fromToken, String text) {
		fromToken = createToken(fromToken);
		fromToken.setType(tokenType);
		fromToken.setText(text);
		Tree t = (Tree)create(fromToken);
		return t;
	}

	public Object create(int tokenType, String text) {
		Token fromToken = createToken(tokenType, text);
		Tree t = (Tree)create(fromToken);
		return t;
	}

	public int getType(Object t) {
		((Tree)t).getType();
		return 0;
	}

	public void setType(Object t, int type) {
		throw new NoSuchMethodError("don't know enough about Tree node");
	}

	public String getText(Object t) {
		return ((Tree)t).getText();
	}

	public void setText(Object t, String text) {
		throw new NoSuchMethodError("don't know enough about Tree node");
	}

	public Object getChild(Object t, int i) {
		return ((Tree)t).getChild(i);
	}

	public int getChildCount(Object t) {
		return ((Tree)t).getChildCount();
	}

	public int getUniqueID(Object node) {
		return System.identityHashCode(node);
	}

	/** Tell me how to create a token for use with imaginary token nodes.
	 *  For example, there is probably no input symbol associated with imaginary
	 *  token DECL, but you need to create it as a payload or whatever for
	 *  the DECL node as in ^(DECL type ID).
	 *
	 *  If you care what the token payload objects' type is, you should
	 *  override this method and any other createToken variant.
	 */
	public abstract Token createToken(int tokenType, String text);

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
	public abstract Token createToken(Token fromToken);
}

