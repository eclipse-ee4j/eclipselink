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
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file long constant:
 *     u1 tag;
 *     u4 high_bytes;
 *     u4 low_bytes;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class LongConstant extends Constant {
	private long value;

	/**
	 * Construct a class file constant from the specified stream
	 * of byte codes.
	 */
	LongConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		super(pool, tag, stream);
	}
	
	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.value = stream.readLong();
	}
	
	public void displayStringOn(IndentingPrintWriter writer) {
		super.displayStringOn(writer);
		writer.print(" value: ");
		writer.println(this.value);
	}
	
	public String description() {
		return "long";
	}
	
	public void printFieldInitializationClauseOn(PrintWriter writer) {
		writer.print(this.value);
		writer.print('L');
	}

	boolean consumesTwoPoolEntries() {
		return true;
	}
	
	public long longValue() {
		return this.value;
	}

	public Object value() {
		return new Long(this.value);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
