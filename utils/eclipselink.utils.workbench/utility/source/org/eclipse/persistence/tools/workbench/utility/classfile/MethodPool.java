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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file method pool:
 *     u2 methods_count;
 *     method_info[methods_count] methods;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class MethodPool implements Member.Pool {
	private ClassFile classFile;
	private short count;
	private Method[] methods;


	/**
	 * Construct a class file field pool from the specified stream
	 * of byte codes.
	 */
	MethodPool(ClassFileDataInputStream stream, ClassFile classFile) throws IOException {
		super();
		this.classFile = classFile;
		this.initialize(stream);
	}

	private void initialize(ClassFileDataInputStream stream) throws IOException {
		this.count = stream.readU2();
		short cnt = this.count;
		this.methods = new Method[cnt];
		Method[] localMethods = this.methods;
		for (short i = 0; i < cnt; i++) {
			localMethods[i] = new Method(stream, this);
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
		Method[] localMethods = this.methods;
		writer.print("Method Pool (count: ");
		writer.print(cnt);
		writer.println(')');
		writer.indent();
		for (short i = 0; i < cnt; i++) {
			writer.print(i);
			writer.print(": ");
			localMethods[i].displayStringOn(writer);
		}
		writer.undent();
	}

	public Method[] declaredConstructors() {
		short cnt = this.count;
		Method[] localMethods = this.methods;
		Collection declaredConstructors = new ArrayList(cnt);
		for (short i = 0; i < cnt; i++) {
			Method method = localMethods[i];
			if (method.isDeclaredConstructor()) {
				declaredConstructors.add(method);
			}
		}
		return (Method[]) declaredConstructors.toArray(new Method[declaredConstructors.size()]);
	}

	public Method[] declaredMethods() {
		short cnt = this.count;
		Method[] localMethods = this.methods;
		Collection declaredMethods = new ArrayList(cnt);
		for (short i = 0; i < cnt; i++) {
			Method method = localMethods[i];
			if (method.isDeclaredMethod()) {
				declaredMethods.add(method);
			}
		}
		return (Method[]) declaredMethods.toArray(new Method[declaredMethods.size()]);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		short cnt = this.count;
		Method[] localMethods = this.methods;
		for (short i = 0; i < cnt; i++) {
			localMethods[i].accept(visitor);
		}
	}

	public ClassFile getClassFile() {
		return this.classFile;
	}

	public short getCount() {
		return this.count;
	}

	public Method get(short index) {
		return this.methods[index];
	}

	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.count + " method(s))";
	}

}
