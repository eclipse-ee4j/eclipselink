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
 * This class models a class file "name and type" constant:
 *     u1 tag;
 *     u2 name_index;
 *     u2 descriptor_index;
 * 
 * The "type" is not really a "type", but either a "field descriptor" or a
 * "method descriptor"; so it may actually contain multiple "types".
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class NameAndTypeConstant extends Constant {
	private short nameIndex;
	private short descriptorIndex;

	/**
	 * Construct a class file constant from the specified stream
	 * of byte codes.
	 */
	NameAndTypeConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		super(pool, tag, stream);
	}
	
	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.nameIndex = stream.readU2();
		this.descriptorIndex = stream.readU2();
	}
	
	public void displayStringOn(IndentingPrintWriter writer) {
		super.displayStringOn(writer);
		writer.print(" name: ");
		writer.print(this.name());
		writer.print("  descriptor: ");
		writer.println(this.descriptor());
	}
	
	public String description() {
		return "name & type";
	}

	public String name() {
		return this.utf8String(this.nameIndex);
	}

	public String descriptor() {
		return this.utf8String(this.descriptorIndex);
	}

	public short getNameIndex() {
		return this.nameIndex;
	}
	
	public short getDescriptorIndex() {
		return this.descriptorIndex;
	}

	public Object value() {
		return this.name() + ':' + this.descriptor();
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
