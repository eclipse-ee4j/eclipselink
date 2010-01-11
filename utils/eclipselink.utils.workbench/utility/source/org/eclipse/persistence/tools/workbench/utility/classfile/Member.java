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
import java.lang.reflect.Modifier;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file member (field or method):
 *     u2 access_flags;
 *     u2 name_index;
 *     u2 descriptor_index;
 *     u2 attributes_count;
 *     attribute_info[attributes_count] attributes;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public abstract class Member {
	private Pool pool;
	private short accessFlags;
	/** these flags will match those returned by java.lang.Field/Method#getModifiers() */
	private short standardAccessFlags;
	private short nameIndex;
	private short descriptorIndex;
	private AttributePool attributePool;

	/** constants defined in "The Java Virtual Machine Specification" */
	public static final short ACC_SYNTHETIC = 0x1000;


	/**
	 * Construct a class file member from the specified stream
	 * of byte codes.
	 */
	Member(ClassFileDataInputStream stream, Pool pool) throws IOException {
		super();
		this.pool = pool;
		this.initialize(stream);
	}

	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.accessFlags = stream.readU2();
		this.standardAccessFlags = this.buildStandardAccessFlags();
		this.nameIndex = stream.readU2();
		this.descriptorIndex = stream.readU2();
		this.attributePool = new AttributePool(stream, this.classFile());
	}

	private short buildStandardAccessFlags() {
		short result = this.accessFlags;
		// clear any unsupported bits
		result &= this.visibleAccessFlagsMask();
		return result;
	}

	/**
	 * Return a mask that will preserve only those bits properly recognized
	 * by the java.lang.reflect.Modifier.toString() method.
	 */
	abstract short visibleAccessFlagsMask();

	public String displayString() {
		StringWriter sw = new StringWriter(1000);
		IndentingPrintWriter writer = new IndentingPrintWriter(sw);
		this.displayStringOn(writer);
		return sw.toString();
	}

	public void displayStringOn(IndentingPrintWriter writer) {
		this.printDeclarationOn(writer);
//		writer.print(" (");
//		writer.print(this.descriptor());
//		writer.print(")");
		writer.println();

		writer.indent();
		this.attributePool.displayStringOn(writer);
		writer.undent();
	}

	public abstract void printDeclarationOn(PrintWriter writer);

	void printModifierOn(PrintWriter writer) {
		String modifierString = this.modifierString();
		if (modifierString.length() != 0) {
			writer.print(modifierString);
			writer.print(' ');
		}
	}

	public void accept(Visitor visitor) {
		this.attributePool.accept(visitor);
	}

	public String modifierString() {
		return Modifier.toString(this.standardAccessFlags);
	}

	public String name() {
		return this.constantPool().getUTF8String(this.nameIndex);
	}

	public String descriptor() {
		return this.constantPool().getUTF8String(this.descriptorIndex);
	}

	public boolean isDeprecated() {
		return this.attributePool.isDeprecated();
	}

	/**
	 * "A class member that does not appear in the source code
	 * must be marked using a Synthetic attribute, or else it must
	 * have its ACC_SYNTHETIC bit set."
	 */
	public boolean isSynthetic() {
		return this.isMarkedSyntheticByAccessFlag() || this.isMarkedSyntheticByAttribute();
	}

	/**
	 * Check a bit that cannot (yet?) be interpreted by the
	 * Modifier static methods. This bit indicates the member
	 * is "not present in the source code".
	 */
	public boolean isMarkedSyntheticByAccessFlag() {
		return (this.accessFlags & ACC_SYNTHETIC) != 0;
	}

	public boolean isMarkedSyntheticByAttribute() {
		return this.attributePool.isSynthetic();
	}

	public Pool getPool() {
		return this.pool;
	}

	public ClassFile classFile() {
		return this.pool.getClassFile();
	}

	public String declaringClassName() {
		return this.classFile().className();
	}

	public ConstantPool constantPool() {
		return this.classFile().getConstantPool();
	}

	/**
	 * Return the set of flags that will match those
	 * returned by java.lang.Class#getModifiers()
	 */
	public short standardAccessFlags() {
		return this.standardAccessFlags;
	}

	public short getAccessFlags() {
		return this.accessFlags;
	}

	public short getNameIndex() {
		return this.nameIndex;
	}

	public short getDescriptorIndex() {
		return this.descriptorIndex;
	}

	public AttributePool getAttributePool() {
		return this.attributePool;
	}

	public final String toString() {
		StringWriter sw = new StringWriter(200);
		PrintWriter pw = new PrintWriter(sw);
		pw.print(ClassTools.shortClassNameForObject(this));
		pw.print('(');
		this.printDeclarationOn(pw);
		pw.print(')');
		return sw.toString();
	}


	// ********** member interface **********

	/**
	 * backpointer interface for members (fields and methods)
	 */
	public interface Pool {
		ClassFile getClassFile();
	}

}
