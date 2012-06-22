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

import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.FieldType;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file class (or interface) constant:
 *     u1 tag;
 *     u2 name_index;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ClassConstant extends Constant {
	private short nameIndex;
	private FieldType fieldDescriptor;		// lazy-initialized - so use the getter

	/**
	 * Construct a class file constant from the specified stream
	 * of byte codes.
	 */
	ClassConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		super(pool, tag, stream);
	}

	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.nameIndex = stream.readU2();
	}

	public void displayStringOn(IndentingPrintWriter writer) {
		super.displayStringOn(writer);
		writer.print(" name: ");
		writer.println(this.name());
	}

	public String description() {
		return "class";
	}

	/**
	 * Return the human-readable version of the class's name.
	 */
	public String name() {
		return this.getFieldDescriptor().declaration();
	}

	private String internalName() {
		return this.utf8String(this.nameIndex);
	}

	/**
	 * Return the name that matches the name returned by
	 * java.lang.Class.getName().
	 */
	public String javaTypeName() {
		return this.getFieldDescriptor().javaName();
	}

	/**
	 * Return the class's Java class, if possible.
	 */
	public Class javaType() throws ClassNotFoundException {
		return this.getFieldDescriptor().javaClass();
	}

	/**
	 * Return the class's array depth. If the class is
	 * not an array, the array depth is zero.
	 */
	public int arrayDepth() {
		return this.getFieldDescriptor().arrayDepth();
	}

	/**
	 * Return the class's element type name.
	 */
	public String elementTypeName() {
		return this.getFieldDescriptor().elementTypeName();
	}

	public short getNameIndex() {
		return this.nameIndex;
	}

	public Object value() {
		return this.name();
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		this.getFieldDescriptor().accept(visitor);
	}

	public FieldType getFieldDescriptor() {
		if (this.fieldDescriptor == null) {
			this.fieldDescriptor = FieldType.createFieldTypeForClassConstant(this.internalName());
		}
		return this.fieldDescriptor;
	}

}
