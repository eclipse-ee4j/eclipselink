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
import java.io.PrintWriter;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a constant value attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 constantvalue_index;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ConstantValueAttribute extends Attribute {
	private short constantValueIndex;

	/**
	 * Construct a constant value attribute from the specified stream
	 * of byte codes.
	 */
	ConstantValueAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
		super(stream, nameIndex, pool);
	}

	void initializeInfo(ClassFileDataInputStream stream) throws IOException {
		this.constantValueIndex = stream.readU2();
	}

	void displayInfoStringOn(IndentingPrintWriter writer) {
		this.constantValue().displayStringOn(writer);
		writer.println();
	}

	public void printFieldInitializationClauseOn(PrintWriter writer) {
		writer.print(" = ");
		this.constantValue().printFieldInitializationClauseOn(writer);
	}

	public Object fieldConstantValue() {
		return this.constantValue().value();
	}

	public Constant constantValue() {
		return this.constantPool().get(this.constantValueIndex);
	}

	public LongConstant longValue() {
		return (LongConstant) this.constantValue();
	}

	public FloatConstant floatValue() {
		return (FloatConstant) this.constantValue();
	}

	public DoubleConstant doubleValue() {
		return (DoubleConstant) this.constantValue();
	}

	public IntegerConstant integerValue() {
		return (IntegerConstant) this.constantValue();
	}

	public StringConstant stringValue() {
		return (StringConstant) this.constantValue();
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public short getConstantValueIndex() {
		return this.constantValueIndex;
	}

	void toString(StringBuffer sb) {
		this.constantValue().toString(sb);
	}

}
