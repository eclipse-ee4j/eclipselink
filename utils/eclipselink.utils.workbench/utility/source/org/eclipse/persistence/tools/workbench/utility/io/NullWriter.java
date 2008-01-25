/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.io;

import java.io.Writer;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This is a writer that does nothing.
 * Everything is thrown into the "bit bucket".
 * Performance should be pretty good....
 */
public final class NullWriter extends Writer {

	// singleton
	private static Writer INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized Writer instance() {
		if (INSTANCE == null) {
			INSTANCE = new NullWriter();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullWriter() {
		super();
	}
	
	/**
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] cbuf, int off, int len) {
		// do nothing
	}
	
	/**
	 * @see java.io.Writer#flush()
	 */
	public void flush() {
		// do nothing
	}
	
	/**
	 * @see java.io.Writer#close()
	 */
	public void close() {
		// do nothing
	}
	
	/**
	 * @see java.io.Writer#write(char[])
	 */
	public void write(char[] cbuf) {
		// do nothing
	}
	
	/**
	 * @see java.io.Writer#write(int)
	 */
	public void write(int c) {
		// do nothing
	}
	
	/**
	 * @see java.io.Writer#write(String, int, int)
	 */
	public void write(String str, int off, int len) {
		// do nothing
	}
	
	/**
	 * @see java.io.Writer#write(String)
	 */
	public void write(String str) {
		// do nothing
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}

}
