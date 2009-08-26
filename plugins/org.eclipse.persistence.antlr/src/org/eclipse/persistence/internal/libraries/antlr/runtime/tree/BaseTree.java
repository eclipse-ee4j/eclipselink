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

import java.util.ArrayList;
import java.util.List;

/** A generic tree implementation with no payload.  You must subclass to
 *  actually have any user data.  ANTLR v3 uses a list of children approach
 *  instead of the child-sibling approach in v2.  A flat tree (a list) is
 *  an empty node whose children represent the list.  An empty, but
 *  non-null node is called "nil".
 */
public abstract class BaseTree implements Tree {
	protected List children;

	public BaseTree() {
	}

	/** Create a new node from an existing node does nothing for BaseTree
	 *  as there are no fields other than the children list, which cannot
	 *  be copied as the children are not considered part of this node. 
	 */
	public BaseTree(Tree node) {
	}

	public Tree getChild(int i) {
		if ( children==null || i>=children.size() ) {
			return null;
		}
		return (BaseTree)children.get(i);
	}

	public Tree getFirstChildWithType(int type) {
		for (int i = 0; children!=null && i < children.size(); i++) {
			Tree t = (Tree) children.get(i);
			if ( t.getType()==type ) {
				return t;
			}
		}	
		return null;
	}

	public int getChildCount() {
		if ( children==null ) {
			return 0;
		}
		return children.size();
	}

	/** Add t as child of this node.
	 *
	 *  Warning: if t has no children, but child does
	 *  and child isNil then this routine moves children to t via
	 *  t.children = child.children; i.e., without copying the array.
	 */
	public void addChild(Tree t) {
		//System.out.println("add "+t.toStringTree()+" as child to "+this.toStringTree());
		if ( t==null ) {
			return; // do nothing upon addChild(null)
		}
		BaseTree childTree = (BaseTree)t;
		if ( childTree.isNil() ) { // t is an empty node possibly with children
			if ( this.children!=null && this.children == childTree.children ) {
				throw new RuntimeException("attempt to add child list to itself");
			}
			// just add all of childTree's children to this
			if ( childTree.children!=null ) {
				if ( this.children!=null ) { // must copy, this has children already
					int n = childTree.children.size();
					for (int i = 0; i < n; i++) {
						this.children.add(childTree.children.get(i));
					}
				}
				else {
					// no children for this but t has children; just set pointer
					this.children = childTree.children;
				}
			}
		}
		else { // t is not empty and might have children
			if ( children==null ) {
				children = createChildrenList(); // create children list on demand
			}
			children.add(t);
		}
	}

	/** Add all elements of kids list as children of this node */
	public void addChildren(List kids) {
		for (int i = 0; i < kids.size(); i++) {
			Tree t = (Tree) kids.get(i);
			addChild(t);
		}
	}

	public void setChild(int i, BaseTree t) {
		if ( children==null ) {
			children = createChildrenList();
		}
		children.set(i, t);
	}

	public BaseTree deleteChild(int i) {
		if ( children==null ) {
			return null;
		}
		return (BaseTree)children.remove(i);
	}

	/** Override in a subclass to change the impl of children list */
	protected List createChildrenList() {
		return new ArrayList();
	}

	public boolean isNil() {
		return false;
	}

	/** Recursively walk this tree, dup'ing nodes until you have copy of
	 *  this tree.  This method should work for all subclasses as long
	 *  as they override dupNode().
	 */
	public Tree dupTree() {
		Tree newTree = this.dupNode();
		for (int i = 0; children!=null && i < children.size(); i++) {
			Tree t = (Tree) children.get(i);
			Tree newSubTree = t.dupTree();
			newTree.addChild(newSubTree);
		}
		return newTree;
	}

	/** Print out a whole tree not just a node */
    public String toStringTree() {
		if ( children==null || children.size()==0 ) {
			return this.toString();
		}
		StringBuffer buf = new StringBuffer();
		if ( !isNil() ) {
			buf.append("(");
			buf.append(this.toString());
			buf.append(' ');
		}
		for (int i = 0; children!=null && i < children.size(); i++) {
			BaseTree t = (BaseTree) children.get(i);
			if ( i>0 ) {
				buf.append(' ');
			}
			buf.append(t.toStringTree());
		}
		if ( !isNil() ) {
			buf.append(")");
		}
		return buf.toString();
	}

    public int getLine() {
		return 0;
	}

	public int getCharPositionInLine() {
		return 0;
	}

	/** Override to say how a node (not a tree) should look as text */
	public abstract String toString();
}
