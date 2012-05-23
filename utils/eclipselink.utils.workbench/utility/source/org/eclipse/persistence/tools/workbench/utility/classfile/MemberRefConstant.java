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

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file member ref constant:
 *     u1 tag;
 *     u2 class_index;
 *     u2 name_and_type_index;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public abstract class MemberRefConstant extends Constant {
	private short classIndex;
	private short nameAndTypeIndex;

	/**
	 * Construct a class file constant from the specified stream
	 * of byte codes.
	 */
	MemberRefConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		super(pool, tag, stream);
	}
	
	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.classIndex = stream.readU2();
		this.nameAndTypeIndex = stream.readU2();
	}
	
	public void displayStringOn(IndentingPrintWriter writer) {
		super.displayStringOn(writer);
		writer.print(" class: ");
		writer.print(this.className());
		writer.print("  name: ");
		writer.print(this.name());
		writer.print("  descriptor: ");
		writer.println(this.descriptor());
	}

	public String className() {
		return this.classConstant().name();
	}

	public ClassConstant classConstant() {
		return this.getPool().getClassConstant(this.classIndex);
	}

	public String name() {
		return this.nameAndTypeConstant().name();
	}

	public String descriptor() {
		return this.nameAndTypeConstant().descriptor();
	}

	public NameAndTypeConstant nameAndTypeConstant() {
		return this.getPool().getNameAndTypeConstant(this.nameAndTypeIndex);
	}

	public short getClassIndex() {
		return this.classIndex;
	}
	
	public short getNameAndTypeIndex() {
		return this.nameAndTypeIndex;
	}

	public Object value() {
		return this.className() + '.' + this.name() + ':' + this.descriptor();
	}

}
