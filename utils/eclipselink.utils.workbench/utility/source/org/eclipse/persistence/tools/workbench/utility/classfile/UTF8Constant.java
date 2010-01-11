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
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file UTF8 constant:
 *     u1 tag;
 *     u2 length;
 *     u1[] bytes;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class UTF8Constant extends Constant {
	private String value;

	/**
	 * Construct a class file constant from the specified stream
	 * of byte codes.
	 */
	UTF8Constant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		super(pool, tag, stream);
	}
	
	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.value = this.readStringFrom(stream);
	}
	
	private String readStringFrom(ClassFileDataInputStream stream) throws IOException {
		short len = stream.readU2();
		StringBuffer sb = new StringBuffer(len);
		for (short count = 0; count < len; count++) {
			byte b = stream.readU1();
			if ((b & 0x80) == 0) {
				// if the first bit is 0, we have a single-byte character
				sb.append((char) b);
			} else {
				// if the first bit is 1, we have a multi-byte character
				if ((b & 0x40) == 0) {
					// if the second bit is 0 at this point, something is wrong...
					throw this.badByte(b);
				}
				short s;
				if ((b & 0x20) == 0) {
					// if the third bit is 0, we have a 2-byte character
					s = (short) ((b & 0x1F) << 6);		// bits 4-0 become bits 10-6
				} else {
					// if the third bit is 1, we have a 3-byte character
					s = (short) ((b & 0x0F) << 12);		// bits 3-0 become bits 15-12
					b = stream.readU1();
					this.checkContinuation(b);
					count++;
					s |= ((b & 0x3f) << 6);		// bits 5-0 become bits 11-6
				}
				b = stream.readU1();
				this.checkContinuation(b);
				count++;
				s |= (b & 0x3f);		// bits 5-0 become bits 5-0
				sb.append((char) s);
			}
		}
		return sb.toString();
	}
	
	private final IOException badByte(byte b) {
		return new IOException("Invalid UTF-8 byte: " + Byte.toString(b));
	}
	
	private final void checkContinuation(byte b) throws IOException {
		// the first bit must be 1, the second bit must be 0
		if (((b & 0x80) == 0) || ((b & 0x40) != 0)) {
			throw this.badByte(b);
		}
	}
	
	public void displayStringOn(IndentingPrintWriter writer) {
		super.displayStringOn(writer);
		writer.print(" value: \"");
		writer.print(this.value);
		writer.println("\"");
	}
	
	public String description() {
		return "UTF8";
	}
	
	public String string() {
		return this.value;
	}

	public Object value() {
		return this.value;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
