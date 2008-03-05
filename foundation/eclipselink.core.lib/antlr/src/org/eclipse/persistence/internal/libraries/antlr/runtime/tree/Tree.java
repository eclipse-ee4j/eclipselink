/*
 [The "BSD licence"]
 Copyright (c) 2005-2006 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.eclipse.persistence.internal.libraries.antlr.runtime.tree;

import org.eclipse.persistence.internal.libraries.antlr.runtime.Token;

/** What does a tree look like?  ANTLR has a number of support classes
 *  such as CommonTreeNodeStream that work on these kinds of trees.  You
 *  don't have to make your trees implement this interface, but if you do,
 *  you'll be able to use more support code.
 *
 *  NOTE: When constructing trees, ANTLR can build any kind of tree; it can
 *  even use Token objects as trees if you add a child list to your tokens.
 *
 *  This is a tree node without any payload; just navigation and factory stuff.
 */
public interface Tree {
	public static final Tree INVALID_NODE = new CommonTree(Token.INVALID_TOKEN);

	Tree getChild(int i);

	int getChildCount();

	/** Add t as a child to this node.  If t is null, do nothing.  If t
	 *  is nil, add all children of t to this' children.
	 * @param t
	 */
	void addChild(Tree t);

	/** Indicates the node is a nil node but may still have children, meaning
	 *  the tree is a flat list.
	 */
	boolean isNil();

	/**  What is the smallest token index (indexing from 0) for this node
	 *   and its children?
	 */
	int getTokenStartIndex();

	void setTokenStartIndex(int index);

	/**  What is the largest token index (indexing from 0) for this node
	 *   and its children?
	 */
	int getTokenStopIndex();

	void setTokenStopIndex(int index);

	Tree dupTree();

	Tree dupNode();

	/** Return a token type; needed for tree parsing */
	int getType();

	String getText();

	/** In case we don't have a token payload, what is the line for errors? */
	int getLine();

	int getCharPositionInLine();

	String toStringTree();

	String toString();
}
