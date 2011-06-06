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
package org.eclipse.persistence.tools.workbench.test.utility.classfile;

import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.persistence.tools.workbench.utility.classfile.ClassFile;

/**
 * dump the contents of the specified class
 */
public class ClassFileDumper {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ClassFileDumper dumper = new ClassFileDumper();
		dumper.dump("org.eclipse.persistence.internal.annotations.CBListener");
//		dumper.dump("classfile.jdk5.JDK5ClassFileTestClass");
//		dumper.dump("org.eclipse.persistence.internal.weaving.TransformerFactory$1");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$1$LocalClass1");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$1$LocalClass2");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$1");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$4");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$AnotherClass$DoubleNestedClass");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$AnotherClass");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$DeprecatedStaticInnerInterface");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$InnerClass1$NestedInnerClass");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$InnerClass1");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$InnerInterface1");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$StaticInnerClass");
//		dumper.dump("test.org.eclipse.persistence.tools.workbench.utility.classfile.ClassFileTestClass$StaticInnerInterface");
	}

	/**
	 * find the byte codes for the specified class
	 * and dump them to the console
	 */
	public void dump(String className) throws IOException, ClassNotFoundException {
		this.dump(Class.forName(className));
	}

	/**
	 * find the byte codes for the specified class
	 * and dump them to the console
	 */
	public void dump(Class javaClass) throws IOException {
		this.dump(ClassFile.forClass(javaClass));
	}

	/**
	 * dump the specified class file to the console
	 */
	public void dump(ClassFile classFile) {
		this.dump(classFile, System.out);
	}

	/**
	 * dump the specified class file to the specified print stream
	 */
	public void dump(ClassFile classFile, PrintStream printStream) {
		printStream.print(classFile.displayString());
		printStream.flush();
	}

}
