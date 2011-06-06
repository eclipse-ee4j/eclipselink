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
 * This class models a class file string constant:
 *     u1 tag;
 *     u2 string_index;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class StringConstant extends Constant {
	private short stringIndex;

	/**
	 * Construct a class file constant from the specified stream
	 * of byte codes.
	 */
	StringConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		super(pool, tag, stream);
	}
	
	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.stringIndex = stream.readU2();
	}
	
	public void displayStringOn(IndentingPrintWriter writer) {
		super.displayStringOn(writer);
		writer.print(" string: ");
		writer.println(this.string());
	}
	
	public String description() {
		return "String";
	}
	
	public void printFieldInitializationClauseOn(PrintWriter writer) {
		writer.print('"');
		writer.print(this.string());
		writer.print('"');
	}

	public String string() {
		return this.utf8String(this.stringIndex);
	}

	public short getStringIndex() {
		return this.stringIndex;
	}

	public Object value() {
		return this.string();
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
