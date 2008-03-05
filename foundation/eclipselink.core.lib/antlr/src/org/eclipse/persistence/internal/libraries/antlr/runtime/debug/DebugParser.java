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

import org.eclipse.persistence.internal.libraries.antlr.runtime.*;
import org.antlr.tool.ErrorManager;

import java.io.IOException;

public class DebugParser extends Parser {
	/** Who to notify when events in the parser occur. */
	protected DebugEventListener dbg = null;

	/** Used to differentiate between fixed lookahead and cyclic DFA decisions
	 *  while profiling.
 	 */
	public boolean isCyclicDecision = false;

	/** Create a normal parser except wrap the token stream in a debug
	 *  proxy that fires consume events.
	 */
	public DebugParser(TokenStream input, DebugEventListener dbg) {
		super(new DebugTokenStream(input,dbg));
		setDebugListener(dbg);
	}

	public DebugParser(TokenStream input) {
		this(input, DebugEventSocketProxy.DEFAULT_DEBUGGER_PORT);
	}

	/** Create a proxy to marshall events across socket to another
	 *  listener.  This constructor returns after handshaking with
	 *  debugger so programmer does not have to manually invoke handshake.
	 */
	public DebugParser(TokenStream input, int port) {
		super(new DebugTokenStream(input,null));
		DebugEventSocketProxy proxy =
			new DebugEventSocketProxy(getGrammarFileName(), port);
		setDebugListener(proxy);
		try {
			proxy.handshake();
		}
		catch (IOException ioe) {
			reportError(ioe);
		}
	}

	/** Provide a new debug event listener for this parser.  Notify the
	 *  input stream too that it should send events to this listener.
	 */
	public void setDebugListener(DebugEventListener dbg) {
		if ( input instanceof DebugTokenStream ) {
			((DebugTokenStream)input).setDebugListener(dbg);
		}
		this.dbg = dbg;
	}

	public DebugEventListener getDebugListener() {
		return dbg;
	}

	public void reportError(IOException e) {
		ErrorManager.internalError(e);
	}

	public void beginResync() {
		dbg.beginResync();
	}

	public void endResync() {
		dbg.endResync();
	}

	public void beginBacktrack(int level) {
		dbg.beginBacktrack(level);
	}

	public void endBacktrack(int level, boolean successful) {
		dbg.endBacktrack(level,successful);		
	}

	public void recoverFromMismatchedToken(IntStream input,
										   RecognitionException mte,
										   int ttype,
										   BitSet follow)
		throws RecognitionException
	{
		dbg.recognitionException(mte);
		super.recoverFromMismatchedToken(input,mte,ttype,follow);
	}

	public void recoverFromMismatchedSet(IntStream input,
										 RecognitionException mte,
										 BitSet follow)
		throws RecognitionException
	{
		dbg.recognitionException(mte);
		super.recoverFromMismatchedSet(input,mte,follow);
	}
}
