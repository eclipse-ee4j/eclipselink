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

import java.io.InputStream;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This implementation of input stream will throw an exception
 * any time it is read from. Marking or closing the stream
 * will NOT trigger an exception.
 */
public final class InvalidInputStream
	extends InputStream
{

	// singleton
	private static InputStream INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized InputStream instance() {
		if (INSTANCE == null) {
			INSTANCE = new InvalidInputStream();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private InvalidInputStream() {
		super();
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	public int read() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}

}
