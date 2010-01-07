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
 * This class models a local variable type table attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 local_variable_type_table_length;
 *     {
 *         u2 start_pc;
 *         u2 length;
 *         u2 name_index;
 *         u2 signature_index;
 *         u2 index;
 *     }[local_variable_type_table_length] local_variable_type_table;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class LocalVariableTypeTableAttribute extends Attribute {
	private short count;
	private LocalVariableType[] localVariableTypes;

	/**
	 * Construct a local variable type table attribute from the specified stream
	 * of byte codes.
	 */
	public LocalVariableTypeTableAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
		super(stream, nameIndex, pool);
	}

	void initializeInfo(ClassFileDataInputStream stream) throws IOException {
		this.count = stream.readU2();
		short cnt = this.count;
		this.localVariableTypes = new LocalVariableType[cnt];
		LocalVariableType[] types = this.localVariableTypes;
		for (short i = 0; i < cnt; i++) {
			types[i] = new LocalVariableType(stream, this.constantPool());
		}
	}

	public void displayNameOn(IndentingPrintWriter writer) {
		super.displayNameOn(writer);
		writer.print(" (count: ");
		writer.print(this.count);
		writer.print(")");
	}
	
	void displayInfoStringOn(IndentingPrintWriter writer) {
		short cnt = this.count;
		LocalVariableType[] types = this.localVariableTypes;
		for (short i = 0; i < cnt; i++) {
			writer.print(i);
			writer.print(": ");
			types[i].displayStringOn(writer);
		}
	}

	public String localVariableTypeName(short index) {
		return this.localVariableTypes[index].name();
	}

	public LocalVariableType localVariableTypeNamed(String name) {
		short cnt = this.count;
		LocalVariableType[] types = this.localVariableTypes;
		for (short i = 0; i < cnt; i++) {
			LocalVariableType type = types[i];
			if (type.isNamed(name)) {
				return type;
			}
		}
		return null;
	}

	public String[] localVariableTypeNames() {
		short cnt = this.count;
		if (cnt == 0) {
			return EMPTY_STRING_ARRAY;
		}
		String[] names = new String[cnt];
		for (short i = 0; i < cnt; i++) {
			names[i] = this.localVariableTypeName(i);
		}
		return names;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		short cnt = this.count;
		LocalVariableType[] types = this.localVariableTypes;
		for (short i = 0; i < cnt; i++) {
			types[i].accept(visitor);
		}
	}

	public short getCount() {
		return this.count;
	}

	public LocalVariableType[] getLocalVariableTypes() {
		return this.localVariableTypes;
	}

	public LocalVariableType getLocalVariableType(short index) {
		return this.localVariableTypes[index];
	}

	void toString(StringBuffer sb) {
		sb.append(this.count);
		sb.append(" local variable type(s)");
	}

}
