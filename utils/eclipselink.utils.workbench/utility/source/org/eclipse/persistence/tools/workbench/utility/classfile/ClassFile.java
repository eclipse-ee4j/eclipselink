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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.ObjectType;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models the information held in a Java .class file.
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
/*
 * TODO
 * 	ClassFileCodeAttribute - byte codes are simply read in to a byte array
 * 	may want to manipulate and *write* the byte codes...
 */
public class ClassFile {
	private Header header;
	private ConstantPool constantPool;
	private ClassDeclaration declaration;
	private FieldPool fieldPool;
	private MethodPool methodPool;
	private AttributePool attributePool;


	// ********** static methods **********

	/**
	 * Construct a class file from the specified stream,
	 * closing the stream once the class file has been built.
	 * Also note that we buffer the stream to improve performance.
	 */
	public static ClassFile fromInputStream(InputStream stream) throws IOException {
		ClassFile classFile = null;
		try {
			// I can't seem to remember to buffer, so I put this here...  ~bjv
			stream = new BufferedInputStream(stream);
			classFile = new ClassFile(stream);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return classFile;
	}

	/**
	 * Construct a class file from the specified JAR entry.
	 * Use this method if you are processing a number of JAR entries and
	 * want to keep the JAR file open for the duration of the processing.
	 */
	public static ClassFile fromArchiveEntry(JarFile jarFile, JarEntry jarEntry) throws IOException {
		return ClassFile.fromInputStream(jarFile.getInputStream(jarEntry));
	}

	/**
	 * Construct a class file from the specified JAR entry.
	 * Use this method if you are processing a number of JAR entries and
	 * want to keep the JAR file open for the duration of the processing.
	 */
	public static ClassFile fromArchiveEntry(JarFile jarFile, String className) throws IOException {
		String entryName = Classpath.convertToArchiveClassFileEntryName(className);
		return ClassFile.fromInputStream(jarFile.getInputStream(jarFile.getJarEntry(entryName)));
	}

	/**
	 * Construct a class file from the specified JAR entry.
	 * Use this method if you are processing a single JAR entry,
	 * because we will close the JAR file file when we are finished
	 * building the class file.
	 */
	public static ClassFile fromArchiveEntry(File file, String className) throws IOException {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			return ClassFile.fromArchiveEntry(jarFile, className);
		} finally {
			if (jarFile != null) {
				jarFile.close();
			}
		}
	}

	/**
	 * Construct a class file from the specified file.
	 */
	public static ClassFile fromFile(File file) throws IOException {
		return ClassFile.fromInputStream(new FileInputStream(file));
	}

	/**
	 * Construct a class file from the specified class file.
	 */
	public static ClassFile fromClassFile(File classpathEntryDirectory, String className) throws IOException {
		return ClassFile.fromFile(new File(classpathEntryDirectory, Classpath.convertToClassFileName(className)));
	}

	/**
	 * Construct a class file for the specified class.
	 */
	public static ClassFile forClass(File classPathEntry, String className) throws IOException {
		if (Classpath.fileNameIsArchive(classPathEntry.getPath())) {
			return ClassFile.fromArchiveEntry(classPathEntry, className);
		}
		return ClassFile.fromClassFile(classPathEntry, className);
	}

	/**
	 * Construct a class file for the specified class.
	 */
	public static ClassFile forClass(Class javaClass) throws IOException {
		return ClassFile.forClass(new File(Classpath.locationFor(javaClass)), javaClass.getName());
	}


	// ********** constructor **********

	/**
	 * Construct a class file from the specified stream of byte codes.
	 * The stream will remain open after the class file has been built.
	 */
	public ClassFile(InputStream stream) throws IOException {
		super();
		this.initialize(new ClassFileDataInputStream(stream));
	}


	// ********** instance methods **********

	private void initialize(ClassFileDataInputStream stream) throws IOException {
		this.header = new Header(stream);
		this.constantPool = new ConstantPool(stream);
		this.declaration = new ClassDeclaration(stream, this.constantPool);
		this.fieldPool = new FieldPool(stream, this);
		this.methodPool = new MethodPool(stream, this);
		this.attributePool = new AttributePool(stream, this);
		if (this.isNestedClass()) {
			this.declaration.setStandardAccessFlagsForNestedClass(this.nestedClassAccessFlags());
		}
	}

	public String className() {
		return this.declaration.thisClassName();
	}

	public String displayString() {
		StringWriter sw = new StringWriter(2000);
		IndentingPrintWriter writer = new IndentingPrintWriter(sw);
		this.displayStringOn(writer);
		return sw.toString();
	}

	public void displayStringOn(IndentingPrintWriter writer) {
		writer.print("ClassFile: ");
		writer.println(this.className());
		writer.indent();
		this.header.displayStringOn(writer);
		this.constantPool.displayStringOn(writer);
		this.declaration.displayStringOn(writer);
		this.fieldPool.displayStringOn(writer);
		this.methodPool.displayStringOn(writer);
		this.attributePool.displayStringOn(writer);
		writer.undent();
	}

	/**
	 * Return the set of flags that will match those
	 * returned by java.lang.Class#getModifiers()
	 */
	public short standardAccessFlags() {
		return this.declaration.standardAccessFlags();
	}

	/**
	 * Return whether the class is an interface.
	 */
	public boolean isInterface() {
		return this.declaration.isInterface();
	}

	/**
	 * Return whether the class is a class,
	 * as opposed to an interface.
	 */
	public boolean isClass() {
		return this.declaration.isClass();
	}

	public String superClassName() {
		return this.declaration.superClassName();
	}

	public String[] interfaceNames() {
		return this.declaration.interfaceNames();
	}

	public boolean isDeprecated() {
		return this.attributePool.isDeprecated();
	}

	/**
	 * Return whether the class was generated by the compiler
	 * and has no Java source code corresponding to it.
	 * "A class member that does not appear in the source code
	 * must be marked using a Synthetic attribute, or else it must
	 * have its ACC_SYNTHETIC bit set."
	 */
	public boolean isSynthetic() {
		return this.declaration.isSynthetic() || this.attributePool.isSynthetic();
	}

	/**
	 * There is only one "top-level" class per Java source file.
	 */
	public boolean isTopLevelClass() {
		return ! this.isNestedClass();
	}

	/**
	 * Return whether the class is a "nested" class, meaning
	 * the class is either a "member" class, a "local" class, or
	 * an "anonymous" class.
	 */
	public boolean isNestedClass() {
		return this.attributePool.isNestedClass();
	}

	/**
	 * Return whether the class is a "member" class,
	 * i.e. it is a peer of the "outer" class's fields and methods.
	 */
	public boolean isMemberClass() {
		return this.attributePool.isMemberClass();
	}

	/**
	 * Return whether the class is a "local" class,
	 * i.e. it is a peer of the local variables in one of
	 * the "outer" class's methods.
	 */
	public boolean isLocalClass() {
		return this.attributePool.isLocalClass();
	}

	/**
	 * Return whether the class is an "anonymous" class,
	 * i.e. it is a defined in an expression.
	 */
	public boolean isAnonymousClass() {
		return this.attributePool.isAnonymousClass();
	}

	public String sourceFileName() {
		return this.attributePool.sourceFileName();
	}

	/**
	 * If the class is an "member" class, return the name of its
	 * "outer" class.
	 */
	public String declaringClassName() {
		return this.attributePool.declaringClassName();
	}

	/**
	 * If the class is a "member" or "local" class, return its name
	 * relative to the scope of its "outer" class.
	 */
	public String nestedClassName() {
		return this.attributePool.nestedClassName();
	}

	/**
	 * If the class is a "nested" class, return the access flags
	 * associated with the class as a "nested" class, as opposed
	 * to the access flags declared in the class's declaration.
	 */
	public short nestedClassAccessFlags() {
		return this.attributePool.nestedClassAccessFlags();
	}

	/**
	 * Return the names of the class's "nested" classes; this
	 * includes "member", "local", and "anonymous" classes.
	 */
	public String[] nestedClassNames() {
		return this.attributePool.nestedClassNames();
	}

	/**
	 * Return the names of the class's "member" classes. These
	 * are the classes that are declared as members of
	 * the class file's class (i.e. the classes are peers to the
	 * class's fields and methods).
	 */
	public String[] declaredMemberClassNames() {
		return this.attributePool.declaredMemberClassNames();
	}

	/**
	 * Return the names of all the classes referenced directly by the
	 * class file's class. NB: This will NOT detect classes that are
	 * referenced indirectly. Classes that are loaded dynamically
	 * by name (e.g. Class.forName("com.foo.Bar")) will not be detected.
	 * Unfortunately, this is what happens when referencing a class
	 * in source code via the .class technique (e.g. com.foo.Bar.class).
	 * Also, classes have indirect references to any superclasses of
	 * directly-referenced classes. Therefore the list returned by this
	 * method cannot be used to determine compile-time dependencies,
	 * nevermind run-time dependences.
	 */
	public String[] referencedClassNames() {
		return new ReferencedClassNamesVisitor(this).referencedClassNames();
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		this.header.accept(visitor);
		this.constantPool.accept(visitor);
		this.declaration.accept(visitor);
		this.fieldPool.accept(visitor);
		this.methodPool.accept(visitor);
		this.attributePool.accept(visitor);
	}

	public Header getHeader() {
		return this.header;
	}

	public ConstantPool getConstantPool() {
		return this.constantPool;
	}

	public ClassDeclaration getDeclaration() {
		return this.declaration;
	}

	public FieldPool getFieldPool() {
		return this.fieldPool;
	}

	public MethodPool getMethodPool() {
		return this.methodPool;
	}

	public AttributePool getAttributePool() {
		return this.attributePool;
	}

	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.className() + ')';
	}


	// ********** member classes **********

	/**
	 * This class models a class file header:
	 *     u4 magic;
	 *     u2 minor_version;
	 *     u2 major_version;
	 * 
	 * See "The Java Virtual Machine Specification" Chapter 4.
	 */
	public static class Header extends Object {
		/** Java tag */
		private int magic;
	
		/** this the class file format version, not the class version */
		private short minorVersion;
		private short majorVersion;
	
	
		/**
		 * Construct a class file header from the specified stream
		 * of byte codes.
		 */
		Header(ClassFileDataInputStream stream) throws IOException {
			super();
			this.initialize(stream);
		}
		
		private void initialize(ClassFileDataInputStream stream) throws IOException {
			this.magic = stream.readU4();
			if (this.magic != 0xCAFEBABE) {
				throw new IOException("bad magic");
			}
			this.minorVersion = stream.readU2();
			this.majorVersion = stream.readU2();
		}
		
		public String displayString() {
			StringWriter sw = new StringWriter();
			IndentingPrintWriter writer = new IndentingPrintWriter(sw);
			this.displayStringOn(writer);
			return sw.toString();
		}
		
		public void displayStringOn(IndentingPrintWriter writer) {
			writer.println("Header");
			writer.indent();
			writer.print("magic: 0x");
			writer.println(this.magicString());
			writer.print("class file format version: ");
			writer.println(this.version());
			writer.undent();
		}
		
		public String magicString() {
			return Integer.toHexString(this.magic).toUpperCase();
		}
		
		public float version() {
			float temp = this.minorVersion;
			while (temp > 1) {
				temp = temp / 10;
			}
			return this.majorVersion + temp;
		}
		
		public String versionString() {
			return String.valueOf(this.version());
		}
	
		public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	
		public int getMagic() {
			return this.magic;
		}
		
		public short getMinorVersion() {
			return this.minorVersion;
		}
		
		public short getMajorVersion() {
			return this.majorVersion;
		}
		
		public String toString() {
			return ClassTools.shortClassNameForObject(this) + "(class file version: " + this.version() + ')';
		}
	
	}


	/**
	 * Example visitor that gathers up all the referenced class names in the
	 * class file.
	 */
	private static class ReferencedClassNamesVisitor extends VisitorAdapter {
		private final Collection referencedClassNames = new HashSet(200);

		ReferencedClassNamesVisitor(ClassFile classFile) {
			super();
			classFile.accept(this);
		}

		public void visit(ObjectType objectType) {
			this.addReferencedClassName(objectType.elementTypeName());
		}

		private void addReferencedClassName(String className) {
			if (ClassTools.classNamedIsReference(className)) {
				this.referencedClassNames.add(className);
			}
		}

		String[] referencedClassNames() {
			return (String[]) this.referencedClassNames.toArray(new String[this.referencedClassNames.size()]);
		}

	}

}
