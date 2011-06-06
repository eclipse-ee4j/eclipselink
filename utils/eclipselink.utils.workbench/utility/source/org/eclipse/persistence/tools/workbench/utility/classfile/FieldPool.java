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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file field pool:
 *     u2 fields_count;
 *     field_info[fields_count] fields;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class FieldPool implements Member.Pool {
	private ClassFile classFile;
	private short count;
	private Field[] fields;


	/**
	 * Construct a class file field pool from the specified stream
	 * of byte codes.
	 */
	FieldPool(ClassFileDataInputStream stream, ClassFile classFile) throws IOException {
		super();
		this.classFile = classFile;
		this.initialize(stream);
	}

	private void initialize(ClassFileDataInputStream stream) throws IOException {
		this.count = stream.readU2();
		short cnt = this.count;
		this.fields = new Field[cnt];
		Field[] localFields = this.fields;
		for (short i = 0; i < cnt; i++) {
			localFields[i] = new Field(stream, this);
		}
	}

	public String displayString() {
		StringWriter sw = new StringWriter(1000);
		IndentingPrintWriter writer = new IndentingPrintWriter(sw);
		this.displayStringOn(writer);
		return sw.toString();
	}

	public void displayStringOn(IndentingPrintWriter writer) {
		short cnt = this.count;
		Field[] localFields = this.fields;
		writer.print("Field Pool (count: ");
		writer.print(cnt);
		writer.println(')');
		writer.indent();
		for (short i = 0; i < cnt; i++) {
			writer.print(i);
			writer.print(": ");
			localFields[i].displayStringOn(writer);
		}
		writer.undent();
	}

	public Field fieldNamed(String fieldName) {
		short cnt = this.count;
		Field[] localFields = this.fields;
		for (short i = 0; i < cnt; i++) {
			Field field = localFields[i];
			if (field.name().equals(fieldName)) {
				return field;
			}
		}
		throw new IllegalArgumentException(fieldName);
	}

	public Field[] declaredFields() {
		short cnt = this.count;
		Field[] localFields = this.fields;
		Collection declaredFields = new ArrayList(cnt);
		for (short i = 0; i < cnt; i++) {
			Field field = localFields[i];
			if (field.isDeclaredField()) {
				declaredFields.add(field);
			}
		}
		return (Field[]) declaredFields.toArray(new Field[declaredFields.size()]);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		short cnt = this.count;
		Field[] localFields = this.fields;
		for (short i = 0; i < cnt; i++) {
			localFields[i].accept(visitor);
		}
	}

	public ClassFile getClassFile() {
		return this.classFile;
	}

	public short getCount() {
		return this.count;
	}

	public Field get(short index) {
		return this.fields[index];
	}

	public Field[] getFields() {
		return this.fields;
	}

	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.count + " field(s))";
	}

}
