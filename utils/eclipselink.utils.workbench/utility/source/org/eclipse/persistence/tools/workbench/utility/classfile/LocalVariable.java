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
import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.FieldType;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a local variable:
 *     u2 start_pc;
 *     u2 length;
 *     u2 name_index;
 *     u2 descriptor_index;
 *     u2 index;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class LocalVariable {
	private ConstantPool constantPool;
	private short startPC;	// index of the opcode in the code array
	private short length;	// length of code in the code array
	private short nameIndex;
	private short descriptorIndex;
	private short index;

	private FieldType fieldDescriptor;

	/**
	 * Construct a local variable from the specified stream
	 * of byte codes.
	 */
	LocalVariable(ClassFileDataInputStream stream, ConstantPool constantPool) throws IOException {
		super();
		this.constantPool = constantPool;
		this.initialize(stream);
	}

	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.startPC = stream.readU2();
		this.length = stream.readU2();
		this.nameIndex = stream.readU2();
		this.descriptorIndex = stream.readU2();
		this.index = stream.readU2();
	}

	public String displayString() {
		StringWriter sw = new StringWriter(1000);
		IndentingPrintWriter writer = new IndentingPrintWriter(sw);
		this.displayStringOn(writer);
		return sw.toString();
	}

	public void displayStringOn(IndentingPrintWriter writer) {
		this.getFieldDescriptor().printDeclarationOn(writer);
		writer.print(' ');
		writer.println(this.name());
		writer.indent();
		writer.print("PC start: ");
		writer.print(this.startPC);
		writer.print(" length: ");
		writer.println(this.length);
//		writer.print("descriptor: ");
//		writer.println(this.descriptor());
		writer.undent();
	}

	public boolean isNamed(String name) {
		return this.name().equals(name);
	}

	public String name() {
		return this.constantPool.getUTF8String(this.nameIndex);
	}

	public String descriptor() {
		return this.constantPool.getUTF8String(this.descriptorIndex);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		this.getFieldDescriptor().accept(visitor);
	}

	public FieldType getFieldDescriptor() {
		if (this.fieldDescriptor == null) {
			this.fieldDescriptor = FieldType.createFieldType(this.descriptor());
		}
		return this.fieldDescriptor;
	}

	public ConstantPool getConstantPool() {
		return this.constantPool;
	}

	public short getStartPC() {
		return this.startPC;
	}

	public short getLength() {
		return this.length;
	}

	public short getNameIndex() {
		return this.nameIndex;
	}

	public short getDescriptorIndex() {
		return this.descriptorIndex;
	}

	public short getIndex() {
		return this.index;
	}

	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.name() + ')';
	}

}
