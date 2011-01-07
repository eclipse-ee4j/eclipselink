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
import java.io.Reader;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.NullReader;


public class NullReaderTests extends TestCase {
	private Reader nullReader;

	public static Test suite() {
		return new TestSuite(NullReaderTests.class);
	}
	
	public NullReaderTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.nullReader = NullReader.instance();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testClose() throws IOException {
		this.nullReader.close();
	}

	public void testRead() throws IOException {
		assertEquals(-1, this.nullReader.read());
	}

	public void testReadCharArray() throws IOException {
		char[] expected = new char[10];
		Arrays.fill(expected, 'a');
		char[] actual = new char[10];
		Arrays.fill(actual, 'a');
		assertEquals(-1, this.nullReader.read(actual));
		assertTrue(Arrays.equals(actual, expected));
	}

	public void testReadCharArrayIntInt() throws IOException {
		char[] expected = new char[10];
		Arrays.fill(expected, 'a');
		char[] actual = new char[10];
		Arrays.fill(actual, 'a');
		assertEquals(-1, this.nullReader.read(actual, 2, 5));
		assertTrue(Arrays.equals(actual, expected));
	}

	public void testSkip() throws IOException {
		assertEquals(0, this.nullReader.skip(5));
	}

	public void testMark() {
		boolean exCaught = false;
		try {
			this.nullReader.mark(5);
		} catch (IOException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testMarkSupported() {
		assertFalse(this.nullReader.markSupported());
	}

	public void testReady() {
		assertFalse(this.nullReader.markSupported());
	}

	public void testReset() {
		boolean exCaught = false;
		try {
			this.nullReader.reset();
		} catch (IOException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

}
