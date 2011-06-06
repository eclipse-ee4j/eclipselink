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
package org.eclipse.persistence.tools.workbench.test.utility.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.NullInputStream;


public class NullInputStreamTests extends TestCase {
	private InputStream nullInputStream;

	public static Test suite() {
		return new TestSuite(NullInputStreamTests.class);
	}
	
	public NullInputStreamTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.nullInputStream = NullInputStream.instance();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testAvailable() throws IOException {
		assertEquals(0, this.nullInputStream.available());
	}

	public void testClose() throws IOException {
		this.nullInputStream.close();
	}

	public void testMark() {
		this.nullInputStream.mark(5);
	}

	public void testMarkSupported() {
		assertFalse(this.nullInputStream.markSupported());
	}

	public void testRead() throws IOException {
		assertEquals(-1, this.nullInputStream.read());
	}

	public void testReadByteArray() throws IOException {
		byte[] expected = new byte[10];
		Arrays.fill(expected, (byte) 7);
		byte[] actual = new byte[10];
		Arrays.fill(actual, (byte) 7);
		assertEquals(-1, this.nullInputStream.read(actual));
		assertTrue(Arrays.equals(actual, expected));
	}

	public void testReadByteArrayIntInt() throws IOException {
		byte[] expected = new byte[10];
		Arrays.fill(expected, (byte) 7);
		byte[] actual = new byte[10];
		Arrays.fill(actual, (byte) 7);
		assertEquals(-1, this.nullInputStream.read(actual, 2, 5));
		assertTrue(Arrays.equals(actual, expected));
	}

	public void testReset() {
		boolean exCaught = false;
		try {
			this.nullInputStream.reset();
		} catch (IOException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testSkip() throws IOException {
		assertEquals(0, this.nullInputStream.skip(5));
	}

}
