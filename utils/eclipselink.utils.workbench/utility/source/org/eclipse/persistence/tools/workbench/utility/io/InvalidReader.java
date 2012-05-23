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

import java.io.IOException;
import java.io.Reader;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This implementation of reader will throw an exception
 * any time it is read from. Closing the reader
 * will NOT trigger an exception.
 */
public final class InvalidReader
	extends Reader
{

	// singleton
	private static Reader INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized Reader instance() {
		if (INSTANCE == null) {
			INSTANCE = new InvalidReader();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private InvalidReader() {
		super();
	}

	/**
	 * @see java.io.Reader#read(char[], int, int)
	 */
	public int read(char[] cbuf, int off, int len) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.io.Reader#close()
	 */
	public void close() throws IOException {
		// do nothing
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}

}
