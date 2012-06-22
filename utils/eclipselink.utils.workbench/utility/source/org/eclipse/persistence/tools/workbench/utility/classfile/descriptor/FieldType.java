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
package org.eclipse.persistence.tools.workbench.utility.classfile.descriptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.Visitor;


/**
 * This class models a class file Descriptor Field Type.
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public abstract class FieldType {


	// ********** static factory methods **********

	public static FieldType createFieldType(String fieldDescriptor) {
		try {
			return createFieldType(new StringReader(fieldDescriptor));
		} catch (IOException ex) {
			// highly unlikely when using a StringReader...
			throw new RuntimeException(ex);
		}
	}

	public static FieldType createFieldType(Reader reader) throws IOException {
		int c = reader.read();
		switch (c) {
			case 'L':	return new ObjectType(reader);
			case '[':	return new ArrayType(reader);
			default: return new BaseType(c);
		}
	}

	/**
	 * Array classes are stored like descriptors; while non-array
	 * classes are simply stored as a name (without the surrounding
	 * "L   ;").
	 */
	public static FieldType createFieldTypeForClassConstant(String internalName) {
		if (internalName.charAt(0) == '[') {
			return FieldType.createFieldType(internalName);
		}
		return new ObjectType(internalName);
	}


	// ********** public API **********

	/**
	 * Accept the specified visitor.
	 */
	public abstract void accept(Visitor visitor);

	/**
	 * Return the field type's "array depth".
	 */
	public abstract int arrayDepth();

	/**
	 * Return the field type's "element type name".
	 */
	public abstract String elementTypeName();

	/**
	 * Return the name that matches the name returned by
	 * java.lang.Class.getName().
	 */
	public abstract String javaName();

	/**
	 * Return the Java class corresponding to the
	 * type declaration, if possible.
	 */
	public abstract Class javaClass() throws ClassNotFoundException;

	/**
	 * Return the declaration used to define the field type in Java
	 * source code.
	 */
	public String declaration() {
		StringBuffer sb = new StringBuffer(200);
		this.appendDeclarationTo(sb);
		return sb.toString();
	}

	/**
	 * Append the Java source code that would define
	 * the field type on the specified string buffer.
	 */
	public abstract void appendDeclarationTo(StringBuffer sb);

	/**
	 * Print the Java source code that would define
	 * the field type on the specified writer.
	 */
	public abstract void printDeclarationOn(PrintWriter writer);

	/**
	 * Return the name that matches the name used internally by the JVM.
	 */
	public abstract String internalName();

	public String toString() {
		StringBuffer sb = new StringBuffer(200);
		sb.append(ClassTools.shortClassNameForObject(this));
		sb.append('(');
		this.appendDeclarationTo(sb);
		sb.append(')');
		return sb.toString();
	}


	// ********** internal API **********

	FieldType() {
		super();
	}

	abstract void appendArrayJavaNameTo(StringBuffer sb);

	abstract void appendArrayInternalNameTo(StringBuffer sb);

}
