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
import java.io.StringWriter;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file constant:
 *     u1 tag;
 *     u1[] info;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public abstract class Constant {

	/** the pool the constant is in */
	private ConstantPool pool;

	/** the constant's tag, which determines its type */
	private byte tag;

	/** constants defined in "The Java Virtual Machine Specification" */
	public static final byte CONSTANT_Class = 7;
	public static final byte CONSTANT_Fieldref = 9;
	public static final byte CONSTANT_Methodref = 10;
	public static final byte CONSTANT_InterfaceMethodref = 11;
	public static final byte CONSTANT_String = 8;
	public static final byte CONSTANT_Integer = 3;
	public static final byte CONSTANT_Float = 4;
	public static final byte CONSTANT_Long = 5;
	public static final byte CONSTANT_Double = 6;
	public static final byte CONSTANT_NameAndType = 12;
	public static final byte CONSTANT_Utf8 = 1;

	/**
	 * Return a null class file constant.
	 */
	static Constant nullConstant(ConstantPool pool, ClassFileDataInputStream stream) throws IOException {
		return new NullConstant(pool, (byte) 0, stream);
	}
	
	/**
	 * Construct a class file constant from the specified stream
	 * of byte codes.
	 */
	static Constant buildConstant(ConstantPool pool, ClassFileDataInputStream stream) throws IOException {
		return buildConstant(pool, stream.readU1(), stream);
	}
	
	private static Constant buildConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		switch (tag) {
			case CONSTANT_Utf8:
				return new UTF8Constant(pool, tag, stream);
			case CONSTANT_Integer:
				return new IntegerConstant(pool, tag, stream);
			case CONSTANT_Float:
				return new FloatConstant(pool, tag, stream);
			case CONSTANT_Long:
				return new LongConstant(pool, tag, stream);
			case CONSTANT_Double:
				return new DoubleConstant(pool, tag, stream);
			case CONSTANT_Class:
				return new ClassConstant(pool, tag, stream);
			case CONSTANT_String:
				return new StringConstant(pool, tag, stream);
			case CONSTANT_Fieldref:
				return new FieldRefConstant(pool, tag, stream);
			case CONSTANT_Methodref:
				return new MethodRefConstant(pool, tag, stream);
			case CONSTANT_InterfaceMethodref:
				return new InterfaceMethodRefConstant(pool, tag, stream);
			case CONSTANT_NameAndType:
				return new NameAndTypeConstant(pool, tag, stream);
			default:
				throw new IOException("Invalid constant tag: " + tag);
		}
	}
	
	/**
	 * Construct a class file constant from the specified stream
	 * of byte codes.
	 */
	Constant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		super();
		this.pool = pool;
		this.tag = tag;
		this.initialize(stream);
	}
	
	abstract void initialize(ClassFileDataInputStream stream) throws IOException;
	
	public String displayString() {
		StringWriter sw = new StringWriter(100);
		IndentingPrintWriter writer = new IndentingPrintWriter(sw);
		this.displayStringOn(writer);
		return sw.toString();
	}
	
	public void displayStringOn(IndentingPrintWriter writer) {
		writer.print("tag: ");
		writer.print(this.tag);
		writer.print(" (");
		writer.print(this.description());
		writer.print(")");
	}
	
	public abstract String description();
	
	public void printFieldInitializationClauseOn(PrintWriter writer) {
		throw new UnsupportedOperationException(this.getClass().getName());
	}

	public ConstantPool getPool() {
		return this.pool;
	}
	
	public byte getTag() {
		return this.tag;
	}
	
	public String utf8String(short index) {
		return this.pool.getUTF8String(index);
	}
	
	/**
	 * "In retrospect, making 8-byte constants take
	 * two constant pool entries was a poor choice."
	 */
	boolean consumesTwoPoolEntries() {
		return false;
	}

	/**
	 * Return the value of the constant.
	 */
	public abstract Object value();

	public abstract void accept(Visitor visitor);

	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(ClassTools.shortClassNameForObject(this));
		sb.append('(');
		this.toString(sb);
		sb.append(')');
		return sb.toString();
	}

	void toString(StringBuffer sb) {
		sb.append(this.value());
	}

}
