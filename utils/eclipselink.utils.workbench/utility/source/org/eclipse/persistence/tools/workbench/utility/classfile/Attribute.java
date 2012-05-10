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
import java.io.Writer;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;
import org.eclipse.persistence.tools.workbench.utility.io.StringBufferWriter;


/**
 * This class models a class file attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u1[attribute_length] info;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public abstract class Attribute {
	private AttributePool pool;
	private short nameIndex;
	private int length;

	static final String[] EMPTY_STRING_ARRAY = new String[0];


	/**
	 * Construct a class file attribute from the specified stream
	 * of byte codes.
	 */
	static Attribute read(ClassFileDataInputStream stream, AttributePool pool) throws IOException {
		short nameIndex = stream.readU2();
		String name = pool.constantPool().getUTF8String(nameIndex);

		if (name.equals("ConstantValue")) {
			return new ConstantValueAttribute(stream, nameIndex, pool);

		} else if (name.equals("Code")) {
			return new CodeAttribute(stream, nameIndex, pool);

		} else if (name.equals("Exceptions")) {
			return new ExceptionsAttribute(stream, nameIndex, pool);

		} else if (name.equals("InnerClasses")) {
			return new InnerClassesAttribute(stream, nameIndex, pool);

		} else if (name.equals("EnclosingMethod")) {
			return new EnclosingMethodAttribute(stream, nameIndex, pool);

		} else if (name.equals("Synthetic")) {
			return new SyntheticAttribute(stream, nameIndex, pool);

		} else if (name.equals("Signature")) {
			return new SignatureAttribute(stream, nameIndex, pool);

		} else if (name.equals("SourceFile")) {
			return new SourceFileAttribute(stream, nameIndex, pool);

		} else if (name.equals("SourceDebugExtension")) {
			return new SourceDebugExtensionAttribute(stream, nameIndex, pool);

		} else if (name.equals("LineNumberTable")) {
			return new LineNumberTableAttribute(stream, nameIndex, pool);

		} else if (name.equals("LocalVariableTable")) {
			return new LocalVariableTableAttribute(stream, nameIndex, pool);

		} else if (name.equals("LocalVariableTypeTable")) {
			return new LocalVariableTypeTableAttribute(stream, nameIndex, pool);

		} else if (name.equals("Deprecated")) {
			return new DeprecatedAttribute(stream, nameIndex, pool);

		} else if (name.equals("RuntimeVisibleAnnotations")) {
			return new RuntimeVisibleAnnotationsAttribute(stream, nameIndex, pool);

		} else if (name.equals("RuntimeInvisibleAnnotations")) {
			return new RuntimeInvisibleAnnotationsAttribute(stream, nameIndex, pool);

		} else if (name.equals("RuntimeVisibleParameterAnnotations")) {
			return new RuntimeVisibleParameterAnnotationsAttribute(stream, nameIndex, pool);

		} else if (name.equals("RuntimeInvisibleParameterAnnotations")) {
			return new RuntimeInvisibleParameterAnnotationsAttribute(stream, nameIndex, pool);

		} else if (name.equals("AnnotationDefault")) {
			return new AnnotationDefaultAttribute(stream, nameIndex, pool);

		} else {
			return new UnknownAttribute(stream, nameIndex, pool);
		}
	}
	
	/**
	 * Construct a class file attribute from the specified stream
	 * of byte codes.
	 */
	Attribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
		super();
		this.pool = pool;
		this.nameIndex = nameIndex;
		this.initialize(stream);
	}
	
	void initialize(ClassFileDataInputStream stream) throws IOException {
		this.length = stream.readU4();
		this.initializeInfo(stream);
	}

	/**
	 * Read in the info.
	 */
	abstract void initializeInfo(ClassFileDataInputStream stream) throws IOException;
	
	public String displayString() {
		StringWriter sw = new StringWriter(1000);
		IndentingPrintWriter writer = new IndentingPrintWriter(sw);
		this.displayStringOn(writer);
		return sw.toString();
	}
	
	public void displayStringOn(IndentingPrintWriter writer) {
		this.displayNameOn(writer);
		writer.println();
		writer.indent();
		this.displayInfoStringOn(writer);
		writer.undent();
	}
	
	public void displayNameOn(IndentingPrintWriter writer) {
		writer.print(this.name());
	}
	
	/**
	 * Display the info.
	 */
	abstract void displayInfoStringOn(IndentingPrintWriter writer);

	public Object fieldConstantValue() {
		return null;
	}

	public void printFieldInitializationClauseOn(PrintWriter writer) {
		// the default is to do nothing
	}

	public String[] exceptionClassNames() {
		return null;
	}

	public String sourceFileName() {
		return null;
	}

	public InnerClass innerClassNamed(String className) {
		return null;
	}

	public String declaringClassName() {
		return null;
	}

	public String nestedClassName() {
		return null;
	}

	public short nestedClassAccessFlags() {
		return 0;
	}

	public String[] nestedClassNames() {
		return null;
	}

	public String[] declaredMemberClassNames() {
		return null;
	}

	public void printThrowsClauseOn(PrintWriter writer) {
		// the default is to do nothing
	}

	public boolean isDeprecated() {
		return false;
	}

	public boolean isSynthetic() {
		return false;
	}

	public boolean isNestedClass() {
		return false;
	}

	public boolean isMemberClass() {
		return false;
	}

	public boolean isLocalClass() {
		return false;
	}

	public boolean isAnonymousClass() {
		return false;
	}

	public ClassFile classFile() {
		return this.pool.getClassFile();
	}

	public ConstantPool constantPool() {
		return this.classFile().getConstantPool();
	}

	public String name() {
		return this.constantPool().getUTF8String(this.nameIndex);
	}

	public AttributePool getPool() {
		return this.pool;
	}

	public short getNameIndex() {
		return this.nameIndex;
	}
	
	public int getLength() {
		return this.length;
	}

	/**
	 * Convert the byte array to a HEX string.
	 * HEX allows for binary data to be printed.
	 */
	void appendHexStringTo(byte[] bytes, StringBuffer sb) {
		this.writeHexStringOn(bytes, new StringBufferWriter(sb));
	}

	/**
	 * Convert the byte array to a HEX string.
	 * HEX allows for binary data to be printed.
	 */
	void writeHexStringOn(byte[] bytes, Writer writer) {
		try {
			this.writeHexStringOnInternal(bytes, writer);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Convert the byte array to a HEX string.
	 * HEX allows for binary data to be printed.
	 */
	private static final char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	private void writeHexStringOnInternal(byte[] bytes, Writer writer) throws IOException {
		int len = bytes.length;
		for (int i = 0; i < len; i++) {
			int b = bytes[i];
			if (b < 0) {
				b = b + 256;	// compensate for the fact that byte is signed in Java
			}
			b = (byte) (b / 16);		// get the first digit
			writer.write(HEX_CHARS[b]);

			b = bytes[i];
			if (b < 0) {
				b = b + 256;
			}
			b = (byte) (b % 16);		// get the second digit
			writer.write(HEX_CHARS[b]);	
		}
	}

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
		// allow subclasses to optionally override
	}

}
