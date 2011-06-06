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
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file attribute pool:
 *     u2 attributes_count;
 *     attribute_info[attributes_count] attributes;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class AttributePool {
	private ClassFile classFile;
	private short count;
	private Attribute[] attributes;

	private static final String EMPTY_STRING = "";
	private static final String[] EMPTY_STRING_ARRAY = new String[0];


	/**
	 * Construct a class file attribute pool from the specified stream
	 * of byte codes.
	 */
	AttributePool(ClassFileDataInputStream stream, ClassFile classFile) throws IOException {
		super();
		this.classFile = classFile;
		this.initialize(stream);
	}
	
	private void initialize(ClassFileDataInputStream stream) throws IOException {
		this.count = stream.readU2();
		short cnt = this.count;
		this.attributes = new Attribute[cnt];
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			localAttributes[i] = Attribute.read(stream, this);
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
		if (cnt == 0) {
			return;
		}
		writer.print("Attribute Pool (count: ");
		writer.print(cnt);
		writer.println(')');
		writer.indent();
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			writer.print(i);
			writer.print(": ");
			localAttributes[i].displayStringOn(writer);
		}
		writer.undent();
	}

	public InnerClass innerClassNamed(String className) {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			InnerClass innerClass = localAttributes[i].innerClassNamed(className);
			if (innerClass != null) {
				return innerClass;
			}
		}
		throw new IllegalArgumentException(className);
	}

	public Object fieldConstantValue() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			Object value = localAttributes[i].fieldConstantValue();
			if (value != null) {
				return value;
			}
		}
		throw new IllegalStateException();
	}

	public void printFieldInitializationClauseOn(PrintWriter writer) {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			localAttributes[i].printFieldInitializationClauseOn(writer);
		}
	}

	public String[] exceptionClassNames() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			String[] exceptionClassNames = localAttributes[i].exceptionClassNames();
			if (exceptionClassNames != null) {
				return exceptionClassNames;
			}
		}
		return EMPTY_STRING_ARRAY;
	}

	public String declaringClassName() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			String declaringClassName = localAttributes[i].declaringClassName();
			if (declaringClassName != null) {
				return declaringClassName;
			}
		}
		return null;
	}

	public String nestedClassName() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			String nestedClassName = localAttributes[i].nestedClassName();
			if (nestedClassName != null) {
				return nestedClassName;
			}
		}
		return null;
	}

	public short nestedClassAccessFlags() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			short nestedClassAccessFlags = localAttributes[i].nestedClassAccessFlags();
			if (nestedClassAccessFlags != 0) {
				return nestedClassAccessFlags;
			}
		}
		return 0;
	}

	public String[] nestedClassNames() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			String[] nestedClassNames = localAttributes[i].nestedClassNames();
			if (nestedClassNames != null) {
				return nestedClassNames;
			}
		}
		return EMPTY_STRING_ARRAY;
	}

	public String[] declaredMemberClassNames() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			String[] declaredMemberClassNames = localAttributes[i].declaredMemberClassNames();
			if (declaredMemberClassNames != null) {
				return declaredMemberClassNames;
			}
		}
		return EMPTY_STRING_ARRAY;
	}

	public void printThrowsClauseOn(PrintWriter writer) {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			localAttributes[i].printThrowsClauseOn(writer);
		}
	}

	public boolean isDeprecated() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			if (localAttributes[i].isDeprecated()) {
				return true;
			}
		}
		return false;
	}

	public boolean isSynthetic() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			if (localAttributes[i].isSynthetic()) {
				return true;
			}
		}
		return false;
	}

	public boolean isNestedClass() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			if (localAttributes[i].isNestedClass()) {
				return true;
			}
		}
		return false;
	}

	public boolean isMemberClass() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			if (localAttributes[i].isMemberClass()) {
				return true;
			}
		}
		return false;
	}

	public boolean isLocalClass() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			if (localAttributes[i].isLocalClass()) {
				return true;
			}
		}
		return false;
	}

	public boolean isAnonymousClass() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			if (localAttributes[i].isAnonymousClass()) {
				return true;
			}
		}
		return false;
	}

	public String sourceFileName() {
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			String sourceFileName = localAttributes[i].sourceFileName();
			if (sourceFileName != null) {
				return sourceFileName;
			}
		}
		return EMPTY_STRING;
	}

	public ClassFile getClassFile() {
		return this.classFile;
	}

	public ConstantPool constantPool() {
		return this.classFile.getConstantPool();
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		short cnt = this.count;
		Attribute[] localAttributes = this.attributes;
		for (short i = 0; i < cnt; i++) {
			localAttributes[i].accept(visitor);
		}
	}

	public short getCount() {
		return this.count;
	}
	
	public Attribute get(short index) {
		return this.attributes[index];
	}

	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.count + " attribute(s))";
	}

}
