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

/** Print out (most of) the events... Useful for debugging, testing... */
public class TraceDebugEventListener extends BlankDebugEventListener {
	public void enterRule(String ruleName) { System.out.println("enterRule "+ruleName); }
	public void exitRule(String ruleName) { System.out.println("exitRule "+ruleName); }
	public void enterSubRule(int decisionNumber) { System.out.println("enterSubRule"); }
	public void exitSubRule(int decisionNumber) { System.out.println("exitSubRule"); }
	public void location(int line, int pos) {System.out.println("location "+line+":"+pos);}

	// Tree parsing stuff

	public void consumeNode(int ID, String text, int type) {
		System.out.println("consumeNode "+ID+" "+text+" "+type);
	}

	public void LT(int i, int ID, String text, int type) {
		System.out.println("LT "+i+" "+ID+" "+text+" "+type);
	}


	// AST stuff
	public void nilNode(int ID) {System.out.println("nilNode "+ID);}
	public void createNode(int ID, String text, int type) {System.out.println("create "+ID+": "+text+", "+type);}
	public void createNode(int ID, int tokenIndex) {System.out.println("create "+ID+": "+tokenIndex);}
	public void becomeRoot(int newRootID, int oldRootID) {System.out.println("becomeRoot "+newRootID+", "+oldRootID);}
	public void addChild(int rootID, int childID) {System.out.println("addChild "+rootID+", "+childID);}
	public void trimNilRoot(int ID) {System.out.println("trimNilRoot "+ID);}
	public void setTokenBoundaries(int ID, int tokenStartIndex, int tokenStopIndex) {System.out.println("setTokenBoundaries "+ID+", "+tokenStartIndex+", "+tokenStopIndex);}
}

