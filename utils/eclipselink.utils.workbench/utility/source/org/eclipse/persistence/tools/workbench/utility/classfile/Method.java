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

import java.io.FilterReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.FieldType;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;


/**
 * This class models a class file method.
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class Method extends Member {
	private FieldType returnDescriptor;		// lazy-initialized - so use the getter
	private FieldType[] parameterDescriptors;		// lazy-initialized - so use the getter
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	/** constants defined in "The Java Virtual Machine Specification" */
	public static final String CONSTRUCTOR_NAME = "<init>";
	public static final String STATIC_INITIALIZER_NAME = "<clinit>";
	public static final short ACC_BRIDGE = 0x0040;
	public static final short ACC_VARARGS = 0x0080;

	/**
	 * cleared bits:
	 *     0x8000 reserved for future use - ignore it
	 *               (although the Eclipse compiler sets it for some reason...)
	 *     0x4000 unrecognized by Modifier
	 *     0x2000 unrecognized by Modifier
	 *     0x1000 unrecognized by Modifier
	 *     0x0200 interface
	 *     0x0080 transient
	 *     x00040 volatile
	 */
	public static final int VISIBLE_ACCESS_FLAGS_MASK = 0x0D3F;


	/**
	 * Construct a class file method from the specified stream
	 * of byte codes.
	 */
	Method(ClassFileDataInputStream stream, MethodPool pool) throws IOException {
		super(stream, pool);
	}

	short visibleAccessFlagsMask() {
		return VISIBLE_ACCESS_FLAGS_MASK;
	}

	public void printDeclarationOn(PrintWriter writer) {
		if (this.isStaticInitializationMethod()) {
			writer.print("<static initialization>");
			return;
		}
		this.printModifierOn(writer);
		if (this.isConstructor()) {
			writer.print(this.codeConstructorName());
		} else {
			this.getReturnDescriptor().printDeclarationOn(writer);
			writer.print(' ');
			writer.print(this.name());
		}
		writer.print('(');
		int len = this.getParameterDescriptors().length;
		for (int i = 0; i < len; i++) {
			if (i != 0) {
				writer.write(", ");
			}
			this.getParameterDescriptor(i).printDeclarationOn(writer);
		}
		writer.print(')');
		this.getAttributePool().printThrowsClauseOn(writer);
	}

	/**
	 * Return the name that matches the name returned by
	 * java.lang.reflect.Method.getReturnType().getName().
	 */
	public String javaReturnTypeName() {
		return this.getReturnDescriptor().javaName();
	}

	/**
	 * Return the names that match the names returned by
	 * java.lang.reflect.Method.getParameterTypes()[index].getName().
	 */
	public String[] javaParameterTypeNames() {
		FieldType[] ptds = this.getParameterDescriptors();
		int len = ptds.length;
		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}
		String[] names = new String[len];
		for (int i = len; i-- > 0; ) {
			names[i] = ptds[i].javaName();
		}
		return names;
	}

	public String[] exceptionClassNames() {
		return this.getAttributePool().exceptionClassNames();
	}

	public boolean isConstructor() {
		return this.name().equals(CONSTRUCTOR_NAME);
	}

	/**
	 * as opposed to a "declared method" etc.
	 */
	public boolean isDeclaredConstructor() {
		if (this.isSynthetic()) {
			return false;
		}
		return this.isConstructor();
	}

	/**
	 * as opposed to a "declared constructor" etc.
	 */
	public boolean isDeclaredMethod() {
		if (this.isSynthetic()) {
			return false;
		}
		if (this.isConstructor()) {
			return false;
		}
		if (this.isStaticInitializationMethod()) {
			return false;
		}
		return true;
	}

	/**
	 * return the name of the constructor as it would be returned
	 * by the reflection api
	 */
	public String constructorName() {
		if ( ! this.isConstructor()) {
			throw new IllegalStateException();
		}
		return this.classFile().className();
	}

	/**
	 * return the name of the constructor as it would appear in code
	 */
	public String codeConstructorName() {
		if ( ! this.isConstructor()) {
			throw new IllegalStateException();
		}
		if (this.classFile().isNestedClass()) {
			// not sure what to return for an "anonymous" class...
			// currently, this will return "<anonymous>"
			return this.classFile().nestedClassName();
		}
		String fullName = this.classFile().className();
		return fullName.substring(fullName.lastIndexOf('.') + 1);
	}

	public boolean isStaticInitializationMethod() {
		return this.name().equals(STATIC_INITIALIZER_NAME);
	}

	/**
	 * Check a bit that cannot (yet?) be interpreted by the
	 * Modifier static methods. This bit indicates the method
	 * is "a bridge method generated by the compiler".
	 */
	public boolean isBridge() {
		return (this.getAccessFlags() & ACC_BRIDGE) != 0;
	}

	/**
	 * Check a bit that cannot (yet?) be interpreted by the
	 * Modifier static methods. This bit indicates the method
	 * "takes a variable number of arguments at the source
	 * code level".
	 */
	public boolean isVarArg() {
		return (this.getAccessFlags() & ACC_VARARGS) != 0;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		this.getReturnDescriptor().accept(visitor);
		FieldType[] ptds = this.getParameterDescriptors();
		int len = ptds.length;
		for (int i = 0; i < len; i++) {
			ptds[i].accept(visitor);
		}
		super.accept(visitor);
	}

	public MethodPool getMethodPool() {
		return (MethodPool) this.getPool();
	}

	public FieldType getReturnDescriptor() {
		if (this.returnDescriptor == null) {
			this.buildDescriptors();
		}
		return this.returnDescriptor;
	}

	public FieldType[] getParameterDescriptors() {
		if (this.parameterDescriptors == null) {
			this.buildDescriptors();
		}
		return this.parameterDescriptors;
	}

	public FieldType getParameterDescriptor(int index) {
		if (this.parameterDescriptors == null) {
			this.buildDescriptors();
		}
		return this.parameterDescriptors[index];
	}

	private void buildDescriptors() {
		Reader reader = new StringReader(this.descriptor());
		try {
			this.parameterDescriptors = buildParameterDescriptors(reader);
			this.returnDescriptor = FieldType.createFieldType(reader);
		} catch (IOException ex) {
			// this is unlikely when reading a String
			throw new RuntimeException(ex);
		}
	}

	static FieldType[] buildParameterDescriptors(Reader reader) throws IOException {
		PeekableReader localReader = new PeekableReader(reader);
		int c = localReader.read();
		if (c != '(') {	// not quite sure why this paren is needed - readability?
			throw new IllegalStateException("open parenthesis expected: " + (char) c);
		}

		List parms = new ArrayList();
		while (localReader.peek() != ')') {
			parms.add(FieldType.createFieldType(localReader));
		}
		localReader.read();		// read and discard the closing parenthesis
		return (FieldType[]) parms.toArray(new FieldType[parms.size()]);
	}


	// ********** helper class **********

	private static class PeekableReader extends FilterReader {
		PeekableReader(Reader reader) {
			super(reader);
		}
		/**
		 * Read and return the next character from the
		 * reader without changing the reader's position.
		 */
		public int peek() throws IOException {
			this.mark(1);
			int peek = this.read();
			this.reset();
			return peek;
		}
	}

}
