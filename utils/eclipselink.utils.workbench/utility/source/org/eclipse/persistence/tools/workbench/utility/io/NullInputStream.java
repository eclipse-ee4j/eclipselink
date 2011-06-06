/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.io.InputStream;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This is an input stream that does nothing.
 * It returns nothing.
 * Performance should be pretty good....
 */
public final class NullInputStream
	extends InputStream
{

	// singleton
	private static InputStream INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized InputStream instance() {
		if (INSTANCE == null) {
			INSTANCE = new NullInputStream();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullInputStream() {
		super();
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		return -1;
	}

	/**
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] b) throws IOException {
		return -1;
	}

	/**
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		return -1;
	}

	/**
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		return 0;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}

}
