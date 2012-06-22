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
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.FieldType;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models an enclosing method attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 class_index;
 *     u2 method_index;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class EnclosingMethodAttribute extends Attribute {
	private short classIndex;
	private short methodIndex;

	private FieldType methodReturnDescriptor;		// lazy-initialized - so use the getter
	private FieldType[] methodParameterDescriptors;		// lazy-initialized - so use the getter


	/**
	 * Construct an enclosing method attribute from the specified stream
	 * of byte codes.
	 */
	EnclosingMethodAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
		super(stream, nameIndex, pool);
	}

	void initializeInfo(ClassFileDataInputStream stream) throws IOException {
		this.classIndex = stream.readU2();
		this.methodIndex = stream.readU2();
	}

	void displayInfoStringOn(IndentingPrintWriter writer) {
		this.printFullyQualifiedMethodDeclarationOn(writer);
		writer.println();
	}

	public void printFullyQualifiedMethodDeclarationOn(PrintWriter writer) {
		writer.print(this.className());
		writer.print('.');
		if (this.methodIndex == 0) {
			writer.print("<static initialization>");
			return;
		}
		if (this.methodIsConstructor()) {
			writer.print(this.codeConstructorName());
		} else {
			writer.print(this.methodName());
		}
		writer.print('(');
		int len = this.getMethodParameterDescriptors().length;
		for (int i = 0; i < len; i++) {
			if (i != 0) {
				writer.write(", ");
			}
			this.getMethodParameterDescriptor(i).printDeclarationOn(writer);
		}
		writer.print(')');
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		this.getMethodReturnDescriptor().accept(visitor);
		FieldType[] ptds = this.getMethodParameterDescriptors();
		int len = ptds.length;
		for (int i = 0; i < len; i++) {
			ptds[i].accept(visitor);
		}
	}

	/**
	 * return the name of the constructor as it would appear in code
	 */
	private String codeConstructorName() {
		String fullName = this.className();
		return fullName.substring(fullName.lastIndexOf('.') + 1);
	}

	public String fullyQualifiedSignature() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		this.printFullyQualifiedMethodDeclarationOn(pw);
		return sw.toString();
	}

	public String className() {
		return this.constantPool().getClassConstant(this.classIndex).name();
	}

	public String methodName() {
		NameAndTypeConstant mnatc = this.methodNameAndTypeConstant();
		return (mnatc == null) ? null : mnatc.name();
	}

	public String methodDescriptor() {
		NameAndTypeConstant mnatc = this.methodNameAndTypeConstant();
		return (mnatc == null) ? null : mnatc.descriptor();
	}

	public NameAndTypeConstant methodNameAndTypeConstant() {
		short index = this.methodIndex;
		return (index == 0) ? null : this.constantPool().getNameAndTypeConstant(index);
	}

	public short getClassIndex() {
		return this.classIndex;
	}

	public short getMethodIndex() {
		return this.methodIndex;
	}

	void toString(StringBuffer sb) {
		sb.append(this.fullyQualifiedSignature());
	}

	public boolean methodIsConstructor() {
		return this.methodName().equals(Method.CONSTRUCTOR_NAME);
	}

	public FieldType getMethodReturnDescriptor() {
		if (this.methodReturnDescriptor == null) {
			this.buildTypeDeclarations();
		}
		return this.methodReturnDescriptor;
	}

	public FieldType[] getMethodParameterDescriptors() {
		if (this.methodParameterDescriptors == null) {
			this.buildTypeDeclarations();
		}
		return this.methodParameterDescriptors;
	}

	public FieldType getMethodParameterDescriptor(int index) {
		if (this.methodParameterDescriptors == null) {
			this.buildTypeDeclarations();
		}
		return this.methodParameterDescriptors[index];
	}

	private void buildTypeDeclarations() {
		Reader reader = new StringReader(this.methodDescriptor());
		try {
			this.methodParameterDescriptors = Method.buildParameterDescriptors(reader);
			this.methodReturnDescriptor = FieldType.createFieldType(reader);
		} catch (IOException ex) {
			// this is unlikely when reading a String
			throw new RuntimeException(ex);
		}
	}

}
