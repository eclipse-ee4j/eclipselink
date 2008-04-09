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
package org.eclipse.persistence.internal.libraries.antlr.runtime.debug;

import org.eclipse.persistence.internal.libraries.antlr.runtime.Token;
import org.eclipse.persistence.internal.libraries.antlr.runtime.tree.TreeAdaptor;

/** A TreeAdaptor proxy that fires debugging events to a DebugEventListener
 *  delegate and uses the TreeAdaptor delegate to do the actual work.  All
 *  AST events are triggered by this adaptor; no code gen changes are needed
 *  in generated rules.  Debugging events are triggered *after* invoking
 *  tree adaptor routines.
 *
 *  Trees created with actions in rewrite actions like "-> ^(ADD {foo} {bar})"
 *  cannot be tracked as they might not use the adaptor to create foo, bar.
 *  The debug listener has to deal with tree node IDs for which it did
 *  not see a createNode event.  A single <unknown> node is sufficient even
 *  if it represents a whole tree.
 */
public class DebugTreeAdaptor implements TreeAdaptor {
	protected DebugEventListener dbg;
	protected TreeAdaptor adaptor;

	public DebugTreeAdaptor(DebugEventListener dbg, TreeAdaptor adaptor) {
		this.dbg = dbg;
		this.adaptor = adaptor;
	}

	public Object create(Token payload) {
		Object n = adaptor.create(payload);
		dbg.createNode(adaptor.getUniqueID(n), payload.getTokenIndex());
		return n;
	}

	public Object dupTree(Object tree) {
		// TODO: do these need to be sent to dbg?
		return adaptor.dupTree(tree);
	}

	public Object dupNode(Object treeNode) {
		// TODO: do these need to be sent to dbg?
		return adaptor.dupNode(treeNode);
	}

	public Object nil() {
		Object n = adaptor.nil();
		dbg.nilNode(adaptor.getUniqueID(n));
		return n;
	}

	public boolean isNil(Object tree) {
		return adaptor.isNil(tree);
	}

	public void addChild(Object t, Object child) {
		adaptor.addChild(t,child);
		dbg.addChild(adaptor.getUniqueID(t), adaptor.getUniqueID(child));
	}

	public Object becomeRoot(Object newRoot, Object oldRoot) {
		Object n = adaptor.becomeRoot(newRoot, oldRoot);
		dbg.becomeRoot(adaptor.getUniqueID(n), adaptor.getUniqueID(oldRoot));
		return n;
	}

	public Object rulePostProcessing(Object root) {
		return adaptor.rulePostProcessing(root);
	}

	public void addChild(Object t, Token child) {
		Object n = this.create(child);
		this.addChild(t, n);
	}

	public Object becomeRoot(Token newRoot, Object oldRoot) {
		Object n = this.create(newRoot);
		adaptor.becomeRoot(n, oldRoot);
		dbg.becomeRoot(adaptor.getUniqueID(n), adaptor.getUniqueID(oldRoot));
		return n;
	}

	public Object create(int tokenType, Token fromToken) {
		Object n = adaptor.create(tokenType, fromToken);
		dbg.createNode(adaptor.getUniqueID(n), fromToken.getText(), tokenType);
		return n;
	}

	public Object create(int tokenType, Token fromToken, String text) {
		Object n = adaptor.create(tokenType, fromToken, text);
		dbg.createNode(adaptor.getUniqueID(n), text, tokenType);
		return n;
	}

	public Object create(int tokenType, String text) {
		Object n = adaptor.create(tokenType, text);
		dbg.createNode(adaptor.getUniqueID(n), text, tokenType);
		return n;
	}

	public int getType(Object t) {
		return adaptor.getType(t);
	}

	public void setType(Object t, int type) {
		adaptor.setType(t, type);
	}

	public String getText(Object t) {
		return adaptor.getText(t);
	}

	public void setText(Object t, String text) {
		adaptor.setText(t, text);
	}

	public Token getToken(Object t) {
		return adaptor.getToken(t);
	}

	public void setTokenBoundaries(Object t, Token startToken, Token stopToken) {
		adaptor.setTokenBoundaries(t, startToken, stopToken);
		if ( t!=null && startToken!=null && stopToken!=null ) {
			dbg.setTokenBoundaries(adaptor.getUniqueID(t),
								   startToken.getTokenIndex(),
								   stopToken.getTokenIndex());
		}
	}

	public int getTokenStartIndex(Object t) {
		return adaptor.getTokenStartIndex(t);
	}

	public int getTokenStopIndex(Object t) {
		return adaptor.getTokenStopIndex(t);
	}

	public Object getChild(Object t, int i) {
		return adaptor.getChild(t, i);
	}

	public int getChildCount(Object t) {
		return adaptor.getChildCount(t);
	}

	public int getUniqueID(Object node) {
		return adaptor.getUniqueID(node);
	}

	
	// support

	public DebugEventListener getDebugEventListener() {
		return dbg;
	}

	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}

}
