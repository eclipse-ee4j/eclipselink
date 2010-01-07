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
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models an inner class:
 *     u2 inner_class_info_index;
 *     u2 outer_class_info_index;
 *     u2 inner_name_index;
 *     u2 inner_class_access_flags;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class InnerClass {
	private InnerClassesAttribute pool;
	private short innerClassInfoIndex;	// this is the fully-qualified class name
	private short outerClassInfoIndex;	// this is only present for "member" classes
	private short innerClassNameIndex;	// this is the "local" name
	private short innerClassAccessFlags;

	/** constants defined in "The Java Virtual Machine Specification" */
	public static final short ACC_SYNTHETIC = 0x1000;
	public static final short ACC_ANNOTATION = 0x2000;
	public static final short ACC_ENUM = 0x4000;

	private static final String ANONYMOUS = "<anonymous>";
	private static final String NOT_APPLICABLE = "<N/A>";


	/**
	 * Construct an inner class from the specified stream
	 * of byte codes.
	 */
	InnerClass(ClassFileDataInputStream stream, InnerClassesAttribute pool) throws IOException {
		super();
		this.pool = pool;
		this.initialize(stream);
	}

	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.innerClassInfoIndex = stream.readU2();
		this.outerClassInfoIndex = stream.readU2();
		this.innerClassNameIndex = stream.readU2();
		this.innerClassAccessFlags = stream.readU2();
	}

	public String displayString() {
		StringWriter sw = new StringWriter(1000);
		IndentingPrintWriter writer = new IndentingPrintWriter(sw);
		this.displayStringOn(writer);
		return sw.toString();
	}

	public void displayStringOn(IndentingPrintWriter writer) {
		writer.println(this.innerClassName());
		writer.indent();
		writer.print("inner class info name: ");
		writer.println(this.innerClassInfoName());
		writer.print("outer class info name: ");
		writer.println(this.outerClassInfoName());
		writer.print("inner class name: ");
		writer.println(this.innerClassName());
		writer.print("modifier string: ");
		writer.println(this.modifierString());
		writer.undent();
	}

	boolean isNamed(short nameIndex) {
		return this.innerClassInfoIndex == nameIndex;
	}

	public boolean isNamed(String name) {
		return this.innerClassInfoName().equals(name);
//		String innerClassName = this.innerClassName();
//		return innerClassName != ANONYMOUS
//				&& innerClassName.equals(name);
	}

	/**
	 * only "member" classes have "declaring" classes
	 * ("local" and "anonymous" classes do not)
	 */
	String declaringClassName() {
		short index = this.outerClassInfoIndex;
		return (index == 0) ? null : this.className(index);
	}

	/**
	 * only a "member" class has an outer class of
	 * the class file's main class
	 */
	void addDeclaredMemberClassTo(List memberClassNames) {
		if (this.isSynthetic()) {
			return;
		}
//		String className = this.classFile().className();
//		if (this.outerClassInfoName().equals(className)) {
		short classNameIndex = this.classFile().getDeclaration().getThisClassIndex();
		if (this.outerClassInfoIndex == classNameIndex) {
			memberClassNames.add(this.innerClassInfoName());
		}
	}

	public String innerClassInfoName() {
		return this.className(this.innerClassInfoIndex);
	}

	public String outerClassInfoName() {
		short index = this.outerClassInfoIndex;
		return (index == 0) ? NOT_APPLICABLE : this.className(index);
	}

	private String className(short index) {
		return this.constantPool().getClassConstant(index).name();
	}

	public String innerClassName() {
		short index = this.innerClassNameIndex;
		return (index == 0) ? ANONYMOUS : this.constantPool().getUTF8String(index);
	}

	public String modifierString() {
		return Modifier.toString(this.innerClassAccessFlags);
	}

	public ConstantPool constantPool() {
		return this.pool.constantPool();
	}

	public ClassFile classFile() {
		return this.pool.classFile();
	}

	/**
	 * Check a bit that cannot (yet?) be interpreted by the
	 * Modifier static methods. This bit indicates the class
	 * is "not present in the source code".
	 */
	public boolean isSynthetic() {
		return (this.innerClassAccessFlags & ACC_SYNTHETIC) != 0;
	}

	/**
	 * Check a bit that cannot (yet?) be interpreted by the
	 * Modifier static methods. This bit indicates the class
	 * is "declared as an annotation type".
	 */
	public boolean isAnnotation() {
		return (this.innerClassAccessFlags & ACC_ANNOTATION) != 0;
	}

	/**
	 * Check a bit that cannot (yet?) be interpreted by the
	 * Modifier static methods. This bit indicates the class
	 * is "declared as an enum type".
	 */
	public boolean isEnum() {
		return (this.innerClassAccessFlags & ACC_ENUM) != 0;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public InnerClassesAttribute getPool() {
		return this.pool;
	}

	public short getInnerClassInfoIndex() {
		return this.innerClassInfoIndex;
	}

	public short getOuterClassInfoIndex() {
		return this.outerClassInfoIndex;
	}

	public short getInnerClassNameIndex() {
		return this.innerClassNameIndex;
	}

	public short getInnerClassAccessFlags() {
		return this.innerClassAccessFlags;
	}

	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.innerClassName() + ')';
	}

}
