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
package org.eclipse.persistence.tools.workbench.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * some stand-alone tests that make sure the CheckEOL code actually
 * works as expected
 */
public class CheckEOLTests extends TestCase {
	
	public static Test suite() {
		return new TestSuite(CheckEOLTests.class);
	}
	
	public CheckEOLTests(String name) {
		super(name);
	}
	
	public void testStreamHasInvalidWinEOL() throws IOException {
		byte CR = CheckEOL.cr;
		byte LF = CheckEOL.lf;
	
		// invalid EOLs
		this.verifyInvalidWinEOL(new byte[] {CR});
		this.verifyInvalidWinEOL(new byte[] {LF});
		this.verifyInvalidWinEOL(new byte[] {LF, CR});
		this.verifyInvalidWinEOL(new byte[] {CR, CR, LF});
		this.verifyInvalidWinEOL(new byte[] {CR, LF, LF});
		this.verifyInvalidWinEOL(new byte[] {CR, LF, CR});
	
		// valid EOLs
		this.verifyValidWinEOL(new byte[] {});
		this.verifyValidWinEOL(new byte[] {CR, LF});
		this.verifyValidWinEOL(new byte[] {CR, LF, CR, LF});
		this.verifyValidWinEOL(new byte[] {CR, LF, 'a', CR, LF});
	}
	
	private void verifyInvalidWinEOL(byte[] bytes) throws IOException {
		assertTrue("Invalid EOL not detected", new CheckEOL().streamHasInvalidWinEOL(this.buildInputStream(bytes)));
	}
	
	private void verifyValidWinEOL(byte[] bytes) throws IOException {
		assertTrue("Invalid EOL wrongly detected", ! new CheckEOL().streamHasInvalidWinEOL(this.buildInputStream(bytes)));
	}
	
	private InputStream buildInputStream(byte[] bytes) {
		return new ByteArrayInputStream(bytes);
	}
	
}
