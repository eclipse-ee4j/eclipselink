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
package org.eclipse.persistence.tools.workbench.utility.classfile.descriptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import org.eclipse.persistence.tools.workbench.utility.classfile.Visitor;


/**
 * This class models a class file Descriptor Array Type.
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ArrayType extends FieldType {
	private FieldType componentType;


	// ********** construction/initialization **********

	ArrayType(Reader reader) throws IOException {
		super();
		this.componentType = FieldType.createFieldType(reader);
	}


	// ********** public API **********

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public int arrayDepth() {
		return this.componentType.arrayDepth() + 1;
	}

	public String elementTypeName() {
		return this.componentType.elementTypeName();
	}

	public String javaName() {
		StringBuffer sb = new StringBuffer();
		this.appendArrayJavaNameTo(sb);
		return sb.toString();
	}

	public Class javaClass() throws ClassNotFoundException {
		return Class.forName(this.javaName());
	}

	public void appendDeclarationTo(StringBuffer sb) {
		this.componentType.appendDeclarationTo(sb);
		sb.append("[]");
	}

	public void printDeclarationOn(PrintWriter writer) {
		this.componentType.printDeclarationOn(writer);
		writer.print("[]");
	}

	public String internalName() {
		StringBuffer sb = new StringBuffer();
		this.appendArrayInternalNameTo(sb);
		return sb.toString();
	}


	// ********** internal API **********

	void appendArrayJavaNameTo(StringBuffer sb) {
		sb.append('[');
		this.componentType.appendArrayJavaNameTo(sb);
	}

	void appendArrayInternalNameTo(StringBuffer sb) {
		sb.append('[');
		this.componentType.appendArrayInternalNameTo(sb);
	}

}
