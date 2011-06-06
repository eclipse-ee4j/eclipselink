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
package org.eclipse.persistence.tools.workbench.utility.classfile.tools;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Add a few helper methods to DataInputStream.
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ClassFileDataInputStream extends DataInputStream {

	/**
	 * Public constructor.
	 */
	public ClassFileDataInputStream(InputStream in) {
		super(in);
	}

	public final byte readU1() throws IOException {
		return (byte) this.readUnsignedByte();
	}

	public final short readU2() throws IOException {
		return (short) this.readUnsignedShort();
	}

	public final int readU4() throws IOException {
		return this.readInt();
	}

}
