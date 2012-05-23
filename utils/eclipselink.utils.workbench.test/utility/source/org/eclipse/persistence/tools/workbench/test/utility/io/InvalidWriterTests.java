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
package org.eclipse.persistence.tools.workbench.test.utility.io;

import java.io.IOException;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.InvalidWriter;


public class InvalidWriterTests extends TestCase {
	private Writer invalidWriter;

	public static Test suite() {
		return new TestSuite(InvalidWriterTests.class);
	}
	
	public InvalidWriterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.invalidWriter = InvalidWriter.instance();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testClose() throws IOException {
		this.invalidWriter.close();
	}

	public void testFlush() throws IOException {
		this.invalidWriter.flush();
	}

	public void testWriteCharArray() throws IOException {
		char[] cbuf = new char[10];
		boolean exCaught = false;
		try {
			this.invalidWriter.write(cbuf);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testWriteCharArrayIntInt() throws IOException {
		char[] cbuf = new char[10];
		boolean exCaught = false;
		try {
			this.invalidWriter.write(cbuf, 3, 2);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testWriteInt() throws IOException {
		boolean exCaught = false;
		try {
			this.invalidWriter.write(77);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testWriteString() throws IOException {
		boolean exCaught = false;
		try {
			this.invalidWriter.write("cbuf");
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testWriteStringIntInt() throws IOException {
		boolean exCaught = false;
		try {
			this.invalidWriter.write("cbuf", 1, 2);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

}
