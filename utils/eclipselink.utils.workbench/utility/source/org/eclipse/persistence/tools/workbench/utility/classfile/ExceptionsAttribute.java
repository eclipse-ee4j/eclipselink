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
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models an exceptions attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 number_of_exceptions;
 *     u2[number_of_exceptions] exception_index_table
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ExceptionsAttribute extends Attribute {
	private short count;
	private short[] exceptionIndexes;


	/**
	 * Construct an exceptions attribute from the specified stream
	 * of byte codes.
	 */
	ExceptionsAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
		super(stream, nameIndex, pool);
	}
	
	void initializeInfo(ClassFileDataInputStream stream) throws IOException {
		this.count = stream.readU2();
		short cnt = this.count;
		this.exceptionIndexes = new short[cnt];
		short[] indexes = this.exceptionIndexes;
		for (short i = 0; i < cnt; i++) {
			indexes[i] = stream.readU2();
		}
	}

	void displayInfoStringOn(IndentingPrintWriter writer) {
		short cnt = this.count;
		for (short i = 0; i < cnt; i++) {
			writer.println(this.exceptionClassName(i));
		}
	}
	
	public String exceptionClassName(short index) {
		return this.constantPool().getClassConstant(this.exceptionIndexes[index]).name();
	}

	public String[] exceptionClassNames() {
		short cnt = this.count;
		if (cnt == 0) {
			return EMPTY_STRING_ARRAY;
		}
		String[] exceptionClassNames = new String[cnt];
		for (short i = 0; i < cnt; i++) {
			exceptionClassNames[i] = this.exceptionClassName(i);
		}
		return exceptionClassNames;
	}

	public void printThrowsClauseOn(PrintWriter writer) {
		short cnt = this.count;
		if (cnt == 0) {
			return;
		}
		writer.print(" throws ");
		for (short i = 0; i < cnt; i++) {
			if (i != 0) {
				writer.print(", ");
			}
			writer.print(this.exceptionClassName(i));
		}
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public short getCount() {
		return this.count;
	}
	
	public short[] getExceptionIndexes() {
		return this.exceptionIndexes;
	}

	public short getExceptionIndex(short index) {
		return this.exceptionIndexes[index];
	}

	void toString(StringBuffer sb) {
		sb.append(this.count);
		sb.append(" exception(s)");
	}

}
