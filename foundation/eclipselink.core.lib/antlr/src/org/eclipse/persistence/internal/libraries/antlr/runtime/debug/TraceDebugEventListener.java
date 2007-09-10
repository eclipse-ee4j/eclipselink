/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

