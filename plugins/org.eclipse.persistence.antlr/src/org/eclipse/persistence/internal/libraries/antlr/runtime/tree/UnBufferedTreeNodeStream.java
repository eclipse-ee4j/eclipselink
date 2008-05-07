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
import org.eclipse.persistence.internal.libraries.antlr.runtime.TokenStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** A stream of tree nodes, accessing nodes from a tree of ANY kind.
 *  No new nodes should be created in tree during the walk.  A small buffer
 *  of tokens is kept to efficiently and easily handle LT(i) calls, though
 *  the lookahead mechanism is fairly complicated.
 *
 *  For tree rewriting during tree parsing, this must also be able
 *  to replace a set of children without "losing its place".
 *  That part is not yet implemented.  Will permit a rule to return
 *  a different tree and have it stitched into the output tree probably.
 *
 *  @see CommonTreeNodeStream
 */
public class UnBufferedTreeNodeStream implements TreeNodeStream {
	public static final int INITIAL_LOOKAHEAD_BUFFER_SIZE = 5;

	/** Reuse same DOWN, UP navigation nodes unless this is true */
	protected boolean uniqueNavigationNodes = false;

	/** Pull nodes from which tree? */
	protected Object root;

	/** IF this tree (root) was created from a token stream, track it. */
	protected TokenStream tokens;

	/** What tree adaptor was used to build these trees */
	TreeAdaptor adaptor;

	/** As we walk down the nodes, we must track parent nodes so we know
	 *  where to go after walking the last child of a node.  When visiting
	 *  a child, push current node and current index.
	 */
	protected Stack nodeStack = new Stack();

	/** Track which child index you are visiting for each node we push.
	 *  TODO: pretty inefficient...use int[] when you have time
	 */
	protected Stack indexStack = new Stack();

	/** Which node are we currently visiting? */
	protected Object currentNode;

	/** Which node did we visit last?  Used for LT(-1) calls. */
	protected Object previousNode;

	/** Which child are we currently visiting?  If -1 we have not visited
	 *  this node yet; next consume() request will set currentIndex to 0.
	 */
	protected int currentChildIndex;

	/** What node index did we just consume?  i=0..n-1 for n node trees.
	 *  IntStream.next is hence 1 + this value.  Size will be same.
	 */
	protected int absoluteNodeIndex;

	/** Buffer tree node stream for use with LT(i).  This list grows
	 *  to fit new lookahead depths, but consume() wraps like a circular
	 *  buffer.
	 */
	protected Object[] lookahead = new Object[INITIAL_LOOKAHEAD_BUFFER_SIZE];

	/** lookahead[head] is the first symbol of lookahead, LT(1). */
	protected int head;

	/** Add new lookahead at lookahead[tail].  tail wraps around at the
	 *  end of the lookahead buffer so tail could be less than head.
	  */
	protected int tail;

	/** When walking ahead with cyclic DFA or for syntactic predicates,
	  *  we need to record the state of the tree node stream.  This
	 *  class wraps up the current state of the UnBufferedTreeNodeStream.
	 *  Calling mark() will push another of these on the markers stack.
	 */
	protected class TreeWalkState {
		int currentChildIndex;
		int absoluteNodeIndex;
		Object currentNode;
		Object previousNode;
		/** Record state of the nodeStack */
		int nodeStackSize;
		/** Record state of the indexStack */
		int indexStackSize;
		Object[] lookahead;
	}

	/** Calls to mark() may be nested so we have to track a stack of
	 *  them.  The marker is an index into this stack.
	 *  This is a List<TreeWalkState>.  Indexed from 1..markDepth.
	 *  A null is kept @ index 0.  Create upon first call to mark().
	 */
	protected List markers;

	/** tracks how deep mark() calls are nested */
	protected int markDepth = 0;

	/** Track the last mark() call result value for use in rewind(). */
	protected int lastMarker;

	// navigation nodes

	protected Object down;
	protected Object up;
	protected Object eof;

	public UnBufferedTreeNodeStream(Object tree) {
		this(new CommonTreeAdaptor(), tree);
	}

	public UnBufferedTreeNodeStream(TreeAdaptor adaptor, Object tree) {
		this.root = tree;
		this.adaptor = adaptor;
		reset();
		down = adaptor.create(Token.DOWN, "DOWN");
		up = adaptor.create(Token.UP, "UP");
		eof = adaptor.create(Token.EOF, "EOF");
	}

	public void reset() {
		currentNode = root;
		previousNode = null;
		currentChildIndex = -1;
		absoluteNodeIndex = -1;
		head = tail = 0;
	}

	// Satisfy TreeNodeStream

	public Object get(int i) {
		throw new UnsupportedOperationException("stream is unbuffered");
	}

	/** Get tree node at current input pointer + i ahead where i=1 is next node.
	 *  i<0 indicates nodes in the past.  So -1 is previous node and -2 is
	 *  two nodes ago. LT(0) is undefined.  For i>=n, return null.
	 *  Return null for LT(0) and any index that results in an absolute address
	 *  that is negative.
	 *
	 *  This is analogus to the LT() method of the TokenStream, but this
	 *  returns a tree node instead of a token.  Makes code gen identical
	 *  for both parser and tree grammars. :)
	 */
	public Object LT(int k) {
		//System.out.println("LT("+k+"); head="+head+", tail="+tail);
		if ( k==-1 ) {
			return previousNode;
		}
		if ( k<0 ) {
			throw new IllegalArgumentException("tree node streams cannot look backwards more than 1 node");
		}
		if ( k==0 ) {
			return Tree.INVALID_NODE;
		}
		fill(k);
		return lookahead[(head+k-1)%lookahead.length];
	}

	/** Where is this stream pulling nodes from?  This is not the name, but
	 *  the object that provides node objects.
	 */
	public Object getTreeSource() {
		return root;
	}

	public TokenStream getTokenStream() {
		return tokens;
	}

	public void setTokenStream(TokenStream tokens) {
		this.tokens = tokens;
	}

	/** Make sure we have at least k symbols in lookahead buffer */
	protected void fill(int k) {
		int n = getLookaheadSize();
		//System.out.println("we have "+n+" nodes; need "+(k-n));
		for (int i=1; i<=k-n; i++) {
			next(); // get at least k-depth lookahead nodes
		}
	}

	/** Add a node to the lookahead buffer.  Add at lookahead[tail].
	 *  If you tail+1 == head, then we must create a bigger buffer
	 *  and copy all the nodes over plus reset head, tail.  After
	 *  this method, LT(1) will be lookahead[0].
	 */
	protected void addLookahead(Object node) {
		//System.out.println("addLookahead head="+head+", tail="+tail);
		lookahead[tail] = node;
		tail = (tail+1)%lookahead.length;
		if ( tail==head ) {
			// buffer overflow: tail caught up with head
			// allocate a buffer 2x as big
			Object[] bigger = new Object[2*lookahead.length];
			// copy head to end of buffer to beginning of bigger buffer
			int remainderHeadToEnd = lookahead.length-head;
			System.arraycopy(lookahead, head, bigger, 0, remainderHeadToEnd);
			// copy 0..tail to after that
			System.arraycopy(lookahead, 0, bigger, remainderHeadToEnd, tail);
			lookahead = bigger; // reset to bigger buffer
			head = 0;
			tail += remainderHeadToEnd;
		}
	}

	// Satisfy IntStream interface

	public void consume() {
		/*
		System.out.println("consume: currentNode="+currentNode.getType()+
						   " childIndex="+currentChildIndex+
						   " nodeIndex="+absoluteNodeIndex);
						   */
		// make sure there is something in lookahead buf, which might call next()
		fill(1);
		absoluteNodeIndex++;
		previousNode = lookahead[head]; // track previous node before moving on
		head = (head+1) % lookahead.length;
	}

	public int LA(int i) {
		Object t = LT(i);
		if ( t==null ) {
			return Token.INVALID_TOKEN_TYPE;
		}
		return adaptor.getType(t);
	}

	/** Record the current state of the tree walk which includes
	 *  the current node and stack state as well as the lookahead
	 *  buffer.
	 */
	public int mark() {
		if ( markers==null ) {
			markers = new ArrayList();
			markers.add(null); // depth 0 means no backtracking, leave blank
		}
		markDepth++;
		TreeWalkState state = null;
		if ( markDepth>=markers.size() ) {
			state = new TreeWalkState();
			markers.add(state);
		}
		else {
			state = (TreeWalkState)markers.get(markDepth);
		}
		state.absoluteNodeIndex = absoluteNodeIndex;
		state.currentChildIndex = currentChildIndex;
		state.currentNode = currentNode;
		state.previousNode = previousNode;
		state.nodeStackSize = nodeStack.size();
		state.indexStackSize = indexStack.size();
		// take snapshot of lookahead buffer
		int n = getLookaheadSize();
		int i=0;
		state.lookahead = new Object[n];
		for (int k=1; k<=n; k++,i++) {
			state.lookahead[i] = LT(k);
		}
		lastMarker = markDepth;
		return markDepth;
	}

	public void release(int marker) {
		// unwind any other markers made after marker and release marker
		markDepth = marker;
		// release this marker
		markDepth--;
	}

	/** Rewind the current state of the tree walk to the state it
	 *  was in when mark() was called and it returned marker.  Also,
	 *  wipe out the lookahead which will force reloading a few nodes
	 *  but it is better than making a copy of the lookahead buffer
	 *  upon mark().
	 */
	public void rewind(int marker) {
		if ( markers==null ) {
			return;
		}
		TreeWalkState state = (TreeWalkState)markers.get(marker);
		absoluteNodeIndex = state.absoluteNodeIndex;
		currentChildIndex = state.currentChildIndex;
		currentNode = state.currentNode;
		previousNode = state.previousNode;
		// drop node and index stacks back to old size
		nodeStack.setSize(state.nodeStackSize);
		indexStack.setSize(state.indexStackSize);
		head = tail = 0; // wack lookahead buffer and then refill
		for (; tail<state.lookahead.length; tail++) {
			lookahead[tail] = state.lookahead[tail];
		}
		release(marker);
	}

	public void rewind() {
		rewind(lastMarker);
	}

	/** consume() ahead until we hit index.  Can't just jump ahead--must
	 *  spit out the navigation nodes.
	 */
	public void seek(int index) {
		if ( index<this.index() ) {
			throw new IllegalArgumentException("can't seek backwards in node stream");
		}
		// seek forward, consume until we hit index
		while ( this.index()<index ) {
			consume();
		}
	}

	public int index() {
		return absoluteNodeIndex+1;
	}

	/** Expensive to compute; recursively walk tree to find size;
	 *  include navigation nodes and EOF.  Reuse functionality
	 *  in CommonTreeNodeStream as we only really use this
	 *  for testing.
	 */
	public int size() {
		CommonTreeNodeStream s = new CommonTreeNodeStream(root);
		return s.size();
	}

	/** Return the next node found during a depth-first walk of root.
	 *  Also, add these nodes and DOWN/UP imaginary nodes into the lokoahead
	 *  buffer as a side-effect.  Normally side-effects are bad, but because
	 *  we can emit many tokens for every next() call, it's pretty hard to
	 *  use a single return value for that.  We must add these tokens to
	 *  the lookahead buffer.
	 *
	 *  This does *not* return the DOWN/UP nodes; those are only returned
	 *  by the LT() method.
	 *
	 *  Ugh.  This mechanism is much more complicated than a recursive
	 *  solution, but it's the only way to provide nodes on-demand instead
	 *  of walking once completely through and buffering up the nodes. :(
	 */
	public Object next() {
		// already walked entire tree; nothing to return
		if ( currentNode==null ) {
			addLookahead(eof);
			// this is infinite stream returning EOF at end forever
			// so don't throw NoSuchElementException
			return null;
		}

		// initial condition (first time method is called)
		if ( currentChildIndex==-1 ) {
			return handleRootNode();
		}

		// index is in the child list?
		if ( currentChildIndex<adaptor.getChildCount(currentNode) ) {
			return visitChild(currentChildIndex);
		}

		// hit end of child list, return to parent node or its parent ...
		walkBackToMostRecentNodeWithUnvisitedChildren();
		if ( currentNode!=null ) {
			return visitChild(currentChildIndex);
		}

		return null;
	}

	protected Object handleRootNode() {
		Object node;
		node = currentNode;
		// point to first child in prep for subsequent next()
		currentChildIndex = 0;
		if ( adaptor.isNil(node) ) {
			// don't count this root nil node
			node = visitChild(currentChildIndex);
		}
		else {
			addLookahead(node);
			if ( adaptor.getChildCount(currentNode)==0 ) {
				// single node case
				currentNode = null; // say we're done
			}
		}
		return node;
	}

	protected Object visitChild(int child) {
		Object node = null;
		// save state
		nodeStack.push(currentNode);
		indexStack.push(new Integer(child));
		if ( child==0 && !adaptor.isNil(currentNode) ) {
			addNavigationNode(Token.DOWN);
		}
		// visit child
		currentNode = adaptor.getChild(currentNode,child);
		currentChildIndex = 0;
		node = currentNode;  // record node to return
		addLookahead(node);
		walkBackToMostRecentNodeWithUnvisitedChildren();
		return node;
	}

	/** As we flatten the tree, we use UP, DOWN nodes to represent
	 *  the tree structure.  When debugging we need unique nodes
	 *  so instantiate new ones when uniqueNavigationNodes is true.
	 */
	protected void addNavigationNode(final int ttype) {
		Object navNode = null;
		if ( ttype==Token.DOWN ) {
			if ( hasUniqueNavigationNodes() ) {
				navNode = adaptor.create(Token.DOWN, "DOWN");
			}
			else {
				navNode = down;
			}
		}
		else {
			if ( hasUniqueNavigationNodes() ) {
				navNode = adaptor.create(Token.UP, "UP");
			}
			else {
				navNode = up;
			}
		}
		addLookahead(navNode);
	}

	/** Walk upwards looking for a node with more children to walk. */
	protected void walkBackToMostRecentNodeWithUnvisitedChildren() {
		while ( currentNode!=null &&
				currentChildIndex>=adaptor.getChildCount(currentNode) )
		{
			currentNode = nodeStack.pop();
			if ( currentNode==null ) { // hit the root?
				return;
			}
			currentChildIndex = ((Integer)indexStack.pop()).intValue();
			currentChildIndex++; // move to next child
			if ( currentChildIndex>=adaptor.getChildCount(currentNode) ) {
				if ( !adaptor.isNil(currentNode) ) {
					addNavigationNode(Token.UP);
				}
				if ( currentNode==root ) { // we done yet?
					currentNode = null;
				}
			}
		}
	}

	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}

	public boolean hasUniqueNavigationNodes() {
		return uniqueNavigationNodes;
	}

	public void setUniqueNavigationNodes(boolean uniqueNavigationNodes) {
		this.uniqueNavigationNodes = uniqueNavigationNodes;
	}

	/** Print out the entire tree including DOWN/UP nodes.  Uses
	 *  a recursive walk.  Mostly useful for testing as it yields
	 *  the token types not text.
	 */
	public String toString() {
		return toString(root, null);
	}

	protected int getLookaheadSize() {
		return tail<head?(lookahead.length-head+tail):(tail-head);
	}

	public String toString(Object start, Object stop) {
		if ( start==null ) {
			return null;
		}
		// if we have the token stream, use that to dump text in order
		if ( tokens!=null ) {
			// don't trust stop node as it's often an UP node etc...
			// walk backwards until you find a non-UP, non-DOWN node
			// and ask for it's token index.
			int beginTokenIndex = adaptor.getTokenStartIndex(start);
			int endTokenIndex = adaptor.getTokenStopIndex(stop);
			if ( stop!=null && adaptor.getType(stop)==Token.UP ) {
				endTokenIndex = adaptor.getTokenStopIndex(start);
			}
			else {
				endTokenIndex = size()-1;
			}
			return tokens.toString(beginTokenIndex, endTokenIndex);
		}
		StringBuffer buf = new StringBuffer();
		toStringWork(start, stop, buf);
		return buf.toString();
	}

	protected void toStringWork(Object p, Object stop, StringBuffer buf) {
		if ( !adaptor.isNil(p) ) {
			String text = adaptor.getText(p);
			if ( text==null ) {
				text = " "+String.valueOf(adaptor.getType(p));
			}
			buf.append(text); // ask the node to go to string
		}
		if ( p==stop ) {
			return;
		}
		int n = adaptor.getChildCount(p);
		if ( n>0 && !adaptor.isNil(p) ) {
			buf.append(" ");
			buf.append(Token.DOWN);
		}
		for (int c=0; c<n; c++) {
			Object child = adaptor.getChild(p,c);
			toStringWork(child, stop, buf);
		}
		if ( n>0 && !adaptor.isNil(p) ) {
			buf.append(" ");
			buf.append(Token.UP);
		}
	}
}

