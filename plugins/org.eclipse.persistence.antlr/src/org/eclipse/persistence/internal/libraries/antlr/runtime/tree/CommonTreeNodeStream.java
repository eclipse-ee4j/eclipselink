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

import java.util.*;

/** A buffered stream of tree nodes.  Nodes can be from a tree of ANY kind.
 *
 *  This node stream sucks all nodes out of the tree specified in
 *  the constructor during construction and makes pointers into
 *  the tree using an array of Object pointers. The stream necessarily
 *  includes pointers to DOWN and UP and EOF nodes.
 *
 *  This stream knows how to mark/release for backtracking.
 *
 *  This stream is most suitable for tree interpreters that need to
 *  jump around a lot or for tree parsers requiring speed (at cost of memory).
 *  There is some duplicated functionality here with UnBufferedTreeNodeStream
 *  but just in bookkeeping, not tree walking etc...
 *
 *  @see UnBufferedTreeNodeStream
 */
public class CommonTreeNodeStream implements TreeNodeStream {
	public static final int DEFAULT_INITIAL_BUFFER_SIZE = 100;
	public static final int INITIAL_CALL_STACK_SIZE = 10;

	protected class StreamIterator implements Iterator {
		int i = 0;
		public boolean hasNext() {
			return i<nodes.size();
		}

		public Object next() {
			int current = i;
			i++;
			if ( current < nodes.size() ) {
				return nodes.get(current);
			}
			return eof;
		}

		public void remove() {
			throw new RuntimeException("cannot remove nodes from stream");
		}
	}

	// all these navigation nodes are shared and hence they
	// cannot contain any line/column info

	protected Object down;
	protected Object up;
	protected Object eof;

	/** The complete mapping from stream index to tree node.
	 *  This buffer includes pointers to DOWN, UP, and EOF nodes.
	 *  It is built upon ctor invocation.  The elements are type
	 *  Object as we don't what the trees look like.
	 *
	 *  Load upon first need of the buffer so we can set token types
	 *  of interest for reverseIndexing.  Slows us down a wee bit to
	 *  do all of the if p==-1 testing everywhere though.
	 */
	protected List nodes;

	/** Pull nodes from which tree? */
	protected Object root;

	/** IF this tree (root) was created from a token stream, track it. */
	protected TokenStream tokens;

	/** What tree adaptor was used to build these trees */
	TreeAdaptor adaptor;

	/** Reuse same DOWN, UP navigation nodes unless this is true */
	protected boolean uniqueNavigationNodes = false;

	/** The index into the nodes list of the current node (next node
	 *  to consume).  If -1, nodes array not filled yet.
	 */
	protected int p = -1;

	/** Track the last mark() call result value for use in rewind(). */
	protected int lastMarker;

	/** Stack of indexes used for push/pop calls */
	protected int[] calls;

	/** Stack pointer for stack of indexes; -1 indicates empty.  Points
	 *  at next location to push a value.
	 */
	protected int _sp = -1;

	/** During fillBuffer(), we can make a reverse index from a set
	 *  of token types of interest to the list of indexes into the
	 *  node stream.  This lets us convert a node pointer to a
	 *  stream index semi-efficiently for a list of interesting
	 *  nodes such as function definition nodes (you'll want to seek
	 *  to their bodies for an interpreter).  Also useful for doing
	 *  dynamic searches; i.e., go find me all PLUS nodes.
	 */
	protected Map tokenTypeToStreamIndexesMap;

	/** If tokenTypesToReverseIndex set to INDEX_ALL then indexing
	 *  occurs for all token types.
	 */
	public static final Set INDEX_ALL = new HashSet();

	/** A set of token types user would like to index for faster lookup.
	 *  If this is INDEX_ALL, then all token types are tracked.  If null,
	 *  then none are indexed.
	 */
	protected Set tokenTypesToReverseIndex = null;

	public CommonTreeNodeStream(Object tree) {
		this(new CommonTreeAdaptor(), tree);
	}

	public CommonTreeNodeStream(TreeAdaptor adaptor, Object tree) {
		this(adaptor, tree, DEFAULT_INITIAL_BUFFER_SIZE);
	}

	public CommonTreeNodeStream(TreeAdaptor adaptor, Object tree, int initialBufferSize) {
		this.root = tree;
		this.adaptor = adaptor;
		nodes = new ArrayList(initialBufferSize);
		down = adaptor.create(Token.DOWN, "DOWN");
		up = adaptor.create(Token.UP, "UP");
		eof = adaptor.create(Token.EOF, "EOF");
	}

	/** Walk tree with depth-first-search and fill nodes buffer.
	 *  Don't do DOWN, UP nodes if its a list (t is isNil).
	 */
	protected void fillBuffer() {
		fillBuffer(root);
		//System.out.println("revIndex="+tokenTypeToStreamIndexesMap);
		p = 0; // buffer of nodes intialized now
	}

	protected void fillBuffer(Object t) {
		boolean nil = adaptor.isNil(t);
		if ( !nil ) {
			nodes.add(t); // add this node
			fillReverseIndex(t, nodes.size()-1);
		}
		// add DOWN node if t has children
		int n = adaptor.getChildCount(t);
		if ( !nil && n>0 ) {
			addNavigationNode(Token.DOWN);
		}
		// and now add all its children
		for (int c=0; c<n; c++) {
			Object child = adaptor.getChild(t,c);
			fillBuffer(child);
		}
		// add UP node if t has children
		if ( !nil && n>0 ) {
			addNavigationNode(Token.UP);
		}
	}

	/** Given a node, add this to the reverse index tokenTypeToStreamIndexesMap.
	 *  You can override this method to alter how indexing occurs.  The
	 *  default is to create a
	 *
	 *    Map<Integer token type,ArrayList<Integer stream index>>
	 *
	 *  This data structure allows you to find all nodes with type INT in order.
	 *
	 *  If you really need to find a node of type, say, FUNC quickly then perhaps
	 *
	 *    Map<Integertoken type,Map<Object tree node,Integer stream index>>
	 *
	 *  would be better for you.  The interior maps map a tree node to
	 *  the index so you don't have to search linearly for a specific node.
	 *
	 *  If you change this method, you will likely need to change
	 *  getNodeIndex(), which extracts information.
	 */
	protected void fillReverseIndex(Object node, int streamIndex) {
		//System.out.println("revIndex "+node+"@"+streamIndex);
		if ( tokenTypesToReverseIndex==null ) {
			return; // no indexing if this is empty (nothing of interest)
		}
		if ( tokenTypeToStreamIndexesMap==null ) {
			tokenTypeToStreamIndexesMap = new HashMap(); // first indexing op
		}
		int tokenType = adaptor.getType(node);
		Integer tokenTypeI = new Integer(tokenType);
		if ( !(tokenTypesToReverseIndex==INDEX_ALL ||
			   tokenTypesToReverseIndex.contains(tokenTypeI)) )
		{
			return; // tokenType not of interest
		}
		Integer streamIndexI = new Integer(streamIndex);
		ArrayList indexes = (ArrayList)tokenTypeToStreamIndexesMap.get(tokenTypeI);
		if ( indexes==null ) {
			indexes = new ArrayList(); // no list yet for this token type
			indexes.add(streamIndexI); // not there yet, add
			tokenTypeToStreamIndexesMap.put(tokenTypeI, indexes);
		}
		else {
			if ( !indexes.contains(streamIndexI) ) {
				indexes.add(streamIndexI); // not there yet, add
			}
		}
	}

	/** Track the indicated token type in the reverse index.  Call this
	 *  repeatedly for each type or use variant with Set argument to
	 *  set all at once.
	 * @param tokenType
	 */
	public void reverseIndex(int tokenType) {
		if ( tokenTypesToReverseIndex==null ) {
			tokenTypesToReverseIndex = new HashSet();
		}
		else if ( tokenTypesToReverseIndex==INDEX_ALL ) {
			return;
		}
		tokenTypesToReverseIndex.add(new Integer(tokenType));
	}

	/** Track the indicated token types in the reverse index. Set
	 *  to INDEX_ALL to track all token types.
	 */
	public void reverseIndex(Set tokenTypes) {
		tokenTypesToReverseIndex = tokenTypes;
	}

	/** Given a node pointer, return its index into the node stream.
	 *  This is not its Token stream index.  If there is no reverse map
	 *  from node to stream index or the map does not contain entries
	 *  for node's token type, a linear search of entire stream is used.
	 *
	 *  Return -1 if exact node pointer not in stream.
	 */
	public int getNodeIndex(Object node) {
		//System.out.println("get "+node);
		if ( tokenTypeToStreamIndexesMap==null ) {
			return getNodeIndexLinearly(node);
		}
		int tokenType = adaptor.getType(node);
		Integer tokenTypeI = new Integer(tokenType);
		ArrayList indexes = (ArrayList)tokenTypeToStreamIndexesMap.get(tokenTypeI);
		if ( indexes==null ) {
			//System.out.println("found linearly; stream index = "+getNodeIndexLinearly(node));
			return getNodeIndexLinearly(node);
		}
		for (int i = 0; i < indexes.size(); i++) {
			Integer streamIndexI = (Integer)indexes.get(i);
			Object n = get(streamIndexI.intValue());
			if ( n==node ) {
				//System.out.println("found in index; stream index = "+streamIndexI);
				return streamIndexI.intValue(); // found it!
			}
		}
		return -1;
	}

	protected int getNodeIndexLinearly(Object node) {
		if ( p==-1 ) {
			fillBuffer();
		}
		for (int i = 0; i < nodes.size(); i++) {
			Object t = (Object) nodes.get(i);
			if ( t==node ) {
				return i;
			}
		}
		return -1;
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
		nodes.add(navNode);
	}

	public Object get(int i) {
		if ( p==-1 ) {
			fillBuffer();
		}
		return nodes.get(i);
	}

	public Object LT(int k) {
		if ( p==-1 ) {
			fillBuffer();
		}
		if ( k==0 ) {
			return null;
		}
		if ( k<0 ) {
			return LB(-k);
		}
		//System.out.print("LT(p="+p+","+k+")=");
		if ( (p+k-1) >= nodes.size() ) {
			return eof;
		}
		return nodes.get(p+k-1);
	}

/*
	public Object getLastTreeNode() {
		int i = index();
		if ( i>=size() ) {
			i--; // if at EOF, have to start one back
		}
		System.out.println("start last node: "+i+" size=="+nodes.size());
		while ( i>=0 &&
			(adaptor.getType(get(i))==Token.EOF ||
			 adaptor.getType(get(i))==Token.UP ||
			 adaptor.getType(get(i))==Token.DOWN) )
		{
			i--;
		}
		System.out.println("stop at node: "+i+" "+nodes.get(i));
		return nodes.get(i);
	}
*/
	
	/** Look backwards k nodes */
	protected Object LB(int k) {
		if ( k==0 ) {
			return null;
		}
		if ( (p-k)<0 ) {
			return null;
		}
		return nodes.get(p-k);
	}

	public Object getTreeSource() {
		return root;
	}

	public TokenStream getTokenStream() {
		return tokens;
	}

	public void setTokenStream(TokenStream tokens) {
		this.tokens = tokens;
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

	public void consume() {
		if ( p==-1 ) {
			fillBuffer();
		}
		p++;
	}

	public int LA(int i) {
		return adaptor.getType(LT(i));
	}

	public int mark() {
		if ( p==-1 ) {
			fillBuffer();
		}
		lastMarker = index();
		return lastMarker;
	}

	public void release(int marker) {
		// no resources to release
	}

	public int index() {
		return p;
	}

	public void rewind(int marker) {
		seek(marker);
	}

	public void rewind() {
		seek(lastMarker);
	}

	public void seek(int index) {
		if ( p==-1 ) {
			fillBuffer();
		}
		p = index;
	}

	/** Make stream jump to a new location, saving old location.
	 *  Switch back with pop().  I manage dyanmic array manually
	 *  to avoid creating Integer objects all over the place.
	 */
	public void push(int index) {
		if ( calls==null ) {
			calls = new int[INITIAL_CALL_STACK_SIZE];
		}
		else if ( (_sp+1)>=calls.length ) {
			int[] newStack = new int[calls.length*2];
			System.arraycopy(calls, 0, newStack, 0, calls.length);
			calls = newStack;
		}
		calls[++_sp] = p; // save current index
		seek(index);
	}

	/** Seek back to previous index saved during last push() call.
	 *  Return top of stack (return index).
	 */
	public int pop() {
		int ret = calls[_sp--];
		seek(ret);
		return ret;
	}

	public int size() {
		if ( p==-1 ) {
			fillBuffer();
		}
		return nodes.size();
	}

	public Iterator iterator() {
		if ( p==-1 ) {
			fillBuffer();
		}
		return new StreamIterator();
	}

	/** Used for testing, just return the token type stream */
	public String toString() {
		if ( p==-1 ) {
			fillBuffer();
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < nodes.size(); i++) {
			Object t = (Object) nodes.get(i);
			buf.append(" ");
			buf.append(adaptor.getType(t));
		}
		return buf.toString();
	}

	public String toString(Object start, Object stop) {
		if ( start==null || stop==null ) {
			return null;
		}
		if ( p==-1 ) {
			fillBuffer();
		}
		System.out.println("stop: "+stop);
		if ( start instanceof CommonTree )
			System.out.print("toString: "+((CommonTree)start).getToken()+", ");
		else
			System.out.println(start);
		if ( stop instanceof CommonTree )
			System.out.println(((CommonTree)stop).getToken());
		else
			System.out.println(stop);
		// if we have the token stream, use that to dump text in order
		if ( tokens!=null ) {
			int beginTokenIndex = adaptor.getTokenStartIndex(start);
			int endTokenIndex = adaptor.getTokenStopIndex(stop);
			// if it's a tree, use start/stop index from start node
			// else use token range from start/stop nodes
			if ( adaptor.getType(stop)==Token.UP ) {
				endTokenIndex = adaptor.getTokenStopIndex(start);
			}
			else if ( adaptor.getType(stop)==Token.EOF ) {
				endTokenIndex = size()-2; // don't use EOF
			}
			return tokens.toString(beginTokenIndex, endTokenIndex);
		}
		// walk nodes looking for start
		Object t = null;
		int i = 0;
		for (; i < nodes.size(); i++) {
			t = nodes.get(i);
			if ( t==start ) {
				break;
			}
		}
		// now walk until we see stop, filling string buffer with text
		 StringBuffer buf = new StringBuffer();
		t = nodes.get(i);
		while ( t!=stop ) {
			String text = adaptor.getText(t);
			if ( text==null ) {
				text = " "+String.valueOf(adaptor.getType(t));
			}
			buf.append(text);
			i++;
			t = nodes.get(i);
		}
		// include stop node too
		String text = adaptor.getText(stop);
		if ( text==null ) {
			text = " "+String.valueOf(adaptor.getType(stop));
		}
		buf.append(text);
		return buf.toString();
	}
}
