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
import java.io.StringWriter;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models an exception handler:
 *     u2 start_pc;
 *     u2 end_pc;
 *     u2 handler_pc;
 *     u2 catch_type;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ExceptionHandler {
	private ConstantPool constantPool;
	private short startPC;
	private short endPC;
	private short handlerPC;
	private short catchType;

	/**
	 * Construct a local variable from the specified stream
	 * of byte codes.
	 */
	ExceptionHandler(ClassFileDataInputStream stream, ConstantPool constantPool) throws IOException {
		super();
		this.constantPool = constantPool;
		this.initialize(stream);
	}

	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.startPC = stream.readU2();
		this.endPC = stream.readU2();
		this.handlerPC = stream.readU2();
		this.catchType = stream.readU2();
	}

	public String displayString() {
		StringWriter sw = new StringWriter(1000);
		IndentingPrintWriter writer = new IndentingPrintWriter(sw);
		this.displayStringOn(writer);
		return sw.toString();
	}

	public void displayStringOn(IndentingPrintWriter writer) {
		writer.println(this.description());
		writer.indent();
		writer.print("PC start: ");
		writer.print(this.startPC);
		writer.print(" end: ");
		writer.print(this.endPC);
		writer.print(" handler: ");
		writer.println(this.handlerPC);
		writer.undent();
	}

	public String description() {
		short type = this.catchType;
		if (type == 0) {
			return "finally";
		}
		return "catch (" + this.className(type) + ")";
	}

	public String exceptionClassName() {
		return this.className(this.catchType);
	}

	private String className(short index) {
		return this.constantPool.getClassConstant(index).name();
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public ConstantPool getConstantPool() {
		return this.constantPool;
	}

	public short getStartPC() {
		return this.startPC;
	}

	public short getEndPC() {
		return this.endPC;
	}

	public short getHandlerPC() {
		return this.handlerPC;
	}

	public short getCatchType() {
		return this.catchType;
	}

	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.description() + ')';
	}

}
