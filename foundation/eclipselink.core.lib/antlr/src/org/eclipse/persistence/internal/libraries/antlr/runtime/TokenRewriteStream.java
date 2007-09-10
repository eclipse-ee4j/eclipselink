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
package org.eclipse.persistence.internal.libraries.antlr.runtime;

import java.util.*;

/** Useful for dumping out the input stream after doing some
 *  augmentation or other manipulations.
 *
 *  You can insert stuff, replace, and delete chunks.  Note that the
 *  operations are done lazily--only if you convert the buffer to a
 *  String.  This is very efficient because you are not moving data around
 *  all the time.  As the buffer of tokens is converted to strings, the
 *  toString() method(s) check to see if there is an operation at the
 *  current index.  If so, the operation is done and then normal String
 *  rendering continues on the buffer.  This is like having multiple Turing
 *  machine instruction streams (programs) operating on a single input tape. :)
 *
 *  Since the operations are done lazily at toString-time, operations do not
 *  screw up the token index values.  That is, an insert operation at token
 *  index i does not change the index values for tokens i+1..n-1.
 *
 *  Because operations never actually alter the buffer, you may always get
 *  the original token stream back without undoing anything.  Since
 *  the instructions are queued up, you can easily simulate transactions and
 *  roll back any changes if there is an error just by removing instructions.
 *  For example,
 *
 *   CharStream input = new ANTLRFileStream("input");
 *   TLexer lex = new TLexer(input);
 *   TokenRewriteStream tokens = new TokenRewriteStream(lex);
 *   T parser = new T(tokens);
 *   parser.startRule();
 *
 * 	 Then in the rules, you can execute
 *      Token t,u;
 *      ...
 *      input.insertAfter(t, "text to put after t");}
 * 		input.insertAfter(u, "text after u");}
 * 		System.out.println(tokens.toString());
 *
 *  Actually, you have to cast the 'input' to a TokenRewriteStream. :(
 *
 *  You can also have multiple "instruction streams" and get multiple
 *  rewrites from a single pass over the input.  Just name the instruction
 *  streams and use that name again when printing the buffer.  This could be
 *  useful for generating a C file and also its header file--all from the
 *  same buffer:
 *
 *      tokens.insertAfter("pass1", t, "text to put after t");}
 * 		tokens.insertAfter("pass2", u, "text after u");}
 * 		System.out.println(tokens.toString("pass1"));
 * 		System.out.println(tokens.toString("pass2"));
 *
 *  If you don't use named rewrite streams, a "default" stream is used as
 *  the first example shows.
 */
public class TokenRewriteStream extends CommonTokenStream {
	public static final String DEFAULT_PROGRAM_NAME = "default";
    public static final int PROGRAM_INIT_SIZE = 100;
	public static final int MIN_TOKEN_INDEX = 0;

	// Define the rewrite operation hierarchy

	static class RewriteOperation {
		protected int index;
		protected Object text;
		protected RewriteOperation(int index, Object text) {
			this.index = index;
			this.text = text;
		}
		/** Execute the rewrite operation by possibly adding to the buffer.
		 *  Return the index of the next token to operate on.
		 */
		public int execute(StringBuffer buf) {
			return index;
		}
		public String toString() {
			String opName = getClass().getName();
			int $index = opName.indexOf('$');
			opName = opName.substring($index+1, opName.length());
			return opName+"@"+index+'"'+text+'"';
		}
	}

	static class InsertBeforeOp extends RewriteOperation {
		public InsertBeforeOp(int index, Object text) {
			super(index,text);
		}
		public int execute(StringBuffer buf) {
			buf.append(text);
			return index;
		}
	}

	/** TODO: make insertAfters append after each other.
	static class InsertAfterOp extends InsertBeforeOp {
		public InsertAfterOp(int index, String text) {
			super(index,text);
		}
	}
	 */

	/** I'm going to try replacing range from x..y with (y-x)+1 ReplaceOp
	 *  instructions.
	 */
	static class ReplaceOp extends RewriteOperation {
		protected int lastIndex;
		public ReplaceOp(int from, int to, Object text) {
			super(from,text);
			lastIndex = to;
		}
		public int execute(StringBuffer buf) {
			if ( text!=null ) {
				buf.append(text);
			}
			return lastIndex+1;
		}
	}

	static class DeleteOp extends ReplaceOp {
		public DeleteOp(int from, int to) {
			super(from, to, null);
		}
	}

	/** You may have multiple, named streams of rewrite operations.
	 *  I'm calling these things "programs."
	 *  Maps String (name) -> rewrite (List)
	 */
	protected Map programs = null;

	/** Map String (program name) -> Integer index */
	protected Map lastRewriteTokenIndexes = null;

	public TokenRewriteStream() {
		init();
	}

	protected void init() {
		programs = new HashMap();
		programs.put(DEFAULT_PROGRAM_NAME, new ArrayList(PROGRAM_INIT_SIZE));
		lastRewriteTokenIndexes = new HashMap();
	}

	public TokenRewriteStream(TokenSource tokenSource) {
	    super(tokenSource);
		init();
	}

	public TokenRewriteStream(TokenSource tokenSource, int channel) {
		super(tokenSource, channel);
		init();
	}

	public void rollback(int instructionIndex) {
		rollback(DEFAULT_PROGRAM_NAME, instructionIndex);
	}

	/** Rollback the instruction stream for a program so that
	 *  the indicated instruction (via instructionIndex) is no
	 *  longer in the stream.  UNTESTED!
	 */
	public void rollback(String programName, int instructionIndex) {
		List is = (List)programs.get(programName);
		if ( is!=null ) {
			programs.put(programName, is.subList(MIN_TOKEN_INDEX,instructionIndex));
		}
	}

	public void deleteProgram() {
		deleteProgram(DEFAULT_PROGRAM_NAME);
	}

	/** Reset the program so that no instructions exist */
	public void deleteProgram(String programName) {
		rollback(programName, MIN_TOKEN_INDEX);
	}

	/** If op.index > lastRewriteTokenIndexes, just add to the end.
	 *  Otherwise, do linear */
	protected void addToSortedRewriteList(RewriteOperation op) {
		addToSortedRewriteList(DEFAULT_PROGRAM_NAME, op);
	}

	/** Add an instruction to the rewrite instruction list ordered by
	 *  the instruction number (use a binary search for efficiency).
	 *  The list is ordered so that toString() can be done efficiently.
	 *
	 *  When there are multiple instructions at the same index, the instructions
	 *  must be ordered to ensure proper behavior.  For example, a delete at
	 *  index i must kill any replace operation at i.  Insert-before operations
	 *  must come before any replace / delete instructions.  If there are
	 *  multiple insert instructions for a single index, they are done in
	 *  reverse insertion order so that "insert foo" then "insert bar" yields
	 *  "foobar" in front rather than "barfoo".  This is convenient because
	 *  I can insert new InsertOp instructions at the index returned by
	 *  the binary search.  A ReplaceOp kills any previous replace op.  Since
	 *  delete is the same as replace with null text, i can check for
	 *  ReplaceOp and cover DeleteOp at same time. :)
	 */
	protected void addToSortedRewriteList(String programName, RewriteOperation op) {
		List rewrites = getProgram(programName);
		//System.out.println("### add "+op+"; rewrites="+rewrites);
		Comparator comparator = new Comparator() {
			public int compare(Object o, Object o1) {
				RewriteOperation a = (RewriteOperation)o;
				RewriteOperation b = (RewriteOperation)o1;
				if ( a.index<b.index ) return -1;
				if ( a.index>b.index ) return 1;
				return 0;
			}
		};
        int pos = Collections.binarySearch(rewrites, op, comparator);
		//System.out.println("bin search returns: pos="+pos);

		if ( pos>=0 ) {
			// binarySearch does not guarantee first element when multiple
			// are found.  I must seach backwards for first op with op.index
			for (; pos>=0; pos--) {
				RewriteOperation prevOp = (RewriteOperation)rewrites.get(pos);
				if ( prevOp.index<op.index ) {
					break;
				}
			}
			pos++; // pos points at first op before ops with op.index; go back up one
			// now pos is the index in rewrites of first op with op.index
			//System.out.println("first op with op.index: pos="+pos);

			// an instruction operating already on that index was found;
			// make this one happen after all the others
			//System.out.println("found instr for index="+op.index);
			if ( op instanceof ReplaceOp ) {
				boolean replaced = false;
				int i;
				// look for an existing replace
				for (i=pos; i<rewrites.size(); i++) {
					RewriteOperation prevOp = (RewriteOperation)rewrites.get(pos);
					if ( prevOp.index!=op.index ) {
						break;
					}
					if ( prevOp instanceof ReplaceOp ) {
						rewrites.set(pos, op); // replace old with new
						replaced=true;
						break;
					}
					// keep going; must be an insert
				}
				if ( !replaced ) {
					// add replace op to the end of all the inserts
					rewrites.add(i, op);
				}
			}
			else {
				// inserts are added in front of existing inserts
				rewrites.add(pos, op);
			}
		}
		else {
			//System.out.println("no instruction at pos=="+pos);
			rewrites.add(-pos-1, op);
		}
		//System.out.println("after, rewrites="+rewrites);
	}

	public void insertAfter(Token t, Object text) {
		insertAfter(DEFAULT_PROGRAM_NAME, t, text);
	}

	public void insertAfter(int index, Object text) {
		insertAfter(DEFAULT_PROGRAM_NAME, index, text);
	}

	public void insertAfter(String programName, Token t, Object text) {
		insertAfter(programName,t.getTokenIndex(), text);
	}

	public void insertAfter(String programName, int index, Object text) {
		// to insert after, just insert before next index (even if past end)
		insertBefore(programName,index+1, text);
		//addToSortedRewriteList(programName, new InsertAfterOp(index,text));
	}

	public void insertBefore(Token t, Object text) {
		insertBefore(DEFAULT_PROGRAM_NAME, t, text);
	}

	public void insertBefore(int index, Object text) {
		insertBefore(DEFAULT_PROGRAM_NAME, index, text);
	}

	public void insertBefore(String programName, Token t, Object text) {
		insertBefore(programName, t.getTokenIndex(), text);
	}

	public void insertBefore(String programName, int index, Object text) {
		addToSortedRewriteList(programName, new InsertBeforeOp(index,text));
	}

	public void replace(int index, Object text) {
		replace(DEFAULT_PROGRAM_NAME, index, index, text);
	}

	public void replace(int from, int to, Object text) {
		replace(DEFAULT_PROGRAM_NAME, from, to, text);
	}

	public void replace(Token indexT, Object text) {
		replace(DEFAULT_PROGRAM_NAME, indexT, indexT, text);
	}

	public void replace(Token from, Token to, Object text) {
		replace(DEFAULT_PROGRAM_NAME, from, to, text);
	}

	public void replace(String programName, int from, int to, Object text) {
		if ( from > to || from<0 || to<0 ) {
			return;
		}
		addToSortedRewriteList(programName, new ReplaceOp(from, to, text));
		/*
		// replace from..to by deleting from..to-1 and then do a replace
		// on last index
		for (int i=from; i<to; i++) {
			addToSortedRewriteList(new DeleteOp(i,i));
		}
		addToSortedRewriteList(new ReplaceOp(to, to, text));
		*/
	}

	public void replace(String programName, Token from, Token to, Object text) {
		replace(programName,
				from.getTokenIndex(),
				to.getTokenIndex(),
				text);
	}

	public void delete(int index) {
		delete(DEFAULT_PROGRAM_NAME, index, index);
	}

	public void delete(int from, int to) {
		delete(DEFAULT_PROGRAM_NAME, from, to);
	}

	public void delete(Token indexT) {
		delete(DEFAULT_PROGRAM_NAME, indexT, indexT);
	}

	public void delete(Token from, Token to) {
		delete(DEFAULT_PROGRAM_NAME, from, to);
	}

	public void delete(String programName, int from, int to) {
		replace(programName,from,to,null);
	}

	public void delete(String programName, Token from, Token to) {
		replace(programName,from,to,null);
	}

	public int getLastRewriteTokenIndex() {
		return getLastRewriteTokenIndex(DEFAULT_PROGRAM_NAME);
	}

	protected int getLastRewriteTokenIndex(String programName) {
		Integer I = (Integer)lastRewriteTokenIndexes.get(programName);
		if ( I==null ) {
			return -1;
		}
		return I.intValue();
	}

	protected void setLastRewriteTokenIndex(String programName, int i) {
		lastRewriteTokenIndexes.put(programName, new Integer(i));
	}

	protected List getProgram(String name) {
		List is = (List)programs.get(name);
		if ( is==null ) {
			is = initializeProgram(name);
		}
		return is;
	}

	private List initializeProgram(String name) {
		List is = new ArrayList(PROGRAM_INIT_SIZE);
		programs.put(name, is);
		return is;
	}

	public String toOriginalString() {
		return toOriginalString(MIN_TOKEN_INDEX, size()-1);
	}

	public String toOriginalString(int start, int end) {
		StringBuffer buf = new StringBuffer();
		for (int i=start; i>=MIN_TOKEN_INDEX && i<=end && i<tokens.size(); i++) {
			buf.append(get(i).getText());
		}
		return buf.toString();
	}

	public String toString() {
		return toString(MIN_TOKEN_INDEX, size()-1);
	}

	public String toString(String programName) {
		return toString(programName, MIN_TOKEN_INDEX, size()-1);
	}

	public String toString(int start, int end) {
		return toString(DEFAULT_PROGRAM_NAME, start, end);
	}

	public String toString(String programName, int start, int end) {
		List rewrites = (List)programs.get(programName);
		if ( rewrites==null || rewrites.size()==0 ) {
			return toOriginalString(start,end); // no instructions to execute
		}
		StringBuffer buf = new StringBuffer();

		/// Index of first rewrite we have not done
		int rewriteOpIndex = 0;

		int tokenCursor=start;
		while ( tokenCursor>=MIN_TOKEN_INDEX &&
				tokenCursor<=end &&
				tokenCursor<tokens.size() )
		{
			//System.out.println("tokenCursor="+tokenCursor);
			// execute instructions associated with this token index
			if ( rewriteOpIndex<rewrites.size() ) {
				RewriteOperation op =
						(RewriteOperation)rewrites.get(rewriteOpIndex);

				// skip all ops at lower index
				while ( op.index<tokenCursor && rewriteOpIndex<rewrites.size() ) {
					rewriteOpIndex++;
					if ( rewriteOpIndex<rewrites.size() ) {
						op = (RewriteOperation)rewrites.get(rewriteOpIndex);
					}
				}

				// while we have ops for this token index, exec them
				while ( tokenCursor==op.index && rewriteOpIndex<rewrites.size() ) {
					//System.out.println("execute "+op+" at instruction "+rewriteOpIndex);
					tokenCursor = op.execute(buf);
					//System.out.println("after execute tokenCursor = "+tokenCursor);
					rewriteOpIndex++;
					if ( rewriteOpIndex<rewrites.size() ) {
						op = (RewriteOperation)rewrites.get(rewriteOpIndex);
					}
				}
			}
			// dump the token at this index
			if ( tokenCursor<=end ) {
				buf.append(get(tokenCursor).getText());
				tokenCursor++;
			}
		}
		// now see if there are operations (append) beyond last token index
		for (int opi=rewriteOpIndex; opi<rewrites.size(); opi++) {
			RewriteOperation op =
					(RewriteOperation)rewrites.get(opi);
			if ( op.index>=size() ) {
				op.execute(buf); // must be insertions if after last token
			}
			//System.out.println("execute "+op+" at "+opi);
			//op.execute(buf); // must be insertions if after last token
		}

		return buf.toString();
	}

	public String toDebugString() {
		return toDebugString(MIN_TOKEN_INDEX, size()-1);
	}

	public String toDebugString(int start, int end) {
		StringBuffer buf = new StringBuffer();
		for (int i=start; i>=MIN_TOKEN_INDEX && i<=end && i<tokens.size(); i++) {
			buf.append(get(i));
		}
		return buf.toString();
	}
}
