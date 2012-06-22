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
 * This class models a local variable table attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 local_variable_table_length;
 *     {
 *         u2 start_pc;
 *         u2 length;
 *         u2 name_index;
 *         u2 descriptor_index;
 *         u2 index;
 *     }[local_variable_table_length] local_variable_table;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class LocalVariableTableAttribute extends Attribute {
	private short count;
	private LocalVariable[] localVariables;

	/**
	 * Construct a local variable table attribute from the specified stream
	 * of byte codes.
	 */
	LocalVariableTableAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
		super(stream, nameIndex, pool);
	}

	void initializeInfo(ClassFileDataInputStream stream) throws IOException {
		this.count = stream.readU2();
		short cnt = this.count;
		this.localVariables = new LocalVariable[cnt];
		LocalVariable[] variables = this.localVariables;
		for (short i = 0; i < cnt; i++) {
			variables[i] = new LocalVariable(stream, this.constantPool());
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
		LocalVariable[] variables = this.localVariables;
		for (short i = 0; i < cnt; i++) {
			writer.print(i);
			writer.print(": ");
			variables[i].displayStringOn(writer);
		}
	}

	public String localVariableName(short index) {
		return this.localVariables[index].name();
	}

	public LocalVariable localVariableNamed(String name) {
		short cnt = this.count;
		LocalVariable[] variables = this.localVariables;
		for (short i = 0; i < cnt; i++) {
			LocalVariable localVariable = variables[i];
			if (localVariable.isNamed(name)) {
				return localVariable;
			}
		}
		return null;
	}

	public String[] localVariableNames() {
		short cnt = this.count;
		if (cnt == 0) {
			return EMPTY_STRING_ARRAY;
		}
		String[] localVariableNames = new String[cnt];
		for (short i = 0; i < cnt; i++) {
			localVariableNames[i] = this.localVariableName(i);
		}
		return localVariableNames;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		short cnt = this.count;
		LocalVariable[] variables = this.localVariables;
		for (short i = 0; i < cnt; i++) {
			variables[i].accept(visitor);
		}
	}

	public short getCount() {
		return this.count;
	}

	public LocalVariable[] getLocalVariables() {
		return this.localVariables;
	}

	public LocalVariable getLocalVariable(short index) {
		return this.localVariables[index];
	}

	void toString(StringBuffer sb) {
		sb.append(this.count);
		sb.append(" local variable(s)");
	}

}
