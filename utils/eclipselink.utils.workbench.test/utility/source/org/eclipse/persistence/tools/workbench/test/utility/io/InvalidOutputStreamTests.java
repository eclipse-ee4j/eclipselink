/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.io.OutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.InvalidOutputStream;


public class InvalidOutputStreamTests extends TestCase {
	private OutputStream invalidOutputStream;

	public static Test suite() {
		return new TestSuite(InvalidOutputStreamTests.class);
	}
	
	public InvalidOutputStreamTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.invalidOutputStream = InvalidOutputStream.instance();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testClose() throws IOException {
		this.invalidOutputStream.close();
	}

	public void testFlush() throws IOException {
		this.invalidOutputStream.flush();
	}

	public void testWriteByteArray() throws IOException {
		byte[] b = new byte[10];
		boolean exCaught = false;
		try {
			this.invalidOutputStream.write(b);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testWriteByteArrayIntInt() throws IOException {
		byte[] b = new byte[10];
		boolean exCaught = false;
		try {
			this.invalidOutputStream.write(b, 3, 2);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testWriteInt() throws IOException {
		boolean exCaught = false;
		try {
			this.invalidOutputStream.write(77);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

}
