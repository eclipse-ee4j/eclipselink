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
 * This class models a class file constant pool:
 *     u2 constant_pool_count;
 *     cp_info[constant_pool_count-1] constant_pool;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ConstantPool {
	private short count;
	private Constant[] constants;


	/**
	 * Construct a class file constant pool from the specified stream
	 * of byte codes.
	 */
	ConstantPool(ClassFileDataInputStream stream) throws IOException {
		super();
		this.initialize(stream);
	}
	
	private void initialize(ClassFileDataInputStream stream) throws IOException {
		this.count = stream.readU2();
		short cnt = this.count;
		this.constants = new Constant[cnt];
		Constant[] localConstants = this.constants;
		// start at 1 - the first entry is not present in the .class file...
		localConstants[0] = Constant.nullConstant(this, stream);
		for (short i = 1; i < cnt; i++) {
			Constant constant = Constant.buildConstant(this, stream);
			localConstants[i] = constant;
			if (constant.consumesTwoPoolEntries()) {
				i++;
				localConstants[i] = Constant.nullConstant(this, stream);
			}
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
		Constant[] localConstants = this.constants;
		writer.print("Constant Pool (count: ");
		writer.print(cnt);
		writer.println(')');
		writer.indent();
		for (short i = 0; i < cnt; i++) {
			writer.print(i);
			writer.print(": ");
			localConstants[i].displayStringOn(writer);
		}
		writer.undent();
	}
	
	public void accept(Visitor visitor) {
		visitor.visit(this);
		short cnt = this.count;
		Constant[] localConstants = this.constants;
		for (short i = 0; i < cnt; i++) {
			localConstants[i].accept(visitor);
		}
	}

	public short getCount() {
		return this.count;
	}
	
	public Constant get(short index) {
		return this.constants[index];
	}
	
	public ClassConstant getClassConstant(short index) {
		return (ClassConstant) this.constants[index];
	}
	
	public DoubleConstant getDoubleConstant(short index) {
		return (DoubleConstant) this.constants[index];
	}
	
	public FloatConstant getFloatConstant(short index) {
		return (FloatConstant) this.constants[index];
	}
	
	public IntegerConstant getIntegerConstant(short index) {
		return (IntegerConstant) this.constants[index];
	}
	
	public LongConstant getLongConstant(short index) {
		return (LongConstant) this.constants[index];
	}
	
	public NameAndTypeConstant getNameAndTypeConstant(short index) {
		return (NameAndTypeConstant) this.constants[index];
	}
	
	public FieldRefConstant getFieldRefConstant(short index) {
		return (FieldRefConstant) this.constants[index];
	}
	
	public InterfaceMethodRefConstant getInterfaceMethodRefConstant(short index) {
		return (InterfaceMethodRefConstant) this.constants[index];
	}
	
	public MethodRefConstant getMethodRefConstant(short index) {
		return (MethodRefConstant) this.constants[index];
	}
	
	public StringConstant getStringConstant(short index) {
		return (StringConstant) this.constants[index];
	}
	
	public UTF8Constant getUTF8Constant(short index) {
		return (UTF8Constant) this.constants[index];
	}
	
	public String getUTF8String(short index) {
		return this.getUTF8Constant(index).string();
	}

	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.count + " constant(s))";
	}

}
