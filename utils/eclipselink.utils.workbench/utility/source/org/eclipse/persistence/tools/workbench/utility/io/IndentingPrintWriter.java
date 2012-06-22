/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.io;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * Extend PrintWriter to automatically indent new lines.
 */
public class IndentingPrintWriter extends PrintWriter {

	private String indent;
	private int indentLevel;
	private boolean needsIndent;

	public static String DEFAULT_INDENT = "\t";

	/**
	 * Construct a writer that indents with tabs.
	 */
	public IndentingPrintWriter(Writer out) {
		this(out, DEFAULT_INDENT);
	}
	
	/**
	 * Construct a writer that indents with the specified string.
	 */
	public IndentingPrintWriter(Writer out, String indent) {
		super(out);
		this.indent = indent;
		this.indentLevel = 0;
		this.needsIndent = true;
	}
	
	/**
	 * Set flag so following line is indented.
	 */
	public void println() {
		synchronized (this.lock) {
			super.println();
			this.needsIndent = true;
		}
	}
	
	/**
	 * Print the appropriate indent.
	 */
	private void printIndent() {
		if (this.needsIndent) {
			this.needsIndent = false;
			for (int i = this.indentLevel; i-- > 0; ) {
				this.print(this.indent);
			}
		}
	}
	
	/**
	 * Write a portion of an array of characters.
	 */
	public void write(char buf[], int off, int len) {
		synchronized (this.lock) {
			this.printIndent();
			super.write(buf, off, len);
		}
	}
	
	/**
	 * Write a single character.
	 */
	public void write(int c) {
		synchronized (this.lock) {
			this.printIndent();
			super.write(c);
		}
	}
	
	/**
	 * Write a portion of a string.
	 */
	public void write(String s, int off, int len) {
		synchronized (this.lock) {
			this.printIndent();
			super.write(s, off, len);
		}
	}
	
	/**
	 * Bump the indent level.
	 */
	public void indent() {
		this.incrementIndentLevel();
	}
	
	/**
	 * Decrement the indent level.
	 */
	public void undent() {
		this.decrementIndentLevel();
	}
	
	/**
	 * Bump the indent level.
	 */
	public void incrementIndentLevel() {
		synchronized (this.lock) {
			this.indentLevel++;
		}
	}
	
	/**
	 * Decrement the indent level.
	 */
	public void decrementIndentLevel() {
		synchronized (this.lock) {
			this.indentLevel--;
		}
	}
	
	/**
	 * Return the current indent level.
	 */
	public int getIndentLevel() {
		return this.indentLevel;
	}
	
	/**
	 * Allow the indent level to be set directly.
	 */
	public void setIndentLevel(int indentLevel) {
		synchronized (this.lock) {
			this.indentLevel = indentLevel;
		}
	}

}
