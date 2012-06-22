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
import java.io.Writer;

/**
 * A "tee" writer forwards the character data written to it
 * to two streams.
 */
public class TeeWriter extends Writer {
	private Writer writer1;
	private Writer writer2;


	/**
	 * Construct a "tee" writer that writes to the specified writers.
	 */
	public TeeWriter(Writer writer1, Writer writer2) {
		super();
		this.writer1 = writer1;
		this.writer2 = writer2;
	}

	/**
	 * Construct a "tee" writer that writes to the specified writers and
	 * locks on the specified object.
	 */
	public TeeWriter(Object lock, Writer writer1, Writer writer2) {
		super(lock);
		this.writer1 = writer1;
		this.writer2 = writer2;
	}

	/**
	 * @see java.io.Writer#write(int)
	 */
	public void write(int c) throws IOException {
		this.writer1.write(c);
		this.writer2.write(c);
	}

	/**
	 * @see java.io.Writer#write(char[])
	 */
	public void write(char[] cbuf) throws IOException {
		this.writer1.write(cbuf);
		this.writer2.write(cbuf);
	}

	/**
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] cbuf, int off, int len) throws IOException {
		this.writer1.write(cbuf, off, len);
		this.writer2.write(cbuf, off, len);
	}

	/**
	 * @see java.io.Writer#write(java.lang.String)
	 */
	public void write(String str) throws IOException {
		this.writer1.write(str);
		this.writer2.write(str);
	}

	/**
	 * @see java.io.Writer#write(java.lang.String, int, int)
	 */
	public void write(String str, int off, int len) throws IOException {
		this.writer1.write(str, off, len);
		this.writer2.write(str, off, len);
	}

	/**
	 * @see java.io.Writer#flush()
	 */
	public void flush() throws IOException {
		this.writer1.flush();
		this.writer2.flush();
	}

	/**
	 * @see java.io.Writer#close()
	 */
	public void close() throws IOException {
		this.writer1.close();
		this.writer2.close();
	}

}
