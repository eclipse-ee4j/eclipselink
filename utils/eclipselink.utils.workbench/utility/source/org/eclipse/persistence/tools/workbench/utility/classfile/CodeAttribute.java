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

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a code attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 max_stack;
 *     u2 max_locals;
 *     u4 code_length;
 *     u1[code_length] code;
 *     u2 exception_table_length;
 *     {
 *         u2 start_pc;
 *         u2 end_pc;
 *         u2 handler_pc;
 *         u2 catch_type;
 *     }[exception_table_length] exception_table;
 *     u2 attributes_count;
 *     attribute_info[attributes_count] attributes;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class CodeAttribute extends Attribute {
	private short maxStack;
	private short maxLocals;
	private int codeLength;
	private byte[] code;
	private short exceptionHandlerCount;
	private ExceptionHandler[] exceptionHandlers;
	private AttributePool attributePool;


	/**
	 * Construct a code attribute from the specified stream
	 * of byte codes.
	 */
	CodeAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
		super(stream, nameIndex, pool);
	}

	void initializeInfo(ClassFileDataInputStream stream) throws IOException {
		this.maxStack = stream.readU2();
		this.maxLocals = stream.readU2();

		this.codeLength = stream.readU4();
		int len = this.codeLength;
		this.code = new byte[len];
		stream.read(this.code);

		this.exceptionHandlerCount = stream.readU2();
		short cnt = this.exceptionHandlerCount;
		this.exceptionHandlers = new ExceptionHandler[cnt];
		ExceptionHandler[] handlers = this.exceptionHandlers;
		for (short i = 0; i < cnt; i++) {
			handlers[i] = new ExceptionHandler(stream, this.constantPool());
		}

		this.attributePool = new AttributePool(stream, this.classFile());
	}

	void displayInfoStringOn(IndentingPrintWriter writer) {
		writer.print("max stack: ");
		writer.println(this.maxStack);
		writer.print("max locals: ");
		writer.println(this.maxLocals);
		writer.print("code: ");
		this.writeHexStringOn(this.code, writer);
		writer.println();
		short count = this.exceptionHandlerCount;
		writer.print("Exception Handlers (count: ");
		writer.print(count);
		writer.println(")");
		writer.indent();
			ExceptionHandler[] handlers = this.exceptionHandlers;
			for (short i = 0; i < count; i++) {
				writer.print(i);
				writer.print(": ");
				handlers[i].displayStringOn(writer);
			}
		writer.undent();
		this.attributePool.displayStringOn(writer);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		short count = this.exceptionHandlerCount;
		ExceptionHandler[] handlers = this.exceptionHandlers;
		for (short i = 0; i < count; i++) {
			handlers[i].accept(visitor);
		}
		this.attributePool.accept(visitor);
	}

	public short getMaxStack() {
		return this.maxStack;
	}

	public short getMaxLocals() {
		return this.maxLocals;
	}

	public int getCodeLength() {
		return this.codeLength;
	}

	public byte[] getCode() {
		return this.code;
	}

	public short getExceptionHandlerCount() {
		return this.exceptionHandlerCount;
	}

	public ExceptionHandler getExceptionHandler(short index) {
		return this.exceptionHandlers[index];
	}

	public ExceptionHandler[] getExceptionHandlers() {
		return this.exceptionHandlers;
	}

	public AttributePool getAttributePool() {
		return this.attributePool;
	}

	void toString(StringBuffer sb) {
		sb.append("code length: ");
		sb.append(this.codeLength);
	}

}
