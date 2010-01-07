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
import java.io.Writer;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.NullWriter;


public class NullWriterTests extends TestCase {
	private Writer nullWriter;

	public static Test suite() {
		return new TestSuite(NullWriterTests.class);
	}
	
	public NullWriterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.nullWriter = NullWriter.instance();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testWriteCharArrayIntInt() throws IOException {
		this.nullWriter.write(new char[10], 2, 5);
	}

	public void testFlush() throws IOException {
		this.nullWriter.flush();
	}

	public void testClose() throws IOException {
		this.nullWriter.close();
	}

	public void testWriteCharArray() throws IOException {
		char[] charArray = new char[10];
		Arrays.fill(charArray, 'a');
		this.nullWriter.write(charArray);
	}

	public void testWriteInt() throws IOException {
		this.nullWriter.write(10);
	}

	public void testWriteStringIntInt() throws IOException {
		this.nullWriter.write("0123456789", 2, 5);
	}

	public void testWriteString() throws IOException {
		this.nullWriter.write("0123456789");
	}

}
