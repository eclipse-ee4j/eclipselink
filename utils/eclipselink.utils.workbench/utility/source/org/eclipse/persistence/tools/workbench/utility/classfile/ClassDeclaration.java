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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Modifier;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file class declaration:
 *     u2 access_flags;
 *     u2 this_class;
 *     u2 super_class;
 *     u2 interfaces_count;
 *     u2[interfaces_count] interfaces;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ClassDeclaration {

	/** the constants referenced by the indexes */
	private ConstantPool constantPool;

	/** these flags can be interrogated by the java.lang.reflect.Modifier static methods */
	private short accessFlags;
	/** these flags will match those returned by java.lang.Class#getModifiers() */
	private short standardAccessFlags;

	private short thisClassIndex;
	private short superClassIndex;
	private short interfacesCount;
	private short[] interfaceIndexes;

	/** constants defined in "The Java Virtual Machine Specification" */
	public static final short ACC_SUPER = 0x0020;
	public static final short ACC_SYNTHETIC = 0x1000;
	public static final short ACC_ANNOTATION = 0x2000;
	public static final short ACC_ENUM = 0x4000;

	static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * cleared bits:
	 *     0x8000 reserved for future use - ignore it
	 *               (although the Eclipse compiler sets it for some reason...)
	 *     0x4000 unrecognized by Modifier
	 *     0x2000 unrecognized by Modifier
	 *     0x1000 unrecognized by Modifier
	 *     0x0100 native
	 *     0x0080 transient
	 *     x00040 volatile
	 *     x00020 synchronized (re-used by "super")
	 */
	public static final int VISIBLE_ACCESS_FLAGS_MASK = 0x0E1F;


	/**
	 * Construct a class file declaration from the specified stream
	 * of byte codes.
	 */
	ClassDeclaration(ClassFileDataInputStream stream, ConstantPool constantPool) throws IOException {
		super();
		this.constantPool = constantPool;
		this.initialize(stream);
	}

	private void initialize(ClassFileDataInputStream stream) throws IOException {
		this.accessFlags = stream.readU2();
		this.standardAccessFlags = this.buildStandardAccessFlags();
		this.thisClassIndex = stream.readU2();
		this.superClassIndex = stream.readU2();
		this.interfacesCount = stream.readU2();
		short cnt = this.interfacesCount;
		this.interfaceIndexes = new short[cnt];
		short[] indexes = this.interfaceIndexes;
		for (short i = 0; i < cnt; i++) {
			indexes[i] = stream.readU2();
		}
	}

	private short buildStandardAccessFlags() {
		short result = this.accessFlags;
		result &= VISIBLE_ACCESS_FLAGS_MASK;
		return result;
	}

	void setStandardAccessFlagsForNestedClass(short flags) {
		this.standardAccessFlags = flags;
	}

	public String displayString() {
		StringWriter sw = new StringWriter();
		IndentingPrintWriter writer = new IndentingPrintWriter(sw);
		this.displayStringOn(writer);
		return sw.toString();
	}

	public void displayStringOn(IndentingPrintWriter writer) {
		writer.println("Declaration:");
		writer.indent();
		this.printDeclarationOn(writer);
		writer.undent();
		writer.println();
	}

	public void printDeclarationOn(PrintWriter writer) {
		String modifierString = this.modifierString();
		if (modifierString.length() != 0) {
			writer.print(modifierString);
			writer.print(' ');
		}
		if (this.isClass()) {	// as opposed to an interface
			writer.print("class ");
		}
		writer.print(this.thisClassName());
		if (this.isClass()) {	// as opposed to an interface
			writer.print(" extends ");
			writer.print(this.superClassName());
		}

		short cnt = this.interfacesCount;
		if (cnt != 0) {
			if (this.isClass()) {	// as opposed to an interface
				writer.print(" implements ");
			} else {
				writer.print(" extends ");
			}
			for (short i = 0; i < cnt; i++) {
				if (i != 0) {
					writer.write(", ");
				}
				writer.write(this.interfaceName(i));
			}
		}
	}
	
	public String modifierString() {
		return Modifier.toString(this.standardAccessFlags);
	}

	/**
	 * Return the set of flags that will match those
	 * returned by java.lang.Class#getModifiers()
	 */
	public short standardAccessFlags() {
		return this.standardAccessFlags;
	}

	/**
	 * Return whether the class is an interface.
	 */
	public boolean isInterface() {
		return Modifier.isInterface(this.accessFlags);
	}

	/**
	 * Return whether the class is a class,
	 * as opposed to an interface.
	 */
	public boolean isClass() {
		return ! this.isInterface();
	}

	/**
	 * Check a bit that cannot be interpreted by the
	 * Modifier static methods. This bit is used by the JVM
	 * for backward-compatibility purposes:
	 * "Treat superclass methods specially when invoked
	 * by the invokespecial instruction."
	 */
	public boolean isSuper() {
		return (this.accessFlags & ACC_SUPER) != 0;
	}

	/**
	 * Check a bit that cannot (yet?) be interpreted by the
	 * Modifier static methods. This bit indicates the class
	 * is "not present in the source code".
	 */
	public boolean isSynthetic() {
		return (this.accessFlags & ACC_SYNTHETIC) != 0;
	}

	/**
	 * Check a bit that cannot (yet?) be interpreted by the
	 * Modifier static methods. This bit indicates the class
	 * is "declared as an annotation type".
	 */
	public boolean isAnnotation() {
		return (this.accessFlags & ACC_ANNOTATION) != 0;
	}

	/**
	 * Check a bit that cannot (yet?) be interpreted by the
	 * Modifier static methods. This bit indicates the class
	 * is "declared as an enumerated type".
	 */
	public boolean isEnum() {
		return (this.accessFlags & ACC_ENUM) != 0;
	}

	public String thisClassName() {
		return this.className(this.thisClassIndex);
	}

	public String superClassName() {
		if (this.isInterface()) {
			return null;
		}
		short index = this.superClassIndex;
		return (index == 0) ? null : this.className(index);
	}

	public String interfaceName(int index) {
		return this.className(this.interfaceIndexes[index]);
	}

	public String[] interfaceNames() {
		short count = this.interfacesCount;
		if (count == 0) {
			return EMPTY_STRING_ARRAY;
		}
		String[] interfaceNames = new String[count];
		for (short i = 0; i < count; i++) {
			interfaceNames[i] = this.interfaceName(i);
		}
		return interfaceNames;
	}

	private String className(short index) {
		return this.constantPool.getClassConstant(index).name();
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public ConstantPool getConstantPool() {
		return this.constantPool;
	}

	public short getAccessFlags() {
		return this.accessFlags;
	}

	public short getThisClassIndex() {
		return this.thisClassIndex;
	}

	public short getSuperClassIndex() {
		return this.superClassIndex;
	}

	public short getInterfacesCount() {
		return this.interfacesCount;
	}

	public short[] getInterfaceIndexes() {
		return this.interfaceIndexes;
	}

	public short getInterfaceIndex(int index) {
		return this.interfaceIndexes[index];
	}

	public String toString() {
		StringWriter sw = new StringWriter(200);
		PrintWriter pw = new PrintWriter(sw);
		pw.print(ClassTools.shortClassNameForObject(this));
		pw.print('(');
		this.printDeclarationOn(pw);
		pw.print(')');
		return sw.toString();
	}

}
