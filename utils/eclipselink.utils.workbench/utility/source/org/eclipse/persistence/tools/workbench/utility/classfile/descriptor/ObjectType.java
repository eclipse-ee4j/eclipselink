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

import org.eclipse.persistence.tools.workbench.utility.classfile.Visitor;


/**
 * This class models a class file Descriptor Object Type.
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ObjectType extends FieldType {
	private String internalTypeName;
	private String typeName;


	// ********** construction/initialization **********

	ObjectType(Reader reader) throws IOException {
		this(extractInternalNameFrom(reader));
	}

	ObjectType(String internalName) {
		super();
		this.internalTypeName = internalName;
		this.typeName = this.internalTypeName.replace('/', '.');
	}

	private static String extractInternalNameFrom(Reader reader) throws IOException {
		StringBuffer sb = new StringBuffer(256);
		for (int c = reader.read(); c != ';'; c = reader.read()) {
			sb.append((char) c);
		}
		return sb.toString();
	}


	// ********** public API **********

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public int arrayDepth() {
		return 0;
	}

	public String elementTypeName() {
		return this.typeName;
	}

	public String javaName() {
		return this.typeName;
	}

	public Class javaClass() throws ClassNotFoundException {
		return Class.forName(this.typeName);
	}

	public void appendDeclarationTo(StringBuffer sb) {
		sb.append(this.typeName);
	}

	public void printDeclarationOn(PrintWriter writer) {
		writer.print(this.typeName);
	}

	public String internalName() {
		StringBuffer sb = new StringBuffer();
		this.appendInternalNameTo(sb);
		return sb.toString();
	}


	// ********** internal API **********

	void appendArrayJavaNameTo(StringBuffer sb) {
		sb.append('L');
		sb.append(this.typeName);
		sb.append(';');
	}

	void appendArrayInternalNameTo(StringBuffer sb) {
		this.appendInternalNameTo(sb);
	}

	void appendInternalNameTo(StringBuffer sb) {
		sb.append('L');
		sb.append(this.internalTypeName);
		sb.append(';');
	}

}
